package tests;

import static org.testng.Assert.assertEquals;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import BaseTest.BaseTest;
import Comman.CommanLogics;
import Pages.LoginPage;
import Utils.ConfigReader;
import Utils.DriverManager;
import Utils.ExtentManager;

public class LoginTest_SauceLabs_MyDemoApp extends BaseTest {

	private LoginPage loginPage;
	private CommanLogics comman;

	private String validUsername;
	private String validPassword;
	private String lockedOutUser;
	private String invalidUsername;
	private String invalidPassword;

	@BeforeClass
	public void BasesetUp() {
		// Calls parent BaseTest.setUp() to ensure driver initialization
		// super.setUp();

		// Read configuration after driver is ready
		validUsername = ConfigReader.getProperty("SauceLabs_userName");
		validPassword = ConfigReader.getProperty("SauceLabs_Password");
		lockedOutUser = ConfigReader.getProperty("SauceLabs_lockedUser");
		invalidUsername = ConfigReader.getProperty("SauceLabs_invalidUserName");
		invalidPassword = ConfigReader.getProperty("SauceLabs_invalidPassword");

		// Initialize page objects using driver instance managed by BaseTest
		loginPage = new LoginPage(DriverManager.getRawDriver());
		comman = new CommanLogics(DriverManager.getRawDriver());

		// Initialize page objects once for the entire class
		loginPage = new LoginPage(DriverManager.getRawDriver());
		comman = new CommanLogics(DriverManager.getRawDriver());
	}

	@Test(priority = 1, description = "Verify error message when logging in with empty fields")
	public void testLoginWithEmptyFields() {
		loginPage.clickOnOpenMenu();
		loginPage.navigateToLoginPage();
		loginPage.clickLoginButton();

		String errorMessage = loginPage.getErrorMessageTextForUserName();
		Assert.assertEquals(errorMessage, ConfigReader.getProperty("errorMessageUserMissing"),
				"Correct error message should be displayed for empty inputs.");

		ExtentManager.getTest().log(Status.PASS, "Verified empty field validation error.: "
				+ MarkupHelper.createLabel(errorMessage, ExtentColor.RED).getMarkup());

	}

	@Test(priority = 2, description = "Verify error message when password is empty")
	public void testLoginWithEmptyPassword() {

		// Fill only username
		loginPage.enterCredentials(validUsername, "");
		loginPage.clickLoginButton();

		String errorMessage = loginPage.getErrorMessageTextForPassword();
		Assert.assertEquals(errorMessage, ConfigReader.getProperty("errorMessagePasswordRequired"),
				"Validation error should prompt for required password.");

		ExtentManager.getTest().log(Status.PASS, "Verified the Password field  error.: "
				+ MarkupHelper.createLabel(errorMessage, ExtentColor.RED).getMarkup());
	}

	@Test(priority = 3, description = "Verify error message with invalid credentials")
	public void testLoginWithInvalidCredentials() {

		// Enter non-existing credentials
		loginPage.enterCredentials(invalidUsername, invalidPassword);
		loginPage.clickLoginButton();

		String errorMessage = loginPage.getErrorMessageTextForInvalidUser();
		Assert.assertEquals(errorMessage, ConfigReader.getProperty("invalidCredentialsMessage"),
				"Validation error should inform user of invalid credentials.");

		ExtentManager.getTest().log(Status.PASS, "Verified empty both the UserName and Password are Invalid error.: "
				+ MarkupHelper.createLabel(errorMessage, ExtentColor.RED).getMarkup());
	}

	@Test(priority = 4, description = "Verify error message for locked-out user account")
	public void testLoginWithLockedOutUser() {

		// Attempt login with locked-out account
		loginPage.enterCredentials(lockedOutUser, validPassword);
		loginPage.clickLoginButton();

		String errorMessage = loginPage.getErrorMessageTextForLockedOutUser();
		Assert.assertEquals(errorMessage, ConfigReader.getProperty("lockedOutUserMessage"),
				"Validation error should alert user that the account is locked.");
		
		ExtentManager.getTest().log(Status.PASS, "Verified The User account is locked error.: "
				+ MarkupHelper.createLabel(errorMessage, ExtentColor.RED).getMarkup());
		ExtentManager.getTest().log(Status.PASS, "Verified locked-out user handling.");
	}

	@Test(priority = 5, description = "Verify successful login with valid credentials")
	public void testSuccessfulLoginWithValidCredentials() {
		// Continues in the same app session on the Login screen
		loginPage.enterCredentials(validUsername, validPassword);
		loginPage.clickLoginButton();

		String isLoggedIn = loginPage.isUserLoggedIn();
		Assert.assertEquals(isLoggedIn, ConfigReader.getProperty("ProductLabel"),
				"User should be redirected to the product catalog after successful login.");

		ExtentManager.getTest().log(Status.PASS, "Successfully logged in with valid user: "
				+ MarkupHelper.createLabel(validUsername, ExtentColor.GREEN).getMarkup());

		// Perform Logout workflow
		loginPage.clickOnOpenMenu();
		loginPage.LogOut();
		loginPage.getAlertTitleText();
		loginPage.getAlertMessageText();
		loginPage.assertAndConfirmLogout();
		loginPage.getLogoutSuccessAlertTitleText();
		loginPage.clickOnOkBtn();

		String loginTitle = loginPage.verifyLoginPageTitle();
		assertEquals(loginTitle, ConfigReader.getProperty("LoginTitle"));
	}
}