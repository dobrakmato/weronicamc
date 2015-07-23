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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Holds all registered modules and provides them.
 */
public class ModuleProvider implements Iterable<Module> {

    private final Map<Class<? extends Module>, Module> modules;

    public ModuleProvider() {
        modules = new HashMap<>();
    }

    /**
     * Returns instance of specified module or null if there is no registered instance for specified class.
     *
     * @param type class of type of module to return
     * @param <T>  type of module to return
     * @return instance of specified module or null if there is no registered instance for specified class
     */
    public
    @Nullable
    <T> T getModule(Class<T> type) {
        return cast(modules.get(type));
    }

    @SuppressWarnings("unchecked")
    private <T> T cast(Module module) {
        return (T) module;
    }

    /**
     * Registers specified module into this ModuleProvider instance.
     *
     * @param module module to register
     */
    public void register(@Nonnull Module module) {
        if (modules.containsKey(module.getClass())) {
            throw new IllegalStateException("Module of type " + module.getClass() + " is already registered!");
        }

        modules.put(module.getClass(), module);
    }

    public Collection<Module> getModules() {
        return modules.values();
    }

    @Override
    public Iterator<Module> iterator() {
        return modules.values().iterator();
    }

    /**
     * Returns count of registered modules.
     *
     * @return count of registered modules in this module provider
     */
    public int size() {
        return modules.size();
    }
}
