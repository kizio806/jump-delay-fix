package com.kizio.jumpdelayfix.common.event;

public final class CommonEvents {

    private static boolean registered;

    private CommonEvents() {
    }

    public static void register() {
        registered = true;
    }

    public static boolean isRegistered() {
        return registered;
    }

    public static void resetForTests() {
        registered = false;
    }
}
