package com.sharecodechestbypycoder.data;

import net.minecraft.nbt.CompoundTag;

public final class InboxRecord {
    private final String instanceId;
    private final String lootTableId;
    private final String displayKey;
    private final String senderUuid;
    private final String senderName;

    public InboxRecord(String instanceId, String lootTableId, String displayKey, String senderUuid, String senderName) {
        this.instanceId = instanceId;
        this.lootTableId = lootTableId;
        this.displayKey = displayKey;
        this.senderUuid = senderUuid;
        this.senderName = senderName;
    }

    public String instanceId() { return instanceId; }
    public String lootTableId() { return lootTableId; }
    public String displayKey() { return displayKey; }
    public String senderUuid() { return senderUuid; }
    public String senderName() { return senderName; }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("InstanceId", instanceId);
        tag.putString("LootTableId", lootTableId);
        tag.putString("DisplayKey", displayKey);
        tag.putString("SenderUuid", senderUuid);
        tag.putString("SenderName", senderName);
        return tag;
    }

    public static InboxRecord load(CompoundTag tag) {
        return new InboxRecord(tag.getString("InstanceId"), tag.getString("LootTableId"), tag.getString("DisplayKey"), tag.getString("SenderUuid"), tag.getString("SenderName"));
    }
}
