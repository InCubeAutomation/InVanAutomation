package visit_customer;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidKeyCode;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import read_data_excel.ReadData;
import shared_functions.SharedFunctions;
import testng_config_methods.TestNGConfig;

import java.util.ArrayList;
import java.util.List;


public class Collection  extends TestNGConfig {
    ReadData readData = new ReadData();
    SharedFunctions sharedFunctions = new SharedFunctions();
    By invoiceBoxLocators =By.id("ll_item_selector_bg");
    By invoiceIDLocator = By.id("RVid");
    By checkBoxLocator = By.id("cbxChecked");
    By payInvoicesLocator = By.id("action_pay");
    By addPaymentLocator = By.id("btn_AddP");
    By payAllCashLocator = By.id("btn_PayAllcash");
    By amountTextLocator = By.id("txt_amount");
    By savePaymentLocator = By.id("btn_add");
    By paidAmountLocator = By.id("txt_PaidAmount");
    By okPaidAmountLocator = By.id("button1");
    By paymentTypeLocator = By.id("cbx_type");
    By bankNameLocator = By.id("cbx_bank");
    By branchNameLocator = By.id("cbx_branch");
    By chequeNumberLocator = By.id("txt_cheque");
    By chequeDateLocator = By.id("txt_date");
    By invoiceListViewLocator = By.id("lst_Invoices");
    
    List<List<String>> invoicesToCollect = new ArrayList<List<String>>();
    List<List<String>> paymentmethodsData = new ArrayList<List<String>>();
    String chequeNumber ;
    String chequeDate;

    @Parameters({"filePath"})
    @Test
    public void collection(String filePath) {
        invoicesToCollect = readData.readData(filePath,"Invoices to collect");
        int  numberOfInvoicesToBeCollected =invoicesToCollect.size();
        paymentmethodsData = readData.readData(filePath,"Payment methods");
        int numberOfPayments = paymentmethodsData.size();

        sharedFunctions.enterScreen("Collection");
        sharedFunctions.getMenuName("Collection");
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.
                        visibilityOfElementLocated(invoiceListViewLocator));
        for(int i=0;i<numberOfInvoicesToBeCollected;i++) {
            String invoiceToSelect = invoicesToCollect.get(i).get(0);
            String amountToPay = invoicesToCollect.get(i).get(1);
        List<MobileElement> invoiceBox = driver.findElements(invoiceBoxLocators);
            for (int j = 0; j < invoiceBox.size(); j++) {
                if (invoiceBox.get(j).findElement(invoiceIDLocator).getText().equals("Inv:" + invoiceToSelect)) {
                    if(amountToPay.equals("Full")){
                    invoiceBox.get(j).findElement(checkBoxLocator).click(); }
                    else {
                        invoiceBox.get(j).findElement(invoiceIDLocator).click();
                        driver.pressKeyCode(AndroidKeyCode.KEYCODE_DEL);
                        driver.getKeyboard().sendKeys(amountToPay);
                        driver.findElement(okPaidAmountLocator).click();
                        if (driver.isKeyboardShown()){
                            driver.hideKeyboard();
                        }
                    }
                    break;
                }
            }
        }
        driver.findElement(payInvoicesLocator).click();
        sharedFunctions.getMenuName("Payment Details");
//        "1]"
        for(int i=0;i<numberOfPayments;i++) {
            By bankNameToSelectLocator=null;
            By branchNameToSelectLocator=null;
            String paymentMethod = paymentmethodsData.get(i).get(0);
            String payemntAmount = paymentmethodsData.get(i).get(1);
            By paymentTypeToSelectLocator = By.id(paymentMethod);

            if(!paymentMethod.equals("Cash")) {
                String bankName = paymentmethodsData.get(i).get(2);
                String branchName = paymentmethodsData.get(i).get(3);
                chequeNumber = paymentmethodsData.get(i).get(4);
                chequeDate = paymentmethodsData.get(i).get(5);

                if (AndroidVersion.equals("7.1.1") || AndroidVersion.equals("7.0") || AndroidVersion.equals("8.0")) {
                    bankNameToSelectLocator = By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.ListView/android.widget.CheckedTextView[@text='"
                            + bankName + "']");
                    branchNameToSelectLocator = By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.ListView/android.widget.CheckedTextView[@text='"
                            + branchName + "']");
                } else{
                    bankNameToSelectLocator = By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.ListView/android.widget.CheckedTextView[@text='"
                            + bankName + "']");
                    branchNameToSelectLocator = By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.ListView/android.widget.CheckedTextView[@text='"
                            + branchName + "']");
                }
            }

            driver.findElement(addPaymentLocator).click();
            String focusable = driver.findElement(amountTextLocator).getAttribute("focused");
            softAssert.assertEquals(focusable, "true", "Not focused");
            if (focusable.equals("true")) {
                driver.pressKeyCode(AndroidKeyCode.KEYCODE_DEL);
                driver.getKeyboard().sendKeys(payemntAmount);
                driver.hideKeyboard();
            } else {

                driver.findElement(amountTextLocator).click();
                driver.pressKeyCode(AndroidKeyCode.KEYCODE_DEL);
                driver.getKeyboard().sendKeys(payemntAmount);
                driver.hideKeyboard();
            }
            driver.findElement(paymentTypeLocator).click();
            driver.findElement(paymentTypeToSelectLocator).click();

            if(!paymentMethod.equals("Cash")){
                driver.findElement(bankNameLocator).click();
                driver.findElement(bankNameToSelectLocator).click();
                driver.findElement(branchNameLocator).click();
                driver.findElement(branchNameToSelectLocator).click();
                if(!sharedFunctions.elementExists(chequeDateLocator)){
                    sharedFunctions.scrollDown();
                }
                driver.findElement(chequeNumberLocator).sendKeys(chequeNumber);
                if(driver.isKeyboardShown()) {
                    driver.hideKeyboard();
                }
                driver.findElement(chequeDateLocator).click();
                driver.findElement(chequeDateLocator).click();
                sharedFunctions.pickDateFromAndroid7Calendar(chequeDate);
            }

            driver.findElement(savePaymentLocator).click();
        }
            driver.findElement(payInvoicesLocator).click();
        if(sharedFunctions.elementExists(invoiceListViewLocator)) {
            driver.navigate().back();
        }
            sharedFunctions.getMenuName("Customer Menu");
    }
}
