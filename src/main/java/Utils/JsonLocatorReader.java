package Utils;

import io.appium.java_client.AppiumBy;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.openqa.selenium.By;

import java.io.InputStream;

public class JsonLocatorReader {

	private static JSONObject jsonObject;

	static {
		try {
			InputStream inputStream = JsonLocatorReader.class.getClassLoader().getResourceAsStream("locators.json");

			if (inputStream == null) {
				inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("locators.json");
			}

			if (inputStream == null) {
				throw new RuntimeException("locators.json file not found in target/test-classes directory!");
			}

			JSONTokener tokener = new JSONTokener(inputStream);
			jsonObject = new JSONObject(tokener);

		} catch (Exception e) {
			System.err.println("CRITICAL ERROR: Failed to load locators.json!");
			e.printStackTrace();
			throw new ExceptionInInitializerError(e);
		}
	}

	public static By getLocator(String pageName, String elementName, Object... args) {
		try {
			JSONObject elementObj = jsonObject.getJSONObject(pageName).getJSONObject(elementName);
			String type = elementObj.getString("type");
			String rawValue = elementObj.getString("value");

			String value = (args.length > 0) ? String.format(rawValue, args) : rawValue;

			switch (type.toLowerCase()) {
			case "id":
				return By.id(value);
			case "xpath":
				return By.xpath(value);
			case "accessibilityid":
			case "accessibility_id":
				return AppiumBy.accessibilityId(value);
			case "androiduiautomator":
				return AppiumBy.androidUIAutomator(value);
			case "classname":
			case "class":
				return By.className(value);
			default:
				throw new IllegalArgumentException("Unsupported locator type: " + type);
			}
		} catch (Exception e) {
			throw new RuntimeException(
					"Error fetching locator for [" + pageName + " -> " + elementName + "]: " + e.getMessage(), e);
		}
	}
}