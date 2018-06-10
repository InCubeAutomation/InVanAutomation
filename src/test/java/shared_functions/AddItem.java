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

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AddItem extends TestNGConfig {


    public static float addItem(String itemCode, String packType, String qty, boolean taxable , boolean isPromotion ,String returnStatus,String returnReason, String expiryDate) {
        SharedFunctions sharedFunctions = new SharedFunctions();
        By searchBarLocator = By.id("sv_search");
        By itemCodeValueLocator = By.id("itemCodeTextView");
        By uomValueLocator = By.id("uomTextView");
        By itemQtyLocator = By.id("txt_addItem_Qty");
        By saveItemButtonLocator = By.id("btn_addItem_save");
        By itemPriceLocator = By.id("txt_addItem_fullPrice");
        By itemDiscountLocator = By.id("txt_addItem_disc");
        //By itemDiscountTypeLocator = By.id("txt_addItem_disctype");
        By itemDiscountTypeLocator = By.id("text1");
        By itemTaxLocator = By.id("txt_addItem_tax");
        By itemValueLocator = By.id("txt_addItem_itemValue");
        By totalInvoiceLocator = By.id("txt_total_inv");
        By packStatusLocator = By.id("cmb_addItem_status");
        By returnReasonLocator = By.id("cmb_addItem_reason");
        By expiryDateLocator = By.id("btn_addItem_expiryDate");

        float itemExpectedPrice = 10;
        float itemActualPrice = 0;
        float itemExpectedDiscount = 0;
        String itemExpectedDiscountType = "ABC";
        String itemActualDiscountType = "ABC";
        String focusable;
        float itemActualDiscount = 0;
        float itemActualTax = 0;
        float itemExpectedTax = 0;
        float itemActualValue = 0;
        boolean calculateTaxBeforeDiscount = false;
        float itemExpectedValue = 0;
        float itemActualTaxValue = 0;
        float quantity = Float.valueOf(qty);
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.HALF_UP);

        driver.findElement(searchBarLocator).click();
        driver.findElement(searchBarLocator).sendKeys(itemCode);
        driver.hideKeyboard();
        AndroidElement addItemListViewLocator = (AndroidElement) driver.findElement(MobileBy.id("lstv_add_item_dialog"));

        MobileElement packLocator = addItemListViewLocator.findElementByAndroidUIAutomator("new UiScrollable(new UiSelector()"
                + ".resourceId(\"com.incube.invan:id/" + uomValueLocator +
                "\")).scrollIntoView(new UiSelector().text(\"" + packType + "\"));"
        );

        packLocator.click();
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.
                        visibilityOfElementLocated(itemQtyLocator));
        focusable = driver.findElement(itemQtyLocator).getAttribute("focused");
        softAssert.assertEquals(focusable, "true", "Not focused");
        if (focusable.equals("true")) {
            driver.pressKeyCode(AndroidKeyCode.KEYCODE_DEL);
            driver.getKeyboard().sendKeys(qty);
        } else {
            driver.findElement(itemQtyLocator).click();
            driver.pressKeyCode(AndroidKeyCode.KEYCODE_DEL);
            driver.getKeyboard().sendKeys(qty);
        }
        driver.hideKeyboard();

        if (!returnStatus.equals("")) {
            driver.findElement(packStatusLocator).click();
            By selectedPackStatusLocator = By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.FrameLayout/" +
                    "android.widget.ListView/android.widget.CheckedTextView[@text='" + returnStatus + "']");
            driver.findElement(selectedPackStatusLocator).click();
        }
        if (!returnStatus.equals("")) {
            driver.findElement(returnReasonLocator).click();
            By selectedReturnReasonLocator = By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.FrameLayout/" +
                    "android.widget.ListView/android.widget.CheckedTextView[@text='" + returnReason + "']");
            driver.findElement(selectedReturnReasonLocator).click();
        }

        if (!expiryDate.equals("")) {
            driver.findElement(expiryDateLocator).click();
            sharedFunctions.pickDateFromAndroid7Calendar(expiryDate);
        }

        itemActualPrice = Float.valueOf(driver.findElement(itemPriceLocator).getText().replace(",", ""));
//        Assert.assertEquals(itemActualPrice,itemExpectedPrice,"Wrong Price");
        if (!isPromotion){
            itemActualDiscount = Float.valueOf(driver.findElement(itemDiscountLocator).getText().replace(",", ""));
        itemActualDiscountType = driver.findElement(itemDiscountTypeLocator).getText();
    }
if (taxable) {
    itemActualTax = Float.valueOf(driver.findElement(itemTaxLocator).getText().replace(",", ""));
}
  //
        //         Assert.assertEquals(itemActualDiscount,itemExpectedDiscount,"Wrong Discount");
        if (!isPromotion) {
            itemActualValue = Float.valueOf(driver.findElement(itemValueLocator).getText().replace(",", ""));

        if(calculateTaxBeforeDiscount==false) {
            if(itemActualDiscountType =="%") {
                itemActualTaxValue = itemActualPrice * quantity * (1 - itemActualDiscount / 100)
                        * itemActualTax / 100;
                itemExpectedValue =  itemActualPrice * quantity * (1 - itemActualDiscount / 100)+itemActualTaxValue;
            }
            else {
                itemActualTaxValue = (itemActualPrice - itemActualDiscount) * quantity * itemActualTax / 100;
                itemExpectedValue =  (itemActualPrice - itemActualDiscount) * quantity + itemActualTaxValue ;
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
        }
if(!sharedFunctions.elementExists(saveItemButtonLocator)) {
    sharedFunctions.scrollUp();
}
        driver.findElement(saveItemButtonLocator).click();
        return itemActualValue;
    }
}
