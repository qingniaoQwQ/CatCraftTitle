package catcraft.title;

import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.sql.*;
import java.util.*;

public class DatabaseManager {

    private final JavaPlugin plugin;
    private final boolean useMySQL;
    private Connection conn;
    private boolean isSQLite = false;
    private boolean connected = false;

    private String host, port, database, user, password;
    private String sqliteFile;

    public DatabaseManager(JavaPlugin plugin, boolean useMySQL) {
        this.plugin = plugin;
        this.useMySQL = useMySQL;
        if (useMySQL) {
            this.host = plugin.getConfig().getString("mysql.host", "localhost");
            this.port = plugin.getConfig().getString("mysql.port", "3306");
            this.database = plugin.getConfig().getString("mysql.db", "minecraft");
            this.user = plugin.getConfig().getString("mysql.user", "root");
            this.password = plugin.getConfig().getString("mysql.pass", "");
        }
        this.sqliteFile = plugin.getConfig().getString("local-database.file", "catcraft.db");
    }

    public void connect() {
        if (useMySQL) {
            try {
                String url = String.format(
                        "jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8",
                        host, port, database
                );
                conn = DriverManager.getConnection(url, user, password);
                plugin.getLogger().info("MySQL连接成功");
                createTables(false);
                connected = true;
                return;
            } catch (Exception e) {
                plugin.getLogger().severe("MySQL连接失败，将切换到本地SQLite存储。");
                e.printStackTrace();
            }
        }

        try {
            Class.forName("org.sqlite.JDBC");
            File dbFile = new File(plugin.getDataFolder(), sqliteFile);
            if (!dbFile.getParentFile().exists()) {
                dbFile.getParentFile().mkdirs();
            }
            String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
            conn = DriverManager.getConnection(url);
            isSQLite = true;
            connected = true;
            plugin.getLogger().info("本地SQLite数据库连接成功，文件: " + dbFile.getName());
            createTables(true);
        } catch (Exception e) {
            plugin.getLogger().severe("SQLite连接失败！");
            e.printStackTrace();
            connected = false;
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public String getDatabaseType() {
        if (!connected) return "未连接";
        return isSQLite ? "SQLite" : "MySQL";
    }

    private void createTables(boolean sqlite) {
        try (Statement stmt = conn.createStatement()) {
            String createTitles = sqlite ?
                    "CREATE TABLE IF NOT EXISTS player_titles (" +
                    "uuid VARCHAR(36) NOT NULL, " +
                    "title_id INTEGER NOT NULL, " +
                    "title_display VARCHAR(128) NOT NULL, " +
                    "is_active INTEGER DEFAULT 0, " +
                    "PRIMARY KEY (uuid, title_id))" :
                    "CREATE TABLE IF NOT EXISTS player_titles (" +
                    "uuid VARCHAR(36) NOT NULL, " +
                    "title_id INT NOT NULL, " +
                    "title_display VARCHAR(128) NOT NULL, " +
                    "is_active BOOLEAN DEFAULT FALSE, " +
                    "PRIMARY KEY (uuid, title_id))";
            stmt.execute(createTitles);
            String createSuffix = sqlite ?
                    "CREATE TABLE IF NOT EXISTS catcraft_titles (" +
                    "uuid VARCHAR(36) PRIMARY KEY, " +
                    "suffix VARCHAR(64), " +
                    "is_active INTEGER DEFAULT 1)" :
                    "CREATE TABLE IF NOT EXISTS catcraft_titles (" +
                    "uuid VARCHAR(36) PRIMARY KEY, " +
                    "suffix VARCHAR(64), " +
                    "is_active BOOLEAN DEFAULT TRUE)";
            stmt.execute(createSuffix);
            ensureSuffixActiveColumn(sqlite);

        } catch (SQLException e) {
            plugin.getLogger().severe("创建表失败！");
            e.printStackTrace();
        }
    }

    // 兼容 MySQL 低版本：先查询列是否存在
    private void ensureSuffixActiveColumn(boolean sqlite) {
        try {
            if (sqlite) {
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("PRAGMA table_info(catcraft_titles)")) {
                    boolean hasColumn = false;
                    while (rs.next()) {
                        if ("is_active".equalsIgnoreCase(rs.getString("name"))) {
                            hasColumn = true;
                            break;
                        }
                    }
                    if (!hasColumn) {
                        stmt.execute("ALTER TABLE catcraft_titles ADD COLUMN is_active INTEGER DEFAULT 1");
                        plugin.getLogger().info("已为 catcraft_titles 表添加 is_active 列 (SQLite)");
                    }
                }
            } else {
                // MySQL：检查列是否存在
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SHOW COLUMNS FROM catcraft_titles LIKE 'is_active'")) {
                    if (!rs.next()) {
                        stmt.execute("ALTER TABLE catcraft_titles ADD COLUMN is_active BOOLEAN DEFAULT TRUE");
                        plugin.getLogger().info("已为 catcraft_titles 表添加 is_active 列 (MySQL)");
                    }
                }
            }
        } catch (Exception e) {
            // 忽略异常，可能表已存在或权限问题
            plugin.getLogger().warning("无法检查或添加 is_active 列，可能表结构已存在");
        }
    }

    public SuffixData getSuffixData(UUID uuid) {
        if (conn == null) return new SuffixData("", true);
        String sql = "SELECT suffix, is_active FROM catcraft_titles WHERE uuid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String suffix = rs.getString("suffix") == null ? "" : rs.getString("suffix");
                    boolean active = isSQLite ? rs.getInt("is_active") == 1 : rs.getBoolean("is_active");
                    return new SuffixData(suffix, active);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new SuffixData("", true);
    }

    public String getSuffix(UUID uuid) {
        return getSuffixData(uuid).suffix;
    }

    public boolean setSuffix(UUID uuid, String suffix) {
        if (conn == null) return false;

        SuffixData existing = getSuffixData(uuid);
        boolean currentActive = existing.active;

        String sql;
        if (isSQLite) {
            sql = "INSERT OR REPLACE INTO catcraft_titles (uuid, suffix, is_active) VALUES (?, ?, ?)";
        } else {
            sql = "INSERT INTO catcraft_titles (uuid, suffix, is_active) VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE suffix = VALUES(suffix), is_active = VALUES(is_active)";
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setString(2, suffix);
            ps.setInt(3, currentActive ? 1 : 0);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 改为参数化查询，不再拼接字符串
    public boolean setSuffixActive(UUID uuid, boolean active) {
        if (conn == null) return false;
        String sql = "UPDATE catcraft_titles SET is_active = ? WHERE uuid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, active);
            ps.setString(2, uuid.toString());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<TitleEntry> getPlayerTitles(UUID uuid) {
        List<TitleEntry> list = new ArrayList<>();
        if (conn == null) return list;
        String sql = "SELECT title_id, title_display, is_active FROM player_titles WHERE uuid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    boolean active = isSQLite ? rs.getInt("is_active") == 1 : rs.getBoolean("is_active");
                    list.add(new TitleEntry(
                            rs.getInt("title_id"),
                            rs.getString("title_display"),
                            active
                    ));
                }
            }
        } catch (Exception e) {
            plugin.getLogger().severe("获取玩家头衔列表失败");
            e.printStackTrace();
        }
        return list;
    }

    public boolean addOrUpdateTitle(UUID uuid, int id, String display) {
        if (conn == null) {
            plugin.getLogger().severe("数据库未连接，无法添加头衔");
            return false;
        }
        String sql;
        if (isSQLite) {
            sql = "INSERT OR REPLACE INTO player_titles (uuid, title_id, title_display) VALUES (?, ?, ?)";
        } else {
            sql = "INSERT INTO player_titles (uuid, title_id, title_display) VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE title_display = VALUES(title_display)";
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setInt(2, id);
            ps.setString(3, display);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            plugin.getLogger().severe("添加/更新头衔失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTitleDisplay(UUID uuid, int id, String newDisplay) {
        if (conn == null) return false;
        String sql = "UPDATE player_titles SET title_display = ? WHERE uuid = ? AND title_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newDisplay);
            ps.setString(2, uuid.toString());
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            plugin.getLogger().severe("更新头衔显示名失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeTitle(UUID uuid, int id) {
        if (conn == null) return false;
        String sql = "DELETE FROM player_titles WHERE uuid = ? AND title_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean setActiveTitle(UUID uuid, int id) {
        if (conn == null) return false;
        try {
            conn.setAutoCommit(false);
            // 清除所有激活状态
            String clearSql = "UPDATE player_titles SET is_active = ? WHERE uuid = ?";
            try (PreparedStatement ps = conn.prepareStatement(clearSql)) {
                ps.setBoolean(1, false);
                ps.setString(2, uuid.toString());
                ps.executeUpdate();
            }
            if (id > 0) {
                String setSql = "UPDATE player_titles SET is_active = ? WHERE uuid = ? AND title_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(setSql)) {
                    ps.setBoolean(1, true);
                    ps.setString(2, uuid.toString());
                    ps.setInt(3, id);
                    ps.executeUpdate();
                }
            }
            conn.commit();
            conn.setAutoCommit(true);
            return true;
        } catch (Exception e) {
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        }
    }

    public TitleEntry getActiveTitle(UUID uuid) {
        if (conn == null) return null;
        String sql = "SELECT title_id, title_display, is_active FROM player_titles WHERE uuid = ? AND is_active = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setBoolean(2, true);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new TitleEntry(
                            rs.getInt("title_id"),
                            rs.getString("title_display"),
                            true
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void disconnect() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                plugin.getLogger().info("✔ 数据库连接已关闭");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class TitleEntry {
        public int id;
        public String display;
        public boolean active;
        public TitleEntry(int id, String display, boolean active) {
            this.id = id;
            this.display = display;
            this.active = active;
        }
    }

    public static class SuffixData {
        public String suffix;
        public boolean active;
        public SuffixData(String suffix, boolean active) {
            this.suffix = suffix;
            this.active = active;
        }
    }
}