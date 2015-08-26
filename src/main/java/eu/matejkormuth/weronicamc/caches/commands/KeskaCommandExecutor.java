package eu.matejkormuth.weronicamc.caches.commands;

import eu.matejkormuth.weronicamc.caches.Cache;
import eu.matejkormuth.weronicamc.caches.CacheFoundData;
import eu.matejkormuth.weronicamc.caches.CachePlayerStorage;
import eu.matejkormuth.weronicamc.caches.CacheStorage;
import eu.matejkormuth.weronicamc.caches.listeners.CreateCacheInteractListener;
import eu.matejkormuth.weronicamc.translations.TranslationPack;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
                case "edit":
                    if (sender instanceof Player) {
                        if (args.length != 2) {
                            throw new IllegalArgumentException("Must specify on or off.");
                        }

                        commandEdit((Player) sender, args[1].equalsIgnoreCase("on"));
                    } else {
                        sender.sendMessage("Sorry this command is only for Players.");
                    }
                    break;
                case "info":
                    if (sender instanceof Player) {
                        if (args.length != 2) {
                            throw new IllegalArgumentException("Must specify on or off.");
                        }

                        commandInfo((Player) sender);
                    } else {
                        sender.sendMessage("Sorry this command is only for Players.");
                    }
                    break;
                case "previousID":
                    if (args.length != 3) {
                        throw new IllegalArgumentException("Not enough arguments!");
                    }
                    int cacheId = Integer.valueOf(args[1]);
                    int previous = Integer.valueOf(args[2]);
                    commandPreviousId(sender, cacheId, previous);
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
                    int cacheId2 = Integer.valueOf(args[1]);
                    commandStats(sender, cacheId2);
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
                        int cacheId3 = Integer.valueOf(args[2]);
                        commandRemove(sender, cacheId3);
                        break;
                    }
                case "reload":
                    if (sender instanceof Player) {
                        commandReload(sender);
                    } else {
                        sender.sendMessage("Sorry this command is only for Players.");
                    }
                    break;
                // Player commands
                case "countme":
                    if (sender instanceof Player) {
                        commandCountMe((Player) sender);
                    } else {
                        sender.sendMessage("Sorry this command is only for Players.");
                    }
                    break;
                case "toplist":
                    if (sender instanceof Player) {
                        commandToplist((Player) sender);
                    } else {
                        sender.sendMessage("Sorry this command is only for Players.");
                    }
                    break;
                case "help":
                    if (sender instanceof Player) {
                        commandHelp((Player) sender);
                    } else {
                        sender.sendMessage("Sorry this command is only for Players.");
                    }
                    break;
                case "?":
                    if (sender instanceof Player) {
                        commandHelp((Player) sender);
                    } else {
                        sender.sendMessage("Sorry this command is only for Players.");
                    }
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

    private void commandPreviousId(CommandSender sender, int cacheId, int previous) {
        if (permissions.has(sender, "keska.previousid")) {
            Optional<Cache> c = cacheStorage.get(cacheId);
            if (c.isPresent()) {
                Cache cache = c.get();
                if (cacheStorage.get(previous).isPresent()) {
                    cache.setPreviousCacheId(previous);
                    sender.sendMessage("Set previous of " + cacheId + " to " + previous);
                } else {
                    sender.sendMessage("Specified previous cache by id " + previous + " not found!");
                }
            } else {
                sender.sendMessage("Cache by id " + cacheId + " not found!");
            }
        } else {
            sender.sendMessage(translations.format("not_enough_permissions"));
        }
    }

    private void commandInfo(Player player) {
        if (permissions.has(player, "keska.info")) {
            if (player.getTargetBlock(null, 16) != null &&
                    player.getTargetBlock(null, 16).getType() == Material.CHEST) {
                Block targetBlock = player.getTargetBlock(null, 16);
                Optional<Cache> c = cacheStorage.get(targetBlock.getLocation());
                if (c.isPresent()) {
                    player.sendMessage("cache id: " + c.get().getId());
                } else {
                    player.sendMessage("not a cache");
                }
            } else {
                player.sendMessage("not looking at chest");
            }
        } else {
            player.sendMessage(translations.format("not_enough_permissions"));
        }
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

            cacheStorage.getAll()
                    .stream()
                    .skip(page * itemsPerPage)
                    .limit(itemsPerPage)
                    .collect(Collectors.toList())
                    .forEach(cache -> sender.sendMessage(cache.getId() +
                            " - " + cache.getPos().toString() + ", " + cache.getWorldName()));

        } else {
            sender.sendMessage(translations.format("not_enough_permissions"));
        }
    }

    private void commandStats(CommandSender sender, int cacheId) {
        if (permissions.has(sender, "keska.stats")) {
            Optional<Map<UUID, CacheFoundData>> founds = this.cachePlayerStorage.getFounds(cacheId);
            SimpleDateFormat sdf = new SimpleDateFormat();
            if (founds.isPresent()) {
                for (Map.Entry<UUID, CacheFoundData> entry : founds.get().entrySet()) {
                    // Request Bukkit for player name.
                    OfflinePlayer p = Bukkit.getOfflinePlayer(entry.getKey());

                    if (p.getName() == null) {
                        sender.sendMessage(ChatColor.YELLOW + p.getUniqueId().toString() + " - "
                                + ChatColor.WHITE + sdf.format(new Date(entry.getValue().foundAt)));
                    } else {
                        sender.sendMessage(ChatColor.YELLOW + p.getName() + " - "
                                + ChatColor.WHITE + sdf.format(new Date(entry.getValue().foundAt)));
                    }
                }
            } else {
                sender.sendMessage("Nothing found!");
            }
        } else {
            sender.sendMessage(translations.format("not_enough_permissions"));
        }
    }

    private void commandCount(CommandSender sender, UUID uniqueId) {
        if (permissions.has(sender, "keska.count")) {
            int found = this.cachePlayerStorage.getFoundCount(uniqueId);
            int total = this.cacheStorage.size();
            sender.sendMessage(found + "/" + total);
        } else {
            sender.sendMessage(translations.format("not_enough_permissions"));
        }
    }

    private void commandRemove(CommandSender sender, int cacheId) {
        if (permissions.has(sender, "keska.remove")) {
            this.cacheStorage.remove(cacheId);
            sender.sendMessage("Cache " + cacheId + " was removed!");
            this.cachePlayerStorage.removeCacheRecords(cacheId);
            sender.sendMessage("All player records for cache " + cacheId + " were removed!");
        } else {
            sender.sendMessage(translations.format("not_enough_permissions"));
        }
    }

    private void commandRemoveAll(CommandSender sender) {
        if (permissions.has(sender, "keska.removeall")) {
            this.cacheStorage.removeAll();
            sender.sendMessage("All cached were removed!");
            this.cachePlayerStorage.removeAllCacheRecords();
            sender.sendMessage("All player records were removed!");
        } else {
            sender.sendMessage(translations.format("not_enough_permissions"));
        }
    }

    private void commandRemoveCount(CommandSender sender, UUID targetPlayer) {
        if (permissions.has(sender, "keska.remove.count")) {
            this.cachePlayerStorage.removePlayerRecords(targetPlayer);
            sender.sendMessage("Player records for player " + targetPlayer + " were removed!");
        } else {
            sender.sendMessage(translations.format("not_enough_permissions"));
        }
    }

    private void commandRemoveStats(CommandSender sender) {
        if (permissions.has(sender, "keska.remove.stats")) {
            this.cachePlayerStorage.removeAllCacheRecords();
            sender.sendMessage("All player records were removed!");
        } else {
            sender.sendMessage(translations.format("not_enough_permissions"));
        }
    }

    private void commandReload(CommandSender sender) {
        if (permissions.has(sender, "keska.reload")) {
            sender.sendMessage("There is nothing to reload.");
        } else {
            sender.sendMessage(translations.format("not_enough_permissions"));
        }
    }

    // Player commands.

    private void commandCountMe(Player sender) {
        if (permissions.has(sender, "keska.countme")) {
            int found = this.cachePlayerStorage.getFoundCount(sender.getUniqueId());
            int total = this.cacheStorage.size();
            sender.sendMessage(found + "/" + total);
        } else {
            sender.sendMessage(translations.format("not_enough_permissions"));
        }
    }

    private void commandToplist(Player sender) {
        if (permissions.has(sender, "keska.toplist")) {

        } else {
            sender.sendMessage(translations.format("not_enough_permissions"));
        }
    }

    private void commandHelp(Player sender) {
        if (permissions.has(sender, "keska.help")) {
            sender.sendMessage(translations.format("help_message"));
        } else {
            sender.sendMessage(translations.format("not_enough_permissions"));
        }
    }
}
