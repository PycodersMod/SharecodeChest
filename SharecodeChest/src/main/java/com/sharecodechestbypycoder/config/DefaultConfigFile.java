package com.sharecodechestbypycoder.config;

import com.sharecodechestbypycoder.SharecodeChestByPycoder;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class DefaultConfigFile {
    private static final String FILE_NAME = SharecodeChestByPycoder.MOD_ID + "-common.toml";

    public static void createIfMissing() {
        Path configPath = FMLPaths.CONFIGDIR.get().resolve(FILE_NAME);
        if (Files.exists(configPath)) return;
        try {
            Files.createDirectories(configPath.getParent());
            Files.writeString(configPath, DEFAULT_CONTENT, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            SharecodeChestByPycoder.LOGGER.error("Failed to create default SharecodeChestByPycoder config", exception);
        }
    }

    private static final String DEFAULT_CONTENT = """
            [conversion]
            # Master switch for replacing loot-table containers with SharecodeChest containers during chunk load.
            convertLootContainers = true
            # If true, old chunks loaded after the mod is installed are scanned and converted once.
            convertAlreadyGeneratedChunks = true
            # If true, vanilla chest block entities with loot tables are converted to SharecodeChest loot chests.
            convertChests = true
            # If true, vanilla barrel block entities with loot tables are converted to SharecodeChest loot barrels.
            convertBarrels = true
            # If true, vanilla trapped chest block entities with loot tables are converted to SharecodeChest trapped loot chests.
            convertTrappedChests = true
            # If true, non-vanilla RandomizableContainerBlockEntity blocks with loot tables are converted to the generic SharecodeChest loot chest.
            convertOtherRandomizableContainers = true
            # If true, loot-table minecart containers are recorded on right click instead of opening the vanilla container.
            recordLootMinecarts = true

            [sharing]
            # Master switch for the share button and server-side share action.
            enableSharing = true
            # If true, players who have joined before can be selected as share targets even while offline.
            allowOfflineShareTargets = true
            # If true, sharing a chest already present in the target inbox does nothing instead of adding a duplicate inbox row.
            silentlySkipDuplicateInboxEntries = true
            # If true, accepting an inbox entry for a chest already in the player's chest list removes that inbox entry.
            removeInboxEntryWhenAlreadyRecorded = true

            [loot]
            # If true, loot is generated only when the player presses Open in the GUI, not when recording or accepting a share.
            generateLootOnGuiOpen = true
            # If true, generated loot is inserted directly into the player's inventory.
            insertLootDirectlyToInventory = true
            # If true, items that do not fit in the inventory are dropped at the player's feet.
            dropOverflowItemsAtPlayerFeet = true
            # If true, each player can roll the same recorded chest instance once independently, similar to Lootr.
            eachPlayerRollsIndependently = true

            [gui]
            # Number of chest or inbox rows shown at once on the main screen.
            mainScreenVisibleRows = 7
            # Number of player rows shown at once on the share screen.
            shareScreenVisibleRows = 8
            # If true, the share screen displays a name search box above the player list.
            enableShareSearchBox = true
            # If true, the share screen appends online/offline status text after each player name.
            showOnlineStatusInShareScreen = true
            # If true, pressing ESC on the share screen returns to the main chest screen instead of closing the GUI.
            escReturnsFromShareScreen = true

            [messages]
            # Translation key shown in the action bar when a container is recorded for the first time.
            recorded = "message.sharecodechestbypycoder.recorded"
            # Translation key shown when the player right-clicks a world container that was already accepted from a share.
            acceptedShareBefore = "message.sharecodechestbypycoder.accepted_share_before"
            # Translation key shown when the player tries to record or open a chest instance that is already opened.
            alreadyOpened = "message.sharecodechestbypycoder.already_opened"
            # Translation key shown when the player right-clicks a container that is already recorded but not opened.
            alreadyRecorded = "message.sharecodechestbypycoder.already_recorded"
            # Translation key shown when a special container has no bound loot table, such as a creative-placed block.
            noLootTable = "message.sharecodechestbypycoder.no_loot_table"
            # Translation key shown after the server successfully generates and grants loot.
            opened = "message.sharecodechestbypycoder.opened"
            # Translation key shown to the sender after the share action completes.
            shareDone = "message.sharecodechestbypycoder.share_done"
            # Translation key shown to an online target player when a new share is received.
            shareReceived = "message.sharecodechestbypycoder.share_received"
            # Translation key shown when accepting a shared chest that is already in the player's chest list.
            alreadyHaveOnAccept = "message.sharecodechestbypycoder.already_have_on_accept"
            # Translation key shown after accepting a shared chest into the player's unopened chest list.
            shareAccepted = "message.sharecodechestbypycoder.share_accepted"
            # Translation key shown after rejecting a shared chest from the inbox.
            shareRejected = "message.sharecodechestbypycoder.share_rejected"
            # Translation key shown when a share action is attempted while sharing is disabled by config.
            sharingDisabled = "message.sharecodechestbypycoder.sharing_disabled"
            """;

    private DefaultConfigFile() {
    }
}
