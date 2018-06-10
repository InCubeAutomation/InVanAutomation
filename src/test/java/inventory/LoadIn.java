package inventory;

import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.touch.TouchActions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;
import shared_functions.SharedFunctions;
import testng_config_methods.TestNGConfig;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class LoadIn extends TestNGConfig {
    SharedFunctions sharedFunctions = new SharedFunctions();
    By itemsListviewLocator = By.id("lv_stock_list");
    By itemBoxLocator = By.id("ll_item_selector_bg");
    By itemTextLocator = By.id("txt_item_name");
    By itemCurrerntQtyLocator = By.id("txt_actual_qty");
    By itemOrigninalQtyLocator = By.id("txt_qty_value");
    int noOfItems = 6;
    int packsToCount = 57;
    int packsCounted=0;
    List<String> moe = new ArrayList<String>();

    @Test
    public void loadIn(){
        sharedFunctions.enterScreen("Inventory");
        sharedFunctions.getMenuName("Inventory Menu");
        sharedFunctions.enterScreen("Load In");
        sharedFunctions.getMenuName("Load In");
        TouchAction touchAction = new TouchAction(driver);


        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.
                        visibilityOfElementLocated(itemsListviewLocator));
        WebElement listView = driver.findElement(itemsListviewLocator);
        int pressX = driver.manage().window().getSize().width/2;
        int topY = listView.getLocation().getY();
        int bottomY = topY + listView.getSize().height;

        while(packsToCount>packsCounted) {
            List<MobileElement> itemBox = driver.findElements(itemBoxLocator);
            for(int i = 0;i<itemBox.size();i++){
           if(!sharedFunctions.subelementExistsInElement(itemTextLocator,itemBox.get(i)) || !sharedFunctions.subelementExistsInElement(itemCurrerntQtyLocator,itemBox.get(i)))
           {
               touchAction.press(PointOption.point(pressX,bottomY-1)).waitAction(new WaitOptions().withDuration(Duration.ofMillis(500))).moveTo(PointOption.point(pressX,bottomY-16)).release().perform();

           } else {
               String itemText = itemBox.get(i).findElement(itemTextLocator).getText();
               String itemCode = itemText.split("-")[0];
               String itemName = itemText.split("-")[1];
               String itemUOM = itemText.split("-")[2];
//             String itemOriginalQty = itemBox.get(i).findElement(itemOrigninalQtyLocator).getText();
               String itemCurrentQty = itemBox.get(i).findElement(itemCurrerntQtyLocator).getText();

//               System.out.println(itemCode + "__" + itemName + "__" + itemUOM + "__" + itemCurrentQty);
               packsCounted++;
               moe.add(itemCode + "__" + itemName + "__" + itemUOM + "__" + itemCurrentQty);
           }
           }

               touchAction.press(PointOption.point(pressX,bottomY-1)).waitAction(new WaitOptions().withDuration(Duration.ofMillis(500))).moveTo(PointOption.point(pressX,topY-1)).release().perform();

        }
System.out.println("MHND");

    }
}
