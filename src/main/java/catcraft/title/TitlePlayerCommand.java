package catcraft.title;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import java.util.*;

public class TitlePlayerCommand implements CommandExecutor, TabCompleter {
    private final TitleManager manager;
    private static final String PREFIX = "&e[&eCatCraft] ";

    public TitlePlayerCommand(TitleManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // 修改：使用传统 instanceof 写法，兼容 Java 8
        if (!(sender instanceof Player)) {
            sender.sendMessage(ColorUtil.color(PREFIX + "§c该命令仅限玩家执行"));
            return true;
        }
        Player p = (Player) sender;

        if (args.length == 0) {
            sendHelp(p);
            return true;
        }

        String sub = args[0].toLowerCase();
        switch (sub) {
            case "list": {
                Map<Integer, String> titles = manager.getOwnedTitles(p);
                int activeId = manager.getActiveId(p);
                DatabaseManager.SuffixData suffixData = manager.getSuffixData(p);

                p.sendMessage(ColorUtil.color(PREFIX + "§6===== 我的头衔列表 ====="));
                if (titles.isEmpty()) {
                    p.sendMessage(ColorUtil.color(PREFIX + "§e你还没有任何头衔，请联系管理员添加。"));
                } else {
                    for (Map.Entry<Integer, String> entry : titles.entrySet()) {
                        String status = (entry.getKey() == activeId) ? "§a[启用]" : "§7[未启用]";
                        p.sendMessage(ColorUtil.color(PREFIX + "§f- §7ID:" + entry.getKey() + " " +
                                ColorUtil.color(entry.getValue()) + " " + status));
                    }
                }

                String suffixStatus = suffixData.active ? "§a[启用]" : "§c[已关闭]";
                String suffixDisplay = suffixData.suffix.isEmpty() ? "§7(未设置)" : ColorUtil.color(suffixData.suffix);
                p.sendMessage(ColorUtil.color(PREFIX + "§6----- 后缀信息 -----"));
                p.sendMessage(ColorUtil.color(PREFIX + "§f- 后缀: " + suffixDisplay + " " + suffixStatus));
                p.sendMessage(ColorUtil.color(PREFIX + "§7使用 §e/title suffixactive §7启用 · §e/title suffixdeactive §7关闭"));
                break;
            }
            case "active": {
                if (args.length < 2) {
                    p.sendMessage(ColorUtil.color(PREFIX + "§e用法: /title active <ID>"));
                    return true;
                }
                int id;
                try {
                    id = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    p.sendMessage(ColorUtil.color(PREFIX + "§c请输入数字ID"));
                    return true;
                }
                if (!manager.getOwnedTitles(p).containsKey(id)) {
                    p.sendMessage(ColorUtil.color(PREFIX + "§c你未拥有此头衔"));
                    return true;
                }
                if (manager.activateTitle(p, id)) {
                    p.sendMessage(ColorUtil.color(PREFIX + "§a已启用头衔 ID:" + id));
                } else {
                    p.sendMessage(ColorUtil.color(PREFIX + "§c启用失败"));
                }
                break;
            }
            case "deactive": {
                if (manager.deactivateTitle(p)) {
                    p.sendMessage(ColorUtil.color(PREFIX + "§a已停用当前头衔，恢复默认"));
                } else {
                    p.sendMessage(ColorUtil.color(PREFIX + "§c停用失败"));
                }
                break;
            }
            case "remove": {
                if (args.length < 2) {
                    p.sendMessage(ColorUtil.color(PREFIX + "§e用法: /title remove <ID>"));
                    return true;
                }
                int id;
                try {
                    id = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    p.sendMessage(ColorUtil.color(PREFIX + "§c请输入数字ID"));
                    return true;
                }
                if (!manager.getOwnedTitles(p).containsKey(id)) {
                    p.sendMessage(ColorUtil.color(PREFIX + "§c你没有此头衔"));
                    return true;
                }
                if (manager.removeTitle(p, id)) {
                    p.sendMessage(ColorUtil.color(PREFIX + "§a已移除头衔 ID:" + id));
                } else {
                    p.sendMessage(ColorUtil.color(PREFIX + "§c无法移除当前启用的头衔，请先停用"));
                }
                break;
            }
            case "suffixactive": {
                if (manager.setSuffixActive(p, true)) {
                    p.sendMessage(ColorUtil.color(PREFIX + "§a已启用后缀显示"));
                } else {
                    p.sendMessage(ColorUtil.color(PREFIX + "§c启用失败，请先设置后缀内容"));
                }
                break;
            }
            case "suffixdeactive": {
                if (manager.setSuffixActive(p, false)) {
                    p.sendMessage(ColorUtil.color(PREFIX + "§a已关闭后缀显示"));
                } else {
                    p.sendMessage(ColorUtil.color(PREFIX + "§c关闭失败"));
                }
                break;
            }
            default:
                sendHelp(p);
        }
        return true;
    }

    private void sendHelp(Player p) {
        p.sendMessage(ColorUtil.color(PREFIX + "§6===== 玩家头衔指令 ====="));
        p.sendMessage(ColorUtil.color(PREFIX + "§e/title list §7- 查看头衔列表和后缀状态"));
        p.sendMessage(ColorUtil.color(PREFIX + "§e/title active <ID> §7- 启用某个头衔"));
        p.sendMessage(ColorUtil.color(PREFIX + "§e/title deactive §7- 停用当前头衔，恢复默认"));
        p.sendMessage(ColorUtil.color(PREFIX + "§e/title remove <ID> §7- 移除自己的某个头衔（不可移除激活的）"));
        p.sendMessage(ColorUtil.color(PREFIX + "§e/title suffixactive §7- 启用后缀显示"));
        p.sendMessage(ColorUtil.color(PREFIX + "§e/title suffixdeactive §7- 关闭后缀显示"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.addAll(Arrays.asList("list", "active", "deactive", "remove", "suffixactive", "suffixdeactive"));
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("active") || args[0].equalsIgnoreCase("remove"))) {
            Player p = (Player) sender;
            Map<Integer, String> titles = manager.getOwnedTitles(p);
            for (Integer id : titles.keySet()) {
                completions.add(id.toString());
            }
        }
        return completions;
    }
}