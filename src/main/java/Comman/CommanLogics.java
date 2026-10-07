package Comman;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import Utils.ExtentManager;
import Utils.JsonLocatorReader;
import java.time.Duration;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.aventstack.extentreports.Status;
import com.google.common.collect.ImmutableMap;

import Utils.ConfigReader;
import Utils.ExtentManager;
import Utils.JsonLocatorReader;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
public class CommanLogics {

	  private final AndroidDriver driver;
	    private final WebDriverWait wait;
	    private final String PAGE_NAME = ConfigReader.getProperty("DragAndDropPageText");

	    public CommanLogics(AndroidDriver driver) {
	        this.driver = driver;
	        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
	    }
	    
	public void scrollTo(String text) {
	    WebElement targetElement;

	    try {
	        // 1. First, check if the element is already visible on screen (short 2-second timeout)
	        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(2));
	        targetElement = shortWait.until(ExpectedConditions.presenceOfElementLocated(
	            JsonLocatorReader.getLocator(PAGE_NAME, "textLabelByContentDesc", text)
	        ));
	        ExtentManager.getTest().log(Status.INFO, "'" + text + "' is already visible on screen. Skipping scroll.");
	    } catch (Exception e) {
	        // 2. If NOT visible on screen, execute UiScrollable to find and scroll to it
	        ExtentManager.getTest().log(Status.INFO, "'" + text + "' not visible. Executing scroll into view...");
	        targetElement = driver.findElement(
	            JsonLocatorReader.getLocator(PAGE_NAME, ConfigReader.getProperty("scrollableText"), text)
	        );
	    }

	    // 3. Click the element once confirmed clickable
	    wait.until(ExpectedConditions.elementToBeClickable(targetElement)).click();
	    ExtentManager.getTest().log(Status.INFO, "Clicked on " + text + " option");
	}
}
