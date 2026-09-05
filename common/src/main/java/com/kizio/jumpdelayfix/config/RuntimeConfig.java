package com.kizio.jumpdelayfix.config;

import java.util.Objects;
import java.util.Properties;
public final class RuntimeConfig {

    static final String KEY_AUTO_PROFILE_SWITCH = "autoProfileSwitch";

    private boolean autoProfileSwitch = true;

    public boolean autoProfileSwitch() {
        return autoProfileSwitch;
    }

    public void setAutoProfileSwitch(boolean autoProfileSwitch) {
        this.autoProfileSwitch = autoProfileSwitch;
    }
    public RuntimeConfig copy() {
        RuntimeConfig copy = new RuntimeConfig();
        copy.autoProfileSwitch = autoProfileSwitch;
        return copy;
    }
    public Properties toProperties() {
        Properties properties = new Properties();
        properties.setProperty(KEY_AUTO_PROFILE_SWITCH, Boolean.toString(autoProfileSwitch));
        return properties;
    }
    public static RuntimeConfig fromProperties(Properties properties) {
        Objects.requireNonNull(properties, "properties");

        RuntimeConfig config = defaults();
        String value = properties.getProperty(KEY_AUTO_PROFILE_SWITCH);
        if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
            config.setAutoProfileSwitch(Boolean.parseBoolean(value));
        }
        return config;
    }
    public static RuntimeConfig defaults() {
        return new RuntimeConfig();
    }
}
