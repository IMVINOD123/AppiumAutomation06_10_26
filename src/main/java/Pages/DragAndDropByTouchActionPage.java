package Pages;

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

public class DragAndDropByTouchActionPage {

    private final AndroidDriver driver;
    private final WebDriverWait wait;
    private final String PAGE_NAME = ConfigReader.getProperty("DragAndDropPageText");

    public DragAndDropByTouchActionPage(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void scrollToAndView(String text) {
        // 1. Explicitly wait for the scrollable container to be ready on the screen
        wait.until(ExpectedConditions.presenceOfElementLocated(
            AppiumBy.androidUIAutomator("new UiSelector().scrollable(true)")
        ));

        WebElement targetElement;
        
        try {
            // 2. Scroll into view and capture the target element
            targetElement = driver.findElement(
                JsonLocatorReader.getLocator(PAGE_NAME, ConfigReader.getProperty("scrollableText"), text)
            );
        } catch (Exception e) {
            // Fallback: If scrolling fails or element is already visible, attempt direct text search
            targetElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                AppiumBy.xpath("//android.widget.TextView[@text='" + text + "']")
            ));
        }

        // 3. Click once the element is confirmed clickable
        wait.until(ExpectedConditions.elementToBeClickable(targetElement)).click();

        ExtentManager.getTest().log(Status.INFO, "Scrolled and clicked on " + text + " option");
    }

    public void dragAndDropLabel() {
        WebElement dragAndDropOption = wait.until(ExpectedConditions.elementToBeClickable(
            JsonLocatorReader.getLocator(PAGE_NAME, ConfigReader.getProperty("dragAndDropLabel"))
        ));
        dragAndDropOption.click();
        
        ExtentManager.getTest().log(Status.INFO, "Clicked on Drag and Drop option");
    }

    public String verifyDragAndDrop() {
        WebElement droppedValues = wait.until(ExpectedConditions.visibilityOfElementLocated(
            JsonLocatorReader.getLocator(PAGE_NAME, "droppedText")
        ));
        return droppedValues.getText();
    }

    public void W3CDragAndDrop() {
        WebElement source = wait.until(ExpectedConditions.visibilityOfElementLocated(
            JsonLocatorReader.getLocator(PAGE_NAME, "dot1")
        ));
        WebElement target = wait.until(ExpectedConditions.visibilityOfElementLocated(
            JsonLocatorReader.getLocator(PAGE_NAME, "dot2")
        ));

        int targetX = target.getLocation().getX() + (target.getSize().getWidth() / 2);
        int targetY = target.getLocation().getY() + (target.getSize().getHeight() / 2);

        ((JavascriptExecutor) driver).executeScript("mobile: dragGesture",
            ImmutableMap.of(
                "elementId", ((RemoteWebElement) source).getId(),
                "endX", targetX,
                "endY", targetY
            ));
    }
}