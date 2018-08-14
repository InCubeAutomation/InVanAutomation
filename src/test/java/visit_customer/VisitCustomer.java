package visit_customer;

import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.android.AndroidKeyCode;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import shared_functions.SharedFunctions;
import sqlite_access.Accounts;
import testng_config_methods.TestNGConfig;

import java.io.IOException;
import java.sql.SQLException;

public class VisitCustomer extends TestNGConfig {
    SharedFunctions sharedFunctions = new SharedFunctions();
    By visitCustomerMenuLocator = By.id("menu_image_1");
    By routeOutRouteLocator = By.id("text1");
    By searchButtonLocator = By.id("search_button") ;
    By customerCodeLocator = By.id("tv_cust_code");
    By outletNameLocator = By.id("tv_cust_outlet");
    By checkInButtonLocator = By.id("btn_golden_continue");
    By InVanAppSwitchLocator = By.xpath("//android.widget.TextView[@content-desc=\"InVan In Van\"]");

    Accounts accounts = new Accounts();
    Double creditLimit=0D;
    Double balance=0D;

    @Parameters({"outletCode","outletName"})
    @Test
public void selectCustomer(String outletCode, String outletName) throws IOException {
        sharedFunctions.enterScreen("Visit Customer");
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.
                        visibilityOfElementLocated(searchButtonLocator));

        try {
            Double[] accountData = accounts.outletBalanceAndCredit(outletCode);
            creditLimit = accountData[0];
            balance = accountData[1];
        } catch (SQLException e) {
            e.printStackTrace();
        }

        driver.findElement(searchButtonLocator).click();
        driver.getKeyboard().sendKeys(outletCode);
        driver.hideKeyboard();
        AndroidElement customerListViewLocator = (AndroidElement) driver.findElement(MobileBy.id("lv_customerlist"));
                MobileElement outletLocator = customerListViewLocator.findElementByAndroidUIAutomator("new UiScrollable(new UiSelector()"
                + ".resourceId(\"com.incube.invan:id/" + outletNameLocator +
                "\")).scrollIntoView(new UiSelector().text(\"" + outletName + "\"));" );
        outletLocator.click();
        if (AndroidVersion.equals("7.1.1") || AndroidVersion.equals("7.0") || AndroidVersion.equals("8.0") ) {
            driver.pressKeyCode(AndroidKeyCode.KEYCODE_APP_SWITCH);
            (new WebDriverWait(driver,10))
                    .until(ExpectedConditions.
                            visibilityOfElementLocated(InVanAppSwitchLocator));
            driver.findElement(InVanAppSwitchLocator).click();


        }
    }

    @Test
    public void goldenMinute() {
// UNABLE TO DETECT GOLDEN MINUTE LOCATOR (NO DEFINED ID)
                    /*
            ASSERTIONS TO BE ADDED LATER
            ASSERTIONS TO BE ADDED LATER
            SOA IMPLEMENTATION TO BE ADDED LATER ALSO
            */
                wait.until(ExpectedConditions.visibilityOfElementLocated(checkInButtonLocator));
        driver.findElement(checkInButtonLocator).click();
        sharedFunctions.getMenuName("Customer Menu");
    }



}