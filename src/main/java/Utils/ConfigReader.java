package Utils;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;

public class ConfigReader {

    private static JSONObject configObject = new JSONObject();

    static {
        // Load config.json first
        loadJsonFile("config.json");
        // Load testdata.json second
        loadJsonFile("testdata.json");
    }

    private static void loadJsonFile(String fileName) {
        try {
            InputStream inputStream = ConfigReader.class.getClassLoader().getResourceAsStream(fileName);
            if (inputStream == null) {
                inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            }
            if (inputStream != null) {
                JSONTokener tokener = new JSONTokener(inputStream);
                JSONObject loadedObject = new JSONObject(tokener);

                // Merge loaded keys into the primary configObject
                for (String key : loadedObject.keySet()) {
                    configObject.put(key, loadedObject.get(key));
                }
            } else {
                System.out.println("Notice: File '" + fileName + "' not found on classpath.");
            }
        } catch (Exception e) {
            System.err.println("Notice: Failed to load " + fileName + ": " + e.getMessage());
        }
    }

    public static String getProperty(String key) {
        // 1. Prioritize System Properties passed via Maven CLI (-Dkey=value)
        String sysProp = System.getProperty(key);
        if (sysProp != null && !sysProp.trim().isEmpty()) {
            return sysProp;
        }

        // 2. Fall back to merged JSON data (config.json & testdata.json)
        if (configObject != null && configObject.has(key)) {
            return configObject.optString(key, null);
        }

        return null;
    }
}