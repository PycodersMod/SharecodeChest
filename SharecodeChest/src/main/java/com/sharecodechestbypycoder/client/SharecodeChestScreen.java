package com.sharecodechestbypycoder.client;

import com.sharecodechestbypycoder.config.SharecodeConfig;
import com.sharecodechestbypycoder.network.AcceptSharePacket;
import com.sharecodechestbypycoder.network.ModNetwork;
import com.sharecodechestbypycoder.network.OpenChestPacket;
import com.sharecodechestbypycoder.network.RejectSharePacket;
import com.sharecodechestbypycoder.network.SharePacket;
import com.sharecodechestbypycoder.network.SyncDataPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class SharecodeChestScreen extends Screen {
    private static final int TAB_UNOPENED = 0;
    private static final int TAB_OPENED = 1;
    private static final int TAB_INBOX = 2;
    private static final int TAB_COUNT = 3;
    private static final Collator NAME_COLLATOR = Collator.getInstance(Locale.CHINA);
    private final SyncDataPacket data;
    private final Set<UUID> selectedPlayers = new HashSet<>();
    private final String[] tabSearchValues = new String[TAB_COUNT];
    private int tab = TAB_UNOPENED;
    private int scroll = 0;
    private int shareScroll = 0;
    private String sharingInstanceId;
    private EditBox mainSearchBox;
    private EditBox searchBox;

    public SharecodeChestScreen(SyncDataPacket data) {
        super(Component.translatable("screen.sharecodechestbypycoder.title"));
        this.data = data;
        for (int i = 0; i < tabSearchValues.length; i++) {
            tabSearchValues[i] = "";
        }
    }

    @Override
    protected void init() {
        rebuildMain();
    }

    private void rebuildMain() {
        clearWidgets();
        sharingInstanceId = null;
        mainSearchBox = null;
        searchBox = null;
        int x = width / 2 - 150;
        addRenderableWidget(tabButton(TAB_UNOPENED, x, 24, "screen.sharecodechestbypycoder.unopened"));
        addRenderableWidget(tabButton(TAB_OPENED, x + 102, 24, "screen.sharecodechestbypycoder.opened"));
        addRenderableWidget(tabButton(TAB_INBOX, x + 204, 24, "screen.sharecodechestbypycoder.inbox"));

        mainSearchBox = new EditBox(font, x, 50, 210, 20, Component.translatable("screen.sharecodechestbypycoder.search_structure"));
        mainSearchBox.setValue(tabSearchValues[tab]);
        mainSearchBox.setResponder(value -> {
            tabSearchValues[tab] = value;
            scroll = 0;
            rebuildMain();
        });
        addRenderableWidget(mainSearchBox);
        setFocused(mainSearchBox);
        mainSearchBox.setFocused(true);

        List<SyncDataPacket.Entry> rows = visibleRows();
        int visibleRows = SharecodeConfig.MAIN_SCREEN_VISIBLE_ROWS.get();
        int max = Math.min(rows.size(), scroll + visibleRows);
        for (int i = scroll; i < max; i++) {
            SyncDataPacket.Entry entry = rows.get(i);
            int rowY = 82 + (i - scroll) * 30;
            if (tab == TAB_INBOX) {
                addRenderableWidget(Button.builder(Component.translatable("screen.sharecodechestbypycoder.accept"), button -> ModNetwork.CHANNEL.sendToServer(new AcceptSharePacket(entry.instanceId()))).bounds(x + 174, rowY, 56, 20).build());
                addRenderableWidget(Button.builder(Component.translatable("screen.sharecodechestbypycoder.reject"), button -> ModNetwork.CHANNEL.sendToServer(new RejectSharePacket(entry.instanceId()))).bounds(x + 234, rowY, 56, 20).build());
            } else {
                if (!entry.opened()) {
                    addRenderableWidget(Button.builder(Component.translatable("screen.sharecodechestbypycoder.open"), button -> ModNetwork.CHANNEL.sendToServer(new OpenChestPacket(entry.instanceId()))).bounds(x + 174, rowY, 56, 20).build());
                }
                addRenderableWidget(Button.builder(Component.translatable("screen.sharecodechestbypycoder.share"), button -> rebuildShare(entry.instanceId())).bounds(x + 234, rowY, 56, 20).build());
            }
        }
        addRenderableWidget(Button.builder(Component.literal("▲"), button -> { if (scroll > 0) { scroll--; rebuildMain(); } }).bounds(x + 305, 82, 22, 20).build());
        addRenderableWidget(Button.builder(Component.literal("▼"), button -> { if (scroll + visibleRows < rows.size()) { scroll++; rebuildMain(); } }).bounds(x + 305, 106, 22, 20).build());
    }

    private Button tabButton(int tabId, int x, int y, String labelKey) {
        Component label = tab == tabId
                ? Component.literal("▶ ").append(Component.translatable(labelKey)).append(" ◀")
                : Component.translatable(labelKey);
        return Button.builder(label, button -> switchTab(tabId)).bounds(x, y, 96, 20).build();
    }

    private void switchTab(int nextTab) {
        tab = nextTab;
        scroll = 0;
        rebuildMain();
    }

    private void rebuildShare(String instanceId) {
        clearWidgets();
        selectedPlayers.clear();
        shareScroll = 0;
        sharingInstanceId = instanceId;
        addSearchAndShareControls("");
    }

    private void rebuildShareKeepSelection() {
        String value = searchBox == null ? "" : searchBox.getValue();
        clearWidgets();
        addSearchAndShareControls(value);
    }

    private void addSearchAndShareControls(String searchValue) {
        int x = width / 2 - 150;
        if (SharecodeConfig.ENABLE_SHARE_SEARCH_BOX.get()) {
            searchBox = new EditBox(font, x, 28, 210, 20, Component.translatable("screen.sharecodechestbypycoder.search"));
            searchBox.setValue(searchValue);
            searchBox.setResponder(ignored -> {
                shareScroll = 0;
                rebuildShareKeepSelection();
            });
            addRenderableWidget(searchBox);
        } else {
            searchBox = null;
        }
        addRenderableWidget(Button.builder(Component.translatable("screen.sharecodechestbypycoder.select_all"), button -> toggleFilteredPlayers()).bounds(x + 216, 28, 84, 20).build());
        rebuildSharePlayerButtons();
    }

    private void rebuildSharePlayerButtons() {
        int x = width / 2 - 150;
        List<SyncDataPacket.PlayerEntry> players = filteredPlayers();
        int visibleRows = SharecodeConfig.SHARE_SCREEN_VISIBLE_ROWS.get();
        int max = Math.min(players.size(), shareScroll + visibleRows);
        for (int i = shareScroll; i < max; i++) {
            SyncDataPacket.PlayerEntry player = players.get(i);
            int rowY = 58 + (i - shareScroll) * 22;
            addRenderableWidget(new Checkbox(x + 250, rowY, 20, 20, Component.empty(), selectedPlayers.contains(player.uuid())) {
                @Override
                public void onPress() {
                    super.onPress();
                    if (selected()) selectedPlayers.add(player.uuid()); else selectedPlayers.remove(player.uuid());
                }
            });
        }
        addRenderableWidget(Button.builder(Component.literal("▲"), button -> { if (shareScroll > 0) { shareScroll--; rebuildShareKeepSelection(); } }).bounds(x + 304, 58, 22, 20).build());
        addRenderableWidget(Button.builder(Component.literal("▼"), button -> { if (shareScroll + visibleRows < filteredPlayers().size()) { shareScroll++; rebuildShareKeepSelection(); } }).bounds(x + 304, 82, 22, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("screen.sharecodechestbypycoder.cancel"), button -> rebuildMain()).bounds(x, height - 34, 80, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("screen.sharecodechestbypycoder.share"), button -> {
            if (sharingInstanceId != null) ModNetwork.CHANNEL.sendToServer(new SharePacket(sharingInstanceId, new ArrayList<>(selectedPlayers)));
        }).bounds(x + 220, height - 34, 80, 20).build());
    }

    private void toggleFilteredPlayers() {
        List<SyncDataPacket.PlayerEntry> players = filteredPlayers();
        boolean allSelected = players.stream().allMatch(player -> selectedPlayers.contains(player.uuid()));
        if (allSelected) {
            players.forEach(player -> selectedPlayers.remove(player.uuid()));
        } else {
            players.forEach(player -> selectedPlayers.add(player.uuid()));
        }
        rebuildShareKeepSelection();
    }

    private List<SyncDataPacket.PlayerEntry> filteredPlayers() {
        String query = searchBox == null ? "" : searchBox.getValue().trim().toLowerCase(Locale.ROOT);
        return data.players().stream().filter(player -> query.isEmpty() || player.name().toLowerCase(Locale.ROOT).contains(query)).toList();
    }

    private List<SyncDataPacket.Entry> visibleRows() {
        String query = tabSearchValues[tab].trim().toLowerCase(Locale.ROOT);
        List<SyncDataPacket.Entry> source = tab == TAB_INBOX
                ? data.inbox()
                : data.chests().stream().filter(entry -> (tab == TAB_OPENED) == entry.opened()).toList();
        return source.stream()
                .filter(entry -> query.isEmpty() || displayNameText(entry).toLowerCase(Locale.ROOT).contains(query))
                .sorted(Comparator.comparing(this::displayNameText, NAME_COLLATOR).thenComparing(SyncDataPacket.Entry::instanceId))
                .toList();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        graphics.drawCenteredString(font, title, width / 2, 10, 0xFFFFFF);
        super.render(graphics, mouseX, mouseY, partialTick);
        if (sharingInstanceId == null) renderSelectedTabHighlight(graphics);
        if (sharingInstanceId == null) renderMainRows(graphics); else renderShareRows(graphics);
    }

    private void renderSelectedTabHighlight(GuiGraphics graphics) {
        int x = width / 2 - 150 + tab * 102;
        graphics.fill(x + 4, 45, x + 92, 48, 0xFFFFD44A);
    }

    private void renderMainRows(GuiGraphics graphics) {
        int x = width / 2 - 150;
        List<SyncDataPacket.Entry> rows = visibleRows();
        int max = Math.min(rows.size(), scroll + SharecodeConfig.MAIN_SCREEN_VISIBLE_ROWS.get());
        for (int i = scroll; i < max; i++) {
            SyncDataPacket.Entry entry = rows.get(i);
            int rowY = 85 + (i - scroll) * 30;
            Component name = displayName(entry);
            if (tab == TAB_INBOX) name = Component.translatable("screen.sharecodechestbypycoder.inbox_line", name, entry.senderName());
            graphics.drawString(font, name, x, rowY, 0xE0E0E0, false);
            graphics.pose().pushPose();
            graphics.pose().scale(0.75F, 0.75F, 1.0F);
            graphics.drawString(font, Component.translatable("screen.sharecodechestbypycoder.entry_number", shortInstanceId(entry.instanceId())), (int) (x / 0.75F), (int) ((rowY + 11) / 0.75F), 0x888888, false);
            graphics.pose().popPose();
        }
        if (rows.isEmpty()) graphics.drawCenteredString(font, Component.translatable("screen.sharecodechestbypycoder.empty"), width / 2, 114, 0xAAAAAA);
    }

    private String shortInstanceId(String instanceId) {
        int dash = instanceId.indexOf('-');
        if (dash > 0) {
            return instanceId.substring(0, dash);
        }
        return instanceId.length() <= 8 ? instanceId : instanceId.substring(0, 8);
    }

    private Component displayName(SyncDataPacket.Entry entry) {
        return Component.literal(displayNameText(entry));
    }

    private String displayNameText(SyncDataPacket.Entry entry) {
        if (I18n.exists(entry.displayKey())) {
            return I18n.get(entry.displayKey());
        }
        return readableLootTableName(entry.lootTableId());
    }

    private String readableLootTableName(String lootTableId) {
        int separator = lootTableId.indexOf(':');
        String namespace = separator >= 0 ? lootTableId.substring(0, separator) : "";
        String path = separator >= 0 ? lootTableId.substring(separator + 1) : lootTableId;
        if (path.startsWith("chests/")) {
            path = path.substring("chests/".length());
        }
        path = path.replace('/', ' ').replace('_', ' ').trim();
        if (path.isEmpty()) {
            return lootTableId;
        }
        StringBuilder result = new StringBuilder();
        for (String word : path.split(" ")) {
            if (word.isEmpty()) continue;
            if (!result.isEmpty()) result.append(' ');
            result.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        if (!namespace.isEmpty() && !"minecraft".equals(namespace)) {
            result.append(" (").append(namespace).append(')');
        }
        return result.toString();
    }

    private void renderShareRows(GuiGraphics graphics) {
        int x = width / 2 - 150;
        List<SyncDataPacket.PlayerEntry> players = filteredPlayers();
        int max = Math.min(players.size(), shareScroll + SharecodeConfig.SHARE_SCREEN_VISIBLE_ROWS.get());
        for (int i = shareScroll; i < max; i++) {
            SyncDataPacket.PlayerEntry player = players.get(i);
            int rowY = 64 + (i - shareScroll) * 22;
            Component label = Component.literal(player.name());
            if (SharecodeConfig.SHOW_ONLINE_STATUS_IN_SHARE_SCREEN.get()) {
                Component status = player.online() ? Component.translatable("screen.sharecodechestbypycoder.online") : Component.translatable("screen.sharecodechestbypycoder.offline");
                label = label.copy().append(" ").append(status);
            }
            graphics.drawString(font, label, x, rowY, player.online() ? 0x80FF80 : 0xAAAAAA, false);
        }
        if (players.isEmpty()) graphics.drawCenteredString(font, Component.translatable("screen.sharecodechestbypycoder.no_players"), width / 2, 90, 0xAAAAAA);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256 && sharingInstanceId != null && SharecodeConfig.ESC_RETURNS_FROM_SHARE_SCREEN.get()) {
            rebuildMain();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
