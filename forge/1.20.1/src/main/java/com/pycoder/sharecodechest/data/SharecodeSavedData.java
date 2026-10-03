package com.pycoder.sharecodechest.data;

import com.pycoder.sharecodechest.config.SharecodeConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.*;

public class SharecodeSavedData extends SavedData {
    private static final String NAME = "sharecodechestbypycoder";
    private final Map<UUID, List<ChestRecord>> chests = new HashMap<>();
    private final Map<UUID, List<InboxRecord>> inboxes = new HashMap<>();
    private final Map<UUID, KnownPlayerRecord> knownPlayers = new HashMap<>();
    private final Map<String, String> instanceLootTables = new HashMap<>();
    private final Set<String> converted = new HashSet<>();

    public static SharecodeSavedData get(MinecraftServer server) {
        DimensionDataStorage storage = server.overworld().getDataStorage();
        return storage.computeIfAbsent(SharecodeSavedData::load, SharecodeSavedData::new, NAME);
    }

    public static SharecodeSavedData load(CompoundTag tag) {
        SharecodeSavedData data = new SharecodeSavedData();
        ListTag chestOwners = tag.getList("Chests", Tag.TAG_COMPOUND);
        for (Tag ownerTag : chestOwners) {
            CompoundTag owner = (CompoundTag) ownerTag;
            UUID uuid = owner.getUUID("Player");
            List<ChestRecord> list = data.chests.computeIfAbsent(uuid, ignored -> new ArrayList<>());
            for (Tag entryTag : owner.getList("Entries", Tag.TAG_COMPOUND)) {
                list.add(ChestRecord.load((CompoundTag) entryTag));
            }
        }
        ListTag inboxOwners = tag.getList("Inboxes", Tag.TAG_COMPOUND);
        for (Tag ownerTag : inboxOwners) {
            CompoundTag owner = (CompoundTag) ownerTag;
            UUID uuid = owner.getUUID("Player");
            List<InboxRecord> list = data.inboxes.computeIfAbsent(uuid, ignored -> new ArrayList<>());
            for (Tag entryTag : owner.getList("Entries", Tag.TAG_COMPOUND)) {
                list.add(InboxRecord.load((CompoundTag) entryTag));
            }
        }
        for (Tag playerTag : tag.getList("KnownPlayers", Tag.TAG_COMPOUND)) {
            KnownPlayerRecord player = KnownPlayerRecord.load((CompoundTag) playerTag);
            data.knownPlayers.put(player.uuid(), player);
        }
        ListTag instances = tag.getList("Instances", Tag.TAG_COMPOUND);
        for (Tag instanceTag : instances) {
            CompoundTag instance = (CompoundTag) instanceTag;
            data.instanceLootTables.put(instance.getString("InstanceId"), instance.getString("LootTableId"));
        }
        for (Tag convertedTag : tag.getList("Converted", Tag.TAG_STRING)) {
            data.converted.add(convertedTag.getAsString());
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag chestOwners = new ListTag();
        chests.forEach((uuid, records) -> {
            CompoundTag owner = new CompoundTag();
            owner.putUUID("Player", uuid);
            ListTag entries = new ListTag();
            records.forEach(record -> entries.add(record.save()));
            owner.put("Entries", entries);
            chestOwners.add(owner);
        });
        tag.put("Chests", chestOwners);

        ListTag inboxOwners = new ListTag();
        inboxes.forEach((uuid, records) -> {
            CompoundTag owner = new CompoundTag();
            owner.putUUID("Player", uuid);
            ListTag entries = new ListTag();
            records.forEach(record -> entries.add(record.save()));
            owner.put("Entries", entries);
            inboxOwners.add(owner);
        });
        tag.put("Inboxes", inboxOwners);

        ListTag players = new ListTag();
        knownPlayers.values().forEach(player -> players.add(player.save()));
        tag.put("KnownPlayers", players);

        ListTag instances = new ListTag();
        instanceLootTables.forEach((instanceId, lootTableId) -> {
            CompoundTag instance = new CompoundTag();
            instance.putString("InstanceId", instanceId);
            instance.putString("LootTableId", lootTableId);
            instances.add(instance);
        });
        tag.put("Instances", instances);

        ListTag convertedList = new ListTag();
        converted.forEach(value -> convertedList.add(net.minecraft.nbt.StringTag.valueOf(value)));
        tag.put("Converted", convertedList);
        return tag;
    }

    public void rememberPlayer(UUID uuid, String name) {
        knownPlayers.compute(uuid, (ignored, existing) -> {
            if (existing == null) return new KnownPlayerRecord(uuid, name);
            existing.updateName(name);
            return existing;
        });
        setDirty();
    }

    public Collection<KnownPlayerRecord> knownPlayers() { return knownPlayers.values(); }
    public List<ChestRecord> chests(UUID uuid) { return chests.computeIfAbsent(uuid, ignored -> new ArrayList<>()); }
    public List<InboxRecord> inbox(UUID uuid) { return inboxes.computeIfAbsent(uuid, ignored -> new ArrayList<>()); }
    public Optional<ChestRecord> chest(UUID uuid, String instanceId) { return chests(uuid).stream().filter(record -> record.instanceId().equals(instanceId)).findFirst(); }
    public boolean hasInbox(UUID uuid, String instanceId) { return inbox(uuid).stream().anyMatch(record -> record.instanceId().equals(instanceId)); }

    public boolean addChest(UUID uuid, ChestRecord record) {
        if (chest(uuid, record.instanceId()).isPresent()) return false;
        chests(uuid).add(record);
        setDirty();
        return true;
    }

    public boolean addInbox(UUID uuid, InboxRecord record) {
        if (SharecodeConfig.SILENTLY_SKIP_DUPLICATE_INBOX_ENTRIES.get() && hasInbox(uuid, record.instanceId())) return false;
        inbox(uuid).add(record);
        setDirty();
        return true;
    }

    public Optional<InboxRecord> removeInbox(UUID uuid, String instanceId) {
        Iterator<InboxRecord> iterator = inbox(uuid).iterator();
        while (iterator.hasNext()) {
            InboxRecord record = iterator.next();
            if (record.instanceId().equals(instanceId)) {
                iterator.remove();
                setDirty();
                return Optional.of(record);
            }
        }
        return Optional.empty();
    }

    public void putInstance(String instanceId, String lootTableId) {
        instanceLootTables.put(instanceId, lootTableId);
        setDirty();
    }

    public Optional<String> lootTable(String instanceId) { return Optional.ofNullable(instanceLootTables.get(instanceId)); }
    public boolean converted(String key) { return converted.contains(key); }
    public void markConverted(String key) { converted.add(key); setDirty(); }
}
