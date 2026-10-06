package Utils;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.EventFiringDecorator;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import java.net.URL;
import java.time.Duration;

public class DriverManager {

    private static final ThreadLocal<AndroidDriver> rawDriverThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<WebDriver> decoratedDriverThreadLocal = new ThreadLocal<>();

    public static void initializeDriver(ExtentTest extentTest) {
        try {
            UiAutomator2Options options = new UiAutomator2Options();

            // Prioritize System Properties (-D argument from Maven/CI), fallback to ConfigReader
            String platformVersion = System.getProperty("platformVersion", ConfigReader.getProperty("platformVersion"));
            String deviceName = System.getProperty("deviceName", ConfigReader.getProperty("device_namein_docker_Emulator"));
            String automationName = System.getProperty("automationName", ConfigReader.getProperty("automationName"));

            options.setPlatformVersion(platformVersion);
            options.setDeviceName(deviceName);
            options.setAutomationName(automationName);

            options.setNoReset(false);
            options.setClearSystemFiles(true);
            options.setAutoGrantPermissions(true);

            // Dynamically resolve server IP and port for local vs CI matrix execution
            String serverIp = System.getProperty("serverIp", ConfigReader.getProperty("serverIp"));
            String port = System.getProperty("port", ConfigReader.getProperty("port"));
            String serverUrl = String.format("http://%s:%s/", serverIp, port);

            // 1. Initialize Driver Session
            AndroidDriver rawDriver = new AndroidDriver(new URL(serverUrl), options);
            rawDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

            String appPackage = ConfigReader.getProperty("appPackage");

            // 2. Check if App is installed on the target device/emulator
            if (rawDriver.isAppInstalled(appPackage)) {
                if (extentTest != null) {
                    extentTest.log(Status.INFO, "App is installed. Launching application...");
                }
                rawDriver.activateApp(appPackage);
            } else {
                throw new RuntimeException("App package '" + appPackage + "' is not installed on the target device. Pre-install failed.");
            }

            // Wrap raw driver with listener using EventFiringDecorator
            AppiumEventListener listener = new AppiumEventListener(rawDriver, extentTest);
            WebDriver decoratedDriver = new EventFiringDecorator<>(listener).decorate(rawDriver);

            // Store instances in ThreadLocal
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