package Pages;

import java.time.Duration;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.aventstack.extentreports.Status;

import Utils.ConfigReader;
import Utils.ExtentManager;
import Utils.JsonLocatorReader;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

public class LoginPage {

	private final AndroidDriver driver;
	private final WebDriverWait wait;
	private final String PAGE_NAME = ConfigReader.getProperty("SauceLabs_LoginPage");

	public LoginPage(AndroidDriver driver) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
	}

	public void clickOnOpenMenu() {
		driver.findElement(JsonLocatorReader.getLocator(PAGE_NAME, "MenuLabel")).click();
		ExtentManager.getTest().log(Status.INFO, "Clicked on Standard Menu Option");
	}

	public void navigateToLoginPage() {
		driver.findElement(JsonLocatorReader.getLocator(PAGE_NAME, "LoginLabel")).click();
		ExtentManager.getTest().log(Status.INFO, "Clicked on Login-Label");
	}

	public String verifyLoginPageTitle() {
		String element = driver.findElement(JsonLocatorReader.getLocator(PAGE_NAME, "LoginDashBoard")).getText();
		return element;
	}

	public void enterCredentials(String username, String password) {
		// 1. Locate Username field, wait until visible, clear existing text, and type
		// username

		WebElement usernameField = wait.until(ExpectedConditions
				.visibilityOfElementLocated(JsonLocatorReader.getLocator(PAGE_NAME, "UsernameInput")));
		usernameField.clear();
		usernameField.sendKeys(username);
		String formattedUsername = "<b style='color:green;'>" + username + "</b>";
		ExtentManager.getTest().log(Status.INFO, "Entered Username: " + formattedUsername);

		// 2. Locate Password field, wait until visible, clear existing text, and type
		// password
		WebElement passwordField = wait.until(ExpectedConditions
				.visibilityOfElementLocated(JsonLocatorReader.getLocator(PAGE_NAME, "PasswordInput")));
		passwordField.clear();
		passwordField.sendKeys(password);
		ExtentManager.getTest().log(Status.INFO, "Entered Password successfully");

		// 3. Optional: Hide keyboard after typing (useful on Android emulators/devices)
		try {
			driver.hideKeyboard();
		} catch (Exception e) {
			// Ignore if keyboard is already hidden or not supported on target environment
		}

	}

	public void clickLoginButton() {
		driver.findElement(JsonLocatorReader.getLocator(PAGE_NAME, "loginButton")).click();
		ExtentManager.getTest().log(Status.INFO, "Clicked on Login-Label");

	}

	public String isUserLoggedIn() {
		String element = driver.findElement(JsonLocatorReader.getLocator(PAGE_NAME, "ProductLabel")).getText();
		return element;

	}

	public void LogOut() {
		driver.findElement(JsonLocatorReader.getLocator(PAGE_NAME, "LogOutLabel")).click();
		ExtentManager.getTest().log(Status.INFO, "Clicked on Login-Label");
	}

	public String getAlertTitleText() {
		WebElement titleElement = wait.until(
				ExpectedConditions.visibilityOfElementLocated(JsonLocatorReader.getLocator(PAGE_NAME, "AlertTitle")));
		String actualTitle = titleElement.getText();

		ExtentManager.getTest().log(Status.INFO, "Alert Title Displayed: <b>" + actualTitle + "</b>");
		return actualTitle;
	}

	public String getAlertMessageText() {
		WebElement messageElement = wait.until(
				ExpectedConditions.visibilityOfElementLocated(JsonLocatorReader.getLocator(PAGE_NAME, "AlertMessage")));
		String actualMessage = messageElement.getText();

		ExtentManager.getTest().log(Status.INFO, "Alert Message Displayed: <b>" + actualMessage + "</b>");
		return actualMessage;
	}

	public String getErrorMessageTextForUserName() {
		String element = driver.findElement(JsonLocatorReader.getLocator(PAGE_NAME, "errorEmptyFieldUserName"))
				.getText();
		return element;

	}

	public String getErrorMessageTextForPassword() {
		String element = driver.findElement(JsonLocatorReader.getLocator(PAGE_NAME, "errorEmptyFieldPassword"))
				.getText();
		return element;

	}

	public String getErrorMessageTextForInvalidUser() {
		String element = driver.findElement(JsonLocatorReader.getLocator(PAGE_NAME, "bothUserAndPasswordInvalid"))
				.getText();
		return element;
	}

	public void assertAndConfirmLogout() {
		// Assert Alert Title
		String actualTitle = getAlertTitleText();
		Assert.assertEquals(actualTitle, "Log Out", "Alert title mismatch!");

		// Assert Alert Message
		String actualMessage = getAlertMessageText();
		Assert.assertEquals(actualMessage, "Are you sure you sure you want to logout?", "Alert message mismatch!");

		// Click LOG OUT button
		wait.until(ExpectedConditions.elementToBeClickable(JsonLocatorReader.getLocator(PAGE_NAME, "confirmLogOutBtn")))
				.click();
		ExtentManager.getTest().log(Status.INFO, "Clicked on <b style='color:green;'>LOG OUT</b> button on dialog");
	}

	public String getLogoutSuccessAlertTitleText() {
		WebElement titleElement = wait.until(ExpectedConditions
				.visibilityOfElementLocated(JsonLocatorReader.getLocator(PAGE_NAME, "AlertTitleSuccessfully")));
		String actualTitle = titleElement.getText();
		ExtentManager.getTest().log(Status.INFO, "Success Alert Title Displayed: <b>" + actualTitle + "</b>");

		return actualTitle;
	}

	public void clickOnOkBtn() {
		// Click OK button to dismiss dialog
		wait.until(ExpectedConditions
				.elementToBeClickable(JsonLocatorReader.getLocator(PAGE_NAME, "AlertTitleSuccessfullyOkBtn"))).click();
		ExtentManager.getTest().log(Status.INFO, "Clicked on <b style='color:green;'>OK</b> button on dialog");
	}
	public String getErrorMessageTextForLockedOutUser()
	{
		String element=driver.findElement(JsonLocatorReader.getLocator(PAGE_NAME, "lockedOutUser")).getText();
		return element;
		
	}
}
