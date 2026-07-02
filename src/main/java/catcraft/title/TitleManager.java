package catcraft.title;

import org.bukkit.entity.Player;
import java.util.*;

public class TitleManager {
    private final DatabaseManager database;
    private final Map<UUID, Map<Integer, String>> ownedTitles = new HashMap<>();
    private final Map<UUID, String> activeDisplay = new HashMap<>();
    private final Map<UUID, DatabaseManager.SuffixData> suffixCache = new HashMap<>();

    private static final String DEFAULT_TITLE = "&d[&d萌新喵]&7";

    public TitleManager(DatabaseManager database) {
        this.database = database;
    }

    public void load(Player p) {
        UUID uuid = p.getUniqueId();


        List<DatabaseManager.TitleEntry> entries = database.getPlayerTitles(uuid);
        Map<Integer, String> map = new HashMap<>();
        for (DatabaseManager.TitleEntry e : entries) {
            map.put(e.id, e.display);
        }
        ownedTitles.put(uuid, map);


        DatabaseManager.TitleEntry active = database.getActiveTitle(uuid);
        if (active != null) {
            activeDisplay.put(uuid, ColorUtil.color(active.display));
        } else {
            activeDisplay.put(uuid, ColorUtil.color(DEFAULT_TITLE));
        }


        DatabaseManager.SuffixData suffixData = database.getSuffixData(uuid);
        suffixCache.put(uuid, suffixData);
    }

    public void unload(Player p) {
        UUID uuid = p.getUniqueId();
        ownedTitles.remove(uuid);
        activeDisplay.remove(uuid);
        suffixCache.remove(uuid);
    }

    public String getTitle(Player p) {
        return activeDisplay.getOrDefault(p.getUniqueId(), ColorUtil.color(DEFAULT_TITLE));
    }

    public String getSuffix(Player p) {
        UUID uuid = p.getUniqueId();
        DatabaseManager.SuffixData data = suffixCache.get(uuid);
        if (data != null && data.active && data.suffix != null) {
            return data.suffix;
        }
        return "";
    }

    public DatabaseManager.SuffixData getSuffixData(Player p) {
        return suffixCache.getOrDefault(p.getUniqueId(), new DatabaseManager.SuffixData("", true));
    }

    public boolean setSuffix(Player p, String suffix) {
        UUID uuid = p.getUniqueId();
        if (database.setSuffix(uuid, suffix)) {
            DatabaseManager.SuffixData data = suffixCache.getOrDefault(uuid, new DatabaseManager.SuffixData("", true));
            suffixCache.put(uuid, new DatabaseManager.SuffixData(suffix, data.active));
            return true;
        }
        return false;
    }

    public boolean setSuffixActive(Player p, boolean active) {
        UUID uuid = p.getUniqueId();
        if (database.setSuffixActive(uuid, active)) {
            DatabaseManager.SuffixData data = suffixCache.getOrDefault(uuid, new DatabaseManager.SuffixData("", true));
            suffixCache.put(uuid, new DatabaseManager.SuffixData(data.suffix, active));
            return true;
        }
        return false;
    }

    public Map<Integer, String> getOwnedTitles(Player p) {
        return ownedTitles.getOrDefault(p.getUniqueId(), new HashMap<>());
    }

    public boolean addTitle(Player p, int id, String display) {
        UUID uuid = p.getUniqueId();
        Map<Integer, String> current = ownedTitles.get(uuid);
        if (current != null && current.containsKey(id)) {
            return false;
        }
        if (database.addOrUpdateTitle(uuid, id, display)) {
            ownedTitles.computeIfAbsent(uuid, k -> new HashMap<>()).put(id, display);
            return true;
        }
        return false;
    }

    public boolean updateTitleDisplay(Player p, int id, String newDisplay) {
        UUID uuid = p.getUniqueId();
        if (!ownedTitles.getOrDefault(uuid, new HashMap<>()).containsKey(id)) {
            return false;
        }
        if (database.updateTitleDisplay(uuid, id, newDisplay)) {
            ownedTitles.get(uuid).put(id, newDisplay);
            DatabaseManager.TitleEntry active = database.getActiveTitle(uuid);
            if (active != null && active.id == id) {
                activeDisplay.put(uuid, ColorUtil.color(newDisplay));
            }
            return true;
        }
        return false;
    }

    public boolean removeTitle(Player p, int id) {
        UUID uuid = p.getUniqueId();
        DatabaseManager.TitleEntry active = database.getActiveTitle(uuid);
        if (active != null && active.id == id) {
            return false;
        }
        if (database.removeTitle(uuid, id)) {
            Map<Integer, String> map = ownedTitles.get(uuid);
            if (map != null) map.remove(id);
            return true;
        }
        return false;
    }

    public boolean activateTitle(Player p, int id) {
        UUID uuid = p.getUniqueId();
        if (!ownedTitles.getOrDefault(uuid, new HashMap<>()).containsKey(id)) {
            return false;
        }
        if (database.setActiveTitle(uuid, id)) {
            String display = ownedTitles.get(uuid).get(id);
            activeDisplay.put(uuid, ColorUtil.color(display));
            return true;
        }
        return false;
    }

    public boolean deactivateTitle(Player p) {
        UUID uuid = p.getUniqueId();
        if (database.setActiveTitle(uuid, -1)) {
            activeDisplay.put(uuid, ColorUtil.color(DEFAULT_TITLE));
            return true;
        }
        return false;
    }


    public int getActiveId(Player p) {
        UUID uuid = p.getUniqueId();
        DatabaseManager.TitleEntry active = database.getActiveTitle(uuid);
        return active == null ? -1 : active.id;
    }
}