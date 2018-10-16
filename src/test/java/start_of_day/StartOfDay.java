package start_of_day;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import shared_functions.SharedFunctions;
import testng_config_methods.TestNGConfig;

import java.util.ArrayList;
import java.util.List;

public class StartOfDay extends TestNGConfig  {


    By inspectionMenuTextLocator = By.className("android.widget.TextView");
    By vanCodeLocator = By.id("tv_van_code");
    By employeeNameLocator = By.id("tv_employee_name");
    By employeeCodeLocator = By.id("tv_employee_code");
    By routeCodeLocator = By.id("tv_route_code");
    By routeNameLocator = By.id("tv_route_name");
    By startButtonLocator = By.id("btn_start_day_Ok");
    By sodMenuLocator = By.id("menu_image_2");
    By sodMenuTextLocator = By.className("android.widget.TextView");
    By odometerTextLocator = By.id("txt_odometer");
    By lightLocator = By.id("Lightswitch");
    By breaksLocator = By.id("Breaksswitch");
    By radiatorLocator = By.id("Radiatorswitch");
    By bodyWorkLocator = By.id("bodyworkswitch");
  //  By tabsLocator = By.className("android.support.v7.app.ActionBar$Tab");
    By defectsTabLocator = By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.RelativeLayout/android.widget.HorizontalScrollView/android.widget.LinearLayout/android.support.v7.app.ActionBar.Tab[2]");
    By imagesTabLocator = By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.RelativeLayout/android.widget.HorizontalScrollView/android.widget.LinearLayout/android.support.v7.app.ActionBar.Tab[3]");
    By majorDefectTextLocator =  By.id("majordefect");
    By addMajorDefectButtonLocator  = By.id("btn_add_majorDefect");
    By minorDefectTextLocator = By.id("minorDefect");
    By addMinorDefectButtonLocator = By.id("btn_add_minorDefect");
    By saveButtonLocator = By.id("action_save");

    List<String> majorDefectXpath =  new ArrayList<>();
    List<String> majorDefectDeleteXpath =  new ArrayList<>();

    List<String> minorDefectXpath =  new ArrayList<>();
    List<String> minorDefectDeleteXpath =  new ArrayList<>();
    SharedFunctions sharedFunctions = new SharedFunctions();

    @Test
    public void startOfDay() {
        sharedFunctions.enterScreen("Start of day");
        sharedFunctions.checkMenuName("Start of day");
        if(sharedFunctions.elementExists(odometerTextLocator)){
            String focused =  driver.findElement(odometerTextLocator).getAttribute("focused");
            if(!focused.equals("true")){
                driver.findElement(odometerTextLocator).click();
            }
            driver.findElement(odometerTextLocator).sendKeys("999999999");

            driver.hideKeyboard();
        }
        softAssert.assertEquals(driver.findElement(vanCodeLocator).getText(),"HA05", "Wrong Van Code");
        softAssert.assertEquals(driver.findElement(employeeNameLocator).getText(), "\"M\" Mohammad Tasawar", "Wrong Employee Name");
        softAssert.assertEquals(driver.findElement(employeeCodeLocator).getText(), "VS004", "Wrong Employee Code");
        softAssert.assertEquals(driver.findElement(routeCodeLocator).getText(), "SHJ006-TWT", "Wrong Route Code");
        softAssert.assertEquals(driver.findElement(routeNameLocator).getText(), "SHJ006-TWT", "Wrong Route Name");
        driver.findElement(startButtonLocator).click();
        softAssert.assertAll();
    }

    @Test
    public void inspection() {
        sharedFunctions.checkMenuName("Van Inspection");
        String focused =  driver.findElement(odometerTextLocator).getAttribute("focused");
        softAssert.assertEquals(focused,"true","Not focused");
        if(!focused.equals("true")){
            driver.findElement(odometerTextLocator).click();
        }
            driver.findElement(odometerTextLocator).sendKeys("999999999");

        driver.hideKeyboard();
        driver.findElement(lightLocator).click();
        driver.findElement(breaksLocator).click();
        driver.findElement(radiatorLocator).click();
        driver.findElement(radiatorLocator).click();
        driver.findElement(bodyWorkLocator).click();
        softAssert.assertTrue(driver.findElement(lightLocator).getAttribute("checked").equals("true"),"wrong Light status");
        softAssert.assertTrue(driver.findElement(radiatorLocator).getAttribute("checked") .equals("false") ,"wrong Radiator status");
  //      List<WebElement> views = driver.findElements(tabsLocator);

        driver.findElement(defectsTabLocator).click();

        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.
                        visibilityOfElementLocated(majorDefectTextLocator));

        // The Next code is to handle xpath locator for each new added defect and its assigned delete button

        int noOfMajorDefects = 3;
        int noOfMinorDefects = 1;

        for(int i = 0; i < noOfMajorDefects;i++ ) {
            majorDefectXpath.add("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.RelativeLayout/android.support.v4.view.ViewPager/android.widget.LinearLayout/android.widget.FrameLayout[1]/android.widget.LinearLayout/android.widget.ExpandableListView/android.widget.LinearLayout["
                    +Integer.toString(i+2)+"]/android.widget.FrameLayout/android.widget.RelativeLayout/android.widget.TextView");
            majorDefectDeleteXpath.add("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.RelativeLayout/android.support.v4.view.ViewPager/android.widget.LinearLayout/android.widget.FrameLayout[1]/android.widget.LinearLayout/android.widget.ExpandableListView/android.widget.LinearLayout["
                    +Integer.toString(i+2)+"]/android.widget.FrameLayout/android.widget.RelativeLayout/android.widget.ImageView[2]");
        }
        for(int i = 0; i < noOfMinorDefects;i++ ) {
            minorDefectXpath.add("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.RelativeLayout/android.support.v4.view.ViewPager/android.widget.LinearLayout/android.widget.FrameLayout[2]/android.widget.LinearLayout/android.widget.ExpandableListView/android.widget.LinearLayout["
                    +Integer.toString(i+2)+"]/android.widget.FrameLayout/android.widget.RelativeLayout/android.widget.TextView");
            minorDefectDeleteXpath.add("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.RelativeLayout/android.support.v4.view.ViewPager/android.widget.LinearLayout/android.widget.FrameLayout[2]/android.widget.LinearLayout/android.widget.ExpandableListView/android.widget.LinearLayout["
                    +Integer.toString(i+2)+"]/android.widget.FrameLayout/android.widget.RelativeLayout/android.widget.ImageView[2]");
        }
        // The Previous code is to handle xpath locator for each new added defect and its assigned delete button

        driver.findElement(majorDefectTextLocator).sendKeys("Def1");
        driver.findElement(addMajorDefectButtonLocator).click();
        driver.findElement(majorDefectTextLocator).sendKeys("Def2");
        driver.findElement(addMajorDefectButtonLocator).click();
        driver.findElement(majorDefectTextLocator).sendKeys("Def3");
        driver.findElement(addMajorDefectButtonLocator).click();
        driver.hideKeyboard();
        driver.findElement(By.xpath(majorDefectDeleteXpath.get(1))).click();
        noOfMajorDefects--;
        Assert.assertEquals(
                driver.findElement(By.xpath(majorDefectXpath.get(1))).getText(),
                     "Def3","Def 3 isn't available");
        driver.findElement(saveButtonLocator).click();
        softAssert.assertAll();

    }
}
