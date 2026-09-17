package com.quizarena;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.Set;

public class ClientHandler extends Thread {

    private final Socket socket;
    private PrintWriter output;
    private Scanner input;
    private String username;
    private Player player;
    private boolean isHost = false;
    private volatile boolean waitingForAnswer = false;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            input = new Scanner(socket.getInputStream());
            output = new PrintWriter(socket.getOutputStream(), true);

            sendMessage(Protocol.format(Protocol.PREFIX_WELCOME, ConsoleUtils.LOGO));
            sendMessage(Protocol.format(Protocol.PREFIX_PROMPT_USERNAME, "Enter your username:"));

            if (input.hasNextLine()) {
                username = input.nextLine().trim();
            }

            if (username == null || username.isEmpty()) {
                username = "Player_" + (System.currentTimeMillis() % 1000);
            }

            player = new Player(username);

            sendMessage(Protocol.format(Protocol.PREFIX_LOBBY_UPDATE, "Hello " + username + "! Connected to QuizArena."));

            GameManager.addPlayer(this);

            // If this player is Room Host, ask for room setup
            if (isHost) {
                configureHostRoom();
            } else {
                sendMessage(Protocol.format(Protocol.PREFIX_LOBBY_UPDATE, "Waiting for Room Host to configure the match..."));
            }

            // Socket line listener loop
            while (!socket.isClosed() && input.hasNextLine()) {
                String line = input.nextLine();
                if (waitingForAnswer) {
                    waitingForAnswer = false;
                    GameManager.submitAnswer(this, line);
                }
            }

        } catch (IOException e) {
            System.out.println("Client disconnected (" + (username != null ? username : "unknown") + "): " + e.getMessage());
        } finally {
            GameManager.removePlayer(this);
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    private void configureHostRoom() {
        Set<String> categories = GameManager.getAvailableCategories();
        StringBuilder catMenu = new StringBuilder("Categories available: [ALL");
        for (String c : categories) {
            catMenu.append(", ").append(c);
        }
        catMenu.append("]");

        sendMessage(Protocol.format(Protocol.PREFIX_HOST_CONFIG, "👑 You are the ROOM HOST!"));
        sendMessage(Protocol.format(Protocol.PREFIX_HOST_CONFIG, catMenu.toString()));
        sendMessage(Protocol.format(Protocol.PREFIX_HOST_CONFIG, "Enter Category choice (default: ALL):"));
        String categoryChoice = input.hasNextLine() ? input.nextLine().trim() : "ALL";
        if (categoryChoice.isEmpty()) categoryChoice = "ALL";

        sendMessage(Protocol.format(Protocol.PREFIX_HOST_CONFIG, "Difficulties available: [ALL, EASY, MEDIUM, HARD]"));
        sendMessage(Protocol.format(Protocol.PREFIX_HOST_CONFIG, "Enter Difficulty choice (default: ALL):"));
        String diffChoice = input.hasNextLine() ? input.nextLine().trim() : "ALL";
        if (diffChoice.isEmpty()) diffChoice = "ALL";

        sendMessage(Protocol.format(Protocol.PREFIX_HOST_CONFIG, "Enter seconds per question (10, 15, 20 - default: 15):"));
        String timeStr = input.hasNextLine() ? input.nextLine().trim() : "15";
        int timeLimit = 15;
        try {
            timeLimit = Integer.parseInt(timeStr);
        } catch (NumberFormatException ignored) {
        }

        GameManager.configureHostSettings(categoryChoice, diffChoice, timeLimit);
    }

    public void setHost(boolean isHost) {
        this.isHost = isHost;
    }

    public boolean isHost() {
        return isHost;
    }

    public void requestAnswer() {
        this.waitingForAnswer = true;
    }

    public void cancelAnswerRequest() {
        this.waitingForAnswer = false;
    }

    public void sendMessage(String msg) {
        if (output != null) {
            output.println(msg);
        }
    }

    public String getUsername() {
        return username;
    }

    public Player getPlayer() {
        return player;
    }
}