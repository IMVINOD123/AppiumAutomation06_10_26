package Utils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;

public class AppiumServerManager {

    private static AppiumDriverLocalService service;

    public static void startServer() {
        int port = Integer.parseInt(ConfigReader.getProperty("port"));
        String ip = ConfigReader.getProperty("serverIp");

        // 1. Check if Appium Server is already running inside Docker
        if (isServerListening(ip, port)) {
            System.out.println("Appium Server is active at http://" + ip + ":" + port + "/ (Docker Container detected).");
            return;
        }

        // 2. Fallback: Start local Appium server programmatically if Docker is not running
        if (service == null || !service.isRunning()) {
            AppiumServiceBuilder builder = new AppiumServiceBuilder()
                    .usingPort(port)
                    .withIPAddress(ip)
                    .withArgument(() -> "--base-path", "/")
                    .withArgument(GeneralServerFlag.SESSION_OVERRIDE)
                    .withArgument(GeneralServerFlag.LOG_LEVEL, "error")
                    .withTimeout(Duration.ofSeconds(60));

            service = AppiumDriverLocalService.buildService(builder);
            service.start();
            System.out.println("Appium Server started programmatically at: " + service.getUrl().toString());
        }
    }

    public static void stopServer() {
        if (service != null && service.isRunning()) {
            service.stop();
            System.out.println("Local Appium Server stopped.");
        } else {
            System.out.println("Docker Appium Server container left running.");
        }
    }

    private static boolean isServerListening(String ip, int port) {
        try {
            URL url = new URL("http://" + ip + ":" + port + "/status");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(2000);
            connection.connect();
            return connection.getResponseCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getServerUrl() {
        String ip = ConfigReader.getProperty("serverIp");
        String port = ConfigReader.getProperty("port");
        return "http://" + ip + ":" + port + "/";
    }
}