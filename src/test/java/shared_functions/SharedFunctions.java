package shared_functions;

import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import testng_config_methods.TestNGConfig;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
        TouchAction touchAction = new TouchAction(driver);
        By menuNameTextLocator = By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.support.v4.view.ViewPager/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.LinearLayout/android.widget.LinearLayout/android.widget.TextView[@text='" +
                menuName + "']");
        while (!elementExists(menuNameTextLocator)){
            swipe("left");
        }
        int xTextCoords   = driver.findElement(menuNameTextLocator).getLocation().getX();
        int yTextCoords   = driver.findElement(menuNameTextLocator).getLocation().getY();
        int textSize =  driver.findElement(menuNameTextLocator).getSize().getWidth();
        touchAction.press(PointOption.point(xTextCoords+textSize/2,yTextCoords-20)).release().perform();
    }

    public void getMenuName(String menuName){
        By menuNameTextLocator;
                if (AndroidVersion.equals("7.0") || AndroidVersion.equals("7.1.1") || AndroidVersion.equals("8.0")){
                    menuNameTextLocator = By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.view.ViewGroup/android.widget.TextView[@text='"
                        + menuName + "']");}
                        else {
                    menuNameTextLocator = By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.view.View/android.widget.TextView[@text='"
                + menuName + "']");}
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.
                        visibilityOfElementLocated(menuNameTextLocator));

        Assert.assertEquals(driver.findElement(menuNameTextLocator).getText(), menuName, "Wrong Menu");
    }

    public void scrollUp(){
        int pressX = driver.manage().window().getSize().width / 3;
        // 4/5 of the screen as the bottom finger-press point
        int bottomY = driver.manage().window().getSize().height * 31/33;
        // just non zero point, as it didn't scroll to zero normally
        int topY = driver.manage().window().getSize().height  * 1/5 ;
//        touchAction.longPress(pressX,bottomY).moveTo(pressX,topY).perform();
        TouchAction touchAction = new TouchAction(driver);
        touchAction.longPress(PointOption.point(pressX,topY)).moveTo(PointOption.point(0,bottomY-topY)).
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
        touchAction.longPress(PointOption.point(pressX,bottomY)).moveTo(PointOption.point(0,topY-bottomY)).
                waitAction(new WaitOptions().withDuration(Duration.ofSeconds(1))).release().perform();
    }

    public Boolean elementExists(By elementLocator) {
        try {
            driver.findElement(elementLocator);
            return true;
        } catch (NoSuchElementException e){
        return false;
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
            touchAction.longPress(PointOption.point(rightX, pressY)).moveTo(PointOption.point(leftX-rightX, 0)).
                    waitAction(new WaitOptions().withDuration(Duration.ofSeconds(1))).release().perform();
        }
        else if (direction.equals("right")) {
            touchAction.longPress(PointOption.point(leftX, pressY)).moveTo(PointOption.point(rightX-leftX, 0)).
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
        }

        if (month < currentmonth) {
            for (int i = currentmonth; i > month; i--) {
                driver.findElement(calendarPreviousMonthLocator).click();
            }
        }
        driver.findElementByAccessibilityId(dateToSelect).click();
        driver.findElement(calendarOkuttonLocator).click();
    }

}
