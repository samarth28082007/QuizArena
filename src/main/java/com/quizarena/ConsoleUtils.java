package com.quizarena;

public class ConsoleUtils {

    // ANSI Color Constants
    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    // ASCII Art Banners
    public static final String LOGO =
            CYAN + BOLD +
            "  ██████╗ ██╗██╗███████╗ █████╗ ██████╗ ███████╗███╗   ██╗███╗   ██╗███████╗\n" +
            " ██╔═══██╗██║██║╚══███╔╝██╔══██╗██╔══██╗██╔════╝████╗  ██║████╗  ██║██╔════╝\n" +
            " ██║   ██║██║██║  ███╔╝ ███████║██████╔╝█████╗  ██╔██╗ ██║██╔██╗ ██║███████╗\n" +
            " ██║▄▄ ██║██║██║ ███╔╝  ██╔══██║██╔══██╗██╔══╝  ██║╚██╗██║██║╚██╗██║╚════██║\n" +
            " ╚██████╔╝╚█████╔╝███████╗██║  ██║██║  ██║███████╗██║ ╚████║██║ ╚████║███████║\n" +
            "  ╚══▀▀═╝  ╚════╝ ╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝╚═╝  ╚═══╝╚═╝  ╚═══╝╚══════╝\n" +
            RESET;

    public static final String LEADERBOARD_BANNER =
            YELLOW + BOLD +
            "  🏆 ==================================================== 🏆\n" +
            "               LIVE GLOBAL LEADERBOARD\n" +
            "  🏆 ==================================================== 🏆\n" +
            RESET;

    public static final String WINNER_BANNER =
            GREEN + BOLD +
            "  🎉 ==================================================== 🎉\n" +
            "                     MATCH CHAMPION!\n" +
            "  🎉 ==================================================== 🎉\n" +
            RESET;

    public static String colorize(String text, String color) {
        return color + text + RESET;
    }
}
