package com.quizarena;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class QuestionLoader {

    public static List<Question> loadQuestions(String filePath) {
        List<Question> questions = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split("\\|");
                if (parts.length != 8) {
                    continue;
                }

                String category = parts[0].trim().toUpperCase();
                Difficulty difficulty;
                try {
                    difficulty = Difficulty.valueOf(parts[1].trim().toUpperCase());
                } catch (IllegalArgumentException e) {
                    continue;
                }

                Question question = new Question(
                        category,
                        parts[2].trim(),
                        parts[3].trim(),
                        parts[4].trim(),
                        parts[5].trim(),
                        parts[6].trim(),
                        parts[7].trim().toUpperCase().charAt(0),
                        difficulty
                );

                questions.add(question);
            }

        } catch (IOException e) {
            System.out.println("Error reading questions file: " + e.getMessage());
        }

        return questions;
    }

    public static Set<String> getCategories(List<Question> questions) {
        if (questions == null) return new TreeSet<>();
        return questions.stream()
                .map(Question::getCategory)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    public static List<Question> filterQuestions(List<Question> allQuestions, String categoryFilter, String difficultyFilter) {
        if (allQuestions == null) return new ArrayList<>();

        return allQuestions.stream()
                .filter(q -> categoryFilter == null || categoryFilter.equalsIgnoreCase("ALL") || q.getCategory().equalsIgnoreCase(categoryFilter))
                .filter(q -> difficultyFilter == null || difficultyFilter.equalsIgnoreCase("ALL") || q.getDifficulty().name().equalsIgnoreCase(difficultyFilter))
                .collect(Collectors.toList());
    }
}