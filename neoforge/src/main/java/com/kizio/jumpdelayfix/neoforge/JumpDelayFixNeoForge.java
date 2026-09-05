package com.kizio.jumpdelayfix.neoforge;

import com.kizio.jumpdelayfix.Constants;
import com.kizio.jumpdelayfix.neoforge.client.NeoForgeClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

import java.util.Objects;

@Mod(Constants.MOD_ID)
public final class JumpDelayFixNeoForge {

    public JumpDelayFixNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        IEventBus bus = Objects.requireNonNull(modEventBus, "modEventBus");
        Objects.requireNonNull(modContainer, "modContainer");

        if (FMLEnvironment.dist == Dist.CLIENT) {
            NeoForgeClient.init(bus);
        }
    }
}
