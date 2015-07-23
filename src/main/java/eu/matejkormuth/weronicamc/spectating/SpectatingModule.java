/**
 * WeronicaMC - Plugin for fantasy and creative server.
 * Copyright (c) 2015, Matej Kormuth <http://www.github.com/dobrakmato>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
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
package eu.matejkormuth.weronicamc.spectating;

import com.google.common.base.Preconditions;
import eu.matejkormuth.weronicamc.Dependency;
import eu.matejkormuth.weronicamc.Module;
import eu.matejkormuth.weronicamc.PluginAccessor;
import eu.matejkormuth.weronicamc.spectating.commands.SpectateCommandExecutor;
import eu.matejkormuth.weronicamc.spectating.commands.UnspectateCommandExecutor;
import eu.matejkormuth.weronicamc.spectating.listeners.ConsistencyPlayerLeaveListener;
import eu.matejkormuth.weronicamc.spectating.listeners.SpectatingInputListener;
import eu.matejkormuth.weronicamc.vault.VaultModule;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpectatingModule extends Module {

    // Spectator -> Spectated
    private final Map<Player, Player> spectating;
    // Spectated -> List of spectatedToSpectators.
    private final Map<Player, List<Player>> spectatedToSpectators;
    // Inventory revert storage.
    private final Map<Player, ItemStack[]> inventoryStorage;

    @Dependency
    private VaultModule vaultModule; // For permissions.

    public SpectatingModule() {
        spectating = new HashMap<>();
        spectatedToSpectators = new HashMap<>();
        inventoryStorage = new HashMap<>();
    }

    @Override
    public void onEnable() {
        JavaPlugin plugin = new PluginAccessor(this).getPlugin();

        // Register commands.
        plugin.getCommand("spectate").setExecutor(new SpectateCommandExecutor(this, vaultModule.getPermissions()));
        plugin.getCommand("unspectate").setExecutor(new UnspectateCommandExecutor(this, vaultModule.getPermissions()));

        // Initialize listeners.
        listener(new SpectatingInputListener(this));
        listener(new ConsistencyPlayerLeaveListener(this));
    }

    /**
     * Adds spectating entry.
     *
     * @param spectator player who is spectating
     * @param spectated player who is spectated
     * @throws IllegalArgumentException when arguments are illegal
     */
    public void addSpectating(Player spectator, Player spectated) {
        Preconditions.checkNotNull(spectator);
        Preconditions.checkNotNull(spectated);

        if (isSpectated(spectator) || isSpectating(spectator)) {
            throw new IllegalArgumentException("Spectator is either already spectating someone or is being spectated!");
        }

        if (isSpectating(spectated)) {
            throw new IllegalArgumentException("Spectated player is already spectating someone!");
        }

        // Add to spectating map.
        this.spectating.put(spectator, spectated);
        // Add as to list of spectatedToSpectators.
        if (spectatedToSpectators.containsKey(spectated)) {
            spectatedToSpectators.get(spectated).add(spectator);
        } else {
            spectatedToSpectators.put(spectated, new ArrayList<>());
            spectatedToSpectators.get(spectated).add(spectator);
        }

        // Save inventory.
        inventoryStorage.put(spectator, spectator.getInventory().getContents());

        // Set new inventory.
        ItemStack[] contents = spectated.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            spectator.getInventory().setItem(i, contents[i]);
        }
    }

    /**
     * Removes all entries for specified spectator.
     *
     * @param spectator player who is spectating
     * @throws IllegalArgumentException when arguments are illegal
     */
    public void removeSpecatating(Player spectator) {
        Preconditions.checkNotNull(spectator);

        // Get spectated player form map.
        Player spectated = spectating.get(spectator);

        if (!isSpectating(spectator)) {
            throw new IllegalArgumentException("Specified spectator is not spectating anyone!");
        }

        if (!isSpectated(spectated)) {
            throw new IllegalArgumentException("Specified spectated is not spectated by anyone!");
        }

        // Remove him as spectator.
        this.spectating.remove(spectator);
        // Remove him from list of spectators.
        spectatedToSpectators.get(spectated).remove(spectator);
        // If no one else is spectating remove this from map of spectations.
        if (spectatedToSpectators.get(spectated).size() == 0) {
            spectatedToSpectators.remove(spectated);
        }

        // Revert inventory.
        ItemStack[] contents = inventoryStorage.get(spectator);
        for (int i = 0; i < contents.length; i++) {
            spectator.getInventory().setItem(i, contents[i]);
        }
        inventoryStorage.remove(spectator);

    }

    public boolean isSpectating(Player player) {
        return spectating.containsKey(player);
    }

    public boolean isSpectated(Player player) {
        return spectating.containsValue(player);
    }

    @Override
    public void onDisable() {
        spectating.clear();
    }

    public List<? extends Player> getSpectators(Player player) {
        return new ArrayList<>(spectatedToSpectators.get(player));
    }
}
