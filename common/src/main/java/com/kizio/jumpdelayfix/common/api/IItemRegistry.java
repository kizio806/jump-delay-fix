package com.kizio.jumpdelayfix.common.api;

import java.util.function.Supplier;
public interface IItemRegistry {
    void register(String path, Supplier<?> factory);
}
