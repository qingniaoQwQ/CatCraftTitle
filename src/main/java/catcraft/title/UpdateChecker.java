package catcraft.title;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {

    private final JavaPlugin plugin;
    private final String repoOwner = "qingniaoQwQ";
    private final String repoName = "CatCraftTitle";
    private final boolean checkEnabled;

    public UpdateChecker(JavaPlugin plugin, boolean checkEnabled) {
        this.plugin = plugin;
        this.checkEnabled = checkEnabled;
    }

    public void check() {
        if (!checkEnabled) {
            plugin.getLogger().info("更新检测已关闭");
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String latestVersion = getLatestVersion();
                String currentVersion = plugin.getDescription().getVersion();

                if (latestVersion == null || latestVersion.isEmpty()) {
                    return;
                }

                if (!latestVersion.equalsIgnoreCase(currentVersion)) {
                    String header = ChatColor.RED + "==========================================";
                    String line1 = ChatColor.RED + "  CatCraftTitle 有新版本可用！";
                    String line2 = ChatColor.RED + "  当前版本: " + ChatColor.WHITE + currentVersion;
                    String line3 = ChatColor.RED + "  最新版本: " + ChatColor.GREEN + latestVersion;
                    String line4 = ChatColor.RED + "  下载地址: " + ChatColor.UNDERLINE + "https://github.com/" + repoOwner + "/" + repoName + "/releases/latest";
                    String footer = ChatColor.RED + "==========================================";

                    // 控制台输出
                    Bukkit.getConsoleSender().sendMessage(header);
                    Bukkit.getConsoleSender().sendMessage(line1);
                    Bukkit.getConsoleSender().sendMessage(line2);
                    Bukkit.getConsoleSender().sendMessage(line3);
                    Bukkit.getConsoleSender().sendMessage(line4);
                    Bukkit.getConsoleSender().sendMessage(footer);


                    Bukkit.getScheduler().runTask(plugin, () -> {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (p.isOp()) {
                                p.sendMessage(header);
                                p.sendMessage(line1);
                                p.sendMessage(line2);
                                p.sendMessage(line3);
                                p.sendMessage(line4);
                                p.sendMessage(footer);
                            }
                        }
                    });
                } else {
                    plugin.getLogger().info("已是最新版本 (" + currentVersion + ")");
                }
            } catch (Exception e) {
                plugin.getLogger().warning("更新检测失败: " + e.getMessage());
            }
        });
    }

    private String getLatestVersion() throws Exception {
        String url = "https://api.github.com/repos/" + repoOwner + "/" + repoName + "/releases/latest";
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.setRequestProperty("Accept", "application/json");

        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            plugin.getLogger().warning("GitHub API 返回错误码: " + responseCode);
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            String json = response.toString();
            String tagKey = "\"tag_name\":\"";
            int start = json.indexOf(tagKey);
            if (start == -1) {
                plugin.getLogger().warning("无法解析 tag_name");
                return null;
            }
            start += tagKey.length();
            int end = json.indexOf("\"", start);
            if (end == -1) {
                return null;
            }
            return json.substring(start, end);
        } finally {
            connection.disconnect();
        }
    }
}