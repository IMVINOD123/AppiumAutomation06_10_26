package tests;

import Pages.ViewsPage;
import Utils.ConfigReader;
import Utils.DriverManager;
import Utils.ExtentManager;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import BaseTest.BaseTest;
import Comman.CommanLogics;

public class VeiwTest extends BaseTest {

	@Test
	public void testMonitoredSwitchToast() throws InterruptedException {
		ViewsPage viewsPage = new ViewsPage(DriverManager.getRawDriver());
		CommanLogics comman=new CommanLogics(DriverManager.getRawDriver());

		// Perform page action
		// viewsPage.toggleMonitoredSwitch();

		// Capture and assert validation
		/*
		 * String actualToastText = viewsPage.getToastText();
		 * System.out.println("Captured Toast: " + actualToastText);
		 */

		/*
		 * Assert.assertTrue(actualToastText.contains("Monitored switch"),
		 * "Toast text validation failed!");
		 */
		// Scroll and Navigate to Views
		comman.scrollTo(ConfigReader.getProperty("viewLabel"));
		ExtentManager.getTest().log(Status.INFO, "Scrolled Till View Locator");
		//System.out.println("***************Scrolled Till View***************");

		// Scroll and Navigate to Switches
		viewsPage.scrollToAndClickSwitches(ConfigReader.getProperty("switchesLabel"));
		ExtentManager.getTest().log(Status.INFO, "Scrolled Till Switche Locator");
		//System.out.println("***************Scrolled Till switch***************");

		// Interact with Standard Switch
		viewsPage.clickStandardSwitch();

		// Validate Default On Text
		String actualName = viewsPage.getDefaultOnSwitchText();
		//System.out.println(actualName);
		String expectedName="Default is on";
		if (expectedName.equals(actualName)) {
			ExtentManager.getTest().pass("<b>Profile Verification:</b> Matched successfully (" + actualName + ").");
		} else {
			ExtentManager.getTest().fail("<b>Profile Verification:</b> Expected " + expectedName + " but found " + actualName);;
		}

		// Toggle and Validate Monitored Switch
		if (viewsPage.isMonitoredSwitchSelected()) {
			ExtentManager.getTest().log(Status.INFO, "Switched on");			//System.out.println("Pass \n***************SuccessFully Switched On******************");
		} else {
			ExtentManager.getTest().log(Status.INFO, "Switched Off");
			viewsPage.clickMonitoredSwitch();

			Thread.sleep(6000); // Recommendation: Replace with explicit wait if checking state updates

			if (viewsPage.isMonitoredSwitchSelected()) {
				ExtentManager.getTest().log(Status.INFO, "Switched off");//System.out.println("Pass \n***************SuccessFully Switched off******************");
			} else {
				ExtentManager.getTest().log(Status.INFO, "Switched on");
				viewsPage.clickMonitoredSwitch();
			}
		}

	}
}