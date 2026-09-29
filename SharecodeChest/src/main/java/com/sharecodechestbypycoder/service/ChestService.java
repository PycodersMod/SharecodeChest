package com.sharecodechestbypycoder.service;

import com.sharecodechestbypycoder.block.SpecialLootBlockEntity;
import com.sharecodechestbypycoder.config.SharecodeConfig;
import com.sharecodechestbypycoder.data.ChestRecord;
import com.sharecodechestbypycoder.data.InboxRecord;
import com.sharecodechestbypycoder.data.KnownPlayerRecord;
import com.sharecodechestbypycoder.data.SharecodeSavedData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class ChestService {
    private ChestService() {
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            SharecodeSavedData.get(player.server).rememberPlayer(player.getUUID(), player.getGameProfile().getName());
        }
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!SharecodeConfig.RECORD_LOOT_MINECARTS.get() || event.getLevel().isClientSide()) return;
        if (isLootMinecart(event.getEntity())) {
            event.getEntity().setInvulnerable(true);
        }
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        if (!SharecodeConfig.RECORD_LOOT_MINECARTS.get()) return;
        if (isLootMinecart(event.getTarget())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onExplosionDetonate(ExplosionEvent.Detonate event) {
        if (!SharecodeConfig.RECORD_LOOT_MINECARTS.get() || event.getLevel().isClientSide()) return;
        event.getAffectedEntities().removeIf(ChestService::isLootMinecart);
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!SharecodeConfig.RECORD_LOOT_MINECARTS.get()) return;
        if (!(event.getEntity() instanceof ServerPlayer player) || player.level().isClientSide()) return;
        Entity target = event.getTarget();
        if (!(target instanceof AbstractMinecartContainer minecart)) return;
        ResourceLocation lootTable = lootTable(minecart);
        if (lootTable == null) return;
        minecart.setInvulnerable(true);
        String instanceId = entityInstanceId(player.level(), minecart, lootTable);
        recordVirtualContainer(player, instanceId, lootTable, "entity");
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    public static void recordContainer(ServerPlayer player, Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof SpecialLootBlockEntity special) || special.lootTableId() == null || special.instanceId().isBlank()) {
            subtitle(player, SharecodeConfig.MESSAGE_NO_LOOT_TABLE.get());
            return;
        }
        recordVirtualContainer(player, special.instanceId(), special.lootTableId(), "self");
    }

    private static void recordVirtualContainer(ServerPlayer player, String instanceId, ResourceLocation lootTableId, String sourceType) {
        SharecodeSavedData data = SharecodeSavedData.get(player.server);
        data.putInstance(instanceId, lootTableId.toString());
        Optional<ChestRecord> existing = data.chest(player.getUUID(), instanceId);
        if (existing.isPresent()) {
            ChestRecord record = existing.get();
            if ("shared".equals(record.source())) {
                subtitle(player, SharecodeConfig.MESSAGE_ACCEPTED_SHARE_BEFORE.get());
            } else if (record.opened()) {
                subtitle(player, SharecodeConfig.MESSAGE_ALREADY_OPENED.get());
            } else {
                subtitle(player, SharecodeConfig.MESSAGE_ALREADY_RECORDED.get());
            }
            return;
        }
        data.addChest(player.getUUID(), new ChestRecord(instanceId, lootTableId.toString(), displayKey(lootTableId), false, sourceType, "", ""));
        subtitle(player, SharecodeConfig.MESSAGE_RECORDED.get());
    }

    public static boolean open(ServerPlayer player, String instanceId) {
        SharecodeSavedData data = SharecodeSavedData.get(player.server);
        Optional<ChestRecord> optional = data.chest(player.getUUID(), instanceId);
        if (optional.isEmpty() || optional.get().opened()) {
            message(player, SharecodeConfig.MESSAGE_ALREADY_OPENED.get());
            return false;
        }
        ChestRecord record = optional.get();
        ResourceLocation lootTableId = new ResourceLocation(record.lootTableId());
        LootTable table = player.server.getLootData().getLootTable(lootTableId);
        LootParams params = new LootParams.Builder(player.serverLevel())
                .withParameter(LootContextParams.ORIGIN, player.position())
                .withLuck(player.getLuck())
                .create(LootContextParamSets.CHEST);
        List<ItemStack> generated = table.getRandomItems(params);
        Inventory inventory = player.getInventory();
        for (ItemStack stack : generated) {
            ItemStack remaining = stack.copy();
            if (SharecodeConfig.INSERT_LOOT_DIRECTLY_TO_INVENTORY.get() && !inventory.add(remaining) && SharecodeConfig.DROP_OVERFLOW_ITEMS_AT_PLAYER_FEET.get()) {
                player.drop(remaining, false);
            }
        }
        record.markOpened();
        data.setDirty();
        message(player, SharecodeConfig.MESSAGE_OPENED.get());
        return true;
    }

    public static void share(ServerPlayer sender, String instanceId, Collection<UUID> targets) {
        if (!SharecodeConfig.ENABLE_SHARING.get()) {
            message(sender, SharecodeConfig.MESSAGE_SHARING_DISABLED.get());
            return;
        }
        SharecodeSavedData data = SharecodeSavedData.get(sender.server);
        Optional<ChestRecord> owned = data.chest(sender.getUUID(), instanceId);
        if (owned.isEmpty()) return;
        ChestRecord source = owned.get();
        for (UUID target : targets) {
            if (target.equals(sender.getUUID())) continue;
            ServerPlayer online = sender.server.getPlayerList().getPlayer(target);
            if (online == null && !SharecodeConfig.ALLOW_OFFLINE_SHARE_TARGETS.get()) continue;
            data.addInbox(target, new InboxRecord(source.instanceId(), source.lootTableId(), source.displayKey(), sender.getUUID().toString(), sender.getGameProfile().getName()));
            if (online != null) {
                message(online, SharecodeConfig.MESSAGE_SHARE_RECEIVED.get());
            }
        }
        message(sender, SharecodeConfig.MESSAGE_SHARE_DONE.get());
    }

    public static void accept(ServerPlayer player, String instanceId) {
        SharecodeSavedData data = SharecodeSavedData.get(player.server);
        Optional<InboxRecord> inbox = SharecodeConfig.REMOVE_INBOX_ENTRY_WHEN_ALREADY_RECORDED.get()
                ? data.removeInbox(player.getUUID(), instanceId)
                : data.inbox(player.getUUID()).stream().filter(record -> record.instanceId().equals(instanceId)).findFirst();
        if (inbox.isEmpty()) return;
        InboxRecord record = inbox.get();
        if (data.chest(player.getUUID(), instanceId).isPresent()) {
            message(player, SharecodeConfig.MESSAGE_ALREADY_HAVE_ON_ACCEPT.get());
            return;
        }
        if (!SharecodeConfig.REMOVE_INBOX_ENTRY_WHEN_ALREADY_RECORDED.get()) {
            data.removeInbox(player.getUUID(), instanceId);
        }
        data.addChest(player.getUUID(), new ChestRecord(record.instanceId(), record.lootTableId(), record.displayKey(), false, "shared", record.senderUuid(), record.senderName()));
        message(player, SharecodeConfig.MESSAGE_SHARE_ACCEPTED.get());
    }

    public static void reject(ServerPlayer player, String instanceId) {
        SharecodeSavedData.get(player.server).removeInbox(player.getUUID(), instanceId);
        message(player, SharecodeConfig.MESSAGE_SHARE_REJECTED.get());
    }

    public static List<KnownPlayerRecord> shareTargets(MinecraftServer server, UUID sender) {
        List<KnownPlayerRecord> players = new ArrayList<>(SharecodeSavedData.get(server).knownPlayers());
        players.removeIf(player -> player.uuid().equals(sender));
        if (!SharecodeConfig.ALLOW_OFFLINE_SHARE_TARGETS.get()) {
            players.removeIf(player -> server.getPlayerList().getPlayer(player.uuid()) == null);
        }
        players.sort(Comparator.comparing((KnownPlayerRecord player) -> server.getPlayerList().getPlayer(player.uuid()) == null).thenComparing(KnownPlayerRecord::name, String.CASE_INSENSITIVE_ORDER));
        return players;
    }

    public static String blockInstanceId(Level level, BlockPos pos, ResourceLocation lootTable) {
        String raw = level.dimension().location() + "|" + pos.asLong() + "|" + lootTable;
        return UUID.nameUUIDFromBytes(raw.getBytes(StandardCharsets.UTF_8)).toString();
    }

    public static String entityInstanceId(Level level, Entity entity, ResourceLocation lootTable) {
        String raw = level.dimension().location() + "|" + entity.getUUID() + "|" + lootTable;
        return UUID.nameUUIDFromBytes(raw.getBytes(StandardCharsets.UTF_8)).toString();
    }

    public static String displayKey(ResourceLocation lootTable) {
        return "loot_source.sharecodechestbypycoder." + lootTable.getNamespace() + "." + lootTable.getPath().replace('/', '.');
    }

    private static boolean isLootMinecart(Entity entity) {
        return entity instanceof AbstractMinecartContainer minecart && lootTable(minecart) != null;
    }

    private static ResourceLocation lootTable(AbstractMinecartContainer minecart) {
        ResourceLocation lootTable = minecart.getLootTable();
        if (lootTable != null) return lootTable;
        CompoundTag tag = new CompoundTag();
        minecart.saveWithoutId(tag);
        return tag.contains("LootTable") ? new ResourceLocation(tag.getString("LootTable")) : null;
    }

    private static void subtitle(ServerPlayer player, String key) {
        player.displayClientMessage(Component.translatable(key).withStyle(ChatFormatting.GOLD), true);
    }

    private static void message(ServerPlayer player, String key) {
        player.sendSystemMessage(Component.translatable(key).withStyle(ChatFormatting.GOLD));
    }
}
