package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class Resources {

    public static List<String> getResourceAsLines(String name) {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        try (InputStream is = classLoader.getResourceAsStream(name)) {
            if (is == null) {
                throw new IllegalArgumentException("Resource not found: " + name);
            }
            try (InputStreamReader isr = new InputStreamReader(is);
                 BufferedReader reader = new BufferedReader(isr)) {
                return reader.lines().toList();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getResourceAsString(String name) {
        // Normalize EOLs
        return String.join("\n", getResourceAsLines(name));
    }
}
