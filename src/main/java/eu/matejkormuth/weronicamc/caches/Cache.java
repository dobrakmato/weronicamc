package eu.matejkormuth.weronicamc.caches;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("Cache")
public class Cache implements ConfigurationSerializable {

    private int id;
    private double reward;
    private int previousCacheId;
    private Vector pos;
    private String worldName;

    public Vector getPos() {
        return pos;
    }

    public void setPos(Vector pos) {
        this.pos = pos;
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getReward() {
        return reward;
    }

    public void setReward(double reward) {
        this.reward = reward;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put("id", id);
        map.put("previousId", previousCacheId);
        map.put("reward", reward);
        map.put("position", pos);
        map.put("worldName", worldName);

        return map;
    }

    public static Cache deserialize(Map<String, Object> map) {
        Cache c = new Cache();

        c.setId((Integer) map.get("id"));
        c.setPreviousCacheId((Integer) map.get("previousId"));
        c.setReward((Double) map.get("reward"));
        c.setPos((Vector) map.get("position"));
        c.setWorldName((String) map.get("worldName"));

        return c;
    }

    public int getPreviousCacheId() {
        return previousCacheId;
    }

    public void setPreviousCacheId(int previousCacheId) {
        this.previousCacheId = previousCacheId;
    }
}
