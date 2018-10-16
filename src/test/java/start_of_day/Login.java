package start_of_day;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.*;
import shared_functions.SharedFunctions;
import sqlite_access.RouteGeneralData;
import testng_config_methods.TestNGConfig;

import java.io.IOException;
import java.sql.SQLException;


public class Login extends TestNGConfig {
    By usernameLocator = By.id("email");
    By passwordLocator = By.id("password");
    By loggedInNameLocator = By.id("employeename");
    By loginMessageLocator = By.id("message");
    By loginButtonLocator = By.id("email_sign_in_button");
    SharedFunctions sharedFunctions = new SharedFunctions();
    @Test
    public void login() throws IOException, SQLException {

      // By MainMenuText = By.xpath("/MainMenuActivity/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.view.ViewGroup/android.widget.TextView.[@text='Add']");
        wait.until(ExpectedConditions.visibilityOfElementLocated(loggedInNameLocator));
        RouteGeneralData.getGeneralData();
        if(RouteGeneralData.Uploaded) {
            Assert.assertEquals(driver.findElement(loggedInNameLocator).getText().trim(), RouteGeneralData.EmployeeName.trim(), "Wrong User");
        }
        driver.findElement(usernameLocator).sendKeys("1");
        driver.findElement(passwordLocator).sendKeys("1");
        if(driver.isKeyboardShown()) {
            driver.hideKeyboard();
        }
        driver.findElement(loginButtonLocator).click();
        if(!RouteGeneralData.Uploaded) {
            RouteGeneralData.getGeneralData();
        }
        sharedFunctions.checkMenuName("Main Menu");

    }

}

