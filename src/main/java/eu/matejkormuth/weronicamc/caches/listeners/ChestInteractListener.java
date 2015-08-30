/**
 * WeronicaMC - Plugin for fantasy and creative server.
 * Copyright (c) 2015, Matej Kormuth <http://www.github.com/dobrakmato>
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * <p>
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * <p>
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Predicate;

public class ChestInteractListener implements Listener {

    private static final Logger log = LoggerFactory.getLogger(ChestInteractListener.class);

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

                // If player is not in edit mode.
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
            // Set cache as found.
            cachePlayerStorage.setFound(player, cache);

            // Process onFound commands.
            try {
                // Execute all commands.
                for (String s : cache.getOnFound()) {
                    Bukkit.getServer().getConsoleSender().sendMessage(s.replace("@p", player.getName()));
                }
            } catch (Exception e) {
                log.error("Can't execute onFound command!", e);
            }

            // Process stuff.
            int foundCaches = cachePlayerStorage.getFoundCount(player);
            int totalCaches = cacheStorage.size();
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
