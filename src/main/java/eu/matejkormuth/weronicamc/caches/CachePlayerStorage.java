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
package eu.matejkormuth.weronicamc.caches;

import eu.matejkormuth.weronicamc.configuration.ConfigurationsModule;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CachePlayerStorage {

    private static final String CONFIGURATION_NAME = "caches_players";
    private static final Logger log = LoggerFactory.getLogger(CachePlayerStorage.class);

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
        log.info("Player records storage saved.");
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

    public Map<UUID, List<CacheFoundData>> getFoundChests() {
        return foundChests;
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
        return this.getFoundCount(offlinePlayer.getUniqueId());
    }

    public int getFoundCount(UUID uniqueId) {
        if (!this.foundChests.containsKey(uniqueId)) {
            return 0;
        }

        return this.foundChests.get(uniqueId).size();
    }

    public Optional<Map<UUID, CacheFoundData>> getFounds(int cacheId) {
        Map<UUID, CacheFoundData> map = new HashMap<>();
        for (Map.Entry<UUID, List<CacheFoundData>> entry : this.foundChests.entrySet()) {
            UUID uuid = entry.getKey();
            for (CacheFoundData cfd : entry.getValue()) {
                if (cfd.cacheId == cacheId) {
                    map.put(uuid, cfd);
                    break;
                }
            }
        }
        return Optional.of(map);
    }


    public void removeCacheRecords(int cacheId) {
        for (Map.Entry<UUID, List<CacheFoundData>> entry : this.foundChests.entrySet()) {
            for (Iterator<CacheFoundData> itr = entry.getValue().iterator(); itr.hasNext(); ) {
                CacheFoundData cfd = itr.next();
                if (cfd.cacheId == cacheId) {
                    itr.remove();
                }
            }
        }
        this.save();
    }

    public void removeAllCacheRecords() {
        this.foundChests.clear();
        this.save();
    }

    public void removePlayerRecords(UUID targetPlayer) {
        this.foundChests.remove(targetPlayer);
        this.save();
    }
}
