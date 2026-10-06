package Utils;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverListener;

public class AppiumEventListener implements WebDriverListener {

    private final AndroidDriver driver;
    private final ExtentTest extentTest;

    public AppiumEventListener(AndroidDriver driver, ExtentTest extentTest) {
        this.driver = driver;
        this.extentTest = extentTest; 
        // DO NOT call ExtentManager.createTest(...) here!
    }

    @Override
    public void beforeClick(WebElement element) {
        if (extentTest != null) {
            extentTest.log(Status.INFO, "Clicking on element: " + element.toString());
        }
    }

    @Override
    public void afterClick(WebElement element) {
        if (extentTest != null) {
            extentTest.log(Status.INFO, "Clicked element successfully");
        }
    }

    // Include other listener overrides as needed without invoking ExtentManager.createTest()
}