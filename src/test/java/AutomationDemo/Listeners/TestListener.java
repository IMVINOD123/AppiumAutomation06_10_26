package AutomationDemo.Listeners;

import Utils.ExtentManager;
import Utils.DriverManager;
import Utils.ScreenshotUtils;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.MediaEntityBuilder;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

	@Override
	public void onStart(ITestContext context) {
		ExtentManager.getInstance();
	}

	@Override
	public void onTestStart(ITestResult result) {
		ExtentManager.createTest(result.getMethod().getMethodName());
		ExtentManager.getTest().log(Status.INFO, "Started test execution: " + result.getMethod().getMethodName());
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		ExtentManager.getTest().log(Status.PASS, "Test Passed Successfully");
		attachVideoToReport(result.getMethod().getMethodName());
	}

	@Override
	public void onTestFailure(ITestResult result) {
		ExtentManager.getTest().log(Status.FAIL, "Test Failed: " + result.getThrowable());

		// Use WebDriver interface type
		WebDriver driver = DriverManager.getDriver();

		if (driver != null) {
			// Capture and Attach Failure Screenshot
			String base64Image = ScreenshotUtils.captureBase64Screenshot(driver);
			if (!base64Image.isEmpty()) {
				ExtentManager.getTest().fail("Failure Screenshot",
						MediaEntityBuilder.createScreenCaptureFromBase64String(base64Image).build());
			}
		} else {
			System.err.println("Driver was NULL in TestListener during failure!");
		}

		attachVideoToReport(result.getMethod().getMethodName());
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		ExtentManager.getTest().log(Status.SKIP, "Test Skipped: " + result.getThrowable());
		DriverManager.stopRecording();
	}

	@Override
	public void onFinish(ITestContext context) {
		ExtentManager.flush();
	}

	private void attachVideoToReport(String methodName) {
		String base64Video = DriverManager.stopRecording();

		if (base64Video != null && !base64Video.isEmpty()) {
			String relativeVideoPath = ScreenshotUtils.saveVideoFile(base64Video, methodName);

			if (!relativeVideoPath.isEmpty()) {
				String videoHtmlTag = "<br><b>Execution Recording Video:</b><br>"
						+ "<video width='320' height='600' controls>" + "<source src='" + relativeVideoPath
						+ "' type='video/mp4'>" + "Your browser does not support the video tag." + "</video>";

				ExtentManager.getTest().log(Status.INFO, videoHtmlTag);
			}
		}
	}
}