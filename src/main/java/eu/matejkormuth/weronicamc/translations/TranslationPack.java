package eu.matejkormuth.weronicamc.translations;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Objects;
import java.util.regex.Pattern;

public class TranslationPack {

    private final YamlConfiguration translations;

    public TranslationPack(YamlConfiguration translations) {
        this.translations = translations;
    }

    public String format(String key, Object... args) {
        if (!translations.contains(key)) {
            throw new IllegalArgumentException("Specified key was not found in this translation pack!");
        }

        String clean = translations.getString(key);
        // Fix chat colors.
        clean = ChatColor.translateAlternateColorCodes('&', clean);
        // Now do the substitution.
        return substitute(clean, args);
    }

    public String substitute(String clean, Object... args) {
        if (args.length == 0) {
            return clean;
        }
        if (args.length == 1) {
            return clean.replaceFirst(Pattern.quote("{}"), Objects.toString(args[0]));
        }

        StringBuilder builder = new StringBuilder();
        boolean state = false;
        int subs = 0;
        for (char ch : clean.toCharArray()) {
            if (ch == '{') {
                state = true;
            } else if (ch == '}' && state) {
                // Substitute.
                if (args.length > subs) {
                    builder.append(Objects.toString(args[subs]));
                    state = false;
                } else {
                    builder.append('{');
                    builder.append('}');
                }
                subs++;
            } else {
                if (state) {
                    builder.append('{');
                    state = false;
                }
                builder.append(ch);
            }
        }
        return builder.toString();
    }
}
