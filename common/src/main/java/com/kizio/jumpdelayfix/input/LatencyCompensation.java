package com.kizio.jumpdelayfix.input;

public final class LatencyCompensation {

    public static final int LOCAL_REQUIRED_GROUNDED_TICKS = 1;
    public static final int REMOTE_TICKS_LOW_LATENCY = 2;
    public static final int REMOTE_TICKS_MEDIUM_LATENCY = 3;
    public static final int REMOTE_TICKS_HIGH_LATENCY = 4;
    public static final int REMOTE_TICKS_VERY_HIGH_LATENCY = 5;

    public static final int LOW_LATENCY_MAX_MS = 80;
    public static final int MEDIUM_LATENCY_MAX_MS = 150;
    public static final int HIGH_LATENCY_MAX_MS = 250;
    public static final int UNKNOWN_LATENCY_MS = -1;
    public static final int UNKNOWN_LATENCY_FALLBACK_TICKS = REMOTE_TICKS_MEDIUM_LATENCY;

    private LatencyCompensation() {

    }

    public static int mapLatencyToRequiredTicks(int latencyMs) {
        if (latencyMs < 0) {
            return UNKNOWN_LATENCY_FALLBACK_TICKS;
        }

        if (latencyMs <= LOW_LATENCY_MAX_MS) {
            return REMOTE_TICKS_LOW_LATENCY;
        }
        if (latencyMs <= MEDIUM_LATENCY_MAX_MS) {
            return REMOTE_TICKS_MEDIUM_LATENCY;
        }
        if (latencyMs <= HIGH_LATENCY_MAX_MS) {
            return REMOTE_TICKS_HIGH_LATENCY;
        }

        return REMOTE_TICKS_VERY_HIGH_LATENCY;
    }
}
