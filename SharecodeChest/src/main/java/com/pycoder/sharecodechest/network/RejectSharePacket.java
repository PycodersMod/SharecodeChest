package com.pycoder.sharecodechest.network;

import com.pycoder.sharecodechest.service.ChestService;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record RejectSharePacket(String instanceId) {
    public static void encode(RejectSharePacket packet, FriendlyByteBuf buf) { buf.writeUtf(packet.instanceId); }
    public static RejectSharePacket decode(FriendlyByteBuf buf) { return new RejectSharePacket(buf.readUtf()); }
    public static void handle(RejectSharePacket packet, Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();
        if (player != null) {
            ChestService.reject(player, packet.instanceId);
            OpenMainScreenPacket.sync(player);
        }
        context.get().setPacketHandled(true);
    }
}
