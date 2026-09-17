package com.quizarena;

public class Question {

    private final String category;
    private final String questionText;
    private final String optionA;
    private final String optionB;
    private final String optionC;
    private final String optionD;
    private final char correctAnswer;
    private final Difficulty difficulty;

    public Question(String category,
                    String questionText,
                    String optionA,
                    String optionB,
                    String optionC,
                    String optionD,
                    char correctAnswer,
                    Difficulty difficulty) {

        this.category = category;
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswer = correctAnswer;
        this.difficulty = difficulty;
    }

    public String getCategory() {
        return category;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getOptionA() {
        return optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public char getCorrectAnswer() {
        return correctAnswer;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }
}