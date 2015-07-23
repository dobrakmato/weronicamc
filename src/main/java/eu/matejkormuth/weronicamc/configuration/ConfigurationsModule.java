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
package eu.matejkormuth.weronicamc.configuration;


import eu.matejkormuth.weronicamc.Dependency;
import eu.matejkormuth.weronicamc.Module;
import eu.matejkormuth.weronicamc.filestorage.FileStorageModule;
import org.bukkit.configuration.file.YamlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigurationsModule extends Module {

    private static final Logger log = LoggerFactory.getLogger(ConfigurationsModule.class);

    @Dependency
    private FileStorageModule module;

    // Path to configs.
    private Path basePath;

    @Override
    public void onEnable() {
        basePath = module.getPath("");
    }

    @Override
    public void onDisable() {

    }

    /**
     * Loads configuration from disk by specified name or creates empty one if specified configuration does not exists
     * on disk.
     *
     * @param name name of configuration
     * @return loaded or newly created configuration
     */
    public YamlConfiguration loadOrCreate(String name) {
        // Fix name if needed.
        if(!name.endsWith(".yml")) {
            name += ".yml";
        }

        if (Files.exists(basePath.resolve(name))) {
            return YamlConfiguration.loadConfiguration(basePath.resolve(name).toFile());
        } else {
            return new YamlConfiguration();
        }
    }

    /**
     * Saves specified configuration to file with specified name.
     *
     * @param name          name of configuration
     * @param configuration configuration object
     */
    public void save(String name, YamlConfiguration configuration) {
        // Fix name if needed.
        if(!name.endsWith(".yml")) {
            name += ".yml";
        }

        try {
            configuration.save(basePath.resolve(name).toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
