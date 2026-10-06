package AutomationDemo;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;

import org.openqa.selenium.support.ui.WebDriverWait;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

public class Scrolling_Switching_AppDemo {

	public static void main(String[] args) throws MalformedURLException, InterruptedException {
		// TODO Auto-generated method stub
		DesiredCapabilities capabilities = new DesiredCapabilities();

		/* Real Device set up */

		/*
		 * capabilities.setCapability("deviceName", "Realme RMX3471");
		 * capabilities.setCapability("platformname", "Android");
		 * capabilities.setCapability("automationName", "uiautomator2");
		 * capabilities.setCapability("platformVersion", "14");
		 * 
		 * capabilities.setCapability("appPackage", "io.appium.android.apis");
		 * capabilities.setCapability("appActivity", "io.appium.android.apis.ApiDemos");
		 */

		/* ===============Emulator Demo================= */

		capabilities.setCapability("deviceName", "AndroidEmulator_21_09");
		capabilities.setCapability("platformname", "Android");
		capabilities.setCapability("automationName", "uiautomator2");
		capabilities.setCapability("platformVersion", "14");
		capabilities.setCapability("appPackage", "io.appium.android.apis");
		capabilities.setCapability("appActivity", "io.appium.android.apis.ApiDemos");

		URL url = URI.create("http://127.0.0.1:4724/").toURL();

		AndroidDriver driver = new AndroidDriver(url, capabilities);

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));

		System.out.println("Application Started--->Switching Handling<----");

		String viewsText = "Views";

		WebElement element = driver
				.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector().scrollable(true))"
						+ ".scrollIntoView(new UiSelector().text(\"" + viewsText + "\"))"));

		WebElement veiwLabel = driver.findElement(By.xpath("//android.widget.TextView[@content-desc=\"Views\"]"));
		veiwLabel.click();

		System.out.println("***************Scrolled Till View***************");

		String switchesText = "Switches";

		WebElement switchesElement = driver
				.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector().scrollable(true))"
						+ ".scrollIntoView(new UiSelector().text(\"" + switchesText + "\"))"));

		WebElement switchLable = wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//android.widget.TextView[@content-desc=\"Switches\"]"))

		);
		switchLable.click();
		System.out.println("***************Scrolled Till switch***************");

		WebElement StdSwitch = driver
				.findElement(By.xpath("//android.widget.Switch[@content-desc=\"Standard switch\"]"));
		StdSwitch.click();

		WebElement defaultOn = driver.findElement(By.xpath("//android.widget.Switch[@content-desc=\"Default is on\"]"));
		String defaultOnText = defaultOn.getText();

		System.out.println(defaultOnText);

		if (defaultOnText.equals("Default is on")) {
			System.out.println("pass");
		} else {
			System.out.println("falied");
		}

		WebElement monitoredSwitch = driver.findElement(By.id("io.appium.android.apis:id/monitored_switch"));

		if (monitoredSwitch.isSelected() == true) {
			System.out.println("Paas \n***************SuccessFully Switched On******************");

		} else {
			System.out.println("Switch is OFF");
			monitoredSwitch.click();
			Thread.sleep(6000);

			if (monitoredSwitch.isSelected() == true) {
				System.out.println("Paas \n***************SuccessFully Switched off******************");

			} else {
				System.out.println("Switch On");
				monitoredSwitch.click();
			}
		}
		Thread.sleep(5000);

		driver.quit();// CLOSE SESSION
		System.out.println("Application Session was closed");

	}
}
