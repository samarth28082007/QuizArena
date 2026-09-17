package com.quizarena;

public class Protocol {

    public static final String PREFIX_WELCOME = "[WELCOME] ";
    public static final String PREFIX_PROMPT_USERNAME = "[PROMPT_USERNAME] ";
    public static final String PREFIX_HOST_CONFIG = "[HOST_CONFIG] ";
    public static final String PREFIX_LOBBY_UPDATE = "[LOBBY_UPDATE] ";
    public static final String PREFIX_COUNTDOWN = "[COUNTDOWN] ";
    public static final String PREFIX_QUESTION_START = "[QUESTION_START] ";
    public static final String PREFIX_PROMPT_ANSWER = "[PROMPT_ANSWER] ";
    public static final String PREFIX_ROUND_RESULT = "[ROUND_RESULT] ";
    public static final String PREFIX_LEADERBOARD = "[LEADERBOARD] ";
    public static final String PREFIX_GAME_OVER = "[GAME_OVER] ";
    public static final String PREFIX_CLEAR_SCREEN = "[CLEAR_SCREEN]";

    public static String format(String prefix, String message) {
        return prefix + message;
    }

    public static boolean isType(String rawMessage, String prefix) {
        return rawMessage != null && rawMessage.startsWith(prefix);
    }

    public static String extractPayload(String rawMessage, String prefix) {
        if (isType(rawMessage, prefix)) {
            return rawMessage.substring(prefix.length());
        }
        return rawMessage;
    }
}
