package AutomationDemo;

import static org.testng.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import io.appium.java_client.android.AndroidDriver;

public class Automate_Calculator_In_RealDevice {

	public static void main(String[] args) throws MalformedURLException, InterruptedException {
		// Gather Desired capabilities
		/* Device Names */

		// Realme RMX3471
		// AndroidEmulator_21_09

		DesiredCapabilities capabilities = new DesiredCapabilities();

		/* Real Device set up */
		capabilities.setCapability("deviceName", "Realme RMX3471");
		capabilities.setCapability("platformname", "Android");
		capabilities.setCapability("automationName", "uiautomator2");
		capabilities.setCapability("platformVersion", "14");
		/*Native Application appPackage Name and appActivity  */
		capabilities.setCapability("appPackage", "com.coloros.calculator");
		capabilities.setCapability("appActivity", "com.android.calculator2.Calculator");

		URL url = URI.create("http://127.0.0.1:4724/").toURL();

		AndroidDriver driver = new AndroidDriver(url, capabilities);
		System.out.println("Application Started");
		
		System.out.print("Calculator app Opens\n");
		
		/*
		 * Operations on Calculators
		 * 1 click on 8
		 * 2 click on +
		 * 3 click on 2
		 * 4 click on =
		 * 5 result 10
		 */
		
		//1 click on 8
		WebElement numb8=driver.findElement(By.id("com.coloros.calculator:id/digit_8"));
				numb8.click();
		//2 click on +
		WebElement pluseSign=driver.findElement(By.id("com.coloros.calculator:id/op_add"));
		pluseSign.click();
		//3 click on 2
	   WebElement num2=	driver.findElement(By.id("com.coloros.calculator:id/digit_2"));
		num2.click();
		//4 click on =
		WebElement equal= driver.findElement(By.id("com.coloros.calculator:id/eq"));
		equal.click();
		
		WebElement result=driver.findElement(By.id("com.coloros.calculator:id/result"));
		String resultValue=result.getText();
				
		if(resultValue.equals("10"))
		{
			System.out.println("Pass");
		}
		else
		{
			System.out.println("Failed");
		}
		
				
		Thread.sleep(5000);
		
		driver.quit();// CLOSE SESSION
		System.out.println("Application Session was closed");

	}

}
