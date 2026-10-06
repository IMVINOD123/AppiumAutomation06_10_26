package AutomationDemo;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.openqa.selenium.remote.DesiredCapabilities;

import io.appium.java_client.android.AndroidDriver;

public class Appium_DesireCapablities {

	public static void main(String[] args) throws MalformedURLException, InterruptedException {

		// Gather Desired capabilities
		/*Device Names*/
		
		//Realme RMX3471
		//AndroidEmulator_21_09

		DesiredCapabilities capabilities = new DesiredCapabilities();

		/* Real-Devices set up */
//		capabilities.setCapability("deviceName", "AndroidEmulator_21_09");
//		capabilities.setCapability("platformname", "Android");
//		capabilities.setCapability("automationName", "uiautomator2");
//		capabilities.setCapability("app", "C:\\QA_Interview_Notes\\Mobile_Automation\\APK_Files\\\\mda-2.2.0-25.apk");
//		capabilities.setCapability("platformVersion", "14");
		
		/* Emulator set up */
		capabilities.setCapability("deviceName", "Realme RMX3471");
		capabilities.setCapability("platformname", "Android");
		capabilities.setCapability("automationName", "uiautomator2");
		capabilities.setCapability("app", "C:\\QA_Interview_Notes\\Mobile_Automation\\APK_Files\\\\mda-2.2.0-25.apk");
		capabilities.setCapability("platformVersion", "14");

		URL url = URI.create("http://127.0.0.1:4724/").toURL();

		AndroidDriver driver = new AndroidDriver(url, capabilities);

		Thread.sleep(5000);
		System.out.println("Application Started");
		driver.quit();// CLOSE SESSION
		System.out.println("Application Session was closed");

	}

}
