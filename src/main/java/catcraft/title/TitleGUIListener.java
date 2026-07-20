package catcraft.title;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.*;

public class TitleGUIListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player p = (Player) e.getWhoClicked();
        Inventory inv = e.getInventory();
        if (inv.getHolder() == null) return;


        if (inv.getHolder() instanceof TitleGUI.GUIHolder) {
            e.setCancelled(true);
            TitleGUI.GUIHolder holder = (TitleGUI.GUIHolder) inv.getHolder();
            UUID uuid = holder.getPlayerUUID();
            if (!uuid.equals(p.getUniqueId())) return;

            TitleManager mgr = CatCraftTitlePlugin.getInstance().getManager();
            ShopManager shop = CatCraftTitlePlugin.getInstance().getShopManager();
            int slot = e.getRawSlot();
            TitleGUI.PageType type = holder.getType();
            int page = holder.getPage();
            int filter = holder.getFilter();

            if (type == TitleGUI.PageType.HOME) {
                if (slot == 4) {
                    TitleGUI.openHome(p);
                } else if (slot == 20) {
                    TitleGUI.openTitlePage(p, 0);
                } else if (slot == 24) {
                    TitleGUI.openSuffixPage(p, 0);
                } else if (slot == 22 && shop.isEnabled()) {
                    TitleGUI.openShopHome(p);
                } else if (slot == 53) {
                    p.closeInventory();
                }
                return;
            }

            if (type == TitleGUI.PageType.TITLE || type == TitleGUI.PageType.SUFFIX) {
                if (slot == 0) {
                    TitleGUI.openHome(p);
                    return;
                }
                if (slot == 8) {
                    if (type == TitleGUI.PageType.TITLE) {
                        mgr.deactivateTitle(p);
                    } else {
                        mgr.deactivateSuffix(p);
                    }
                    refreshPage(p, type, page);
                    return;
                }
                if (slot >= 9 && slot < 45) {
                    int index = slot - 9;
                    Map<Integer, String> map = (type == TitleGUI.PageType.TITLE) ? mgr.getOwnedTitles(p) : mgr.getOwnedSuffixes(p);
                    List<Map.Entry<Integer, String>> list = new ArrayList<>(map.entrySet());
                    list.sort(Comparator.comparingInt(Map.Entry::getKey));
                    int start = page * 36;
                    if (start + index < list.size()) {
                        int id = list.get(start + index).getKey();
                        if (type == TitleGUI.PageType.TITLE) {
                            mgr.activateTitle(p, id);
                        } else {
                            mgr.activateSuffix(p, id);
                        }
                        refreshPage(p, type, page);
                    }
                    return;
                }
                if (slot == 45) refreshPage(p, type, page - 1);
                else if (slot == 53) refreshPage(p, type, page + 1);
                return;
            }

            if (type == TitleGUI.PageType.SHOP_HOME) {
                if (slot == 0) {
                    TitleGUI.openHome(p);
                    return;
                }
                if (slot == 20) {
                    TitleGUI.openShopListPage(p, 0, 0);
                    return;
                } else if (slot == 24) {
                    TitleGUI.openShopListPage(p, 0, 1);
                    return;
                } else if (slot == 22) {
                    if (shop.signin(p)) {
                        p.sendMessage(ColorUtil.color(MessageManager.get("gui-signin-success", shop.getSigninReward())));
                    } else {
                        p.sendMessage(ColorUtil.color(MessageManager.get("gui-signin-fail")));
                    }
                    TitleGUI.openShopHome(p);
                    return;
                }
                return;
            }

            if (type == TitleGUI.PageType.SHOP_LIST) {
                if (slot == 0) {
                    TitleGUI.openShopHome(p);
                    return;
                }
                if (slot >= 9 && slot < 45) {
                    int index = slot - 9;
                    List<ShopItem> items = shop.getShopItemsByType(filter);
                    int start = page * 36;
                    if (start + index < items.size()) {
                        ShopItem item = items.get(start + index);
                        if (shop.purchase(p, item.getId(), item.getType())) {
                            p.sendMessage(ColorUtil.color(MessageManager.get("shop-purchase-success", item.getDisplay())));
                        } else {
                            p.sendMessage(ColorUtil.color(MessageManager.get("shop-purchase-fail")));
                        }
                        TitleGUI.openShopListPage(p, page, filter);
                    }
                    return;
                }
                if (slot == 45) TitleGUI.openShopListPage(p, page - 1, filter);
                else if (slot == 53) TitleGUI.openShopListPage(p, page + 1, filter);
            }
            return;
        }


        if (inv.getHolder() instanceof AdminGUI.Holder) {
            e.setCancelled(true);
            AdminGUI.Holder holder = (AdminGUI.Holder) inv.getHolder();
            if (!holder.getPlayerUUID().equals(p.getUniqueId())) return;
            if (!p.hasPermission("catcraft.admin")) {
                p.closeInventory();
                p.sendMessage(ColorUtil.color(MessageManager.get("no-permission")));
                return;
            }

            CatCraftTitlePlugin plugin = CatCraftTitlePlugin.getInstance();
            int slot = e.getRawSlot();

            if (slot == 9) {
                boolean current = plugin.isAttributeSupportEnabled();
                plugin.toggleAttributeSupport(!current);
                AdminGUI.open(p);
                return;
            }
            if (slot == 10) {
                boolean current = plugin.getConfig().getBoolean("settings.enable-rgb", false);
                plugin.toggleRGB(!current);
                AdminGUI.open(p);
                return;
            }
            if (slot == 11) {
                boolean current = plugin.getConfig().getBoolean("shop.enabled", false);
                plugin.toggleShop(!current);
                AdminGUI.open(p);
                return;
            }
            if (slot == 18) {
                MessageManager.setLanguage("zh");
                p.sendMessage(ColorUtil.color("&a语言已切换为中文"));
                AdminGUI.open(p);
                return;
            }
            if (slot == 19) {
                MessageManager.setLanguage("en");
                p.sendMessage(ColorUtil.color("&aLanguage switched to English"));
                AdminGUI.open(p);
                return;
            }
            if (slot == 31) {
                plugin.reloadPlugin();
                p.sendMessage(ColorUtil.color("&a配置文件已重载！"));
                AdminGUI.open(p);
                return;
            }
            if (slot == 53) {
                p.closeInventory();
            }
        }
    }

    private void refreshPage(Player p, TitleGUI.PageType type, int newPage) {
        if (type == TitleGUI.PageType.TITLE) {
            TitleGUI.openTitlePage(p, newPage);
        } else if (type == TitleGUI.PageType.SUFFIX) {
            TitleGUI.openSuffixPage(p, newPage);
        }
    }
}