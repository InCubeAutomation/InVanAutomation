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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

public class AddItem extends TestNGConfig {


    public static BigDecimal addItem(String itemCode, String packType, String qty, boolean taxable , boolean discountEditable, boolean isPromotion , String returnStatus, String returnReason, String expiryDate) {
        SharedFunctions sharedFunctions = new SharedFunctions();
        int numberOfDigits = 2;
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("#,###.###",symbols);
        df.setParseBigDecimal(true);
        df.setRoundingMode(RoundingMode.HALF_UP);
        By searchBarLocator = By.id("sv_search");
        By itemCodeValueLocator = By.id("itemCodeTextView");
        By uomValueLocator = By.id("uomTextView");
        By itemQtyLocator = By.id("txt_addItem_Qty");
        By saveItemButtonLocator = By.id("btn_addItem_save");
        By itemPriceLocator = By.id("txt_addItem_fullPrice");
        By itemDiscountLocator = By.id("txt_addItem_disc");
        By itemDiscountTypeLocator;
        By itemDiscountTypeSpinnerLocator = By.id("spinner_discount_type");
        if(discountEditable) {
            itemDiscountTypeLocator = By.id("text1");
        } else {
            itemDiscountTypeLocator = By.id("txt_addItem_disctype");
        }
        By itemTaxLocator = By.id("txt_addItem_tax");
        By itemValueLocator = By.id("txt_addItem_itemValue");
        By totalInvoiceLocator = By.id("txt_total_inv");
        By packStatusLocator = By.id("cmb_addItem_status");
        By returnReasonLocator = By.id("cmb_addItem_reason");
        By expiryDateLocator = By.id("btn_addItem_expiryDate");

        BigDecimal itemExpectedPrice = new BigDecimal("0.00");
        BigDecimal itemActualPrice = new BigDecimal("0.00");
        BigDecimal itemExpectedDiscount = new BigDecimal("0.00");
        String itemExpectedDiscountType = "ABC";
        String itemActualDiscountType = "ABC";
        String focusable;
        BigDecimal itemActualDiscount = new BigDecimal("0.00");
        BigDecimal itemActualTax = new BigDecimal("0.00");
        BigDecimal itemExpectedTax = new BigDecimal("0.00");
        BigDecimal itemActualValue = new BigDecimal("0.00");
        boolean calculateTaxBeforeDiscount = false;
        BigDecimal itemExpectedValue = new BigDecimal("0.00");
        BigDecimal itemActualTaxValue = new BigDecimal("0.00");
        BigDecimal quantity = new BigDecimal(qty);


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

        try {
            itemActualPrice = (BigDecimal) df.parse((driver.findElement(itemPriceLocator).getText().replace(" ","")));
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        Assert.assertEquals(itemActualPrice,itemExpectedPrice,"Wrong Price");
        if (!isPromotion){
            try {
                itemActualDiscount = (BigDecimal) df.parse((driver.findElement(itemDiscountLocator).getText().replace(" ","")));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(discountEditable){
            itemActualDiscountType = driver.findElement(itemDiscountTypeSpinnerLocator).findElement(itemDiscountTypeLocator).getText();

        } else {
            itemActualDiscountType = driver.findElement(itemDiscountTypeLocator).getText();
        }
    }
if (taxable) {
    try {
        itemActualTax = (BigDecimal) df.parse((driver.findElement(itemTaxLocator).getText().replace(" ","")));
    } catch (ParseException e) {
        e.printStackTrace();
    }
}
  //
        //         Assert.assertEquals(itemActualDiscount,itemExpectedDiscount,"Wrong Discount");
        if (!isPromotion) {
            try {
                itemActualValue = (BigDecimal) df.parse(driver.findElement(itemValueLocator).getText().replace(" ",""));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(calculateTaxBeforeDiscount==false) {
            if(itemActualDiscountType =="%") {
                itemActualTaxValue = itemActualPrice.multiply(quantity).multiply(new BigDecimal("1.00").subtract(itemActualDiscount
                        .divide(new BigDecimal("100.00")))).multiply(itemActualTax.divide(new BigDecimal("100.00"))).setScale(numberOfDigits,RoundingMode.HALF_UP);
                itemExpectedValue = itemActualPrice.multiply(quantity).multiply(new BigDecimal(1.00).subtract(itemActualDiscount
                        .divide(new BigDecimal("100.00")))).add(itemActualTaxValue).setScale(numberOfDigits,RoundingMode.HALF_UP);
                            }
            else {
                itemActualTaxValue = (itemActualPrice.subtract(itemActualDiscount)).multiply(quantity).multiply(itemActualTax
                        .divide(new BigDecimal("100.00"))).setScale(numberOfDigits,RoundingMode.HALF_UP);
                itemExpectedValue = ((itemActualPrice.subtract(itemActualDiscount)).multiply(quantity)).add(itemActualTaxValue).setScale(numberOfDigits,RoundingMode.HALF_UP);
            }
        }
        else {
            if(itemActualDiscountType =="%") {
                itemActualTaxValue = itemActualPrice.multiply(quantity).multiply(itemActualTax).divide(new BigDecimal("100.00")).setScale(numberOfDigits,RoundingMode.HALF_UP);
                itemExpectedValue = itemActualPrice.multiply(quantity).multiply(new BigDecimal("1.00").subtract(itemActualDiscount
                        .divide(new BigDecimal("100.00")))).add(itemActualPrice).add(itemActualTaxValue).setScale(numberOfDigits,RoundingMode.HALF_UP);

            }
            else
            {
                itemActualTaxValue = itemActualPrice.multiply(quantity).multiply(itemActualTax).divide(new BigDecimal("100.00")).setScale(numberOfDigits,RoundingMode.HALF_UP);
                itemExpectedValue = (itemActualValue.subtract(itemActualDiscount)).multiply(quantity).add(itemActualTaxValue).setScale(numberOfDigits,RoundingMode.HALF_UP);
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
