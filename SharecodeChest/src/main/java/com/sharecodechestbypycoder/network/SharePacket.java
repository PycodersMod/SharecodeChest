package com.sharecodechestbypycoder.network;

import com.sharecodechestbypycoder.service.ChestService;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public record SharePacket(String instanceId, List<UUID> targets) {
    public static void encode(SharePacket packet, FriendlyByteBuf buf) { buf.writeUtf(packet.instanceId); buf.writeCollection(packet.targets, FriendlyByteBuf::writeUUID); }
    public static SharePacket decode(FriendlyByteBuf buf) { return new SharePacket(buf.readUtf(), buf.readList(FriendlyByteBuf::readUUID)); }
    public static void handle(SharePacket packet, Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();
        if (player != null) {
            ChestService.share(player, packet.instanceId, packet.targets);
            OpenMainScreenPacket.sync(player);
        }
        context.get().setPacketHandled(true);
    }
}
