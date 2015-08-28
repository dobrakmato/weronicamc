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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CacheStorage {

    private static final String CONFIGURATION_NAME = "caches";
    private static final Logger log = LoggerFactory.getLogger(CacheStorage.class);

    private final List<Cache> caches;
    private final ConfigurationsModule configurationsModule;
    private final AtomicInteger lastId;

    public CacheStorage(ConfigurationsModule configurationsModule) {
        this.configurationsModule = configurationsModule;

        YamlConfiguration chestStorage = configurationsModule.loadOrCreate(CONFIGURATION_NAME, new YamlConfiguration());

        // Load all caches from config.
        caches = new ArrayList<>();
        List<?> cacheList = chestStorage.getList("caches", new ArrayList<>());
        caches.addAll(cacheList.stream().map(o -> (Cache) o).collect(Collectors.toList()));

        // Inject from config.
        lastId = new AtomicInteger(chestStorage.getInt("lastId", 0));
    }

    public Cache createCache(Location chestLoc) {
        if (chestLoc.getBlock().getType() != Material.CHEST) {
            throw new IllegalArgumentException("Block at specified location is not Material.CHEST!");
        }

        Cache c = new Cache();

        c.setId(lastId.incrementAndGet());
        c.setPos(chestLoc.toVector());
        c.setWorldName(chestLoc.getWorld().getName());

        // Add chest to registered chests list.
        this.caches.add(c);

        // Save configuration after each edit.
        this.save();

        return c;
    }

    public void save() {
        YamlConfiguration yaml = new YamlConfiguration();

        yaml.set("lastId", this.lastId.get());
        yaml.set("caches", this.caches);

        configurationsModule.save(CONFIGURATION_NAME, yaml);
        log.info("Cache storage saved.");
    }

    public Optional<Cache> get(Location location) {
        for (Cache c : caches) {
            // World matches.
            if (c.getWorldName().equals(location.getWorld().getName())) {
                // Position matches.
                if (c.getPos().equals(location.getBlock().getLocation().toVector())) {
                    return Optional.of(c);
                }
            }
        }
        return Optional.empty();
    }

    public int size() {
        return caches.size();
    }

    public Optional<Cache> get(int cacheId) {
        for (Cache c : caches) {
            if (c.getId() == cacheId) {
                return Optional.of(c);
            }
        }
        return Optional.empty();
    }

    public void remove(int cacheId) {
        for (Iterator<Cache> itr = this.caches.iterator(); itr.hasNext(); ) {
            Cache cache = itr.next();
            if (cache.getId() == cacheId) {
                itr.remove();
                break;
            }
        }
        this.save();
    }

    public void removeAll() {
        this.caches.clear();
        this.save();
    }

    public List<Cache> getAll() {
        List<Cache> caches = new ArrayList<>(this.caches);
        caches.sort(Comparator.comparingInt(Cache::getId));
        return caches;
    }
}
