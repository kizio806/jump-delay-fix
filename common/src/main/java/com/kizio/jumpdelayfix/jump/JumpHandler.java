package com.kizio.jumpdelayfix.jump;

import com.kizio.jumpdelayfix.input.JumpInput;
import com.kizio.jumpdelayfix.model.JumpProfile;
import com.kizio.jumpdelayfix.state.RuntimeState;

import java.util.Objects;
public final class JumpHandler {

    private static final JumpHandler NO_OP = new JumpHandler(NoOpJumpInput.INSTANCE);
    private static final int JUMP_INPUT_BUFFER_TICKS = 3;
    private static final int JUMP_NO_LIFT_TIMEOUT_TICKS = 3;
    private static final int JUMP_LANDING_TIMEOUT_TICKS = 20;
    private static final int MIN_AIRBORNE_TICKS_FOR_CONFIRMATION = 2;
    private static final double JUMP_SUCCESS_MIN_HEIGHT_DELTA = 0.005D;

    private final JumpInput input;
    private int groundedTicks;
    private int ticksSinceLastJumpAttempt;
    private int bufferedJumpTicks;
    private int adaptivePenaltyTicks;
    private int successfulJumpStreak;
    private int confirmedJumpCount;
    private int rejectedJumpCount;
    private int lastRequiredGroundedTicks = 1;

    private boolean awaitingJumpResult;
    private boolean jumpLiftObserved;
    private int jumpAirborneTicks;
    private int jumpNoLiftTicksLeft;
    private int jumpLandingTicksLeft;
    private double jumpStartY = Double.NaN;

    public JumpHandler(JumpInput input) {
        this.input = Objects.requireNonNull(input, "input");
    }

    public static JumpHandler noOp() {
        return NO_OP;
    }
    public void tick() {
        if (!RuntimeState.isEnabled()) {
            resetTransientState();
            return;
        }

        updateJumpBuffer();
        if (updateAdaptivePenaltyFromJumpResult()) {
            return;
        }

        boolean onGround = input.isPlayerOnGround();
        if (!onGround) {
            groundedTicks = 0;
            return;
        }

        groundedTicks++;
        ticksSinceLastJumpAttempt++;

        if (bufferedJumpTicks <= 0) {
            return;
        }

        if (awaitingJumpResult) {
            return;
        }

        JumpProfile profile = RuntimeState.getProfile();
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

        input.jump();
        bufferedJumpTicks = 0;
        ticksSinceLastJumpAttempt = 0;
        startJumpResultTracking();
    }

    private void updateJumpBuffer() {
        if (input.isJumpPressed()) {
            bufferedJumpTicks = JUMP_INPUT_BUFFER_TICKS;
        } else if (bufferedJumpTicks > 0) {
            bufferedJumpTicks--;
        }
    }

    private boolean updateAdaptivePenaltyFromJumpResult() {
        if (!awaitingJumpResult) {
            return false;
        }

        boolean onGround = input.isPlayerOnGround();
        double currentY = input.getPlayerY();
        if (!Double.isNaN(currentY) && !Double.isNaN(jumpStartY) && currentY > jumpStartY + JUMP_SUCCESS_MIN_HEIGHT_DELTA) {
            jumpLiftObserved = true;
        }

        if (!onGround) {
            jumpLiftObserved = true;
            jumpAirborneTicks++;
        }

        if (!jumpLiftObserved) {
            jumpNoLiftTicksLeft--;
            if (jumpNoLiftTicksLeft <= 0) {
                onJumpRejected();
                return true;
            }
            return false;
        }

        if (onGround) {
            if (jumpAirborneTicks >= MIN_AIRBORNE_TICKS_FOR_CONFIRMATION) {
                onJumpConfirmed();
            } else {
                onJumpRejected();
            }
            return true;
        }

        jumpLandingTicksLeft--;
        if (jumpLandingTicksLeft <= 0) {
            onJumpRejected();
            return true;
        }

        return false;
    }

    private void startJumpResultTracking() {
        awaitingJumpResult = true;
        jumpLiftObserved = false;
        jumpAirborneTicks = 0;
        jumpNoLiftTicksLeft = JUMP_NO_LIFT_TIMEOUT_TICKS;
        jumpLandingTicksLeft = JUMP_LANDING_TIMEOUT_TICKS;
        jumpStartY = input.getPlayerY();
    }

    private void onJumpConfirmed() {
        clearPendingJumpResult();
        confirmedJumpCount++;

        JumpProfile profile = RuntimeState.getProfile();
        successfulJumpStreak++;
        if (adaptivePenaltyTicks > 0 && successfulJumpStreak >= profile.successfulJumpsToReducePenalty()) {
            adaptivePenaltyTicks--;
            successfulJumpStreak = 0;
        }
    }

    private void onJumpRejected() {
        clearPendingJumpResult();
        successfulJumpStreak = 0;
        rejectedJumpCount++;

        JumpProfile profile = RuntimeState.getProfile();
        adaptivePenaltyTicks = Math.min(profile.maxAdaptivePenaltyTicks(), adaptivePenaltyTicks + 1);
    }

    private void clearPendingJumpResult() {
        awaitingJumpResult = false;
        jumpLiftObserved = false;
        jumpAirborneTicks = 0;
        jumpNoLiftTicksLeft = 0;
        jumpLandingTicksLeft = 0;
        jumpStartY = Double.NaN;
    }
    public void resetTransientState() {
        groundedTicks = 0;
        ticksSinceLastJumpAttempt = 0;
        bufferedJumpTicks = 0;
        adaptivePenaltyTicks = 0;
        successfulJumpStreak = 0;
        clearPendingJumpResult();
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

        }
    }
}
