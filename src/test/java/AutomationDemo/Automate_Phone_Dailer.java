package AutomationDemo;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;

import io.appium.java_client.android.AndroidDriver;

public class Automate_Phone_Dailer {

	public static void main(String[] args) throws MalformedURLException, InterruptedException {

		// TODO Auto-generated method stub
		/*
		 * Program to automate the Phone Dailer using code
		 */
		DesiredCapabilities capabilities = new DesiredCapabilities();

		/* Real Device set up */
		capabilities.setCapability("deviceName", "Realme RMX3471");
		capabilities.setCapability("platformname", "Android");
		capabilities.setCapability("automationName", "uiautomator2");
		capabilities.setCapability("platformVersion", "14");
		/* Native Application appPackage Name and appActivity */
		capabilities.setCapability("appPackage", "com.google.android.dialer");
		capabilities.setCapability("appActivity", "com.google.android.dialer.extensions.GoogleDialtactsActivity");

		URL url = URI.create("http://127.0.0.1:4724/").toURL();

		AndroidDriver driver = new AndroidDriver(url, capabilities);
		System.out.println("Application Started--->Dialer<----");

		// -->Steps to follow for Automate the Dailer 6362531967 dailer number

		/*
		 * Launch dailer pad
		 */
		// Click On Launch pad to get the dailer numbers pannle
		WebElement LaunchDialPad = driver.findElement(By.id("com.google.android.dialer:id/tab_dialpad"));
		LaunchDialPad.click();

		// Post Open of Dailer launch pad dail numbers

		WebElement num6 = driver.findElement(By.id("com.google.android.dialer:id/six"));
		num6.click();

		WebElement num3 = driver.findElement(By.id("com.google.android.dialer:id/three"));
		num3.click();
		WebElement num26 = driver.findElement(By.id("com.google.android.dialer:id/six"));
		num26.click();
		WebElement num2 = driver.findElement(By.id("com.google.android.dialer:id/two"));
		num2.click();
		WebElement num5 = driver.findElement(By.id("com.google.android.dialer:id/five"));
		num5.click();
		WebElement num23 = driver.findElement(By.id("com.google.android.dialer:id/three"));
		num23.click();
		WebElement num1 = driver.findElement(By.id("com.google.android.dialer:id/one"));
		num1.click();
		WebElement num9 = driver.findElement(By.id("com.google.android.dialer:id/nine"));
		num9.click();
		WebElement num36 = driver.findElement(By.id("com.google.android.dialer:id/six"));
		num36.click();
		WebElement num7 = driver.findElement(By.id("com.google.android.dialer:id/seven"));
		num7.click();

		WebElement dialerDigits = driver.findElement(By.id("com.google.android.dialer:id/digits"));
		String phoneNumber = dialerDigits.getText();
		// System.out.println(phoneNumber);

		if (phoneNumber.equals("63625 31967")) {
			System.out.println("pass");
			WebElement callButton = driver.findElement(By.id("com.google.android.dialer:id/dialpad_voice_call_button"));
			callButton.click();
			WebElement selectNumToCall = driver.findElement(By.xpath(
					"//android.widget.ListView[@resource-id=\"com.google.android.dialer:id/select_dialog_listview\"]/android.widget.LinearLayout[1]/android.widget.LinearLayout[@resource-id=\"com.google.android.dialer:id/text\"]"));
			selectNumToCall.click();
		} else {
			System.out.println("failed");
		}

		WebElement dialedNum = driver
				.findElement(By.xpath("//android.widget.TextView[@resource-id=\"contact_grid_bottom_row_label\"]"));
		String str = dialedNum.getText();
		String repStr = str.replaceAll("\\D+", " ");
		String removeSpace = repStr.trim();
		Thread.sleep(5000);

		Assert.assertEquals(removeSpace, phoneNumber);

		driver.quit();// CLOSE SESSION
		System.out.println("Application Session was closed");

	}

}
