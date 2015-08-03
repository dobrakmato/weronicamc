package eu.matejkormuth.weronicamc.caches.listeners;

import eu.matejkormuth.weronicamc.caches.Cache;
import eu.matejkormuth.weronicamc.caches.CachePlayerStorage;
import eu.matejkormuth.weronicamc.caches.CacheStorage;
import eu.matejkormuth.weronicamc.translations.TranslationPack;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.function.Predicate;

public class ChestInteractListener implements Listener {

    /**
     * Predicate that tells whether player opens chest.
     */
    private static final Predicate<PlayerInteractEvent> PLAYER_OPENS_CHEST = event -> event.getAction() == Action.RIGHT_CLICK_BLOCK &&
            event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.CHEST;

    private final CacheStorage cacheStorage;
    private final CachePlayerStorage cachePlayerStorage;
    private final Economy economy;
    private final TranslationPack translationPack;
    private final Permission permission;
    private final CreateCacheInteractListener createCacheInteractListener;

    public ChestInteractListener(CacheStorage cacheStorage, CachePlayerStorage cachePlayerStorage,
                                 Economy economy, TranslationPack translationPack, Permission permission,
                                 CreateCacheInteractListener createCacheInteractListener) {
        this.cacheStorage = cacheStorage;
        this.cachePlayerStorage = cachePlayerStorage;
        this.economy = economy;
        this.translationPack = translationPack;
        this.permission = permission;
        this.createCacheInteractListener = createCacheInteractListener;
    }

    @EventHandler
    private void onPlayerInteractBlock(final PlayerInteractEvent event) {
        if (PLAYER_OPENS_CHEST.test(event)) {
            // Request optional cache object for clicked chest.
            Optional<Cache> cacheOptional = cacheStorage.get(event.getClickedBlock().getLocation());

            // If cache object was found, use it.
            if (cacheOptional.isPresent()) {
                Optional<Cache> previousCache = cacheStorage.get(cacheOptional.get().getPreviousCacheId());

                // Check if player found previous cache.
                if (previousCache.isPresent() && !cachePlayerStorage.hasFound(event.getPlayer(),
                        previousCache.get())) {
                    // Plugin should do nothing.
                    return;
                }

                // TODO: If player has edit mode.
                if (!createCacheInteractListener.isEditing(event.getPlayer())) {
                    event.setCancelled(true);

                    Chest chest = (Chest) event.getClickedBlock().getState();
                    Inventory fakeInventory = Bukkit.createInventory(null, InventoryType.CHEST);
                    for (int i = 0; i < chest.getBlockInventory().getSize(); i++) {
                        ItemStack is = chest.getBlockInventory().getItem(i);
                        if (is != null) {
                            fakeInventory.setItem(i, is);
                        }
                    }

                    // Open fake inventory.
                    event.getPlayer().openInventory(fakeInventory);

                    // Give him reward.
                    this.processFound(cacheOptional.get(), event.getPlayer());
                }
            }
        }
    }

    private void processFound(Cache cache, Player player) {
        if (cachePlayerStorage.hasFound(player, cache)) {
            player.sendMessage(translationPack.format("cache_open_next"));
        } else {
            int foundCaches = 0;
            int totalCaches = 0;
            if (foundCaches == totalCaches) {
                double amount = cache.getReward();
                player.sendMessage(translationPack.format("cache_latest_open"));
                economy.depositPlayer(player, amount);
            } else {
                // Add him some monetary reward.
                double amount = cache.getReward();
                player.sendMessage(translationPack.format("cache_open_first", foundCaches, totalCaches, amount));
                economy.depositPlayer(player, amount);
            }
        }
    }
}
