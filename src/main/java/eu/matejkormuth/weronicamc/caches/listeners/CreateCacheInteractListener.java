package eu.matejkormuth.weronicamc.caches.listeners;

import eu.matejkormuth.weronicamc.caches.Cache;
import eu.matejkormuth.weronicamc.caches.CacheStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CreateCacheInteractListener implements Listener {

    private final Set<Player> creating;
    private final Map<Player, Double> rewards;
    private final Set<Player> editing;
    private final CacheStorage cacheStorage;

    public CreateCacheInteractListener(CacheStorage cacheStorage) {
        this.cacheStorage = cacheStorage;
        creating = new HashSet<>();
        rewards = new HashMap<>();
        editing = new HashSet<>();
    }

    public void addCreate(Player player, double reward) {
        this.creating.add(player);
        this.rewards.put(player, reward);
    }

    public void setEditing(Player player, boolean editing) {
        if (editing) {
            this.editing.add(player);
        } else {
            this.editing.remove(player);
        }
    }

    public boolean isEditing(Player player) {
        return editing.contains(player);
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
