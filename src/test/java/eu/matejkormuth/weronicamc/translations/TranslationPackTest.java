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