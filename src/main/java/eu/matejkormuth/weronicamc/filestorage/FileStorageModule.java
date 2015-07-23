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
package eu.matejkormuth.weronicamc.filestorage;

import eu.matejkormuth.weronicamc.Module;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Module that provides access to filesystem storage.
 */
public class FileStorageModule extends Module {

    private static final Logger log = LoggerFactory.getLogger(FileStorageModule.class);

    private Plugin plugin = Bukkit.getPluginManager().getPlugin("WeronicaMC");
    private Path dataFolder;

    @Override
    public void onEnable() {
        // Set data folder provided by Bukkit.
        dataFolder = plugin.getDataFolder().toPath();
        // Create all needed directories.
        safeMkdirs(dataFolder);
    }

    private void safeMkdirs(Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            log.warn("Can't create directories for path {}!", path);
        }
    }

    /**
     * Returns Path object of specified sub-path using first and more arguments. Also ensures that all directories
     * in the path exist. If any directory doesn't exists, it will be automatically created.
     *
     * @param first first part of path
     * @param more  more parts of path
     * @return requested path
     */
    public Path getPath(String first, String... more) {
        // Create requested path.
        Path path = dataFolder.resolve(Paths.get(first, more));
        // Ensure that all directories exists.
        safeMkdirs(path);
        // Return created path.
        return path;
    }

    @Override
    public void onDisable() {

    }
}
