package com.quizarena;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;

class GameManagerTest {

    @Test
    void testGetSortedPlayers() {
        Player p1 = new Player("Alice");
        p1.addScore(10);
        p1.addCorrectAnswer();

        Player p2 = new Player("Bob");
        p2.addScore(20);
        p2.addCorrectAnswer();
        p2.addCorrectAnswer();

        // Create mock handlers
        ClientHandler h1 = new ClientHandler(null) {
            @Override
            public Player getPlayer() {
                return p1;
            }
        };

        ClientHandler h2 = new ClientHandler(null) {
            @Override
            public Player getPlayer() {
                return p2;
            }
        };

        GameManager.addPlayer(h1);
        GameManager.addPlayer(h2);

        List<Player> sorted = GameManager.getSortedPlayers();
        assertNotNull(sorted);
        assertEquals(2, sorted.size());
        assertEquals("Bob", sorted.get(0).getUsername());
        assertEquals("Alice", sorted.get(1).getUsername());

        // Cleanup
        GameManager.removePlayer(h1);
        GameManager.removePlayer(h2);
    }
}
