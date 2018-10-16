package visit_customer;

import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import read_data_excel.ReadData;
import shared_functions.AddItem;
import shared_functions.SharedFunctions;
import sqlite_access.Accounts;
import testng_config_methods.TestNGConfig;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class Sales extends TestNGConfig {
    ReadData readData = new ReadData();
    SharedFunctions sharedFunctions = new SharedFunctions();
    Accounts accounts = new Accounts();
    AddItem addItem = new AddItem();
    boolean calculateTaxBeforeDiscount = false;
    int numberOfDigits = 2;

    //region Define Locators
    By saveImageButtonLocator = By.id("action_save");
    By saveTextButtonLocaor = By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.ListView/android.widget.LinearLayout/android.widget.RelativeLayout/android.widget.TextView[@text='Save']");
    By moreOptionsLocator = By.xpath("//android.widget.ImageView[@content-desc=\"More options\"]");
    By yesSaveLocator = By.id("button1");
    By addItemLocator = By.id("btn_add_item");
    By saveItemsListLocator = By.id("btn_save_order");
    By payAllCashButtonLocator = By.id("btn_PayAllcash");
    By savePaymentsLocator = By.id("action_pay");
    By finalSaveButtonLocator = By.id("btn_ok_order_summary");
    By alertMessageLocator = By.id("message");
    By alertTitleLocator = By.id("alertTitle");
    By InVanAppSwitchLocator = By.xpath("//android.widget.TextView[@content-desc=\"InVan In Van\"]");
    By btnTakePromotionLocator = By.id("btn_take_promo");
    By salesSummaryNetTotalLocator = By.id("tv_net_total_order_summary");
    By itemListNetTotalLocator = By.id("txt_net_total");

    //endregion

    //region Define Variables
    List<List<String>> itemsData = new ArrayList<List<String>>();
    List<List<List<String>>> promotedItemsData = new ArrayList<List<List<String>>>();
    List<List<String>> salesItemData = new ArrayList<List<String>>();

    boolean taxable = true;
    BigDecimal salesValue = new BigDecimal("0.00");
    BigDecimal salesValueAfterPromotions = new BigDecimal("0.00");
    BigDecimal itemAmount = new BigDecimal("0.00");
    BigDecimal promotedItemsAmount =  new BigDecimal("0.00");
    BigDecimal salesSummaryNetTotal =  new BigDecimal("0.00");
    BigDecimal expectedNetTotal = new BigDecimal("0.00");

    Integer noOfSalesItems;
    int noOfPromotedItems;
    int noOfPromotions;

    Double toleranceValuePercentage=5d;
    Double highCreditValue = 0d;
    Double creditLimit=0D;
    Double balance=0D;
    Double availableCredit=0d;
    boolean availableCreditChecked = false;
    boolean highCreditChecked = false;
//endregion

    @Parameters({"outletCode","salesMode","filePath"})
    @Test
    public void sales(String outletCode,String salesMode,String filePath) throws SQLException, IOException, ParseException {
        //region Setting Decimal Format
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("#,###.###",symbols);
        df.setParseBigDecimal(true);
        df.setRoundingMode(RoundingMode.HALF_UP);
        //endregion

        //region Reading Outlet Account Data
        Double[] accountData = accounts.outletBalanceAndCredit(outletCode);
            creditLimit = accountData[0];
            balance = accountData[1];
            toleranceValuePercentage = accountData[2];
            availableCredit = creditLimit*(1.00+toleranceValuePercentage/100)-  balance;
            highCreditValue = creditLimit*(1.00-toleranceValuePercentage/100)-  balance;
            //endregion


        //region Reading Item Data
        itemsData = readData.readData(filePath, "Sales");
        noOfSalesItems = itemsData.size()-1;

        for (int i = 0; i< noOfSalesItems; i++){
            List<String> packToAdd = new ArrayList<>();
            String itemCode =itemsData.get(i).get(0);
            String packType = itemsData.get(i).get(1);
            String itemQuantity = itemsData.get(i).get(2);
            String itemPrice = itemsData.get(i).get(3);
            String itemDefinedDiscount = itemsData.get(i).get(4).split("-")[0];
            String itemDefinedDiscountType = itemsData.get(i).get(4).split("-")[1];
            String itemDefinedPromotedDiscount = itemsData.get(i).get(5).split("-")[0];
            String itemDefinedPromotedDiscountType = itemsData.get(i).get(5).split("-")[1];
            String itemDefinedTax = itemsData.get(i).get(6);

            BigDecimal itemDiscountValue = new BigDecimal("0.00");
            BigDecimal itemTaxBeforePromotions = new BigDecimal("0.00");
            BigDecimal itemPromotedDiscountValue = new BigDecimal("0.00");
            BigDecimal itemTaxValue= new BigDecimal("0.00");
            BigDecimal itemNetTotal= new BigDecimal("0.00");
            BigDecimal itemNetBeforePromotions = new BigDecimal("0.00");

            BigDecimal itemGross = new BigDecimal(itemQuantity).multiply(new BigDecimal(itemPrice)).setScale(numberOfDigits,RoundingMode.HALF_UP);
            if(itemDefinedDiscountType.equals("%")) {
                itemDiscountValue = itemGross.multiply(new BigDecimal(itemDefinedDiscount)).divide(new BigDecimal("100.00"),numberOfDigits,RoundingMode.HALF_UP);
            } else {
                itemDiscountValue = new BigDecimal(itemQuantity).multiply(new BigDecimal(itemDefinedDiscount)).setScale(numberOfDigits,RoundingMode.HALF_UP);
            }


            if(itemDefinedPromotedDiscountType.equals("%")) {
                itemPromotedDiscountValue = (itemGross.subtract(itemDiscountValue)).multiply(new BigDecimal(itemDefinedPromotedDiscount)).divide(new BigDecimal("100.00"),numberOfDigits,RoundingMode.HALF_UP);
            } else {
                itemPromotedDiscountValue = new BigDecimal(itemQuantity).multiply(new BigDecimal(itemDefinedDiscount)).setScale(numberOfDigits,RoundingMode.HALF_UP);
            }

            if(calculateTaxBeforeDiscount) {
                itemTaxValue = itemGross.multiply(new BigDecimal(itemDefinedTax)).divide(new BigDecimal("100.00"),numberOfDigits,RoundingMode.HALF_UP);
                itemTaxBeforePromotions = itemTaxValue;
            } else {
                itemTaxValue = (itemGross.subtract(itemDiscountValue).subtract(itemPromotedDiscountValue)).multiply(new BigDecimal(itemDefinedTax)).divide(new BigDecimal("100.00"),numberOfDigits,RoundingMode.HALF_UP);
                itemTaxBeforePromotions = (itemGross.subtract(itemDiscountValue)).multiply(new BigDecimal(itemDefinedTax)).divide(new BigDecimal("100.00"),numberOfDigits,RoundingMode.HALF_UP);
            }
            itemNetBeforePromotions = itemGross.subtract(itemDiscountValue).add(itemTaxBeforePromotions);
            itemNetTotal = itemGross.subtract(itemDiscountValue).subtract(itemPromotedDiscountValue).add(itemTaxValue);
            packToAdd.add(itemCode);//0
            packToAdd.add(packType);//1
            packToAdd.add(itemQuantity);//2
            packToAdd.add(itemPrice);//3
            packToAdd.add(String.valueOf(itemGross));//4
            packToAdd.add(String.valueOf(itemDiscountValue));//5
            packToAdd.add(String.valueOf(itemTaxBeforePromotions));//6
            packToAdd.add(String.valueOf(itemNetBeforePromotions));//7
            packToAdd.add(String.valueOf(itemPromotedDiscountValue));//8
            packToAdd.add(String.valueOf(itemTaxValue));//9
            packToAdd.add(String.valueOf(itemNetTotal));//10
            salesItemData.add(packToAdd);
            salesValueAfterPromotions = salesValueAfterPromotions.add(itemNetTotal);
        }

            //region Filling Promoted Items Data
        noOfPromotions = Integer.parseInt(itemsData.get(noOfSalesItems).get(1));
                for (int i =0; i<noOfPromotions;i++) {
                    if(!itemsData.get(noOfSalesItems).get(i+2).equals("--") && !itemsData.get(noOfSalesItems).get(i+2).equals("")){
                       promotedItemsData.add(readData.readData(filePath, itemsData.get(noOfSalesItems).get(i + 2)));
                    } else{
                        List<List<String>> list1 = new ArrayList<List<String>>();
                        List<String> list2 = new ArrayList<String>();
                        list2.add("--");
                        list1.add(list2);
                        promotedItemsData.add(list1);
                    }
                }
        //endregion
//endregion

        sharedFunctions.enterScreen("Sales");

        //region Credit Limit Violation Check before opening sales screen
        if(availableCredit < 0 && salesMode.equals("Credit")){
            By keyTypeLocator = By.id("lbl_keyType");
            (new WebDriverWait(driver, 10))
                    .until(ExpectedConditions.
                            visibilityOfElementLocated(keyTypeLocator));
            String keyType = driver.findElement(keyTypeLocator).getText().trim().toLowerCase();
            if(keyType.contains("exceed") && keyType.contains("credit") && keyType.contains("limit")){
                sharedFunctions.enterKey();
            } else {
                Assert.fail("Wrong Key Type");
            }
            availableCreditChecked=true;
        }
        if (highCreditValue < 0 && salesMode.equals("Credit") && !availableCreditChecked ){
                wait.until(ExpectedConditions.visibilityOfElementLocated(alertTitleLocator));
                String messageDescription = driver.findElement(alertMessageLocator).getText().trim().toLowerCase();
                if(!messageDescription.contains("customer is about to reach his credit limit")) {
                    softAssert.fail();
                }
                driver.findElement(yesSaveLocator).click();
                highCreditChecked = true;
        }
//endregion

        sharedFunctions.checkMenuName("Sales");

        for (int i = 0; i < salesItemData.size(); i++) {
            expectedNetTotal = expectedNetTotal.add(new BigDecimal(salesItemData.get(i).get(7)));
            salesValue = salesValue.add(addItem.addItem("Sales",salesItemData.get(i).get(0), salesItemData.get(i).get(1), salesItemData.get(i).get(2),taxable,true,"","",""));
            BigDecimal actualNetTotal = (BigDecimal) df.parse(driver.findElement(itemListNetTotalLocator).getText().trim());
            softAssert.assertEquals(actualNetTotal,salesValue,"Actual and Expected Net Total are different");
            softAssert.assertEquals(actualNetTotal,expectedNetTotal,"Actual and Expected Net Total are different");
        }


        if (sharedFunctions.elementExists(saveImageButtonLocator)) {
            driver.findElement(saveImageButtonLocator).click();
        } else {
            driver.findElement(moreOptionsLocator).click();
            driver.findElement(saveTextButtonLocaor).click();
        }


        //region Taking Promotions
        if (noOfPromotions>0 && (AndroidVersion.equals("7.1.1") || AndroidVersion.equals("7.0") || AndroidVersion.equals("8.0"))){
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            driver.pressKey(new KeyEvent(AndroidKey.APP_SWITCH));
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            (new WebDriverWait(driver,10))
                    .until(ExpectedConditions.
                            visibilityOfElementLocated(InVanAppSwitchLocator));
            driver.findElement(InVanAppSwitchLocator).click();

        }
        for (int i =0; i<noOfPromotions;i++) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(btnTakePromotionLocator));
            driver.findElement(btnTakePromotionLocator).click();
            if(!promotedItemsData.get(i).get(0).get(0).equals("--"))
            {
                noOfPromotedItems = promotedItemsData.get(i).size();
                driver.findElement(addItemLocator).click();
                for (int j = 0; j < noOfPromotedItems; j++) {
                    AddItem addItem = new AddItem();
                    itemAmount = addItem.addItem("Promotions",promotedItemsData.get(i).get(j).get(0), promotedItemsData.get(i).get(j).get(1), promotedItemsData.get(i).get(j).get(2),taxable,true,"","","");
                    promotedItemsAmount = promotedItemsAmount.add(itemAmount);
                    itemAmount = BigDecimal.ZERO;
                }
                driver.navigate().back();
                driver.findElement(saveItemsListLocator).click();
            }
        }

        //endregion

        //region Credit Limit Violation Check After Saving
        if (salesValueAfterPromotions.doubleValue() > availableCredit && !availableCreditChecked && salesMode.equals("Credit")) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(alertTitleLocator));
            String messageDescription = driver.findElement(alertMessageLocator).getText().trim().toLowerCase();
            if(!messageDescription.contains("exceed") || !messageDescription.contains("credit") || !messageDescription.contains("limit"))
            {
                softAssert.fail();
            }
            driver.findElement(yesSaveLocator).click();
            By keyTypeLocator = By.id("lbl_keyType");
            (new WebDriverWait(driver, 10))
                    .until(ExpectedConditions.
                            visibilityOfElementLocated(keyTypeLocator));
            String keyType = driver.findElement(keyTypeLocator).getText().trim().toLowerCase();
            if(keyType.contains("exceed") && keyType.contains("credit") && keyType.contains("limit"))
            {
                sharedFunctions.enterKey();
            }
            availableCreditChecked=true;
        }
        if (salesValueAfterPromotions.doubleValue() > highCreditValue && !availableCreditChecked && !highCreditChecked && salesMode.equals("Credit") ) {
            if (!sharedFunctions.elementExists(alertTitleLocator)) {
                softAssert.fail("No high credit warning");
            } else {
                wait.until(ExpectedConditions.visibilityOfElementLocated(alertTitleLocator));
                String messageDescription = driver.findElement(alertMessageLocator).getText().trim().toLowerCase();
                if (!messageDescription.contains("customer") || !messageDescription.contains("about")
                        || !messageDescription.contains("reach") || !messageDescription.contains("credit limit")) {
                    softAssert.fail();
                }
                driver.findElement(yesSaveLocator).click();
            }
        }
//endregion

        //region Paying Invoice for Cash Sales Mode
        if(salesMode.equals("Cash")) {
            (new WebDriverWait(driver, 10))
                    .until(ExpectedConditions.
                            visibilityOfElementLocated(payAllCashButtonLocator));
            driver.findElement(payAllCashButtonLocator).click();
            driver.findElement(yesSaveLocator).click();
            driver.findElement(savePaymentsLocator).click();
        }
        //endregion

        //region Sales Summary and final saving
        wait.until(ExpectedConditions.visibilityOfElementLocated(finalSaveButtonLocator));
        salesSummaryNetTotal = (BigDecimal) df.parse(driver.findElement(salesSummaryNetTotalLocator).getText().trim());
        softAssert.assertEquals(salesSummaryNetTotal,salesValueAfterPromotions,"Wrong Net Total At Summary");
        driver.findElement(finalSaveButtonLocator).click();
        driver.findElement(yesSaveLocator).click();
        //endregion

        softAssert.assertAll();
    }
}
