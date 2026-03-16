package com.kizio.jumpdelayfix.common.network;

public final class CommonNetworking {

    private static boolean registered;

    private CommonNetworking() {
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
