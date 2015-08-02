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
package eu.matejkormuth.weronicamc;

import eu.matejkormuth.weronicamc.caches.CachesModule;
import eu.matejkormuth.weronicamc.configuration.ConfigurationsModule;
import eu.matejkormuth.weronicamc.filestorage.FileStorageModule;
import eu.matejkormuth.weronicamc.resourcepacks.ResourcePacksModule;
import eu.matejkormuth.weronicamc.spectating.SpectatingModule;
import eu.matejkormuth.weronicamc.translations.TranslationsModule;
import eu.matejkormuth.weronicamc.vault.VaultModule;

/**
 * This class initializes Starving server mod.
 */
public class Initializer {
    /**
     * Calling this initializes all modules required for Starving to work in specified ModuleProvider.
     *
     * @param provider provider to register modules to
     */
    public void initialize(ModuleProvider provider) {
        // Here goes initialization logic.
        provider.register(new CachesModule());
        provider.register(new ConfigurationsModule());
        provider.register(new FileStorageModule());
        provider.register(new ResourcePacksModule());
        provider.register(new SpectatingModule());
        provider.register(new TranslationsModule());
        provider.register(new VaultModule());
    }
}
