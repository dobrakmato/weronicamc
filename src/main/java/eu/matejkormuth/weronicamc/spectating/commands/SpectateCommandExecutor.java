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
package eu.matejkormuth.weronicamc.spectating.commands;

import eu.matejkormuth.weronicamc.spectating.SpectatingModule;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpectateCommandExecutor implements CommandExecutor {

    private static final Logger log = LoggerFactory.getLogger(SpectateCommandExecutor.class);

    private final Permission permissions;
    private final SpectatingModule specatatingModule;

    public SpectateCommandExecutor(SpectatingModule spectatingModule, Permission permissions) {
        this.specatatingModule = spectatingModule;
        this.permissions = permissions;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if(commandSender instanceof Player) {
            if(permissions.has(commandSender, "spectate")) {
                // Check for argument count.
                if(args.length == 1) {
                    String playerName = args[0];
                    Player spectated = Bukkit.matchPlayer(playerName).get(0);

                    // Try to spectate.
                    try {
                        specatatingModule.addSpectating((Player) commandSender, spectated);
                        commandSender.sendMessage(ChatColor.GREEN + "You are now spectating player " + spectated.getName() + "!");
                    } catch (IllegalArgumentException e) {
                        commandSender.sendMessage(ChatColor.RED + "Can't spectate: " + e.getMessage());
                        log.error("Can't spectate: ", e);
                    }

                } else {
                    return false;
                }
            } else {
                commandSender.sendMessage(ChatColor.RED + "You don't have enough permissions!");
            }
        } else {
            commandSender.sendMessage(ChatColor.RED + "This command is only for players!");
        }
        return true;
    }
}
