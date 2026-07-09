package catcraft.title;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class CatCraftTitlePlugin extends JavaPlugin {
    private static CatCraftTitlePlugin instance;
    private DatabaseManager database;
    private TitleManager manager;
    private ShopManager shopManager;

    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        MessageManager.init(this);

        database = new DatabaseManager(this);
        database.connect();

        manager = new TitleManager(database);

        boolean shopEnabled = getConfig().getBoolean("shop.enabled", true);
        int signinReward = getConfig().getInt("shop.signin-reward", 30);
        shopManager = new ShopManager(database, manager, shopEnabled, signinReward);

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(manager), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(manager), this);
        getServer().getPluginManager().registerEvents(new ChatListener(manager), this);
        getServer().getPluginManager().registerEvents(new TitleGUIListener(), this);

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new CatCraftExpansion(manager).register();
            getLogger().info("PlaceholderAPI 扩展已注册。");
        } else {
            getLogger().warning("PlaceholderAPI 未启用，跳过占位符扩展注册。");
        }

        TitlePlayerCommand playerCmd = new TitlePlayerCommand(manager);
        getCommand("title").setExecutor(playerCmd);
        getCommand("title").setTabCompleter(playerCmd);

        TitleAdminCommand adminCmd = new TitleAdminCommand(manager);
        getCommand("titleadmin").setExecutor(adminCmd);
        getCommand("titleadmin").setTabCompleter(adminCmd);

        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            getLogger().info("检测到 Folia 服务端，已启用区域调度兼容模式。");
        } catch (ClassNotFoundException ignored) {}

        printBanner();

        boolean checkUpdate = getConfig().getBoolean("settings.check-update", true);
        new UpdateChecker(this, checkUpdate).check();

        getLogger().info("CatCraftTitle 插件启动成功");
    }

    private void printBanner() {
        String version = getDescription().getVersion();
        String serverName = Bukkit.getName();
        String serverVersion = Bukkit.getVersion();
        String dbType = database.isConnected() ? database.getDatabaseType() : MessageManager.get("banner-disconnected");
        String dbStatus = database.isConnected() ? MessageManager.get("banner-connected") : MessageManager.get("banner-failed");
        boolean hasPAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;

        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(ColorUtil.color("&6+------------------------------------------+"));
        Bukkit.getConsoleSender().sendMessage(ColorUtil.color("&6|  &2CatCraftTitle &bV" + version + "  &6|"));
        Bukkit.getConsoleSender().sendMessage(ColorUtil.color("&6|  " + MessageManager.get("banner-author") + " &6|"));
        Bukkit.getConsoleSender().sendMessage(ColorUtil.color("&6|  " + MessageManager.get("banner-server") + " &6" + serverName + " &7" + serverVersion + " &6|"));
        Bukkit.getConsoleSender().sendMessage(ColorUtil.color("&6|  " + MessageManager.get("banner-database") + " &a" + dbType + " &7(" + dbStatus + ") &6|"));
        Bukkit.getConsoleSender().sendMessage(ColorUtil.color("&6|  " + MessageManager.get("banner-papi") + (hasPAPI ? "&a" + MessageManager.get("banner-found") : "&c" + MessageManager.get("banner-not-found")) + " &6|"));
        Bukkit.getConsoleSender().sendMessage(ColorUtil.color("&6+------------------------------------------+"));
        Bukkit.getConsoleSender().sendMessage("");
    }

    public void onDisable() {
        if (database != null) database.disconnect();
    }

    public void reloadPlugin() {
        reloadConfig();
    }

    public static CatCraftTitlePlugin getInstance() { return instance; }
    public DatabaseManager getDatabase() { return database; }
    public TitleManager getManager() { return manager; }
    public ShopManager getShopManager() { return shopManager; }
}