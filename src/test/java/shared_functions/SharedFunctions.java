package shared_functions;

import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import sqlite_access.EmployeeKeys;
import testng_config_methods.TestNGConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class SharedFunctions extends TestNGConfig {

    public void enterStorekeeperPassword(){
        if(elementExists(By.id("parentPanel"))) {
            String inventoryOperationsPassword = "123";
            By passwordLocator = By.id("txt_key");
            By enterStorekeeperPasswordTextLocator = By.id("lblConfirmationMsg");
            By okKey = By.id("btn_key_ok");

            softAssert.assertTrue(driver.findElement(enterStorekeeperPasswordTextLocator).getText().equals("Enter Store Keeper Password:"), "Wrong Message");
            driver.findElement(passwordLocator).sendKeys(inventoryOperationsPassword);
            driver.findElement(okKey).click();
        }
    }

    public void enterScreen(String menuName){
        By firstMenuLocator = By.id("menu_name_1");
        boolean swipeLeft = true;
        By menuViewLocator = By.xpath("//ancestor::*[*[@text='"+menuName+"']]");
        TouchAction touchAction = new TouchAction(driver);
        while (!elementExists(menuViewLocator) && swipeLeft){
            String firstMenuNameBefore = driver.findElement(firstMenuLocator).getText();
            swipe("left");
            String firstMenuNameAfter = driver.findElement(firstMenuLocator).getText();
            if(firstMenuNameBefore.equals(firstMenuNameAfter)){
                swipeLeft = false;
            }
        }

        while (!elementExists(menuViewLocator) && !swipeLeft){
            swipe("right");
        }

        int xmenuViewCoords   = driver.findElement(menuViewLocator).getLocation().getX();
        int ymenuViewCoords   = driver.findElement(menuViewLocator).getLocation().getY();
        int menuViewWidth =  driver.findElement(menuViewLocator).getSize().getWidth();
        int menuViewHeight =  driver.findElement(menuViewLocator).getSize().getHeight();
        touchAction.press(PointOption.point(xmenuViewCoords+menuViewWidth/2,ymenuViewCoords+menuViewHeight/2)).release().perform();
    }

    public void checkMenuName(String menuName){
        By menuNameTextLocator;
                if (AndroidVersion.equals("7.0") || AndroidVersion.equals("7.1.1") || AndroidVersion.equals("8.0")){
                    menuNameTextLocator = By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.view.ViewGroup/android.widget.TextView[@text='"
                        + menuName + "']");
                } else {
                    menuNameTextLocator = By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.view.View/android.widget.TextView[@text='"
                + menuName + "']");
                        }

                    wait.until(ExpectedConditions.visibilityOfElementLocated(menuNameTextLocator));

        softAssert.assertEquals(driver.findElement(menuNameTextLocator).getText(), menuName, "Wrong Menu");
    }

    public String getMenuName(){
        By menuNameTextLocator;
        if (AndroidVersion.equals("7.0") || AndroidVersion.equals("7.1.1") || AndroidVersion.equals("8.0")){
            menuNameTextLocator = By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.view.ViewGroup/android.widget.TextView");
        } else {
            menuNameTextLocator = By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.view.View/android.widget.TextView");
        }

        wait.until(ExpectedConditions.visibilityOfElementLocated(menuNameTextLocator));
        String menuName = driver.findElement(menuNameTextLocator).getText();
        return menuName;
    }

    public void scrollUp(){
        int pressX = driver.manage().window().getSize().width / 3;
        // 4/5 of the screen as the bottom finger-press point
        int bottomY = driver.manage().window().getSize().height * 31/33;
        // just non zero point, as it didn't scroll to zero normally
        int topY = driver.manage().window().getSize().height  * 1/5 ;
//        touchAction.longPress(pressX,bottomY).moveTo(pressX,topY).perform();
        TouchAction touchAction = new TouchAction(driver);
        touchAction.longPress(PointOption.point(pressX,topY)).moveTo(PointOption.point(pressX,bottomY)).
                waitAction(new WaitOptions().withDuration(Duration.ofSeconds(1))).release().perform();
    }

    public void scrollDown(){
        int pressX = driver.manage().window().getSize().width / 2;
        // 4/5 of the screen as the bottom finger-press point
         int bottomY = driver.manage().window().getSize().height * 4/5;
        // just non zero point, as it didn't scroll to zero normally
          int topY = driver.manage().window().getSize().height  * 4/33 ;
//        touchAction.longPress(pressX,bottomY).moveTo(pressX,topY).perform();
        TouchAction touchAction = new TouchAction(driver);
        touchAction.longPress(PointOption.point(pressX,bottomY)).moveTo(PointOption.point(pressX,topY)).
                waitAction(new WaitOptions().withDuration(Duration.ofSeconds(1))).release().perform();
    }

    public Boolean elementExists(By elementLocator) {
        try {
            driver.findElement(elementLocator);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }
    public Boolean elementExistsWithWait (By elementLocator) {
        driver.manage().timeouts().implicitlyWait(4, TimeUnit.SECONDS);
        try {
            driver.findElement(elementLocator);
            return true;
        } catch (NoSuchElementException e){
        return false;
        }finally {
            driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
        }
    }

    public Boolean subelementExistsInElement(By subelementLocator,MobileElement element) {
        try{
             element.findElement(subelementLocator);
             return true;
        } catch (NoSuchElementException e){
            return false;
        }
    }


    public void swipe(String direction){
        int rightX = driver.manage().window().getSize().width * 31/32;
        int pressY = driver.manage().window().getSize().height / 2;
        int leftX = driver.manage().window().getSize().width  * 1/32 ;
//        touchAction.longPress(pressX,bottomY).moveTo(pressX,topY).perform();
        TouchAction touchAction = new TouchAction(driver);
        if (direction.equals("left")) {
            touchAction.longPress(PointOption.point(rightX, pressY)).moveTo(PointOption.point(leftX, pressY)).
                    waitAction(new WaitOptions().withDuration(Duration.ofSeconds(1))).release().perform();
        }
        else if (direction.equals("right")) {
            touchAction.longPress(PointOption.point(leftX, pressY)).moveTo(PointOption.point(rightX, pressY)).
                    waitAction(new WaitOptions().withDuration(Duration.ofSeconds(1))).release().perform();
        }
    }

    public void pickDateFromAndroid7Calendar(String dateToSelect){
        By calendarOkuttonLocator = By.id("button1");
        By calendarNextMonthLocator = By.id("next");
        By calendarPreviousMonthLocator = By.id("prev");
        By calendarYearPickerLocator = By.id("date_picker_header_year");
        By selectedDateLocator = By.id("date_picker_header_date");


        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        LocalDate convertedExpiryDate = LocalDate.parse(dateToSelect, dtf);
//            int day  = convertedExpiryDate.getDayOfMonth();
        int month = convertedExpiryDate.getMonthValue();
        int year = convertedExpiryDate.getYear();
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.
                        visibilityOfElementLocated(selectedDateLocator));
        String calendarCurrentDayAndMonth = driver.findElement(selectedDateLocator).getText().split(",")[0];
        String calendarCurrentDay = calendarCurrentDayAndMonth.split(" ")[1];
        String calendarCurrentMonth = calendarCurrentDayAndMonth.split(" ")[0];
        switch (calendarCurrentMonth){
            case "Jan":
                calendarCurrentMonth = "January";
                break;
            case "Feb":
                calendarCurrentMonth = "February";
                break;
            case "Mar":
                calendarCurrentMonth = "March";
                break;
            case "Apr":
                calendarCurrentMonth = "April";
                break;
            case "May":
                calendarCurrentMonth = "May";
                break;
            case "Jun":
                calendarCurrentMonth = "June";
                break;
            case "Jul":
                calendarCurrentMonth = "July";
                break;
            case "Aug":
                calendarCurrentMonth = "August";
                break;
            case "Sep":
                calendarCurrentMonth = "August";
                break;
            case "September":
                calendarCurrentMonth = "August";
                break;
            case "Oct":
                calendarCurrentMonth = "October";
                break;
            case "Nov":
                calendarCurrentMonth = "November";
                break;
            case "Dec":
                calendarCurrentMonth = "December";
                break;
        }

        if(calendarCurrentDay.length()== 1)
        {
            calendarCurrentDay = "0" + calendarCurrentDay;
        }
        LocalDate currentDate = LocalDate.parse(calendarCurrentMonth + " " + calendarCurrentDay + " " +
                driver.findElement(calendarYearPickerLocator).getText(), DateTimeFormatter.ofPattern("MMMM dd yyyy"));
//            int currentday  = currentDate.getDayOfMonth();
        int currentmonth = currentDate.getMonthValue();
        int currentyear = currentDate.getYear();

        if (year != currentyear) {
            driver.findElement(calendarYearPickerLocator).click();
            By yearLocator = By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.LinearLayout/" +
                    "android.widget.FrameLayout/android.widget.FrameLayout/android.widget.DatePicker/android.widget.LinearLayout/android.widget.ViewAnimator/" +
                    "android.widget.ListView/android.widget.TextView[@text='" +
                    year + "']");
            driver.findElement(yearLocator).click();
        }

        if (month > currentmonth) {
            for (int i = currentmonth; i < month; i++) {
                driver.findElement(calendarNextMonthLocator).click();
            }
        } else if (month < currentmonth) {
            for (int i = currentmonth; i > month; i--) {
                driver.findElement(calendarPreviousMonthLocator).click();
            }
        }
        driver.findElementByAccessibilityId(dateToSelect).click();
        driver.findElement(calendarOkuttonLocator).click();
    }
        public void takeScreenshot(String filePath){
            File srcFiler = driver.getScreenshotAs(OutputType.FILE);
            File file = new File(filePath);
            File directory = file.getParentFile();
            if (!directory.exists()) {
                try {
                    Files.createDirectories(Paths.get(filePath));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(file.exists()){
                file.delete();
            }
            try {
                FileUtils.copyFile(srcFiler, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!file.canRead()){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    public void enterKey() throws SQLException, IOException {
        int keyTypeID = 0;
        String  Keyvalue = null;
        By keyTypeLocator = By.id("lbl_keyType");
        By keyTextLocator = By.id("txt_key");
        By employeeKeyOKButtonLocator =By.id("btn_employeekey_ok");
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.
                        visibilityOfElementLocated(keyTypeLocator));
        String keyType = driver.findElement(keyTypeLocator).getText().trim();
        switch (keyType){
            case  "Sell on hold customer key" :
                keyTypeID = 1;
                break;
            case  "Sell customer violated payment term key" :
                keyTypeID = 2;
                break;
            case  "Sell customer exceeded credit limit key" :
                keyTypeID = 3;
                break;
            case  "Edit price key" :
                keyTypeID = 4;
                break;
            case  "Edit discount key" :
                keyTypeID = 5;
                break;
            case  "Edit FOC key" :
                keyTypeID = 6;
                break;
            case  "Void payment key" :
                keyTypeID = 7;
                break;
            case  "Skip promotion key" :
                keyTypeID = 8;
                break;
            case  "Return good items key" :
                keyTypeID = 9;
                break;
            case  "Exchange items between vans key" :
                keyTypeID = 10;
                break;
            case  "Void invoice key" :
                keyTypeID = 11;
                break;
            case  "Visit customer out off route key" :
                keyTypeID = 12;
                break;
            case  "Visit customer without barcode scanning key" :
                keyTypeID = 13;
                break;
            case  "Change currency exchange rate for customer key" :
                keyTypeID = 14;
                break;
            case  "Change currency exchange rate permanently key" :
                keyTypeID = 15;
                break;
            case  "Allow unlimited FOC key" :
                keyTypeID = 16;
                break;
            case  "Change System Date Key" :
                keyTypeID = 17;
                break;
            case  "Create order for customer exceeded credit limit key" :
                keyTypeID = 18;
                break;
            case  "Skip customer key" :
                keyTypeID = 19;
                break;
            case  "Allow Exchange Key" :
                keyTypeID = 20;
                break;
            case  "Order to customer violated payment terms key" :
                keyTypeID = 21;
                break;
            case  "Allow Partial Delivery In Sales Order Key" :
                keyTypeID = 22;
                break;
            case  "Create order for on hold customer key" :
                keyTypeID = 23;
                break;
            case  "Allow Customer Exceed Containers Limit Key" :
                keyTypeID = 24;
                break;
            case  "Edit price on return key" :
                keyTypeID = 25;
                break;
            case  "Skip promotion Individually key" :
                keyTypeID = 26;
                break;
            case  "Create Return Order For OnHold Customer Key" :
                keyTypeID = 27;
                break;
            case  "Create Return Order For Customer Violated Payment Term Key" :
                keyTypeID = 28;
                break;
            case  "Create exchange order for customer exceeded credit limit key" :
                keyTypeID = 29;
                break;
            case  "Create exchange order for customer violated payment term key" :
                keyTypeID = 30;
                break;
            case  "Create exchange order for onHold customer key" :
                keyTypeID = 31;
                break;
            case  "Allow reschedule delivery key" :
                keyTypeID = 32;
                break;
            case  "Allow reject delivery key" :
                keyTypeID = 33;
                break;
            case  "Apply Cash Discount Key" :
                keyTypeID = 34;
                break;
            case  "Apply Supervisor Discount Key" :
                keyTypeID = 35;
                break;
            case  "allow Credit Sales for Cash Customers" :
                keyTypeID = 36;
                break;
            case  "End Customer Visit Without Barcode Scanning Key" :
                keyTypeID = 37;
                break;
            case  "Print Original Invoice Key" :
                keyTypeID = 38;
                break;
            case  "Skip must sell items key" :
                keyTypeID = 39;
                break;
            case  "Increase num of invoice due dates key" :
                keyTypeID = 40;
                break;
            case  "Allow Edit Discount With Password On Returns" :
                keyTypeID = 41;
                break;
            case  "Allow Visit Customer Out Of Geo Fencing Key" :
                keyTypeID = 42;
                break;
            case  "Allow Add New Customers Key" :
                keyTypeID = 43;
                break;
            case  "Allow Edit Price To Be Out Of Range Key" :
                keyTypeID = 44;
                break;
            case  "Apply Key To Edit Header Discount" :
                keyTypeID = 45;
                break;
            case  "Apply Key To Sell Customers Exceeding Bill Number" :
                keyTypeID = 46;
                break;
            case  "Apply Key To Process Exchange On Hold Customer" :
                keyTypeID = 47;
                break;
            case  "Apply Key To Process Exchange Customer Violated Payment Term" :
                keyTypeID = 48;
                break;
            case  "Apply Key To Process Exchange Customer Exceeds Credit Limit" :
                keyTypeID = 49;
                break;
            case  "COS Key" :
                keyTypeID = 50;
                break;
            case  "Allow Fixed Incentive Key" :
                keyTypeID = 51;
                break;
            case  "Allow Variable Incentive Key" :
                keyTypeID = 52;
                break;
            case  "Allow Prison FOC Key" :
                keyTypeID = 53;
                break;
            case  "Allow Guarantees FOC Key" :
                keyTypeID = 54;
                break;
            case  "Sell Customer Exceeds Commitment Limit" :
                keyTypeID = 55;
                break;
            case  "Allow Returns Key" :
                keyTypeID = 56;
                break;
            case  "Allow Giving Unlimited FOC For OnHold Customer with Password" :
                keyTypeID = 57;
                break;
            case  "Allow Giving Unlimited FOC If The Customer Violat Payment Term with Password" :
                keyTypeID = 58;
                break;
            case  "Allow Giving Unlimited FOC If The Customer Exceeds Credit Limit with Password" :
                keyTypeID = 59;
                break;
            case  "Allow Giving Unlimited FOC If The Customer Exceeds Bill Number with Password" :
                keyTypeID = 60;
                break;
            case  "Apply Key To Edit Collection Discount Percentage" :
                keyTypeID = 61;
                break;
        }
        EmployeeKeys employeeKeys = new EmployeeKeys();
        Keyvalue = employeeKeys.getKey(keyTypeID);
        driver.findElement(keyTextLocator).sendKeys(Keyvalue);
        if(driver.isKeyboardShown()){
            driver.hideKeyboard();
        }
        driver.findElement(employeeKeyOKButtonLocator).click();
    }

    public void sendKeys(String keysToSend, By elementLocator) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(elementLocator)).sendKeys(keysToSend);
        if (driver.isKeyboardShown()) {
            driver.hideKeyboard();
        }
    }
    public String convertImageToText(String filePath){
            String result = null;
            File imageFile = new File(filePath);
            ITesseract instance = new Tesseract();
            try {
                result = instance.doOCR(imageFile).toLowerCase().replaceAll("\n"," ");

            } catch (TesseractException e) {
                System.err.println(e.getMessage());
            }

            return result;
    }

    public void skipAppiumActivityOverActivityHanging(long milliSecondstoWait){
        By InVanAppSwitchLocator = By.xpath("//android.widget.TextView[@content-desc=\"InVan In Van\"]");
        try {
            Thread.sleep(milliSecondstoWait);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        driver.pressKey(new KeyEvent(AndroidKey.APP_SWITCH));
        try {
            Thread.sleep(milliSecondstoWait);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        wait.until(ExpectedConditions.visibilityOfElementLocated(InVanAppSwitchLocator));
        driver.findElement(InVanAppSwitchLocator).click();
    }
}
