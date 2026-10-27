package com.kizio.jumpdelayfix.jump;

import com.kizio.jumpdelayfix.jump.JumpHandler;

import com.kizio.jumpdelayfix.input.JumpInput;
import com.kizio.jumpdelayfix.model.JumpProfile;
import com.kizio.jumpdelayfix.state.RuntimeState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JumpHandlerTest {

    private FakeJumpInput input;
    private JumpHandler handler;

    @BeforeEach
    void setUp() {
        RuntimeState.setEnabled(true);
        RuntimeState.setProfile(JumpProfile.SMART);
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
        RuntimeState.setEnabled(false);
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

        handler.tick();
        handler.tick();
        handler.tick();
        handler.tick();
        handler.tick();

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

    @Test
    void shouldBufferJumpPressedBeforeLanding() {
        input.jumpPressed = true;
        input.onGround = false;

        handler.tick();

        input.jumpPressed = false;
        input.onGround = true;
        handler.tick();

        assertEquals(1, input.jumpCalls);
    }

    @Test
    void shouldTreatLongFallAirborneTimeoutAsInconclusiveWithoutPenaltyOrConfirmation() {
        input.jumpPressed = true;
        input.onGround = true;
        input.playerY = 64.0D;
        handler.tick();

        input.onGround = false;
        input.playerY = 65.0D;
        for (int i = 0; i < 45; i++) {
            handler.tick();
        }

        assertEquals(0, handler.getConfirmedJumpCount());
        assertEquals(0, handler.getRejectedJumpCount());
        assertEquals(1, handler.getInconclusiveJumpCount());
        assertEquals(0, handler.getAdaptivePenaltyTicks());
    }

    @Test
    void shouldNotReduceAdaptivePenaltyOnLongFallTimeout() {
        input.jumpPressed = true;
        input.onGround = true;
        handler.tick();

        for (int i = 0; i < 4; i++) {
            handler.tick();
        }
        assertEquals(1, handler.getAdaptivePenaltyTicks());
        assertEquals(1, handler.getRejectedJumpCount());

        handler.tick();
        input.onGround = false;
        input.playerY = 70.0D;
        for (int i = 0; i < 25; i++) {
            handler.tick();
        }

        assertEquals(1, handler.getInconclusiveJumpCount());
        assertEquals(0, handler.getConfirmedJumpCount());
        assertEquals(1, handler.getRejectedJumpCount());
        assertEquals(1, handler.getAdaptivePenaltyTicks());
    }

    @Test
    void shouldNotAdvanceSuccessfulJumpStreakOrReducePenaltyOnInconclusiveTimeout() {
        RuntimeState.setProfile(JumpProfile.SMART);
        input.jumpPressed = true;
        input.onGround = true;
        handler.tick();
        for (int i = 0; i < 4; i++) {
            handler.tick();
        }
        assertEquals(1, handler.getAdaptivePenaltyTicks());
        assertEquals(1, handler.getRejectedJumpCount());

        for (int jump = 0; jump < 2; jump++) {
            for (int g = 0; g < handler.getRequiredGroundedTicks(); g++) {
                input.onGround = true;
                handler.tick();
            }
            input.onGround = false;
            input.playerY = 65.0D;
            handler.tick();
            handler.tick();
            handler.tick();
            input.onGround = true;
            input.playerY = 64.0D;
            handler.tick();
        }
        assertEquals(2, handler.getConfirmedJumpCount());
        assertEquals(1, handler.getAdaptivePenaltyTicks());

        for (int g = 0; g < handler.getRequiredGroundedTicks(); g++) {
            input.onGround = true;
            handler.tick();
        }
        input.onGround = false;
        input.playerY = 70.0D;
        for (int i = 0; i < 25; i++) {
            handler.tick();
        }

        assertEquals(1, handler.getInconclusiveJumpCount());
        assertEquals(2, handler.getConfirmedJumpCount());
        assertEquals(1, handler.getRejectedJumpCount());
        assertEquals(1, handler.getAdaptivePenaltyTicks());

        for (int g = 0; g < handler.getRequiredGroundedTicks(); g++) {
            input.onGround = true;
            handler.tick();
        }
        input.onGround = false;
        input.playerY = 65.0D;
        handler.tick();
        handler.tick();
        handler.tick();
        input.onGround = true;
        input.playerY = 64.0D;
        handler.tick();

        assertEquals(3, handler.getConfirmedJumpCount());
        assertEquals(0, handler.getAdaptivePenaltyTicks());
    }

    @Test
    void shouldTreatLowCeilingJumpAsInconclusiveWithoutPenalty() {
        input.jumpPressed = true;
        input.onGround = true;
        input.playerY = 64.0D;
        handler.tick();

        input.onGround = false;
        input.playerY = 64.3D;
        handler.tick();

        input.onGround = true;
        input.playerY = 64.0D;
        handler.tick();

        assertEquals(1, handler.getInconclusiveJumpCount());
        assertEquals(0, handler.getConfirmedJumpCount());
        assertEquals(0, handler.getRejectedJumpCount());
        assertEquals(0, handler.getAdaptivePenaltyTicks());
    }

    @Test
    void shouldNotReducePenaltyOrCountSuccessOnLowCeilingJumpWithExistingPenalty() {
        input.jumpPressed = true;
        input.onGround = true;
        input.playerY = 64.0D;
        handler.tick();

        for (int i = 0; i < 4; i++) {
            handler.tick();
        }
        assertEquals(1, handler.getAdaptivePenaltyTicks());
        assertEquals(1, handler.getRejectedJumpCount());

        for (int g = 0; g < handler.getRequiredGroundedTicks(); g++) {
            input.onGround = true;
            handler.tick();
        }

        input.onGround = false;
        input.playerY = 64.2D;
        handler.tick();

        input.onGround = true;
        input.playerY = 64.0D;
        handler.tick();

        assertEquals(1, handler.getInconclusiveJumpCount());
        assertEquals(0, handler.getConfirmedJumpCount());
        assertEquals(1, handler.getRejectedJumpCount());
        assertEquals(1, handler.getAdaptivePenaltyTicks());
    }

    @Test
    void shouldJumpImmediatelyOnLandingWhenSingleGroundedTickRequired() {
        RuntimeState.setProfile(JumpProfile.COMPETITIVE);
        input.requiredGroundedTicksBeforeJump = 1;

        input.jumpPressed = true;
        input.onGround = true;
        input.playerY = 64.0D;
        handler.tick();

        assertEquals(1, input.jumpCalls);

        input.onGround = false;
        input.playerY = 65.0D;
        handler.tick();

        input.playerY = 65.2D;
        handler.tick();

        input.onGround = true;
        input.playerY = 64.0D;
        handler.tick();

        assertEquals(2, input.jumpCalls);
        assertEquals(1, handler.getConfirmedJumpCount());
    }

    @Test
    void shouldWaitExtraGroundedTickWhenTwoGroundedTicksRequired() {
        RuntimeState.setProfile(JumpProfile.SMART);
        input.requiredGroundedTicksBeforeJump = 2;

        input.jumpPressed = true;
        input.onGround = true;
        input.playerY = 64.0D;
        handler.tick();
        assertEquals(0, input.jumpCalls);

        handler.tick();
        assertEquals(1, input.jumpCalls);

        input.onGround = false;
        input.playerY = 65.0D;
        handler.tick();
        input.playerY = 65.2D;
        handler.tick();

        input.onGround = true;
        input.playerY = 64.0D;
        handler.tick();

        assertEquals(1, input.jumpCalls);
        assertEquals(1, handler.getConfirmedJumpCount());

        handler.tick();

        assertEquals(2, input.jumpCalls);
    }

    @Test
    void shouldDelegateServerIdentifierAndLatency() {
        assertEquals("global", handler.getServerIdentifier());
        assertEquals(-1, handler.getLatencyMs());
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
