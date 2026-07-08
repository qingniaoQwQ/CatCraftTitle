package catcraft.title;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;

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
            plugin.getLogger().info(MessageManager.get("update-disabled"));
            return;
        }


        try {

            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() { return null; }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                        public void checkServerTrusted(X509Certificate[] certs, String authType) { }
                    }
            };
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

            String latestVersion = getLatestVersion();
            String currentVersion = plugin.getDescription().getVersion();

            if (latestVersion == null || latestVersion.isEmpty()) {
                plugin.getLogger().warning("无法获取最新版本信息，请检查网络或 GitHub API 状态。");
                return;
            }

            if (latestVersion.startsWith("v")) {
                latestVersion = latestVersion.substring(1);
            }

            if (!latestVersion.equalsIgnoreCase(currentVersion)) {
                String header = ChatColor.RED + "==========================================";
                String line1 = ChatColor.RED + "  " + MessageManager.get("update-available");
                String line2 = ChatColor.RED + "  " + MessageManager.get("update-current") + " " + ChatColor.WHITE + currentVersion;
                String line3 = ChatColor.RED + "  " + MessageManager.get("update-latest") + " " + ChatColor.GREEN + latestVersion;
                String line4 = ChatColor.RED + "  " + MessageManager.get("update-download") + " " + ChatColor.UNDERLINE + "https://github.com/" + repoOwner + "/" + repoName + "/releases/latest";
                String footer = ChatColor.RED + "==========================================";

                Bukkit.getConsoleSender().sendMessage(header);
                Bukkit.getConsoleSender().sendMessage(line1);
                Bukkit.getConsoleSender().sendMessage(line2);
                Bukkit.getConsoleSender().sendMessage(line3);
                Bukkit.getConsoleSender().sendMessage(line4);
                Bukkit.getConsoleSender().sendMessage(footer);

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
            } else {
                plugin.getLogger().info(MessageManager.get("update-latest") + " " + currentVersion);
            }
        } catch (Exception e) {
            plugin.getLogger().warning(MessageManager.get("update-fail", e.getMessage()));
        }
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
            plugin.getLogger().warning(MessageManager.get("update-api-error", responseCode));
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
                plugin.getLogger().warning(MessageManager.get("update-parse-error"));
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