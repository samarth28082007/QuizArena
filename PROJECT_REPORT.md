# QUIZARENA – REAL-TIME SYNCHRONOUS CLI MULTIPLAYER QUIZ

**Course/Domain:** Java Programming / Software Engineering
**Technology:** Java 21, Maven, TCP Sockets, JUnit 5

---

## 1. ABSTRACT

QuizArena is a command-line based multiplayer quiz application developed using Java. The project uses a client-server architecture where multiple players can connect to a central server and participate in the same quiz.

The main purpose of the project is to make a multiplayer quiz more organized and synchronized. In a normal socket-based quiz, different players may answer at different speeds, which can cause them to move to different questions. To solve this problem, QuizArena uses Java's concurrency features to make sure that all players participate in the same round before the game moves forward.

The application also includes features such as a multiplayer leaderboard, question categories and difficulty levels, a timer for each question, and a simple terminal-based interface. JUnit 5 tests are used to verify important parts of the application.

The project helped us understand Java networking, multithreading, synchronization, file handling, exception handling, and software testing.

---

# 2. INTRODUCTION

## 2.1 Problem Statement

Most basic quiz applications are designed for a single player. Creating a multiplayer quiz using Java sockets introduces several challenges.

For example, if one player answers a question quickly while another player takes more time, both players may end up being on different questions. The server also needs to handle players who stop responding or disconnect from the game.

Another challenge is communication between the client and server. Since TCP sockets mainly exchange text data, it is important to clearly identify different types of messages such as questions, answers, game updates, and leaderboard information.

Therefore, QuizArena was developed to provide a simple multiplayer quiz system where players can participate in synchronized rounds while the server manages the overall game.

---

## 2.2 Objectives

The main objectives of QuizArena are:

* To create a multiplayer quiz using Java TCP sockets.
* To allow multiple players to connect to the same server.
* To keep all players synchronized during each quiz round.
* To provide a timer for answering questions.
* To calculate player scores automatically.
* To display a leaderboard after each round.
* To allow questions to be filtered according to category and difficulty.
* To handle player disconnections and timeouts properly.
* To provide a simple and easy-to-use command-line interface.
* To test important components using JUnit.

---

# 3. TECHNOLOGIES USED

The following technologies were used to develop the project:

| Technology        | Purpose                                  |
| ----------------- | ---------------------------------------- |
| Java 21           | Main programming language                |
| Maven             | Project and dependency management        |
| TCP Sockets       | Communication between server and clients |
| JUnit 5           | Unit testing                             |
| Multithreading    | Handling multiple players                |
| CountDownLatch    | Synchronizing players during rounds      |
| File Handling     | Loading questions from a text file       |
| ANSI Escape Codes | Improving terminal appearance            |

---

# 4. SYSTEM ARCHITECTURE

QuizArena follows a **client-server architecture**.

The server acts as the central point of the application. Players connect to the server using their individual clients. The server manages the players, questions, answers, scores, and game rounds.

### Basic Architecture

```text
                 +----------------------+
                 |     Quiz Server      |
                 |     Port: 5000       |
                 +----------+-----------+
                            |
              +-------------+-------------+
              |                           |
              v                           v
      +---------------+           +---------------+
      |   Player 1    |           |   Player 2    |
      | Quiz Client   |           | Quiz Client   |
      +---------------+           +---------------+
              \                           /
               \                         /
                +-----------------------+
                |     GameManager       |
                |                       |
                | Players               |
                | Questions             |
                | Synchronization       |
                | Scoring               |
                | Leaderboard            |
                +-----------------------+
```

The server creates a separate `ClientHandler` for each connected player. The `GameManager` is responsible for managing the shared game state and keeping the players synchronized.

---

# 5. MAIN COMPONENTS

## 5.1 QuizServer

`QuizServer` is the main server program.

It creates a `ServerSocket` on port 5000 and waits for players to connect. Whenever a new player connects, the server creates a separate `ClientHandler` thread for that player.

---

## 5.2 ClientHandler

`ClientHandler` manages communication with an individual player.

Its responsibilities include:

* Receiving the player's username.
* Sending questions to the player.
* Receiving answers.
* Handling timeouts.
* Sending leaderboard updates.
* Managing communication between the client and server.

Each connected player gets their own handler thread.

---

## 5.3 GameManager

`GameManager` is one of the most important components of the project.

It manages the overall game state, including:

* Connected players.
* Questions being used in the game.
* Player scores.
* Correct and incorrect answers.
* Round synchronization.
* Leaderboard generation.

It also uses `CountDownLatch` to make sure that players remain synchronized during each question.

---

## 5.4 Question

The `Question` class represents a quiz question.

It stores:

* Question text
* Four options
* Correct answer
* Category
* Difficulty level

The difficulty levels are:

* EASY
* MEDIUM
* HARD

---

## 5.5 Player

The `Player` class stores information about each player.

It contains:

* Username
* Score
* Number of correct answers
* Number of wrong answers

The score is updated after every question.

---

## 5.6 QuestionLoader

`QuestionLoader` loads questions from the `questions.txt` file.

The questions are stored using a pipe-separated format:

```text
CATEGORY|DIFFICULTY|QUESTION|OPT_A|OPT_B|OPT_C|OPT_D|ANSWER
```

This makes it easy to add or modify questions without changing the Java source code.

---

## 5.7 QuizClient

`QuizClient` is the program used by players to connect to the server.

It:

* Connects to the server through TCP.
* Displays questions.
* Takes answers from the user.
* Sends answers to the server.
* Displays scores and leaderboard updates.

---

## 5.8 QuizApp

`QuizApp` provides a standalone single-player version of the quiz.

Unlike the multiplayer mode, it does not require a server or network connection. It also includes a timer for each question.

---

# 6. MULTIPLAYER GAME FLOW

The multiplayer game works approximately as follows:

### Step 1 – Server Starts

The server starts listening on port 5000.

### Step 2 – Players Connect

Players start their `QuizClient` and connect to the server.

### Step 3 – Lobby

The first player is treated as the host. The game waits until at least two players have joined.

### Step 4 – Game Configuration

The host can select options such as:

* Question category
* Difficulty
* Time limit

The selected settings are then used for the game.

### Step 5 – Game Starts

After the required number of players have joined, the server starts the game and gives players a short countdown.

### Step 6 – Question Round

The same question is sent to all players.

Every player gets the same amount of time to answer.

### Step 7 – Answer Collection

Players submit their answers to the server.

If a player does not answer before the timer expires, the answer is treated as a timeout.

### Step 8 – Score Calculation

Correct answers give the player **10 points**.

Wrong answers and timeouts do not give points.

### Step 9 – Leaderboard

After the round is completed, the server calculates the updated scores and sends the leaderboard to all players.

### Step 10 – Next Round

The server moves to the next question only after the current round has been completed.

This prevents players from getting out of sync.

---

# 7. SYNCHRONIZATION

Synchronization is one of the main features of QuizArena.

Without synchronization, a fast player could answer Question 1 and immediately receive Question 2 while another player is still answering Question 1.

To prevent this, the project uses Java's `CountDownLatch`.

The basic idea is:

```text
Question 1
    |
    +---- Player 1 answers
    |
    +---- Player 2 answers
    |
    +---- Player 3 answers
    |
    v
All players finished / Timer expired
    |
    v
Leaderboard
    |
    v
Question 2
```

The server waits for all active players to submit their answers or for the question timer to expire before moving to the next round.

This keeps the multiplayer experience fair and synchronized.

---

# 8. LEADERBOARD

After every question, QuizArena displays an updated leaderboard.

The leaderboard is sorted according to the player's current score. The number of correct answers can also be used to determine the ranking when players have the same score.

Example:

```text
================================
          LEADERBOARD
================================

Rank   Player       Score   Correct
------------------------------------
1      Samarth       40       4
2      Rahul         30       3
3      Aryan         20       2

================================
```

This allows players to see their current position during the game rather than waiting until the end.

---

# 9. MESSAGE PROTOCOL

To make communication between the server and clients easier to manage, the project uses structured message prefixes.

For example:

```text
[WELCOME]
[QUESTION_START]
[PROMPT_ANSWER]
[LOBBY_UPDATE]
[LEADERBOARD]
```

Instead of treating every message as ordinary text, the client can identify what type of message it has received.

This makes the communication more organized and reduces the chances of incorrect message handling.

---

# 10. USER INTERFACE

Although QuizArena is a command-line application, some effort has been made to make the interface easier to understand.

ANSI escape codes and formatted text are used for:

* Game headings
* Questions
* Timers
* Scores
* Leaderboards
* Game status messages

For example:

```text
========================================
              QUIZ ARENA
========================================

Player: Samarth
Score : 30

Question 4
Which language is mainly used with the
Spring Framework?

A. Python
B. Java
C. C++
D. JavaScript

Enter your answer:
```

The goal is to keep the interface simple while making important information easy to identify.

---

# 11. TESTING

JUnit 5 was used to test important parts of the project.

The tests were executed using Maven:

```text
mvn test
```

The following components were tested:

| Test                 | Purpose                                         | Result |
| -------------------- | ----------------------------------------------- | ------ |
| `GameManagerTest`    | Tests player sorting and leaderboard order      | Passed |
| `ProtocolTest`       | Tests message formatting and payload extraction | Passed |
| `QuestionLoaderTest` | Tests loading questions from the file           | Passed |
| `QuestionLoaderTest` | Tests category and difficulty filtering         | Passed |

Testing helped identify problems in individual components before running the complete multiplayer application.

---

# 12. CHALLENGES FACED

During development, some of the main challenges were:

### 12.1 Synchronizing Players

Making sure that every player stays on the same question was one of the biggest challenges. This was handled using synchronization mechanisms such as `CountDownLatch`.

### 12.2 Handling Timeouts

A player may not answer within the given time. The server therefore needs to continue the game without waiting indefinitely.

### 12.3 Multiple Client Connections

The server needs to communicate with multiple players at the same time. Separate handler threads are used for individual clients.

### 12.4 Maintaining Shared Game State

Scores, player lists, questions, and answers are shared between multiple threads. Proper synchronization is required to avoid inconsistent data.

### 12.5 Network Communication

The client and server must correctly understand the messages being exchanged. The protocol structure was introduced to make this communication easier to manage.

---

# 13. FUTURE IMPROVEMENTS

There are several features that can be added to QuizArena in the future.

### 13.1 Persistent Leaderboard

A database such as SQLite or MySQL can be added so that player scores and rankings are stored even after the server is closed.

### 13.2 More Game Modes

Different game modes could be introduced, such as:

* Classic Quiz
* Speed Round
* Survival Mode
* Team Battle
* Knockout Mode

### 13.3 Better Scoring System

Instead of giving every correct answer the same 10 points, the score could depend on how quickly the player answers.

For example:

```text
Fast answer     → 15 points
Normal answer   → 10 points
Last-second     → 5 points
Wrong answer    → 0 points
```

### 13.4 Web-Based Interface

The current CLI interface could later be replaced or extended with a web interface. Players could join the game through a browser while the Java server continues to manage the game.

### 13.5 Authentication

Players could create accounts and log in before joining the game. This would allow their statistics and achievements to be stored.

### 13.6 Question Bank Expansion

The question database can be expanded with more categories such as:

* Programming
* Science
* History
* Sports
* Movies
* General Knowledge
* Artificial Intelligence

### 13.7 Admin Panel

An administrator could be given an interface to add, remove, or edit questions without directly modifying the text file.

### 13.8 Reconnection Support

If a player temporarily loses their network connection, the system could allow them to reconnect to the same game instead of removing them immediately.

---

# 14. CONCLUSION

QuizArena is a Java-based multiplayer quiz application that demonstrates how networking and multithreading can be used to build an interactive multiplayer system.

The project uses TCP sockets for communication between the server and clients and uses Java concurrency features to synchronize players during each question. Features such as timers, question filtering, scoring, and a live leaderboard make the application more interactive than a basic console quiz.

Working on this project provided practical experience with Java socket programming, multithreading, synchronization, file handling, exception handling, testing, and basic software architecture.

The current version provides a strong foundation that can later be extended into a database-backed or web-based multiplayer quiz platform.


