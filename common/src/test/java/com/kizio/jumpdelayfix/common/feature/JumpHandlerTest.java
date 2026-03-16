package com.kizio.jumpdelayfix.common.feature;

import com.kizio.jumpdelayfix.common.api.JumpInput;
import com.kizio.jumpdelayfix.common.model.JumpProfile;
import com.kizio.jumpdelayfix.common.state.ModState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JumpHandlerTest {

    private FakeJumpInput input;
    private JumpHandler handler;

    @BeforeEach
    void setUp() {
        ModState.setEnabled(true);
        ModState.setProfile(JumpProfile.SMART);
        input = new FakeJumpInput();
        handler = new JumpHandler(input);
    }

    @Test
    void shouldJumpWhenEnabledAndConditionsMatch() {
        input.jumpPressed = true;
        input.onGround = true;

        handler.tick();

        assertEquals(1, input.jumpCalls);
    }

    @Test
    void shouldNotJumpWhenDisabled() {
        ModState.setEnabled(false);
        input.jumpPressed = true;
        input.onGround = true;

        handler.tick();

        assertEquals(0, input.jumpCalls);
    }

    @Test
    void shouldNotJumpWhenPlayerIsNotOnGround() {
        input.jumpPressed = true;
        input.onGround = false;

        handler.tick();

        assertEquals(0, input.jumpCalls);
    }

    @Test
    void shouldDelayJumpUntilRequiredGroundedTicks() {
        input.jumpPressed = true;
        input.onGround = true;
        input.requiredGroundedTicksBeforeJump = 2;

        handler.tick();
        handler.tick();

        assertEquals(1, input.jumpCalls);
    }

    @Test
    void shouldIncreaseSafetyAfterRejectedJumpAndRetry() {
        input.jumpPressed = true;
        input.onGround = true;
        input.playerY = 64.0D;

        handler.tick(); // first attempt
        handler.tick(); // wait 1
        handler.tick(); // wait 2
        handler.tick(); // wait 3, rejection -> penalty + retry

        assertEquals(2, input.jumpCalls);
    }

    @Test
    void shouldNotSpamJumpWhileAwaitingServerResult() {
        input.jumpPressed = true;
        input.onGround = true;
        input.playerY = 64.0D;

        handler.tick();
        handler.tick();
        handler.tick();

        assertEquals(1, input.jumpCalls);
    }

    private static final class FakeJumpInput implements JumpInput {

        private boolean jumpPressed;
        private boolean onGround;
        private int jumpCalls;
        private int requiredGroundedTicksBeforeJump = 1;
        private double playerY;

        @Override
        public boolean isJumpPressed() {
            return jumpPressed;
        }

        @Override
        public boolean isPlayerOnGround() {
            return onGround;
        }

        @Override
        public void jump() {
            jumpCalls++;
        }

        @Override
        public int requiredGroundedTicksBeforeJump() {
            return requiredGroundedTicksBeforeJump;
        }

        @Override
        public double getPlayerY() {
            return playerY;
        }
    }
}
