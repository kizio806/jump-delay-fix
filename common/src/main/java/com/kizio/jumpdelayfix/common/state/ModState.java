package com.kizio.jumpdelayfix.common.state;

import com.kizio.jumpdelayfix.common.model.JumpProfile;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Atomic runtime state for enabled flag and selected profile.
 */
public final class ModState {

    private static final AtomicBoolean ENABLED = new AtomicBoolean(true);
    private static final AtomicReference<JumpProfile> PROFILE = new AtomicReference<>(JumpProfile.SMART);

    private ModState() {
    }

    public static boolean isEnabled() {
        return ENABLED.get();
    }

    public static boolean toggle() {
        while (true) {
            boolean current = ENABLED.get();
            boolean next = !current;
            if (ENABLED.compareAndSet(current, next)) {
                return next;
            }
        }
    }

    public static void setEnabled(boolean enabled) {
        ENABLED.set(enabled);
    }

    public static JumpProfile getProfile() {
        return PROFILE.get();
    }

    public static JumpProfile cycleProfile() {
        while (true) {
            JumpProfile current = PROFILE.get();
            JumpProfile next = current.next();
            if (PROFILE.compareAndSet(current, next)) {
                return next;
            }
        }
    }

    public static void setProfile(JumpProfile profile) {
        PROFILE.set(profile);
    }
}
