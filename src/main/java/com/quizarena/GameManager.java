package com.quizarena;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GameManager {

    private static final List<ClientHandler> players = Collections.synchronizedList(new ArrayList<>());
    private static List<Question> allQuestions;
    private static List<Question> activeQuestions;
    private static boolean gameStarted = false;
    private static ClientHandler host = null;

    // Host Config Settings
    private static String selectedCategory = "ALL";
    private static String selectedDifficulty = "ALL";
    private static int secondsPerQuestion = 15;
    private static boolean hostConfigured = false;

    private static final Map<ClientHandler, String> currentRoundAnswers = Collections.synchronizedMap(new HashMap<>());
    private static CountDownLatch roundLatch;

    public static synchronized void addPlayer(ClientHandler player) {
        players.add(player);
        System.out.println("Player joined: " + player.getUsername() + ". Total players: " + players.size());

        if (host == null) {
            host = player;
            player.setHost(true);
            broadcast(Protocol.format(Protocol.PREFIX_LOBBY_UPDATE,
                    ConsoleUtils.colorize("👑 " + player.getUsername() + " joined as ROOM HOST!", ConsoleUtils.YELLOW)));
        } else {
            broadcast(Protocol.format(Protocol.PREFIX_LOBBY_UPDATE,
                    ConsoleUtils.colorize("📢 " + player.getUsername() + " joined the arena! (" + players.size() + " connected)", ConsoleUtils.CYAN)));
        }

        if (!gameStarted && players.size() >= 2 && hostConfigured) {
            gameStarted = true;
            new Thread(GameManager::runGameLoop).start();
        }
    }

    public static synchronized void removePlayer(ClientHandler player) {
        players.remove(player);
        System.out.println("Player disconnected: " + (player != null && player.getUsername() != null ? player.getUsername() : "Unknown")
                + ". Remaining players: " + players.size());

        broadcast(Protocol.format(Protocol.PREFIX_LOBBY_UPDATE,
                ConsoleUtils.colorize("📢 A player disconnected. Total remaining: " + players.size(), ConsoleUtils.RED)));

        if (player == host) {
            if (!players.isEmpty()) {
                host = players.get(0);
                host.setHost(true);
                broadcast(Protocol.format(Protocol.PREFIX_LOBBY_UPDATE,
                        ConsoleUtils.colorize("👑 " + host.getUsername() + " is now the new ROOM HOST!", ConsoleUtils.YELLOW)));
            } else {
                host = null;
            }
        }

        if (roundLatch != null && roundLatch.getCount() > 0) {
            roundLatch.countDown();
        }
    }

    public static int getPlayerCount() {
        return players.size();
    }

    public static ClientHandler getHost() {
        return host;
    }

    public static void loadQuestions() {
        allQuestions = QuestionLoader.loadQuestions("data/questions.txt");
        System.out.println("All questions loaded: " + (allQuestions != null ? allQuestions.size() : 0));
    }

    public static Set<String> getAvailableCategories() {
        if (allQuestions == null || allQuestions.isEmpty()) {
            loadQuestions();
        }
        return QuestionLoader.getCategories(allQuestions);
    }

    public static synchronized void configureHostSettings(String category, String difficulty, int timeLimitSeconds) {
        selectedCategory = category != null ? category.trim().toUpperCase() : "ALL";
        selectedDifficulty = difficulty != null ? difficulty.trim().toUpperCase() : "ALL";
        secondsPerQuestion = timeLimitSeconds > 0 ? timeLimitSeconds : 15;

        if (allQuestions == null || allQuestions.isEmpty()) {
            loadQuestions();
        }

        activeQuestions = QuestionLoader.filterQuestions(allQuestions, selectedCategory, selectedDifficulty);
        hostConfigured = true;

        String configMsg = String.format("⚙️ ROOM CONFIG: Category: %s | Difficulty: %s | Time: %ds/question | Questions: %d",
                selectedCategory, selectedDifficulty, secondsPerQuestion, activeQuestions.size());
        System.out.println(configMsg);
        broadcast(Protocol.format(Protocol.PREFIX_LOBBY_UPDATE, ConsoleUtils.colorize(configMsg, ConsoleUtils.GREEN)));

        if (!gameStarted && players.size() >= 2) {
            gameStarted = true;
            new Thread(GameManager::runGameLoop).start();
        }
    }

    public static void broadcast(String message) {
        synchronized (players) {
            for (ClientHandler player : players) {
                player.sendMessage(message);
            }
        }
    }

    public static void submitAnswer(ClientHandler player, String answer) {
        currentRoundAnswers.put(player, answer != null ? answer.trim() : "");
        if (roundLatch != null) {
            roundLatch.countDown();
        }
    }

    private static void runGameLoop() {
        try {
            broadcast(Protocol.PREFIX_CLEAR_SCREEN);
            broadcast(Protocol.format(Protocol.PREFIX_COUNTDOWN, ConsoleUtils.LOGO));
            broadcast(Protocol.format(Protocol.PREFIX_COUNTDOWN,
                    ConsoleUtils.colorize("===========================================", ConsoleUtils.CYAN)));
            broadcast(Protocol.format(Protocol.PREFIX_COUNTDOWN,
                    ConsoleUtils.colorize("   MINIMUM PLAYERS CONNECTED!              ", ConsoleUtils.YELLOW + ConsoleUtils.BOLD)));
            broadcast(Protocol.format(Protocol.PREFIX_COUNTDOWN,
                    ConsoleUtils.colorize("   Game starting in 3 seconds...           ", ConsoleUtils.WHITE)));
            broadcast(Protocol.format(Protocol.PREFIX_COUNTDOWN,
                    ConsoleUtils.colorize("===========================================", ConsoleUtils.CYAN)));
            Thread.sleep(3000);

            if (activeQuestions == null || activeQuestions.isEmpty()) {
                if (allQuestions == null || allQuestions.isEmpty()) {
                    loadQuestions();
                }
                activeQuestions = QuestionLoader.filterQuestions(allQuestions, selectedCategory, selectedDifficulty);
                if (activeQuestions.isEmpty()) {
                    activeQuestions = allQuestions;
                }
            }

            int totalQuestions = activeQuestions.size();

            for (int i = 0; i < totalQuestions; i++) {
                if (players.isEmpty()) {
                    break;
                }

                Question q = activeQuestions.get(i);
                currentRoundAnswers.clear();

                int activePlayerCount;
                synchronized (players) {
                    activePlayerCount = players.size();
                }
                roundLatch = new CountDownLatch(activePlayerCount);

                broadcast(Protocol.PREFIX_CLEAR_SCREEN);
                broadcast(Protocol.format(Protocol.PREFIX_QUESTION_START,
                        ConsoleUtils.colorize(String.format("📌 QUESTION %d / %d  [%s | %s]",
                                (i + 1), totalQuestions, q.getCategory(), q.getDifficulty()), ConsoleUtils.PURPLE + ConsoleUtils.BOLD)));
                broadcast(Protocol.format(Protocol.PREFIX_QUESTION_START,
                        ConsoleUtils.colorize("--------------------------------------------------", ConsoleUtils.CYAN)));
                broadcast(Protocol.format(Protocol.PREFIX_QUESTION_START,
                        ConsoleUtils.colorize(q.getQuestionText(), ConsoleUtils.WHITE + ConsoleUtils.BOLD)));
                broadcast(Protocol.format(Protocol.PREFIX_QUESTION_START, "A. " + q.getOptionA()));
                broadcast(Protocol.format(Protocol.PREFIX_QUESTION_START, "B. " + q.getOptionB()));
                broadcast(Protocol.format(Protocol.PREFIX_QUESTION_START, "C. " + q.getOptionC()));
                broadcast(Protocol.format(Protocol.PREFIX_QUESTION_START, "D. " + q.getOptionD()));
                broadcast(Protocol.format(Protocol.PREFIX_QUESTION_START,
                        ConsoleUtils.colorize("⏱️ You have " + secondsPerQuestion + " seconds to answer!", ConsoleUtils.YELLOW)));

                broadcast(Protocol.format(Protocol.PREFIX_PROMPT_ANSWER, "Enter your answer (A/B/C/D):"));

                synchronized (players) {
                    for (ClientHandler player : players) {
                        player.requestAnswer();
                    }
                }

                // Wait for secondsPerQuestion timer or until all players submit
                roundLatch.await(secondsPerQuestion, TimeUnit.SECONDS);

                synchronized (players) {
                    for (ClientHandler player : players) {
                        player.cancelAnswerRequest();
                    }
                }

                // Round Evaluation
                broadcast("");
                broadcast(Protocol.format(Protocol.PREFIX_ROUND_RESULT,
                        ConsoleUtils.colorize("---------------- ROUND RESULTS ----------------", ConsoleUtils.CYAN)));

                char correctChar = q.getCorrectAnswer();
                String correctStr = String.valueOf(correctChar);

                synchronized (players) {
                    for (ClientHandler player : players) {
                        String ans = currentRoundAnswers.get(player);
                        if (ans != null && ans.equalsIgnoreCase(correctStr)) {
                            player.getPlayer().addScore(10);
                            player.getPlayer().addCorrectAnswer();
                            broadcast(Protocol.format(Protocol.PREFIX_ROUND_RESULT,
                                    ConsoleUtils.colorize("✅ " + player.getUsername() + ": Correct! (+10 pts)", ConsoleUtils.GREEN)));
                        } else if (ans == null || ans.isEmpty()) {
                            player.getPlayer().addWrongAnswer();
                            broadcast(Protocol.format(Protocol.PREFIX_ROUND_RESULT,
                                    ConsoleUtils.colorize("⏰ " + player.getUsername() + ": Time Out! (Correct answer was " + correctChar + ")", ConsoleUtils.YELLOW)));
                        } else {
                            player.getPlayer().addWrongAnswer();
                            broadcast(Protocol.format(Protocol.PREFIX_ROUND_RESULT,
                                    ConsoleUtils.colorize("❌ " + player.getUsername() + ": Wrong ('" + ans.toUpperCase() + "')! (Correct answer was " + correctChar + ")", ConsoleUtils.RED)));
                        }
                    }
                }

                // Broadcast Live Global Leaderboard
                broadcastLeaderboard();

                Thread.sleep(3500); // 3.5s pause before next question
            }

            // Game Finished
            broadcast(Protocol.PREFIX_CLEAR_SCREEN);
            broadcast(Protocol.format(Protocol.PREFIX_GAME_OVER, ConsoleUtils.WINNER_BANNER));
            broadcastFinalSummary();

        } catch (InterruptedException e) {
            System.out.println("Game loop interrupted: " + e.getMessage());
        } finally {
            gameStarted = false;
        }
    }

    public static void broadcastLeaderboard() {
        broadcast("");
        broadcast(Protocol.format(Protocol.PREFIX_LEADERBOARD, ConsoleUtils.LEADERBOARD_BANNER));
        List<Player> sortedPlayers = getSortedPlayers();
        int rank = 1;
        for (Player p : sortedPlayers) {
            String rankSymbol = rank == 1 ? "🥇" : rank == 2 ? "🥈" : rank == 3 ? "🥉" : "  ";
            String line = String.format("%s #%-2d %-15s | Score: %-3d | Correct: %-2d | Wrong: %-2d",
                    rankSymbol, rank, p.getUsername(), p.getScore(), p.getCorrectAnswers(), p.getWrongAnswers());
            broadcast(Protocol.format(Protocol.PREFIX_LEADERBOARD, ConsoleUtils.colorize(line, rank == 1 ? ConsoleUtils.YELLOW : ConsoleUtils.CYAN)));
            rank++;
        }
        broadcast(Protocol.format(Protocol.PREFIX_LEADERBOARD,
                ConsoleUtils.colorize("----------------------------------------------------", ConsoleUtils.YELLOW)));
    }

    public static List<Player> getSortedPlayers() {
        List<Player> playerList = new ArrayList<>();
        synchronized (players) {
            for (ClientHandler ch : players) {
                if (ch.getPlayer() != null) {
                    playerList.add(ch.getPlayer());
                }
            }
        }
        playerList.sort(Comparator.comparingInt(Player::getScore)
                .thenComparingInt(Player::getCorrectAnswers)
                .reversed());
        return playerList;
    }

    private static void broadcastFinalSummary() {
        List<Player> sorted = getSortedPlayers();
        if (!sorted.isEmpty()) {
            Player winner = sorted.get(0);
            broadcast(Protocol.format(Protocol.PREFIX_GAME_OVER,
                    ConsoleUtils.colorize("👑 WINNER: " + winner.getUsername() + " with " + winner.getScore() + " points! 👑", ConsoleUtils.GREEN + ConsoleUtils.BOLD)));
        }
        broadcastLeaderboard();
        broadcast(Protocol.format(Protocol.PREFIX_GAME_OVER,
                ConsoleUtils.colorize("Thank you for playing QuizArena!", ConsoleUtils.CYAN)));
    }
}