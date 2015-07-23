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
package eu.matejkormuth.weronicamc.vault;

import eu.matejkormuth.weronicamc.Module;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VaultModule extends Module {

    private static final Logger log = LoggerFactory.getLogger(VaultModule.class);

    private Economy economy;
    private Permission permissions;
    private Chat chat;

    @Override
    public void onEnable() {

        // Check if plugin exists.
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            throw new VaultNotFoundException("Vault was not found!");
        }

        // Setup economy.
        RegisteredServiceProvider<Economy> economyRSP = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (economyRSP != null) {
            this.economy = economyRSP.getProvider();
        }
        // Setup permissions.
        RegisteredServiceProvider<Permission> permissionRSP = Bukkit.getServicesManager().getRegistration(Permission.class);
        if (permissionRSP != null) {
            this.permissions = permissionRSP.getProvider();
        }
        // Setup chat.
        RegisteredServiceProvider<Chat> chatRSP = Bukkit.getServicesManager().getRegistration(Chat.class);
        if (chatRSP != null) {
            this.chat = chatRSP.getProvider();
        }

        if (this.economy == null) {
            log.warn("Economy was not initialized! Some economy features may not work!");
        }

        if (this.permissions == null) {
            log.warn("Permissions was not initialized! Some permissions features may not work!");
        }

        if (this.chat == null) {
            log.warn("Chat was not initialized! Some chat features may not work!");
        }
    }

    @Override
    public void onDisable() {

    }

    public Economy getEconomy() {
        return economy;
    }

    public Permission getPermissions() {
        return permissions;
    }

    public Chat getChat() {
        return chat;
    }
}
