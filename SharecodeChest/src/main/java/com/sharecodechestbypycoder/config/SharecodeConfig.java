package com.sharecodechestbypycoder.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class SharecodeConfig {
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue CONVERT_LOOT_CONTAINERS;
    public static final ForgeConfigSpec.BooleanValue CONVERT_ALREADY_GENERATED_CHUNKS;
    public static final ForgeConfigSpec.BooleanValue CONVERT_CHESTS;
    public static final ForgeConfigSpec.BooleanValue CONVERT_BARRELS;
    public static final ForgeConfigSpec.BooleanValue CONVERT_TRAPPED_CHESTS;
    public static final ForgeConfigSpec.BooleanValue CONVERT_OTHER_RANDOMIZABLE_CONTAINERS;
    public static final ForgeConfigSpec.BooleanValue RECORD_LOOT_MINECARTS;

    public static final ForgeConfigSpec.BooleanValue ENABLE_SHARING;
    public static final ForgeConfigSpec.BooleanValue ALLOW_OFFLINE_SHARE_TARGETS;
    public static final ForgeConfigSpec.BooleanValue SILENTLY_SKIP_DUPLICATE_INBOX_ENTRIES;
    public static final ForgeConfigSpec.BooleanValue REMOVE_INBOX_ENTRY_WHEN_ALREADY_RECORDED;

    public static final ForgeConfigSpec.BooleanValue GENERATE_LOOT_ON_GUI_OPEN;
    public static final ForgeConfigSpec.BooleanValue INSERT_LOOT_DIRECTLY_TO_INVENTORY;
    public static final ForgeConfigSpec.BooleanValue DROP_OVERFLOW_ITEMS_AT_PLAYER_FEET;
    public static final ForgeConfigSpec.BooleanValue EACH_PLAYER_ROLLS_INDEPENDENTLY;

    public static final ForgeConfigSpec.IntValue MAIN_SCREEN_VISIBLE_ROWS;
    public static final ForgeConfigSpec.IntValue SHARE_SCREEN_VISIBLE_ROWS;
    public static final ForgeConfigSpec.BooleanValue ENABLE_SHARE_SEARCH_BOX;
    public static final ForgeConfigSpec.BooleanValue SHOW_ONLINE_STATUS_IN_SHARE_SCREEN;
    public static final ForgeConfigSpec.BooleanValue ESC_RETURNS_FROM_SHARE_SCREEN;

    public static final ForgeConfigSpec.ConfigValue<String> MESSAGE_RECORDED;
    public static final ForgeConfigSpec.ConfigValue<String> MESSAGE_ACCEPTED_SHARE_BEFORE;
    public static final ForgeConfigSpec.ConfigValue<String> MESSAGE_ALREADY_OPENED;
    public static final ForgeConfigSpec.ConfigValue<String> MESSAGE_ALREADY_RECORDED;
    public static final ForgeConfigSpec.ConfigValue<String> MESSAGE_NO_LOOT_TABLE;
    public static final ForgeConfigSpec.ConfigValue<String> MESSAGE_OPENED;
    public static final ForgeConfigSpec.ConfigValue<String> MESSAGE_SHARE_DONE;
    public static final ForgeConfigSpec.ConfigValue<String> MESSAGE_SHARE_RECEIVED;
    public static final ForgeConfigSpec.ConfigValue<String> MESSAGE_ALREADY_HAVE_ON_ACCEPT;
    public static final ForgeConfigSpec.ConfigValue<String> MESSAGE_SHARE_ACCEPTED;
    public static final ForgeConfigSpec.ConfigValue<String> MESSAGE_SHARE_REJECTED;
    public static final ForgeConfigSpec.ConfigValue<String> MESSAGE_SHARING_DISABLED;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("conversion");
        CONVERT_LOOT_CONTAINERS = builder.comment("Master switch for replacing loot-table containers with SharecodeChest containers during chunk load.")
                .define("convertLootContainers", true);
        CONVERT_ALREADY_GENERATED_CHUNKS = builder.comment("If true, old chunks loaded after the mod is installed are scanned and converted once.")
                .define("convertAlreadyGeneratedChunks", true);
        CONVERT_CHESTS = builder.comment("If true, vanilla chest block entities with loot tables are converted to SharecodeChest loot chests.")
                .define("convertChests", true);
        CONVERT_BARRELS = builder.comment("If true, vanilla barrel block entities with loot tables are converted to SharecodeChest loot barrels.")
                .define("convertBarrels", true);
        CONVERT_TRAPPED_CHESTS = builder.comment("If true, vanilla trapped chest block entities with loot tables are converted to SharecodeChest trapped loot chests.")
                .define("convertTrappedChests", true);
        CONVERT_OTHER_RANDOMIZABLE_CONTAINERS = builder.comment("If true, non-vanilla RandomizableContainerBlockEntity blocks with loot tables are converted to the generic SharecodeChest loot chest.")
                .define("convertOtherRandomizableContainers", true);
        RECORD_LOOT_MINECARTS = builder.comment("If true, loot-table minecart containers are recorded on right click instead of opening the vanilla container.")
                .define("recordLootMinecarts", true);
        builder.pop();

        builder.push("sharing");
        ENABLE_SHARING = builder.comment("Master switch for the share button and server-side share action.")
                .define("enableSharing", true);
        ALLOW_OFFLINE_SHARE_TARGETS = builder.comment("If true, players who have joined before can be selected as share targets even while offline.")
                .define("allowOfflineShareTargets", true);
        SILENTLY_SKIP_DUPLICATE_INBOX_ENTRIES = builder.comment("If true, sharing a chest already present in the target inbox does nothing instead of adding a duplicate inbox row.")
                .define("silentlySkipDuplicateInboxEntries", true);
        REMOVE_INBOX_ENTRY_WHEN_ALREADY_RECORDED = builder.comment("If true, accepting an inbox entry for a chest already in the player's chest list removes that inbox entry.")
                .define("removeInboxEntryWhenAlreadyRecorded", true);
        builder.pop();

        builder.push("loot");
        GENERATE_LOOT_ON_GUI_OPEN = builder.comment("If true, loot is generated only when the player presses Open in the GUI, not when recording or accepting a share.")
                .define("generateLootOnGuiOpen", true);
        INSERT_LOOT_DIRECTLY_TO_INVENTORY = builder.comment("If true, generated loot is inserted directly into the player's inventory.")
                .define("insertLootDirectlyToInventory", true);
        DROP_OVERFLOW_ITEMS_AT_PLAYER_FEET = builder.comment("If true, items that do not fit in the inventory are dropped at the player's feet.")
                .define("dropOverflowItemsAtPlayerFeet", true);
        EACH_PLAYER_ROLLS_INDEPENDENTLY = builder.comment("If true, each player can roll the same recorded chest instance once independently, similar to Lootr.")
                .define("eachPlayerRollsIndependently", true);
        builder.pop();

        builder.push("gui");
        MAIN_SCREEN_VISIBLE_ROWS = builder.comment("Number of chest or inbox rows shown at once on the main screen.")
                .defineInRange("mainScreenVisibleRows", 7, 3, 12);
        SHARE_SCREEN_VISIBLE_ROWS = builder.comment("Number of player rows shown at once on the share screen.")
                .defineInRange("shareScreenVisibleRows", 8, 3, 14);
        ENABLE_SHARE_SEARCH_BOX = builder.comment("If true, the share screen displays a name search box above the player list.")
                .define("enableShareSearchBox", true);
        SHOW_ONLINE_STATUS_IN_SHARE_SCREEN = builder.comment("If true, the share screen appends online/offline status text after each player name.")
                .define("showOnlineStatusInShareScreen", true);
        ESC_RETURNS_FROM_SHARE_SCREEN = builder.comment("If true, pressing ESC on the share screen returns to the main chest screen instead of closing the GUI.")
                .define("escReturnsFromShareScreen", true);
        builder.pop();

        builder.push("messages");
        MESSAGE_RECORDED = builder.comment("Translation key shown in the action bar when a container is recorded for the first time.")
                .define("recorded", "message.sharecodechestbypycoder.recorded");
        MESSAGE_ACCEPTED_SHARE_BEFORE = builder.comment("Translation key shown when the player right-clicks a world container that was already accepted from a share.")
                .define("acceptedShareBefore", "message.sharecodechestbypycoder.accepted_share_before");
        MESSAGE_ALREADY_OPENED = builder.comment("Translation key shown when the player tries to record or open a chest instance that is already opened.")
                .define("alreadyOpened", "message.sharecodechestbypycoder.already_opened");
        MESSAGE_ALREADY_RECORDED = builder.comment("Translation key shown when the player right-clicks a container that is already recorded but not opened.")
                .define("alreadyRecorded", "message.sharecodechestbypycoder.already_recorded");
        MESSAGE_NO_LOOT_TABLE = builder.comment("Translation key shown when a special container has no bound loot table, such as a creative-placed block.")
                .define("noLootTable", "message.sharecodechestbypycoder.no_loot_table");
        MESSAGE_OPENED = builder.comment("Translation key shown after the server successfully generates and grants loot.")
                .define("opened", "message.sharecodechestbypycoder.opened");
        MESSAGE_SHARE_DONE = builder.comment("Translation key shown to the sender after the share action completes.")
                .define("shareDone", "message.sharecodechestbypycoder.share_done");
        MESSAGE_SHARE_RECEIVED = builder.comment("Translation key shown to an online target player when a new share is received.")
                .define("shareReceived", "message.sharecodechestbypycoder.share_received");
        MESSAGE_ALREADY_HAVE_ON_ACCEPT = builder.comment("Translation key shown when accepting a shared chest that is already in the player's chest list.")
                .define("alreadyHaveOnAccept", "message.sharecodechestbypycoder.already_have_on_accept");
        MESSAGE_SHARE_ACCEPTED = builder.comment("Translation key shown after accepting a shared chest into the player's unopened chest list.")
                .define("shareAccepted", "message.sharecodechestbypycoder.share_accepted");
        MESSAGE_SHARE_REJECTED = builder.comment("Translation key shown after rejecting a shared chest from the inbox.")
                .define("shareRejected", "message.sharecodechestbypycoder.share_rejected");
        MESSAGE_SHARING_DISABLED = builder.comment("Translation key shown when a share action is attempted while sharing is disabled by config.")
                .define("sharingDisabled", "message.sharecodechestbypycoder.sharing_disabled");
        builder.pop();

        SPEC = builder.build();
    }

    private SharecodeConfig() {
    }
}
