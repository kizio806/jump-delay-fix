package com.kizio.jumpdelayfix.model;

public final class ServerAdaptiveStats {

    private static final double RATE_EPSILON = 1.0E-6D;

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
            smoothedRollbackRate = (smoothedRollbackRate * 0.82D) + (batchRollbackRate * 0.18D);
        } else {
            smoothedRollbackRate *= 0.998D;
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
