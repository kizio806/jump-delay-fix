package com.kizio.jumpdelayfix.common.config;

/**
 * Mutable runtime settings persisted in {@code jumpdelayfix.properties}.
 * <p>
 * All setters apply defensive clamping for out-of-range values so corrupted
 * user input cannot put the mod into invalid state.
 */
public final class JumpRuntimeConfig {

    private static final int MIN_ATTEMPTS = 2;
    private static final int HUD_POSITION_LIMIT = 10_000;
    private static final double HUD_SCALE_MIN = 0.60D;
    private static final double HUD_SCALE_MAX = 2.20D;

    private boolean autoProfileSwitch = true;
    private boolean hudEnabled;
    private boolean shadowMode;
    private boolean safetyFailsafe = true;

    private int hudOffsetX = 6;
    private int hudOffsetY = 6;
    private double hudScale = 1.0D;

    private boolean hudShowProfileAndPing = true;
    private boolean hudShowRollbackAndPenalty = true;
    private boolean hudShowModeAndQuality = true;
    private boolean hudShowServer = true;
    private boolean hudShowQualityBar = true;

    private int minAttemptsForProfileSwitch = 8;

    private double competitiveRollbackRateMax = 0.08D;
    private double stableRollbackRateMin = 0.30D;
    private double failsafeRollbackRate = 0.45D;

    public boolean autoProfileSwitch() {
        return autoProfileSwitch;
    }

    public void setAutoProfileSwitch(boolean autoProfileSwitch) {
        this.autoProfileSwitch = autoProfileSwitch;
    }

    public boolean hudEnabled() {
        return hudEnabled;
    }

    public void setHudEnabled(boolean hudEnabled) {
        this.hudEnabled = hudEnabled;
    }

    public boolean shadowMode() {
        return shadowMode;
    }

    public void setShadowMode(boolean shadowMode) {
        this.shadowMode = shadowMode;
    }

    public boolean safetyFailsafe() {
        return safetyFailsafe;
    }

    public void setSafetyFailsafe(boolean safetyFailsafe) {
        this.safetyFailsafe = safetyFailsafe;
    }

    public int hudOffsetX() {
        return hudOffsetX;
    }

    public void setHudOffsetX(int hudOffsetX) {
        this.hudOffsetX = clampHudOffset(hudOffsetX);
    }

    public int hudOffsetY() {
        return hudOffsetY;
    }

    public void setHudOffsetY(int hudOffsetY) {
        this.hudOffsetY = clampHudOffset(hudOffsetY);
    }

    public double hudScale() {
        return hudScale;
    }

    public void setHudScale(double hudScale) {
        this.hudScale = clampHudScale(hudScale);
    }

    public boolean hudShowProfileAndPing() {
        return hudShowProfileAndPing;
    }

    public void setHudShowProfileAndPing(boolean hudShowProfileAndPing) {
        this.hudShowProfileAndPing = hudShowProfileAndPing;
    }

    public boolean hudShowRollbackAndPenalty() {
        return hudShowRollbackAndPenalty;
    }

    public void setHudShowRollbackAndPenalty(boolean hudShowRollbackAndPenalty) {
        this.hudShowRollbackAndPenalty = hudShowRollbackAndPenalty;
    }

    public boolean hudShowModeAndQuality() {
        return hudShowModeAndQuality;
    }

    public void setHudShowModeAndQuality(boolean hudShowModeAndQuality) {
        this.hudShowModeAndQuality = hudShowModeAndQuality;
    }

    public boolean hudShowServer() {
        return hudShowServer;
    }

    public void setHudShowServer(boolean hudShowServer) {
        this.hudShowServer = hudShowServer;
    }

    public boolean hudShowQualityBar() {
        return hudShowQualityBar;
    }

    public void setHudShowQualityBar(boolean hudShowQualityBar) {
        this.hudShowQualityBar = hudShowQualityBar;
    }

    public int minAttemptsForProfileSwitch() {
        return minAttemptsForProfileSwitch;
    }

    public void setMinAttemptsForProfileSwitch(int minAttemptsForProfileSwitch) {
        this.minAttemptsForProfileSwitch = Math.max(MIN_ATTEMPTS, minAttemptsForProfileSwitch);
    }

    public double competitiveRollbackRateMax() {
        return competitiveRollbackRateMax;
    }

    public void setCompetitiveRollbackRateMax(double competitiveRollbackRateMax) {
        this.competitiveRollbackRateMax = clamp01(competitiveRollbackRateMax);
    }

    public double stableRollbackRateMin() {
        return stableRollbackRateMin;
    }

    public void setStableRollbackRateMin(double stableRollbackRateMin) {
        this.stableRollbackRateMin = clamp01(stableRollbackRateMin);
    }

    public double failsafeRollbackRate() {
        return failsafeRollbackRate;
    }

    public void setFailsafeRollbackRate(double failsafeRollbackRate) {
        this.failsafeRollbackRate = clamp01(failsafeRollbackRate);
    }

    /**
     * @return deep copy safe to expose to UI code
     */
    public JumpRuntimeConfig copy() {
        JumpRuntimeConfig copy = new JumpRuntimeConfig();
        copy.autoProfileSwitch = autoProfileSwitch;
        copy.hudEnabled = hudEnabled;
        copy.shadowMode = shadowMode;
        copy.safetyFailsafe = safetyFailsafe;
        copy.hudOffsetX = hudOffsetX;
        copy.hudOffsetY = hudOffsetY;
        copy.hudScale = hudScale;
        copy.hudShowProfileAndPing = hudShowProfileAndPing;
        copy.hudShowRollbackAndPenalty = hudShowRollbackAndPenalty;
        copy.hudShowModeAndQuality = hudShowModeAndQuality;
        copy.hudShowServer = hudShowServer;
        copy.hudShowQualityBar = hudShowQualityBar;
        copy.minAttemptsForProfileSwitch = minAttemptsForProfileSwitch;
        copy.competitiveRollbackRateMax = competitiveRollbackRateMax;
        copy.stableRollbackRateMin = stableRollbackRateMin;
        copy.failsafeRollbackRate = failsafeRollbackRate;
        return copy;
    }

    /**
     * @return config populated with default values
     */
    public static JumpRuntimeConfig defaults() {
        return new JumpRuntimeConfig();
    }

    private static double clamp01(double value) {
        return Math.max(0.0D, Math.min(1.0D, value));
    }

    private static int clampHudOffset(int value) {
        return Math.max(-HUD_POSITION_LIMIT, Math.min(HUD_POSITION_LIMIT, value));
    }

    private static double clampHudScale(double value) {
        return Math.max(HUD_SCALE_MIN, Math.min(HUD_SCALE_MAX, value));
    }
}
