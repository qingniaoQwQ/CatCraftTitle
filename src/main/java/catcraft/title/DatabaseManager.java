package catcraft.title;

import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.sql.*;
import java.util.*;

public class DatabaseManager {
    private final JavaPlugin plugin;
    private Connection conn;
    private boolean connected = false;
    private String dbType;
    private String host, port, database, user, password;
    private String sqliteFile;

    public DatabaseManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.dbType = plugin.getConfig().getString("database.type", "sqlite").toLowerCase();
        if (dbType.equals("mysql") || dbType.equals("postgresql")) {
            this.host = plugin.getConfig().getString("database." + dbType + ".host", "localhost");
            this.port = plugin.getConfig().getString("database." + dbType + ".port", dbType.equals("mysql") ? "3306" : "5432");
            this.database = plugin.getConfig().getString("database." + dbType + ".db", "minecraft");
            this.user = plugin.getConfig().getString("database." + dbType + ".user", "root");
            this.password = plugin.getConfig().getString("database." + dbType + ".pass", "");
        } else {
            this.sqliteFile = plugin.getConfig().getString("database.sqlite.file", "catcraft.db");
        }
    }

    public void connect() {
        try {
            if (dbType.equals("mysql")) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                String url = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8", host, port, database);
                conn = DriverManager.getConnection(url, user, password);
                plugin.getLogger().info("MySQL连接成功");
            } else if (dbType.equals("postgresql")) {
                Class.forName("org.postgresql.Driver");
                String url = String.format("jdbc:postgresql://%s:%s/%s", host, port, database);
                conn = DriverManager.getConnection(url, user, password);
                plugin.getLogger().info("PostgreSQL连接成功");
            } else {
                Class.forName("org.sqlite.JDBC");
                File dbFile = new File(plugin.getDataFolder(), sqliteFile);
                if (!dbFile.getParentFile().exists()) dbFile.getParentFile().mkdirs();
                String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
                conn = DriverManager.getConnection(url);
                plugin.getLogger().info("SQLite连接成功，文件: " + dbFile.getName());
            }
            createTables();
            connected = true;
        } catch (Exception e) {
            plugin.getLogger().severe("数据库连接失败！");
            e.printStackTrace();
            connected = false;
        }
    }

    public boolean isConnected() { return connected; }
    public String getDatabaseType() { return dbType; }

    private void createTables() {
        try (Statement stmt = conn.createStatement()) {
            boolean isSqlite = dbType.equals("sqlite");
            String sql = isSqlite ?
                    "CREATE TABLE IF NOT EXISTS player_titles (uuid VARCHAR(36) NOT NULL, title_id INTEGER NOT NULL, title_display VARCHAR(128) NOT NULL, type INTEGER DEFAULT 0, is_active INTEGER DEFAULT 0, PRIMARY KEY (uuid, title_id))" :
                    "CREATE TABLE IF NOT EXISTS player_titles (uuid VARCHAR(36) NOT NULL, title_id INT NOT NULL, title_display VARCHAR(128) NOT NULL, type INT DEFAULT 0, is_active BOOLEAN DEFAULT FALSE, PRIMARY KEY (uuid, title_id))";
            stmt.execute(sql);
        } catch (SQLException e) {
            plugin.getLogger().severe("创建表失败！");
            e.printStackTrace();
        }
    }

    public List<TitleEntry> getPlayerTitles(UUID uuid, int type) {
        List<TitleEntry> list = new ArrayList<>();
        if (conn == null) return list;
        String sql = "SELECT title_id, title_display, is_active FROM player_titles WHERE uuid = ? AND type = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setInt(2, type);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    boolean active = dbType.equals("sqlite") ? rs.getInt("is_active") == 1 : rs.getBoolean("is_active");
                    list.add(new TitleEntry(rs.getInt("title_id"), rs.getString("title_display"), active));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean addOrUpdateTitle(UUID uuid, int id, String display, int type) {
        if (conn == null) return false;
        String sql;
        if (dbType.equals("sqlite")) {
            sql = "INSERT OR REPLACE INTO player_titles (uuid, title_id, title_display, type) VALUES (?, ?, ?, ?)";
        } else if (dbType.equals("postgresql")) {
            sql = "INSERT INTO player_titles (uuid, title_id, title_display, type) VALUES (?, ?, ?, ?) ON CONFLICT (uuid, title_id) DO UPDATE SET title_display = EXCLUDED.title_display, type = EXCLUDED.type";
        } else {
            sql = "INSERT INTO player_titles (uuid, title_id, title_display, type) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE title_display = VALUES(title_display), type = VALUES(type)";
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setInt(2, id);
            ps.setString(3, display);
            ps.setInt(4, type);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean updateTitleDisplay(UUID uuid, int id, String newDisplay) {
        if (conn == null) return false;
        String sql = "UPDATE player_titles SET title_display = ? WHERE uuid = ? AND title_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newDisplay);
            ps.setString(2, uuid.toString());
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean removeTitle(UUID uuid, int id) {
        if (conn == null) return false;
        String sql = "DELETE FROM player_titles WHERE uuid = ? AND title_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean setActiveTitle(UUID uuid, int id, int type) {
        if (conn == null) return false;
        try {
            conn.setAutoCommit(false);
            String clearSql = "UPDATE player_titles SET is_active = ? WHERE uuid = ? AND type = ?";
            try (PreparedStatement ps = conn.prepareStatement(clearSql)) {
                ps.setBoolean(1, false);
                ps.setString(2, uuid.toString());
                ps.setInt(3, type);
                ps.executeUpdate();
            }
            if (id > 0) {
                String setSql = "UPDATE player_titles SET is_active = ? WHERE uuid = ? AND title_id = ? AND type = ?";
                try (PreparedStatement ps = conn.prepareStatement(setSql)) {
                    ps.setBoolean(1, true);
                    ps.setString(2, uuid.toString());
                    ps.setInt(3, id);
                    ps.setInt(4, type);
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

    public TitleEntry getActiveTitle(UUID uuid, int type) {
        if (conn == null) return null;
        String sql = "SELECT title_id, title_display, is_active FROM player_titles WHERE uuid = ? AND type = ? AND is_active = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setInt(2, type);
            ps.setBoolean(3, true);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new TitleEntry(rs.getInt("title_id"), rs.getString("title_display"), true);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public boolean deactivateType(UUID uuid, int type) {
        if (conn == null) return false;
        String sql = "UPDATE player_titles SET is_active = ? WHERE uuid = ? AND type = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, false);
            ps.setString(2, uuid.toString());
            ps.setInt(3, type);
            ps.executeUpdate();
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public void disconnect() {
        try {
            if (conn != null && !conn.isClosed()) { conn.close(); plugin.getLogger().info("数据库连接已关闭"); }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static class TitleEntry {
        public int id; public String display; public boolean active;
        public TitleEntry(int id, String display, boolean active) { this.id = id; this.display = display; this.active = active; }
    }
}