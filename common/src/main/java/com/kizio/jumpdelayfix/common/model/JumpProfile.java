package com.kizio.jumpdelayfix.common.model;

public enum JumpProfile {

    COMPETITIVE("message.jumpdelayfix.profile.competitive", -1, 1, 5),
    SMART("message.jumpdelayfix.profile.smart", 0, 3, 3),
    STABLE("message.jumpdelayfix.profile.stable", 1, 5, 2);

    private static final JumpProfile[] VALUES = values();

    private final String translationKey;
    private final int groundedTicksOffset;
    private final int maxAdaptivePenaltyTicks;
    private final int successfulJumpsToReducePenalty;

    JumpProfile(String translationKey, int groundedTicksOffset, int maxAdaptivePenaltyTicks, int successfulJumpsToReducePenalty) {
        this.translationKey = translationKey;
        this.groundedTicksOffset = groundedTicksOffset;
        this.maxAdaptivePenaltyTicks = maxAdaptivePenaltyTicks;
        this.successfulJumpsToReducePenalty = successfulJumpsToReducePenalty;
    }

    public String translationKey() {
        return translationKey;
    }

    public int groundedTicksOffset() {
        return groundedTicksOffset;
    }

    public int maxAdaptivePenaltyTicks() {
        return maxAdaptivePenaltyTicks;
    }

    public int successfulJumpsToReducePenalty() {
        return successfulJumpsToReducePenalty;
    }

    public JumpProfile next() {
        return VALUES[(ordinal() + 1) % VALUES.length];
    }
}
