package com.quizarena;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class QuizApp {

    public static void main(String[] args) {

        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(System.in));

            System.out.println("=================================");
            System.out.println("          QUIZARENA");
            System.out.println("     Multiplayer Quiz Game");
            System.out.println("=================================");

            System.out.print("Enter your username: ");
            String username = reader.readLine();

            Player player = new Player(username);

            // Load questions from file
            List<Question> questions =
                    QuestionLoader.loadQuestions("data/questions.txt");

            System.out.println("\nQuestions loaded: "
                    + questions.size());

            if (questions.isEmpty()) {
                System.out.println("No questions found!");
                return;
            }

            int questionNumber = 1;

            for (Question question : questions) {

                System.out.println("\n---------------------------------");
                System.out.println("Question " + questionNumber
                        + "/" + questions.size());
                System.out.println("---------------------------------");

                System.out.println(question.getQuestionText());
                System.out.println("A. " + question.getOptionA());
                System.out.println("B. " + question.getOptionB());
                System.out.println("C. " + question.getOptionC());
                System.out.println("D. " + question.getOptionD());

                System.out.println("\nYou have 10 seconds!");

                QuizTimer timer = new QuizTimer(10);
                timer.start();

                String input = null;

                // Wait for user's answer or timeout
                while (!timer.isTimeUp()) {

                    if (reader.ready()) {
                        input = reader.readLine();
                        break;
                    }

                    Thread.yield();
                }

                // Stop timer if answer was given
                if (input != null) {
                    timer.interrupt();
                }

                // Time expired
                if (input == null) {

                    System.out.println(
                            "⏰ Time's up!");

                    System.out.println("Correct answer: "
                            + question.getCorrectAnswer());

                    player.addWrongAnswer();

                } else {

                    // Check answer
                    char answer =
                            input.toUpperCase().charAt(0);

                    if (answer ==
                            question.getCorrectAnswer()) {

                        System.out.println("Correct! 🎉");

                        player.addScore(10);
                        player.addCorrectAnswer();

                    } else {

                        System.out.println("Wrong!");

                        System.out.println(
                                "Correct answer: "
                                + question.getCorrectAnswer());

                        player.addWrongAnswer();
                    }
                }

                questionNumber++;
            }

            // Final result
            System.out.println("\n=================================");
            System.out.println("          FINAL RESULT");
            System.out.println("=================================");

            System.out.println("Player: "
                    + player.getUsername());

            System.out.println("Score: "
                    + player.getScore());

            System.out.println("Correct Answers: "
                    + player.getCorrectAnswers());

            System.out.println("Wrong Answers: "
                    + player.getWrongAnswers());

            System.out.println("=================================");

        } catch (IOException e) {

            System.out.println(
                    "Something went wrong: "
                    + e.getMessage());
        }
    }
}