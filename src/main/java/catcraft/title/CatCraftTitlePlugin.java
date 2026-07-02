package catcraft.title;

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

        printBanner();

        boolean checkUpdate = getConfig().getBoolean("settings.check-update", true);
        new UpdateChecker(this, checkUpdate).check();

        getLogger().info("CatCraftTitle 插件启动成功");
    }

    private void printBanner() {
        String version = getDescription().getVersion();
        String serverName = Bukkit.getName();
        String serverVersion = Bukkit.getVersion();
        String dbType = database.isConnected() ? database.getDatabaseType() : "未连接";
        String dbStatus = database.isConnected() ? "已连接" : "连接失败";
        boolean hasPAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;

        String top = ColorUtil.color("&6+------------------------------------------+");
        String line1 = ColorUtil.color("&6|  &2CatCraftTitle &bV" + version + "  &6|");
        String line2 = ColorUtil.color("&6|  插件作者：&eQingNiaoQaQ &7(CatCraft Team)  &6|");
        String line3 = ColorUtil.color("&6|  服务器: &6" + serverName + " &7" + serverVersion + " &6|");
        String line4 = ColorUtil.color("&6|  数据库: &a" + dbType + " &7(" + dbStatus + ") &6|");
        String line5 = ColorUtil.color("&6|  PlaceholderAPI: " + (hasPAPI ? "&a已找到" : "&c未找到") + " &6|");
        String bottom = ColorUtil.color("&6+------------------------------------------+");

        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(top);
        Bukkit.getConsoleSender().sendMessage(line1);
        Bukkit.getConsoleSender().sendMessage(line2);
        Bukkit.getConsoleSender().sendMessage(line3);
        Bukkit.getConsoleSender().sendMessage(line4);
        Bukkit.getConsoleSender().sendMessage(line5);
        Bukkit.getConsoleSender().sendMessage(bottom);
        Bukkit.getConsoleSender().sendMessage("");
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