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
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsistencyPlayerLeaveListener implements Listener {

    private static final Logger log = LoggerFactory.getLogger(ConsistencyPlayerLeaveListener.class);

    private final SpectatingModule spectatingModule;

    public ConsistencyPlayerLeaveListener(SpectatingModule spectatingModule) {
        this.spectatingModule = spectatingModule;
    }

    @EventHandler
    private void onPlayerLeave(final PlayerQuitEvent event) {
        // If player was spectator, remove him from collections.
        if (spectatingModule.isSpectating(event.getPlayer())) {
            spectatingModule.removeSpecatating(event.getPlayer());
        }

        // If player was spectated, remove his spectating session.
        if (spectatingModule.isSpectated(event.getPlayer())) {
            // Remove all spectators.
            for (Player p : spectatingModule.getSpectators(event.getPlayer())) {
                p.sendMessage(ChatColor.YELLOW + "Player " + event.getPlayer().getName() +
                        " left the game. Your spectating session was canceled.");
                // If this fails for some reason, catch exception and log it.
                try {
                    spectatingModule.removeSpecatating(p);
                } catch (IllegalArgumentException e) {
                    log.error("Can't remove spectating {} because: {}.", p.getName(), e);
                }
            }
        }
    }
}
