package com.kizio.jumpdelayfix.common.model;

/**
 * Rolling per-server counters used for auto profile adaptation.
 */
public final class ServerAdaptiveStats {

    private int confirmedJumps;
    private int rejectedJumps;
    private double smoothedRollbackRate;

    /**
     * Updates counters and exponentially-smoothed rollback rate.
     *
     * @param confirmedDelta newly observed confirmed jumps
     * @param rejectedDelta  newly observed rejected jumps
     */
    public void update(int confirmedDelta, int rejectedDelta) {
        confirmedJumps += Math.max(0, confirmedDelta);
        rejectedJumps += Math.max(0, rejectedDelta);

        int sampleSize = Math.max(0, confirmedDelta) + Math.max(0, rejectedDelta);
        if (sampleSize > 0) {
            double batchRollbackRate = (double) Math.max(0, rejectedDelta) / sampleSize;
            smoothedRollbackRate = (smoothedRollbackRate * 0.82D) + (batchRollbackRate * 0.18D);
        } else {
            smoothedRollbackRate *= 0.998D;
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
