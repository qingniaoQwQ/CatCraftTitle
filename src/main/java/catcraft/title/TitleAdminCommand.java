package catcraft.title;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import java.util.*;

public class TitleAdminCommand implements CommandExecutor, TabCompleter {
    private final TitleManager manager;
    private static final String PREFIX = "&e[&eCatCraft] ";

    public TitleAdminCommand(TitleManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        if (!sender.hasPermission("catcraft.admin")) {
            sender.sendMessage(ColorUtil.color(PREFIX + "§c你没有管理员权限"));
            return true;
        }

        String sub = args[0].toLowerCase();
        switch (sub) {
            case "give": {
                if (args.length < 4) {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§e用法: /titleadmin give <玩家> <ID> <显示名>"));
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§c玩家不在线"));
                    return true;
                }
                int id;
                try { id = Integer.parseInt(args[2]); } catch (NumberFormatException e) {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§cID必须是数字"));
                    return true;
                }
                String display = String.join(" ", Arrays.copyOfRange(args, 3, args.length));

                if (manager.getOwnedTitles(target).containsKey(id)) {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§c注意，这个ID已经占用，本次添加未成功！"));
                    return true;
                }

                if (manager.addTitle(target, id, display)) {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§a已为 " + target.getName() + " 添加头衔 ID:" + id + " 显示: " + display));
                } else {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§c添加失败，请检查控制台日志。"));
                }
                break;
            }

            case "edit": {
                if (args.length < 4) {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§e用法: /titleadmin edit <玩家> <ID> <新显示名>"));
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§c玩家不在线"));
                    return true;
                }
                int id;
                try { id = Integer.parseInt(args[2]); } catch (NumberFormatException e) {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§cID必须是数字"));
                    return true;
                }
                String newDisplay = String.join(" ", Arrays.copyOfRange(args, 3, args.length));

                if (!manager.getOwnedTitles(target).containsKey(id)) {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§c该玩家没有此ID的头衔"));
                    return true;
                }

                if (manager.updateTitleDisplay(target, id, newDisplay)) {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§a已修改 " + target.getName() + " 的头衔 ID:" + id + " 为: " + newDisplay));
                } else {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§c修改失败，请检查控制台日志。"));
                }
                break;
            }

            case "take": {
                if (args.length < 3) {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§e用法: /titleadmin take <玩家> <ID>"));
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§c玩家不在线"));
                    return true;
                }
                int id;
                try { id = Integer.parseInt(args[2]); } catch (NumberFormatException e) {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§cID必须是数字"));
                    return true;
                }
                if (manager.removeTitle(target, id)) {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§a已移除 " + target.getName() + " 的头衔 ID:" + id));
                } else {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§c移除失败（可能玩家没有此头衔或正在启用）"));
                }
                break;
            }

            case "list": {
                if (args.length < 2) {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§e用法: /titleadmin list <玩家>"));
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§c玩家不在线"));
                    return true;
                }
                Map<Integer, String> titles = manager.getOwnedTitles(target);
                DatabaseManager.SuffixData suffixData = manager.getSuffixData(target);

                if (titles.isEmpty()) {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§e" + target.getName() + " 没有任何头衔"));
                } else {
                    int activeId = manager.getActiveId(target);
                    sender.sendMessage(ColorUtil.color(PREFIX + "§6===== " + target.getName() + " 的头衔列表 ====="));
                    for (Map.Entry<Integer, String> entry : titles.entrySet()) {
                        String status = (entry.getKey() == activeId) ? "§a[启用]" : "§7[未启用]";
                        sender.sendMessage(ColorUtil.color(PREFIX + "§f- §7ID:" + entry.getKey() + " " +
                                ColorUtil.color(entry.getValue()) + " " + status));
                    }
                }

                String suffixStatus = suffixData.active ? "§a[启用]" : "§c[已关闭]";
                String suffixDisplay = suffixData.suffix.isEmpty() ? "§7(未设置)" : ColorUtil.color(suffixData.suffix);
                sender.sendMessage(ColorUtil.color(PREFIX + "§6----- 后缀信息 -----"));
                sender.sendMessage(ColorUtil.color(PREFIX + "§f- 后缀: " + suffixDisplay + " " + suffixStatus));
                break;
            }

            case "setactive": {
                if (args.length < 3) {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§e用法: /titleadmin setactive <玩家> <ID>"));
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§c玩家不在线"));
                    return true;
                }
                int id;
                try { id = Integer.parseInt(args[2]); } catch (NumberFormatException e) {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§cID必须是数字"));
                    return true;
                }
                if (manager.activateTitle(target, id)) {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§a已激活 " + target.getName() + " 的头衔 ID:" + id));
                } else {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§c激活失败（玩家可能未拥有该头衔）"));
                }
                break;
            }

            case "suffix": {
                if (args.length < 3) {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§e用法: /titleadmin suffix <玩家> <后缀>"));
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§c玩家不在线"));
                    return true;
                }
                String suffix = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                if (manager.setSuffix(target, ColorUtil.color(suffix))) {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§a已设置 " + target.getName() + " 的后缀为 " + suffix + "（保留原有启用状态）"));
                } else {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§c设置失败"));
                }
                break;
            }

            // 新增：管理员启用玩家后缀
            case "suffixactive": {
                if (args.length < 2) {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§e用法: /titleadmin suffixactive <玩家>"));
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§c玩家不在线"));
                    return true;
                }
                if (manager.setSuffixActive(target, true)) {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§a已启用 " + target.getName() + " 的后缀显示"));
                } else {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§c启用失败，请先设置后缀内容"));
                }
                break;
            }

            // 新增：管理员禁用玩家后缀
            case "suffixdeactive": {
                if (args.length < 2) {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§e用法: /titleadmin suffixdeactive <玩家>"));
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§c玩家不在线"));
                    return true;
                }
                if (manager.setSuffixActive(target, false)) {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§a已禁用 " + target.getName() + " 的后缀显示"));
                } else {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§c禁用失败"));
                }
                break;
            }

            // 新增：管理员清空后缀（移除后缀内容，但不删除记录，保留启用状态）
            case "suffixclear": {
                if (args.length < 2) {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§e用法: /titleadmin suffixclear <玩家>"));
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§c玩家不在线"));
                    return true;
                }
                // 清空后缀内容，但保留启用状态（用空字符串）
                if (manager.setSuffix(target, "")) {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§a已清空 " + target.getName() + " 的后缀内容（保留启用状态）"));
                } else {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§c清空失败"));
                }
                break;
            }

            case "deactive": {
                if (args.length < 2) {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§e用法: /titleadmin deactive <玩家>"));
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§c玩家不在线"));
                    return true;
                }
                if (manager.deactivateTitle(target)) {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§a已停用 " + target.getName() + " 的头衔"));
                } else {
                    sender.sendMessage(ColorUtil.color(PREFIX + "§c停用失败"));
                }
                break;
            }

            default:
                sendHelp(sender);
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ColorUtil.color(PREFIX + "§6===== 管理员头衔指令 ====="));
        sender.sendMessage(ColorUtil.color(PREFIX + "§e/titleadmin give <玩家> <ID> <显示名> §7- 添加头衔"));
        sender.sendMessage(ColorUtil.color(PREFIX + "§e/titleadmin edit <玩家> <ID> <新显示名> §7- 修改头衔显示名"));
        sender.sendMessage(ColorUtil.color(PREFIX + "§e/titleadmin take <玩家> <ID> §7- 移除头衔"));
        sender.sendMessage(ColorUtil.color(PREFIX + "§e/titleadmin list <玩家> §7- 查看玩家所有头衔和后缀"));
        sender.sendMessage(ColorUtil.color(PREFIX + "§e/titleadmin setactive <玩家> <ID> §7- 激活玩家某个头衔"));
        sender.sendMessage(ColorUtil.color(PREFIX + "§e/titleadmin suffix <玩家> <后缀> §7- 设置玩家后缀（保留启用状态）"));
        sender.sendMessage(ColorUtil.color(PREFIX + "§e/titleadmin suffixactive <玩家> §7- 启用玩家后缀显示"));
        sender.sendMessage(ColorUtil.color(PREFIX + "§e/titleadmin suffixdeactive <玩家> §7- 禁用玩家后缀显示"));
        sender.sendMessage(ColorUtil.color(PREFIX + "§e/titleadmin suffixclear <玩家> §7- 清空玩家后缀内容（保留启用状态）"));
        sender.sendMessage(ColorUtil.color(PREFIX + "§e/titleadmin deactive <玩家> §7- 停用玩家头衔"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.addAll(Arrays.asList("give", "edit", "take", "list", "setactive",
                    "suffix", "suffixactive", "suffixdeactive", "suffixclear", "deactive"));
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("edit") ||
                    args[0].equalsIgnoreCase("take") || args[0].equalsIgnoreCase("list") ||
                    args[0].equalsIgnoreCase("setactive") || args[0].equalsIgnoreCase("suffix") ||
                    args[0].equalsIgnoreCase("suffixactive") || args[0].equalsIgnoreCase("suffixdeactive") ||
                    args[0].equalsIgnoreCase("suffixclear") || args[0].equalsIgnoreCase("deactive")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    completions.add(p.getName());
                }
            }
        } else if (args.length == 3 && (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("edit") ||
                args[0].equalsIgnoreCase("take") || args[0].equalsIgnoreCase("setactive"))) {
            // 对于 edit/take/setactive，补全已拥有的头衔ID
            if (args[0].equalsIgnoreCase("edit") || args[0].equalsIgnoreCase("take") ||
                    args[0].equalsIgnoreCase("setactive")) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    Map<Integer, String> titles = manager.getOwnedTitles(target);
                    for (Integer id : titles.keySet()) {
                        completions.add(id.toString());
                    }
                }
            }
        }
        return completions;
    }
}