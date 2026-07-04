package catcraft.title;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class TitleGUI {

    private static final int PER_PAGE = 36;

    public enum PageType { HOME, TITLE, SUFFIX }

    public static void openHome(Player player) {
        Inventory inv = Bukkit.createInventory(new GUIHolder(player.getUniqueId(), PageType.HOME, 0), 54, MessageManager.get("gui-home-title"));

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(player);
        meta.setDisplayName("§a" + player.getName());
        meta.setLore(Collections.singletonList(MessageManager.get("gui-head-lore")));
        head.setItemMeta(meta);
        inv.setItem(4, head);

        ItemStack titleBtn = createItem(Material.BOOK,
                MessageManager.get("gui-title-btn"),
                Arrays.asList(MessageManager.get("gui-title-lore1"), MessageManager.get("gui-title-lore2") + getActiveTitleDisplay(player)));
        inv.setItem(20, titleBtn);

        ItemStack suffixBtn = createItem(Material.FEATHER,
                MessageManager.get("gui-suffix-btn"),
                Arrays.asList(MessageManager.get("gui-suffix-lore1"), MessageManager.get("gui-suffix-lore2") + getActiveSuffixDisplay(player)));
        inv.setItem(24, suffixBtn);

        inv.setItem(53, createItem(Material.RED_STAINED_GLASS_PANE,
                MessageManager.get("gui-close"),
                Collections.singletonList(MessageManager.get("gui-close-lore"))));

        for (int i = 0; i < 54; i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, createEmptyGlass());
            }
        }

        player.openInventory(inv);
    }

    private static String getActiveTitleDisplay(Player p) {
        TitleManager mgr = CatCraftTitlePlugin.getInstance().getManager();
        int id = mgr.getActiveTitleId(p);
        if (id == -1) return MessageManager.get("gui-none");
        Map<Integer, String> titles = mgr.getOwnedTitles(p);
        return titles.containsKey(id) ? ColorUtil.color(titles.get(id)) : MessageManager.get("gui-deleted");
    }

    private static String getActiveSuffixDisplay(Player p) {
        TitleManager mgr = CatCraftTitlePlugin.getInstance().getManager();
        int id = mgr.getActiveSuffixId(p);
        if (id == -1) return MessageManager.get("gui-none");
        Map<Integer, String> suffixes = mgr.getOwnedSuffixes(p);
        return suffixes.containsKey(id) ? ColorUtil.color(suffixes.get(id)) : MessageManager.get("gui-deleted");
    }

    public static void openTitlePage(Player player, int page) {
        TitleManager mgr = CatCraftTitlePlugin.getInstance().getManager();
        Map<Integer, String> all = mgr.getOwnedTitles(player);
        openTypePage(player, page, PageType.TITLE, all, MessageManager.get("gui-title-page"));
    }

    public static void openSuffixPage(Player player, int page) {
        TitleManager mgr = CatCraftTitlePlugin.getInstance().getManager();
        Map<Integer, String> all = mgr.getOwnedSuffixes(player);
        openTypePage(player, page, PageType.SUFFIX, all, MessageManager.get("gui-suffix-page"));
    }

    private static void openTypePage(Player player, int page, PageType type, Map<Integer, String> all, String title) {
        List<Map.Entry<Integer, String>> entries = new ArrayList<>(all.entrySet());
        entries.sort(Comparator.comparingInt(Map.Entry::getKey));

        int total = entries.size();
        int maxPage = (total == 0) ? 1 : (int) Math.ceil((double) total / PER_PAGE);
        if (page < 0) page = 0;
        if (page >= maxPage) page = maxPage - 1;

        int start = page * PER_PAGE;
        int end = Math.min(start + PER_PAGE, total);
        List<Map.Entry<Integer, String>> sub = entries.subList(start, end);

        Inventory inv = Bukkit.createInventory(new GUIHolder(player.getUniqueId(), type, page), 54,
                title + " (" + (page+1) + "/" + maxPage + ")");

        inv.setItem(0, createItem(Material.ARROW,
                MessageManager.get("gui-back-home"),
                Collections.singletonList(MessageManager.get("gui-back-home-lore"))));

        int activeId = (type == PageType.TITLE) ?
                CatCraftTitlePlugin.getInstance().getManager().getActiveTitleId(player) :
                CatCraftTitlePlugin.getInstance().getManager().getActiveSuffixId(player);
        String activeDisplay = MessageManager.get("gui-none");
        if (activeId != -1 && all.containsKey(activeId)) {
            activeDisplay = ColorUtil.color(all.get(activeId));
        }
        inv.setItem(4, createItem(Material.NAME_TAG,
                MessageManager.get("gui-current-active"),
                Collections.singletonList(activeDisplay)));

        inv.setItem(8, createItem(Material.BARRIER,
                MessageManager.get("gui-deactivate"),
                Collections.singletonList(MessageManager.get("gui-deactivate-lore"))));

        for (int i = 0; i < sub.size(); i++) {
            int slot = 9 + i;
            Map.Entry<Integer, String> entry = sub.get(i);
            int id = entry.getKey();
            String display = entry.getValue();
            boolean isActive = (id == activeId);
            List<String> lore = Arrays.asList(
                    MessageManager.get("gui-id") + id,
                    isActive ? MessageManager.get("gui-active") : MessageManager.get("gui-click-activate")
            );
            Material mat = isActive ? Material.GOLDEN_APPLE : Material.PAPER;
            inv.setItem(slot, createItem(mat, ColorUtil.color(display), lore));
        }

        for (int i = 9 + sub.size(); i < 45; i++) {
            inv.setItem(i, createEmptyGlass());
        }

        if (page > 0) inv.setItem(45, createItem(Material.ARROW,
                MessageManager.get("gui-previous"), null));
        else inv.setItem(45, createEmptyGlass());

        inv.setItem(49, createItem(Material.PAPER,
                "§e" + (page+1) + "/" + maxPage, null));

        if (page < maxPage - 1) inv.setItem(53, createItem(Material.ARROW,
                MessageManager.get("gui-next"), null));
        else inv.setItem(53, createEmptyGlass());

        for (int i = 46; i <= 48; i++) inv.setItem(i, createEmptyGlass());
        for (int i = 50; i <= 52; i++) inv.setItem(i, createEmptyGlass());

        player.openInventory(inv);
    }

    private static ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if (lore != null) meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack createEmptyGlass() {
        return createItem(Material.GRAY_STAINED_GLASS_PANE, " ", null);
    }

    public static class GUIHolder implements InventoryHolder {
        private final UUID playerUUID;
        private final PageType type;
        private final int page;

        public GUIHolder(UUID uuid, PageType type, int page) {
            this.playerUUID = uuid;
            this.type = type;
            this.page = page;
        }

        public UUID getPlayerUUID() { return playerUUID; }
        public PageType getType() { return type; }
        public int getPage() { return page; }

        @Override
        public Inventory getInventory() { return null; }
    }
}