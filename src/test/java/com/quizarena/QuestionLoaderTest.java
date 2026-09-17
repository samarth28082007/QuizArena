package com.quizarena;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

class QuestionLoaderTest {

    @Test
    void loadQuestions_shouldReturnQuestionsFromDataFile() {
        List<Question> questions = QuestionLoader.loadQuestions("data/questions.txt");

        assertNotNull(questions);
        assertFalse(questions.isEmpty());
        assertNotNull(questions.get(0).getCategory());
    }

    @Test
    void testFilterQuestions() {
        List<Question> questions = QuestionLoader.loadQuestions("data/questions.txt");

        Set<String> categories = QuestionLoader.getCategories(questions);
        assertTrue(categories.contains("JAVA"));

        List<Question> javaEasy = QuestionLoader.filterQuestions(questions, "JAVA", "EASY");
        assertFalse(javaEasy.isEmpty());
        for (Question q : javaEasy) {
            assertEquals("JAVA", q.getCategory());
            assertEquals(Difficulty.EASY, q.getDifficulty());
        }
    }
}
