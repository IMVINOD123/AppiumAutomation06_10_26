package BaseTest;

import Utils.AppiumServerManager;
import Utils.ConfigReader;
import Utils.DriverManager;
import Utils.ExtentManager;
import Utils.ScreenshotUtils;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.lang.reflect.Method;

public class BaseTest {

    public WebDriver getDriver() {
        return DriverManager.getDriver();
    }

    public AndroidDriver getRawDriver() {
        return DriverManager.getRawDriver();
    }

    @BeforeSuite
    public void startServer() {
    	// 1. Flush and purge old recorded videos from previous executions
        ScreenshotUtils.cleanVideoDirectory();

        // 2. Start Appium server programmatically
        AppiumServerManager.startServer();
        
    }

    @BeforeMethod
    public void setUp(Method method) {
        // 1. Force creation of an isolated ExtentTest node for THIS specific test method
        ExtentTest currentTestNode = ExtentManager.createTest(method.getName());

        // 2. Initialize Driver with thread-bound ExtentTest instance (Capabilities loaded from config.json)
        DriverManager.initializeDriver(currentTestNode);

        // 3. Start Appium screen recording for this specific test run
        DriverManager.startRecording();
        
        AndroidDriver driver = DriverManager.getRawDriver();
        String appPackage = ConfigReader.getProperty("appPackage");

        if (driver != null) {
            // 1. Terminate running instance from previous test
            if (driver.isAppInstalled(appPackage)) {
                driver.terminateApp(appPackage);
            }

            // 2. Relaunch fresh instance
            driver.activateApp(appPackage);
        }
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        // Retrieve the current test node bound to this thread
        ExtentTest currentTestNode = ExtentManager.getTest();

        try {
            // STEP A: Capture and attach individual test screenshot
            if (result.getStatus() == ITestResult.FAILURE) {
                String errorMessage = (result.getThrowable() != null) ? result.getThrowable().getMessage() : "Test Failed";
                ScreenshotUtils.attachScreenshotToTest(
                        getDriver(), 
                        currentTestNode, 
                        "<b>Execution Final State (FAILED):</b> " + errorMessage, 
                        Status.FAIL
                );
            } else if (result.getStatus() == ITestResult.SUCCESS) {
                ScreenshotUtils.attachScreenshotToTest(
                        getDriver(), 
                        currentTestNode, 
                        "<b>Execution Final State (PASSED):</b> Completed successfully.", 
                        Status.PASS
                );
            } else if (result.getStatus() == ITestResult.SKIP) {
                currentTestNode.log(Status.SKIP, "Test Skipped: " + result.getThrowable().getMessage());
            }

            // STEP B: Stop screen recording & attach unique MP4 video to current test node
            String base64Video = DriverManager.stopRecording();
            if (base64Video != null && !base64Video.trim().isEmpty()) {
                String relativeVideoPath = ScreenshotUtils.saveVideoFile(base64Video, result.getName());
                if (!relativeVideoPath.isEmpty()) {
                    currentTestNode.info("<b>Individual Execution Playback:</b><br/>"
                            + "<video width='320' height='240' controls><source src='" 
                            + relativeVideoPath + "' type='video/mp4'></video>");
                }
            }
        } catch (Exception e) {
            System.err.println("Error attaching media artifacts to test node: " + e.getMessage());
        } finally {
            // STEP C: Tear down driver session and clean up ThreadLocal bindings
            DriverManager.quitDriver();
            ExtentManager.removeTest();
        }
     // Optional: Terminate app after test completion
        AndroidDriver driver = DriverManager.getRawDriver();
        String appPackage = ConfigReader.getProperty("appPackage");
        if (driver != null && appPackage != null) {
            driver.terminateApp(appPackage);
        }
    }

    @AfterSuite
    public void stopServer() {
        AppiumServerManager.stopServer();
        ExtentManager.flush();
    }
}