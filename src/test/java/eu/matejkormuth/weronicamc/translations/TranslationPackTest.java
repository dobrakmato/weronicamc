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

import org.junit.Assert;
import org.junit.Test;

public class TranslationPackTest {

    @Test
    public void testSubstitute0() throws Exception {
        TranslationPack tp = new TranslationPack(null);
        Assert.assertEquals("test without substitution", tp.substitute("test without substitution"));
        Assert.assertEquals("test without substitution", tp.substitute("test without substitution", "arg0"));
        Assert.assertEquals("test without substitution", tp.substitute("test without substitution", "arg0", 55));
    }

    @Test
    public void testSubstitute1() throws Exception {
        TranslationPack tp = new TranslationPack(null);
        Assert.assertEquals("test with one {}", tp.substitute("test with one {}"));
        Assert.assertEquals("test with one substitution", tp.substitute("test with one {}", "substitution"));
        Assert.assertEquals("test with one substitution", tp.substitute("test with one {}", "substitution", 55));
        Assert.assertEquals("test with 1 substitution", tp.substitute("test with {} substitution", 1));
    }

    @Test
    public void testSubstituteMany() throws Exception {
        TranslationPack tp = new TranslationPack(null);
        Assert.assertEquals("test with {} {}", tp.substitute("test with {} {}"));
        Assert.assertEquals("test with multiple {} {}", tp.substitute("test with {} {} {}", "multiple"));
        Assert.assertEquals("test with one substitution", tp.substitute("test with {} {}", "one", "substitution"));
        Assert.assertEquals("test with 1 substitution", tp.substitute("test with {} {}", 1, "substitution"));
    }
}