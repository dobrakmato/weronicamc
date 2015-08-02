package eu.matejkormuth.weronicamc.caches;

import eu.matejkormuth.weronicamc.Dependency;
import eu.matejkormuth.weronicamc.Module;
import eu.matejkormuth.weronicamc.PluginAccessor;
import eu.matejkormuth.weronicamc.caches.commands.KeskaCommandExecutor;
import eu.matejkormuth.weronicamc.caches.listeners.ChestInteractListener;
import eu.matejkormuth.weronicamc.caches.listeners.CreateCacheInteractListener;
import eu.matejkormuth.weronicamc.configuration.ConfigurationsModule;
import eu.matejkormuth.weronicamc.translations.TranslationPack;
import eu.matejkormuth.weronicamc.translations.TranslationsModule;
import eu.matejkormuth.weronicamc.vault.VaultModule;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

public class CachesModule extends Module {

    @Dependency
    private ConfigurationsModule configurationsModule;

    @Dependency
    private VaultModule vaultModule;

    @Dependency
    private TranslationsModule translationsModule;
    private TranslationPack translations;

    private CacheStorage cacheStorage;
    private CachePlayerStorage cachePlayerStorage;

    @Override
    public void onEnable() {
        // Register new configuration types.
        ConfigurationSerialization.registerClass(Cache.class, "Cache");
        ConfigurationSerialization.registerClass(CacheFoundData.class, "CacheFoundData");

        // Load translations.
        translations = translationsModule.load("cache");

        // Initialize objects.
        cachePlayerStorage = new CachePlayerStorage(configurationsModule);
        cacheStorage = new CacheStorage(configurationsModule);

        // Register listeners.
        CreateCacheInteractListener createCacheInteractListener;
        listener(new ChestInteractListener(cacheStorage, cachePlayerStorage, vaultModule.getEconomy(), translations, permission));
        listener(createCacheInteractListener = new CreateCacheInteractListener(cacheStorage));

        // Register commands.
        JavaPlugin plugin = new PluginAccessor(this).getPlugin();
        // Register commands.
        plugin.getCommand("keska").setExecutor(new KeskaCommandExecutor(vaultModule.getPermissions(),
                cacheStorage, cachePlayerStorage, createCacheInteractListener, translations));
    }

    @Override
    public void onDisable() {

    }
}
