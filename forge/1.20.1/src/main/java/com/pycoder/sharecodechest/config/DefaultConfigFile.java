package com.pycoder.sharecodechest.config;

import com.pycoder.sharecodechest.SharecodeChestByPycoder;
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
            SharecodeChestByPycoder.LOGGER.error("创建 SharecodeChestByPycoder 默认配置失败", exception);
        }
    }

    private static final String DEFAULT_CONTENT = """
            [conversion]
            # 是否在区块加载时将带战利品表的容器替换为 SharecodeChest 容器。
            convertLootContainers = true
            # 为 true 时，模组安装后首次加载旧区块会扫描并转换其中的容器。
            convertAlreadyGeneratedChunks = true
            # 为 true 时，带战利品表的原版箱子方块实体会转换为 SharecodeChest 战利品箱。
            convertChests = true
            # 为 true 时，带战利品表的原版木桶方块实体会转换为 SharecodeChest 战利品木桶。
            convertBarrels = true
            # 为 true 时，带战利品表的原版陷阱箱方块实体会转换为 SharecodeChest 陷阱战利品箱。
            convertTrappedChests = true
            # 为 true 时，带战利品表的非原版 RandomizableContainerBlockEntity 会转换为通用的 SharecodeChest 战利品箱。
            convertOtherRandomizableContainers = true
            # 为 true 时，右键带战利品表的矿车容器会记录容器，而不会打开原版容器。
            recordLootMinecarts = true

            [sharing]
            # 是否启用分享按钮和服务端分享操作。
            enableSharing = true
            # 为 true 时，曾加入过服务器的玩家即使当前离线，也可被选为分享对象。
            allowOfflineShareTargets = true
            # 为 true 时，如果目标收件箱已有该箱子，则忽略重复分享，不再添加重复条目。
            silentlySkipDuplicateInboxEntries = true
            # 为 true 时，若玩家接受的箱子已在自己的箱子列表中，则移除对应收件箱条目。
            removeInboxEntryWhenAlreadyRecorded = true

            [loot]
            # 为 true 时，仅在玩家于图形界面按下“打开”时生成战利品；记录或接受分享时不会生成。
            generateLootOnGuiOpen = true
            # 为 true 时，生成的战利品会直接放入玩家物品栏。
            insertLootDirectlyToInventory = true
            # 为 true 时，物品栏放不下的物品会掉落在玩家脚边。
            dropOverflowItemsAtPlayerFeet = true
            # 为 true 时，每名玩家都可独立开启同一已记录箱子一次，机制类似 Lootr。
            eachPlayerRollsIndependently = true

            [gui]
            # 主界面同时显示的箱子或收件箱条目行数。
            mainScreenVisibleRows = 7
            # 分享界面同时显示的玩家行数。
            shareScreenVisibleRows = 8
            # 为 true 时，分享界面会在玩家列表上方显示名称搜索框。
            enableShareSearchBox = true
            # 为 true 时，分享界面会在每名玩家名称后显示在线/离线状态。
            showOnlineStatusInShareScreen = true
            # 为 true 时，在分享界面按 ESC 会返回箱子主界面，而不是关闭界面。
            escReturnsFromShareScreen = true

            [messages]
            # 首次记录容器时在快捷栏上方显示的翻译键。
            recorded = "message.sharecodechestbypycoder.recorded"
            # 玩家右键已通过分享接受的世界容器时显示的翻译键。
            acceptedShareBefore = "message.sharecodechestbypycoder.accepted_share_before"
            # 玩家尝试记录或打开已经开启的箱子实例时显示的翻译键。
            alreadyOpened = "message.sharecodechestbypycoder.already_opened"
            # 玩家右键已记录但尚未开启的容器时显示的翻译键。
            alreadyRecorded = "message.sharecodechestbypycoder.already_recorded"
            # 特殊容器没有绑定战利品表时显示的翻译键，例如创造模式放置的方块。
            noLootTable = "message.sharecodechestbypycoder.no_loot_table"
            # 服务端成功生成并发放战利品后显示的翻译键。
            opened = "message.sharecodechestbypycoder.opened"
            # 分享操作完成后向发送者显示的翻译键。
            shareDone = "message.sharecodechestbypycoder.share_done"
            # 在线目标玩家收到新分享时显示的翻译键。
            shareReceived = "message.sharecodechestbypycoder.share_received"
            # 接受的分享箱子已在玩家箱子列表中时显示的翻译键。
            alreadyHaveOnAccept = "message.sharecodechestbypycoder.already_have_on_accept"
            # 玩家接受分享箱子并将其加入未开启列表后显示的翻译键。
            shareAccepted = "message.sharecodechestbypycoder.share_accepted"
            # 玩家从收件箱拒绝分享箱子后显示的翻译键。
            shareRejected = "message.sharecodechestbypycoder.share_rejected"
            # 配置禁用分享时仍尝试执行分享操作所显示的翻译键。
            sharingDisabled = "message.sharecodechestbypycoder.sharing_disabled"
            """;

    private DefaultConfigFile() {
    }
}
