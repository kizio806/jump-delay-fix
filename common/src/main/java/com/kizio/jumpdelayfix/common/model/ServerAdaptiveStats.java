package com.kizio.jumpdelayfix.common.model;

public final class ServerAdaptiveStats {

    private int confirmedJumps;
    private int rejectedJumps;
    private int shadowPredictions;
    private double smoothedRollbackRate;

    public void update(int confirmedDelta, int rejectedDelta, int shadowDelta) {
        confirmedJumps += Math.max(0, confirmedDelta);
        rejectedJumps += Math.max(0, rejectedDelta);
        shadowPredictions += Math.max(0, shadowDelta);

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

    public int shadowPredictions() {
        return shadowPredictions;
    }

    public double rollbackRate() {
        return smoothedRollbackRate;
    }
}
