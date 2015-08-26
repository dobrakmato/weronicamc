package eu.matejkormuth.weronicamc.caches.listeners;

import eu.matejkormuth.weronicamc.caches.ScoreboardManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ScoreboardListener implements Listener {

    private final ScoreboardManager manager;

    public ScoreboardListener(ScoreboardManager manager) {
        this.manager = manager;
    }

    @EventHandler
    private void onPlayerJoin(final PlayerJoinEvent event) {
        manager.add(event.getPlayer());
    }

    @EventHandler
    private void onPlayerLeave(final PlayerQuitEvent event) {
        manager.remove(event.getPlayer());
    }
}
