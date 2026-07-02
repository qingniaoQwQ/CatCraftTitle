package catcraft.title;

import org.bukkit.ChatColor;

public class ColorUtil {
    public static String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}