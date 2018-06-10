package start_of_day;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;
import shared_functions.SharedFunctions;
import testng_config_methods.TestNGConfig;


public class Login extends TestNGConfig {
    By usernameLocator = By.id("email");
    By passwordLocator = By.id("password");
    By loggedInNameLocator = By.id("employeename");
    By loginMessageLocator = By.id("message");
    By loginButtonLocator = By.id("email_sign_in_button");
    SharedFunctions sharedFunctions = new SharedFunctions();
    @Test
    public void login()  {


      // By MainMenuText = By.xpath("/MainMenuActivity/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.view.ViewGroup/android.widget.TextView.[@text='Add']");

        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.
                        visibilityOfElementLocated(loggedInNameLocator));
        System.out.println(driver.findElement(loggedInNameLocator).getText());
        Assert.assertEquals(driver.findElement(loggedInNameLocator).getText(), "\"M\" Mohammad Tasawar", "Wrong User");
        driver.findElement(usernameLocator).sendKeys("1");
        driver.findElement(passwordLocator).sendKeys("1");
        if(driver.isKeyboardShown()) {
            driver.hideKeyboard();
        }
        driver.findElement(loginButtonLocator).click();
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.
                        invisibilityOfElementLocated(loginMessageLocator));
        sharedFunctions.getMenuName("Main Menu");

    }

}

