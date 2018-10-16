package visit_customer;

import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidElement;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import shared_functions.SharedFunctions;
import testng_config_methods.TestNGConfig;

import java.io.IOException;

public class VisitCustomer extends TestNGConfig {
    SharedFunctions sharedFunctions = new SharedFunctions();
    By visitCustomerMenuLocator = By.id("menu_image_1");
    By routeOutRouteLocator = By.id("text1");
    By searchButtonLocator = By.id("search_button") ;
    By searchTextBoxLocator = By.id("search_src_text") ;
    By customerCodeLocator = By.id("tv_cust_code");
    By outletNameLocator = By.id("tv_cust_outlet");
    By checkInButtonLocator = By.id("btn_golden_continue");


    @Parameters({"outletCode","outletName"})
    @Test
public void selectCustomer(String outletCode, String outletName) throws IOException {

        sharedFunctions.enterScreen("Visit Customer");
        wait.until(ExpectedConditions.visibilityOfElementLocated(searchButtonLocator));
        driver.findElement(searchButtonLocator).click();
        driver.findElement(searchTextBoxLocator).sendKeys(outletCode);
        driver.hideKeyboard();
        AndroidElement customerListViewLocator = (AndroidElement) driver.findElement(MobileBy.id("lv_customerlist"));
                MobileElement outletLocator = customerListViewLocator.findElementByAndroidUIAutomator("new UiScrollable(new UiSelector()"
                + ".resourceId(\"com.incube.invan:id/" + outletNameLocator +
                "\")).scrollIntoView(new UiSelector().text(\"" + outletName + "\"));" );
        outletLocator.click();
        if (AndroidVersion.equals("7.1.1") || AndroidVersion.equals("7.0") || AndroidVersion.equals("8.0") ) {
            sharedFunctions.skipAppiumActivityOverActivityHanging(300);
        }
    }

    @Test
    public void goldenMinute() {
// UNABLE TO DETECT GOLDEN MINUTE LOCATOR (NO DEFINED ID)
        //TODO
                    /*
            ASSERTIONS TO BE ADDED LATER
            ASSERTIONS TO BE ADDED LATER
            SOA IMPLEMENTATION TO BE ADDED LATER ALSO
            */
        wait.until(ExpectedConditions.visibilityOfElementLocated(checkInButtonLocator));
        driver.findElement(checkInButtonLocator).click();
        sharedFunctions.checkMenuName("Customer Menu");
    }



}