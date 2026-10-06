package AutomationDemo;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;

import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

public class AmezonLaunch {

    public static void main(String[] args) throws MalformedURLException {

        // 1. Initialize UiAutomator2Options instead of legacy DesiredCapabilities
        UiAutomator2Options options = new UiAutomator2Options();

        options.setDeviceName("AndroidEmulator_21_09");
        options.setPlatformName("Android");
        options.setPlatformVersion("14");
        options.setAutomationName("UiAutomator2");

        // 2. Set App Package
        options.setAppPackage("in.amazon.mShop.android.shopping");

        // 3. Set Home / Launcher Activity (Exported activity)
        options.setAppActivity("com.amazon.mShop.home.HomeActivity");

        // 4. Set App Wait Activity to catch MainActivity once it loads internally
        options.setAppWaitActivity("com.amazon.mShop.*");
        
        options.setNoReset(true);

        // 5. Connect to Appium Server
        URL url = URI.create("http://127.0.0.1:4724/").toURL();

        AndroidDriver driver = new AndroidDriver(url, options);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));

        System.out.println("<------------Application Started ------------->");
    }
}