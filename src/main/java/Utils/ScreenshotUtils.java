package Utils;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Base64;

public class ScreenshotUtils {

    public static String captureBase64Screenshot(WebDriver driver) {
        if (driver == null) {
            return "";
        }
        try {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            System.err.println("Failed to capture screenshot: " + e.getMessage());
            return "";
        }
    }

    public static void attachScreenshotToTest(WebDriver driver, ExtentTest testNode, String message, Status status) {
        String base64Image = captureBase64Screenshot(driver);
        if (testNode != null && !base64Image.isEmpty()) {
            testNode.log(status, message, 
                MediaEntityBuilder.createScreenCaptureFromBase64String(base64Image).build());
        } else if (testNode != null) {
            testNode.log(status, message);
        }
    }

    /**
     * Deletes all pre-existing video recordings inside reports/videos/ before execution starts.
     */
    public static void cleanVideoDirectory() {
        try {
            File videoDir = new File("reports/videos");
            if (videoDir.exists() && videoDir.isDirectory()) {
                File[] files = videoDir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isFile() && file.getName().endsWith(".mp4")) {
                            file.delete();
                        }
                    }
                }
                System.out.println("Flushed old video recordings from reports/videos/");
            }
        } catch (Exception e) {
            System.err.println("Failed to clean video directory: " + e.getMessage());
        }
    }

    public static String saveVideoFile(String base64Video, String testName) {
        if (base64Video == null || base64Video.trim().isEmpty()) {
            return "";
        }
        try {
            File videoDir = new File("reports/videos");
            if (!videoDir.exists()) {
                videoDir.mkdirs();
            }

            // Append unique System nanosecond timestamp to guarantee isolated file paths
            String fileName = testName + "_" + System.nanoTime() + ".mp4";
            String fullPath = "reports/videos/" + fileName;

            byte[] videoBytes = Base64.getDecoder().decode(base64Video);
            try (OutputStream stream = new FileOutputStream(fullPath)) {
                stream.write(videoBytes);
                stream.flush(); // Flush video output buffer directly to disk
            }

            // Returns relative path for Extent HTML video rendering
            return "videos/" + fileName;
        } catch (Exception e) {
            System.err.println("Failed to save unique video file: " + e.getMessage());
            return "";
        }
    }
}