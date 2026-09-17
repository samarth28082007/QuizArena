# 📄 ACADEMIC PROJECT REPORT

## PROJECT TITLE: QuizArena – Real-Time Synchronous CLI Multiplayer Quiz Engine
**Course / Domain**: Java Programming / Software Engineering / Distributed Systems  
**Evaluation Platform**: VITyarthi Flipped Course Evaluation  

---

## 1. ABSTRACT

**QuizArena** is a high-performance, multi-threaded command-line interface (CLI) multiplayer quiz system implemented in Java 21 using client-server socket architecture over TCP/IP. The system addresses common pitfalls in CLI multiplayer games—such as asynchronous player drift, unhandled socket timeouts, unstructured network text parsing, and uninspiring terminal displays. QuizArena introduces a round-synchronous game coordinator using Java concurrency primitives (`CountDownLatch`), a custom prefix-based protocol state machine (`Protocol`), a Room Host lobby filter mechanism for categories and difficulty levels, and per-question timers with automated fallback submissions. Evaluated with automated JUnit 5 test suites, QuizArena demonstrates robust concurrency handling, low network latency, and clean architectural separation between networking, state management, and user interaction layers.

---

## 2. INTRODUCTION & PROBLEM STATEMENT

### 2.1 Problem Statement
Traditional command-line quiz applications operate strictly in single-player or turn-based modes. Implementing real-time multiplayer over socket connections presents several technical challenges:
1. **Asynchronous Drift**: Without centralized round synchronization, fast-answering players advance to subsequent questions while slower players remain on earlier questions.
2. **Unresponsive Blocking**: A single player failing or stalling on a question can block server execution indefinitely.
3. **Fragile Communication**: Relying on unformatted text lines over TCP leads to parsing errors when prompt text overlaps with user responses.
4. **Poor User Interface**: CLI games often lack visual structure, making leaderboards and round transitions difficult to read.

### 2.2 Project Objectives
- **Synchronous Round Management**: Guarantee that all connected players view questions, submit answers, and review score updates in lockstep.
- **Dynamic Match Customization**: Enable the first connected player (Room Host) to filter question pools by category and difficulty and set question countdown timers.
- **Protocol State Machine**: Design a structured message protocol to distinguish between system prompts, lobby updates, question broadcasts, and leaderboard metrics.
- **Robust Failure Resilience**: Implement countdown latches and timeouts so disconnections or stalled inputs do not hang the server loop.
- **Vibrant ANSI CLI Interface**: Incorporate ANSI escape codes, screen clearing, and ASCII art headers to deliver a visually engaging terminal user experience.

---

## 3. SYSTEM ARCHITECTURE & COMPONENT DESIGN

### 3.1 Architectural Overview
QuizArena follows a Client-Server Architecture driven by a Centralized Coordinator (`GameManager`):

```

### 3.2 Key Classes and Responsibilities

| Class | Package | Responsibility |
| :--- | :--- | :--- |
| `QuizServer` | `com.quizarena` | Main entry point; opens `ServerSocket` on port 5000 and accepts incoming client connections. |
| `ClientHandler` | `com.quizarena` | Worker thread for each TCP client socket; handles username registration, host config responses, and answer input loops. |
| `GameManager` | `com.quizarena` | Central static thread-safe state manager; coordinates game loops, countdown latches, answer scoring, and global leaderboards. |
| `Protocol` | `com.quizarena` | Protocol state machine defining typed message prefixes (`[WELCOME]`, `[QUESTION_START]`, `[LEADERBOARD]`, etc.). |
| `ConsoleUtils` | `com.quizarena` | Visual utility defining ANSI color escape sequences and ASCII art text banners. |
| `QuestionLoader` | `com.quizarena` | Utility reading pipe-delimited data from `data/questions.txt`, providing category extraction and filtering algorithms. |
| `Question` | `com.quizarena` | Immutable domain model storing question text, options A–D, correct answer, category, and `Difficulty`. |
| `Player` | `com.quizarena` | Domain model storing player username, total score, correct answer tally, and wrong answer tally. |
| `QuizClient` | `com.quizarena` | Terminal client reading TCP streams, parsing protocol prefixes, rendering ANSI graphics, and capturing keyboard input. |
| `QuizApp` | `com.quizarena` | Offline standalone single-player console quiz game. |

---

## 4. IMPLEMENTATION DETAILS & WORKFLOW

### 4.1 Synchronous Game Loop Execution Flow
1. **Lobby & Host Assignment**:
   - The first client connecting to `QuizServer` is designated as `isHost = true`.
   - `ClientHandler` prompts the Host to select Category, Difficulty, and Timer limit.
   - Non-host clients receive `[LOBBY_UPDATE]` notifications while waiting.

2. **Game Loop Trigger**:
   - Once player count reaches $\ge 2$ and host configuration is complete, `GameManager` spawns the `runGameLoop()` background thread.
   - A 3-second countdown banner is broadcast to all clients.

3. **Round Broadcast & Answer Collection**:
   - For each filtered question, `GameManager` initializes a `CountDownLatch(activePlayersCount)`.
   - The question text and options A–D are sent to all clients via `[QUESTION_START]`.
   - `GameManager` prompts answer input via `[PROMPT_ANSWER]` and awaits `roundLatch.await(secondsPerQuestion, TimeUnit.SECONDS)`.

4. **Evaluation & Live Leaderboard**:
   - Answers submitted before timeout earn +10 points. Unsubmitted or wrong answers record wrong tallies.
   - `GameManager` computes and broadcasts the **Live Global Leaderboard** sorted descending by score and correct answer count.

---

## 5. TESTING & VERIFICATION

Automated testing was conducted using **JUnit 5** via Maven test runner:

```cmd
mvn test
```

### Test Case Coverage

| Test Class | Test Case | Target Component | Status |
| :--- | :--- | :--- | :--- |
| `GameManagerTest` | `testGetSortedPlayers()` | Validates thread-safe player list sorting and rank order. | **PASSED** |
| `ProtocolTest` | `testFormatAndExtractPayload()` | Validates prefix formatting and payload extraction integrity. | **PASSED** |
| `QuestionLoaderTest` | `loadQuestions_shouldReturnQuestionsFromDataFile()` | Verifies reading and parsing `data/questions.txt`. | **PASSED** |
| `QuestionLoaderTest` | `testFilterQuestions()` | Verifies category and difficulty filtering algorithms. | **PASSED** |

---

## 6. CONCLUSION & FUTURE SCOPE

### 6.1 Conclusion
QuizArena successfully demonstrates a production-grade, multi-threaded CLI multiplayer application in Java 21. By combining low-level TCP socket communication with higher-level synchronization primitives and a clear protocol state machine, QuizArena achieves real-time multiplayer coordination with robust error handling and attractive terminal presentation.

### 6.2 Future Scope
- **Web Interface**: Extend server to support WebSocket protocol for browser clients.
- **Database Persistence**: Integration with SQLite/MySQL for persistent global high-score tables.
- **Web UI & Sound FX**: Terminal audio cues and graphical web-based frontend options.
