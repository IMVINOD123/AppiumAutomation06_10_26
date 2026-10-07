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
            String dirPath = System.getProperty("user.dir") + File.separator + "reports" + File.separator + "videos";
            File videoDir = new File(dirPath);
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

    /**
     * Decodes Base64 video string and saves it into reports/videos/ directory.
     * 
     * @param base64Video The base64 video string from driver.stopRecordingScreen()
     * @param testName Name of the test method
     * @return Relative path ("videos/fileName.mp4") to embed directly into Extent HTML reports
     */
    public static String saveVideoFile(String base64Video, String testName) {
        if (base64Video == null || base64Video.trim().isEmpty()) {
            System.err.println("[Video Log] Base64 video string is empty or null.");
            return "";
        }

        try {
            // 1. Ensure target directory structure exists
            String dirPath = System.getProperty("user.dir") + File.separator + "reports" + File.separator + "videos";
            File videoDir = new File(dirPath);
            if (!videoDir.exists()) {
                boolean created = videoDir.mkdirs();
                if (created) {
                    System.out.println("[Video Log] Created directory: " + videoDir.getAbsolutePath());
                }
            }

            // 2. Sanitize testName to prevent illegal path characters
            String sanitizedTestName = testName.replaceAll("[^a-zA-Z0-9_-]", "_");
            String fileName = sanitizedTestName + "_" + System.currentTimeMillis() + ".mp4";
            File destinationFile = new File(videoDir, fileName);

            // 3. Clean up Base64 string from newline characters or white space
            String cleanBase64 = base64Video.replaceAll("\\s+", "");
            byte[] videoBytes = Base64.getDecoder().decode(cleanBase64);

            // 4. Write binary video bytes directly to disk
            try (OutputStream stream = new FileOutputStream(destinationFile)) {
                stream.write(videoBytes);
                stream.flush();
            }

            System.out.println("[Video Log] Video saved successfully at: " + destinationFile.getAbsolutePath());

            // 5. Return relative path for HTML Extent Report
            return "videos/" + fileName;

        } catch (Exception e) {
            System.err.println("[Video Error] Failed to save video file for test '" + testName + "': " + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }
}