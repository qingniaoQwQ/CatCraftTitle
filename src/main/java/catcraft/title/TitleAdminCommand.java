package catcraft.title;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import java.util.*;

public class TitleAdminCommand implements CommandExecutor, TabCompleter {
    private final TitleManager manager;
    private static final String PREFIX = "&e[&eCatCraft] ";

    public TitleAdminCommand(TitleManager manager) { this.manager = manager; }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) { sendHelp(sender); return true; }
        if (!sender.hasPermission("catcraft.admin")) {
            sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("no-permission")));
            return true;
        }
        String sub = args[0].toLowerCase();
        if (sub.equals("give")) return handleGive(sender, args, 0);
        if (sub.equals("edit")) return handleEdit(sender, args, 0);
        if (sub.equals("take")) return handleTake(sender, args, 0);
        if (sub.equals("list")) return handleList(sender, args, 0);
        if (sub.equals("setactive")) return handleSetActive(sender, args, 0);
        if (sub.equals("deactive")) return handleDeactive(sender, args, 0);
        if (sub.equals("suffixgive")) return handleGive(sender, args, 1);
        if (sub.equals("suffixedit")) return handleEdit(sender, args, 1);
        if (sub.equals("suffixtake")) return handleTake(sender, args, 1);
        if (sub.equals("suffixlist")) return handleList(sender, args, 1);
        if (sub.equals("suffixsetactive")) return handleSetActive(sender, args, 1);
        if (sub.equals("suffixdeactive")) return handleDeactive(sender, args, 1);
        if (sub.equals("shop")) {
            if (args.length < 2) { sendShopHelp(sender); return true; }
            String shopSub = args[1].toLowerCase();
            switch (shopSub) {
                case "add": return handleShopAdd(sender, args);
                case "remove": return handleShopRemove(sender, args);
                case "setprice": return handleShopSetPrice(sender, args);
                case "list": return handleShopList(sender, args);
                case "givebalance": return handleShopGiveBalance(sender, args);
                case "setbalance": return handleShopSetBalance(sender, args);
                case "toggle": return handleShopToggle(sender, args);
                default: sendShopHelp(sender); return true;
            }
        }
        sendHelp(sender);
        return true;
    }

    private boolean handleGive(CommandSender sender, String[] args, int type) {
        if (args.length < 4) {
            sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-give-usage", type==0?"give":"suffixgive")));
            return true;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) { sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("player-offline"))); return true; }
        int id;
        try { id = Integer.parseInt(args[2]); } catch (NumberFormatException e) { sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("invalid-id"))); return true; }
        String display = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
        if (manager.getOwnedTitles(target).containsKey(id) || manager.getOwnedSuffixes(target).containsKey(id)) {
            sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("id-already-owned")));
            return true;
        }
        boolean success = (type == 0) ? manager.addTitle(target, id, display) : manager.addSuffix(target, id, display);
        if (success) sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-give-success", target.getName(), type==0?"title":"suffix", id, display)));
        else sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-give-fail")));
        return true;
    }

    private boolean handleEdit(CommandSender sender, String[] args, int type) {
        if (args.length < 4) { sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-edit-usage", type==0?"edit":"suffixedit"))); return true; }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) { sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("player-offline"))); return true; }
        int id;
        try { id = Integer.parseInt(args[2]); } catch (NumberFormatException e) { sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("invalid-id"))); return true; }
        String newDisplay = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
        boolean hasTitle = manager.getOwnedTitles(target).containsKey(id);
        boolean hasSuffix = manager.getOwnedSuffixes(target).containsKey(id);
        if (!hasTitle && !hasSuffix) { sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("no-such-id"))); return true; }
        if (type == 0 && !hasTitle) { sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("not-title"))); return true; }
        if (type == 1 && !hasSuffix) { sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("not-suffix"))); return true; }
        if (manager.updateTitleDisplay(target, id, newDisplay)) sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-edit-success", target.getName(), type==0?"title":"suffix", id, newDisplay)));
        else sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-edit-fail")));
        return true;
    }

    private boolean handleTake(CommandSender sender, String[] args, int type) {
        if (args.length < 3) { sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-take-usage", type==0?"take":"suffixtake"))); return true; }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) { sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("player-offline"))); return true; }
        int id;
        try { id = Integer.parseInt(args[2]); } catch (NumberFormatException e) { sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("invalid-id"))); return true; }
        boolean hasTitle = manager.getOwnedTitles(target).containsKey(id);
        boolean hasSuffix = manager.getOwnedSuffixes(target).containsKey(id);
        if (type == 0 && !hasTitle) { sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("no-title"))); return true; }
        if (type == 1 && !hasSuffix) { sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("no-suffix"))); return true; }
        if (manager.removeTitle(target, id)) sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-take-success", target.getName(), type==0?"title":"suffix", id)));
        else sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-take-fail")));
        return true;
    }

    private boolean handleList(CommandSender sender, String[] args, int type) {
        if (args.length < 2) { sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-list-usage", type==0?"list":"suffixlist"))); return true; }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) { sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("player-offline"))); return true; }
        if (type == 0) {
            Map<Integer, String> titles = manager.getOwnedTitles(target);
            int activeId = manager.getActiveTitleId(target);
            sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-list-title-header", target.getName())));
            if (titles.isEmpty()) sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-list-empty")));
            else for (Map.Entry<Integer, String> entry : titles.entrySet()) {
                String status = (entry.getKey() == activeId) ? "§a[启用]" : "§7[未启用]";
                sender.sendMessage(ColorUtil.color(PREFIX + "§f- §7ID:" + entry.getKey() + " " + ColorUtil.color(entry.getValue()) + " " + status));
            }
        } else {
            Map<Integer, String> suffixes = manager.getOwnedSuffixes(target);
            int activeId = manager.getActiveSuffixId(target);
            sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-list-suffix-header", target.getName())));
            if (suffixes.isEmpty()) sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-list-empty")));
            else for (Map.Entry<Integer, String> entry : suffixes.entrySet()) {
                String status = (entry.getKey() == activeId) ? "§a[启用]" : "§7[未启用]";
                sender.sendMessage(ColorUtil.color(PREFIX + "§f- §7ID:" + entry.getKey() + " " + ColorUtil.color(entry.getValue()) + " " + status));
            }
        }
        return true;
    }

    private boolean handleSetActive(CommandSender sender, String[] args, int type) {
        if (args.length < 3) { sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-setactive-usage", type==0?"setactive":"suffixsetactive"))); return true; }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) { sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("player-offline"))); return true; }
        int id;
        try { id = Integer.parseInt(args[2]); } catch (NumberFormatException e) { sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("invalid-id"))); return true; }
        boolean success = (type == 0) ? manager.activateTitle(target, id) : manager.activateSuffix(target, id);
        if (success) sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-setactive-success", target.getName(), type==0?"title":"suffix", id)));
        else sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-setactive-fail")));
        return true;
    }

    private boolean handleDeactive(CommandSender sender, String[] args, int type) {
        if (args.length < 2) { sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-deactive-usage", type==0?"deactive":"suffixdeactive"))); return true; }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) { sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("player-offline"))); return true; }
        boolean success = (type == 0) ? manager.deactivateTitle(target) : manager.deactivateSuffix(target);
        if (success) sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-deactive-success", target.getName(), type==0?"title":"suffix")));
        else sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-deactive-fail")));
        return true;
    }


    private boolean handleShopAdd(CommandSender sender, String[] args) {
        if (args.length < 6) {
            sender.sendMessage(ColorUtil.color("§c用法: /titleadmin shop add <ID> <类型(0头衔/1后缀)> <价格> <显示名>"));
            return true;
        }
        int id, type, price;
        try {
            id = Integer.parseInt(args[2]);
            type = Integer.parseInt(args[3]);
            price = Integer.parseInt(args[4]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ColorUtil.color("§cID、类型和价格必须是数字"));
            return true;
        }
        String display = String.join(" ", Arrays.copyOfRange(args, 5, args.length));


        if (CatCraftTitlePlugin.getInstance().getDatabase().isTitleIdUsedByAnyPlayer(id)) {
            sender.sendMessage(ColorUtil.color("§c该ID已被某玩家拥有，请使用其他ID"));
            return true;
        }

        if (CatCraftTitlePlugin.getInstance().getShopManager().addShopItem(id, type, display, price)) {
            sender.sendMessage(ColorUtil.color("§a商品添加成功！"));
        } else {
            sender.sendMessage(ColorUtil.color("§c添加失败，可能ID已存在或商店未启用"));
        }
        return true;
    }

    private boolean handleShopRemove(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage(ColorUtil.color("§c用法: /titleadmin shop remove <ID> <类型>"));
            return true;
        }
        int id, type;
        try {
            id = Integer.parseInt(args[2]);
            type = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ColorUtil.color("§cID和类型必须是数字"));
            return true;
        }
        if (CatCraftTitlePlugin.getInstance().getShopManager().removeShopItem(id, type)) {
            sender.sendMessage(ColorUtil.color("§a商品移除成功"));
        } else {
            sender.sendMessage(ColorUtil.color("§c移除失败"));
        }
        return true;
    }

    private boolean handleShopSetPrice(CommandSender sender, String[] args) {
        if (args.length < 5) {
            sender.sendMessage(ColorUtil.color("§c用法: /titleadmin shop setprice <ID> <类型> <新价格>"));
            return true;
        }
        int id, type, price;
        try {
            id = Integer.parseInt(args[2]);
            type = Integer.parseInt(args[3]);
            price = Integer.parseInt(args[4]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ColorUtil.color("§cID、类型和价格必须是数字"));
            return true;
        }
        if (CatCraftTitlePlugin.getInstance().getShopManager().setShopItemPrice(id, type, price)) {
            sender.sendMessage(ColorUtil.color("§a价格更新成功"));
        } else {
            sender.sendMessage(ColorUtil.color("§c更新失败"));
        }
        return true;
    }

    private boolean handleShopList(CommandSender sender, String[] args) {
        List<ShopItem> items = CatCraftTitlePlugin.getInstance().getShopManager().getShopItems();
        if (items.isEmpty()) {
            sender.sendMessage(ColorUtil.color("§e商店暂无商品"));
            return true;
        }
        sender.sendMessage(ColorUtil.color("§6===== 商店商品列表 ====="));
        for (ShopItem item : items) {
            String typeStr = (item.getType() == 0) ? "头衔" : "后缀";
            sender.sendMessage(ColorUtil.color("§fID:" + item.getId() + " §7" + typeStr + " §a" + item.getDisplay() + " §6价格:" + item.getPrice()));
        }
        return true;
    }

    private boolean handleShopGiveBalance(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage(ColorUtil.color("§c用法: /titleadmin shop givebalance <玩家> <金额>"));
            return true;
        }
        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            sender.sendMessage(ColorUtil.color(MessageManager.get("player-offline")));
            return true;
        }
        int amount;
        try {
            amount = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ColorUtil.color("§c金额必须是数字"));
            return true;
        }
        if (CatCraftTitlePlugin.getInstance().getShopManager().addBalance(target.getUniqueId(), amount)) {
            sender.sendMessage(ColorUtil.color("§a已为 " + target.getName() + " 增加 " + amount + " 金币"));
        } else {
            sender.sendMessage(ColorUtil.color("§c操作失败"));
        }
        return true;
    }

    private boolean handleShopSetBalance(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage(ColorUtil.color("§c用法: /titleadmin shop setbalance <玩家> <金额>"));
            return true;
        }
        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            sender.sendMessage(ColorUtil.color(MessageManager.get("player-offline")));
            return true;
        }
        int amount;
        try {
            amount = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ColorUtil.color("§c金额必须是数字"));
            return true;
        }
        if (amount < 0) { sender.sendMessage(ColorUtil.color("§c金额不能为负数")); return true; }
        if (CatCraftTitlePlugin.getInstance().getShopManager().setBalance(target.getUniqueId(), amount)) {
            sender.sendMessage(ColorUtil.color("§a已将 " + target.getName() + " 余额设为 " + amount));
        } else {
            sender.sendMessage(ColorUtil.color("§c操作失败"));
        }
        return true;
    }

    private boolean handleShopToggle(CommandSender sender, String[] args) {
        sender.sendMessage(ColorUtil.color("§e请编辑 config.yml 中的 shop.enabled 并执行 /catcraft reload（或重载插件）"));
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ColorUtil.color(PREFIX + "§6===== " + MessageManager.get("admin-help-title") + " ====="));
        sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-help-give-line")));
        sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-help-edit-line")));
        sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-help-take-line")));
        sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-help-list-line")));
        sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-help-setactive-line")));
        sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-help-deactive-line")));
        sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-help-suffixgive-line")));
        sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-help-suffixedit-line")));
        sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-help-suffixtake-line")));
        sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-help-suffixlist-line")));
        sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-help-suffixsetactive-line")));
        sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-help-suffixdeactive-line")));
        sender.sendMessage(ColorUtil.color(PREFIX + "§6--- " + MessageManager.get("admin-help-shop-title") + " ---"));
        sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-help-shop-add-line")));
        sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-help-shop-remove-line")));
        sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-help-shop-setprice-line")));
        sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-help-shop-list-line")));
        sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-help-shop-givebalance-line")));
        sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-help-shop-setbalance-line")));
        sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-help-shop-toggle-line")));
    }

    private void sendShopHelp(CommandSender sender) {
        sender.sendMessage(ColorUtil.color(PREFIX + "§6===== " + MessageManager.get("admin-help-shop-title") + " ====="));
        sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-help-shop-add-line")));
        sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-help-shop-remove-line")));
        sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-help-shop-setprice-line")));
        sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-help-shop-list-line")));
        sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-help-shop-givebalance-line")));
        sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-help-shop-setbalance-line")));
        sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("admin-help-shop-toggle-line")));
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.addAll(Arrays.asList("give","edit","take","list","setactive","deactive","suffixgive","suffixedit","suffixtake","suffixlist","suffixsetactive","suffixdeactive","shop"));
        } else if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (sub.equals("shop")) {
                completions.addAll(Arrays.asList("add","remove","setprice","list","givebalance","setbalance","toggle"));
            } else if (sub.startsWith("suffix") || sub.equals("give") || sub.equals("edit") || sub.equals("take") || sub.equals("list") || sub.equals("setactive") || sub.equals("deactive")) {
                for (Player p : Bukkit.getOnlinePlayers()) completions.add(p.getName());
            }
        } else if (args.length == 3) {
            String sub = args[0].toLowerCase();
            if (sub.equals("shop")) {
                String shopSub = args[1].toLowerCase();
                if (shopSub.equals("remove") || shopSub.equals("setprice")) {
                    for (ShopItem item : CatCraftTitlePlugin.getInstance().getShopManager().getShopItems()) {
                        completions.add(String.valueOf(item.getId()));
                    }
                } else if (shopSub.equals("givebalance") || shopSub.equals("setbalance")) {
                    for (Player p : Bukkit.getOnlinePlayers()) completions.add(p.getName());
                }
            } else if (sub.equals("give") || sub.equals("edit") || sub.equals("take") || sub.equals("setactive") ||
                    sub.equals("suffixgive") || sub.equals("suffixedit") || sub.equals("suffixtake") || sub.equals("suffixsetactive")) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    boolean isSuffix = sub.startsWith("suffix");
                    Map<Integer, String> map = isSuffix ? manager.getOwnedSuffixes(target) : manager.getOwnedTitles(target);
                    for (Integer id : map.keySet()) completions.add(id.toString());
                }
            }
        } else if (args.length == 4) {
            String sub = args[0].toLowerCase();
            if (sub.equals("shop") && args[1].equalsIgnoreCase("remove")) {
                completions.addAll(Arrays.asList("0","1"));
            }
        }
        return completions;
    }
}