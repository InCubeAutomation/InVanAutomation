package inventory;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import shared_functions.AddItem;
import read_data.ReadInventoryData;
import shared_functions.SharedFunctions;
import testng_config_methods.TestNGConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Load extends TestNGConfig {

    SharedFunctions sharedFunctions = new SharedFunctions();
    ReadInventoryData readInventoryData = new ReadInventoryData();
    AddItem addItem = new AddItem();
    By inventoryMenuLocator = By.id("menu_image_6");
    By loadMenuLocator = By.id("menu_image_3");

    float loadValue =0;
    float itemAmount;
    List<List<String>> itemsData = new ArrayList<List<String>>();

    @Parameters({"filePath"})
    @Test
    public void load(String filePath) {

        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.
                        visibilityOfElementLocated(inventoryMenuLocator));
        driver.findElement(inventoryMenuLocator).click();
        sharedFunctions.getMenuName("Inventory Menu");
        driver.findElement(loadMenuLocator).click();
        sharedFunctions.enterStorekeeperPassword();
        sharedFunctions.getMenuName("Load");
        try {
            itemsData = readInventoryData.readInventoryData(filePath, "Load");
        } catch (Throwable throwable) {// In case exception happen
            Assert.fail("Read Item Data failed, " +
                    "please check log: \n" + throwable.getMessage());
        }
            for (int i=0;i<itemsData.size();i++)
            {
                itemAmount = addItem.addItem(itemsData.get(i).get(0),itemsData.get(i).get(1),itemsData.get(i).get(2)) ;
                loadValue=loadValue+itemAmount;
                itemAmount=0;
            }



//        itemAmount =  addItem.addItem("4001","Outer","5");
//        loadValue=loadValue+itemAmount;
//        itemAmount = addItem.addItem("4002","Carton","9");
//        loadValue=loadValue+itemAmount;
//        itemAmount = addItem.addItem("4002","Outer","9");
//        loadValue=loadValue+itemAmount;
{
    System.out.println("MHND");
}
    }
}
