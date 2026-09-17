package com.quizarena;

public class QuizTimer extends Thread {

    private int seconds;
    private boolean timeUp = false;

    public QuizTimer(int seconds) {
        this.seconds = seconds;
    }

    @Override
    public void run() {

        try {
            long endTime = System.currentTimeMillis() + (seconds * 1000L);

            while (System.currentTimeMillis() < endTime) {
                long remainingSeconds = (endTime - System.currentTimeMillis()) / 1000L;

                if (remainingSeconds != seconds) {
                    seconds = (int) remainingSeconds;
                    System.out.println("Time remaining: "
                            + seconds + " seconds");
                }

                Thread.yield();
            }

            timeUp = true;
            System.out.println("⏰ Time's up!");

        } catch (Exception e) {
            System.out.println("Timer stopped.");
        }
    }

    public boolean isTimeUp() {
        return timeUp;
    }
}