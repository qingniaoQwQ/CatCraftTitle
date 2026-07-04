package catcraft.title;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class MessageManager {
    private static MessageManager instance;
    private final Map<String, String> messages = new HashMap<>();

    private MessageManager(JavaPlugin plugin) {
        FileConfiguration config = plugin.getConfig();
        String lang = config.getString("language", "zh");

        // 获取对应语言的配置段，例如 "messages.zh" 或 "messages.en"
        if (config.isConfigurationSection("messages." + lang)) {
            Map<String, Object> section = config.getConfigurationSection("messages." + lang).getValues(false);
            for (Map.Entry<String, Object> entry : section.entrySet()) {
                if (entry.getValue() instanceof String) {
                    messages.put(entry.getKey(), (String) entry.getValue());
                }
            }
        } else {
            // 如果指定语言不存在，回退到中文
            Map<String, Object> section = config.getConfigurationSection("messages.zh").getValues(false);
            for (Map.Entry<String, Object> entry : section.entrySet()) {
                if (entry.getValue() instanceof String) {
                    messages.put(entry.getKey(), (String) entry.getValue());
                }
            }
        }
    }

    public static void init(JavaPlugin plugin) {
        instance = new MessageManager(plugin);
    }

    public static String get(String key) {
        String msg = instance.messages.getOrDefault(key, "§cMissing message: " + key);
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static String get(String key, Object... args) {
        String msg = instance.messages.getOrDefault(key, "§cMissing message: " + key);
        msg = String.format(msg, args);
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}