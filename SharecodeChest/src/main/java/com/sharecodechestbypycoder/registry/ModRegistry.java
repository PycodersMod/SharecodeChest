package com.sharecodechestbypycoder.registry;

import com.sharecodechestbypycoder.SharecodeChestByPycoder;
import com.sharecodechestbypycoder.block.SpecialLootBlock;
import com.sharecodechestbypycoder.block.SpecialLootBlockEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModRegistry {
    private ModRegistry() {
    }

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, SharecodeChestByPycoder.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SharecodeChestByPycoder.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, SharecodeChestByPycoder.MOD_ID);

    public static final RegistryObject<Block> LOOT_CHEST = BLOCKS.register("loot_chest", () -> new SpecialLootBlock(Block.Properties.copy(Blocks.CHEST).strength(55.0F, 3_600_000.0F).noOcclusion(), Blocks.CHEST, false));
    public static final RegistryObject<Block> LOOT_BARREL = BLOCKS.register("loot_barrel", () -> new SpecialLootBlock(Block.Properties.copy(Blocks.BARREL).strength(55.0F, 3_600_000.0F), Blocks.BARREL, false));
    public static final RegistryObject<Block> LOOT_TRAPPED_CHEST = BLOCKS.register("loot_trapped_chest", () -> new SpecialLootBlock(Block.Properties.copy(Blocks.TRAPPED_CHEST).strength(55.0F, 3_600_000.0F).noOcclusion(), Blocks.TRAPPED_CHEST, true));

    public static final RegistryObject<Item> LOOT_CHEST_ITEM = ITEMS.register("loot_chest", () -> new BlockItem(LOOT_CHEST.get(), new Item.Properties()));
    public static final RegistryObject<Item> LOOT_BARREL_ITEM = ITEMS.register("loot_barrel", () -> new BlockItem(LOOT_BARREL.get(), new Item.Properties()));
    public static final RegistryObject<Item> LOOT_TRAPPED_CHEST_ITEM = ITEMS.register("loot_trapped_chest", () -> new BlockItem(LOOT_TRAPPED_CHEST.get(), new Item.Properties()));

    public static final RegistryObject<BlockEntityType<SpecialLootBlockEntity>> SPECIAL_LOOT_BLOCK_ENTITY = BLOCK_ENTITIES.register(
            "special_loot_container",
            () -> BlockEntityType.Builder.of(SpecialLootBlockEntity::new, LOOT_CHEST.get(), LOOT_BARREL.get(), LOOT_TRAPPED_CHEST.get()).build(null)
    );

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
    }
}
