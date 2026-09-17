package com.quizarena;

public class Player {

    private final String username;
    private int score;
    private int correctAnswers;
    private int wrongAnswers;

    public Player(String username) {
        this.username = username;
        this.score = 0;
        this.correctAnswers = 0;
        this.wrongAnswers = 0;
    }

    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public int getWrongAnswers() {
        return wrongAnswers;
    }

    public void addScore(int points) {
        score += points;
    }

    public void addCorrectAnswer() {
        correctAnswers++;
    }

    public void addWrongAnswer() {
        wrongAnswers++;
    }
}