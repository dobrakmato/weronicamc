package eu.matejkormuth.weronicamc.caches.scoreboard;

import eu.matejkormuth.weronicamc.caches.CachePlayerStorage;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sorting {

    private final CachePlayerStorage cachePlayerStorage;

    public Sorting(CachePlayerStorage cachePlayerStorage) {
        this.cachePlayerStorage = cachePlayerStorage;
    }

    public List<Object> sort(List<OfflinePlayer> players) {
        int targetAmount = 15;
        Map<Integer, List<OfflinePlayer>> firstOrder = new HashMap<>();

        // Add them to groups.
        for (OfflinePlayer offlinePlayer : players) {
            int cachesFound = cachePlayerStorage.getFoundCount(offlinePlayer);
            if (!firstOrder.containsKey(cachesFound)) {
                firstOrder.put(cachesFound, new ArrayList<>());
            }
            firstOrder.get(cachesFound).add(offlinePlayer);
        }

        // Compute amount of groups needed.
        int currentAmount = 0;
        List<List<OfflinePlayer>> groupsNeeded = new ArrayList<>();
        for (List<OfflinePlayer> list : firstOrder.values()) {
            groupsNeeded.add(list);
            currentAmount += groupsNeeded.size();
            if (currentAmount > targetAmount) {
                break;
            }
        }

        // Order players in groups.

        return null;
    }
}
