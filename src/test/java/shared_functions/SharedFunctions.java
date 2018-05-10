package shared_functions;

import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import testng_config_methods.TestNGConfig;

import java.time.Duration;

public class SharedFunctions extends TestNGConfig {

    public void enterStorekeeperPassword(){
    String inventoryOperationsPassword = "123";
    By passwordLocator = By.id("txt_key");
    By enterStorekeeperPasswordTextLocator = By.id("lblConfirmationMsg");
    By okKey = By.id("btn_key_ok");

            (new WebDriverWait(driver, 10))
            .until(ExpectedConditions.
                           visibilityOfElementLocated(passwordLocator));
        softAssert.assertTrue(driver.findElement(enterStorekeeperPasswordTextLocator).getText().equals("Enter Store Keeper Password:"),"Wrong Message");
        driver.findElement(passwordLocator).sendKeys(inventoryOperationsPassword);
        driver.findElement(okKey).click();
    }


    public void getMenuName(String menuName){
        By menuNameTextLocator = By.className("android.widget.TextView");
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.
                        visibilityOfElementLocated(menuNameTextLocator));

        Assert.assertEquals(driver.findElement(menuNameTextLocator).getText(), menuName, "Wrong Menu");
    }


    public void scrollDown(){
        int pressX = driver.manage().window().getSize().width / 2;
        // 4/5 of the screen as the bottom finger-press point
         int bottomY = driver.manage().window().getSize().height * 4/5;
        // just non zero point, as it didn't scroll to zero normally
          int topY = driver.manage().window().getSize().height  * 4/33 ;
//        touchAction.longPress(pressX,bottomY).moveTo(pressX,topY).perform();
        TouchAction touchAction = new TouchAction(driver);
        touchAction.longPress(PointOption.point(pressX,bottomY)).moveTo(PointOption.point(0,topY-bottomY)).
                waitAction(new WaitOptions().withDuration(Duration.ofSeconds(1))).release().perform();
    }
    
}
