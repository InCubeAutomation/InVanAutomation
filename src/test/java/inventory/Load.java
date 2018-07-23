package inventory;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import read_data_excel.ReadData;
import shared_functions.AddItem;
import shared_functions.SharedFunctions;
import testng_config_methods.TestNGConfig;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class Load extends TestNGConfig {

    SharedFunctions sharedFunctions = new SharedFunctions();
    ReadData readData = new ReadData();
    By saveImageButtonLocator = By.id("action_save");
    By saveTextButtonLocaor = By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.ListView/android.widget.LinearLayout/android.widget.RelativeLayout/android.widget.TextView[@text='Save']");
    By moreOptionsLocator = By.xpath("//android.widget.ImageView[@content-desc=\"More options\"]");
    By yesSaveLocator = By.id("button1");
    BigDecimal loadValue = new BigDecimal("0.00");
    BigDecimal itemAmount = new BigDecimal("0.00");
    List<List<String>> itemsData = new ArrayList<List<String>>();

    @Parameters({"filePath"})
    @Test
    public void load(String filePath) {

        itemsData = readData.readData(filePath, "Load");

        sharedFunctions.enterScreen("Inventory");
        sharedFunctions.getMenuName("Inventory Menu");
        sharedFunctions.enterScreen("Load");
        sharedFunctions.enterStorekeeperPassword();
        sharedFunctions.getMenuName("Load");

        for (int i = 0; i < itemsData.size(); i++) {
            itemAmount = AddItem.addItem(itemsData.get(i).get(0), itemsData.get(i).get(1), itemsData.get(i).get(2),false,false,false,"","","");
            loadValue = loadValue.add(itemAmount);
            itemAmount = BigDecimal.ZERO;
        }

        if (sharedFunctions.elementExists(saveImageButtonLocator)) {
            driver.findElement(saveImageButtonLocator).click();
        } else {
            driver.findElement(moreOptionsLocator).click();
            driver.findElement(saveTextButtonLocaor).click();
        }

            driver.findElement(yesSaveLocator).click();
            sharedFunctions.getMenuName("Inventory Menu");
            driver.navigate().back();
            sharedFunctions.getMenuName("Main Menu");


        }
    }

