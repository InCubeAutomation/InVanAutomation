package shared_functions;

import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidElement;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import testng_config_methods.TestNGConfig;

public class DeleteItem extends TestNGConfig {

    SharedFunctions sharedFunctions = new SharedFunctions();

    By searchBarLocator = By.id("sv_search");
    By itemQtyLocator = By.id("txt_addItem_Qty");
    By itemCodeValueLocator = By.id("itemCodeTextView");
    By uomValueLocator = By.id("uomTextView");
    By deleteItemButtonLocator = By.id("btn_deleteItem");


    public void deleteItem(String itemCode, String packType){
        driver.findElement(searchBarLocator).click();
        driver.findElement(searchBarLocator).sendKeys(itemCode);
        driver.hideKeyboard();
        AndroidElement addItemListViewLocator = (AndroidElement) driver.findElement(MobileBy.id("lstv_add_item_dialog"));
        MobileElement packLocator = addItemListViewLocator.findElementByAndroidUIAutomator("new UiScrollable(new UiSelector()"
                + ".resourceId(\"com.incube.invan:id/" + uomValueLocator +
                "\")).scrollIntoView(new UiSelector().text(\"" + packType + "\"));"
        );

        packLocator.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(itemQtyLocator));
        if(driver.isKeyboardShown()) {
            driver.hideKeyboard();
        }
        if(!sharedFunctions.elementExists(deleteItemButtonLocator)) {
            sharedFunctions.scrollUp();
        }
        driver.findElement(deleteItemButtonLocator).click();

    }
}
