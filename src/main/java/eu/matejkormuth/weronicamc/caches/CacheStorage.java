package eu.matejkormuth.weronicamc.caches;

import eu.matejkormuth.weronicamc.configuration.ConfigurationsModule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CacheStorage {

    private static final String CONFIGURATION_NAME = "caches";

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
}
