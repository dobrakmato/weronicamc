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
package eu.matejkormuth.weronicamc.caches;

import eu.matejkormuth.weronicamc.Module;
import eu.matejkormuth.weronicamc.PluginAccessor;
import eu.matejkormuth.weronicamc.translations.TranslationPack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;


public class ScoreboardManager {

    private final CachePlayerStorage cachePlayerStorage;
    private final CacheStorage cacheStorage;
    private final YamlConfiguration scoreboardConfig;
    private final TranslationPack translationPack;
    private final Plugin plugin;

    public ScoreboardManager(CachePlayerStorage cachePlayerStorage, CacheStorage cacheStorage,
                             Module module, YamlConfiguration scoreboardConfig, TranslationPack translationPack) {
        this.cachePlayerStorage = cachePlayerStorage;
        this.cacheStorage = cacheStorage;
        this.scoreboardConfig = scoreboardConfig;
        this.translationPack = translationPack;

        this.plugin = new PluginAccessor(module).getPlugin();
    }

    public void displayScoreboard(Player player) {
        Scoreboard scoreboard = createScoreboard();

        player.setScoreboard(scoreboard);

        // Configuration node visibleTime in seconds.
        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin,
                () -> player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard()),
                20L * scoreboardConfig.getInt("visibleTime"));
    }

    private Scoreboard createScoreboard() {
        List<Map.Entry<UUID, List<CacheFoundData>>> data = createToplist();

        // Build scoreboard.
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective toplist = scoreboard.getObjective("Toplist");
        if (toplist == null) {
            toplist = scoreboard.registerNewObjective("Toplist", "dummy");
        }

        toplist.setDisplayName("Toplist");
        toplist.setDisplaySlot(DisplaySlot.SIDEBAR);

        // Create scores.
        Map.Entry<UUID, List<CacheFoundData>> entry;
        for (int i = data.size(); i > 0; i--) {
            entry = data.get(i - 1);
            OfflinePlayer player = Bukkit.getOfflinePlayer(entry.getKey());
            String name = "not resolvable";
            if (player != null) {
                name = player.getName();
            }

            int length = 4 + Integer.toString(entry.getValue().size()).length();
            int nameLength = name.length();
            int scoreboardLength = 16;
            ChatColor colorCode = ChatColor.DARK_PURPLE;

            if (length + nameLength > scoreboardLength) {
                int newNameLength = nameLength - length;

                toplist
                        .getScore(name.substring(0, newNameLength - 1) + ": " + colorCode + entry.getValue().size())
                        .setScore(data.size() - i);
            } else {
                if (length + nameLength == scoreboardLength) {
                    toplist
                            .getScore(name + ": " + colorCode + entry.getValue().size())
                            .setScore(data.size() - i);
                } else {
                    // TODO: Musime vyplnit medzerami.

                    toplist
                            .getScore(name + ": " + colorCode + entry.getValue().size())
                            .setScore(data.size() - i);
                }
            }

        }

        return scoreboard;
    }

    public List<Map.Entry<UUID, List<CacheFoundData>>> createToplist() {
        int itemsInScoreboard = 16;

        Map<UUID, List<CacheFoundData>> founds = this.cachePlayerStorage.getFoundChests();
        List<Map.Entry<UUID, List<CacheFoundData>>> sorted = new ArrayList<>(founds.entrySet());
        // Sort by found caches, then by time the last cache was found by specified player.
        sorted.sort(sorter);
        // Reverse, so we get highest on top.
        Collections.reverse(sorted);
        // Now reverse list of all items that should be in scoreboard in correct oreder.
        List<Map.Entry<UUID, List<CacheFoundData>>> result = sorted.stream()
                .limit(itemsInScoreboard)
                .collect(Collectors.toList());
        return result;
    }

    // Comparator.
    public static final Comparator<Map.Entry<UUID, List<CacheFoundData>>> sorter = Comparator
            // First compare by amount of found caches.
            // NOTE: It looks like we must use anonymous class first time.
            .comparingInt(new ToIntFunction<Map.Entry<UUID, List<CacheFoundData>>>() {
                @Override
                public int applyAsInt(Map.Entry<UUID, List<CacheFoundData>> value) {
                    return value.getValue().size();
                }
            })
                    // Then compare by the time of last cache found.
            .thenComparingLong(value -> {
                // Find the last cache found time by knowing it will be the biggest.
                long favorite = -1;

                for (CacheFoundData cfd : value.getValue()) {
                    if (cfd.foundAt > favorite) {
                        favorite = cfd.foundAt;
                    }
                }

                return System.currentTimeMillis() - favorite;
            });

}
