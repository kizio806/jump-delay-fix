package com.kizio.jumpdelayfix.model;

public final class ServerAdaptiveStats {

    private static final double RATE_EPSILON = 1.0E-6D;
    private static final double SMOOTHING_FACTOR_OLD = 0.82D;
    private static final double SMOOTHING_FACTOR_NEW = 0.18D;
    private static final double DECAY_FACTOR = 0.998D;

    private int confirmedJumps;
    private int rejectedJumps;
    private double smoothedRollbackRate;

    public void update(int confirmedDelta, int rejectedDelta) {
        int validConfirmed = Math.max(0, confirmedDelta);
        int validRejected = Math.max(0, rejectedDelta);

        confirmedJumps += validConfirmed;
        rejectedJumps += validRejected;

        int sampleSize = validConfirmed + validRejected;
        if (sampleSize > 0) {
            double batchRollbackRate = (double) validRejected / sampleSize;
            smoothedRollbackRate = (smoothedRollbackRate * SMOOTHING_FACTOR_OLD) + (batchRollbackRate * SMOOTHING_FACTOR_NEW);
        } else if (smoothedRollbackRate > 0.0D) {
            smoothedRollbackRate *= DECAY_FACTOR;
            if (smoothedRollbackRate < RATE_EPSILON) {
                smoothedRollbackRate = 0.0D;
            }
        }
    }

    public int confirmedJumps() {
        return confirmedJumps;
    }

    public int rejectedJumps() {
        return rejectedJumps;
    }

    public double rollbackRate() {
        return smoothedRollbackRate;
    }
}
