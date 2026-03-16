package com.kizio.jumpdelayfix.neoforge;

import com.kizio.jumpdelayfix.common.ModConstants;
import com.kizio.jumpdelayfix.common.bootstrap.CommonBootstrap;
import com.kizio.jumpdelayfix.neoforge.client.NeoForgeClientBootstrap;
import com.kizio.jumpdelayfix.neoforge.datagen.NeoForgeDataGen;
import com.kizio.jumpdelayfix.neoforge.network.NeoForgeNetworking;
import com.kizio.jumpdelayfix.neoforge.registry.NeoForgeRegistries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

import java.util.Objects;

@Mod(ModConstants.MOD_ID)
public final class JumpDelayFixNeoForge {

    public JumpDelayFixNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        IEventBus bus = Objects.requireNonNull(modEventBus, "modEventBus");
        Objects.requireNonNull(modContainer, "modContainer");

        CommonBootstrap.bootstrap();
        NeoForgeRegistries.register(bus);
        NeoForgeNetworking.register(bus);
        NeoForgeDataGen.register(bus);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            NeoForgeClientBootstrap.init(bus);
        }
    }
}
