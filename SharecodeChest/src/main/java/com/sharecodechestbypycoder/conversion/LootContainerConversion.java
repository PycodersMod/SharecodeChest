package com.sharecodechestbypycoder.conversion;

import com.sharecodechestbypycoder.block.SpecialLootBlockEntity;
import com.sharecodechestbypycoder.config.SharecodeConfig;
import com.sharecodechestbypycoder.data.SharecodeSavedData;
import com.sharecodechestbypycoder.registry.ModRegistry;
import com.sharecodechestbypycoder.service.ChestService;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public final class LootContainerConversion {
    private static final int MAX_CHUNKS_PER_TICK = 2;
    private static final Queue<QueuedChunk> QUEUED_CHUNKS = new ArrayDeque<>();
    private static final Set<String> QUEUED_KEYS = new HashSet<>();

    private LootContainerConversion() {
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!SharecodeConfig.CONVERT_LOOT_CONTAINERS.get() || !SharecodeConfig.CONVERT_ALREADY_GENERATED_CHUNKS.get()) return;
        if (!(event.getLevel() instanceof ServerLevel level) || !(event.getChunk() instanceof LevelChunk chunk)) return;
        queue(level, chunk.getPos());
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!SharecodeConfig.CONVERT_LOOT_CONTAINERS.get() || !SharecodeConfig.CONVERT_ALREADY_GENERATED_CHUNKS.get()) return;
        int processed = 0;
        while (processed < MAX_CHUNKS_PER_TICK && !QUEUED_CHUNKS.isEmpty()) {
            QueuedChunk queued = QUEUED_CHUNKS.poll();
            QUEUED_KEYS.remove(queued.key());
            ServerLevel level = event.getServer().getLevel(queued.dimension());
            if (level == null || !level.hasChunk(queued.chunkPos().x, queued.chunkPos().z)) continue;
            convertChunk(level, level.getChunk(queued.chunkPos().x, queued.chunkPos().z));
            processed++;
        }
    }

    private static void queue(ServerLevel level, ChunkPos chunkPos) {
        String key = level.dimension().location() + "|" + chunkPos.x + "|" + chunkPos.z;
        if (!QUEUED_KEYS.add(key)) return;
        QUEUED_CHUNKS.add(new QueuedChunk(level.dimension(), chunkPos, key));
    }

    private static void convertChunk(ServerLevel level, LevelChunk chunk) {
        SharecodeSavedData data = SharecodeSavedData.get(level.getServer());
        for (BlockPos pos : new ArrayList<>(chunk.getBlockEntitiesPos())) {
            BlockEntity blockEntity = chunk.getBlockEntity(pos);
            if (!(blockEntity instanceof RandomizableContainerBlockEntity)) continue;
            ResourceLocation lootTable = readLootTable(blockEntity);
            if (lootTable == null) continue;
            String key = level.dimension().location() + "|" + pos.asLong();
            if (data.converted(key)) continue;
            BlockState oldState = level.getBlockState(pos);
            Block replacement = replacementFor(oldState.getBlock());
            if (replacement == null) continue;
            String instanceId = ChestService.blockInstanceId(level, pos, lootTable);
            discardOldContainer(level, pos, (RandomizableContainerBlockEntity) blockEntity);
            level.setBlock(pos, replacementState(replacement, oldState), Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
            BlockEntity newEntity = level.getBlockEntity(pos);
            if (newEntity instanceof SpecialLootBlockEntity special) {
                special.configure(instanceId, lootTable, ChestService.displayKey(lootTable), "block");
                data.putInstance(instanceId, lootTable.toString());
                data.markConverted(key);
            }
        }
    }

    private static void discardOldContainer(ServerLevel level, BlockPos pos, RandomizableContainerBlockEntity blockEntity) {
        blockEntity.clearContent();
        blockEntity.setChanged();
        level.removeBlockEntity(pos);
    }

    private static ResourceLocation readLootTable(BlockEntity blockEntity) {
        CompoundTag tag = blockEntity.saveWithoutMetadata();
        if (!tag.contains("LootTable")) return null;
        return new ResourceLocation(tag.getString("LootTable"));
    }

    private static Block replacementFor(Block block) {
        if (block == Blocks.CHEST) return SharecodeConfig.CONVERT_CHESTS.get() ? ModRegistry.LOOT_CHEST.get() : null;
        if (block == Blocks.BARREL) return SharecodeConfig.CONVERT_BARRELS.get() ? ModRegistry.LOOT_BARREL.get() : null;
        if (block == Blocks.TRAPPED_CHEST) return SharecodeConfig.CONVERT_TRAPPED_CHESTS.get() ? ModRegistry.LOOT_TRAPPED_CHEST.get() : null;
        return SharecodeConfig.CONVERT_OTHER_RANDOMIZABLE_CONTAINERS.get() ? ModRegistry.LOOT_CHEST.get() : null;
    }

    private static BlockState replacementState(Block replacement, BlockState oldState) {
        BlockState state = replacement.defaultBlockState();
        if (oldState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            state = state.setValue(BlockStateProperties.HORIZONTAL_FACING, oldState.getValue(BlockStateProperties.HORIZONTAL_FACING));
        }
        return state;
    }

    private record QueuedChunk(net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dimension, ChunkPos chunkPos, String key) {
    }
}
