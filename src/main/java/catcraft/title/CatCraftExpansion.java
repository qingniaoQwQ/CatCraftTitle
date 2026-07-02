package catcraft.title;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class CatCraftExpansion extends PlaceholderExpansion {

    private final TitleManager manager;

    public CatCraftExpansion(TitleManager manager) {
        this.manager = manager;
    }

    @Override
    public String getIdentifier() {
        return "catcraft";
    }

    @Override
    public String getAuthor() {
        return "CatCraft";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (player == null) return "";

        if (params.equals("title")) {
            return manager.getTitle(player);
        }

        if (params.equals("suffix")) {
            return manager.getSuffix(player);
        }

        return null;
    }
}