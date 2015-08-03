package eu.matejkormuth.weronicamc.caches.commands;

import eu.matejkormuth.weronicamc.caches.Cache;
import eu.matejkormuth.weronicamc.caches.CachePlayerStorage;
import eu.matejkormuth.weronicamc.caches.CacheStorage;
import eu.matejkormuth.weronicamc.caches.listeners.CreateCacheInteractListener;
import eu.matejkormuth.weronicamc.translations.TranslationPack;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class KeskaCommandExecutor implements CommandExecutor {

    private static final Logger log = LoggerFactory.getLogger(KeskaCommandExecutor.class);

    private final Permission permissions;

    private final CacheStorage cacheStorage;
    private final CachePlayerStorage cachePlayerStorage;
    private final CreateCacheInteractListener createCacheInteractListener;
    private final TranslationPack translations;

    public KeskaCommandExecutor(Permission permissions, CacheStorage cacheStorage,
                                CachePlayerStorage cachePlayerStorage,
                                CreateCacheInteractListener createCacheInteractListener,
                                TranslationPack translations) {
        this.permissions = permissions;
        this.cacheStorage = cacheStorage;
        this.cachePlayerStorage = cachePlayerStorage;
        this.createCacheInteractListener = createCacheInteractListener;
        this.translations = translations;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Command parsing logic is here, actual command implementation is in custom
        // methods below this.

        if (args.length >= 1) {
            String subCommand = args[0];

            switch (subCommand) {
                // Admin commands.
                case "create":
                    if (sender instanceof Player) {
                        if (args.length != 2) {
                            throw new IllegalArgumentException("You must specify monetary amount as reward!");
                        }

                        String rewardStr = args[1];
                        double reward = Double.valueOf(rewardStr);

                        commandCreate((Player) sender, reward);
                    } else {
                        sender.sendMessage("Sorry this command is only for Players.");
                    }
                    break;
                case "list":
                    int page = 0;
                    if (args.length == 2) {
                        page = Integer.valueOf(args[1]);
                    }
                    commandList(sender, page);
                    break;
                case "stats":
                    if (args.length != 2) {
                        throw new IllegalArgumentException("Cache ID must be specified!");
                    }
                    int cacheId = Integer.valueOf(args[1]);
                    commandStats(sender, cacheId);
                    break;
                case "count":
                    if (args.length != 2) {
                        throw new IllegalArgumentException("Player name must be specified!");
                    }
                    String playerName = args[1];
                    OfflinePlayer candidate = Bukkit.getPlayer(playerName);
                    if (candidate == null) {
                        candidate = Bukkit.getOfflinePlayer(playerName);
                    }
                    if (candidate == null) {
                        throw new IllegalArgumentException("Can't find player " + playerName);
                    }
                    commandCount(sender, candidate.getUniqueId());
                    break;
                case "remove":
                    if (args.length < 2) {
                        throw new IllegalArgumentException("Not enough arguments!");
                    }
                    // Special IDs.
                    if (args[1].equalsIgnoreCase("all")) {
                        commandRemoveAll(sender);
                        break;
                    } else if (args[1].equalsIgnoreCase("count")) {
                        if (args.length != 3) {
                            throw new IllegalArgumentException("Player name must be specified!");
                        }
                        String playerName2 = args[2];
                        OfflinePlayer candidate2 = Bukkit.getPlayer(playerName2);
                        if (candidate2 == null) {
                            candidate2 = Bukkit.getOfflinePlayer(playerName2);
                        }
                        if (candidate2 == null) {
                            throw new IllegalArgumentException("Can't find player " + playerName2);
                        }
                        commandRemoveCount(sender, candidate2.getUniqueId());
                    } else if (args[1].equalsIgnoreCase("stats")) {
                        commandRemoveStats(sender);
                        break;
                    } else {
                        if (args.length != 3) {
                            throw new IllegalArgumentException("Cache ID must be specified!");
                        }
                        int cacheId2 = Integer.valueOf(args[2]);
                        commandRemove(sender, cacheId2);
                        break;
                    }
                case "reload":
                    commandReload(sender);
                    break;
                // Player commands
                case "countme":
                    commandCountMe(sender);
                    break;
                case "toplist":
                    commandToplist(sender);
                    break;
                case "help":
                    commandHelp(sender);
                    break;
                case "?":
                    commandHelp(sender);
                    break;
                default:
                    sender.sendMessage("Wrong usage! /keska help for help.");
                    break;
            }
        } else {
            sender.sendMessage("Wrong usage! /keska help for help.");
        }

        return true;
    }

    // Admin commands.

    private void commandCreate(Player player, double reward) {
        if (permissions.has(player, "keska.create")) {
            if (player.getTargetBlock(null, 16) != null &&
                    player.getTargetBlock(null, 16).getType() == Material.CHEST) {
                Block targetBlock = player.getTargetBlock(null, 16);
                Cache c = cacheStorage.createCache(targetBlock.getLocation());
                c.setReward(reward);
                player.sendMessage(translations.format("cache_created_successfully"));
            } else {
                // Register one time block interact listener.
                player.sendMessage(translations.format("click_the_chest_block"));
                createCacheInteractListener.addCreate(player, reward);
            }

        } else {
            player.sendMessage(translations.format("not_enough_permissions"));
        }
    }

    private void commandEdit(Player sender, boolean editMode) {
        if (permissions.has(sender, "keska.edit")) {
            createCacheInteractListener.setEditing(sender, editMode);
            if (editMode) {
                sender.sendMessage(translations.format("edit_mode_on"));
            } else {
                sender.sendMessage(translations.format("edit_mode_off"));
            }
        } else {
            sender.sendMessage(translations.format("not_enough_permissions"));
        }
    }

    private void commandList(CommandSender sender, int page) {
        if (permissions.has(sender, "keska.list")) {
            int itemsPerPage = 10;
            int totalPages = (int) Math.ceil(cacheStorage.size() / 10);
            if (page > totalPages) {
                throw new IllegalArgumentException("Page number is too high!");
            }


        } else {
            sender.sendMessage(translations.format("not_enough_permissions"));
        }
    }

    private void commandStats(CommandSender sender, int cacheId) {
        if (permissions.has(sender, "keska.stats")) {

        } else {
            sender.sendMessage(translations.format("not_enough_permissions"));
        }
    }

    private void commandCount(CommandSender sender, UUID uniqueId) {
        if (permissions.has(sender, "keska.count")) {

        } else {
            sender.sendMessage(translations.format("not_enough_permissions"));
        }
    }

    private void commandRemove(CommandSender sender, int cacheId) {
        if (permissions.has(sender, "keska.remove")) {

        } else {
            sender.sendMessage(translations.format("not_enough_permissions"));
        }
    }

    private void commandRemoveAll(CommandSender sender) {
        if (permissions.has(sender, "keska.removeall")) {

        } else {
            sender.sendMessage(translations.format("not_enough_permissions"));
        }
    }

    private void commandRemoveCount(CommandSender sender, UUID targetPlayer) {
        if (permissions.has(sender, "keska.remove.count")) {

        } else {
            sender.sendMessage(translations.format("not_enough_permissions"));
        }
    }

    private void commandRemoveStats(CommandSender sender) {
        if (permissions.has(sender, "keska.remove.stats")) {

        } else {
            sender.sendMessage(translations.format("not_enough_permissions"));
        }
    }

    private void commandReload(CommandSender sender) {
        if (permissions.has(sender, "keska.reload")) {

        } else {
            sender.sendMessage(translations.format("not_enough_permissions"));
        }
    }

    // Player commands.

    private void commandCountMe(CommandSender sender) {
        if (permissions.has(sender, "keska.countme")) {

        } else {
            sender.sendMessage(translations.format("not_enough_permissions"));
        }
    }

    private void commandToplist(CommandSender sender) {
        if (permissions.has(sender, "keska.toplist")) {

        } else {
            sender.sendMessage(translations.format("not_enough_permissions"));
        }
    }

    private void commandHelp(CommandSender sender) {
        if (permissions.has(sender, "keska.help")) {

        } else {
            sender.sendMessage(translations.format("not_enough_permissions"));
        }
    }
}
