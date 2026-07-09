package catcraft.title;

import org.bukkit.entity.Player;
import java.util.List;
import java.util.UUID;

public class ShopManager {
    private final DatabaseManager database;
    private final TitleManager titleManager;
    private final boolean enabled;
    private final int signinReward;

    public ShopManager(DatabaseManager database, TitleManager titleManager, boolean enabled, int signinReward) {
        this.database = database;
        this.titleManager = titleManager;
        this.enabled = enabled;
        this.signinReward = signinReward;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean addShopItem(int id, int type, String display, int price) {
        return enabled && database.addShopItem(id, type, display, price);
    }

    public boolean removeShopItem(int id, int type) {
        return enabled && database.removeShopItem(id, type);
    }

    public boolean setShopItemPrice(int id, int type, int newPrice) {
        return enabled && database.updateShopItemPrice(id, type, newPrice);
    }

    public List<ShopItem> getShopItems() {
        return enabled ? database.getShopItems() : java.util.Collections.emptyList();
    }

    public List<ShopItem> getShopItemsByType(int type) {
        return enabled ? database.getShopItemsByType(type) : java.util.Collections.emptyList();
    }

    public ShopItem getShopItem(int id, int type) {
        return enabled ? database.getShopItem(id, type) : null;
    }

    public int getBalance(UUID uuid) {
        return enabled ? database.getBalance(uuid) : 0;
    }

    public boolean setBalance(UUID uuid, int amount) {
        return enabled && database.setBalance(uuid, amount);
    }

    public boolean addBalance(UUID uuid, int amount) {
        return enabled && database.addBalance(uuid, amount);
    }

    public boolean canSignin(UUID uuid) {
        if (!enabled) return false;
        long last = database.getLastSignin(uuid);
        long now = System.currentTimeMillis();
        java.time.LocalDate lastDate = java.time.Instant.ofEpochMilli(last).atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        java.time.LocalDate nowDate = java.time.LocalDate.now();
        return !lastDate.equals(nowDate);
    }

    public boolean signin(Player player) {
        if (!enabled) return false;
        UUID uuid = player.getUniqueId();
        if (!canSignin(uuid)) return false;
        if (!database.setLastSignin(uuid, System.currentTimeMillis())) return false;
        if (!database.addBalance(uuid, signinReward)) {
            return false;
        }
        return true;
    }

    public int getSigninReward() {
        return signinReward;
    }

    public boolean purchase(Player player, int id, int type) {
        if (!enabled) return false;
        UUID uuid = player.getUniqueId();

        if (type == 0 && titleManager.getOwnedTitles(player).containsKey(id)) return false;
        if (type == 1 && titleManager.getOwnedSuffixes(player).containsKey(id)) return false;

        ShopItem item = getShopItem(id, type);
        if (item == null) return false;

        int price = item.getPrice();
        int balance = getBalance(uuid);
        if (balance < price) return false;

        if (!database.addBalance(uuid, -price)) return false;

        boolean added = (type == 0) ?
                titleManager.addTitle(player, id, item.getDisplay()) :
                titleManager.addSuffix(player, id, item.getDisplay());

        if (!added) {
            database.addBalance(uuid, price);
            return false;
        }

        titleManager.load(player);
        return true;
    }
}