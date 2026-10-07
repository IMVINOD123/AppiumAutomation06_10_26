package Utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentManager {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> extentTestThreadLocal = new ThreadLocal<>();

    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            ExtentSparkReporter spark = new ExtentSparkReporter("reports/ExtentReport.html");
            spark.config().setReportName("Appium Automation Execution Report");
            spark.config().setDocumentTitle("Test Results");
            spark.config().setTheme(Theme.STANDARD);

            // Inject custom CSS for light green pass and red fail text styling
            String customCss = 
                ".status.pass, .badge-pass { color: #2ecc71 !important; font-weight: bold; }" +
                ".status.fail, .badge-fail { color: #e74c3c !important; font-weight: bold; }" +
                ".test-status.pass { background-color: #2ecc71 !important; }" +
                ".test-status.fail { background-color: #e74c3c !important; }";

            spark.config().setCss(customCss);

            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Operating System", System.getProperty("os.name"));
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
            extent.setSystemInfo("User Name", System.getProperty("user.name"));
            extent.setSystemInfo("Environment", "QA");
            extent.setSystemInfo("Execution Engine", "Appium - UiAutomator2");
        }
        return extent;
    }

    public static ExtentTest createTest(String testName) {
        ExtentTest test = getInstance().createTest(testName);
        extentTestThreadLocal.set(test);
        return test;
    }

    public static ExtentTest getTest() {
        return extentTestThreadLocal.get();
    }

    public static void removeTest() {
        extentTestThreadLocal.remove();
    }

    public static void flush() {
        if (extent != null) {
            extent.flush();
        }
    }
}