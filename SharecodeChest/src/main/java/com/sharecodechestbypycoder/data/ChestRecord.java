package com.sharecodechestbypycoder.data;

import net.minecraft.nbt.CompoundTag;

public final class ChestRecord {
    private final String instanceId;
    private final String lootTableId;
    private final String displayKey;
    private boolean opened;
    private final String source;
    private final String senderUuid;
    private final String senderName;

    public ChestRecord(String instanceId, String lootTableId, String displayKey, boolean opened, String source, String senderUuid, String senderName) {
        this.instanceId = instanceId;
        this.lootTableId = lootTableId;
        this.displayKey = displayKey;
        this.opened = opened;
        this.source = source;
        this.senderUuid = senderUuid;
        this.senderName = senderName;
    }

    public String instanceId() { return instanceId; }
    public String lootTableId() { return lootTableId; }
    public String displayKey() { return displayKey; }
    public boolean opened() { return opened; }
    public String source() { return source; }
    public String senderUuid() { return senderUuid; }
    public String senderName() { return senderName; }
    public void markOpened() { opened = true; }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("InstanceId", instanceId);
        tag.putString("LootTableId", lootTableId);
        tag.putString("DisplayKey", displayKey);
        tag.putBoolean("Opened", opened);
        tag.putString("Source", source);
        tag.putString("SenderUuid", senderUuid);
        tag.putString("SenderName", senderName);
        return tag;
    }

    public static ChestRecord load(CompoundTag tag) {
        return new ChestRecord(tag.getString("InstanceId"), tag.getString("LootTableId"), tag.getString("DisplayKey"), tag.getBoolean("Opened"), tag.getString("Source"), tag.getString("SenderUuid"), tag.getString("SenderName"));
    }
}
