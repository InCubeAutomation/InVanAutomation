package shared_functions;

import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.android.AndroidKeyCode;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import testng_config_methods.TestNGConfig;

public class AddItem extends TestNGConfig {

    SharedFunctions sharedFunctions = new SharedFunctions();
    public static float addItem(String itemCode, String packType, String qty ){
        By searchBarLocator = By.id("sv_search");
        By itemCodeValueLocator = By.id("itemCodeTextView");
        By uomValueLocator = By.id("uomTextView");
        By itemQtyLocator = By.id("txt_addItem_Qty");
        By saveItemButtonLocator = By.id("btn_addItem_save");
        By itemPriceLocator = By.id("txt_addItem_fullPrice");
        By itemDiscountLocator = By.id("txt_addItem_disc");
        By itemDiscountTypeLocator = By.id("txt_addItem_disctype");
        By itemTaxLocator = By.id("txt_addItem_tax") ;
        By itemValueLocator = By.id("txt_addItem_itemValue");
        By totalInvoiceLocator = By.id("txt_total_inv");
        float itemExpectedPrice = 10;
        float itemActualPrice = 0;
        float itemExpectedDiscount = 0;
        String itemExpectedDiscountType = "ABC";
        String itemActualDiscountType;
        String focusable;
        float itemActualDiscount = 0;
        float itemActualTax = 0;
        float itemExpectedTax = 0;
        float itemActualValue = 0;
        boolean calculateTaxBeforeDiscount = false;
        float itemExpectedValue = 0;
        float quantity = Float.valueOf(qty);

        driver.findElement(searchBarLocator).click();
        driver.findElement(searchBarLocator).sendKeys(itemCode);

        AndroidElement addItemListViewLocator = (AndroidElement)  driver.findElement(MobileBy.id("lstv_add_item_dialog"));

        MobileElement packLocator = addItemListViewLocator.findElementByAndroidUIAutomator("new UiScrollable(new UiSelector()"
                + ".resourceId(\"com.incube.invan:id/" + uomValueLocator +
                "\")).scrollIntoView(new UiSelector().text(\"" + packType + "\"));"
        );

        packLocator.click();
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.
                        visibilityOfElementLocated(itemQtyLocator));
        focusable = driver.findElement(itemQtyLocator).getAttribute("focused");
        softAssert.assertEquals(focusable,"true","Not focused");
        if(focusable.equals("true")) {
            driver.pressKeyCode(AndroidKeyCode.KEYCODE_DEL);
             driver.getKeyboard().sendKeys(qty);
        }
        else {
            driver.findElement(itemQtyLocator).click();
            driver.pressKeyCode(AndroidKeyCode.KEYCODE_DEL);
            driver.getKeyboard().sendKeys(qty);
        }
        driver.navigate().back();

        itemActualPrice = Float.valueOf(driver.findElement(itemPriceLocator).getText().replace(",","")) ;
//        Assert.assertEquals(itemActualPrice,itemExpectedPrice,"Wrong Price");
        itemActualDiscount = Float.valueOf(driver.findElement(itemDiscountLocator).getText().replace(",","")) ;
        itemActualDiscountType = driver.findElement(itemDiscountTypeLocator).getText();
//        Assert.assertEquals(itemActualDiscount,itemExpectedDiscount,"Wrong Discount");
        itemActualValue  = Float.valueOf(driver.findElement(itemValueLocator).getText().replace(",","")) ;
        if(calculateTaxBeforeDiscount==false) {
            if(itemActualDiscountType =="%") {
                itemExpectedValue = itemActualPrice * quantity * (1 - itemActualDiscount / 100) * (1 + itemActualTax);
            }
            else {
                itemExpectedValue = (itemActualPrice - itemActualDiscount) * quantity * (1 + itemActualTax);
            }
        }
        else {
            if(itemActualDiscountType =="%") {
                itemExpectedValue = itemActualPrice * quantity * (1 - itemActualDiscount / 100)
                        + (itemActualPrice * quantity * itemActualTax) / 100;
            }
            else
            {
                itemExpectedValue = (itemActualPrice - itemActualDiscount) * quantity
                        + (itemActualPrice * quantity * itemActualTax) / 100;
            }
        }
        Assert.assertEquals(itemActualValue,itemExpectedValue,"Wrong Item Value");
//        if() {
//            itemTax = Float.valueOf(driver.findElement(itemTaxLocator).getText().replace(",",""));
//        }
        driver.findElement(saveItemButtonLocator).click();
        return itemActualValue;
    }
}
