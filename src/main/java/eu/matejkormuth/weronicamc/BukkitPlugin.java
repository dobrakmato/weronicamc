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

import com.google.common.base.Joiner;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.jar.Manifest;

/**
 * This class provides entry point for Starving as Bukkit plugin.
 */
public class BukkitPlugin extends JavaPlugin {

    public static final String BOOTSTRAP_NAME = "Bukkit_MBoot";
    public static final String BOOTSTRAP_VERSION = "1.0";

    // Logger.
    private final static Logger log = LoggerFactory.getLogger(BukkitPlugin.class);

    // Here is whole Starving stored.
    private ModuleProvider moduleProvider;

    @Override
    public void onEnable() {
        log.info("By using this plugin you agree to its license: ");

        // Load license and display it.
        this.displayLicense();

        // Initialize commands.
        getCommand("bmboot").setExecutor(this::cmdStarving);

        log.info("Initializing...");

        // Initialize module provider.
        moduleProvider = new ModuleProvider();
        // Start initialization script.
        new Initializer().initialize(moduleProvider);
        // Enable all modules.
        this.enableAllModules();

        log.info("Enabled all modules!");
    }

    private void displayLicense() {
        log.info("==========================================================");
        InputStream in = getClass().getClassLoader().getResourceAsStream("LICENSE.txt");
        BufferedReader input = new BufferedReader(new InputStreamReader(in));
        input.lines().forEach(log::info);
        log.info("==========================================================");
    }

    private void enableAllModules() {
        List<Module> disabledModules = new ArrayList<>(moduleProvider.getModules());
        List<Module> erroredModules = new ArrayList<>();

        log.info("Resolving dependencies between modules...");
        disabledModules.forEach(eu.matejkormuth.weronicamc.Module::findInjectedDependencies);

        log.info("Enabling modules...");

        // Represents number of enabling iteration.
        int iterationCount = 1;
        int maxIterationCount = 50;

        // Loop while there are disabled modules.
        while (disabledModules.size() > 0 && iterationCount < maxIterationCount) {
            for (Iterator<Module> itr = disabledModules.iterator(); itr.hasNext(); ) {
                // Get next disabled module.
                Module m = itr.next();

                // Check if all dependencies of M are enabled.
                boolean dependenciesSatisfied = true;
                for (Class<? extends Module> dependencyModuleClass : m.getDependencyList()) {
                    Module dependencyModule = moduleProvider.getModule(dependencyModuleClass);

                    if (dependencyModule == null) {
                        log.error("Module {} declares reference to non-existing (not registered) module {}!",
                                m, dependencyModuleClass);
                        itr.remove();

                        // Add to errored modules.
                        erroredModules.add(m);

                        // Skip check of other dependencies, as this module will never load.
                        break;
                    }

                    dependenciesSatisfied &= dependencyModule.enabled;
                }

                // If the dependencies for M were satisfied, enable M.
                if (dependenciesSatisfied) {
                    try {
                        // Inject all dependencies.
                        m.injectDependencies(moduleProvider);
                        // Enable module.
                        m.onEnable();
                        m.enabled = true;
                        log.info("Enabled module {}!", m);
                        // Remove M from disabledModules list.
                        itr.remove();
                    } catch (Exception e) {
                        log.error("Can't enable module " + m.toString() + "! Exception: ", e);
                        erroredModules.add(m);
                        // Remove M from disabled as this module will never load.
                        itr.remove();
                    }
                }
            }
            iterationCount++;
        }

        // Display error message.
        if (disabledModules.size() > 0) {
            log.error("Not all modules could be enabled!");
            log.error("There are still {} disabled modules after {} enable iterations.",
                    disabledModules.size(), iterationCount);

            log.error("These modules are still disabled: {}", Joiner.on(", ").join(disabledModules));
        }

        if (erroredModules.size() > 0) {
            log.error("There are also modules that produced errors while they were enabled!");
            log.error("Errored modules (check log for concrete errors): {}", Joiner.on(", ").join(erroredModules));
        }
    }

    @Override
    public void onDisable() {
        log.info("Disabling modules.");
        // Disable all modules.
        for (Module m : moduleProvider) {
            try {
                m.onDisable();
                log.info("Disabled module {}!", m);
            } catch (Exception e) {
                log.error("Can't disable module {}! Exception: {}", m, e);
            }
        }
        log.info("Disabled all modules.");

        // Clean up the things that mayn't be cleaned up.
        Bukkit.getScheduler().cancelTasks(this);
    }


    private boolean cmdStarving(CommandSender commandSender, Command command, String label, String[] strings) {
        if (!commandSender.isOp()) {
            commandSender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }

        // Find version information.
        URLClassLoader cl = ((URLClassLoader) this.getClass().getClassLoader());
        String title = "WeronicaMC";
        String version = "unknown";
        String buildNumber = "unknown";
        String scmRevision = "unknown";
        try {
            URL url = cl.findResource("META-INF/MANIFEST.MF");
            Manifest manifest = new Manifest(url.openStream());
            // do stuff with it
            title = manifest.getMainAttributes().getValue("Implementation-Title");
            version = manifest.getMainAttributes().getValue("Implementation-Version");
            buildNumber = manifest.getMainAttributes().getValue("Implementation-Build-Number");
            scmRevision = manifest.getMainAttributes().getValue("Implementation-SCM-Revision");
        } catch (IOException e) {
            e.printStackTrace();
        }

        commandSender.sendMessage(ChatColor.YELLOW + BOOTSTRAP_NAME + " " + BOOTSTRAP_VERSION);
        commandSender.sendMessage(ChatColor.BLUE + "Mod: " + title + " " + version);
        commandSender.sendMessage(ChatColor.GRAY + "Rev: " + scmRevision);
        commandSender.sendMessage(ChatColor.GRAY + "Build number: " + buildNumber);

        StringBuilder modules = new StringBuilder();
        int disabledModules = 0;
        for (Module m : moduleProvider) {
            if (m.isEnabled()) {
                modules.append(ChatColor.GREEN + m.toString().replace("Module", ""));
            } else {
                disabledModules++;
                modules.append(ChatColor.RED + m.toString().replace("Module", ""));
            }
            modules.append(ChatColor.WHITE + ", ");
        }

        String modulesStr = modules.toString();

        commandSender.sendMessage(ChatColor.YELLOW + "Modules (" + moduleProvider.size() + "): "
                + modulesStr.substring(0, modulesStr.length() - 2));

        if(disabledModules > 0) {
            commandSender.sendMessage(ChatColor.LIGHT_PURPLE + "Please check logs to see more information about why there " +
                    "are " + disabledModules + " disabled modules!");
        }

        return true;
    }
}
