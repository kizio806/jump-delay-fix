package com.kizio.jumpdelayfix.common.config;

/**
 * Mutable runtime settings persisted in {@code jumpdelayfix.properties}.
 */
public final class JumpRuntimeConfig {

    private boolean autoProfileSwitch = true;

    public boolean autoProfileSwitch() {
        return autoProfileSwitch;
    }

    public void setAutoProfileSwitch(boolean autoProfileSwitch) {
        this.autoProfileSwitch = autoProfileSwitch;
    }

    /**
     * @return deep copy safe to expose outside runtime internals
     */
    public JumpRuntimeConfig copy() {
        JumpRuntimeConfig copy = new JumpRuntimeConfig();
        copy.autoProfileSwitch = autoProfileSwitch;
        return copy;
    }

    /**
     * @return config populated with default values
     */
    public static JumpRuntimeConfig defaults() {
        return new JumpRuntimeConfig();
    }
}
