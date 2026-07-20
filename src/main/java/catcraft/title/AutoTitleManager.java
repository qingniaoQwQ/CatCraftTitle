package catcraft.title.auto;

import catcraft.title.*;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.regex.Pattern;

public class AutoTitleManager implements Listener {
    private final TitleManager titleManager;
    private final DatabaseManager database;
    private final JavaPlugin plugin;
    private int taskId = -1;
    private final List<AutoRule> rules = new ArrayList<>();

    public AutoTitleManager(TitleManager titleManager, DatabaseManager database, JavaPlugin plugin) {
        this.titleManager = titleManager;
        this.database = database;
        this.plugin = plugin;
        loadRules();
    }

    private void loadRules() {
        rules.clear();
        List<Map<?, ?>> configRules = plugin.getConfig().getMapList("auto-rules");
        for (Map<?, ?> map : configRules) {
            try {
                int id = (int) map.get("id");
                int type = (int) map.get("type");
                String display = (String) map.get("display");
                String conditionStr = (String) map.get("condition");
                if (conditionStr == null || conditionStr.isEmpty()) continue;
                Condition condition = new Condition(conditionStr);
                rules.add(new AutoRule(id, type, display, condition));
            } catch (Exception e) {
                plugin.getLogger().warning("自动称号规则解析失败: " + map);
            }
        }
    }

    public void startScheduler() {
        if (taskId != -1) return;
        loadRules();
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                evaluate(p);
            }
        }, 20L, 20L * 30);
        plugin.getLogger().info("自动称号调度任务已启动，间隔30秒");
    }

    public void stopScheduler() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
            plugin.getLogger().info("自动称号调度任务已停止");
        }
    }

    public void clearAllAutoTitles() {
        plugin.getLogger().info("自动称号已清除（未实现强制移除，请手动管理）");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> evaluate(e.getPlayer()), 20L);
    }

    public void evaluate(Player player) {
        if (rules.isEmpty()) return;
        for (AutoRule rule : rules) {
            boolean meets = rule.condition.test(player);
            boolean has = titleManager.hasTitle(player, rule.id, rule.type);
            if (meets && !has) {
                boolean added = (rule.type == 0) ?
                        titleManager.addTitle(player, rule.id, rule.display) :
                        titleManager.addSuffix(player, rule.id, rule.display);
                if (added) {
                    plugin.getLogger().info("自动授予 " + player.getName() + " 称号 ID:" + rule.id + " (条件满足)");
                    if (rule.type == 0) titleManager.activateTitle(player, rule.id);
                    else titleManager.activateSuffix(player, rule.id);
                }
            } else if (!meets && has) {
                boolean removed = titleManager.removeTitle(player, rule.id);
                if (removed) {
                    plugin.getLogger().info("自动移除 " + player.getName() + " 称号 ID:" + rule.id + " (条件不满足)");
                }
            }
        }
    }


    private static class AutoRule {
        int id, type;
        String display;
        Condition condition;
        AutoRule(int id, int type, String display, Condition condition) {
            this.id = id; this.type = type; this.display = display; this.condition = condition;
        }
    }

    public static class Condition {
        private final String placeholder;
        private final String operator;
        private final double value;

        public Condition(String raw) throws IllegalArgumentException {
            raw = raw.trim();
            String op = null;
            String[] ops = {">=", "<=", "==", "!=", ">", "<"};
            for (String o : ops) {
                if (raw.contains(o)) {
                    op = o;
                    break;
                }
            }
            if (op == null) throw new IllegalArgumentException("无效条件格式: " + raw);
            String[] parts = raw.split(Pattern.quote(op), 2);
            if (parts.length != 2) throw new IllegalArgumentException("无效条件格式: " + raw);


            String rawPlaceholder = parts[0].trim();
            if (!rawPlaceholder.startsWith("%")) {
                rawPlaceholder = "%" + rawPlaceholder + "%";
            }
            this.placeholder = rawPlaceholder;
            this.operator = op;
            try {
                this.value = Double.parseDouble(parts[1].trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("条件值不是数字: " + parts[1]);
            }
        }

        public boolean test(Player player) {
            String resolved = PlaceholderAPI.setPlaceholders(player, placeholder);
            double actual;
            try {
                actual = Double.parseDouble(resolved);
            } catch (NumberFormatException e) {
                return false;
            }
            switch (operator) {
                case ">=": return actual >= value;
                case "<=": return actual <= value;
                case "==": return Math.abs(actual - value) < 1e-9;
                case "!=": return Math.abs(actual - value) >= 1e-9;
                case ">":  return actual > value;
                case "<":  return actual < value;
                default: return false;
            }
        }
    }
}