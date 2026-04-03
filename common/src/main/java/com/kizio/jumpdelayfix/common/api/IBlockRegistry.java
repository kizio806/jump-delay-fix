package com.kizio.jumpdelayfix.common.api;

import java.util.function.Supplier;
public interface IBlockRegistry {
    void register(String path, Supplier<?> factory);
}
