package end_of_day;

import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidKeyCode;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import read_data_excel.ReadData;
import shared_functions.SharedFunctions;
import testng_config_methods.TestNGConfig;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EndOfDay extends TestNGConfig {
SharedFunctions sharedFunctions = new SharedFunctions();
By denominationsListviewLocator = By.id("SettlementList");
By denominationBoxLocator = By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.support.v4.view.ViewPager/android.widget.LinearLayout/android.widget.ListView/android.widget.FrameLayout/android.widget.LinearLayout");
By denominationNameLocator = By.id("denomination");
By denominationAmountLocator = By.id("amount");
By amountPopupLocator = By.id("txt_PaidAmount");
By totalCashLocator = By.id("tv_cash_total");
By totalChequeLocator = By.id("tv_credit_total");
By popupOkLocator = By.id("button1");
By settlementButtonLocator = By.id("btn_settle");
By saveButtonLocator = By.id("action_save");
By alertPopup = By.id("parentPanel");
By alertMessage = By.id("message");
By alertOkLocator = By.id("button2");
Float expectedTotalCash ;
Float expectedTotalCheque ;
List<List<String>> settlementData = new ArrayList<List<String>>();
HashMap<String,String> denominationFillData = new HashMap<String,String>();
int numberOfDenominationsToBeFilled;
int numberOfDenominationsFilled;

ReadData readData = new ReadData();

@Parameters({"filePath"})
@Test
    public void endOfDay(String filePath){
        settlementData = readData.readData(filePath,"Settlement");
        numberOfDenominationsToBeFilled = settlementData.size() - 2;
        for(int i=0;i<numberOfDenominationsToBeFilled;i++){
            denominationFillData.put(settlementData.get(i).get(0),settlementData.get(i).get(1));
        }

        expectedTotalCash = Float.valueOf(settlementData.get(numberOfDenominationsToBeFilled).get(1));
        expectedTotalCheque = Float.valueOf(settlementData.get(numberOfDenominationsToBeFilled+1).get(1));
        sharedFunctions.enterScreen("End of day");
        sharedFunctions.getMenuName("End Of Day");
        Float actualTotalCash = Float.valueOf(driver.findElement(totalCashLocator).getText().replace(",",""));
        Assert.assertEquals(actualTotalCash,expectedTotalCash,"Wrong Total Cash");

        Float actualTotalCheque = Float.valueOf(driver.findElement(totalChequeLocator).getText().replace(",",""));
        Assert.assertEquals(actualTotalCheque,expectedTotalCheque,"Wrong Total Cheque");

    WebElement listView = driver.findElement(denominationsListviewLocator);
    int pressX = driver.manage().window().getSize().width/2;
    int topY = listView.getLocation().getY();
    int bottomY = topY + listView.getSize().height;
    while(numberOfDenominationsToBeFilled>numberOfDenominationsFilled) {
        List<MobileElement> denominationBox = driver.findElements(denominationBoxLocator);
        for(int i = 0;i<denominationBox.size();i++) {
            if(sharedFunctions.subelementExistsInElement(denominationNameLocator,denominationBox.get(i))){
                String denominationName = denominationBox.get(i).findElement(denominationNameLocator).getText();
            if (denominationFillData.containsKey(denominationName)) {

                denominationBox.get(i).click();
                String focusable = driver.findElement(amountPopupLocator).getAttribute("focused");
                if (focusable.equals("true")) {
                    driver.pressKeyCode(AndroidKeyCode.KEYCODE_DEL);
                    driver.getKeyboard().sendKeys(denominationFillData.get(denominationName));
                } else {
                    driver.findElement(amountPopupLocator).click();
                    driver.pressKeyCode(AndroidKeyCode.KEYCODE_DEL);
                    driver.getKeyboard().sendKeys(denominationFillData.get(denominationName));
                }
                driver.findElement(popupOkLocator).click();
                denominationFillData.remove(denominationName);
                numberOfDenominationsFilled++;
                int pressX2 = denominationBox.get(i).getCenter().getX();
                int bottomY2 = denominationBox.get(i).getCenter().getY();
                TouchAction touchAction = new TouchAction(driver);

                touchAction.longPress(PointOption.point(pressX2,bottomY2)).waitAction(new WaitOptions().withDuration(Duration.ofMillis(400))).moveTo(PointOption.point(0,-20)).release().perform();

            }
        }
        }
        TouchAction touchAction = new TouchAction(driver);
        touchAction.longPress(PointOption.point(pressX,bottomY-1)).waitAction(new WaitOptions().withDuration(Duration.ofMillis(400))).moveTo(PointOption.point(0,topY-bottomY)).release().perform();
        System.out.println("MHND");

    }
            driver.findElement(settlementButtonLocator).click();
            driver.findElement(saveButtonLocator).click();
            driver.findElement(popupOkLocator).click();
            sharedFunctions.getMenuName("EOD Reports");
            driver.navigate().back();
            if(sharedFunctions.elementExists(alertPopup)){
                if (driver.findElement(alertPopup).findElement(alertMessage).getText().equals("Route data should be downloaded before proceeding.")){
                    driver.findElement(alertOkLocator).click();
                    if (AndroidVersion.equals("7.1.1") || AndroidVersion.equals("7.0") || AndroidVersion.equals("8.0")){
                        driver.pressKeyCode(AndroidKeyCode.KEYCODE_APP_SWITCH);
                        driver.findElementByAccessibilityId("InVan In Van").click();
                    }
                }
            }
            sharedFunctions.getMenuName("Main Menu");
        System.out.println("MHND");

    }
}