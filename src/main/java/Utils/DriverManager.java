package Utils;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.EventFiringDecorator;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import java.io.File;
import java.net.URL;
import java.time.Duration;

public class DriverManager {

    private static final ThreadLocal<AndroidDriver> rawDriverThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<WebDriver> decoratedDriverThreadLocal = new ThreadLocal<>();

    public static void initializeDriver(ExtentTest extentTest) {
        try {
            UiAutomator2Options options = new UiAutomator2Options();

            // 1. Prioritize System Properties (-D arguments from Maven/CI), fallback to ConfigReader
            String platformVersion = System.getProperty("platformVersion", ConfigReader.getProperty("platformVersion"));
            String deviceName = System.getProperty("deviceName", ConfigReader.getProperty("device_namein_docker_Emulator"));
            String automationName = System.getProperty("automationName", ConfigReader.getProperty("automationName"));

            options.setPlatformVersion(platformVersion);
            options.setDeviceName(deviceName);
            options.setAutomationName(automationName);

            options.setNoReset(false);
            options.setClearSystemFiles(true);
            options.setAutoGrantPermissions(true);

            // 2. Set App path dynamically as fallback if app isn't installed
            String appPath = System.getProperty("user.dir") + "/src/main/resources/APKFiles/ApiDemos-release.apk";
            File apkFile = new File(appPath);
            if (apkFile.exists()) {
                options.setApp(apkFile.getAbsolutePath());
            }

            // 3. Dynamically resolve server IP and port for local vs CI matrix execution
            String serverIp = System.getProperty("serverIp", ConfigReader.getProperty("serverIp"));
            String port = System.getProperty("port", ConfigReader.getProperty("port"));
            
            // Format URL cleanly (handles trailing slashes)
            String serverUrl = String.format("http://%s:%s/", serverIp, port);

            // 4. Initialize Driver Session
            AndroidDriver rawDriver = new AndroidDriver(new URL(serverUrl), options);
            rawDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

            String appPackage = ConfigReader.getProperty("appPackage");

            // 5. Activate App if installed, otherwise log installation
            if (appPackage != null && rawDriver.isAppInstalled(appPackage)) {
                if (extentTest != null) {
                    extentTest.log(Status.INFO, "App package '" + appPackage + "' is installed. Activating application...");
                }
                rawDriver.activateApp(appPackage);
            } else if (extentTest != null) {
                extentTest.log(Status.INFO, "App installed automatically via Capabilities setup.");
            }

            // 6. Wrap raw driver with listener using EventFiringDecorator
            AppiumEventListener listener = new AppiumEventListener(rawDriver, extentTest);
            WebDriver decoratedDriver = new EventFiringDecorator<>(listener).decorate(rawDriver);

            // 7. Store instances in ThreadLocal for thread safety
            rawDriverThreadLocal.set(rawDriver);
            decoratedDriverThreadLocal.set(decoratedDriver);

        } catch (Exception e) {
            throw new RuntimeException("Driver initialization failed: " + e.getMessage(), e);
        }
    }

    public static WebDriver getDriver() {
        return decoratedDriverThreadLocal.get();
    }

    public static AndroidDriver getRawDriver() {
        return rawDriverThreadLocal.get();
    }

    public static void startRecording() {
        if (getRawDriver() != null) {
            getRawDriver().startRecordingScreen();
        }
    }

    public static String stopRecording() {
        if (getRawDriver() != null) {
            return getRawDriver().stopRecordingScreen();
        }
        return "";
    }

    public static void quitDriver() {
        if (getRawDriver() != null) {
            try {
                getRawDriver().quit();
            } catch (Exception e) {
                System.err.println("Error quitting raw driver session: " + e.getMessage());
            } finally {
                rawDriverThreadLocal.remove();
                decoratedDriverThreadLocal.remove();
            }
        }
    }
}