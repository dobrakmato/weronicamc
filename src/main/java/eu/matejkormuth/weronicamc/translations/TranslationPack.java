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
