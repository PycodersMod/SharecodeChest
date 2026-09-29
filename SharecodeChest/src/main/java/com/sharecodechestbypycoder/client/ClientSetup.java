package com.sharecodechestbypycoder.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.sharecodechestbypycoder.network.ModNetwork;
import com.sharecodechestbypycoder.network.OpenMainScreenPacket;
import com.sharecodechestbypycoder.registry.ModRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ClientSetup {
    public static final KeyMapping OPEN_KEY = new KeyMapping(
            "key.sharecodechestbypycoder.open",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            "key.categories.sharecodechestbypycoder"
    );

    private ClientSetup() {
    }

    public static void init() {
        BlockEntityRenderers.register(ModRegistry.SPECIAL_LOOT_BLOCK_ENTITY.get(), SpecialLootBlockEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(OPEN_KEY);
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static final class ForgeClientEvents {
        private ForgeClientEvents() {
        }

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;
            while (OPEN_KEY.consumeClick()) {
                ModNetwork.CHANNEL.sendToServer(new OpenMainScreenPacket());
            }
        }
    }
}
