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
        if (!(inv.getHolder() instanceof TitleGUI.GUIHolder)) return;

        e.setCancelled(true);
        TitleGUI.GUIHolder holder = (TitleGUI.GUIHolder) inv.getHolder();
        UUID uuid = holder.getPlayerUUID();
        if (!uuid.equals(p.getUniqueId())) return;

        TitleManager mgr = CatCraftTitlePlugin.getInstance().getManager();
        int slot = e.getRawSlot();
        TitleGUI.PageType type = holder.getType();
        int page = holder.getPage();

        // 主页
        if (type == TitleGUI.PageType.HOME) {
            if (slot == 4) {
                TitleGUI.openHome(p);
            } else if (slot == 20) {
                TitleGUI.openTitlePage(p, 0);
            } else if (slot == 24) {
                TitleGUI.openSuffixPage(p, 0);
            } else if (slot == 53) {
                p.closeInventory();
            }
            return;
        }

        // 称号管理页（头衔或后缀）
        if (slot == 0) { // 返回主页
            TitleGUI.openHome(p);
            return;
        }

        if (slot == 8) { // 停用当前激活
            if (type == TitleGUI.PageType.TITLE) {
                mgr.deactivateTitle(p);
            } else {
                mgr.deactivateSuffix(p);
            }
            refreshPage(p, type, page);
            return;
        }

        // 点击称号（9~44）
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

        // 分页
        if (slot == 45) {
            refreshPage(p, type, page - 1);
        } else if (slot == 53) {
            refreshPage(p, type, page + 1);
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