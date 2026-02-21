package com.kizio.jumpdelayfix.common.api;

public interface JumpInput {
    boolean isJumpPressed();
    boolean isPlayerOnGround();
    void jump();

    default int requiredGroundedTicksBeforeJump() {
        return 1;
    }

    default double getPlayerY() {
        return Double.NaN;
    }

    default int getLatencyMs() {
        return -1;
    }

    default String getServerIdentifier() {
        return "global";
    }
}
