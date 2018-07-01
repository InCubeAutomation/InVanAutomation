package visit_customer;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import read_data_excel.ReadData;
import shared_functions.AddItem;
import shared_functions.SharedFunctions;
import testng_config_methods.TestNGConfig;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class Returns extends TestNGConfig {
    ReadData readData = new ReadData();
    SharedFunctions sharedFunctions = new SharedFunctions();
    AddItem addItem = new AddItem();
    By returnsMenuLocator = By.id("menu_image_2");
    By saveImageButtonLocator = By.id("action_save");
    By moreOptionsLocator = By.xpath("//android.widget.ImageView[@content-desc='More options']");
    By saveTextButtonLocaor = By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.ListView/android.widget.LinearLayout/android.widget.RelativeLayout/android.widget.TextView[@text='Save']");
    By summarySaveButtonLocator = By.id("btn_ok_summary");
    By yesSaveLocator = By.id("button1");
    By summaryNetTotalLocator = By.id("tv_net_total_summary");
    BigDecimal returnsActualValue = new BigDecimal("0.00");
    List<List<String>> itemsData = new ArrayList<List<String>>();
    BigDecimal itemAmount = new BigDecimal("0.00");
    BigDecimal returnsValue = new BigDecimal("0.00");


    @Parameters({"filePath"})
    @Test
    public void returns (String filePath) {
        itemsData = readData.readData(filePath, "Returns");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("#,###.###",symbols);
        df.setParseBigDecimal(true);
        df.setRoundingMode(RoundingMode.HALF_UP);
        sharedFunctions.enterScreen("Return");
        sharedFunctions.getMenuName("Return");

        for (int i = 0; i < itemsData.size(); i++) {
            itemAmount = AddItem.addItem(itemsData.get(i).get(0), itemsData.get(i).get(1), itemsData.get(i).get(2),true,true,false,itemsData.get(i).get(3),itemsData.get(i).get(4),itemsData.get(i).get(5));
            returnsValue = returnsValue.add(itemAmount);
            itemAmount = BigDecimal.ZERO;
        }
        if (sharedFunctions.elementExists(saveImageButtonLocator)) {
            driver.findElement(saveImageButtonLocator).click();
        } else {
            driver.findElement(moreOptionsLocator).click();
            driver.findElement(saveTextButtonLocaor).click();
        }
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.
                        visibilityOfElementLocated(summaryNetTotalLocator));
        try {
            returnsActualValue = (BigDecimal) df.parse(driver.findElement(summaryNetTotalLocator).getText().replace(" ",""));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(returnsValue,returnsActualValue);
        driver.findElement(summarySaveButtonLocator).click();
        driver.findElement(yesSaveLocator).click();

    }

}
