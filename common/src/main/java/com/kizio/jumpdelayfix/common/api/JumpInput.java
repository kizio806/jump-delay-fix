package com.kizio.jumpdelayfix.common.api;

/**
 * Abstraction over loader-specific client input and movement APIs.
 */
public interface JumpInput {

    /**
     * @return {@code true} when the jump key is currently pressed
     */
    boolean isJumpPressed();

    /**
     * @return {@code true} when the controlled player is on the ground
     */
    boolean isPlayerOnGround();

    /**
     * Triggers a jump action on the local client player.
     */
    void jump();

    /**
     * @return required grounded ticks from the platform perspective before a jump attempt
     */
    default int requiredGroundedTicksBeforeJump() {
        return 1;
    }

    /**
     * @return current player Y coordinate or {@link Double#NaN} when unavailable
     */
    default double getPlayerY() {
        return Double.NaN;
    }

    /**
     * @return measured latency in milliseconds, or negative when unknown
     */
    default int getLatencyMs() {
        return -1;
    }

    /**
     * @return stable server identifier used for per-server profile memory
     */
    default String getServerIdentifier() {
        return "global";
    }
}
