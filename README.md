# QuizArena - CLI Multiplayer Quiz Game

QuizArena is a command-line multiplayer quiz game developed using **Java 21**. It uses TCP sockets to connect players to a central server and allows them to play a quiz together.

The main idea behind the project is to keep all players on the same question. Each question has a fixed time limit, and once the round is over, the scores are updated and shown on a leaderboard.

The project also supports a single-player mode, question filtering by category and difficulty, and a simple client-server communication protocol.

---

## Features

### Multiplayer Mode

* Multiple players can connect to the same server.
* The first player to join becomes the Room Host.
* The host can configure the game before it starts.
* All players receive the same question.
* Players have the same time limit for each question.
* The game moves to the next question after the round is completed.
* A leaderboard is displayed after every round.

### Room Host Settings

The first player who joins the room becomes the host.

The host can choose:

**Category**

* `ALL`
* `JAVA`
* `COMPUTER_SCIENCE`
* `TECHNOLOGY`
* `GENERAL_KNOWLEDGE`

**Difficulty**

* `ALL`
* `EASY`
* `MEDIUM`
* `HARD`

**Question Time Limit**

The host can select the amount of time players get to answer each question, such as 10, 15, or 20 seconds.

### Question Timer

Every question has a countdown timer.

If a player does not submit an answer before the timer ends, the question is treated as unanswered for that player. The round can then continue without waiting for that player.

### Leaderboard

After each question, the current scores are shown to all players.

The leaderboard contains:

* Player name
* Rank
* Score
* Correct answers
* Wrong answers

### Client-Server Communication

The client and server communicate using TCP sockets.

Different types of messages are identified using simple prefixes, for example:

```text
[WELCOME]
[HOST_CONFIG]
[QUESTION_START]
[ROUND_RESULT]
[LEADERBOARD]
[GAME_OVER]
```

This makes it easier for the client to understand what kind of information it has received.

### Terminal Interface

The game runs completely in the terminal.

ANSI escape codes are used to improve the appearance of the console, including formatted headings, questions, scores, and leaderboard information.

### Single-Player Mode

QuizArena also has a standalone mode that can be played without starting the server.

This mode includes a timer for each question.

---

## Technologies Used

| Technology        | Purpose                                  |
| ----------------- | ---------------------------------------- |
| Java 21           | Main programming language                |
| Maven             | Build and dependency management          |
| TCP Sockets       | Communication between clients and server |
| Multithreading    | Handling multiple players                |
| CountDownLatch    | Synchronizing players during rounds      |
| JUnit 5           | Unit testing                             |
| File Handling     | Loading questions from a text file       |
| ANSI Escape Codes | Terminal formatting                      |

---

## Requirements

Before running the project, make sure the following are installed.

### Java

JDK 21 or later is required.

Check your Java version:

```cmd
java -version
```

You can install Java from Oracle or Eclipse Temurin.

### Maven

Maven 3.8 or later is recommended.

Check your Maven version:

```cmd
mvn -version
```

---

## Project Structure

```text
QuizArena/
│
├── data/
│   └── questions.txt
│
├── docs/
│   └── PROJECT_REPORT.md
│
├── src/
│   ├── main/
│   │   └── java/com/quizarena/
│   │       ├── ClientHandler.java
│   │       ├── ConsoleUtils.java
│   │       ├── Difficulty.java
│   │       ├── GameManager.java
│   │       ├── Player.java
│   │       ├── Protocol.java
│   │       ├── Question.java
│   │       ├── QuestionLoader.java
│   │       ├── QuizApp.java
│   │       ├── QuizClient.java
│   │       ├── QuizServer.java
│   │       └── QuizTimer.java
│   │
│   └── test/
│       └── java/com/quizarena/
│           ├── GameManagerTest.java
│           ├── ProtocolTest.java
│           └── QuestionLoaderTest.java
│
├── pom.xml
├── runquiz.bat
├── README.md
└── .gitignore
```

---

## How to Run

There are two ways to run the multiplayer version.

### Option 1: Windows Batch File

If you are using Windows, you can use the provided batch file:

```cmd
.\runquiz.bat
```

The script will:

1. Compile the project.
2. Start the server.
3. Open a client for the first player.
4. Open another client for the second player.

This is the easiest way to test the multiplayer version.

---

### Option 2: Run Manually

#### Step 1: Compile the project

Open a terminal in the project directory and run:

```cmd
mvn clean compile
```

#### Step 2: Start the server

Open the first terminal and run:

```cmd
java -cp target/classes com.quizarena.QuizServer
```

The server will listen for connections on TCP port `5000`.

#### Step 3: Start Player 1

Open a second terminal and run:

```cmd
java -cp target/classes com.quizarena.QuizClient
```

Enter a username.

Since this is the first player, they will become the Room Host and will be asked to select the game settings.

#### Step 4: Start Player 2

Open a third terminal and run:

```cmd
java -cp target/classes com.quizarena.QuizClient
```

Enter another username.

Once the required players have joined and the host has completed the setup, the quiz will start.

---

## Single-Player Mode

The project also includes an offline single-player mode.

First compile the project:

```cmd
mvn clean compile
```

Then run:

```cmd
java -cp target/classes com.quizarena.QuizApp
```

This mode does not require the server or any network connection.

---

## Running Tests

The project uses JUnit 5 for unit testing.

To run the tests:

```cmd
mvn test
```

The tests currently cover:

* Player and leaderboard sorting
* Protocol message formatting
* Question loading
* Category filtering
* Difficulty filtering

A successful test run should end with:

```text
BUILD SUCCESS
```

---

## Question File

Questions are stored in:

```text
data/questions.txt
```

The file uses the pipe (`|`) character to separate the different fields.

### Format

```text
CATEGORY|DIFFICULTY|QUESTION|OPT_A|OPT_B|OPT_C|OPT_D|CORRECT_ANSWER
```

### Example

```text
JAVA|EASY|Which keyword is used for inheritance?|implements|extends|inherits|super|B
COMPUTER_SCIENCE|EASY|What does CPU stand for?|Central Processing Unit|Computer Personal Unit|Central Program Utility|Control Processing Unit|A
```

New questions can be added directly to this file without changing the Java source code.

---

## How the Multiplayer Game Works

The basic flow of the game is:

```text
Player 1 connects
       |
       v
Player 1 becomes Host
       |
       v
Player 2 connects
       |
       v
Host selects game settings
       |
       v
Game starts
       |
       v
Question is sent to all players
       |
       v
Players submit answers
       |
       v
Timer ends / all answers received
       |
       v
Scores are calculated
       |
       v
Leaderboard is displayed
       |
       v
Next question
       |
       v
Final results
```

The server controls the rounds so that players do not move to the next question independently.

---

## Main Classes

| Class            | Purpose                                          |
| ---------------- | ------------------------------------------------ |
| `QuizServer`     | Starts the server and accepts player connections |
| `ClientHandler`  | Handles communication with an individual client  |
| `GameManager`    | Manages players, questions, rounds, and scores   |
| `QuizClient`     | Client application used by players               |
| `QuizApp`        | Standalone single-player application             |
| `Question`       | Stores question information                      |
| `Player`         | Stores player information and score              |
| `QuestionLoader` | Loads and filters questions                      |
| `Protocol`       | Defines the different message types              |
| `QuizTimer`      | Handles question countdowns                      |
| `ConsoleUtils`   | Handles terminal formatting                      |
| `Difficulty`     | Defines the available difficulty levels          |

---

## Why We Built This

The main purpose of this project was to apply Java concepts in a practical application instead of only using them in small programs.

While developing QuizArena, we worked with:

* Object-oriented programming
* Socket programming
* Client-server architecture
* Multithreading
* Thread synchronization
* File handling
* Exception handling
* Unit testing

One of the main parts we focused on was synchronization. Making multiple clients participate in the same round helped us understand how threads communicate and how shared data needs to be managed.

---

## Future Improvements

Some features that could be added in future versions are:

* Saving player scores in a database
* User accounts and login
* A permanent global leaderboard
* Private rooms
* More game modes
* Team-based quizzes
* More question categories
* Player reconnection after a network failure
* An admin interface for adding and editing questions
* A web-based interface
* More advanced scoring based on answer speed

---

## License

This project is released under the MIT License.

See the `LICENSE` file for more information.
