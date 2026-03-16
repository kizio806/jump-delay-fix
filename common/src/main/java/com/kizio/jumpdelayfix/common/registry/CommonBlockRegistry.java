package com.kizio.jumpdelayfix.common.registry;

public final class CommonBlockRegistry {

    private static boolean registered;

    private CommonBlockRegistry() {
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
