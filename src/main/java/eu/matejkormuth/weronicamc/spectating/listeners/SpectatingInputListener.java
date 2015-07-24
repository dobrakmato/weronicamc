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
package eu.matejkormuth.weronicamc.spectating.listeners;

import eu.matejkormuth.weronicamc.spectating.SpectatingModule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public class SpectatingInputListener implements Listener {
    private final SpectatingModule spectatingModule;

    public SpectatingInputListener(SpectatingModule spectatingModule) {
        this.spectatingModule = spectatingModule;
    }

    @EventHandler
    private void onPlayerMove(final PlayerMoveEvent event) {
        // Check if this player is spectated by someone.
        if (spectatingModule.isSpectated(event.getPlayer())) {
            // Teleport all spectators.
            for (Player p : spectatingModule.getSpectators(event.getPlayer())) {
                p.teleport(event.getTo());
            }
        }
    }

    @EventHandler
    private void onSelectedSlotChange(final PlayerItemHeldEvent event) {
        // Check if this player is spectated by someone.
        if (spectatingModule.isSpectated(event.getPlayer())) {
            // Set selected slot to all spectators.
            for (Player p : spectatingModule.getSpectators(event.getPlayer())) {
                p.getInventory().setHeldItemSlot(event.getNewSlot());
            }
        }
    }

    @EventHandler
    private void onInventoryChange(final InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            // Check if this player is spectated by someone.
            if (spectatingModule.isSpectated((Player) event.getWhoClicked())) {

                ItemStack[] contents = event.getWhoClicked().getInventory().getContents();
                // Copy inventory to all spectators.
                for (Player p : spectatingModule.getSpectators((Player) event.getWhoClicked())) {
                    // Copy inventory.
                    for (int i = 0; i < contents.length; i++) {
                        p.getInventory().setItem(i, contents[i]);
                    }
                }
            }
        }
    }

}
