package shared_functions;

import collections.FilterLists;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import sqlite_access.Configurations;
import testng_config_methods.TestNGConfig;
import visit_customer.Delivery;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class AddItem extends TestNGConfig {

    //region calling classes
    SharedFunctions sharedFunctions = new SharedFunctions();
    Configurations configurations = new Configurations();
    DecimalFormatSymbols symbols = new DecimalFormatSymbols();
    DecimalFormat df = new DecimalFormat("#,###.###",symbols);
    // endregion

    //region commonAddItemLocators
    By searchBarLocator = By.id("sv_search");
    By itemCodeValueLocator = By.id("itemCodeTextView");
    By uomValueLocator = By.id("uomTextView");
    By itemQtyLocator = By.id("txt_addItem_Qty");
    By saveItemButtonLocator = By.id("btn_addItem_save");
    By itemPriceLocator = By.id("txt_addItem_fullPrice");
    By itemDiscountLocator = By.id("txt_addItem_disc");
    By itemDiscountTypeLocator;
    By itemDiscountTypeSpinnerLocator = By.id("spinner_discount_type");
    By itemTaxLocator = By.id("txt_addItem_tax");
    By itemValueLocator = By.id("txt_addItem_itemValue");
    By totalInvoiceLocator = By.id("txt_total_inv");
    By packStatusLocator = By.id("cmb_addItem_status");
    By returnReasonLocator = By.id("cmb_addItem_reason");
    By expiryDateLocator = By.id("btn_addItem_expiryDate");
    //endregion

    //region defining variables
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
    int numberOfDigits = 2;
    String oldQty="";
    BigDecimal oldQuantity= new BigDecimal("0.00");
    BigDecimal newQuantity= new BigDecimal("0.00");
    boolean successInEdit=true;

    //endregion

    public  BigDecimal addItem(String originalScreen, String itemCode, String packType, String qty, boolean taxable , boolean discountEditable, String packStatus, String returnReason, String expiryDate) throws ParseException {

        //region setting decimal digits
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');
        df.setParseBigDecimal(true);
        df.setRoundingMode(RoundingMode.HALF_UP);
        BigDecimal quantity = new BigDecimal(qty);
        //endregion

        // discount type locator is different as it's a textview when not editable and a spinnen when editable
        if(discountEditable) {
            itemDiscountTypeLocator = By.id("text1");
        } else {
            itemDiscountTypeLocator = By.id("txt_addItem_disctype");
        }
        // region selecting item
        driver.findElement(searchBarLocator).click();
        driver.findElement(searchBarLocator).sendKeys(itemCode);
        driver.hideKeyboard();
        AndroidElement addItemListViewLocator = (AndroidElement) driver.findElement(MobileBy.id("lstv_add_item_dialog"));

        MobileElement packLocator = addItemListViewLocator.findElementByAndroidUIAutomator("new UiScrollable(new UiSelector()"
                + ".resourceId(\"com.incube.invan:id/" + uomValueLocator +
                "\")).scrollIntoView(new UiSelector().text(\"" + packType + "\"));"
        );

        packLocator.click();
  //endregion

        //region inputting quantity
        wait.until(ExpectedConditions.
                        visibilityOfElementLocated(itemQtyLocator));
        oldQty = driver.findElement(itemQtyLocator).getText();
        if(!oldQty.equals("")) {
            oldQuantity = new BigDecimal(oldQty);
        }
        newQuantity = new BigDecimal(qty);
        focusable = driver.findElement(itemQtyLocator).getAttribute("focused");
        softAssert.assertEquals(focusable, "true", "Not focused");
        if (!focusable.equals("true")) {
            driver.findElement(itemQtyLocator).click();
        }
            driver.pressKey(new KeyEvent(AndroidKey.DEL));
            driver.findElement(itemQtyLocator).sendKeys(qty);
            driver.hideKeyboard();
//endregion

        // region filling pack status, return reason, and expirydate
        if (!packStatus.equals("")) {
            driver.findElement(packStatusLocator).click();
            By selectedPackStatusLocator = By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.FrameLayout/" +
                    "android.widget.ListView/android.widget.TextView[@text='" + packStatus + "']");
            wait.until(ExpectedConditions.visibilityOfElementLocated(selectedPackStatusLocator));
            driver.findElement(selectedPackStatusLocator).click();
        }
        if (!returnReason.equals("")) {
            driver.findElement(returnReasonLocator).click();
            By selectedReturnReasonLocator = By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.FrameLayout/" +
                    "android.widget.ListView/android.widget.CheckedTextView[@text='" + returnReason + "']");
            wait.until(ExpectedConditions.visibilityOfElementLocated(selectedReturnReasonLocator));
            driver.findElement(selectedReturnReasonLocator).click();
        }

        if (!expiryDate.equals("")) {
            driver.findElement(expiryDateLocator).click();
            sharedFunctions.pickDateFromAndroid7Calendar(expiryDate);
        }
//endregion

        //region reading item price, discount, and tax from UI and (later) validate with values from excel sheet
        try {
            itemActualPrice = (BigDecimal) df.parse((driver.findElement(itemPriceLocator).getText().replace(" ","")));
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        Assert.assertEquals(itemActualPrice,itemExpectedPrice,"Wrong Price");
        if (!originalScreen.equals("Promotions")&& !originalScreen.equals("Load") ){
            try {
                itemActualDiscount = (BigDecimal) df.parse((driver.findElement(itemDiscountLocator).getText().replace(" ","")));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //      Assert.assertEquals(itemActualDiscount,itemExpectedDiscount,"Wrong Discount");

            if(discountEditable){
            itemActualDiscountType = driver.findElement(itemDiscountTypeSpinnerLocator).findElement(itemDiscountTypeLocator).getText();

        } else {
            itemActualDiscountType = driver.findElement(itemDiscountTypeLocator).getText();
        }
    }


if (taxable & !originalScreen.equals("Promotions")) {
    try {
        itemActualTax = (BigDecimal) df.parse((driver.findElement(itemTaxLocator).getText().replace(" ","")));
    } catch (ParseException e) {
        e.printStackTrace();
    }
}
              //Assert.assertEquals(itemActualTax,itemExpectedTax,"Wrong Tax");

        //endregion

        //region item actual value in commonAddItem and validate correct calculations
        if (!originalScreen.equals("Promotions")) {
                itemActualValue = (BigDecimal) df.parse(driver.findElement(itemValueLocator).getText().replace(" ",""));

            if(!calculateTaxBeforeDiscount) {
            if(itemActualDiscountType.equals("%")) {
                itemActualTaxValue = itemActualPrice.multiply(quantity).multiply(new BigDecimal("1.00").subtract(itemActualDiscount
                        .divide(new BigDecimal("100.00"),numberOfDigits,RoundingMode.HALF_UP))).multiply(itemActualTax.divide(new BigDecimal("100.00"),numberOfDigits,RoundingMode.HALF_UP)).setScale(numberOfDigits,RoundingMode.HALF_UP);
                itemExpectedValue = itemActualPrice.multiply(quantity).multiply(new BigDecimal(1.00).subtract(itemActualDiscount
                        .divide(new BigDecimal("100.00"),numberOfDigits,RoundingMode.HALF_UP))).add(itemActualTaxValue).setScale(numberOfDigits,RoundingMode.HALF_UP);
                            }
            else {
                itemActualTaxValue = (itemActualPrice.subtract(itemActualDiscount)).multiply(quantity).multiply(itemActualTax
                        .divide(new BigDecimal("100.00"),numberOfDigits,RoundingMode.HALF_UP)).setScale(numberOfDigits,RoundingMode.HALF_UP);
                itemExpectedValue = ((itemActualPrice.subtract(itemActualDiscount)).multiply(quantity)).add(itemActualTaxValue).setScale(numberOfDigits,RoundingMode.HALF_UP);
            }
        }
        else {
            if(itemActualDiscountType.equals("%")) {
                itemActualTaxValue = itemActualPrice.multiply(quantity).multiply(itemActualTax).divide(new BigDecimal("100.00"),numberOfDigits,RoundingMode.HALF_UP).setScale(numberOfDigits,RoundingMode.HALF_UP);
                itemExpectedValue = itemActualPrice.multiply(quantity).multiply(new BigDecimal("1.00").subtract(itemActualDiscount
                        .divide(new BigDecimal("100.00"),numberOfDigits,RoundingMode.HALF_UP))).add(itemActualPrice).add(itemActualTaxValue).setScale(numberOfDigits,RoundingMode.HALF_UP);

            }
            else
            {
                itemActualTaxValue = itemActualPrice.multiply(quantity).multiply(itemActualTax).divide(new BigDecimal("100.00"),numberOfDigits,RoundingMode.HALF_UP).setScale(numberOfDigits,RoundingMode.HALF_UP);
                itemExpectedValue = (itemActualPrice.subtract(itemActualDiscount)).multiply(quantity).add(itemActualTaxValue).setScale(numberOfDigits,RoundingMode.HALF_UP);
            }
        }
        if(itemExpectedValue.compareTo(new BigDecimal("0.00"))==-1){
            itemExpectedValue = new BigDecimal("0.00");
        }
        softAssert.assertEquals(itemActualValue,itemExpectedValue,"Wrong Item Value");
        }
        //endregion

if(!sharedFunctions.elementExists(saveItemButtonLocator)) {
    sharedFunctions.scrollUp();
}


            if(originalScreen.equals("Delivery")){
                BigDecimal availableToAdd=new BigDecimal("0.00");
                FilterLists filterLists = new FilterLists();
                List<List<String>> currentItemPacks = new ArrayList<List<String>>();
                List<List<String>> sameItemChangedQuantityPacks = new ArrayList<List<String>>();
                //currentItemPacks : 0 - item Code , 1- UOM , 2- pack Quantity
                currentItemPacks = filterLists.filterList(Delivery.itemPacksQuantities,0,itemCode);
                sameItemChangedQuantityPacks = filterLists.filterList(Delivery.changedItemsData,0,itemCode);
                if(sameItemChangedQuantityPacks.size()>0) {

                    for (int i = 0; i < sameItemChangedQuantityPacks.size(); i++) {
                        //sameItemChangedQuantityPacks : 0-itemCode, 1-UOM, 2-original order quantity, 3- delivery new quantity
                        String UOM = sameItemChangedQuantityPacks.get(i).get(1);
                        BigDecimal extraPackQuantity = BigDecimal.valueOf(Double.valueOf(sameItemChangedQuantityPacks.get(i).get(2)) - Double.valueOf(sameItemChangedQuantityPacks.get(i).get(3)));
                        BigDecimal otherPackQty = (BigDecimal) df.parse(filterLists.filterList(currentItemPacks, 1, UOM).get(0).get(2));
                        BigDecimal currentPackQty = (BigDecimal) df.parse(filterLists.filterList(currentItemPacks, 1, packType).get(0).get(2));
                        BigDecimal extraQuantity = extraPackQuantity.multiply(otherPackQty).divide(currentPackQty,numberOfDigits,RoundingMode.FLOOR).setScale(0,RoundingMode.FLOOR);
                        availableToAdd = availableToAdd.add(extraQuantity);
                    }
                }
                int res = (availableToAdd.add(oldQuantity)).compareTo(newQuantity);
                if(res==-1){
                    successInEdit=false;
                }else {
                    successInEdit=true;
                }
            }
        driver.findElement(saveItemButtonLocator).click();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!successInEdit) {

            String filePath = "D://TestDB//" + itemCode + "-" + packType + ".png";
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sharedFunctions.takeScreenshot(filePath);
            String screenText = sharedFunctions.convertImageToText(filePath);
            driver.navigate().back();
            itemActualValue = null;
            if (!screenText.contains("requested quantity more than originally ordered item")) {
                Assert.fail("No quantity error");
            }

        }

        return itemActualValue;
    }



    public  BigDecimal editItemFromOutsieList(String itemCode, String packType, String qty, boolean taxable , boolean discountEditable, boolean isPromotion , String packStatus, String returnReason, String expiryDate) {


        return itemActualValue;
    }



}

