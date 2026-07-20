package catcraft.title;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class MessageManager {
    private static MessageManager instance;
    private final Map<String, String> messages = new HashMap<>();

    private static final Map<String, Map<String, String>> DEFAULT_MESSAGES = new HashMap<>();

    static {
        // ========== 中文 ==========
        Map<String, String> zh = new HashMap<>();
        zh.put("no-permission", "&c你没有权限执行此操作");
        zh.put("player-only-command", "&c该命令仅限玩家执行");
        zh.put("player-offline", "&c玩家不在线");
        zh.put("invalid-id", "&cID必须是数字");
        zh.put("id-already-owned", "&c该玩家已拥有此ID的称号（头衔或后缀），ID必须全局唯一！");
        zh.put("no-such-id", "&c该玩家没有此ID的称号");
        zh.put("not-title", "&c该ID不是头衔，请使用 suffixedit 修改后缀");
        zh.put("not-suffix", "&c该ID不是后缀，请使用 edit 修改头衔");
        zh.put("no-title", "&c你未拥有此头衔");
        zh.put("no-suffix", "&c你未拥有此后缀");
        zh.put("none", "无");
        zh.put("title", "头衔");
        zh.put("suffix", "后缀");

        zh.put("admin-give-usage", "用法: /titleadmin %s <玩家> <ID> <显示名>");
        zh.put("admin-edit-usage", "用法: /titleadmin %s <玩家> <ID> <新显示名>");
        zh.put("admin-take-usage", "用法: /titleadmin %s <玩家> <ID>");
        zh.put("admin-list-usage", "用法: /titleadmin %s <玩家>");
        zh.put("admin-setactive-usage", "用法: /titleadmin %s <玩家> <ID>");
        zh.put("admin-deactive-usage", "用法: /titleadmin %s <玩家>");
        zh.put("admin-give-success", "&a已为 %s 添加%s ID:%d 显示: %s");
        zh.put("admin-give-fail", "&c添加失败，请检查控制台日志");
        zh.put("admin-edit-success", "&a已修改 %s 的%s ID:%d 为: %s");
        zh.put("admin-edit-fail", "&c修改失败，请检查控制台日志");
        zh.put("admin-take-success", "&a已移除 %s 的%s ID:%d");
        zh.put("admin-take-fail", "&c移除失败（可能该称号正在启用）");
        zh.put("admin-list-title-header", "&6===== %s 的头衔列表 =====");
        zh.put("admin-list-suffix-header", "&6===== %s 的后缀列表 =====");
        zh.put("admin-list-empty", "&e无");
        zh.put("admin-setactive-success", "&a已激活 %s 的%s ID:%d");
        zh.put("admin-setactive-fail", "&c激活失败（玩家可能未拥有该称号）");
        zh.put("admin-deactive-success", "&a已停用 %s 的%s");
        zh.put("admin-deactive-fail", "&c停用失败");

        zh.put("player-list-header", "&6===== 我的称号列表 =====");
        zh.put("player-list-empty", "&e你还没有任何称号，请联系管理员添加。");
        zh.put("player-list-hint", "使用 /catcraft gui 打开图形界面");
        zh.put("player-active-usage", "用法: /catcraft active <ID>");
        zh.put("player-active-success", "&a已启用头衔 ID:%d");
        zh.put("player-active-fail", "&c启用失败");
        zh.put("player-deactive-success", "&a已停用当前头衔，恢复默认");
        zh.put("player-deactive-fail", "&c停用失败");
        zh.put("player-suffixactive-usage", "用法: /catcraft suffixactive <ID>");
        zh.put("player-suffixactive-success", "&a已启用后缀 ID:%d");
        zh.put("player-suffixactive-fail", "&c启用失败");
        zh.put("player-suffixdeactive-success", "&a已停用当前后缀");
        zh.put("player-suffixdeactive-fail", "&c停用失败");
        zh.put("player-remove-usage", "用法: /catcraft remove <ID>");
        zh.put("player-remove-success", "&a已移除称号 ID:%d");
        zh.put("player-remove-fail", "&c无法移除当前启用的称号，请先停用");

        zh.put("player-help-title", "玩家称号指令");
        zh.put("help-gui", "打开图形界面");
        zh.put("help-list", "查看所有称号");
        zh.put("help-active", "激活头衔");
        zh.put("help-deactive", "停用头衔");
        zh.put("help-suffixactive", "激活后缀");
        zh.put("help-suffixdeactive", "停用后缀");
        zh.put("help-remove", "移除称号（不可移除激活的）");

        zh.put("admin-help-give", "添加头衔");
        zh.put("admin-help-edit", "修改头衔");
        zh.put("admin-help-take", "移除头衔");
        zh.put("admin-help-list", "查看玩家头衔");
        zh.put("admin-help-setactive", "激活头衔");
        zh.put("admin-help-deactive", "停用头衔");
        zh.put("admin-help-suffixgive", "添加后缀");
        zh.put("admin-help-suffixedit", "修改后缀");
        zh.put("admin-help-suffixtake", "移除后缀");
        zh.put("admin-help-suffixlist", "查看玩家后缀");
        zh.put("admin-help-suffixsetactive", "激活后缀");
        zh.put("admin-help-suffixdeactive", "停用后缀");

        zh.put("update-disabled", "更新检测已关闭");
        zh.put("update-available", "CatCraftTitle 有新版本可用！");
        zh.put("update-current", "当前版本:");
        zh.put("update-latest", "最新版本:");
        zh.put("update-download", "下载地址:");
        zh.put("update-api-error", "GitHub API 返回错误码: %d");
        zh.put("update-parse-error", "无法解析 tag_name");
        zh.put("update-fail", "更新检测失败: %s");

        zh.put("gui-home-title", "§6CatCraft称号管理 · 主页");
        zh.put("admin-help-title", "管理员称号指令");
        zh.put("gui-head-lore", "§7点击进入称号管理");
        zh.put("gui-title-btn", "§6头衔管理");
        zh.put("gui-title-lore1", "§7点击查看所有头衔");
        zh.put("gui-title-lore2", "§7当前激活: ");
        zh.put("gui-suffix-btn", "§6后缀管理");
        zh.put("gui-suffix-lore1", "§7点击查看所有后缀");
        zh.put("gui-suffix-lore2", "§7当前激活: ");
        zh.put("gui-close", "§c关闭");
        zh.put("gui-close-lore", "§7点击关闭界面");
        zh.put("gui-none", "§7无");
        zh.put("gui-deleted", "§7已删除");
        zh.put("gui-title-page", "§6头衔管理");
        zh.put("gui-suffix-page", "§6后缀管理");
        zh.put("gui-back-home", "§e返回主页");
        zh.put("gui-back-home-lore", "§7点击返回");
        zh.put("gui-current-active", "§a当前激活");
        zh.put("gui-deactivate", "§c停用");
        zh.put("gui-deactivate-lore", "§7点击停用当前激活的称号");
        zh.put("gui-id", "§7ID: ");
        zh.put("gui-active", "§a★ 已激活");
        zh.put("gui-click-activate", "§7点击激活");
        zh.put("gui-previous", "§6上一页");
        zh.put("gui-next", "§6下一页");
        zh.put("banner-author", "插件作者：&eQingNiaoQaQ &7(CatCraft Team)");
        zh.put("banner-server", "服务器:");
        zh.put("banner-database", "数据库:");
        zh.put("banner-papi", "PlaceholderAPI: ");
        zh.put("banner-connected", "已连接");
        zh.put("banner-failed", "连接失败");
        zh.put("banner-found", "已找到");
        zh.put("banner-not-found", "未找到");
        zh.put("banner-disconnected", "未连接");

        // 管理员帮助完整行
        zh.put("admin-help-give-line", "§e/titleadmin give §f<玩家> <ID> <显示名> §7- 添加头衔");
        zh.put("admin-help-edit-line", "§e/titleadmin edit §f<玩家> <ID> <新显示名> §7- 修改头衔");
        zh.put("admin-help-take-line", "§e/titleadmin take §f<玩家> <ID> §7- 移除头衔");
        zh.put("admin-help-list-line", "§e/titleadmin list §f<玩家> §7- 查看玩家头衔");
        zh.put("admin-help-setactive-line", "§e/titleadmin setactive §f<玩家> <ID> §7- 激活头衔");
        zh.put("admin-help-deactive-line", "§e/titleadmin deactive §f<玩家> §7- 停用头衔");
        zh.put("admin-help-suffixgive-line", "§e/titleadmin suffixgive §f<玩家> <ID> <显示名> §7- 添加后缀");
        zh.put("admin-help-suffixedit-line", "§e/titleadmin suffixedit §f<玩家> <ID> <新显示名> §7- 修改后缀");
        zh.put("admin-help-suffixtake-line", "§e/titleadmin suffixtake §f<玩家> <ID> §7- 移除后缀");
        zh.put("admin-help-suffixlist-line", "§e/titleadmin suffixlist §f<玩家> §7- 查看玩家后缀");
        zh.put("admin-help-suffixsetactive-line", "§e/titleadmin suffixsetactive §f<玩家> <ID> §7- 激活后缀");
        zh.put("admin-help-suffixdeactive-line", "§e/titleadmin suffixdeactive §f<玩家> §7- 停用后缀");

        // 玩家帮助完整行
        zh.put("player-help-gui-line", "§e/catcraft gui §7- 打开图形界面");
        zh.put("player-help-list-line", "§e/catcraft list §7- 查看所有称号");
        zh.put("player-help-active-line", "§e/catcraft active §f<ID> §7- 激活头衔");
        zh.put("player-help-deactive-line", "§e/catcraft deactive §7- 停用头衔");
        zh.put("player-help-suffixactive-line", "§e/catcraft suffixactive §f<ID> §7- 激活后缀");
        zh.put("player-help-suffixdeactive-line", "§e/catcraft suffixdeactive §7- 停用后缀");
        zh.put("player-help-remove-line", "§e/catcraft remove §f<ID> §7- 移除称号（不可移除激活的）");
        zh.put("player-help-shop-line", "§e/catcraft shop §7- 打开商店");

        // 商店相关
        zh.put("gui-shop-title", "§6CatCraft称号商店");
        zh.put("gui-shop-lore", "§7点击进入商店购买称号");
        zh.put("gui-shop-balance-hint", "§7你的当前余额");
        zh.put("gui-shop-price", "价格:");
        zh.put("gui-shop-owned", "已拥有");
        zh.put("gui-shop-click-buy", "点击购买");
        zh.put("gui-balance", "§6当前余额: §a");
        zh.put("shop-disabled", "§c商店系统已关闭");
        zh.put("shop-purchase-success", "§a你成功购买了称号: %s");
        zh.put("shop-purchase-fail", "§c购买失败，可能余额不足、已拥有或商店错误");

        zh.put("gui-shop-filter-all", "全部");
        zh.put("gui-shop-filter-title", "头衔");
        zh.put("gui-shop-filter-suffix", "后缀");
        zh.put("gui-shop-click-filter", "§7点击选择");
        zh.put("gui-shop-id", "ID:");
        zh.put("gui-signin-btn", "每日签到");
        zh.put("gui-signin-done", "今日已签到");
        zh.put("gui-signin-lore", "签到获得 §6%d 金币");
        zh.put("gui-signin-already", "今天已经签到了哦");
        zh.put("gui-signin-success", "§a签到成功！获得 %d 金币");
        zh.put("gui-signin-fail", "§c签到失败，请稍后重试");

        zh.put("admin-help-shop-title", "商店管理");
        zh.put("admin-help-shop-add-line", "§e/titleadmin shop add §f<ID> <类型(0/1)> <价格> <显示名> §7- 添加商品");
        zh.put("admin-help-shop-remove-line", "§e/titleadmin shop remove §f<ID> <类型> §7- 移除商品");
        zh.put("admin-help-shop-setprice-line", "§e/titleadmin shop setprice §f<ID> <类型> <新价格> §7- 修改价格");
        zh.put("admin-help-shop-list-line", "§e/titleadmin shop list §7- 列出所有商品");
        zh.put("admin-help-shop-givebalance-line", "§e/titleadmin shop givebalance §f<玩家> <金额> §7- 增加玩家金币");
        zh.put("admin-help-shop-setbalance-line", "§e/titleadmin shop setbalance §f<玩家> <金额> §7- 设置玩家金币");
        zh.put("admin-help-shop-toggle-line", "§e/titleadmin shop toggle §7- 开关商店（需修改配置文件）");

        zh.put("gui-shop-home-title", "§6CatCraft称号商店");
        zh.put("gui-shop-back-home", "§e返回商店首页");
        zh.put("gui-shop-back-home-lore", "§7点击返回");
        zh.put("gui-shop-title-list-title", "§6头衔商店");
        zh.put("gui-shop-title-list-suffix", "§6后缀商店");
        zh.put("gui-shop-type", "类型:");
        zh.put("gui-shop-list-hint", "§7点击进入查看");

        zh.put("admin-panel-title", "§6CatCraft 管理面板");
        zh.put("admin-author", "§7作者: §eQingNiaoQaQ");
        zh.put("admin-papi-status", "§7PlaceholderAPI: ");
        zh.put("admin-papi-found", "§a✔");
        zh.put("admin-papi-notfound", "§c✘");
        zh.put("admin-server-status", "§6服务器状态");
        zh.put("admin-tps", "§7TPS: ");
        zh.put("admin-online", "§7在线: ");
        zh.put("admin-memory", "§7内存: ");
        zh.put("admin-memory-used", "MB §7/ §f");
        zh.put("admin-attribute-support", "§6属性插件支持");
        zh.put("admin-attribute-desc", "§7根据 PAPI 占位符自动授予/移除称号");
        zh.put("admin-rgb", "§6RGB 渐变色头衔/后缀");
        zh.put("admin-rgb-desc", "§7需服务端支持 RGB 颜色代码");
        zh.put("admin-shop", "§6内置商店系统");
        zh.put("admin-shop-desc", "§7允许玩家使用金币购买称号");
        zh.put("admin-reload", "§e重载配置文件");
        zh.put("admin-reload-lore", "§7重新加载 config.yml");
        zh.put("admin-reload-lore2", "§7无需重启服务器");
        zh.put("admin-database", "§6数据库状态");
        zh.put("admin-db-type", "§7类型: ");
        zh.put("admin-db-status", "§7状态: ");
        zh.put("admin-db-connected", "§a已连接");
        zh.put("admin-db-disconnected", "§c未连接");
        zh.put("admin-close", "§c关闭面板");
        zh.put("admin-close-lore", "§7点击关闭");
        zh.put("admin-toggle-enabled", "§a✔ 已启用");
        zh.put("admin-toggle-disabled", "§c✘ 已禁用");
        zh.put("admin-toggle-click", "§e点击切换");
        zh.put("admin-lang-zh", "§a中文 (简体)");
        zh.put("admin-lang-en", "§eEnglish");
        zh.put("admin-lang-code", "§7语言代码: ");
        zh.put("admin-lang-current", "§a★ 当前");
        zh.put("admin-lang-switch", "§7点击切换");


        DEFAULT_MESSAGES.put("zh", zh);

        Map<String, String> en = new HashMap<>();
        en.put("no-permission", "&cYou do not have permission");
        en.put("player-only-command", "&cThis command is for players only");
        en.put("player-offline", "&cPlayer is offline");
        en.put("invalid-id", "&cID must be a number");
        en.put("id-already-owned", "&cPlayer already owns this ID (title or suffix), ID must be globally unique!");
        en.put("no-such-id", "&cPlayer does not have this ID");
        en.put("not-title", "&cThis ID is not a title, use suffixedit to modify suffix");
        en.put("not-suffix", "&cThis ID is not a suffix, use edit to modify title");
        en.put("no-title", "&cYou do not own this title");
        en.put("no-suffix", "&cYou do not own this suffix");
        en.put("none", "None");
        en.put("title", "Title");
        en.put("suffix", "Suffix");

        en.put("admin-give-usage", "Usage: /titleadmin %s <player> <ID> <display>");
        en.put("admin-edit-usage", "Usage: /titleadmin %s <player> <ID> <new display>");
        en.put("admin-take-usage", "Usage: /titleadmin %s <player> <ID>");
        en.put("admin-list-usage", "Usage: /titleadmin %s <player>");
        en.put("admin-setactive-usage", "Usage: /titleadmin %s <player> <ID>");
        en.put("admin-deactive-usage", "Usage: /titleadmin %s <player>");
        en.put("admin-give-success", "&aAdded %s to %s ID:%d display: %s");
        en.put("admin-give-fail", "&cAdd failed, check console logs");
        en.put("admin-edit-success", "&aModified %s's %s ID:%d to: %s");
        en.put("admin-edit-fail", "&cModify failed, check console logs");
        en.put("admin-take-success", "&aRemoved %s's %s ID:%d");
        en.put("admin-take-fail", "&cRemove failed (maybe active)");
        en.put("admin-list-title-header", "&6===== %s's Titles =====");
        en.put("admin-list-suffix-header", "&6===== %s's Suffixes =====");
        en.put("admin-list-empty", "&eNone");
        en.put("admin-setactive-success", "&aActivated %s's %s ID:%d");
        en.put("admin-setactive-fail", "&cActivation failed (player may not own it)");
        en.put("admin-deactive-success", "&aDeactivated %s's %s");
        en.put("admin-deactive-fail", "&cDeactivation failed");

        en.put("player-list-header", "&6===== My Titles and Suffixes =====");
        en.put("player-list-empty", "&eYou don't have any titles or suffixes yet.");
        en.put("player-list-hint", "Use /catcraft gui to open GUI");
        en.put("player-active-usage", "Usage: /catcraft active <ID>");
        en.put("player-active-success", "&aActivated title ID:%d");
        en.put("player-active-fail", "&cActivation failed");
        en.put("player-deactive-success", "&aDeactivated current title, reset to default");
        en.put("player-deactive-fail", "&cDeactivation failed");
        en.put("player-suffixactive-usage", "Usage: /catcraft suffixactive <ID>");
        en.put("player-suffixactive-success", "&aActivated suffix ID:%d");
        en.put("player-suffixactive-fail", "&cActivation failed");
        en.put("player-suffixdeactive-success", "&aDeactivated current suffix");
        en.put("player-suffixdeactive-fail", "&cDeactivation failed");
        en.put("player-remove-usage", "Usage: /catcraft remove <ID>");
        en.put("player-remove-success", "&aRemoved title/suffix ID:%d");
        en.put("player-remove-fail", "&cCannot remove active title/suffix, deactivate first");

        en.put("player-help-title", "Player Title Commands");
        en.put("help-gui", "Open GUI");
        en.put("help-list", "List all titles/suffixes");
        en.put("help-active", "Activate title");
        en.put("help-deactive", "Deactivate title");
        en.put("help-suffixactive", "Activate suffix");
        en.put("help-suffixdeactive", "Deactivate suffix");
        en.put("help-remove", "Remove title/suffix (cannot remove active)");

        en.put("admin-help-give", "Add title");
        en.put("admin-help-edit", "Edit title");
        en.put("admin-help-take", "Remove title");
        en.put("admin-help-list", "View player's titles");
        en.put("admin-help-setactive", "Activate title");
        en.put("admin-help-deactive", "Deactivate title");
        en.put("admin-help-suffixgive", "Add suffix");
        en.put("admin-help-suffixedit", "Edit suffix");
        en.put("admin-help-suffixtake", "Remove suffix");
        en.put("admin-help-suffixlist", "View player's suffixes");
        en.put("admin-help-suffixsetactive", "Activate suffix");
        en.put("admin-help-suffixdeactive", "Deactivate suffix");

        en.put("update-disabled", "Update check disabled");
        en.put("update-available", "CatCraftTitle has a new update!");
        en.put("update-current", "Current version:");
        en.put("update-latest", "Latest version:");
        en.put("update-download", "Download:");
        en.put("update-api-error", "GitHub API error code: %d");
        en.put("update-parse-error", "Failed to parse tag_name");
        en.put("update-fail", "Update check failed: %s");

        en.put("gui-home-title", "§6CatCraft Title Manager · Home");
        en.put("admin-help-title", "Admin Title Commands");
        en.put("gui-head-lore", "§7Click to manage titles");
        en.put("gui-title-btn", "§6Title Management");
        en.put("gui-title-lore1", "§7Click to view all titles");
        en.put("gui-title-lore2", "§7Current active: ");
        en.put("gui-suffix-btn", "§6Suffix Management");
        en.put("gui-suffix-lore1", "§7Click to view all suffixes");
        en.put("gui-suffix-lore2", "§7Current active: ");
        en.put("gui-close", "§cClose");
        en.put("gui-close-lore", "§7Click to close");
        en.put("gui-none", "§7None");
        en.put("gui-deleted", "§7Deleted");
        en.put("gui-title-page", "§6Title Management");
        en.put("gui-suffix-page", "§6Suffix Management");
        en.put("gui-back-home", "§eBack to Home");
        en.put("gui-back-home-lore", "§7Click to return");
        en.put("gui-current-active", "§aCurrent Active");
        en.put("gui-deactivate", "§cDeactivate");
        en.put("gui-deactivate-lore", "§7Click to deactivate current");
        en.put("gui-id", "§7ID: ");
        en.put("gui-active", "§a★ Active");
        en.put("gui-click-activate", "§7Click to activate");
        en.put("gui-previous", "§6Previous Page");
        en.put("gui-next", "§6Next Page");
        en.put("banner-author", "Plugin Author: &eQingNiaoQaQ &7(CatCraft Team)");
        en.put("banner-server", "Server:");
        en.put("banner-database", "Database:");
        en.put("banner-papi", "PlaceholderAPI: ");
        en.put("banner-connected", "Connected");
        en.put("banner-failed", "Failed");
        en.put("banner-found", "Found");
        en.put("banner-not-found", "Not Found");
        en.put("banner-disconnected", "Disconnected");

        // Admin help full lines
        en.put("admin-help-give-line", "§e/titleadmin give §f<player> <ID> <display> §7- Add title");
        en.put("admin-help-edit-line", "§e/titleadmin edit §f<player> <ID> <new display> §7- Edit title");
        en.put("admin-help-take-line", "§e/titleadmin take §f<player> <ID> §7- Remove title");
        en.put("admin-help-list-line", "§e/titleadmin list §f<player> §7- View player's titles");
        en.put("admin-help-setactive-line", "§e/titleadmin setactive §f<player> <ID> §7- Activate title");
        en.put("admin-help-deactive-line", "§e/titleadmin deactive §f<player> §7- Deactivate title");
        en.put("admin-help-suffixgive-line", "§e/titleadmin suffixgive §f<player> <ID> <display> §7- Add suffix");
        en.put("admin-help-suffixedit-line", "§e/titleadmin suffixedit §f<player> <ID> <new display> §7- Edit suffix");
        en.put("admin-help-suffixtake-line", "§e/titleadmin suffixtake §f<player> <ID> §7- Remove suffix");
        en.put("admin-help-suffixlist-line", "§e/titleadmin suffixlist §f<player> §7- View player's suffixes");
        en.put("admin-help-suffixsetactive-line", "§e/titleadmin suffixsetactive §f<player> <ID> §7- Activate suffix");
        en.put("admin-help-suffixdeactive-line", "§e/titleadmin suffixdeactive §f<player> §7- Deactivate suffix");

        // Player help full lines
        en.put("player-help-gui-line", "§e/catcraft gui §7- Open GUI");
        en.put("player-help-list-line", "§e/catcraft list §7- List all titles/suffixes");
        en.put("player-help-active-line", "§e/catcraft active §f<ID> §7- Activate title");
        en.put("player-help-deactive-line", "§e/catcraft deactive §7- Deactivate title");
        en.put("player-help-suffixactive-line", "§e/catcraft suffixactive §f<ID> §7- Activate suffix");
        en.put("player-help-suffixdeactive-line", "§e/catcraft suffixdeactive §7- Deactivate suffix");
        en.put("player-help-remove-line", "§e/catcraft remove §f<ID> §7- Remove title/suffix (cannot remove active)");
        en.put("player-help-shop-line", "§e/catcraft shop §7- Open shop");

        // Shop related
        en.put("gui-shop-title", "§6CatCraft Title Shop");
        en.put("gui-shop-lore", "§7Click to enter shop");
        en.put("gui-shop-balance-hint", "§7Your balance");
        en.put("gui-shop-price", "Price:");
        en.put("gui-shop-owned", "Owned");
        en.put("gui-shop-click-buy", "Click to buy");
        en.put("gui-balance", "§6Balance: §a");
        en.put("shop-disabled", "§cShop system is disabled");
        en.put("shop-purchase-success", "§aYou purchased title: %s");
        en.put("shop-purchase-fail", "§cPurchase failed, insufficient balance, already owned, or error");

        en.put("gui-shop-filter-all", "All");
        en.put("gui-shop-filter-title", "Titles");
        en.put("gui-shop-filter-suffix", "Suffixes");
        en.put("gui-shop-click-filter", "§7Click to select");
        en.put("gui-shop-id", "ID:");
        en.put("gui-signin-btn", "Daily Signin");
        en.put("gui-signin-done", "Signed in today");
        en.put("gui-signin-lore", "Sign in to get §6%d coins");
        en.put("gui-signin-already", "Already signed in today");
        en.put("gui-signin-success", "§aSignin successful! Got %d coins");
        en.put("gui-signin-fail", "§cSignin failed, please try again later");

        en.put("admin-help-shop-title", "Shop Management");
        en.put("admin-help-shop-add-line", "§e/titleadmin shop add §f<ID> <type(0/1)> <price> <display> §7- Add item");
        en.put("admin-help-shop-remove-line", "§e/titleadmin shop remove §f<ID> <type> §7- Remove item");
        en.put("admin-help-shop-setprice-line", "§e/titleadmin shop setprice §f<ID> <type> <new price> §7- Set price");
        en.put("admin-help-shop-list-line", "§e/titleadmin shop list §7- List all items");
        en.put("admin-help-shop-givebalance-line", "§e/titleadmin shop givebalance §f<player> <amount> §7- Add coins");
        en.put("admin-help-shop-setbalance-line", "§e/titleadmin shop setbalance §f<player> <amount> §7- Set coins");
        en.put("admin-help-shop-toggle-line", "§e/titleadmin shop toggle §7- Toggle shop (edit config)");

        en.put("gui-shop-home-title", "§6CatCraft Title Shop");
        en.put("gui-shop-back-home", "§eBack to Shop Home");
        en.put("gui-shop-back-home-lore", "§7Click to return");
        en.put("gui-shop-title-list-title", "§6Title Shop");
        en.put("gui-shop-title-list-suffix", "§6Suffix Shop");
        en.put("gui-shop-type", "Type:");
        en.put("gui-shop-list-hint", "§7Click to browse");

        en.put("admin-panel-title", "§6CatCraft Admin Panel");
        en.put("admin-author", "§7Author: §eQingNiaoQaQ");
        en.put("admin-papi-status", "§7PlaceholderAPI: ");
        en.put("admin-papi-found", "§a✔");
        en.put("admin-papi-notfound", "§c✘");
        en.put("admin-server-status", "§6Server Status");
        en.put("admin-tps", "§7TPS: ");
        en.put("admin-online", "§7Online: ");
        en.put("admin-memory", "§7Memory: ");
        en.put("admin-memory-used", "MB §7/ §f");
        en.put("admin-attribute-support", "§6Attribute Plugin Support");
        en.put("admin-attribute-desc", "§7Auto grant/remove titles based on PAPI placeholders");
        en.put("admin-rgb", "§6RGB Gradient Titles/Suffixes");
        en.put("admin-rgb-desc", "§7Requires server support for RGB color codes");
        en.put("admin-shop", "§6Built-in Shop System");
        en.put("admin-shop-desc", "§7Allow players to buy titles with coins");
        en.put("admin-reload", "§eReload Config");
        en.put("admin-reload-lore", "§7Reload config.yml");
        en.put("admin-reload-lore2", "§7No server restart needed");
        en.put("admin-database", "§6Database Status");
        en.put("admin-db-type", "§7Type: ");
        en.put("admin-db-status", "§7Status: ");
        en.put("admin-db-connected", "§aConnected");
        en.put("admin-db-disconnected", "§cDisconnected");
        en.put("admin-close", "§cClose Panel");
        en.put("admin-close-lore", "§7Click to close");
        en.put("admin-toggle-enabled", "§a✔ Enabled");
        en.put("admin-toggle-disabled", "§c✘ Disabled");
        en.put("admin-toggle-click", "§eClick to toggle");
        en.put("admin-lang-zh", "§aChinese (Simplified)");
        en.put("admin-lang-en", "§eEnglish");
        en.put("admin-lang-code", "§7Language code: ");
        en.put("admin-lang-current", "§a★ Current");
        en.put("admin-lang-switch", "§7Click to switch");

        DEFAULT_MESSAGES.put("en", en);
    }

    private MessageManager(JavaPlugin plugin) {
        String lang = plugin.getConfig().getString("language", "zh");
        Map<String, String> defaults = DEFAULT_MESSAGES.getOrDefault(lang, DEFAULT_MESSAGES.get("zh"));
        messages.putAll(defaults);
    }

    public static void init(JavaPlugin plugin) {
        instance = new MessageManager(plugin);
    }

    public static String get(String key) {
        String msg = instance.messages.getOrDefault(key, "§cMissing message: " + key);
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static String get(String key, Object... args) {
        String msg = instance.messages.getOrDefault(key, "§cMissing message: " + key);
        try {
            msg = String.format(msg, args);
        } catch (Exception ignored) {}
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static void setLanguage(String lang) {
        if (instance == null) return;
        Map<String, String> defaults = DEFAULT_MESSAGES.getOrDefault(lang, DEFAULT_MESSAGES.get("zh"));
        instance.messages.clear();
        instance.messages.putAll(defaults);
        CatCraftTitlePlugin.getInstance().getConfig().set("language", lang);
        CatCraftTitlePlugin.getInstance().saveConfig();
    }
}