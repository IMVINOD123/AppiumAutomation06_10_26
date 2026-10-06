package Pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.aventstack.extentreports.Status;

import Utils.ConfigReader;
import Utils.ExtentManager;
import Utils.JsonLocatorReader;

import java.time.Duration;

public class ViewsPage {

    private final AndroidDriver driver;
    private final WebDriverWait wait;
    private final String PAGE_NAME = "ViewsPage";

    // Constructor
    public ViewsPage(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // Action Methods
    public void toggleMonitoredSwitch() {
        driver.findElement(JsonLocatorReader.getLocator(PAGE_NAME, "monitoredSwitch")).click();
    }

    public String getToastText() {
        WebDriverWait toastWait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement toast = toastWait.until(ExpectedConditions.presenceOfElementLocated(
                JsonLocatorReader.getLocator(PAGE_NAME, "toastMessage")
        ));
        return toast.getText();
    }

 
           
   

    public void scrollToAndClickSwitches(String text) {
        driver.findElement(JsonLocatorReader.getLocator(PAGE_NAME, "scrollableText", text));
        WebElement switchLabel = wait.until(ExpectedConditions.visibilityOfElementLocated(
                JsonLocatorReader.getLocator(PAGE_NAME, "switchesLabel")
        ));
        switchLabel.click();
        ExtentManager.getTest().log(Status.INFO, "Clicked on Switch option");
    }

    public void clickStandardSwitch() {
        driver.findElement(JsonLocatorReader.getLocator(PAGE_NAME, "standardSwitch")).click();
        ExtentManager.getTest().log(Status.INFO, "Clicked on Standard Switch option");
    }

    public String getDefaultOnSwitchText() {
        return driver.findElement(JsonLocatorReader.getLocator(PAGE_NAME, "defaultOnSwitch")).getText();
    }

    public boolean isMonitoredSwitchSelected() {
        return driver.findElement(JsonLocatorReader.getLocator(PAGE_NAME, "monitoredSwitchLocator")).isSelected();
    }

    public void clickMonitoredSwitch() {
        driver.findElement(JsonLocatorReader.getLocator(PAGE_NAME, "monitoredSwitchLocator")).click();
    }
}