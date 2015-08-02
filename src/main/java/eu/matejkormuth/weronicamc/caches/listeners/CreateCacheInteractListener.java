package eu.matejkormuth.weronicamc.caches.listeners;

import eu.matejkormuth.weronicamc.caches.Cache;
import eu.matejkormuth.weronicamc.caches.CacheStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateCacheInteractListener implements Listener {

    private final List<Player> creating;
    private final Map<Player, Double> rewards;
    private final CacheStorage cacheStorage;

    public CreateCacheInteractListener(CacheStorage cacheStorage) {
        this.cacheStorage = cacheStorage;
        creating = new ArrayList<>();
        rewards = new HashMap<>();
    }

    public void addCreate(Player player, double reward) {
        this.creating.add(player);
        this.rewards.put(player, reward);
    }

    @EventHandler
    private void onPlayerLeave(final PlayerQuitEvent event) {
        creating.remove(event.getPlayer());
        rewards.remove(event.getPlayer());
    }

    @EventHandler
    private void onPlayerClick(final PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (creating.contains(event.getPlayer())) {
                // Create cache.
                Cache c = cacheStorage.createCache(event.getClickedBlock().getLocation());
                c.setReward(rewards.get(event.getPlayer()));

                // TODO: Output messages

                // Remove from lists.
                creating.remove(event.getPlayer());
                rewards.remove(event.getPlayer());

                // Cancel possible chest break.
                event.setCancelled(true);
            }
        }
    }
}
