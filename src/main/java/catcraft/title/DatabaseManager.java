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
            createShopTables();
            createSigninTable();
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

    private void createShopTables() {
        try (Statement stmt = conn.createStatement()) {
            String sqlItems = "CREATE TABLE IF NOT EXISTS shop_items (" +
                    "id INT PRIMARY KEY, " +
                    "type INT NOT NULL, " +
                    "display VARCHAR(128) NOT NULL, " +
                    "price INT NOT NULL)";
            stmt.execute(sqlItems);

            String sqlBal = "CREATE TABLE IF NOT EXISTS player_balances (" +
                    "uuid VARCHAR(36) PRIMARY KEY, " +
                    "balance INT NOT NULL DEFAULT 0)";
            stmt.execute(sqlBal);
            plugin.getLogger().info("商店表创建/检查完成。");
        } catch (SQLException e) {
            plugin.getLogger().severe("创建商店表失败！");
            e.printStackTrace();
        }
    }

    private void createSigninTable() {
        try (Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS player_signin (" +
                    "uuid VARCHAR(36) PRIMARY KEY, " +
                    "last_signin BIGINT NOT NULL DEFAULT 0)";
            stmt.execute(sql);
            plugin.getLogger().info("签到表创建/检查完成。");
        } catch (SQLException e) {
            plugin.getLogger().severe("创建签到表失败！");
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

    public boolean addShopItem(int id, int type, String display, int price) {
        if (conn == null) return false;
        String sql = "INSERT INTO shop_items (id, type, display, price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, type);
            ps.setString(3, display);
            ps.setInt(4, price);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean removeShopItem(int id, int type) {
        if (conn == null) return false;
        String sql = "DELETE FROM shop_items WHERE id = ? AND type = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, type);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean updateShopItemPrice(int id, int type, int newPrice) {
        if (conn == null) return false;
        String sql = "UPDATE shop_items SET price = ? WHERE id = ? AND type = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newPrice);
            ps.setInt(2, id);
            ps.setInt(3, type);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public ShopItem getShopItem(int id, int type) {
        if (conn == null) return null;
        String sql = "SELECT id, type, display, price FROM shop_items WHERE id = ? AND type = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, type);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ShopItem(rs.getInt("id"), rs.getInt("type"),
                            rs.getString("display"), rs.getInt("price"));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public List<ShopItem> getShopItems() {
        List<ShopItem> list = new ArrayList<>();
        if (conn == null) return list;
        String sql = "SELECT id, type, display, price FROM shop_items ORDER BY id";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new ShopItem(rs.getInt("id"), rs.getInt("type"),
                        rs.getString("display"), rs.getInt("price")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<ShopItem> getShopItemsByType(int type) {
        List<ShopItem> list = new ArrayList<>();
        if (conn == null) return list;
        String sql = "SELECT id, type, display, price FROM shop_items WHERE type = ? ORDER BY id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, type);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ShopItem(rs.getInt("id"), rs.getInt("type"),
                            rs.getString("display"), rs.getInt("price")));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public int getBalance(UUID uuid) {
        if (conn == null) return 0;
        String sql = "SELECT balance FROM player_balances WHERE uuid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("balance");
            }
        } catch (Exception e) { e.printStackTrace(); }
        setBalance(uuid, 0);
        return 0;
    }

    public boolean setBalance(UUID uuid, int amount) {
        if (conn == null) return false;
        String sql;
        if (dbType.equals("sqlite")) {
            sql = "INSERT OR REPLACE INTO player_balances (uuid, balance) VALUES (?, ?)";
        } else if (dbType.equals("postgresql")) {
            sql = "INSERT INTO player_balances (uuid, balance) VALUES (?, ?) ON CONFLICT (uuid) DO UPDATE SET balance = EXCLUDED.balance";
        } else {
            sql = "INSERT INTO player_balances (uuid, balance) VALUES (?, ?) ON DUPLICATE KEY UPDATE balance = VALUES(balance)";
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setInt(2, amount);
            ps.executeUpdate();
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean addBalance(UUID uuid, int delta) {
        if (conn == null) return false;
        String sql = "UPDATE player_balances SET balance = balance + ? WHERE uuid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setString(2, uuid.toString());
            int affected = ps.executeUpdate();
            if (affected == 0) {
                setBalance(uuid, Math.max(0, delta));
            }
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public long getLastSignin(UUID uuid) {
        if (conn == null) return 0;
        String sql = "SELECT last_signin FROM player_signin WHERE uuid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong("last_signin");
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    public boolean setLastSignin(UUID uuid, long time) {
        if (conn == null) return false;
        String sql;
        if (dbType.equals("sqlite")) {
            sql = "INSERT OR REPLACE INTO player_signin (uuid, last_signin) VALUES (?, ?)";
        } else if (dbType.equals("postgresql")) {
            sql = "INSERT INTO player_signin (uuid, last_signin) VALUES (?, ?) ON CONFLICT (uuid) DO UPDATE SET last_signin = EXCLUDED.last_signin";
        } else {
            sql = "INSERT INTO player_signin (uuid, last_signin) VALUES (?, ?) ON DUPLICATE KEY UPDATE last_signin = VALUES(last_signin)";
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setLong(2, time);
            ps.executeUpdate();
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean isTitleIdUsedByAnyPlayer(int id) {
        if (conn == null) return false;
        String sql = "SELECT 1 FROM player_titles WHERE title_id = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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