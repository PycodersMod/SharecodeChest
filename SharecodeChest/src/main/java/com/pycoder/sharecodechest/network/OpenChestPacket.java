package com.pycoder.sharecodechest.network;

import com.pycoder.sharecodechest.service.ChestService;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record OpenChestPacket(String instanceId) {
    public static void encode(OpenChestPacket packet, FriendlyByteBuf buf) { buf.writeUtf(packet.instanceId); }
    public static OpenChestPacket decode(FriendlyByteBuf buf) { return new OpenChestPacket(buf.readUtf()); }
    public static void handle(OpenChestPacket packet, Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();
        if (player != null) {
            ChestService.open(player, packet.instanceId);
            OpenMainScreenPacket.sync(player);
        }
        context.get().setPacketHandled(true);
    }
}
