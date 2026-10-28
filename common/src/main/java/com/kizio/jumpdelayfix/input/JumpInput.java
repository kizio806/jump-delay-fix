package com.kizio.jumpdelayfix.input;

public interface JumpInput {
    boolean isJumpPressed();
    boolean isPlayerOnGround();
    void jump();
    default boolean isPlayerValid() {
        return true;
    }
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
