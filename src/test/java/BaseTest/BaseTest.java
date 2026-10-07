package BaseTest;

import Utils.AppiumServerManager;
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

		// 2. Start Appium server programmatically (if not managed by CI)
		String isCI = System.getenv("GITHUB_ACTIONS");
		if (isCI == null || !isCI.equalsIgnoreCase("true")) {
			AppiumServerManager.startServer();
		}
	}

	@BeforeClass
	public void setUpClass() {
		// 1. Initialize Driver ONCE for all test methods in this class
		DriverManager.initializeDriver(null);

		// 2. Start screen recording for the class execution session
		DriverManager.startRecording();
	}

	@BeforeMethod
	public void setUpMethod(Method method) {
		// 🟢 Create individual Extent Report test node for each @Test method
		ExtentTest testNode = ExtentManager.createTest(method.getName());

		// Attach test description if declared in @Test(description = "...")
		Test testAnnotation = method.getAnnotation(Test.class);
		if (testAnnotation != null && !testAnnotation.description().isEmpty()) {
			testNode.info(testAnnotation.description());
			DriverManager.startRecording();
		}
	}

	@AfterMethod
	public void logTestResult(ITestResult result) {
		ExtentTest currentTestNode = ExtentManager.getTest();

		if (currentTestNode != null) {
			// STEP A: Log Pass/Fail Status and Screenshot
			if (result.getStatus() == ITestResult.FAILURE) {
				String errorMessage = (result.getThrowable() != null) ? result.getThrowable().getMessage()
						: "Test Failed";
				ScreenshotUtils.attachScreenshotToTest(getDriver(), currentTestNode,
						"<b>Execution Final State (FAILED):</b> " + errorMessage, Status.FAIL);
			} else if (result.getStatus() == ITestResult.SUCCESS) {
				ScreenshotUtils.attachScreenshotToTest(getDriver(), currentTestNode,
						"<b>Execution Final State (PASSED):</b> Completed successfully.", Status.PASS);
			} else if (result.getStatus() == ITestResult.SKIP) {
				currentTestNode.log(Status.SKIP, "Test Skipped: " + result.getName());
			}

			// 🟢 STEP B: Embed Base64 Video Directly into Report HTML
			try {
				String base64Video = DriverManager.stopRecording();
				if (base64Video != null && !base64Video.trim().isEmpty()) {
					// Inline Base64 video tag eliminates relative file dependency on CI
					String videoHtml = "<b>Test Case Execution Playback:</b><br/>"
							+ "<video width='320' height='240' controls>" + "<source src='data:video/mp4;base64,"
							+ base64Video + "' type='video/mp4'>" + "</video>";

					currentTestNode.info(videoHtml);
				} else {
					currentTestNode.info("<i>Screen recording was empty or skipped by Appium server.</i>");
				}
			} catch (Exception e) {
				System.err.println("Error attaching video for " + result.getName() + ": " + e.getMessage());
			}
		}
	}

	@AfterClass
	public void tearDownClass() {
		try {
			// Tear down driver session and clear ExtentThreadLocal reference
			DriverManager.quitDriver();
		} finally {
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