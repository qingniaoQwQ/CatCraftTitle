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

    // 使用新的全行键
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
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.addAll(Arrays.asList("give","edit","take","list","setactive","deactive","suffixgive","suffixedit","suffixtake","suffixlist","suffixsetactive","suffixdeactive"));
        } else if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (sub.startsWith("suffix") || sub.equals("give") || sub.equals("edit") || sub.equals("take") || sub.equals("list") || sub.equals("setactive") || sub.equals("deactive")) {
                for (Player p : Bukkit.getOnlinePlayers()) completions.add(p.getName());
            }
        } else if (args.length == 3) {
            String sub = args[0].toLowerCase();
            if (sub.equals("give") || sub.equals("edit") || sub.equals("take") || sub.equals("setactive") ||
                    sub.equals("suffixgive") || sub.equals("suffixedit") || sub.equals("suffixtake") || sub.equals("suffixsetactive")) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    boolean isSuffix = sub.startsWith("suffix");
                    Map<Integer, String> map = isSuffix ? manager.getOwnedSuffixes(target) : manager.getOwnedTitles(target);
                    for (Integer id : map.keySet()) completions.add(id.toString());
                }
            }
        }
        return completions;
    }
}