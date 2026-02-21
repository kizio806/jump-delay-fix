package com.kizio.jumpdelayfix.common.feature;

import com.kizio.jumpdelayfix.common.api.JumpInput;
import com.kizio.jumpdelayfix.common.model.JumpProfile;
import com.kizio.jumpdelayfix.common.state.ModState;

import java.util.Objects;

public final class JumpHandler {

    private static final JumpHandler NO_OP = new JumpHandler(NoOpJumpInput.INSTANCE);
    private static final int JUMP_RESULT_TIMEOUT_TICKS = 3;
    private static final double JUMP_SUCCESS_MIN_HEIGHT_DELTA = 0.005D;

    private final JumpInput input;
    private int groundedTicks;
    private int ticksSinceLastJumpAttempt;
    private int adaptivePenaltyTicks;
    private int successfulJumpStreak;
    private int confirmedJumpCount;
    private int rejectedJumpCount;
    private int shadowJumpPredictionCount;
    private int lastRequiredGroundedTicks = 1;
    private boolean shadowMode;

    private boolean awaitingJumpResult;
    private int jumpResultTicksLeft;
    private double jumpStartY = Double.NaN;

    public JumpHandler(JumpInput input) {
        this.input = Objects.requireNonNull(input, "input");
    }

    public static JumpHandler noOp() {
        return NO_OP;
    }

    public void tick() {
        if (!ModState.isEnabled()) {
            return;
        }

        updateAdaptivePenaltyFromJumpResult();

        boolean onGround = input.isPlayerOnGround();
        if (!onGround) {
            groundedTicks = 0;
            ticksSinceLastJumpAttempt = 0;
            return;
        }

        groundedTicks++;
        ticksSinceLastJumpAttempt++;

        if (!input.isJumpPressed()) {
            return;
        }

        if (awaitingJumpResult) {
            return;
        }

        JumpProfile profile = ModState.getProfile();
        int requiredGroundedTicks = Math.max(
                1,
                input.requiredGroundedTicksBeforeJump()
                        + profile.groundedTicksOffset()
                        + adaptivePenaltyTicks
        );
        lastRequiredGroundedTicks = requiredGroundedTicks;

        if (groundedTicks < requiredGroundedTicks || ticksSinceLastJumpAttempt < requiredGroundedTicks) {
            return;
        }

        if (shadowMode) {
            shadowJumpPredictionCount++;
            ticksSinceLastJumpAttempt = 0;
            return;
        }

        input.jump();
        ticksSinceLastJumpAttempt = 0;
        startJumpResultTracking();
    }

    private void updateAdaptivePenaltyFromJumpResult() {
        if (!awaitingJumpResult) {
            return;
        }

        double currentY = input.getPlayerY();
        if (!Double.isNaN(currentY) && !Double.isNaN(jumpStartY) && currentY > jumpStartY + JUMP_SUCCESS_MIN_HEIGHT_DELTA) {
            onJumpConfirmed();
            return;
        }

        jumpResultTicksLeft--;
        if (jumpResultTicksLeft <= 0) {
            onJumpRejected();
        }
    }

    private void startJumpResultTracking() {
        awaitingJumpResult = true;
        jumpResultTicksLeft = JUMP_RESULT_TIMEOUT_TICKS;
        jumpStartY = input.getPlayerY();
    }

    private void onJumpConfirmed() {
        awaitingJumpResult = false;
        jumpStartY = Double.NaN;
        confirmedJumpCount++;

        JumpProfile profile = ModState.getProfile();
        successfulJumpStreak++;
        if (adaptivePenaltyTicks > 0 && successfulJumpStreak >= profile.successfulJumpsToReducePenalty()) {
            adaptivePenaltyTicks--;
            successfulJumpStreak = 0;
        }
    }

    private void onJumpRejected() {
        awaitingJumpResult = false;
        jumpStartY = Double.NaN;
        successfulJumpStreak = 0;
        rejectedJumpCount++;

        JumpProfile profile = ModState.getProfile();
        adaptivePenaltyTicks = Math.min(profile.maxAdaptivePenaltyTicks(), adaptivePenaltyTicks + 1);
    }

    public void setShadowMode(boolean shadowMode) {
        this.shadowMode = shadowMode;
    }

    public int getAdaptivePenaltyTicks() {
        return adaptivePenaltyTicks;
    }

    public int getRequiredGroundedTicks() {
        return Math.max(1, lastRequiredGroundedTicks);
    }

    public int getConfirmedJumpCount() {
        return confirmedJumpCount;
    }

    public int getRejectedJumpCount() {
        return rejectedJumpCount;
    }

    public int getShadowJumpPredictionCount() {
        return shadowJumpPredictionCount;
    }

    public int getLatencyMs() {
        return input.getLatencyMs();
    }

    public String getServerIdentifier() {
        return input.getServerIdentifier();
    }

    private enum NoOpJumpInput implements JumpInput {
        INSTANCE;

        @Override
        public boolean isJumpPressed() {
            return false;
        }

        @Override
        public boolean isPlayerOnGround() {
            return false;
        }

        @Override
        public void jump() {
            // Intentional no-op.
        }
    }
}
