package visit_customer;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;
import shared_functions.SharedFunctions;
import testng_config_methods.TestNGConfig;

public class EndVisit extends TestNGConfig {
SharedFunctions sharedFunctions = new SharedFunctions();
By customersListView = By.id("lv_customerlist") ;

    @Test
    public void endVisit() {
        driver.navigate().back();
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.
                        visibilityOfElementLocated(customersListView));
        if(driver.isKeyboardShown()){
            driver.hideKeyboard();
        }
        driver.navigate().back();
        sharedFunctions.getMenuName("Main Menu");
    }
}
