package eu.matejkormuth.weronicamc.translations;

import eu.matejkormuth.weronicamc.Dependency;
import eu.matejkormuth.weronicamc.Module;
import eu.matejkormuth.weronicamc.configuration.ConfigurationsModule;
import org.bukkit.configuration.file.YamlConfiguration;

public class TranslationsModule extends Module {

    @Dependency
    private ConfigurationsModule configurationsModule;

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    public TranslationPack load(String name) {
        YamlConfiguration conf = configurationsModule.loadOrCreate("translations_" + name, new YamlConfiguration());
        return new TranslationPack(conf);
    }
}
