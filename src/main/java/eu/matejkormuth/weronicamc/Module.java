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

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents part of Starving project. Each module should declare dependencies to other modules in constructor method
 * by calling getDependencyList() and then adding all dependencies to that list.
 */
public abstract class Module {

    // Logger.
    private static final Logger log = LoggerFactory.getLogger(Module.class);

    // Reference to JavaPlugin.
    static final Plugin plugin = Bukkit.getPluginManager().getPlugin("WeronicaMC");

    // List of dependency modules.
    private final List<Class<? extends Module>> dependencies = new ArrayList<>();
    // Whether this module is enabled or not.
    boolean enabled = false;

    // DI - this method builds list of dependencies based on annotations.
    @SuppressWarnings("unchecked")
    void findInjectedDependencies() {
        for (Field f : this.getClass().getDeclaredFields()) {
            if (f.isAnnotationPresent(Dependency.class)) {
                if (Module.class.isAssignableFrom(f.getType())) {
                    dependencies.add((Class<? extends Module>) f.getType());
                }
            }
        }
    }

    // DI - this method inject all dependency modules into instance.
    @SuppressWarnings("unchecked")
    void injectDependencies(ModuleProvider provider) {
        for (Field f : this.getClass().getDeclaredFields()) {
            if (f.isAnnotationPresent(Dependency.class)) {
                if (Module.class.isAssignableFrom(f.getType())) {
                    if(!f.isAccessible()) {
                        f.setAccessible(true);
                    }

                    try {
                        f.set(this, provider.getModule(f.getType()));
                    } catch (IllegalAccessException e) {
                        log.error("Can't inject dependencies!", e);
                    }
                }
            }
        }
    }

    /**
     * Registers specified Bukkit Listener so it will receive events.
     *
     * @param listener listener to register
     */
    public void listener(@Nonnull Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, plugin);
        log.debug("Registering Listener {}...", listener.getClass());
    }

    /**
     * Called when specified module should execute it's initialization logic.
     * <p>
     * Dependencies SHOULD NOT be declared here, they should be declared in Module constructor.
     * <p>
     * However it is guaranteed that all dependencies declared in constructor are available at this moment.
     * <p>
     * Initialization logic consists of four steps:
     * <ul>
     * <li>registration of Listener objects</li>
     * <li>registration of CommandExecutor objects</li>
     * <li>scheduling of RepeatingTask objects</li>
     * <li>initialization of child components</li>
     * </ul>
     */
    public abstract void onEnable();

    /**
     * Called when specified module should execute it's finalization logic.
     */
    public abstract void onDisable();

    /**
     * Returns whether this modules is enabled or not.
     *
     * @return true if module is enabled, false otherwise
     */
    public boolean isEnabled() {
        return enabled;
    }

    public List<Class<? extends Module>> getDependencyList() {
        return Collections.unmodifiableList(dependencies);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
