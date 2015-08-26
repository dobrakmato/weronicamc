package eu.matejkormuth.weronicamc.caches;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

public class ScoreboardManager {

    private final List<Player> players = new ArrayList<>();
    private final CachePlayerStorage cachePlayerStorage;
    private final CacheStorage cacheStorage;

    public ScoreboardManager(CachePlayerStorage cachePlayerStorage, CacheStorage cacheStorage) {
        this.cachePlayerStorage = cachePlayerStorage;
        this.cacheStorage = cacheStorage;
    }

    public Player remove(int index) {
        return players.remove(index);
    }

    public boolean add(Player player) {
        return players.add(player);
    }

    private void update() {
        List<Map.Entry<UUID, List<CacheFoundData>>> data = createToplist();

        // Build scoreboard.
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective toplist = scoreboard.getObjective("Toplist");
        toplist.setDisplayName("Toplist");
        toplist.setDisplaySlot(DisplaySlot.SIDEBAR);

        // Create scores.
        Map.Entry<UUID, List<CacheFoundData>> entry;
        for (int i = data.size(); i > 0; i--) {
            entry = data.get(i);
            OfflinePlayer player = Bukkit.getOfflinePlayer(entry.getKey());
            String name = "not resolvable";
            if (player != null) {
                name = player.getName();
            }

            toplist
                    .getScore(name + ": " + entry.getValue().size())
                    .setScore(data.size() - i);
        }

        // Remove offline players.
        for (Iterator<Player> itr = players.iterator(); itr.hasNext(); ) {
            Player p = itr.next();
            if (!p.isOnline()) {
                itr.remove();
            }
        }

        // Set scoreboard to all online players.
        for (Player p : players) {
            p.setScoreboard(scoreboard);
        }
    }

    private List<Map.Entry<UUID, List<CacheFoundData>>> createToplist() {
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

    Comparator<Map.Entry<UUID, List<CacheFoundData>>> sorter = Comparator
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
