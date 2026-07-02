package catcraft.title;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private final TitleManager manager;

    public ChatListener(TitleManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        String title = manager.getTitle(e.getPlayer());
        String suffix = manager.getSuffix(e.getPlayer());

        String msg = title + e.getPlayer().getName() + suffix + ": " + e.getMessage();
        e.setFormat(ColorUtil.color(msg));
    }
}