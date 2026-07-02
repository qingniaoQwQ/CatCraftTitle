package catcraft.title;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class CatCraftTitlePlugin extends JavaPlugin {

    private static CatCraftTitlePlugin instance;
    private DatabaseManager database;
    private TitleManager manager;
    private boolean useMySQL;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        useMySQL = getConfig().getBoolean("mysql.enabled", false);
        if (useMySQL) {
            getLogger().info("✔ MySQL已启用（跨服模式）");
        } else {
            getLogger().warning("⚠ MySQL未启用，使用本地模式（数据仅限本服）");
        }

        database = new DatabaseManager(this, useMySQL);
        database.connect();

        manager = new TitleManager(database);

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(manager), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(manager), this);
        getServer().getPluginManager().registerEvents(new ChatListener(manager), this);

        new CatCraftExpansion(manager).register();

        TitlePlayerCommand playerCmd = new TitlePlayerCommand(manager);
        getCommand("title").setExecutor(playerCmd);
        getCommand("title").setTabCompleter(playerCmd);

        TitleAdminCommand adminCmd = new TitleAdminCommand(manager);
        getCommand("titleadmin").setExecutor(adminCmd);
        getCommand("titleadmin").setTabCompleter(adminCmd);

        // ========== 启动横幅 ==========
        printBanner();

        getLogger().info("CatCraftTitle 插件启动成功");
    }

    private void printBanner() {
        String version = getDescription().getVersion();
        String serverName = Bukkit.getName();
        String serverVersion = Bukkit.getVersion();
        String dbType = database.isConnected() ? database.getDatabaseType() : "未连接";
        String dbStatus = database.isConnected() ? "已连接" : "连接失败";

        MiniMessage mm = MiniMessage.miniMessage();

        // 构建横幅内容
        Component top = mm.deserialize("<color:#FFA500>+------------------------------------------+</color>");
        Component line1 = mm.deserialize(
                "|  <color:#008B00>CatCraftTitle</color> <color:#00BFFF>V" + version + "</color>  |"
        );
        Component line2 = mm.deserialize(
                "|  插件作者：<color:#FFD700>QingNiaoQaQ</color> <gray>(CatCraft Team)</gray>  |"
        );
        Component line3 = mm.deserialize(
                "|  服务器: <color:#FFA500>" + serverName + "</color> <gray>" + serverVersion + "</gray> |"
        );
        Component line4 = mm.deserialize(
                "|  数据库: <color:#00FF00>" + dbType + "</color> <gray>(" + dbStatus + ")</gray> |"
        );
        boolean hasPAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
        Component line5 = mm.deserialize(
                "|  PlaceholderAPI: " + (hasPAPI ? "<color:#00FF00>已找到</color>" : "<color:#FF4444>未找到</color>") + " |"
        );
        Component bottom = mm.deserialize("<color:#FFA500>+------------------------------------------+</color>");

        // 发送
        Bukkit.getConsoleSender().sendMessage(Component.text(""));
        Bukkit.getConsoleSender().sendMessage(top);
        Bukkit.getConsoleSender().sendMessage(line1);
        Bukkit.getConsoleSender().sendMessage(line2);
        Bukkit.getConsoleSender().sendMessage(line3);
        Bukkit.getConsoleSender().sendMessage(line4);
        Bukkit.getConsoleSender().sendMessage(line5);
        Bukkit.getConsoleSender().sendMessage(bottom);
        Bukkit.getConsoleSender().sendMessage(Component.text(""));
    }

    @Override
    public void onDisable() {
        if (database != null) database.disconnect();
    }

    public void reloadPlugin() {
        reloadConfig();
    }

    public static CatCraftTitlePlugin getInstance() {
        return instance;
    }

    public DatabaseManager getDatabase() {
        return database;
    }

    public TitleManager getManager() {
        return manager;
    }
}