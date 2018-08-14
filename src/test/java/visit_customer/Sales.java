package visit_customer;

import io.appium.java_client.android.AndroidKeyCode;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import read_data_excel.ReadData;
import shared_functions.AddItem;
import shared_functions.SharedFunctions;
import sqlite_access.Accounts;
import sqlite_access.Configurations;
import testng_config_methods.TestNGConfig;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Sales extends TestNGConfig {
    ReadData readData = new ReadData();
    SharedFunctions sharedFunctions = new SharedFunctions();
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
    //endregion

    //region Define Variables
    List<List<String>> itemsData = new ArrayList<List<String>>();
    List<List<List<String>>> promotedItemsData = new ArrayList<List<List<String>>>();

    boolean taxable = true;
    BigDecimal salesValue = new BigDecimal("0.00");
    BigDecimal itemAmount = new BigDecimal("0.00");
    BigDecimal promotedItemsAmount =  new BigDecimal("0.00");
    Integer noOfSalesItems;
    int noOfPromotedItems;
    int noOfPromotions;
    Double toleranceValuePercentage=5d;
    Double highCreditValue = 0d;
    Accounts accounts = new Accounts();
    Double creditLimit=0D;
    Double balance=0D;
    Double availableCredit=0d;
    boolean availableCreditChecked = false;
    boolean highCreditChecked = false;
//endregion

    @Parameters({"outletCode","salesMode","filePath"})
    @Test
    public void sales(String outletCode,String salesMode,String filePath) throws SQLException, IOException {
        //region Reading Outlet Account Data
        Double[] accountData = accounts.outletBalanceAndCredit(outletCode);
            creditLimit = accountData[0];
            balance = accountData[1];
            toleranceValuePercentage = accountData[2];
            availableCredit = creditLimit*(1.00+toleranceValuePercentage/100)-  balance;
            highCreditValue = creditLimit*(1.00-toleranceValuePercentage/100)-  balance;
            itemsData = readData.readData(filePath, "Sales");
            //endregion

        //region Reading Item Data
            noOfSalesItems = itemsData.size()-1;
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

        sharedFunctions.enterScreen("Sales");

        //region Credit Limit Violation Check before opening sales screen
        if(availableCredit < 0){
            By keyTypeLocator = By.id("lbl_keyType");
            By employeeKeyOKButtonLocator =By.id("btn_employeekey_ok");
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
        } else if (highCreditValue < 0 ){
                wait.until(ExpectedConditions.visibilityOfElementLocated(alertTitleLocator));
                String messageDescription = driver.findElement(alertMessageLocator).getText().trim().toLowerCase();
                if(!messageDescription.contains("customer is about to reach his credit limit")) {
                    softAssert.fail();
                }
                driver.findElement(yesSaveLocator).click();
                highCreditChecked = true;
        }
//endregion

        sharedFunctions.getMenuName("Sales");

        //region Adding items

        for (int i = 0; i < noOfSalesItems; i++) {
            itemAmount = AddItem.addItem(itemsData.get(i).get(0), itemsData.get(i).get(1), itemsData.get(i).get(2),taxable,true,false,"","","");
            salesValue = salesValue.add(itemAmount);
            itemAmount = BigDecimal.ZERO;
        }
        //endregion

        if (sharedFunctions.elementExists(saveImageButtonLocator)) {
            driver.findElement(saveImageButtonLocator).click();
        } else {
            driver.findElement(moreOptionsLocator).click();
            driver.findElement(saveTextButtonLocaor).click();
        }


        //region Credit Limit Violation Check After Saving
        if(salesValue.doubleValue() > availableCredit && !availableCreditChecked ) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(alertTitleLocator));
            String messageDescription = driver.findElement(alertMessageLocator).getText().trim().toLowerCase();
            if(!messageDescription.contains("exceed") || !messageDescription.contains("credit") || !messageDescription.contains("limit")){
                softAssert.fail();
            }
            driver.findElement(yesSaveLocator).click();
            By keyTypeLocator = By.id("lbl_keyType");
            (new WebDriverWait(driver, 10))
                    .until(ExpectedConditions.
                            visibilityOfElementLocated(keyTypeLocator));
            String keyType = driver.findElement(keyTypeLocator).getText().trim().toLowerCase();
            if(keyType.contains("exceed") && keyType.contains("credit") && keyType.contains("limit")){
                    sharedFunctions.enterKey();
            }
        } else if (salesValue.doubleValue() > highCreditValue && !availableCreditChecked && !highCreditChecked ) {
            if (!sharedFunctions.elementExists(alertTitleLocator)) {
                softAssert.fail("No high credit warning");
            } else {
                wait.until(ExpectedConditions.visibilityOfElementLocated(alertTitleLocator));
                String messageDescription = driver.findElement(alertMessageLocator).getText().trim().toLowerCase();
                if (!messageDescription.contains("customer is about to reach his credit limit")) {
                    softAssert.fail();
                }
                driver.findElement(yesSaveLocator).click();
            }
        }
//endregion

        //region Taking Promotions
        if (noOfPromotions>0 && (AndroidVersion.equals("7.1.1") || AndroidVersion.equals("7.0") || AndroidVersion.equals("8.0"))){
            driver.pressKeyCode(AndroidKeyCode.KEYCODE_APP_SWITCH);
            (new WebDriverWait(driver,10))
                    .until(ExpectedConditions.
                            visibilityOfElementLocated(InVanAppSwitchLocator));
            driver.findElement(InVanAppSwitchLocator).click();

        }
        for (int i =0; i<noOfPromotions;i++) {
            driver.findElement(btnTakePromotionLocator).click();
            if(!promotedItemsData.get(i).get(0).get(0).equals("--"))
            {
                noOfPromotedItems = promotedItemsData.get(i).size();
                driver.findElement(addItemLocator).click();
                for (int j = 0; j < noOfPromotedItems; j++) {
                    itemAmount = AddItem.addItem(promotedItemsData.get(i).get(j).get(0), promotedItemsData.get(i).get(j).get(1), promotedItemsData.get(i).get(j).get(2),taxable,true,true,"","","");
                    promotedItemsAmount = promotedItemsAmount.add(itemAmount);
                    itemAmount = BigDecimal.ZERO;
                }
                driver.navigate().back();
                driver.findElement(saveItemsListLocator).click();
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
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.
                        visibilityOfElementLocated(finalSaveButtonLocator));
        driver.findElement(finalSaveButtonLocator).click();
        driver.findElement(yesSaveLocator).click();
        //endregion

        softAssert.assertAll();
    }
}
