package inventory;

import io.appium.java_client.android.AndroidElement;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;
import shared_functions.SharedFunctions;
import testng_config_methods.TestNGConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LoadOut extends TestNGConfig {

    SharedFunctions sharedFunctions = new SharedFunctions();
    By inventoryMenuLocator = By.id("menu_image_2");
    By loadOutMenuLocator = By.id("menu_image_1");
    By listViewLocator = By.id("lv_stock_list");
    By itemCardLocator = By.id("ll_item_selector_bg");
    By itemCodeLocator = By.id("txt_stock_code");
    By itemUOMLocator = By.id("txt_stock_uom");
    By itemQtyLocator = By.id("txt_stock_qty");
    By saveButtonLocator = By.id("action_save");
    By yesSaveLocator = By.id("button1");
    String[] expectedPackandQtyArray = {"4001 - Outer = 2","4001 - Carton = 11","4002 - Carton = 1","4002 - Carton = 9"};
    List<String> expectedPackandQty  = Arrays.asList(expectedPackandQtyArray);

    List<AndroidElement> itemCardLocators =  new ArrayList<>();
    List<String> actualPackandQty =  new ArrayList<>();
    List<String> itemQty =  new ArrayList<>();
    List<String> itemUOMXpath =  new ArrayList<>();
    List<String> itemQtyXpath =  new ArrayList<>();

    @Test
    public void loadOut(){
        sharedFunctions.enterScreen("Inventory");
        sharedFunctions.getMenuName("Inventory Menu");
        sharedFunctions.enterScreen("Load Out");
        sharedFunctions.enterStorekeeperPassword();
        sharedFunctions.getMenuName("Load Out");
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.
                        visibilityOfElementLocated(listViewLocator));

        itemCardLocators = driver.findElements(itemCardLocator);
        for (int i =0;i <4;i++) {
            actualPackandQty.add(itemCardLocators.get(i).findElement(itemCodeLocator).getText() + " - " +
                    itemCardLocators.get(i).findElement(itemUOMLocator).getText() + " = " +
          itemCardLocators.get(i).findElement(itemQtyLocator).getText());
        }

        Collections.sort(actualPackandQty);
        Collections.sort(expectedPackandQty);
        softAssert.assertEquals(actualPackandQty,expectedPackandQty);

        // To handle later :  remove duplicates
        // check addAll method ?! replace it with more proper method
/*
        while(itemCode.size() < 57)
        {
            itemCardLocators = driver.findElements(itemCardLocator);
            for (int i =0;i <itemCardLocators.size();i++) {
                // Check that all 3 elements are available within the Card Check is currently wrong.. to handle later
                if (itemCardLocators.get(i).findElement(itemCodeLocator).isDisplayed()
                        && itemCardLocators.get(i).findElement(itemUOMLocator).isDisplayed()
                        && itemCardLocators.get(i).findElement(itemQtyLocator).isDisplayed()) {
                    itemCode.add(itemCardLocators.get(i).findElement(itemCodeLocator).getText());
                    itemUOM.add(itemCardLocators.get(i).findElement(itemUOMLocator).getText());
                    itemQty.add(itemCardLocators.get(i).findElement(itemQtyLocator).getText());
                }
            }
            sharedFunctions.scrollDown();
        }
*/
        driver.findElement(saveButtonLocator).click();
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.
                        visibilityOfElementLocated(yesSaveLocator));
        driver.findElement(yesSaveLocator).click();
        sharedFunctions.getMenuName("Inventory Menu");
        driver.navigate().back();
        sharedFunctions.getMenuName("Main Menu");
        softAssert.assertAll();
    }
}
