# 🎮 QuizArena - CLI Multiplayer Quiz Game Engine

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Build](https://img.shields.io/badge/Build-Maven-blue.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

**QuizArena** is a multi-threaded console-based CLI multiplayer quiz game built in Java 21 using native TCP Sockets. It features round-by-round synchronous multiplayer competition, real-time live global leaderboards, customizable room host game rules, per-question countdown timers with auto-submit, a custom protocol state machine, and vibrant ANSI terminal styling.

---

## 🌟 Key Features

- **Synchronous Multiplayer Engine**: All players receive questions simultaneously, answer within a shared round, and progress together.
- **Room Host Game Setup**: The first player to connect becomes the **Room Host** (`👑`) and can configure:
  - **Category Filter**: `ALL` or specific topics (`JAVA`, `COMPUTER_SCIENCE`, `TECHNOLOGY`, `GENERAL_KNOWLEDGE`).
  - **Difficulty Filter**: `ALL`, `EASY`, `MEDIUM`, or `HARD`.
  - **Question Time Limit**: Configurable countdown per question (e.g., 10s, 15s, 20s).
- **Per-Question Timer & Auto-Submit**: Automatic round evaluation if a player fails to respond before the timer expires.
- **Real-Time Live Leaderboard**: Displays rank, score, correct answers, and wrong answers after each question round.
- **Structured Protocol State Machine**: Clean prefix-based messaging layer (`[WELCOME]`, `[HOST_CONFIG]`, `[QUESTION_START]`, `[ROUND_RESULT]`, `[LEADERBOARD]`, `[GAME_OVER]`).
- **ANSI Terminal Styling & ASCII Art**: Rich console experience with ANSI colors, ASCII banners, and screen-clearing transitions.
- **Single-Player Offline Mode**: Standalone CLI quiz mode (`QuizApp`) with timer countdowns.

---

## 🛠️ Prerequisites & Environment Setup

Ensure the following tools are installed on your environment before running the application:

1. **Java Development Kit (JDK 21 or higher)**:
   - Check version: `java -version`
   - Download JDK 21 from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [Adoptium Temurin](https://adoptium.net/).
2. **Apache Maven (v3.8+)**:
   - Check version: `mvn -version`

---

## 📁 Repository Structure

```
QuizArena/
├── data/
│   └── questions.txt              # Question bank formatted in pipe-separated CSV
├── docs/
│   └── PROJECT_REPORT.md          # Comprehensive Academic Project Evaluation Report
├── src/
│   ├── main/java/com/quizarena/
│   │   ├── ClientHandler.java     # Worker thread handling TCP socket client connection
│   │   ├── ConsoleUtils.java      # ANSI color utilities and ASCII art banners
│   │   ├── Difficulty.java        # Difficulty enum (EASY, MEDIUM, HARD)
│   │   ├── GameManager.java       # Central synchronized lobby & round manager
│   │   ├── Player.java            # Player state & score model
│   │   ├── Protocol.java          # State machine protocol prefix definitions
│   │   ├── Question.java          # Question data model with category & difficulty
│   │   ├── QuestionLoader.java    # Parser & filter utility for question bank
│   │   ├── QuizApp.java           # Single-player offline standalone CLI application
│   │   ├── QuizClient.java        # CLI client terminal application
│   │   ├── QuizServer.java        # Main socket server entrypoint (Port 5000)
│   │   └── QuizTimer.java         # Threaded timer utility
│   └── test/java/com/quizarena/
│       ├── GameManagerTest.java   # Unit tests for leaderboard and player sorting
│       ├── ProtocolTest.java      # Unit tests for protocol message parsing
│       └── QuestionLoaderTest.java# Unit tests for question parsing and filtering
├── pom.xml                        # Maven project configuration & dependencies
├── runquiz.bat                    # Automated Windows launcher script
├── README.md                      # Project setup & evaluation documentation
└── .gitignore                     # Git exclusion rules
```

---

## 🚀 Execution & Running Instructions

### Option 1: Automated Launch (Windows Script)

To automatically compile and launch 1 server window and 2 client player windows:

```cmd
.\runquiz.bat
```

---

### Option 2: Manual Multi-Terminal Command Line Execution

#### **Step 1: Compile the Project**
Run from the root directory:
```cmd
mvn clean compile
```

#### **Step 2: Start the QuizArena Server**
In **Terminal Window 1**:
```cmd
java -cp target/classes com.quizarena.QuizServer
```
*(Server listens on TCP Port `5000`)*

#### **Step 3: Launch Player 1 (Room Host)**
In **Terminal Window 2**:
```cmd
java -cp target/classes com.quizarena.QuizClient
```
- Enter player username.
- As Room Host, select **Category**, **Difficulty**, and **Time Limit**.

#### **Step 4: Launch Player 2**
In **Terminal Window 3**:
```cmd
java -cp target/classes com.quizarena.QuizClient
```
- Enter player username.
- Game automatically commences as soon as 2 players join!

---

### Option 3: Single-Player Offline Mode

To play individually without networking:
```cmd
java -cp target/classes com.quizarena.QuizApp
```

---

## 🧪 Running Unit Tests

To run the automated JUnit 5 test suite:

```cmd
mvn test
```

Expected Output:
```
[INFO] Running com.quizarena.GameManagerTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.quizarena.ProtocolTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.quizarena.QuestionLoaderTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## 📄 Question File Format

Questions are stored in `data/questions.txt` using pipe (`|`) delimiters:

```text
CATEGORY|DIFFICULTY|QUESTION|OPT_A|OPT_B|OPT_C|OPT_D|CORRECT_ANSWER
```

**Example:**
```text
JAVA|EASY|Which keyword is used for inheritance?|implements|extends|inherits|super|B
COMPUTER_SCIENCE|EASY|What does CPU stand for?|Central Processing Unit|Computer Personal Unit|Central Program Utility|Control Processing Unit|A
```

---

## 📜 License

This project is open-source and released under the [MIT License](LICENSE).
