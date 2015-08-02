package eu.matejkormuth.weronicamc.caches;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("CacheFoundData")
public class CacheFoundData implements ConfigurationSerializable {
    public int cacheId;
    public long foundAt;

    public CacheFoundData(int cacheId, long foundAt) {
        this.cacheId = cacheId;
        this.foundAt = foundAt;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(this.cacheId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CacheFoundData that = (CacheFoundData) o;

        return cacheId == that.cacheId;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", cacheId);
        map.put("foundAt", foundAt);
        return map;
    }


    public static CacheFoundData deserialize(Map<String, Object> map) {
        return new CacheFoundData((Integer) map.get("id"), (Long) map.get("foundAt"));
    }
}
