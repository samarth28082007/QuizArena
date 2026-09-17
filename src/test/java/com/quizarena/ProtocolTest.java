package com.quizarena;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ProtocolTest {

    @Test
    void testFormatAndExtractPayload() {
        String msg = Protocol.format(Protocol.PREFIX_WELCOME, "Hello World");
        assertTrue(Protocol.isType(msg, Protocol.PREFIX_WELCOME));
        assertEquals("Hello World", Protocol.extractPayload(msg, Protocol.PREFIX_WELCOME));
    }
}
