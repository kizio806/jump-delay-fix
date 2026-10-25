package com.kizio.jumpdelayfix;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConstantsTest {

    @Test
    void shouldVerifyModConstants() {
        assertEquals("jumpdelayfix", Constants.MOD_ID);
        assertEquals("Jump Delay Fix", Constants.MOD_NAME);
    }
}
