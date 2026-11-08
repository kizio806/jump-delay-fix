package com.kizio.jumpdelayfix.state;

import com.kizio.jumpdelayfix.model.JumpProfile;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public final class RuntimeState {

    private static final RuntimeState DEFAULT_INSTANCE = new RuntimeState();

    private final AtomicReference<Boolean> enabled;
    private final AtomicReference<JumpProfile> profile;

    public RuntimeState() {
        this(true, JumpProfile.SMART);
    }

    public RuntimeState(boolean initialEnabled, JumpProfile initialProfile) {
        this.enabled = new AtomicReference<>(initialEnabled);
        this.profile = new AtomicReference<>(Objects.requireNonNull(initialProfile, "initialProfile"));
    }

    public static RuntimeState defaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public boolean isStateEnabled() {
        return Boolean.TRUE.equals(enabled.get());
    }

    public boolean toggleState() {
        return Boolean.TRUE.equals(enabled.updateAndGet(current -> !Boolean.TRUE.equals(current)));
    }

    public void setStateEnabled(boolean isEnabled) {
        enabled.set(isEnabled);
    }

    public JumpProfile stateProfile() {
        return profile.get();
    }

    public JumpProfile cycleStateProfile() {
        return profile.updateAndGet(JumpProfile::next);
    }

    public void setStateProfile(JumpProfile newProfile) {
        profile.set(Objects.requireNonNull(newProfile, "profile"));
    }

    public void resetState() {
        enabled.set(Boolean.TRUE);
        profile.set(JumpProfile.SMART);
    }

    public static boolean isEnabled() {
        return DEFAULT_INSTANCE.isStateEnabled();
    }

    public static boolean toggle() {
        return DEFAULT_INSTANCE.toggleState();
    }

    public static void setEnabled(boolean enabled) {
        DEFAULT_INSTANCE.setStateEnabled(enabled);
    }

    public static JumpProfile getProfile() {
        return DEFAULT_INSTANCE.stateProfile();
    }

    public static JumpProfile cycleProfile() {
        return DEFAULT_INSTANCE.cycleStateProfile();
    }

    public static void setProfile(JumpProfile profile) {
        DEFAULT_INSTANCE.setStateProfile(profile);
    }

    public static void reset() {
        DEFAULT_INSTANCE.resetState();
    }
}
