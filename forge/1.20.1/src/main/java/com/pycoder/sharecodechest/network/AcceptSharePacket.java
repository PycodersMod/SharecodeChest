package com.pycoder.sharecodechest.network;

import com.pycoder.sharecodechest.service.ChestService;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record AcceptSharePacket(String instanceId) {
    public static void encode(AcceptSharePacket packet, FriendlyByteBuf buf) { buf.writeUtf(packet.instanceId); }
    public static AcceptSharePacket decode(FriendlyByteBuf buf) { return new AcceptSharePacket(buf.readUtf()); }
    public static void handle(AcceptSharePacket packet, Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();
        if (player != null) {
            ChestService.accept(player, packet.instanceId);
            OpenMainScreenPacket.sync(player);
        }
        context.get().setPacketHandled(true);
    }
}
