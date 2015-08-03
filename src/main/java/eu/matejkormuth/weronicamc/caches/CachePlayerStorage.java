package eu.matejkormuth.weronicamc.caches;

import eu.matejkormuth.weronicamc.configuration.ConfigurationsModule;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;

public class CachePlayerStorage {

    private static final String CONFIGURATION_NAME = "caches_players";

    private final Map<UUID, List<CacheFoundData>> foundChests;
    private ConfigurationsModule configurationsModule;

    public CachePlayerStorage(ConfigurationsModule configurationsModule) {
        this.foundChests = new HashMap<>();
        this.configurationsModule = configurationsModule;

        YamlConfiguration cachePlayerConfiguration = configurationsModule.loadOrCreate(CONFIGURATION_NAME,
                new YamlConfiguration());

        // Load from configuration.
        for (Map.Entry<String, Object> entry : cachePlayerConfiguration.getValues(true).entrySet()) {
            UUID uuid = UUID.fromString(entry.getKey());
            List<CacheFoundData> cacheFoundDataList = (List<CacheFoundData>) entry.getValue();
            foundChests.put(uuid, cacheFoundDataList);
        }
    }

    public void save() {
        YamlConfiguration yaml = new YamlConfiguration();
        for (Map.Entry<UUID, List<CacheFoundData>> entry : foundChests.entrySet()) {
            yaml.set(entry.getKey().toString(), entry.getValue());
        }
        this.configurationsModule.save(CONFIGURATION_NAME, yaml);
    }

    public boolean hasFound(OfflinePlayer player, Cache cache) {
        if (!foundChests.containsKey(player.getUniqueId())) {
            return false;
        } else {
            for (CacheFoundData cfd : foundChests.get(player.getUniqueId())) {
                if (cfd.cacheId == cache.getId()) {
                    return true;
                }
            }
            return false;
        }
    }

    public void setFound(OfflinePlayer player, Cache cache) {
        setFound(player, cache, true);
    }

    public void setFound(OfflinePlayer player, Cache cache, boolean found) {
        if (found) {
            if (foundChests.containsKey(player.getUniqueId())) {
                foundChests.get(player.getUniqueId()).add(new CacheFoundData(cache.getId(),
                        System.currentTimeMillis()));
            } else {
                ArrayList<CacheFoundData> foundChestIds = new ArrayList<>();
                foundChestIds.add(new CacheFoundData(cache.getId(), System.currentTimeMillis()));
                foundChests.put(player.getUniqueId(), foundChestIds);
            }
        } else {
            if (hasFound(player, cache)) {
                // We use this kind weird construction as selector.
                for (Iterator<CacheFoundData> itr = foundChests.get(player.getUniqueId()).iterator();
                     itr.hasNext(); ) {
                    CacheFoundData cfd = itr.next();
                    if (cfd.cacheId == cache.getId()) {
                        itr.remove();
                    }
                }
            }
        }

        // Save the configuration after each change.
        this.save();
    }

    public int getFoundCount(OfflinePlayer offlinePlayer) {
        if (!this.foundChests.containsKey(offlinePlayer.getUniqueId())) {
            return 0;
        }

        return this.foundChests.get(offlinePlayer.getUniqueId()).size();
    }
}
