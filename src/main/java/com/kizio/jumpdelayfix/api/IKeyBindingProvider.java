package com.kizio.jumpdelayfix.common.api;
public interface IKeyBindingProvider {
    IKeyBindingProvider NO_OP = new IKeyBindingProvider() {
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
