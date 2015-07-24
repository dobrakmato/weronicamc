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
package eu.matejkormuth.weronicamc.resourcepacks.listeners;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorldChangeListener implements Listener {

    private static final Logger log = LoggerFactory.getLogger(WorldChangeListener.class);

    private final YamlConfiguration config;

    public WorldChangeListener(YamlConfiguration config) {
        this.config = config;
    }

    @EventHandler
    private void onPlayerChangeEvent(final PlayerChangedWorldEvent event) {
        // If the resource pack URL is specified.
        if(config.contains(event.getPlayer().getWorld().getName())) {
            String url = config.getString(event.getPlayer().getWorld().getName());
            // Send resource pack to player.
            log.info("Sending {} as resource pack to {}!", url, event.getPlayer().getName());
            event.getPlayer().setResourcePack(url);
        } else {
            // Use "empty" resource pack to reset back to default.
            if(!config.contains("_empty")) {
                log.error("Resource packs config does not contains empty resource pack URL!");
                log.error("Server now can't restore default resource pack to user {}!", event.getPlayer().getName());
                return;
            }
            String url = config.getString("_empty");
            // Send resource pack to player.
            log.info("Sending {} as resource pack to {}!", url, event.getPlayer().getName());
            event.getPlayer().setResourcePack(url);
        }
    }

    @EventHandler
    private void onPlayerJoinWorld(final PlayerJoinEvent event) {
        // If the resource pack URL is specified.
        if(config.contains(event.getPlayer().getWorld().getName())) {
            String url = config.getString(event.getPlayer().getWorld().getName());
            // Send resource pack to player.
            log.info("Sending {} as resource pack to {}!", url, event.getPlayer().getName());
            event.getPlayer().setResourcePack(url);
        } else {
            // Use "empty" resource pack to reset back to default.
            if(!config.contains("_empty")) {
                log.error("Resource packs config does not contains empty resource pack URL!");
                log.error("Server now can't restore default resource pack to user {}!", event.getPlayer().getName());
                return;
            }
            String url = config.getString("_empty");
            // Send resource pack to player.
            log.info("Sending {} as resource pack to {}!", url, event.getPlayer().getName());
            event.getPlayer().setResourcePack(url);
        }
    }
}
