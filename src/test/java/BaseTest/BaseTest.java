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
import java.time.Duration;

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

        // 2. Start Appium server programmatically (if not already managed by CI)
        String isCI = System.getenv("GITHUB_ACTIONS");
        if (isCI == null || !isCI.equalsIgnoreCase("true")) {
            AppiumServerManager.startServer();
        }
    }

    @BeforeMethod
    public void setUp(Method method) {
        // 1. Force creation of an isolated ExtentTest node for THIS specific test method
        ExtentTest currentTestNode = ExtentManager.createTest(method.getName());

        // 2. Initialize Driver with thread-bound ExtentTest instance (Capabilities loaded from config or System properties)
        DriverManager.initializeDriver(currentTestNode);

        // 3. Start Appium screen recording for this specific test run
        DriverManager.startRecording();
        
        AndroidDriver driver = DriverManager.getRawDriver();
        String appPackage = ConfigReader.getProperty("appPackage");

        if (driver != null && appPackage != null) {
            try {
                // Terminate any background instance and relaunch fresh home state
                if (driver.isAppInstalled(appPackage)) {
                    driver.terminateApp(appPackage);
                    // Small delay to ensure process terminates completely on slow CI emulators
                    Thread.sleep(1000); 
                    driver.activateApp(appPackage);
                    // Give the home screen activity 2 seconds to stabilize on headless runners
                    Thread.sleep(2000); 
                }
            } catch (Exception e) {
                System.err.println("Warning during app state reset in @BeforeMethod: " + e.getMessage());
            }
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

            // STEP C: Terminate app after test completion (Executed while driver is still active)
            AndroidDriver driver = DriverManager.getRawDriver();
            String appPackage = ConfigReader.getProperty("appPackage");
            if (driver != null && appPackage != null) {
                try {
                    driver.terminateApp(appPackage);
                } catch (Exception e) {
                    System.err.println("Warning terminating app during tearDown: " + e.getMessage());
                }
            }

        } catch (Exception e) {
            System.err.println("Error attaching media artifacts to test node: " + e.getMessage());
        } finally {
            // STEP D: Tear down driver session and clean up ThreadLocal bindings
            DriverManager.quitDriver();
            ExtentManager.removeTest();
        }
    }

    @AfterSuite
    public void stopServer() {
        String isCI = System.getenv("GITHUB_ACTIONS");
        if (isCI == null || !isCI.equalsIgnoreCase("true")) {
            AppiumServerManager.stopServer();
        }
        ExtentManager.flush();
    }
}