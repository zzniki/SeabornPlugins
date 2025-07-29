package com.oheers.fish.api.utils.system;

import org.jetbrains.annotations.NotNull;

public class SystemUtils {
    public static final String JAVA_VERSION = System.getProperty("java.specification.version");

    private SystemUtils() {
        throw new UnsupportedOperationException();
    }

    public static boolean isJavaVersionAtLeast(@NotNull JavaSpecVersion required) {
        return isJavaVersionAtLeast(JAVA_VERSION, required);
    }

    static boolean isJavaVersionAtLeast(@NotNull String currentVersion, @NotNull JavaSpecVersion required) {
        float current = parseJavaVersion(currentVersion);
        return current >= required.getValue();
    }

    static float parseJavaVersion(@NotNull String version) {
        String[] parts = version.split("\\.");

        try {
            // Handle legacy format like "1.8"
            if (parts[0].equals("1")) {
                if (parts.length > 1 && !parts[1].isBlank()) {
                    return Float.parseFloat(parts[0] + "." + parts[1]);
                }

                return -1F; // Invalid legacy format like "1." or "1."
            }

            // Handle modern format like "9", "11", etc.
            if (parts.length == 1 && !parts[0].isBlank()) {
                return Float.parseFloat(parts[0]);
            }

            return -1F; // Catch other malformed formats
        } catch (NumberFormatException e) {
            return -1F;
        }
    }

}

