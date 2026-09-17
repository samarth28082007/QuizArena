QuizArena – Real-Time Synchronized Multiplayer Quiz (CLI)

Course/Domain: Java Programming / Software Engineering
Tech Stack: Java 21, Maven, TCP Sockets, JUnit 5

1. Abstract

QuizArena is a command-line, multiplayer quiz built in Java around a client-server model: a group of players connect to one central server and work through the same quiz together, in lockstep.

The idea grew out of a fairly specific annoyance with basic socket-based quizzes — players race ahead at different speeds, and before long nobody's looking at the same question anymore. QuizArena fixes that by leaning on Java's concurrency tools so every player has to finish a round before the game lets anyone move on.

Beyond the core synchronization piece, the app has a live leaderboard, question categories and difficulty tiers, a per-question timer, and a terminal interface dressed up just enough to stay readable. A set of JUnit 5 tests covers the pieces that matter most.

More than anything, the project was a hands-on tour through Java networking, multithreading and synchronization, file handling, exception handling, and testing — the kind of things that are hard to really internalize without building something that breaks in interesting ways first.

2. Introduction
2.1 Problem Statement

Most beginner quiz programs assume a single player at one terminal, so the moment you add a second player over a socket, new problems show up. The obvious one: if Player A answers in two seconds and Player B takes twenty, what happens to Question 2 in the meantime? Left unchecked, the two players simply drift apart, and the leaderboard stops meaning anything.

There's also the matter of players who go quiet — a dropped connection, a closed terminal, someone who just walks away — and the server has to keep the game moving without waiting on them forever.

A second, quieter challenge is the conversation between client and server itself. TCP sockets just move bytes; they don't know a question from an answer from a leaderboard update. Without structure imposed on top, it's easy for a client to misread one message as another.

QuizArena was built to work through these problems directly: keep every player on the same question at the same time, and give the server a clear way to manage that shared state.

2.2 Objectives
Build a multiplayer quiz on top of Java TCP sockets
Support several players connecting to one server at once
Keep everyone synchronized through each round of the quiz
Give every question a timer
Calculate scores automatically as the game runs
Show a leaderboard that updates after each round
Let questions be filtered by category and difficulty
Handle disconnects and timeouts without stalling the game
Keep the command-line interface simple and readable
Cover the important logic with JUnit tests
3. Technologies Used

Nothing exotic here — the project leans on the standard Java toolkit rather than external frameworks, which kept the focus on understanding what's actually happening under the hood.

Technology	Purpose
Java 21	Core language for both server and client
Maven	Project structure and dependency management
TCP Sockets	Server–client communication
JUnit 5	Unit testing
Multithreading	One handler thread per connected player
CountDownLatch	Keeping players synchronized within a round
File Handling	Loading questions from a text file
ANSI Escape Codes	Giving the terminal UI some shape
4. System Architecture

QuizArena follows a fairly conventional client-server split. Every player runs a QuizClient that opens a TCP connection to the server; the server spins up a dedicated ClientHandler thread for each connection and hands the shared game state to a single GameManager. That GameManager is really the heart of the whole thing — it keeps score, holds the question list, and makes sure nobody gets ahead of the group.
Splitting responsibilities this way keeps each class focused: a ClientHandler doesn't need to know how scoring works, and the GameManager doesn't need to know anything about sockets.

5. Main Components

5.1 QuizServer — The entry point on the server side. It opens a ServerSocket on port 5000 and sits in a loop accepting connections — every time a player joins, it hands that socket off to a new ClientHandler thread and goes back to listening.

5.2 ClientHandler — One of these runs per connected player, and it's essentially the translator between that player's socket and the rest of the game. Its job covers reading the player's username, pushing questions out, reading back answers, watching for timeouts, forwarding leaderboard updates, and generally keeping that one connection alive and well-behaved.

5.3 GameManager — If there's a single class that holds the project together, it's this one. It tracks connected players, the active question set, running scores, who got what right or wrong, and builds the leaderboard after each round. It's also where the synchronization logic lives — GameManager creates and waits on the CountDownLatch that keeps every player's round in step with everyone else's.

5.4 Question — A small data class representing one quiz question: the question text, four answer options, the correct answer, a category, and a difficulty level (EASY, MEDIUM, or HARD).

5.5 Player — Tracks everything about one participant: username, current score, and running counts of correct and incorrect answers. The score field gets touched after every single question.

5.6 QuestionLoader — Reads the question bank in from questions.txt at startup. Each line follows a simple pipe-delimited layout:

CATEGORY|DIFFICULTY|QUESTION|OPT_A|OPT_B|OPT_C|OPT_D|ANSWER

The upside of this format is that new questions can be dropped in — or existing ones tweaked — without touching a line of Java.

5.7 QuizClient — The program each player actually runs. It opens the TCP connection, renders incoming questions, collects the player's answer from the keyboard, sends it back, and displays scores and leaderboard updates as they arrive.

5.8 QuizApp — A standalone, single-player mode that skips networking entirely — no server, no socket, just one person and the quiz, still with a timer on each question. Handy for testing question content without needing a second terminal open.

6. Multiplayer Game Flow
Step 1  -> Server starts, listens on port 5000
Step 2  -> Players connect via QuizClient (TCP)
Step 3  -> Lobby: first player is host, waits for 2+ players
Step 4  -> Host configures category, difficulty, time limit
Step 5  -> Countdown, game starts
Step 6  -> Question round: same question + timer to everyone
Step 7  -> Answers collected (late = timeout)
Step 8  -> Scores calculated (+10 per correct answer)
Step 9  -> Leaderboard broadcast to all clients
Step 10 -> Next round begins  ---> loops back to Step 6
              (repeats until questions run out)

Step 1 — Server Starts. The server binds to port 5000 and begins listening for incoming connections.

Step 2 — Players Connect. Each player launches QuizClient, which opens a socket to the server.

Step 3 — Lobby. Whoever connects first is treated as the host. The game stays in the lobby until at least two players are present.

Step 4 — Game Configuration. The host chooses the category, difficulty, and time limit for the session; these settings apply to the whole game.

Step 5 — Game Starts. Once enough players have joined, the server runs a short countdown and the first question goes out.

Step 6 — Question Round. Every player receives the exact same question at the exact same moment, with an identical time limit to answer it.

Step 7 — Answer Collection. Players send answers back as they decide. Anyone who runs out the clock is simply recorded as a timeout.

Step 8 — Score Calculation. A correct answer is worth 10 points; wrong answers and timeouts add nothing.

Step 9 — Leaderboard. Once the round closes, updated scores are calculated and the refreshed leaderboard goes out to every client.

Step 10 — Next Round. Only after the current round is fully wrapped up does the server move on — which is really the whole point of the synchronization design.

7. Synchronization

This is arguably the feature the whole project was built to prove out. Without some form of coordination, a quick player could clear Question 1 and be looking at Question 2 while a slower player is still reading Question 1 — at which point the "multiplayer" part stops meaning much.

QuizArena solves this with a CountDownLatch shared across the round: every player submission counts it down, and a background timer counts it down too if the time limit is reached first. The server simply blocks on that latch, so the next question can't be released until either everyone has answered or the clock has run out for anyone who hasn't.

Question 1
    |
    +---- Player 1 answers .......... (latch--)
    |
    +---- Player 2 answers .......... (latch--)
    |
    +---- Player 3: timer expires .... (latch--)
    |
    v
latch.await() releases
    |
    v
Leaderboard sent
    |
    v
Question 2

The practical effect: fast and slow players alike always see the leaderboard, and the next question, at the same time.

8. Leaderboard

After each question, QuizArena recalculates standings and pushes them out immediately — players don't have to wait until the game ends to see where they stand. Scores drive the primary sort, and the number of correct answers acts as a tiebreaker.

================================
          LEADERBOARD
================================

Rank   Player       Score   Correct
------------------------------------
1      Samarth       40       4
2      Rahul         30       3
3      Aryan         20       2

================================
9. Message Protocol

Plain TCP just moves bytes back and forth — it has no concept of what any of those bytes mean. To keep both sides honest about what they're sending, QuizArena tags every message with a short prefix identifying its type:

[WELCOME]
[QUESTION_START]
[PROMPT_ANSWER]
[LOBBY_UPDATE]
[LEADERBOARD]

Rather than the client guessing at what a raw line of text represents, it checks the prefix first and routes the message accordingly. A small addition, but it removes a whole class of bugs around misinterpreted messages.

10. User Interface

QuizArena is a terminal app at heart, but a little formatting effort goes a long way. ANSI escape codes give headings, questions, timers, scores, and leaderboards enough visual separation to stay readable even in a plain console window.

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

The goal was never a flashy terminal UI — just one where a player can glance at the screen and immediately tell what's being asked, what their score is, and how much time is left.

11. Testing

JUnit 5 covers the logic that would be hardest to catch by eye during a live game — sorting, message parsing, and file loading — and the suite runs through Maven with a single mvn test.

Test	Purpose	Result
GameManagerTest	Player sorting and leaderboard ordering	Passed
ProtocolTest	Message formatting and payload extraction	Passed
QuestionLoaderTest	Loading questions from file	Passed
QuestionLoaderTest	Category and difficulty filtering	Passed

Running these before wiring everything together over sockets made it much easier to tell whether a bug lived in the game logic or in the networking layer — a distinction that matters a lot once threads get involved.

12. Challenges Faced

12.1 Synchronizing Players — Getting every player to land on the same question at the same time was, unsurprisingly, the hardest part of the build. CountDownLatch turned out to be the right tool once the problem was framed correctly — as a barrier the whole round has to clear, not as something to manage per player.

12.2 Handling Timeouts — A player who never answers can't be allowed to stall the game indefinitely, so the server needed a way to move on after a fixed window regardless of who has or hasn't responded.

12.3 Multiple Client Connections — Serving several players at once meant giving each one an independent handler thread rather than trying to process everyone sequentially on a single thread.

12.4 Maintaining Shared Game State — Scores, the player list, and the current question all get touched from multiple threads at once, so keeping that state consistent took care — this is where a fair number of the trickier bugs showed up during development.

12.5 Network Communication — Making sure the client and server agreed on what a given message meant was solved by introducing the tagged protocol described earlier, rather than leaving message interpretation implicit.

13. Future Improvements

13.1 Persistent Leaderboard — Right now scores live only in memory for the duration of a session. Adding SQLite or MySQL would let rankings survive past a server restart.

13.2 More Game Modes — Classic Quiz, Speed Round, Survival Mode, Team Battle, Knockout Mode.

13.3 Better Scoring System — A flat 10 points per correct answer is simple but doesn't reward speed:

Fast answer     -> 15 points
Normal answer   -> 10 points
Last-second     -> 5 points
Wrong answer    -> 0 points

13.4 Web-Based Interface — The terminal client could eventually be swapped for, or paired with, a browser-based front end — the server's game logic wouldn't need to change much to support it.

13.5 Authentication — Letting players create accounts would open the door to persistent stats and achievements across sessions.

13.6 Question Bank Expansion — Programming, Science, History, Sports, Movies, General Knowledge, Artificial Intelligence.

13.7 Admin Panel — An interface for adding, editing, or removing questions would beat hand-editing questions.txt directly, especially as the question bank grows.

13.8 Reconnection Support — A player who briefly drops off the network shouldn't necessarily be booted from the game — allowing a reconnect back into the same session would make the whole thing more forgiving to play.

14. Conclusion

QuizArena set out to answer a fairly narrow question — how do you keep several players honestly in sync during a live quiz — and ended up touching most of the core ideas in Java networking and concurrency along the way: sockets, threads, a shared mutable game state, and the coordination primitives needed to manage all three safely.

The timer, category filtering, live scoring, and leaderboard make it feel like an actual game rather than a protocol demo, but the synchronization logic is really the piece the rest of the project was built around.

More than the finished program, what stuck was the practical experience: socket programming, multithreading, synchronization, file handling, exception handling, and the testing habits that make all of the above easier to trust. The current version is a reasonably solid base to build on, whether the next step is a persistent database, a web front end, or both.
