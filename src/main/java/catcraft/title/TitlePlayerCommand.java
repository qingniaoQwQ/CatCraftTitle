package catcraft.title;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import java.util.*;

public class TitlePlayerCommand implements CommandExecutor, TabCompleter {
    private final TitleManager manager;
    private static final String PREFIX = "&e[&eCatCraft] ";

    public TitlePlayerCommand(TitleManager manager) { this.manager = manager; }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("player-only-command")));
            return true;
        }
        Player p = (Player) sender;
        if (args.length == 0) { sendHelp(p); return true; }
        String sub = args[0].toLowerCase();
        switch (sub) {
            case "gui": TitleGUI.openHome(p); break;
            case "shop": TitleGUI.openShopHome(p); break;
            case "list": {
                Map<Integer, String> titles = manager.getOwnedTitles(p);
                Map<Integer, String> suffixes = manager.getOwnedSuffixes(p);
                int activeTitle = manager.getActiveTitleId(p);
                int activeSuffix = manager.getActiveSuffixId(p);
                p.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("player-list-header")));
                if (titles.isEmpty() && suffixes.isEmpty()) p.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("player-list-empty")));
                else {
                    p.sendMessage(ColorUtil.color(PREFIX + "§6--- " + MessageManager.get("title") + " ---"));
                    if (titles.isEmpty()) p.sendMessage(ColorUtil.color(PREFIX + "§7" + MessageManager.get("none")));
                    else for (Map.Entry<Integer, String> entry : titles.entrySet()) {
                        String status = (entry.getKey() == activeTitle) ? "§a[启用]" : "§7[未启用]";
                        p.sendMessage(ColorUtil.color(PREFIX + "§f- §7ID:" + entry.getKey() + " " + ColorUtil.color(entry.getValue()) + " " + status));
                    }
                    p.sendMessage(ColorUtil.color(PREFIX + "§6--- " + MessageManager.get("suffix") + " ---"));
                    if (suffixes.isEmpty()) p.sendMessage(ColorUtil.color(PREFIX + "§7" + MessageManager.get("none")));
                    else for (Map.Entry<Integer, String> entry : suffixes.entrySet()) {
                        String status = (entry.getKey() == activeSuffix) ? "§a[启用]" : "§7[未启用]";
                        p.sendMessage(ColorUtil.color(PREFIX + "§f- §7ID:" + entry.getKey() + " " + ColorUtil.color(entry.getValue()) + " " + status));
                    }
                }
                p.sendMessage(ColorUtil.color(PREFIX + "§7" + MessageManager.get("player-list-hint")));
                break;
            }
            case "active": {
                if (args.length < 2) { p.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("player-active-usage"))); return true; }
                int id;
                try { id = Integer.parseInt(args[1]); } catch (NumberFormatException e) { p.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("invalid-id"))); return true; }
                if (!manager.getOwnedTitles(p).containsKey(id)) { p.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("no-title"))); return true; }
                if (manager.activateTitle(p, id)) p.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("player-active-success", id)));
                else p.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("player-active-fail")));
                break;
            }
            case "deactive": {
                if (manager.deactivateTitle(p)) p.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("player-deactive-success")));
                else p.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("player-deactive-fail")));
                break;
            }
            case "suffixactive": {
                if (args.length < 2) { p.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("player-suffixactive-usage"))); return true; }
                int id;
                try { id = Integer.parseInt(args[1]); } catch (NumberFormatException e) { p.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("invalid-id"))); return true; }
                if (!manager.getOwnedSuffixes(p).containsKey(id)) { p.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("no-suffix"))); return true; }
                if (manager.activateSuffix(p, id)) p.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("player-suffixactive-success", id)));
                else p.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("player-suffixactive-fail")));
                break;
            }
            case "suffixdeactive": {
                if (manager.deactivateSuffix(p)) p.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("player-suffixdeactive-success")));
                else p.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("player-suffixdeactive-fail")));
                break;
            }
            case "remove": {
                if (args.length < 2) { p.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("player-remove-usage"))); return true; }
                int id;
                try { id = Integer.parseInt(args[1]); } catch (NumberFormatException e) { p.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("invalid-id"))); return true; }
                if (!manager.getOwnedTitles(p).containsKey(id) && !manager.getOwnedSuffixes(p).containsKey(id)) {
                    p.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("no-such-id")));
                    return true;
                }
                if (manager.removeTitle(p, id)) p.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("player-remove-success", id)));
                else p.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("player-remove-fail")));
                break;
            }
            default: sendHelp(p);
        }
        return true;
    }

    private void sendHelp(Player p) {
        p.sendMessage(ColorUtil.color(PREFIX + "§6===== " + MessageManager.get("player-help-title") + " ====="));
        p.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("player-help-gui-line")));
        p.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("player-help-list-line")));
        p.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("player-help-active-line")));
        p.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("player-help-deactive-line")));
        p.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("player-help-suffixactive-line")));
        p.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("player-help-suffixdeactive-line")));
        p.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("player-help-remove-line")));
        p.sendMessage(ColorUtil.color(PREFIX + MessageManager.get("player-help-shop-line")));
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.addAll(Arrays.asList("gui","shop","list","active","deactive","suffixactive","suffixdeactive","remove"));
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("active") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("suffixactive")) {
                Player p = (Player) sender;
                if (args[0].equalsIgnoreCase("active") || args[0].equalsIgnoreCase("remove")) {
                    for (Integer id : manager.getOwnedTitles(p).keySet()) completions.add(id.toString());
                }
                if (args[0].equalsIgnoreCase("suffixactive") || args[0].equalsIgnoreCase("remove")) {
                    for (Integer id : manager.getOwnedSuffixes(p).keySet()) completions.add(id.toString());
                }
            }
        }
        return completions;
    }
}