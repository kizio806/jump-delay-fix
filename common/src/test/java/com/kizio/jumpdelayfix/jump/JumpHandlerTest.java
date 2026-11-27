package com.kizio.jumpdelayfix.jump;

import com.kizio.jumpdelayfix.jump.JumpHandler;

import com.kizio.jumpdelayfix.input.JumpInput;
import com.kizio.jumpdelayfix.model.JumpProfile;
import com.kizio.jumpdelayfix.state.RuntimeState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JumpHandlerTest {

    private FakeJumpInput input;
    private RuntimeState runtimeState;
    private JumpHandler handler;

    @BeforeEach
    void setUp() {
        RuntimeState.reset();
        runtimeState = new RuntimeState(true, JumpProfile.SMART);
        input = new FakeJumpInput();
        handler = new JumpHandler(input, runtimeState);
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
        runtimeState.setStateEnabled(false);
        input.jumpPressed = true;
        input.onGround = true;

        handler.tick();

        assertEquals(0, input.jumpCalls);
    }

    @Test
    void shouldRespectInjectedRuntimeStateIndependentlyOfGlobalState() {
        RuntimeState isolatedState = new RuntimeState(true, JumpProfile.COMPETITIVE);
        FakeJumpInput isolatedInput = new FakeJumpInput();
        JumpHandler isolatedHandler = new JumpHandler(isolatedInput, isolatedState);

        RuntimeState.setEnabled(false);
        RuntimeState.setProfile(JumpProfile.STABLE);

        isolatedInput.jumpPressed = true;
        isolatedInput.onGround = true;

        isolatedHandler.tick();

        assertEquals(1, isolatedInput.jumpCalls, "Isolated handler must use injected enabled state");

        isolatedState.setStateEnabled(false);
        isolatedHandler.tick();
        assertEquals(1, isolatedInput.jumpCalls, "Isolated handler must honor injected disabled state");
    }

    @Test
    void shouldConstructWithDefaultRuntimeState() {
        JumpHandler defaultHandler = new JumpHandler(input);
        assertNotNull(defaultHandler);
        input.jumpPressed = true;
        input.onGround = true;
        defaultHandler.tick();
        assertEquals(1, input.jumpCalls);
    }

    @Test
    void shouldThrowWhenConstructorArgumentsAreNull() {
        assertThrows(NullPointerException.class, () -> new JumpHandler(null));
        assertThrows(NullPointerException.class, () -> new JumpHandler(null, runtimeState));
        assertThrows(NullPointerException.class, () -> new JumpHandler(input, null));
    }

    @Test
    void shouldReturnNoOpInstance() {
        JumpHandler noOp = JumpHandler.noOp();
        assertNotNull(noOp);
        noOp.tick();
        noOp.resetTransientState();
        assertEquals(0, noOp.getConfirmedJumpCount());
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
        runtimeState.setStateProfile(JumpProfile.SMART);
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
        runtimeState.setStateProfile(JumpProfile.COMPETITIVE);
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
        runtimeState.setStateProfile(JumpProfile.SMART);
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

    @Test
    void shouldResetTransientStateWhenPlayerBecomesInvalidWhileEnabled() {
        input.jumpPressed = true;
        input.onGround = true;
        handler.tick();
        assertEquals(1, input.jumpCalls);

        input.playerValid = false;
        handler.tick();
        assertEquals(1, input.jumpCalls, "Handler must return early without jumping when player is invalid");
    }

    @Test
    void shouldEnforceJumpAttemptCooldownWhenSufficientlyGrounded() {
        runtimeState.setStateProfile(JumpProfile.STABLE);
        input.requiredGroundedTicksBeforeJump = 2;
        input.jumpPressed = true;
        input.onGround = true;

        // Ticks 1 to 3: groundedTicks and ticksSinceLastJumpAttempt reach 3 -> jump triggers on tick 3
        handler.tick();
        handler.tick();
        handler.tick();
        assertEquals(1, input.jumpCalls);

        // Ticks 4, 5, 6: player remains grounded with no lift -> jump is rejected on tick 6
        // Adaptive penalty increases required ticks from 3 to 4.
        handler.tick();
        handler.tick();
        handler.tick();
        assertEquals(1, input.jumpCalls);
        assertEquals(1, handler.getRejectedJumpCount());

        // Tick 7: groundedTicks is 7 (>= 4), but ticksSinceLastJumpAttempt is 3 (< 4).
        // Must return early via jump attempt cooldown condition.
        handler.tick();
        assertEquals(1, input.jumpCalls, "Jump attempt cooldown must prevent immediate jump re-trigger while remaining grounded");

        // Tick 8: ticksSinceLastJumpAttempt reaches 4 (>= 4) -> second jump attempt fires
        handler.tick();
        assertEquals(2, input.jumpCalls);
    }

    @Test
    void shouldHandleNanCoordinatesGracefullyDuringJumpTracking() {
        input.jumpPressed = true;
        input.onGround = true;
        input.playerY = Double.NaN;

        handler.tick();
        assertEquals(1, input.jumpCalls);

        input.onGround = false;
        input.playerY = 65.0D;
        handler.tick();
        handler.tick();

        input.onGround = true;
        input.playerY = Double.NaN;
        handler.tick();

        assertEquals(1, handler.getConfirmedJumpCount(), "Jump with NaN start coordinates must resolve safely when airborne");
    }

    private static final class FakeJumpInput implements JumpInput {

        private boolean playerValid = true;
        private boolean jumpPressed;
        private boolean onGround;
        private int jumpCalls;
        private int requiredGroundedTicksBeforeJump = 1;
        private double playerY;

        @Override
        public boolean isPlayerValid() {
            return playerValid;
        }

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
