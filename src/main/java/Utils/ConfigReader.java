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
            if (inputStream != null) {
                JSONTokener tokener = new JSONTokener(inputStream);
                configObject = new JSONObject(tokener);
            }
        } catch (Exception e) {
            System.err.println("Notice: Failed to load config.json: " + e.getMessage());
        }
    }

    public static String getProperty(String key) {
        // 1. Prioritize System Properties passed via Maven command line (-Dkey=value)
        String sysProp = System.getProperty(key);
        if (sysProp != null && !sysProp.trim().isEmpty()) {
            return sysProp;
        }

        // 2. Fall back to config.json file
        if (configObject != null && configObject.has(key)) {
            return configObject.getString(key);
        }

        return null;
    }
}