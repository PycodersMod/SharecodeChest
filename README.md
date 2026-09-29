# SharecodeChest

提供可分享礼包码的箱子功能，用于以游戏内物品承载和分享礼包码。

## 工程布局

- Gradle 工程：[SharecodeChest](./SharecodeChest/)
- 工程详细说明：[SharecodeChest/README.md](./SharecodeChest/README.md)
- 项目注册信息：[PycodersMod.projects.json](../../PycodersMod.projects.json)
- 工作区统一测试入口：[launch.ps1](../../launch.ps1)
- 启动参数填写说明：[launch-parameters.md](../../launch-parameters.md)

## 构建

在 SharecodeChest 目录执行 .\gradlew.bat clean build。

工程使用 Gradle Java Toolchain 声明所需 Java 版本。机器本地的 JDK 路径位于工作区 local-config，不写入 Mod 仓库。

## 标识

| 项目 | 值 |
|---|---|
| Minecraft | 1.20.1 |
| Loader | Forge 47.2.0 |
| Java | 17 |
| Mod ID | sharecodechestbypycoder |
| Java Package | com.pycoder.sharecodechest |

Mod ID、Registry Namespace 和存档标识保持原值。工程目录、Gradle group、Java package 和 GitHub 仓库名属于工程组织信息。


## ????

# SharecodeChestByPycoder v1.0.0 Release Notes

---

## English

### What's Implemented in v1.0.0

#### Loot Container Conversion

- On world generation and existing chunk loading, eligible loot containers are automatically replaced with the mod's special containers.
- Converted block types: chests (plain, trapped), barrels, and any other randomizable container block entity. Chest minecarts are recorded via interaction interception rather than block replacement.
- The conversion runs in the background, a few chunks per tick, to avoid world-load stutter.
- A persistent conversion record is kept per world to prevent re-conversion on reload or dimension travel.

#### Special Container Behavior

- Converted containers keep the exact same model, collision box, and appearance as the original. No visual change yet — custom textures are planned for a later release.
- Right-clicking a converted container no longer opens it. Instead, it records the container into your personal chest list and shows a subtitle.
- Hardness is equivalent to reinforced deepslate. Blast resistance is set to near-maximum (3,600,000, comparable to bedrock).
- Destroying a container by any means (mining, explosion, etc.) produces no drops — no container item, no original block, no loot.
- The mod's special containers cannot be crafted, and they do not affect vanilla recipe behavior.
- In Creative mode, if a player places one of these special containers, it has no bound loot table. Right-clicking will show "This container has no loot table" and will not record anything.
- Trapped chest variants emit a redstone pulse (15 ticks) on right-click, matching original trapped chest behavior.

#### Three-Tab GUI (Press C)

- Press the `C` key (configurable in key bindings) to open the main chest list GUI.
- The GUI has three tabs: **Unopened**, **Opened**, and **Inbox**. The active tab is visually highlighted.
- Each tab has its own search box — filter entries by structure name in real time.
- Entries in each tab are sorted by structure name, using locale-aware collation for correct ordering of Chinese names.
- Each entry displays two lines: the structure name and a short identifier (first 8 characters of the instance UUID) for distinguishing multiple containers of the same type.

#### Opening Loot

- In the Unopened tab, click the "Open" button on an entry.
- The server generates loot from the original loot table at that moment (not pre-generated).
- Generated items are inserted directly into the player's inventory; overflow drops at the player's feet.
- Each player may open the same container instance independently once. Different players rolling the same container each get their own random loot.
- Once opened, the container moves to the Opened tab and cannot be opened again by the same player.

#### Sharing

- In the Unopened or Opened tab, click the "Share" button on an entry to open the share screen.
- The share screen shows all known players (anyone who has ever joined the server), with online players listed first. A search box is available.
- Select players via checkboxes, or use "Select All" to toggle all filtered players.
- Click "Share" to send the container to the selected players' inboxes.
- Sharing adds a copy of the container reference (loot table + instance ID) to each recipient's inbox. It does not transfer items or consume the sender's record.
- If a recipient already has the same container instance in their inbox, it is silently skipped to avoid duplicates.
- Sharing to offline players is allowed (configurable).
- Recipients receive a system message when they come online and have a new share.

#### Receiving Shares

- In the Inbox tab, each entry shows the structure name and the sender's name.
- **Accept** adds the container to your Unopened list. If you already have or have opened that instance, you'll see a message and the inbox entry is removed without duplication.
- **Reject** removes the inbox entry.

#### Data Persistence

- All chest records, inbox entries, known players, instance-to-loot-table mappings, and conversion flags are persisted in the world's save data (overworld data storage).
- Data survives server restarts, player logout/login, chunk unload, dimension travel, and death.

#### Server-Authoritative Design

- Loot generation is always server-side. The client never generates or previews loot.
- Claim state (opened/unopened) is validated server-side on every operation.
- The client GUI receives a sync packet when opened and re-syncs after every action (open, share, accept, reject) via the same packet.

#### Configuration

- A `sharecodechestbypycoder-common.toml` config file is generated in the `config/` folder on first launch.
- Categories: `[conversion]`, `[sharing]`, `[loot]`, `[gui]`, `[messages]`.
- Every option has an English comment. Examples:
  - Toggle conversion for chests, barrels, trapped chests, and other randomizable containers separately.
  - Toggle sharing on/off, allow/disallow offline targets, silent inbox duplicate skip.
  - Toggle direct inventory insertion and overflow dropping.
  - Adjust visible row counts for main screen and share screen (range-restricted).
  - All message texts are configurable via string options — translation keys for resource pack override.

#### Known Scope in v1.0.0

- Custom container textures are not yet included. The mod currently uses vanilla block textures as placeholders.
- The GUI has no custom texture backgrounds — plain screen with text/buttons only.
- Modded containers that do not extend `RandomizableContainerBlockEntity` are not converted.
- Only `minecraft:chests/*` loot tables have pre-defined display name translations. Other loot tables fall back to an auto-generated readable name.
- The instance identifier displayed in the GUI is a shortened UUID prefix, not a sequential number.

---

## 中文

### v1.0.0 已实现内容

#### 战利品容器转换

- 世界生成和已有区块加载时，符合条件的战利品容器会自动被替换为模组的特殊容器。
- 被替换的方块类型：普通箱子、陷阱箱、木桶，以及其他继承 `RandomizableContainerBlockEntity` 的容器。运输矿车通过交互拦截实现记录，不做方块替换。
- 转换以每 tick 少量区块的后台方式执行，避免世界加载卡顿。
- 每个世界维护一份已转换记录，防止重复转换或维度切换后再次转换。

#### 特殊容器行为

- 被替换的容器保留与原版完全一致的模型、碰撞箱和外观。目前没有视觉变化——自定义材质将在后续版本制作。
- 右键被替换容器不再打开，而是记录到玩家个人箱子列表中，并显示操作提示。
- 挖掘难度与强化深板岩相同（硬度 55），爆炸抗性接近最大值（3,600,000，与基岩相当）。
- 任何方式破坏容器均无掉落——不掉落容器本身、不掉落原方块、不掉落内部物品。
- 模组的特殊容器不可合成，不影响原版合成配方。
- 创造模式下放置的特殊容器没有绑定战利品表，右键会提示"该容器没有绑定战利品表"，不会记录。
- 陷阱箱变种在右键记录时产生一次红石脉冲（15 tick），与原版陷阱箱行为一致。

#### 三栏 GUI（按 C 键）

- 按下 `C` 键（可在按键设置中修改）打开箱子列表界面。
- 界面分三个栏目：**未开启**、**已开启**、**收件箱**。当前栏目会高亮显示。
- 每个栏目有独立的搜索框，可按结构名称实时过滤。
- 列表按结构名称排序，中文按本地化排序规则排列。
- 每条记录显示两行：第一行结构名称，第二行编号（实例 UUID 前 8 位），用于区分同结构的多个箱子。

#### 开启战利品

- 在"未开启"栏目中点击条目的"开启"按钮。
- 服务端实时从原战利品表随机生成物品（非预生成）。
- 物品直接放入玩家背包；背包满时溢出物品掉落在脚边。
- 每个玩家可对同一容器实例独立开启一次。不同玩家开启同一容器各自获得独立随机结果。
- 开启后该条目移至"已开启"栏目，同一玩家无法再次开启。

#### 分享

- 在"未开启"或"已开启"栏目中点击条目的"分享"按钮，进入分享界面。
- 分享界面显示所有已知玩家（包括在线和离线），在线玩家排在前面，支持搜索。
- 通过复选框选择目标玩家，"全选"可切换选择全部已过滤玩家。
- 点击"分享"后将容器引用（战利品表 + 实例 ID）发送至所选玩家的收件箱。
- 分享不转移具体物品，也不消耗发送者的记录。
- 若接收者收件箱中已有同一实例，则静默跳过，避免重复。
- 支持分享给离线玩家（可配置）。
- 接收者在下次上线时会收到系统消息提示。

#### 接收分享

- 在"收件箱"栏目中，每条记录显示结构名称和发送者名称。
- **接收**：将该容器加入你的"未开启"列表。如果你已拥有或已开启过该实例，会显示提示并移除收件箱条目，不会重复添加。
- **拒绝**：直接移除收件箱条目。

#### 数据持久化

- 所有箱子记录、收件箱条目、已知玩家、实例与战利品表映射、已转换标记均保存在世界存档数据中。
- 数据在服务器重启、玩家登出/登录、区块卸载、维度传送、死亡后均不丢失。

#### 服务端权威设计

- 战利品生成始终在服务端执行，客户端从不生成或预览战利品。
- 每次操作（开启、分享、接收、拒绝）均在服务端验证记录状态。
- 客户端 GUI 打开时从服务端同步数据，每次操作后重新同步。

#### 配置文件

- 首次启动时在 `config/` 文件夹下生成 `sharecodechestbypycoder-common.toml`。
- 配置分类：`[conversion]`（转换）、`[sharing]`（分享）、`[loot]`（战利品）、`[gui]`（界面）、`[messages]`（消息）。
- 每一项均有英文注释。关键可配置项举例：
  - 分别控制箱子、木桶、陷阱箱、其他随机容器的转换开关。
  - 分享功能的开启/关闭、是否允许离线目标、收件箱静默去重。
  - 直接入背包和溢出掉落的开关。
  - 主界面和分享界面的可见行数（有范围限制）。
  - 所有提示文字均可通过配置项修改，翻译键可通过资源包覆盖。

#### v1.0.0 已知范围

- 暂未制作自定义容器贴图，目前使用原版方块纹理占位。
- GUI 没有自定义背景纹理，为纯文字/按钮界面。
- 不继承 `RandomizableContainerBlockEntity` 的模组容器不会被转换。
- 仅 `minecraft:chests/*` 战利品表预置了显示名称翻译，其他战利品表自动回退为可读名称。
- GUI 中显示的编号是实例 UUID 的简短前缀，非自增序号。

## License

本项目采用 MIT License，详见 [LICENSE](./LICENSE)。

本仓库的 Gradle Wrapper 保留其随附的 Apache-2.0 许可，详见 [THIRD_PARTY_NOTICES.md](./THIRD_PARTY_NOTICES.md)。
