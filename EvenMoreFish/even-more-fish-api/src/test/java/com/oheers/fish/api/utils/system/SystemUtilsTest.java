package com.oheers.fish.api.utils.system;


import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class SystemUtilsTest {

    @Test
    void testParseJavaVersion_LegacyFormat() {
        assertEquals(1.5F, SystemUtils.parseJavaVersion("1.5"));
        assertEquals(1.8F, SystemUtils.parseJavaVersion("1.8"));
    }

    @Test
    void testParseJavaVersion_NewFormat() {
        assertEquals(9F, SystemUtils.parseJavaVersion("9"));
        assertEquals(21F, SystemUtils.parseJavaVersion("21"));
    }

    @Test
    void testParseJavaVersion_InvalidFormat() {
        assertEquals(-1F, SystemUtils.parseJavaVersion("not.a.version"));
        assertEquals(-1F, SystemUtils.parseJavaVersion("21a"));
        assertEquals(-1F, SystemUtils.parseJavaVersion("1."));
    }

    @Test
    void testIsJavaVersionAtLeast_LegacyToLegacy() {
        assertTrue(SystemUtils.isJavaVersionAtLeast("1.8", JavaSpecVersion.JAVA_1_5));
        assertTrue(SystemUtils.isJavaVersionAtLeast("1.6", JavaSpecVersion.JAVA_1_6));
        assertFalse(SystemUtils.isJavaVersionAtLeast("1.4", JavaSpecVersion.JAVA_1_5));
    }

    @Test
    void testIsJavaVersionAtLeast_ModernToModern() {
        assertTrue(SystemUtils.isJavaVersionAtLeast("21", JavaSpecVersion.JAVA_11));
        assertFalse(SystemUtils.isJavaVersionAtLeast("10", JavaSpecVersion.JAVA_11));
    }

    @Test
    void testIsJavaVersionAtLeast_CrossLegacyToModern() {
        assertFalse(SystemUtils.isJavaVersionAtLeast("1.8", JavaSpecVersion.JAVA_9));
        assertTrue(SystemUtils.isJavaVersionAtLeast("9", JavaSpecVersion.JAVA_1_8));
    }
}
