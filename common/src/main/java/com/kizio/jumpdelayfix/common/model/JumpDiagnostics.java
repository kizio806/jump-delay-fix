package com.kizio.jumpdelayfix.common.model;

public record JumpDiagnostics(
        String serverId,
        JumpProfile profile,
        boolean enabled,
        boolean autoProfileSwitch,
        boolean shadowMode,
        boolean hudEnabled,
        boolean safetyFailsafe,
        int latencyMs,
        int adaptivePenaltyTicks,
        int requiredGroundedTicks,
        int confirmedJumps,
        int rejectedJumps,
        int shadowJumpPredictions,
        double rollbackRate
) {

    public static JumpDiagnostics empty() {
        return new JumpDiagnostics(
                "n/a",
                JumpProfile.SMART,
                false,
                true,
                false,
                false,
                true,
                -1,
                0,
                1,
                0,
                0,
                0,
                0.0D
        );
    }
}
