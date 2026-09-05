package com.kizio.jumpdelayfix.input;
public interface KeyBindingProvider {
    KeyBindingProvider NO_OP = new KeyBindingProvider() {
        @Override
        public boolean consumeTogglePress() {
            return false;
        }

        @Override
        public boolean consumeProfileCyclePress() {
            return false;
        }

        @Override
        public boolean consumeConfigScreenPress() {
            return false;
        }
    };
    boolean consumeTogglePress();
    boolean consumeProfileCyclePress();
    boolean consumeConfigScreenPress();
}
