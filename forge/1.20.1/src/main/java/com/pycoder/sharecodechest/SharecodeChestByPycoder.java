package com.pycoder.sharecodechest;

import com.mojang.logging.LogUtils;
import com.pycoder.sharecodechest.client.ClientSetup;
import com.pycoder.sharecodechest.config.DefaultConfigFile;
import com.pycoder.sharecodechest.config.SharecodeConfig;
import com.pycoder.sharecodechest.conversion.LootContainerConversion;
import com.pycoder.sharecodechest.network.ModNetwork;
import com.pycoder.sharecodechest.registry.ModRegistry;
import com.pycoder.sharecodechest.service.ChestService;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.ModLoadingContext;
import org.slf4j.Logger;

@Mod(SharecodeChestByPycoder.MOD_ID)
public final class SharecodeChestByPycoder {
    public static final String MOD_ID = "sharecodechestbypycoder";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SharecodeChestByPycoder() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SharecodeConfig.SPEC, MOD_ID + "-common.toml");
        DefaultConfigFile.createIfMissing();
        ModRegistry.register(modBus);
        modBus.addListener(this::commonSetup);
        modBus.addListener(this::clientSetup);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(LootContainerConversion.class);
        MinecraftForge.EVENT_BUS.register(ChestService.class);
        LOGGER.info("SharecodeChestByPycoder loaded");
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(ModNetwork::register);
    }

    private void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(ClientSetup::init);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("SharecodeChestByPycoder server data ready");
    }
}
