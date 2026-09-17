@echo off
title QuizArena Launcher

echo ================================
echo        QUIZARENA
echo ================================
echo.
echo Compiling project...
call mvn compile

if errorlevel 1 (
    echo.
    echo Compilation failed!
    pause
    exit /b
)

echo.
echo Compilation successful!
echo Starting QuizArena Server...
echo.

start "QuizArena Server" cmd /k "java -cp target\classes com.quizarena.QuizServer"

timeout /t 2 /nobreak >nul

start "QuizArena Player 1" cmd /k "java -cp target\classes com.quizarena.QuizClient"

timeout /t 1 /nobreak >nul

start "QuizArena Player 2" cmd /k "java -cp target\classes com.quizarena.QuizClient"

exit