package com.sharecodechestbypycoder.data;

import net.minecraft.nbt.CompoundTag;
import java.util.UUID;

public final class KnownPlayerRecord {
    private final UUID uuid;
    private String name;

    public KnownPlayerRecord(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public UUID uuid() { return uuid; }
    public String name() { return name; }
    public void updateName(String name) { this.name = name; }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("Uuid", uuid);
        tag.putString("Name", name);
        return tag;
    }

    public static KnownPlayerRecord load(CompoundTag tag) {
        return new KnownPlayerRecord(tag.getUUID("Uuid"), tag.getString("Name"));
    }
}
