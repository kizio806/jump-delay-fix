package com.kizio.jumpdelayfix.neoforge;

import com.kizio.jumpdelayfix.Constants;
import com.kizio.jumpdelayfix.neoforge.client.NeoForgeClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(Constants.MOD_ID)
public final class JumpDelayFixNeoForge {

    public JumpDelayFixNeoForge() {
        this(FMLJavaModLoadingContext.get());
    }

    public JumpDelayFixNeoForge(FMLJavaModLoadingContext context) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            IEventBus bus = (context != null) ? context.getModEventBus() : FMLJavaModLoadingContext.get().getModEventBus();
            NeoForgeClient.init(bus);
        }
    }
}
