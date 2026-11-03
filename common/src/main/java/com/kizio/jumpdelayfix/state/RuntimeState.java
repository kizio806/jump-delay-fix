package com.kizio.jumpdelayfix.state;

import com.kizio.jumpdelayfix.model.JumpProfile;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public final class RuntimeState {

    private static final AtomicReference<Boolean> ENABLED = new AtomicReference<>(Boolean.TRUE);
    private static final AtomicReference<JumpProfile> PROFILE = new AtomicReference<>(JumpProfile.SMART);

    private RuntimeState() {
    }

    public static boolean isEnabled() {
        return Boolean.TRUE.equals(ENABLED.get());
    }

    public static boolean toggle() {
        return Boolean.TRUE.equals(ENABLED.updateAndGet(current -> !Boolean.TRUE.equals(current)));
    }

    public static void setEnabled(boolean enabled) {
        ENABLED.set(enabled);
    }

    public static JumpProfile getProfile() {
        return PROFILE.get();
    }

    public static JumpProfile cycleProfile() {
        return PROFILE.updateAndGet(JumpProfile::next);
    }

    public static void setProfile(JumpProfile profile) {
        PROFILE.set(Objects.requireNonNull(profile, "profile"));
    }

    public static void reset() {
        ENABLED.set(Boolean.TRUE);
        PROFILE.set(JumpProfile.SMART);
    }
}
