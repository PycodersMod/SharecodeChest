package com.pycoder.sharecodechest.block;

import com.pycoder.sharecodechest.registry.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SpecialLootBlockEntity extends BlockEntity implements LidBlockEntity {
    private String instanceId = "";
    private ResourceLocation lootTableId;
    private String displayKey = "";
    private String sourceType = "block";

    public SpecialLootBlockEntity(BlockPos pos, BlockState state) {
        super(ModRegistry.SPECIAL_LOOT_BLOCK_ENTITY.get(), pos, state);
    }

    public String instanceId() {
        return instanceId;
    }

    public ResourceLocation lootTableId() {
        return lootTableId;
    }

    public String displayKey() {
        return displayKey.isBlank() && lootTableId != null ? "loot_source.sharecodechestbypycoder." + lootTableId.getNamespace() + "." + lootTableId.getPath().replace('/', '.') : displayKey;
    }

    public String sourceType() {
        return sourceType;
    }

    public void configure(String instanceId, ResourceLocation lootTableId, String displayKey, String sourceType) {
        this.instanceId = instanceId;
        this.lootTableId = lootTableId;
        this.displayKey = displayKey;
        this.sourceType = sourceType;
        setChanged();
    }

    @Override
    public float getOpenNess(float partialTicks) {
        return 0.0F;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("InstanceId", instanceId);
        if (lootTableId != null) {
            tag.putString("LootTableId", lootTableId.toString());
        }
        tag.putString("DisplayKey", displayKey);
        tag.putString("SourceType", sourceType);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        instanceId = tag.getString("InstanceId");
        displayKey = tag.getString("DisplayKey");
        sourceType = tag.getString("SourceType");
        if (tag.contains("LootTableId")) {
            lootTableId = new ResourceLocation(tag.getString("LootTableId"));
        }
    }
}
