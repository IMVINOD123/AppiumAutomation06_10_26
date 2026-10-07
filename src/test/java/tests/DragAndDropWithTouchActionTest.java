package tests;

import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;

import BaseTest.BaseTest;
import Comman.CommanLogics;
import Pages.DragAndDropByTouchActionPage;
import Utils.ConfigReader;
import Utils.DriverManager;
import Utils.ExtentManager;

public class DragAndDropWithTouchActionTest extends BaseTest {

	@Test
	public void testDragAndDropUisngTouchActions() throws InterruptedException {
		DragAndDropByTouchActionPage dragAndDrop = new DragAndDropByTouchActionPage(DriverManager.getRawDriver());
		CommanLogics comman=new CommanLogics(DriverManager.getRawDriver());

		comman.scrollTo(ConfigReader.getProperty("viewLabel"));
		ExtentManager.getTest().log(Status.INFO, "Scrolled Till View Locator");

		dragAndDrop.dragAndDropLabel();
		ExtentManager.getTest().log(Status.INFO, "Clicked on Drop Down Locator");
		// System.out.println("***************Clicked On Drop Down Label or
		// Button***************");

		dragAndDrop.W3CDragAndDrop();
		ExtentManager.getTest().log(Status.INFO, "Performed Drag and Drop Using W3C Actions");
		// System.out.println("***************Performed Drag and Drop Using W3C Actions
		// ***************");

		String text = dragAndDrop.verifyDragAndDrop();
		assertEquals(text, "Dropped!");
	}
}