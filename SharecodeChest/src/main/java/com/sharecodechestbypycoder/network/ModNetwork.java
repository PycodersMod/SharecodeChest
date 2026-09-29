package com.sharecodechestbypycoder.network;

import com.sharecodechestbypycoder.SharecodeChestByPycoder;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class ModNetwork {
    private static final String VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(SharecodeChestByPycoder.MOD_ID, "main"),
            () -> VERSION,
            VERSION::equals,
            VERSION::equals
    );
    private static int id;

    private ModNetwork() {
    }

    public static void register() {
        CHANNEL.messageBuilder(OpenMainScreenPacket.class, id++).encoder(OpenMainScreenPacket::encode).decoder(OpenMainScreenPacket::decode).consumerMainThread(OpenMainScreenPacket::handle).add();
        CHANNEL.messageBuilder(SyncDataPacket.class, id++).encoder(SyncDataPacket::encode).decoder(SyncDataPacket::decode).consumerMainThread(SyncDataPacket::handle).add();
        CHANNEL.messageBuilder(OpenChestPacket.class, id++).encoder(OpenChestPacket::encode).decoder(OpenChestPacket::decode).consumerMainThread(OpenChestPacket::handle).add();
        CHANNEL.messageBuilder(AcceptSharePacket.class, id++).encoder(AcceptSharePacket::encode).decoder(AcceptSharePacket::decode).consumerMainThread(AcceptSharePacket::handle).add();
        CHANNEL.messageBuilder(RejectSharePacket.class, id++).encoder(RejectSharePacket::encode).decoder(RejectSharePacket::decode).consumerMainThread(RejectSharePacket::handle).add();
        CHANNEL.messageBuilder(SharePacket.class, id++).encoder(SharePacket::encode).decoder(SharePacket::decode).consumerMainThread(SharePacket::handle).add();
    }
}
