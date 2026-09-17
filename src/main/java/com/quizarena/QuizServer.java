package com.quizarena;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class QuizServer {

    private static final int PORT = 5000;

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("       QUIZARENA SERVER");
        System.out.println("=================================");

        // Load questions
        GameManager.loadQuestions();

        try (ServerSocket serverSocket =
                     new ServerSocket(PORT)) {

            System.out.println("Server started successfully!");
            System.out.println("Waiting for players...");
            System.out.println("Port: " + PORT);

            while (true) {

                Socket clientSocket =
                        serverSocket.accept();

                System.out.println(
                        "New player connected: "
                        + clientSocket.getInetAddress());

                ClientHandler clientHandler =
                        new ClientHandler(clientSocket);

                clientHandler.start();
            }

        } catch (IOException e) {

            System.out.println(
                    "Server error: " + e.getMessage());
        }
    }
}