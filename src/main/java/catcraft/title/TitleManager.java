package catcraft.title;

import org.bukkit.entity.Player;
import java.util.*;

public class TitleManager {
    private final DatabaseManager database;
    private final boolean enableRgb;

    private final Map<UUID, Map<Integer, String>> ownedTitles = new HashMap<>();
    private final Map<UUID, Map<Integer, String>> ownedSuffixes = new HashMap<>();
    private final Map<UUID, Integer> activeTitleId = new HashMap<>();
    private final Map<UUID, Integer> activeSuffixId = new HashMap<>();

    private static final String DEFAULT_TITLE = "&d[&d萌新喵]&7";

    public TitleManager(DatabaseManager database) {
        this.database = database;
        this.enableRgb = CatCraftTitlePlugin.getInstance().getConfig().getBoolean("settings.enable-rgb", false);
    }

    public void load(Player p) {
        UUID uuid = p.getUniqueId();

        List<DatabaseManager.TitleEntry> titles = database.getPlayerTitles(uuid, 0);
        Map<Integer, String> titleMap = new HashMap<>();
        for (DatabaseManager.TitleEntry e : titles) {
            titleMap.put(e.id, e.display);
        }
        ownedTitles.put(uuid, titleMap);

        List<DatabaseManager.TitleEntry> suffixes = database.getPlayerTitles(uuid, 1);
        Map<Integer, String> suffixMap = new HashMap<>();
        for (DatabaseManager.TitleEntry e : suffixes) {
            suffixMap.put(e.id, e.display);
        }
        ownedSuffixes.put(uuid, suffixMap);

        DatabaseManager.TitleEntry activeTitle = database.getActiveTitle(uuid, 0);
        activeTitleId.put(uuid, activeTitle == null ? -1 : activeTitle.id);

        DatabaseManager.TitleEntry activeSuffix = database.getActiveTitle(uuid, 1);
        activeSuffixId.put(uuid, activeSuffix == null ? -1 : activeSuffix.id);
    }

    public void unload(Player p) {
        UUID uuid = p.getUniqueId();
        ownedTitles.remove(uuid);
        ownedSuffixes.remove(uuid);
        activeTitleId.remove(uuid);
        activeSuffixId.remove(uuid);
    }

    public String getTitle(Player p) {
        UUID uuid = p.getUniqueId();
        int id = activeTitleId.getOrDefault(uuid, -1);
        String raw;
        if (id == -1) {
            raw = DEFAULT_TITLE;
        } else {
            Map<Integer, String> map = ownedTitles.getOrDefault(uuid, new HashMap<>());
            raw = map.getOrDefault(id, DEFAULT_TITLE);
        }
        return applyFormat(raw);
    }

    public String getSuffix(Player p) {
        UUID uuid = p.getUniqueId();
        int id = activeSuffixId.getOrDefault(uuid, -1);
        if (id == -1) return "";
        Map<Integer, String> map = ownedSuffixes.getOrDefault(uuid, new HashMap<>());
        String raw = map.get(id);
        return raw == null ? "" : applyFormat(raw);
    }

    private String applyFormat(String raw) {
        if (enableRgb) {
            return RGBUtil.gradient(raw);
        } else {
            return ColorUtil.color(raw);
        }
    }

    public Map<Integer, String> getOwnedTitles(Player p) {
        return ownedTitles.getOrDefault(p.getUniqueId(), new HashMap<>());
    }

    public Map<Integer, String> getOwnedSuffixes(Player p) {
        return ownedSuffixes.getOrDefault(p.getUniqueId(), new HashMap<>());
    }

    public int getActiveTitleId(Player p) {
        return activeTitleId.getOrDefault(p.getUniqueId(), -1);
    }

    public int getActiveSuffixId(Player p) {
        return activeSuffixId.getOrDefault(p.getUniqueId(), -1);
    }

    public boolean addTitle(Player p, int id, String display) {
        return addTitle(p, id, display, 0);
    }

    public boolean addSuffix(Player p, int id, String display) {
        return addTitle(p, id, display, 1);
    }

    private boolean addTitle(Player p, int id, String display, int type) {
        UUID uuid = p.getUniqueId();
        if (ownedTitles.getOrDefault(uuid, new HashMap<>()).containsKey(id) ||
                ownedSuffixes.getOrDefault(uuid, new HashMap<>()).containsKey(id)) {
            return false;
        }
        if (database.addOrUpdateTitle(uuid, id, display, type)) {
            if (type == 0) {
                ownedTitles.computeIfAbsent(uuid, k -> new HashMap<>()).put(id, display);
            } else {
                ownedSuffixes.computeIfAbsent(uuid, k -> new HashMap<>()).put(id, display);
            }
            return true;
        }
        return false;
    }

    public boolean updateTitleDisplay(Player p, int id, String newDisplay) {
        UUID uuid = p.getUniqueId();
        Map<Integer, String> titles = ownedTitles.getOrDefault(uuid, new HashMap<>());
        Map<Integer, String> suffixes = ownedSuffixes.getOrDefault(uuid, new HashMap<>());
        if (!titles.containsKey(id) && !suffixes.containsKey(id)) return false;

        if (database.updateTitleDisplay(uuid, id, newDisplay)) {
            if (titles.containsKey(id)) titles.put(id, newDisplay);
            if (suffixes.containsKey(id)) suffixes.put(id, newDisplay);
            return true;
        }
        return false;
    }

    public boolean removeTitle(Player p, int id) {
        UUID uuid = p.getUniqueId();
        if (activeTitleId.getOrDefault(uuid, -1) == id || activeSuffixId.getOrDefault(uuid, -1) == id) {
            return false;
        }
        if (database.removeTitle(uuid, id)) {
            ownedTitles.getOrDefault(uuid, new HashMap<>()).remove(id);
            ownedSuffixes.getOrDefault(uuid, new HashMap<>()).remove(id);
            return true;
        }
        return false;
    }

    public boolean activateTitle(Player p, int id) {
        UUID uuid = p.getUniqueId();
        if (!ownedTitles.getOrDefault(uuid, new HashMap<>()).containsKey(id)) return false;
        if (database.setActiveTitle(uuid, id, 0)) {
            activeTitleId.put(uuid, id);
            return true;
        }
        return false;
    }

    public boolean deactivateTitle(Player p) {
        UUID uuid = p.getUniqueId();
        if (database.deactivateType(uuid, 0)) {
            activeTitleId.put(uuid, -1);
            return true;
        }
        return false;
    }

    public boolean activateSuffix(Player p, int id) {
        UUID uuid = p.getUniqueId();
        if (!ownedSuffixes.getOrDefault(uuid, new HashMap<>()).containsKey(id)) return false;
        if (database.setActiveTitle(uuid, id, 1)) {
            activeSuffixId.put(uuid, id);
            return true;
        }
        return false;
    }

    public boolean deactivateSuffix(Player p) {
        UUID uuid = p.getUniqueId();
        if (database.deactivateType(uuid, 1)) {
            activeSuffixId.put(uuid, -1);
            return true;
        }
        return false;
    }
}