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
import org.bukkit.configuration.file.YamlConfiguration;
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
    private ScoreboardManager scoreboardManager;

    @Override
    public void onEnable() {
        // Register new configuration types.
        ConfigurationSerialization.registerClass(Cache.class, "Cache");
        ConfigurationSerialization.registerClass(CacheFoundData.class, "CacheFoundData");

        // Load translations.
        translations = translationsModule.load("cache");

        // Load scoreboard config.
        YamlConfiguration scoreboardConfigDefault = new YamlConfiguration();
        scoreboardConfigDefault.set("visibleTime", 10);
        YamlConfiguration scoreboardConfig = configurationsModule.loadOrCreate("scoreboard", scoreboardConfigDefault);

        // Initialize objects.
        cachePlayerStorage = new CachePlayerStorage(configurationsModule);
        cacheStorage = new CacheStorage(configurationsModule);
        scoreboardManager = new ScoreboardManager(cachePlayerStorage, cacheStorage, this, scoreboardConfig,
                translations);

        // Register listeners.
        CreateCacheInteractListener createCacheInteractListener;
        listener(createCacheInteractListener = new CreateCacheInteractListener(cacheStorage));
        listener(new ChestInteractListener(cacheStorage, cachePlayerStorage, vaultModule.getEconomy(),
                translations, vaultModule.getPermissions(), createCacheInteractListener));

        // Register commands.
        JavaPlugin plugin = new PluginAccessor(this).getPlugin();
        // Register commands.
        plugin.getCommand("keska").setExecutor(new KeskaCommandExecutor(vaultModule.getPermissions(),
                cacheStorage, cachePlayerStorage, createCacheInteractListener, translations, scoreboardManager));
    }

    @Override
    public void onDisable() {

    }
}
