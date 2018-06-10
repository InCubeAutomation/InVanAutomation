package inventory;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import read_data.ReadData;
import shared_functions.AddItem;
import shared_functions.SharedFunctions;
import testng_config_methods.TestNGConfig;

import java.util.ArrayList;
import java.util.List;


public class Load extends TestNGConfig {

    SharedFunctions sharedFunctions = new SharedFunctions();
    ReadData readData = new ReadData();
    By saveImageButtonLocator = By.id("action_save");
    By saveTextButtonLocaor = By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.ListView/android.widget.LinearLayout/android.widget.RelativeLayout/android.widget.TextView[@text='Save']");
    By moreOptionsLocator = By.xpath("//android.widget.ImageView[@content-desc=\"More options\"]");
    By yesSaveLocator = By.id("button1");
    float loadValue = 0;
    float itemAmount;
    List<List<String>> itemsData = new ArrayList<List<String>>();

    @Parameters({"filePath"})
    @Test
    public void load(String filePath) {

        try {
            itemsData = readData.readData(filePath, "Load");
        } catch (Throwable throwable) {// In case exception happen
            Assert.fail("Read Item Data failed, " +
                    "please check log: \n" + throwable.getMessage());
        }

        sharedFunctions.enterScreen("Inventory");
        sharedFunctions.getMenuName("Inventory Menu");
        sharedFunctions.enterScreen("Load");
        sharedFunctions.enterStorekeeperPassword();
        sharedFunctions.getMenuName("Load");

        for (int i = 0; i < itemsData.size(); i++) {
            itemAmount = AddItem.addItem(itemsData.get(i).get(0), itemsData.get(i).get(1), itemsData.get(i).get(2),false,false,"","","");
            loadValue = loadValue + itemAmount;
            itemAmount = 0;
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

