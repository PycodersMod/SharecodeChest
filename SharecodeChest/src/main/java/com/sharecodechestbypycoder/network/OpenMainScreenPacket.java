package com.sharecodechestbypycoder.network;

import com.sharecodechestbypycoder.data.ChestRecord;
import com.sharecodechestbypycoder.data.InboxRecord;
import com.sharecodechestbypycoder.data.KnownPlayerRecord;
import com.sharecodechestbypycoder.data.SharecodeSavedData;
import com.sharecodechestbypycoder.service.ChestService;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public record OpenMainScreenPacket() {
    public static void encode(OpenMainScreenPacket packet, FriendlyByteBuf buf) {}
    public static OpenMainScreenPacket decode(FriendlyByteBuf buf) { return new OpenMainScreenPacket(); }
    public static void handle(OpenMainScreenPacket packet, Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();
        if (player != null) sync(player);
        context.get().setPacketHandled(true);
    }

    public static void sync(ServerPlayer player) {
        SharecodeSavedData data = SharecodeSavedData.get(player.server);
        List<ChestRecord> chests = data.chests(player.getUUID());
        List<InboxRecord> inbox = data.inbox(player.getUUID());
        List<KnownPlayerRecord> targets = ChestService.shareTargets(player.server, player.getUUID());
        ModNetwork.CHANNEL.sendTo(new SyncDataPacket(chests, inbox, targets, player.server), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
}
