package com.quizarena;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class QuizClient {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 5000;

    public static void main(String[] args) {

        try (
            Socket socket = new Socket(SERVER_ADDRESS, PORT);
            Scanner input = new Scanner(socket.getInputStream());
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            Scanner keyboard = new Scanner(System.in)
        ) {

            System.out.println(ConsoleUtils.colorize("Connected to QuizArena Server!", ConsoleUtils.GREEN));

            while (input.hasNextLine()) {
                String line = input.nextLine();

                if (Protocol.isType(line, Protocol.PREFIX_CLEAR_SCREEN)) {
                    System.out.print("\033[H\033[2J");
                    System.out.flush();
                    continue;
                }

                if (Protocol.isType(line, Protocol.PREFIX_PROMPT_USERNAME)) {
                    String payload = Protocol.extractPayload(line, Protocol.PREFIX_PROMPT_USERNAME);
                    System.out.print(ConsoleUtils.colorize(payload + " ", ConsoleUtils.CYAN + ConsoleUtils.BOLD));
                    String username = keyboard.nextLine();
                    output.println(username);
                    continue;
                }

                if (Protocol.isType(line, Protocol.PREFIX_HOST_CONFIG)) {
                    String payload = Protocol.extractPayload(line, Protocol.PREFIX_HOST_CONFIG);
                    if (payload.contains("Enter")) {
                        System.out.print(ConsoleUtils.colorize(payload + " ", ConsoleUtils.YELLOW + ConsoleUtils.BOLD));
                        String choice = keyboard.nextLine();
                        output.println(choice);
                    } else {
                        System.out.println(ConsoleUtils.colorize(payload, ConsoleUtils.YELLOW));
                    }
                    continue;
                }

                if (Protocol.isType(line, Protocol.PREFIX_PROMPT_ANSWER)) {
                    String payload = Protocol.extractPayload(line, Protocol.PREFIX_PROMPT_ANSWER);
                    System.out.print(ConsoleUtils.colorize(payload + " ", ConsoleUtils.GREEN + ConsoleUtils.BOLD));
                    String answer = keyboard.nextLine();
                    output.println(answer);
                    continue;
                }

                if (Protocol.isType(line, Protocol.PREFIX_WELCOME) ||
                    Protocol.isType(line, Protocol.PREFIX_COUNTDOWN) ||
                    Protocol.isType(line, Protocol.PREFIX_QUESTION_START) ||
                    Protocol.isType(line, Protocol.PREFIX_ROUND_RESULT) ||
                    Protocol.isType(line, Protocol.PREFIX_LEADERBOARD) ||
                    Protocol.isType(line, Protocol.PREFIX_GAME_OVER) ||
                    Protocol.isType(line, Protocol.PREFIX_LOBBY_UPDATE)) {

                    String payload = Protocol.extractPayload(line, getPrefix(line));
                    System.out.println(payload);
                    continue;
                }

                // Fallback for raw lines
                System.out.println(line);
            }

        } catch (IOException e) {
            System.out.println(ConsoleUtils.colorize("Connection error: " + e.getMessage(), ConsoleUtils.RED));
        }
    }

    private static String getPrefix(String line) {
        if (line.startsWith(Protocol.PREFIX_WELCOME)) return Protocol.PREFIX_WELCOME;
        if (line.startsWith(Protocol.PREFIX_COUNTDOWN)) return Protocol.PREFIX_COUNTDOWN;
        if (line.startsWith(Protocol.PREFIX_QUESTION_START)) return Protocol.PREFIX_QUESTION_START;
        if (line.startsWith(Protocol.PREFIX_ROUND_RESULT)) return Protocol.PREFIX_ROUND_RESULT;
        if (line.startsWith(Protocol.PREFIX_LEADERBOARD)) return Protocol.PREFIX_LEADERBOARD;
        if (line.startsWith(Protocol.PREFIX_GAME_OVER)) return Protocol.PREFIX_GAME_OVER;
        if (line.startsWith(Protocol.PREFIX_LOBBY_UPDATE)) return Protocol.PREFIX_LOBBY_UPDATE;
        return "";
    }
}