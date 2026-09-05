package com.kizio.jumpdelayfix.jump;

import com.kizio.jumpdelayfix.jump.JumpHandler;

import com.kizio.jumpdelayfix.input.JumpInput;
import com.kizio.jumpdelayfix.model.JumpProfile;
import com.kizio.jumpdelayfix.state.RuntimeState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JumpHandlerIntegrationTest {

    private TickDrivenJumpInput input;
    private JumpHandler handler;

    @BeforeEach
    void setUp() {
        RuntimeState.setEnabled(true);
        RuntimeState.setProfile(JumpProfile.SMART);
        input = new TickDrivenJumpInput();
        handler = new JumpHandler(input);
    }

    @Test
    void shouldRejectJumpWhileSimulatedJumpTicksStayActive() {
        input.jumpPressed = true;
        input.jumpTicks = 4;

        tickPlayer(4);

        assertEquals(1, handler.getRejectedJumpCount());
        assertEquals(1, handler.getAdaptivePenaltyTicks());
    }

    @Test
    void shouldConfirmJumpAfterSimulatedJumpTicksExpire() {
        input.jumpPressed = true;

        tickPlayer(1);
        input.jumpTicks = 0;
        tickPlayer(3);

        assertEquals(1, handler.getConfirmedJumpCount());
        assertEquals(0, handler.getRejectedJumpCount());
        assertEquals(1, input.jumpCalls);
    }

    private void tickPlayer(int ticks) {
        for (int tick = 0; tick < ticks; tick++) {
            input.advanceTick();
            handler.tick();
        }
    }

    private static final class TickDrivenJumpInput implements JumpInput {

        private boolean jumpPressed;
        private boolean onGround = true;
        private boolean riseNextTick;
        private boolean sustainAirNextTick;
        private boolean landNextTick;
        private int jumpTicks;
        private int jumpCalls;
        private double playerY = 64.0D;
        private void advanceTick() {
            if (jumpTicks > 0) {
                jumpTicks--;
            }

            if (riseNextTick) {
                playerY = 65.0D;
                onGround = false;
                riseNextTick = false;
                sustainAirNextTick = true;
                return;
            }

            if (sustainAirNextTick) {
                playerY = 65.0D;
                onGround = false;
                sustainAirNextTick = false;
                landNextTick = true;
                return;
            }

            if (landNextTick) {
                playerY = 64.0D;
                onGround = true;
                landNextTick = false;
                return;
            }

            playerY = 64.0D;
            onGround = true;
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
            if (jumpTicks == 0) {
                riseNextTick = true;
            }
        }

        @Override
        public double getPlayerY() {
            return playerY;
        }
    }
}
