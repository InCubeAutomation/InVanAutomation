package inventory;

import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import read_data_excel.ReadData;
import shared_functions.SharedFunctions;
import testng_config_methods.TestNGConfig;
import java.time.Duration;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;

public class LoadIn extends TestNGConfig {
    SharedFunctions sharedFunctions = new SharedFunctions();
    ReadData readData = new ReadData();

    //region define Locators
    By itemsListviewLocator = By.id("lv_stock_list");
    By itemBoxLocator = By.id("ll_item_selector_bg");
    By itemTextLocator = By.id("txt_item_name");
    By itemCurrerntQtyLocator = By.id("txt_actual_qty");
    By itemOrigninalQtyLocator = By.id("txt_qty_value");
    By itemChangeCurrentQtyLocator = By.id("ed_actual_quantity");
    By okButtonChangeQtyLocator = By.id("button1");
    By yesSaveLocator = By.id("button1");
    By saveButtonLocator = By.id("action_save");
    By saveSalesSummaryLocator = By.id("btn_ok_order_summary");
    By invoiceItemsListviewLocator = By.id("lv_item_summary");
    //endregion

    List<List<String>> countedPacks = new ArrayList<List<String>>();
    List<List<String>> loadInExpectedItems = new ArrayList<List<String>>();
    HashMap<String,String[]> loadInChangedItems = new HashMap<String, String[]>();

    @Parameters ({"filePath"})
    @Test
    public void loadIn(String filepath){


        //region read data from Excel
        loadInExpectedItems = readData.readData(filepath,"Load In");
        int loadInPacksToCount =loadInExpectedItems.size();

        for (int i=0;i<loadInExpectedItems.size();i++){
            if(loadInExpectedItems.get(i).size()>3){
                if(!loadInExpectedItems.get(i).get(3).equals("") || !loadInExpectedItems.get(i).get(3).equals(loadInExpectedItems.get(i).get(2))){
                    String discrepancyQty = Integer.toString (Integer.valueOf(loadInExpectedItems.get(i).get(2)) - Integer.valueOf(loadInExpectedItems.get(i).get(3)));
// Array packdata : [0]: new actual quantity [1] : price , [2] : tax , [3] : discrepancy qty
                String[] packData = {loadInExpectedItems.get(i).get(3),loadInExpectedItems.get(i).get(4),
                        loadInExpectedItems.get(i).get(5),discrepancyQty};
                loadInChangedItems.put(loadInExpectedItems.get(i).get(0)+"-"+loadInExpectedItems.get(i).get(1),packData);
                }
            }
        }
        //endregion

        sharedFunctions.enterScreen("Inventory");
        sharedFunctions.checkMenuName("Inventory Menu");
        sharedFunctions.enterScreen("Load In");
        sharedFunctions.checkMenuName("Load In");

        wait.until(ExpectedConditions.visibilityOfElementLocated(itemsListviewLocator));
        WebElement listView = driver.findElement(itemsListviewLocator);
        int pressX = driver.manage().window().getSize().width/2;
        int topY = listView.getLocation().getY();
        int bottomY = topY + listView.getSize().height;

        while(loadInPacksToCount>countedPacks.size()) {
            List<MobileElement> itemBox = driver.findElements(itemBoxLocator);
            for(int i = 0;i<itemBox.size();i++){


           if(!sharedFunctions.subelementExistsInElement(itemTextLocator,itemBox.get(i)) || !sharedFunctions.subelementExistsInElement(itemCurrerntQtyLocator,itemBox.get(i)))
           {
               TouchAction touchAction = new TouchAction(driver);
               touchAction.longPress(PointOption.point(pressX,bottomY-1)).waitAction(new WaitOptions().withDuration(Duration.ofMillis(400))).moveTo(PointOption.point(pressX,bottomY-16)).release().perform();


           }
           else {

               //region reading available items and their data [Contains sub-region : changing item quantities]
               List<String> packToAdd = new ArrayList<String>();
               String itemText = itemBox.get(i).findElement(itemTextLocator).getText();
               String itemActualCode = itemText.split("-")[0];
               String itemActualName = itemText.split("-")[1];
               String itemActualUOM = itemText.split("-")[2];
               String packDefinition = itemActualCode+"-"+itemActualUOM;
//             String itemOriginalQty = itemBox.get(i).findElement(itemOrigninalQtyLocator).getText();
               String itemActualCurrentQty = itemBox.get(i).findElement(itemCurrerntQtyLocator).getText().replace(",","");

                packToAdd.add(itemActualCode);
                packToAdd.add(itemActualUOM);
                packToAdd.add(itemActualCurrentQty);


               //region Changing item Quantoties
               if(loadInChangedItems.containsKey(packDefinition) ){
                   itemBox.get(i).click();
                   String qty = loadInChangedItems.get(packDefinition)[0];
                   packToAdd.add(qty);
                   packToAdd.add(loadInChangedItems.get(packDefinition)[1]);
                   packToAdd.add(loadInChangedItems.get(packDefinition)[2]);
                   String focusable = driver.findElement(itemChangeCurrentQtyLocator).getAttribute("focused");
                   softAssert.assertEquals(focusable, "true", "Not focused");
                   if (!focusable.equals("true")) {
                       driver.findElement(itemChangeCurrentQtyLocator).click();
                   }
                       driver.findElement(itemChangeCurrentQtyLocator).sendKeys(qty);

                    driver.findElement(okButtonChangeQtyLocator).click();
               }
               //endregion

               if(!countedPacks.contains(packToAdd)) {
                   countedPacks.add(packToAdd);
               }
               //endregion

           }

            }
            //region scrolling down to change items in list
            String oldItemText=itemBox.get(itemBox.size()-2).findElement(itemTextLocator).getText();
            TouchAction touchAction = new TouchAction(driver);
            touchAction.longPress(PointOption.point(pressX,bottomY-1)).waitAction(new WaitOptions().withDuration(Duration.ofMillis(400))).moveTo(PointOption.point(pressX,topY-1)).release().perform();
            String newItemText = itemBox.get(itemBox.size()-2).findElement(itemTextLocator).getText();
            //endregion
            //region scrolling when previous scroll wasn't enogh to change items in the screen
            if(newItemText.equals(oldItemText)) {
                int pressX2 =itemBox.get(itemBox.size()-2).getCenter().getX();
                int bottomY2 =itemBox.get(itemBox.size()-2).getCenter().getY();
                touchAction.longPress(PointOption.point(pressX2,bottomY2)).waitAction(new WaitOptions().withDuration(Duration.ofMillis(400))).moveTo(PointOption.point(pressX2,topY)).release().perform();
            }
            //endregion

        }

        if(!countedPacks.containsAll(loadInExpectedItems)|| countedPacks.size()!= loadInExpectedItems.size()){
            Assert.fail("different quantities from the expected load in items");
        }

        driver.findElement(saveButtonLocator).click();
        if (loadInChangedItems.size()>0){
                    sharedFunctions.checkMenuName("Sales");

//Summary Logic to be inserted Later
/*
                    HashMap<String,String[]> packToAdd = new HashMap<String, String[]>();
            (new WebDriverWait(driver, 10))
                    .until(ExpectedConditions.
                            visibilityOfElementLocated(invoiceItemsListviewLocator));
            int discrepancyInvoicePacksTocount = loadInChangedItems.size();
            pressX = driver.manage().window().getSize().width/2;
            topY = listView.getLocation().getY();
            bottomY = topY + listView.getSize().height;
*/

            driver.findElement(saveSalesSummaryLocator).click();

        }
        driver.findElement(yesSaveLocator).click();
        driver.navigate().back();
    }

}
