package catcraft.title;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class AdminGUI {

    private static final String TITLE = MessageManager.get("admin-panel-title");

    public static class Holder implements InventoryHolder {
        private final UUID playerUUID;
        public Holder(UUID uuid) { this.playerUUID = uuid; }
        public UUID getPlayerUUID() { return playerUUID; }
        @Override public Inventory getInventory() { return null; }
    }

    public static void open(Player player) {
        if (!player.hasPermission("catcraft.admin")) {
            player.sendMessage(ColorUtil.color(MessageManager.get("no-permission")));
            return;
        }

        Inventory inv = Bukkit.createInventory(new Holder(player.getUniqueId()), 54, TITLE);
        CatCraftTitlePlugin plugin = CatCraftTitlePlugin.getInstance();


        boolean hasPAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
        String papiStatus = hasPAPI ? MessageManager.get("admin-papi-found") : MessageManager.get("admin-papi-notfound");
        ItemStack logo = createItem(Material.BOOK,
                "§6CatCraftTitle §bv" + plugin.getDescription().getVersion(),
                Arrays.asList(
                        MessageManager.get("admin-author"),
                        MessageManager.get("admin-papi-status") + papiStatus
                ));
        inv.setItem(0, logo);


        double tps = getTPS();
        String tpsColor = tps >= 18 ? "§a" : (tps >= 15 ? "§e" : "§c");
        Runtime runtime = Runtime.getRuntime();
        long usedMem = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
        long maxMem = runtime.maxMemory() / 1024 / 1024;

        ItemStack status = createItem(Material.COMPASS,
                MessageManager.get("admin-server-status"),
                Arrays.asList(
                        MessageManager.get("admin-tps") + tpsColor + String.format("%.2f", tps),
                        MessageManager.get("admin-online") + "§a" + Bukkit.getOnlinePlayers().size() + " §7/ §f" + Bukkit.getMaxPlayers(),
                        MessageManager.get("admin-memory") + "§e" + usedMem + MessageManager.get("admin-memory-used") + maxMem + "MB"
                ));
        inv.setItem(4, status);


        boolean attr = plugin.isAttributeSupportEnabled();
        inv.setItem(9, createToggleItem(
                Material.COMPARATOR,
                MessageManager.get("admin-attribute-support"),
                attr,
                MessageManager.get("admin-attribute-desc")
        ));

        boolean rgb = plugin.getConfig().getBoolean("settings.enable-rgb", false);
        inv.setItem(10, createToggleItem(
                Material.ENCHANTING_TABLE,
                MessageManager.get("admin-rgb"),
                rgb,
                MessageManager.get("admin-rgb-desc")
        ));

        boolean shop = plugin.getConfig().getBoolean("shop.enabled", false);
        inv.setItem(11, createToggleItem(
                Material.CHEST,
                MessageManager.get("admin-shop"),
                shop,
                MessageManager.get("admin-shop-desc")
        ));


        String currentLang = plugin.getConfig().getString("language", "zh");
        inv.setItem(18, createLangItem(Material.WRITTEN_BOOK,
                MessageManager.get("admin-lang-zh"), "zh", currentLang.equals("zh")));
        inv.setItem(19, createLangItem(Material.WRITTEN_BOOK,
                MessageManager.get("admin-lang-en"), "en", currentLang.equals("en")));


        inv.setItem(31, createItem(Material.HOPPER,
                MessageManager.get("admin-reload"),
                Arrays.asList(
                        MessageManager.get("admin-reload-lore"),
                        MessageManager.get("admin-reload-lore2")
                )));


        DatabaseManager db = plugin.getDatabase();
        boolean connected = db.isConnected();
        String dbStatus = connected ? MessageManager.get("admin-db-connected") : MessageManager.get("admin-db-disconnected");
        inv.setItem(40, createItem(Material.ENDER_CHEST,
                MessageManager.get("admin-database"),
                Arrays.asList(
                        MessageManager.get("admin-db-type") + "§f" + db.getDatabaseType(),
                        MessageManager.get("admin-db-status") + dbStatus
                )));

        inv.setItem(53, createItem(Material.RED_STAINED_GLASS_PANE,
                MessageManager.get("admin-close"),
                Arrays.asList(MessageManager.get("admin-close-lore"))));

        for (int i = 0; i < 54; i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, createGlass());
            }
        }

        player.openInventory(inv);
    }

    private static ItemStack createToggleItem(Material mat, String name, boolean enabled, String desc) {
        String status = enabled ? MessageManager.get("admin-toggle-enabled") : MessageManager.get("admin-toggle-disabled");
        return createItem(mat, name, Arrays.asList(
                "§7状态: " + status,
                "§7" + desc,
                MessageManager.get("admin-toggle-click")
        ));
    }

    private static ItemStack createLangItem(Material mat, String name, String langCode, boolean isCurrent) {
        String status = isCurrent ? MessageManager.get("admin-lang-current") : MessageManager.get("admin-lang-switch");
        return createItem(mat, name, Arrays.asList(
                MessageManager.get("admin-lang-code") + langCode,
                status
        ));
    }

    private static ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (lore != null) meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack createGlass() {
        try {
            return createItem(Material.GRAY_STAINED_GLASS_PANE, " ", null);
        } catch (NoSuchFieldError | Exception e) {
            return createItem(Material.GLASS, " ", null);
        }
    }

    private static double getTPS() {
        return 20.0;
    }
}