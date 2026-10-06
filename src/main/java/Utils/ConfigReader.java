package Utils;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;

public class ConfigReader {

    private static JSONObject configObject;

    static {
        try {
            InputStream inputStream = ConfigReader.class.getClassLoader().getResourceAsStream("config.json");
            if (inputStream == null) {
                inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.json");
            }
            if (inputStream == null) {
                throw new RuntimeException("config.json not found on classpath!");
            }

            JSONTokener tokener = new JSONTokener(inputStream);
            configObject = new JSONObject(tokener);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config.json: " + e.getMessage(), e);
        }
    }

    public static String getProperty(String key) {
        if (!configObject.has(key)) {
            throw new IllegalArgumentException("Key [" + key + "] not found in config.json");
        }
        return configObject.getString(key);
    }
}