package visit_customer;

import io.appium.java_client.android.AndroidKeyCode;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import read_data.ReadData;
import shared_functions.AddItem;
import shared_functions.SharedFunctions;
import testng_config_methods.TestNGConfig;
import java.util.ArrayList;
import java.util.List;

public class Sales extends TestNGConfig {
    ReadData readData = new ReadData();
    SharedFunctions sharedFunctions = new SharedFunctions();
    AddItem addItem = new AddItem();
    By salesMenuLocator = By.id("menu_image_1");
    By saveImageButtonLocator = By.id("action_save");
    By saveTextButtonLocaor = By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.ListView/android.widget.LinearLayout/android.widget.RelativeLayout/android.widget.TextView[@text='Save']");
    By moreOptionsLocator = By.xpath("//android.widget.ImageView[@content-desc=\"More options\"]");
    By yesSaveLocator = By.id("button1");
    By addItemLocator = By.id("btn_add_item");
    By saveItemsListLocator = By.id("btn_save_order");
    By payAllCashButtonLocator = By.id("btn_PayAllcash");
    By savePaymentsLocator = By.id("action_pay");
    By finalSaveButtonLocator = By.id("btn_ok_order_summary");
    List<List<String>> itemsData = new ArrayList<List<String>>();
    List<List<List<String>>> promotedItemsData = new ArrayList<List<List<String>>>();

    By btnTakePromotionLocator = By.id("btn_take_promo");

    boolean taxable = true;
    float salesValue = 0;
    float itemAmount;
    float promotedItemsAmount;
    int noOfSalesItems;
    int noOfPromotedItems;
    int noOfPromotions;


    @Parameters({"filePath"})
    @Test
    public void sales(String filePath) {
        try {
            itemsData = readData.readData(filePath, "Sales");
            noOfSalesItems = itemsData.size()-1;
            noOfPromotions = Integer.parseInt(itemsData.get(noOfSalesItems).get(1));
                for (int i =0; i<noOfPromotions;i++) {
                    if(!itemsData.get(noOfSalesItems).get(i+2).equals("--") && !itemsData.get(noOfSalesItems).get(i+2).equals("")){
                   try {
                       promotedItemsData.add(readData.readData(filePath, itemsData.get(noOfSalesItems).get(i + 2)));
                   } catch (Throwable throwable) {// In case exception happen
                            Assert.fail("Read Item Data failed, " +
                                    "please check log: \n" + throwable.getMessage());
                        }
                    } else{
                        List<List<String>> list1 = new ArrayList<List<String>>();
                        List<String> list2 = new ArrayList<String>();
                        list2.add("--");
                        list1.add(list2);
                        promotedItemsData.add(list1);
                    }
                }
        } catch (Throwable throwable) {// In case exception happen
            Assert.fail("Read Item Data failed, " +
                    "please check log: \n" + throwable.getMessage());
        }
        sharedFunctions.enterScreen("Sales");
        sharedFunctions.getMenuName("Sales");

        for (int i = 0; i < noOfSalesItems; i++) {
            itemAmount = AddItem.addItem(itemsData.get(i).get(0), itemsData.get(i).get(1), itemsData.get(i).get(2),taxable,false,"","","");
            salesValue = salesValue + itemAmount;
            itemAmount = 0;
        }

        if (sharedFunctions.elementExists(saveImageButtonLocator)) {
            driver.findElement(saveImageButtonLocator).click();
        } else {
            driver.findElement(moreOptionsLocator).click();
            driver.findElement(saveTextButtonLocaor).click();
        }
        if (noOfPromotions>0 && (AndroidVersion.equals("7.1.1") || AndroidVersion.equals("7.0") || AndroidVersion.equals("8.0"))){
            driver.pressKeyCode(AndroidKeyCode.KEYCODE_APP_SWITCH);
            driver.findElementByAccessibilityId("InVan In Van").click();
        }
        for (int i =0; i<noOfPromotions;i++) {
            driver.findElement(btnTakePromotionLocator).click();
            if(!promotedItemsData.get(i).get(0).get(0).equals("--"))
            {
                noOfPromotedItems = promotedItemsData.get(i).size();
                driver.findElement(addItemLocator).click();
                for (int j = 0; j < noOfPromotedItems; j++) {
                    itemAmount = AddItem.addItem(promotedItemsData.get(i).get(j).get(0), promotedItemsData.get(i).get(j).get(1), promotedItemsData.get(i).get(j).get(2),taxable,true,"","","");
                    promotedItemsAmount = promotedItemsAmount + itemAmount;
                    itemAmount = 0;
                }
                driver.hideKeyboard();
                driver.findElement(saveItemsListLocator).click();
            }
        }
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.
                        visibilityOfElementLocated(payAllCashButtonLocator));
        driver.findElement(payAllCashButtonLocator).click();
        driver.findElement(yesSaveLocator).click();
        driver.findElement(savePaymentsLocator).click();
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.
                        visibilityOfElementLocated(finalSaveButtonLocator));
        driver.findElement(finalSaveButtonLocator).click();
        driver.findElement(yesSaveLocator).click();
    }
}
