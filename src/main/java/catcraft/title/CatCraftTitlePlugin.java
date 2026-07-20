package catcraft.title;

import catcraft.title.auto.AutoTitleManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CatCraftTitlePlugin extends JavaPlugin {
    private static CatCraftTitlePlugin instance;
    private DatabaseManager database;
    private TitleManager manager;
    private ShopManager shopManager;
    private boolean attributeSupportEnabled;
    private AutoTitleManager autoTitleManager;

    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        MessageManager.init(this);

        database = new DatabaseManager(this);
        database.connect();

        manager = new TitleManager(database);

        boolean shopEnabled = getConfig().getBoolean("shop.enabled", false);
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
        getCommand("catcraft").setExecutor(playerCmd);
        getCommand("catcraft").setTabCompleter(playerCmd);

        TitleAdminCommand adminCmd = new TitleAdminCommand(manager);
        getCommand("titleadmin").setExecutor(adminCmd);
        getCommand("titleadmin").setTabCompleter(adminCmd);

        attributeSupportEnabled = getConfig().getBoolean("settings.attribute-support", false);
        if (attributeSupportEnabled) {
            autoTitleManager = new AutoTitleManager(manager, database, this);
            getServer().getPluginManager().registerEvents(autoTitleManager, this);
            autoTitleManager.startScheduler();
            getLogger().info("属性插件支持已启用，自动称号系统已启动。");
        }

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
        if (autoTitleManager != null) autoTitleManager.stopScheduler();
        if (database != null) database.disconnect();
    }

    public void reloadPlugin() {
        reloadConfig();
        boolean shopEnabled = getConfig().getBoolean("shop.enabled", false);
        toggleShop(shopEnabled);
        boolean rgbEnabled = getConfig().getBoolean("settings.enable-rgb", false);
        toggleRGB(rgbEnabled);
        boolean attrEnabled = getConfig().getBoolean("settings.attribute-support", false);
        toggleAttributeSupport(attrEnabled);
        String lang = getConfig().getString("language", "zh");
        MessageManager.setLanguage(lang);
        getLogger().info("配置已重载。");
    }


    public void toggleRGB(boolean enable) {
        getConfig().set("settings.enable-rgb", enable);
        saveConfig();
        manager.setRgbEnabled(enable);
        for (Player p : Bukkit.getOnlinePlayers()) {
            manager.load(p);
        }
    }

    public void toggleShop(boolean enable) {
        getConfig().set("shop.enabled", enable);
        saveConfig();
        this.shopManager = new ShopManager(
                database,
                manager,
                enable,
                getConfig().getInt("shop.signin-reward", 30)
        );
    }

    public void toggleAttributeSupport(boolean enable) {
        getConfig().set("settings.attribute-support", enable);
        saveConfig();
        this.attributeSupportEnabled = enable;
        if (enable) {
            if (autoTitleManager == null) {
                autoTitleManager = new AutoTitleManager(manager, database, this);
                getServer().getPluginManager().registerEvents(autoTitleManager, this);
            }
            autoTitleManager.startScheduler();
            getLogger().info("属性插件支持已开启");
        } else {
            if (autoTitleManager != null) {
                autoTitleManager.stopScheduler();
                autoTitleManager.clearAllAutoTitles();
            }
            getLogger().info("属性插件支持已关闭");
        }
    }

    public boolean isAttributeSupportEnabled() {
        return attributeSupportEnabled;
    }

    public AutoTitleManager getAutoTitleManager() {
        return autoTitleManager;
    }


    public static CatCraftTitlePlugin getInstance() { return instance; }
    public DatabaseManager getDatabase() { return database; }
    public TitleManager getManager() { return manager; }
    public ShopManager getShopManager() { return shopManager; }
}