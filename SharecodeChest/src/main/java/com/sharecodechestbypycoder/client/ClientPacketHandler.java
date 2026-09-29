package com.sharecodechestbypycoder.client;

import com.sharecodechestbypycoder.network.SyncDataPacket;
import net.minecraft.client.Minecraft;

public final class ClientPacketHandler {
    private ClientPacketHandler() {
    }

    public static void openMain(SyncDataPacket packet) {
        Minecraft.getInstance().setScreen(new SharecodeChestScreen(packet));
    }
}
