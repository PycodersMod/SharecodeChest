package com.sharecodechestbypycoder.network;

import com.sharecodechestbypycoder.client.ClientPacketHandler;
import com.sharecodechestbypycoder.data.ChestRecord;
import com.sharecodechestbypycoder.data.InboxRecord;
import com.sharecodechestbypycoder.data.KnownPlayerRecord;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public record SyncDataPacket(List<Entry> chests, List<Entry> inbox, List<PlayerEntry> players) {
    public SyncDataPacket(List<ChestRecord> chests, List<InboxRecord> inbox, List<KnownPlayerRecord> players, MinecraftServer server) {
        this(chests.stream().map(Entry::fromChest).toList(), inbox.stream().map(Entry::fromInbox).toList(), players.stream().map(player -> new PlayerEntry(player.uuid(), player.name(), server.getPlayerList().getPlayer(player.uuid()) != null)).toList());
    }

    public static void encode(SyncDataPacket packet, FriendlyByteBuf buf) {
        buf.writeCollection(packet.chests, Entry::write);
        buf.writeCollection(packet.inbox, Entry::write);
        buf.writeCollection(packet.players, PlayerEntry::write);
    }

    public static SyncDataPacket decode(FriendlyByteBuf buf) {
        return new SyncDataPacket(buf.readList(Entry::read), buf.readList(Entry::read), buf.readList(PlayerEntry::read));
    }

    public static void handle(SyncDataPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> ClientPacketHandler.openMain(packet));
        context.get().setPacketHandled(true);
    }

    public record Entry(String instanceId, String lootTableId, String displayKey, boolean opened, String senderName) {
        static Entry fromChest(ChestRecord record) { return new Entry(record.instanceId(), record.lootTableId(), record.displayKey(), record.opened(), record.senderName()); }
        static Entry fromInbox(InboxRecord record) { return new Entry(record.instanceId(), record.lootTableId(), record.displayKey(), false, record.senderName()); }
        static void write(FriendlyByteBuf buf, Entry entry) { buf.writeUtf(entry.instanceId); buf.writeUtf(entry.lootTableId); buf.writeUtf(entry.displayKey); buf.writeBoolean(entry.opened); buf.writeUtf(entry.senderName); }
        static Entry read(FriendlyByteBuf buf) { return new Entry(buf.readUtf(), buf.readUtf(), buf.readUtf(), buf.readBoolean(), buf.readUtf()); }
    }

    public record PlayerEntry(UUID uuid, String name, boolean online) {
        static void write(FriendlyByteBuf buf, PlayerEntry entry) { buf.writeUUID(entry.uuid); buf.writeUtf(entry.name); buf.writeBoolean(entry.online); }
        static PlayerEntry read(FriendlyByteBuf buf) { return new PlayerEntry(buf.readUUID(), buf.readUtf(), buf.readBoolean()); }
    }
}
