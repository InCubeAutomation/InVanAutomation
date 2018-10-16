package visit_customer;

import collections.FilterLists;
import collections.SortLists;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import read_data_excel.ReadData;
import shared_functions.AddItem;
import shared_functions.DeleteItem;
import shared_functions.SharedFunctions;
import sqlite_access.*;
import testng_config_methods.TestNGConfig;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Delivery extends TestNGConfig {
    //region import classes
    ReadData readData = new ReadData();
    Configurations configurations = new Configurations();
    SharedFunctions sharedFunctions = new SharedFunctions();
    AddItem addItem = new AddItem();
    DeleteItem deleteItem = new DeleteItem();
    PackQuantities packQuantities = new PackQuantities();
    FilterLists filterLists = new FilterLists();
    Accounts accounts = new Accounts();
    Orders orders = new Orders();
    DocumentSequence documentSequence = new DocumentSequence();
    SortLists sortLists = new SortLists();
    //endregion

    Integer numberOfDigits = 2;
    boolean calculateTaxBeforeDiscount=false;

    //region Define Variables
    List<List<String>> packsData = new ArrayList<List<String>>();
    List<List<String>> salesPacksData = new ArrayList<List<String>>();
    List<List<String>> expectedSalesPacksDataComparison = new ArrayList<List<String>>();
    List<List<String>> partialDeliveryExpectedPacksData = new ArrayList<List<String>>();
    List<List<String>> expectedSalesPacksDataWithoutPromotions = new ArrayList<List<String>>();
    List<List<String>> originalPromotedPacksData = new ArrayList<List<String>>();
    List<List<List<String>>> newPromotedPacksData = new ArrayList<List<List<String>>>();
    List<List<String>> expectedPromotedPacksDataComparison = new ArrayList<List<String>>();
    List<List<String>> packsToBeChangedData = new ArrayList<List<String>>();
    List<String> itemCodesToBeChanged = new ArrayList<String>();
    List<List<String>> countedSalesPacks= new ArrayList<List<String>>();
    List<List<String>> countedPromotionPacks = new ArrayList<List<String>>();
    List<String> expectedSavingOfComplementaryOrderHeaderInSqlite = new ArrayList<String>();
    List<List<String>> expectedSavingOfComplementaryOrderDetailInSqlite = new ArrayList<List<String>>();
    List<String> savedComplementaryOrderHeader = new ArrayList<String>();
    List<List<String>> savedComplementaryOrderDetail = new ArrayList<List<String>>();


    String itemCodestoGetPackQuantities="";
    String complementaryOrderID;
    public static List<List<String>> itemPacksQuantities = new ArrayList<List<String>>();
    public static List<List<String>> changedItemsData = new ArrayList<List<String>>();

    boolean taxable = true;
    Integer noOfItems =0;
    Integer noOfPromotions;
    Integer noOfPromotedItems;
    Integer noOfOriginalSalesItems=0;
    Integer noOfOriginalPromotedItems=0;
    BigDecimal invoiceNetWithoutPromotions = new BigDecimal("0.00");
    BigDecimal expectedNetTotal = new BigDecimal("0.00");
    BigDecimal actualNetTotal = new BigDecimal("0.00");
    BigDecimal itemAmount = new BigDecimal("0.00");
    BigDecimal promotedItemsAmount =  new BigDecimal("0.00");
    BigDecimal partialDeliveryNetTotal = new BigDecimal("0.00");
    BigDecimal salesSummaryNetTotal =  new BigDecimal("0.00");

    Double toleranceValuePercentage=5d;
    Double highCreditValue = 0d;
    Double creditLimit=0D;
    Double balance=0D;
    Double availableCredit=0d;
    boolean availableCreditChecked = false;
    boolean highCreditChecked = false;
    boolean createComplementaryOrder = false;
    boolean stopExecuting = false;
    //endregion

    //region Define Locators
    By orderIDLocator = By.id("tv_order_id");
    By itemBoxLocator = By.id("ll_item_list_bg");
    By salesItemsExpand = By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.TabHost/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.ExpandableListView/android.widget.LinearLayout/android.widget.TextView[@text='Sales Items']");
    By promotedItemsExpand = By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.TabHost/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.ExpandableListView/android.widget.LinearLayout/android.widget.TextView[@text='Promoted Items']");
    By itemCodeLocator = By.id("lbl_item_code");
    By itemPackTypeLocator = By.id("lbl_item_uom");
    By itemQtyLocator = By.id("lbl_item_qty");
    By itemPriceLocator = By.id("lbl_item_price");
    By itemDiscountocator = By.id("lbl_item_disc");
    By itemTaxLocator = By.id("lbl_item_tax");
    By itemNetLocator = By.id("lbl_item_total");
    By itemsListviewLocator = By.id("expand_listView");
    By addItemButtonLocator = By.id("btn_add_item");
    By itemListSaveButtonLocator = By.id("btn_save_order");
    By summarySaveButtonLocator = By.id("btn_ok_order_summary");
    By saveImageButtonLocator = By.id("action_save");
    By saveTextButtonLocaor = By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.ListView/android.widget.LinearLayout/android.widget.RelativeLayout/android.widget.TextView[@text='Save']");
    By moreOptionsLocator = By.xpath("//android.widget.ImageView[@content-desc=\"More options\"]");
    By alertMessageLocator = By.id("message");
    By alertTitleLocator = By.id("alertTitle");
    By yesSaveLocator = By.id("button1");
    By itemListNetTotalLocator = By.id("txt_net_total");
    By btnTakePromotionLocator = By.id("btn_take_promo");
    By saveItemsListLocator = By.id("btn_save_order");
    By payAllCashButtonLocator = By.id("btn_PayAllcash");
    By savePaymentsLocator = By.id("action_pay");
    By salesSummaryNetTotalLocator = By.id("tv_net_total_order_summary");
    By okButtonLocator = By.xpath("//*[@text='OK']");
    //endregion



    @Parameters({"outletCode","orderIDToDeliver","salesMode","filePath"})
    @Test
    public void delivery(String outletCode, String orderIDToDeliver,String salesMode, String filePath ) throws IOException, SQLException, ParseException {
        //region Setting Decimal Format
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("#,###.#########",symbols);
        df.setParseBigDecimal(true);
        df.setRoundingMode(RoundingMode.HALF_UP);
        //endregion
       // calculateTaxBeforeDiscount = configurations.customerConfigurationValue("CalculateTaxBeforeDiscount",outletCode,EmployeeCode,"").toLowerCase().equals("true");
        createComplementaryOrder = Boolean.valueOf(configurations.customerConfigurationValue("CreateComplementaryOrder",outletCode,RouteGeneralData.EmployeeCode,"-1"));
        complementaryOrderID = documentSequence.nextDocumentSequence("Sales Order","-1");
        packsData = readData.readData(filePath,"Delivery");
        noOfItems = packsData.size()-2;

        //region Reading Outlet Account Data
        Double[] accountData = accounts.outletBalanceAndCredit(outletCode);
        creditLimit = accountData[0];
        balance = accountData[1];
        toleranceValuePercentage = accountData[2];
        availableCredit = creditLimit*(1.00+toleranceValuePercentage/100)-  balance;
        highCreditValue = creditLimit*(1.00-toleranceValuePercentage/100)-  balance;
        //endregion


        //region Fill and Distribute Expected Items Data
        for (int i = 0; i< noOfItems; i++){
            String itemCode =packsData.get(i).get(0);
            String packType = packsData.get(i).get(1);
            if(packsData.get(i).get(4).equals("Promotion") || packsData.get(i).get(4).equals("Prom")){
                originalPromotedPacksData.add(packsData.get(i));
                List<String> packToAdd = new ArrayList<>();
                packToAdd.add(itemCode); //0-item code
                packToAdd.add(packType); //1-pack type
                packToAdd.add(packsData.get(i).get(2)); //2-order quantity
                expectedPromotedPacksDataComparison.add(packToAdd);

            } else {

                if (!packsData.get(i).get(2).equals("0")) {
                    salesPacksData.add(packsData.get(i));
                    List<String> packToAdd = new ArrayList<>();
                    List<String> packToAddWithoutPromotions = new ArrayList<>();

                    packToAdd.add(itemCode); //0-item code
                    packToAddWithoutPromotions.add(itemCode); //0-item code
                    itemCodestoGetPackQuantities = itemCodestoGetPackQuantities+"'"+itemCode+"',";

                    packToAdd.add(packsData.get(i).get(1)); //1-pack type
                    packToAddWithoutPromotions.add(packsData.get(i).get(1)); //1-pack type

                    String excelQty = packsData.get(i).get(2);
                    packToAdd.add(excelQty); //2-order quantity
                    packToAddWithoutPromotions.add(excelQty); //2-order quantity
                    BigDecimal quantity = new BigDecimal(excelQty);

                    BigDecimal excelPrice  = new BigDecimal (String.valueOf(df.parse(packsData.get(i).get(4)))).setScale(numberOfDigits,RoundingMode.HALF_UP);
                    packToAdd.add(String.valueOf(excelPrice)); //3-price
                    packToAddWithoutPromotions.add(String.valueOf(excelPrice)); //3-price

                    BigDecimal discount = new BigDecimal(packsData.get(i).get(5).split("-")[0]);
                    String discountType = packsData.get(i).get(5).split("-")[1];

                    BigDecimal promotedDiscount = new BigDecimal(packsData.get(i).get(6).split("-")[0]);
                    String promotedDiscountType = packsData.get(i).get(6).split("-")[1];

                    BigDecimal tax = new BigDecimal(packsData.get(i).get(7));
                    BigDecimal calculatedPromotedDiscount = new BigDecimal("0.00");
                    BigDecimal calculatedDiscount = new BigDecimal("0.00");
                    BigDecimal wholeDiscount = new BigDecimal("0.00");
                    BigDecimal calculatedTax = new BigDecimal("0.00");
                    BigDecimal calculatedTaxWithoutPromotions = new BigDecimal("0.00");
                    BigDecimal calculatedItemNet = new BigDecimal("0.00");
                    BigDecimal calculatedItemNetWithoutPromotions = new BigDecimal("0.00");


                    if (discountType.equals("%")) {
                        calculatedDiscount = quantity.multiply(excelPrice).setScale(numberOfDigits, RoundingMode.HALF_UP).
                                multiply(discount).divide(new BigDecimal("100.00"),numberOfDigits,RoundingMode.HALF_UP).setScale(numberOfDigits, RoundingMode.HALF_UP);
                    } else {
                        if(excelPrice.compareTo(discount)!=1) {
                            calculatedDiscount = quantity.multiply(excelPrice).setScale(numberOfDigits, RoundingMode.HALF_UP);
                        } else {
                            calculatedDiscount = quantity.multiply(discount).setScale(numberOfDigits, RoundingMode.HALF_UP);
                        }
                    }

                    if (promotedDiscountType.equals("%")) {
                        calculatedPromotedDiscount = (quantity.multiply(excelPrice).setScale(numberOfDigits, RoundingMode.HALF_UP).subtract(calculatedDiscount))
                                .multiply(promotedDiscount).divide(new BigDecimal("100.00"),numberOfDigits,RoundingMode.HALF_UP).setScale(numberOfDigits, RoundingMode.HALF_UP);
                    } else {
                        calculatedPromotedDiscount = quantity.multiply(promotedDiscount).setScale(numberOfDigits, RoundingMode.HALF_UP);
                    }

                    packToAddWithoutPromotions.add(String.valueOf(calculatedDiscount)); //4-discount

                    wholeDiscount = calculatedDiscount.add(calculatedPromotedDiscount);
                    packToAdd.add(String.valueOf(wholeDiscount)); //4-discount

                    if (!calculateTaxBeforeDiscount) {
                        calculatedTax = (quantity.multiply(excelPrice).setScale(numberOfDigits, RoundingMode.HALF_UP).subtract(wholeDiscount)).
                                multiply(tax).divide(new BigDecimal("100.00"),numberOfDigits,RoundingMode.HALF_UP).setScale(numberOfDigits, RoundingMode.HALF_UP);
                        calculatedTaxWithoutPromotions = (quantity.multiply(excelPrice).setScale(numberOfDigits, RoundingMode.HALF_UP).subtract(calculatedDiscount)).
                                multiply(tax).divide(new BigDecimal("100.00"),numberOfDigits,RoundingMode.HALF_UP).setScale(numberOfDigits, RoundingMode.HALF_UP);

                    } else {
                        calculatedTax = quantity.multiply(excelPrice).setScale(numberOfDigits, RoundingMode.HALF_UP).
                                multiply(tax).divide(new BigDecimal("100.00"),numberOfDigits,RoundingMode.HALF_UP).setScale(numberOfDigits, RoundingMode.HALF_UP);
                        calculatedTaxWithoutPromotions = calculatedTax;
                    }
                    packToAdd.add(String.valueOf(calculatedTax)); //5-tax
                    packToAddWithoutPromotions.add(String.valueOf(calculatedTaxWithoutPromotions)); //5-tax

                    calculatedItemNet = quantity.multiply(excelPrice).setScale(numberOfDigits, RoundingMode.HALF_UP).
                            subtract(wholeDiscount).add(calculatedTax);
                    calculatedItemNetWithoutPromotions = quantity.multiply(excelPrice).setScale(numberOfDigits, RoundingMode.HALF_UP).
                            subtract(calculatedDiscount).add(calculatedTaxWithoutPromotions);
                    packToAdd.add(String.valueOf(calculatedItemNet)); //6-item net total
                    packToAddWithoutPromotions.add(String.valueOf(calculatedItemNetWithoutPromotions)); //6-item net total

                    expectedSalesPacksDataComparison.add(packToAdd);
                    expectedSalesPacksDataWithoutPromotions.add(packToAddWithoutPromotions);
                }

                if (!packsData.get(i).get(3).equals("0")) {
                    List<String> packtoAddPartialDelivery = new ArrayList<>();

                    packtoAddPartialDelivery.add(itemCode); //0-item code
                    packtoAddPartialDelivery.add(packType); //1-pack type
                    String excelNewQty = packsData.get(i).get(3);
                    packtoAddPartialDelivery.add(excelNewQty); //2-sales quantity
                    BigDecimal newQuantity = new BigDecimal(String.valueOf(df.parse(excelNewQty)));

                    BigDecimal excelPrice  = new BigDecimal (String.valueOf(df.parse(packsData.get(i).get(4)))).setScale(numberOfDigits,RoundingMode.HALF_UP);
                    packtoAddPartialDelivery.add(String.valueOf(excelPrice)); // 3-Price

                    BigDecimal discount = new BigDecimal(String.valueOf(df.parse(packsData.get(i).get(5).split("-")[0])));
                    String discountType = packsData.get(i).get(5).split("-")[1];

                    BigDecimal newPromotedDiscount = new BigDecimal(packsData.get(i).get(8).split("-")[0]);
                    String newPromotedDiscountType = packsData.get(i).get(8).split("-")[1];

                    BigDecimal tax = new BigDecimal(String.valueOf(df.parse(packsData.get(i).get(7))));

                    BigDecimal partialDeliveryCalculatedPromotedDiscount = new BigDecimal("0.00");

                    BigDecimal partialDeliveryCalculatedDiscount = new BigDecimal("0.00");

                    BigDecimal partialDeliveryWholeDiscount = new BigDecimal("0.00");
                    BigDecimal partialDeliverycalculatedTax = new BigDecimal("0.00");

                    BigDecimal partialDeliveryCalculatedItemNet = new BigDecimal("0.00");

                    if (discountType.equals("%")) {
                        partialDeliveryCalculatedDiscount = newQuantity.multiply(excelPrice).setScale(numberOfDigits, RoundingMode.HALF_UP).
                                multiply(discount).divide(new BigDecimal("100.00"),numberOfDigits,RoundingMode.HALF_UP).setScale(numberOfDigits, RoundingMode.HALF_UP);


                    } else {
                        if(excelPrice.compareTo(discount)!=1) {
                            partialDeliveryCalculatedDiscount = newQuantity.multiply(excelPrice).setScale(numberOfDigits, RoundingMode.HALF_UP);
                        } else {
                            partialDeliveryCalculatedDiscount = newQuantity.multiply(discount).setScale(numberOfDigits, RoundingMode.HALF_UP);
                        }

                    }

                    if (newPromotedDiscountType.equals("%")) {
                        partialDeliveryCalculatedPromotedDiscount = (newQuantity.multiply(excelPrice).setScale(numberOfDigits, RoundingMode.HALF_UP).subtract(partialDeliveryCalculatedDiscount))
                                .multiply(newPromotedDiscount).divide(new BigDecimal("100.00"),numberOfDigits,RoundingMode.HALF_UP).setScale(numberOfDigits, RoundingMode.HALF_UP);
                    } else {
                        partialDeliveryCalculatedPromotedDiscount = newQuantity.multiply(newPromotedDiscount).setScale(numberOfDigits, RoundingMode.HALF_UP);
                    }

                    partialDeliveryWholeDiscount =partialDeliveryCalculatedDiscount.add(partialDeliveryCalculatedPromotedDiscount);
                    packtoAddPartialDelivery.add(String.valueOf(partialDeliveryWholeDiscount)); // 4- Discount

                    if (!calculateTaxBeforeDiscount) {
                        partialDeliverycalculatedTax = (newQuantity.multiply(excelPrice).setScale(numberOfDigits, RoundingMode.HALF_UP).subtract(partialDeliveryWholeDiscount)).
                                multiply(tax).divide(new BigDecimal("100.00"),numberOfDigits,RoundingMode.HALF_UP).setScale(numberOfDigits, RoundingMode.HALF_UP);

                    } else {
                        partialDeliverycalculatedTax = newQuantity.multiply(excelPrice).setScale(numberOfDigits, RoundingMode.HALF_UP).
                                multiply(tax).divide(new BigDecimal("100.00"),numberOfDigits,RoundingMode.HALF_UP).setScale(numberOfDigits, RoundingMode.HALF_UP);
                    }
                    packtoAddPartialDelivery.add(String.valueOf(partialDeliverycalculatedTax)); //5-tax

                    partialDeliveryCalculatedItemNet =  newQuantity.multiply(excelPrice)
                            .setScale(numberOfDigits, RoundingMode.HALF_UP).subtract(partialDeliveryWholeDiscount).add(partialDeliverycalculatedTax);
                    packtoAddPartialDelivery.add(String.valueOf(partialDeliveryCalculatedItemNet)); //6-item net total
                    partialDeliveryExpectedPacksData.add(packtoAddPartialDelivery);
                }

                if(!packsData.get(i).get(2).equals(packsData.get(i).get(3))){
                    packsToBeChangedData.add(packsData.get(i));
                    if(!itemCodesToBeChanged.contains(itemCode)) {
                        itemCodesToBeChanged.add(itemCode);
                    }
               }
            }
        }
        //endregion

            itemCodestoGetPackQuantities = itemCodestoGetPackQuantities.substring(0,itemCodestoGetPackQuantities.length()-1);
            itemPacksQuantities = packQuantities.getPackQuantities(itemCodestoGetPackQuantities);


        //region Filling Expected Complementary order
        if (packsToBeChangedData.size()>0 && createComplementaryOrder) {
            BigDecimal expectedComplementaryOrderGross = new BigDecimal("0.00");
            BigDecimal expectedComplementaryOrderDiscount = new BigDecimal("0.00");
            BigDecimal expectedComplementaryOrderNet = new BigDecimal("0.00");
            BigDecimal expectedComplementaryOrderTax = new BigDecimal("0.00");
            for (List<String> packToBeChangedData : packsToBeChangedData) {
                packToBeChangedData.add(filterLists.filterList(filterLists.filterList(itemPacksQuantities,0,packToBeChangedData.get(0)),1,packToBeChangedData.get(1)).get(0).get(2));
        }
            List<List<String>> orderedPacksToBeChangedData = new ArrayList<>(packsToBeChangedData) ;
            sortLists.sortListofLists(orderedPacksToBeChangedData,0,true,9,false);
            for (String itemCodeToBeChanged: itemCodesToBeChanged){
                List<List<String>> thisItemPacksToBeChanged = filterLists.filterList(orderedPacksToBeChangedData,0,itemCodeToBeChanged);

            }


            for (String itemCodeToBeChanged : itemCodesToBeChanged) {
                List<List<String>> thisItemPacksToBeChanged = filterLists.filterList(orderedPacksToBeChangedData, 0, itemCodeToBeChanged);
                Boolean needToConvertToSmallestUOM = false;
                Integer originalQtyInSmallerPack = 0;
                Integer newQtyInSmallerPack = 0;
                Integer complementaryQuantityInSmallestPack = 0;
                for (List<String> itemPackToBeChanged : thisItemPacksToBeChanged) {
                    if (Integer.valueOf(itemPackToBeChanged.get(3)) > Integer.valueOf(itemPackToBeChanged.get(2))) {
                        needToConvertToSmallestUOM = true;
                        break;
                    }
                }

                if (needToConvertToSmallestUOM) {
                    for (List<String> itemPackToBeChanged : thisItemPacksToBeChanged) {
                        originalQtyInSmallerPack = originalQtyInSmallerPack + Integer.valueOf(itemPackToBeChanged.get(2)) * Integer.valueOf(itemPackToBeChanged.get(9));
                        newQtyInSmallerPack = newQtyInSmallerPack + Integer.valueOf(itemPackToBeChanged.get(3)) * Integer.valueOf(itemPackToBeChanged.get(9));
                    }
                    complementaryQuantityInSmallestPack = originalQtyInSmallerPack - newQtyInSmallerPack;
                }

                for (List<String> itemPackToBeChanged : thisItemPacksToBeChanged) {
                    Integer quantity = 0;
                    if (needToConvertToSmallestUOM) {
                        Integer packQuantity = Integer.valueOf(itemPackToBeChanged.get(9));
                        quantity = complementaryQuantityInSmallestPack / packQuantity;
                        complementaryQuantityInSmallestPack = complementaryQuantityInSmallestPack - quantity * packQuantity;
                    } else {
                        quantity = Integer.valueOf(itemPackToBeChanged.get(2)) - Integer.valueOf(itemPackToBeChanged.get(3));
                    }
                    String itemCode = itemPackToBeChanged.get(0);
                    String packType = itemPackToBeChanged.get(1);
                    String packPrice = String.valueOf(df.format(df.parse(itemPackToBeChanged.get(4))));
                    String packDiscountAndType = itemPackToBeChanged.get(5);
                    String packTax = itemPackToBeChanged.get(7);
                    String toSavePackDiscountType;
                    String toSavePackDiscount;
                    if (packDiscountAndType.split("-")[1].equals("%")) {
                        toSavePackDiscountType = "1";
                    } else {
                        toSavePackDiscountType = "2";
                    }

                    String rawPackDiscount = df.format(((BigDecimal) df.parse(packDiscountAndType.split("-")[0])).stripTrailingZeros());

                    if (toSavePackDiscountType.equals("1")) {
                        toSavePackDiscount = rawPackDiscount;
                    } else {
                        if (Double.valueOf(packPrice) < Double.valueOf(rawPackDiscount)) {
                            toSavePackDiscount = "100.00";
                        } else {
                            toSavePackDiscount = String.valueOf(df.format(((BigDecimal) df.parse(rawPackDiscount)).multiply(new BigDecimal("100.00"))
                                    .divide((BigDecimal) df.parse(String.valueOf(packPrice)), 9, RoundingMode.HALF_UP)));
                        }
                    }


                    List<String> expectedComplementaryOrderPack = new ArrayList<String>();
                    expectedComplementaryOrderPack.add(itemCode);
                    expectedComplementaryOrderPack.add(packType);
                    expectedComplementaryOrderPack.add(df.format(new BigDecimal(quantity).setScale(numberOfDigits)));
                    expectedComplementaryOrderPack.add(packPrice);
                    expectedComplementaryOrderPack.add(toSavePackDiscount);
                    expectedComplementaryOrderPack.add(toSavePackDiscountType);
                    expectedComplementaryOrderPack.add("0"); // no promoted discount for promoted items
                    expectedComplementaryOrderPack.add(df.format(new BigDecimal(packTax).setScale(numberOfDigits)));
                    expectedComplementaryOrderPack.add("1"); // Complementary Order only have sales items (no promoted items)
                    expectedSavingOfComplementaryOrderDetailInSqlite.add(expectedComplementaryOrderPack);
                    BigDecimal complementaryPackGrossAmount = new BigDecimal(packPrice).multiply(new BigDecimal(quantity));
                    BigDecimal complementarypackDiscountAmount = complementaryPackGrossAmount.multiply(new BigDecimal(toSavePackDiscount)).divide(new BigDecimal("100.00"),numberOfDigits,RoundingMode.HALF_UP);
                    BigDecimal complementaryPackTaxAmount = (complementaryPackGrossAmount.subtract(complementarypackDiscountAmount)).multiply(new BigDecimal(packTax)).divide(new BigDecimal("100.00"),numberOfDigits,RoundingMode.HALF_UP);
                    expectedComplementaryOrderGross = expectedComplementaryOrderGross.add(complementaryPackGrossAmount);
                    expectedComplementaryOrderDiscount = expectedComplementaryOrderDiscount.add(complementarypackDiscountAmount);
                    expectedComplementaryOrderTax = expectedComplementaryOrderTax.add(complementaryPackTaxAmount);
                }
            }

            expectedComplementaryOrderNet = expectedComplementaryOrderGross.subtract(expectedComplementaryOrderDiscount).add(expectedComplementaryOrderTax);

            expectedSavingOfComplementaryOrderHeaderInSqlite.add(outletCode);//0-Outlet Code
            expectedSavingOfComplementaryOrderHeaderInSqlite.add(complementaryOrderID);//1-Order ID
            expectedSavingOfComplementaryOrderHeaderInSqlite.add(df.format(expectedComplementaryOrderGross));//2-Order Gross
            expectedSavingOfComplementaryOrderHeaderInSqlite.add(df.format(expectedComplementaryOrderDiscount));//3-Order Discount
            expectedSavingOfComplementaryOrderHeaderInSqlite.add(df.format(expectedComplementaryOrderTax));//4-Order Tax
            expectedSavingOfComplementaryOrderHeaderInSqlite.add(df.format(expectedComplementaryOrderNet));//5-Order Net
            expectedSavingOfComplementaryOrderHeaderInSqlite.add("18");//6-Order Status
            expectedSavingOfComplementaryOrderHeaderInSqlite.add("1");//7-Order Type ID
            expectedSavingOfComplementaryOrderHeaderInSqlite.add(RouteGeneralData.RouteHistoryID);//8-Route History ID
            expectedSavingOfComplementaryOrderHeaderInSqlite.add(RouteGeneralData.EmployeeID);//9-EmployeeID
            expectedSavingOfComplementaryOrderHeaderInSqlite.add("-1");//10- Division ID
            expectedSavingOfComplementaryOrderHeaderInSqlite.add(orderIDToDeliver);//11-Reference Order ID
        }
        //endregion


            noOfOriginalSalesItems=salesPacksData.size();
            noOfOriginalPromotedItems = originalPromotedPacksData.size();


        expectedNetTotal = (BigDecimal) df.parse(packsData.get(noOfItems+1).get(1));

        //region Filling New Promoted Items Data
        noOfPromotions = Integer.parseInt(packsData.get(noOfItems).get(1));
        for (int i =0; i<noOfPromotions;i++) {
            if(!packsData.get(noOfItems).get(i+2).equals("--") && !packsData.get(noOfItems).get(i+2).equals("")){
                newPromotedPacksData.add(readData.readData(filePath, packsData.get(noOfItems).get(i + 2)));
            } else{
                List<List<String>> list1 = new ArrayList<List<String>>();
                List<String> list2 = new ArrayList<String>();
                list2.add("--");
                list1.add(list2);
                newPromotedPacksData.add(list1);
            }
        }
        //endregion

        sharedFunctions.enterScreen("Delivery");
        sharedFunctions.checkMenuName("Delivery");

        //region Selecting OrderID To Deliver
        List<MobileElement> availableOrderIDs = driver.findElements(orderIDLocator);
        for (int i=0;i<availableOrderIDs.size();i++){
            if(availableOrderIDs.get(i).getText().trim().equals(orderIDToDeliver)){
                availableOrderIDs.get(i).click();
                break;
            }
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        driver.findElementByAccessibilityId("Deliver").click();

        //region Credit Limit Violation Check Beofre Delivering Order
        if(availableCredit < 0 && salesMode.equals("Credit")){
            By keyTypeLocator = By.id("lbl_keyType");
            (new WebDriverWait(driver, 10))
                    .until(ExpectedConditions.
                            visibilityOfElementLocated(keyTypeLocator));
            String keyType = driver.findElement(keyTypeLocator).getText().trim().toLowerCase();
            if(keyType.contains("exceed") && keyType.contains("credit") && keyType.contains("limit")){
                sharedFunctions.enterKey();
            } else {
                Assert.fail("Wrong Key Type");
            }
            availableCreditChecked=true;
        }
        if (highCreditValue < 0 && salesMode.equals("Credit") && !availableCreditChecked ){
            wait.until(ExpectedConditions.visibilityOfElementLocated(alertTitleLocator));
            String messageDescription = driver.findElement(alertMessageLocator).getText().trim().toLowerCase();
            if(!messageDescription.contains("customer is about to reach his credit limit")) {
                softAssert.fail();
            }
            driver.findElement(yesSaveLocator).click();
            highCreditChecked = true;
        }
        //endregion

        sharedFunctions.skipAppiumActivityOverActivityHanging(300);
        sharedFunctions.checkMenuName("Delivery");
        //endregion

        actualNetTotal = (BigDecimal) df.parse(driver.findElement(itemListNetTotalLocator).getText());
        softAssert.assertEquals(actualNetTotal,expectedNetTotal,"Actual and Expected Net Total are different");

        WebElement listView = driver.findElement(itemsListviewLocator);
        int pressX = driver.manage().window().getSize().width/2;
        int topY = listView.getLocation().getY();
        int bottomY = topY + listView.getSize().height;


        //region Filling Actual Sales Item Data from UI
        while(noOfOriginalSalesItems>countedSalesPacks.size()) {
            List<MobileElement> itemBox = driver.findElements(itemBoxLocator);
            for(int i=0;i<itemBox.size() ;i++){

               while (!sharedFunctions.subelementExistsInElement(itemCodeLocator,itemBox.get(i)) ||
                    !sharedFunctions.subelementExistsInElement(itemQtyLocator,itemBox.get(i)) ||
                    !sharedFunctions.subelementExistsInElement(itemPriceLocator,itemBox.get(i)) ) {
                    TouchAction touchAction = new TouchAction(driver);
                    touchAction.longPress(PointOption.point(pressX,bottomY-15)).waitAction(new WaitOptions().withDuration(Duration.ofMillis(400))).moveTo(PointOption.point(pressX,bottomY-90)).release().perform();
                }

                List<String> packToAdd = new ArrayList<>();
                packToAdd.add(itemBox.get(i).findElement(itemCodeLocator).getText());//0-item code
                packToAdd.add(itemBox.get(i).findElement(itemPackTypeLocator).getText());//1-pack type
                packToAdd.add(itemBox.get(i).findElement(itemQtyLocator).getText());//2-order quantity
                packToAdd.add(String.valueOf(df.parse(itemBox.get(i).findElement(itemPriceLocator).getText())));//3-price
                packToAdd.add(String.valueOf(df.parse(itemBox.get(i).findElement(itemDiscountocator).getText())));//4-discount
                packToAdd.add(String.valueOf(df.parse(itemBox.get(i).findElement(itemTaxLocator).getText())));//5-tax
                packToAdd.add(String.valueOf(df.parse(itemBox.get(i).findElement(itemNetLocator).getText())));//6-item net total

                if(!countedSalesPacks.contains(packToAdd)){
                countedSalesPacks.add(packToAdd);
                }

                if(noOfOriginalSalesItems<=countedSalesPacks.size()){
                    break;
                }

            }

            if(noOfOriginalSalesItems>countedSalesPacks.size()) {
                TouchAction touchAction = new TouchAction(driver);
                touchAction.longPress(PointOption.point(pressX, bottomY - 20)).waitAction(new WaitOptions().withDuration(Duration.ofMillis(400))).moveTo(PointOption.point(pressX,topY-20) ).release().perform();
            } else  if (sharedFunctions.elementExists(promotedItemsExpand)) {
                break;

            }

        }
        //endregion

        //region Scrolling Up to close Sales Item Expand
        TouchAction touchAction = new TouchAction(driver);
        touchAction.longPress(PointOption.point(pressX,topY)).moveTo(PointOption.point(pressX,bottomY)).
                waitAction(new WaitOptions().withDuration(Duration.ofSeconds(1))).release().perform();
             while (!sharedFunctions.elementExists(salesItemsExpand)) {
                 touchAction.longPress(PointOption.point(pressX, topY + 20)).waitAction(new WaitOptions().withDuration(Duration.ofMillis(400))).moveTo(PointOption.point(pressX, bottomY +20)).release().perform();
             }
        driver.findElement(salesItemsExpand).click();
        //endregion

        //region Filling Actual Promoted Item Data from UI
        while(noOfOriginalPromotedItems> countedPromotionPacks.size()) {
            List<MobileElement> itemBox = driver.findElements(itemBoxLocator);

            for (int i = 0; i < itemBox.size(); i++) {

                if (!sharedFunctions.subelementExistsInElement(itemCodeLocator, itemBox.get(i)) ||
                        !sharedFunctions.subelementExistsInElement(itemQtyLocator, itemBox.get(i))) {
                    TouchAction touchAction2 = new TouchAction(driver);
                    touchAction2.longPress(PointOption.point(pressX, bottomY - 1)).waitAction(new WaitOptions().withDuration(Duration.ofMillis(400))).moveTo(PointOption.point(pressX, bottomY-126)).release().perform();
                }

                if (!sharedFunctions.subelementExistsInElement(itemCodeLocator, itemBox.get(i))){
                    i++;
                }

                List<String> packToAdd = new ArrayList<>();
                packToAdd.add(itemBox.get(i).findElement(itemCodeLocator).getText());//0-item code
                packToAdd.add(itemBox.get(i).findElement(itemPackTypeLocator).getText());//1-pack type
                packToAdd.add(itemBox.get(i).findElement(itemQtyLocator).getText());//2-order quantity

                if (!countedPromotionPacks.contains(packToAdd)) {
                    countedPromotionPacks.add(packToAdd);
                }

                if (noOfOriginalPromotedItems <= countedPromotionPacks.size()) {
                    break;
                }
            }



            if (noOfOriginalPromotedItems > countedPromotionPacks.size()) {
                //       TouchAction touchAction = new TouchAction(driver);
                touchAction.longPress(PointOption.point(pressX, bottomY - 20)).waitAction(new WaitOptions().withDuration(Duration.ofMillis(400))).moveTo(PointOption.point(pressX, topY - 20)).release().perform();
            } else {
                break;
            }

        }
        //endregion

        //region Compare Expected and Actual Items
        if(!countedSalesPacks.containsAll(expectedSalesPacksDataComparison) || countedSalesPacks.size() != expectedSalesPacksDataComparison.size() ){
            Assert.fail("differenet quantities from expected Sales Items");
        }

            if(!countedPromotionPacks.containsAll(expectedPromotedPacksDataComparison) || countedPromotionPacks.size() != expectedPromotedPacksDataComparison.size() ){
                softAssert.fail("differenet quantities from expected Promoted Items");
            }
        //endregion


            if (packsToBeChangedData.size()!= 0){
                BigDecimal newItemAmount = new BigDecimal("0.00");

                //region Handling Partial Delivery
                for(int i = 0; i< expectedSalesPacksDataWithoutPromotions.size(); i++){
                    invoiceNetWithoutPromotions =  invoiceNetWithoutPromotions.add(new BigDecimal(String.valueOf(df.parse(expectedSalesPacksDataWithoutPromotions.get(i).get(6)))));
                }

                driver.findElement(addItemButtonLocator).click();
                for (int i =0;i<packsToBeChangedData.size();i++){
                    BigDecimal packOriginalAmount= new BigDecimal("0.00");
                    String itemCode = packsToBeChangedData.get(i).get(0);
                    String packType = packsToBeChangedData.get(i).get(1);
                    String oldQty = packsToBeChangedData.get(i).get(2);
                    String newQty = packsToBeChangedData.get(i).get(3);
                    if(!packsToBeChangedData.get(i).get(2).equals("0")){
                         packOriginalAmount = new BigDecimal(filterLists.filterList(filterLists.filterList
                                (expectedSalesPacksDataWithoutPromotions,0,itemCode),1,packType)
                                        .get(0).get(6));
                    }

                    if(!packsToBeChangedData.get(i).get(3).equals("0")){
                         newItemAmount = addItem.addItem("Delivery",itemCode,packType,
                                newQty,taxable,false,"","","");
                        if(newItemAmount!=null ) //newItemAmount will return null only and only if available quantity was higher than original
                        {
                         invoiceNetWithoutPromotions = invoiceNetWithoutPromotions.add(newItemAmount);
                        }
                    }  else {
                        deleteItem.deleteItem(itemCode,packType);
                    }
                    if(newItemAmount!=(null)) { //newItemAmount will return null only and only if available quantity was higher than original
                        invoiceNetWithoutPromotions = invoiceNetWithoutPromotions.subtract(packOriginalAmount);
                        List<String> changedItem = new ArrayList<String>();
                        changedItem.add(itemCode);//0-itemCode
                        changedItem.add(packType);//1-UOM
                        changedItem.add(oldQty);//2-original order quantity
                        changedItem.add(newQty);//3- delivery new quantity
                        changedItemsData.add(changedItem);
                    } else {
                        stopExecuting = true;
                        break;
                    }
                    // Compare Actual New Net Total to Expected New Net Total
                    actualNetTotal = (BigDecimal) df.parse(driver.findElement(itemListNetTotalLocator).getText().trim());
                    softAssert.assertEquals(actualNetTotal,invoiceNetWithoutPromotions,"Actual and Expected Net Total are different");
                }
                //endregion

                if(!stopExecuting) {
                    if (sharedFunctions.elementExists(saveImageButtonLocator)) {
                        driver.findElement(saveImageButtonLocator).click();
                    } else {
                        driver.findElement(moreOptionsLocator).click();
                        driver.findElement(saveTextButtonLocaor).click();
                    }

                    //region Taking Promotions in Partial Delivery
                    if (noOfPromotions > 0 && (AndroidVersion.equals("7.1.1") || AndroidVersion.equals("7.0") || AndroidVersion.equals("8.0"))) {
                        sharedFunctions.skipAppiumActivityOverActivityHanging(2000);
                    }

                    for (int i = 0; i < noOfPromotions; i++) {
                        wait.until(ExpectedConditions.visibilityOfElementLocated(btnTakePromotionLocator));
                        driver.findElement(btnTakePromotionLocator).click();
                        if (!newPromotedPacksData.get(i).get(0).get(0).equals("--")) {
                            noOfPromotedItems = newPromotedPacksData.get(i).size();
                            driver.findElement(addItemButtonLocator).click();
                            for (int j = 0; j < noOfPromotedItems; j++) {
                                AddItem addItem = new AddItem();
                                itemAmount = addItem.addItem("Promotions", newPromotedPacksData.get(i).get(j).get(0), newPromotedPacksData.get(i).get(j).get(1), newPromotedPacksData.get(i).get(j).get(2), taxable, true, "", "", "");
                                promotedItemsAmount = promotedItemsAmount.add(itemAmount);
                                itemAmount = BigDecimal.ZERO;
                            }
                            driver.navigate().back();
                            driver.findElement(saveItemsListLocator).click();
                        }
                    }
                    //endregion

                }
            }
            else {
                driver.findElement(itemListSaveButtonLocator).click();
            }

        if(!stopExecuting) {
            for (List<String> expItemsData : partialDeliveryExpectedPacksData) {
                partialDeliveryNetTotal = partialDeliveryNetTotal.add((BigDecimal) df.parse(expItemsData.get(6)));
            }
            partialDeliveryNetTotal = partialDeliveryNetTotal.add(promotedItemsAmount);

            //region Paying Invoice for Cash Sales Mode
            if (salesMode.equals("Cash")) {
                (new WebDriverWait(driver, 10))
                        .until(ExpectedConditions.
                                visibilityOfElementLocated(payAllCashButtonLocator));
                driver.findElement(payAllCashButtonLocator).click();
                driver.findElement(yesSaveLocator).click();
                driver.findElement(savePaymentsLocator).click();
            }
            //endregion


            //region Credit Limit Violation Check After Saving
            if (partialDeliveryNetTotal.doubleValue() > availableCredit && !availableCreditChecked && salesMode.equals("Credit")) {
                By keyTypeLocator = By.id("lbl_keyType");
                if (!sharedFunctions.elementExistsWithWait(keyTypeLocator)) {
                    Assert.fail("no key appears");
                }
                String keyType = driver.findElement(keyTypeLocator).getText().trim().toLowerCase();

                if (!keyType.contains("exceed") || !keyType.contains("credit") || !keyType.contains("limit")) {
                    softAssert.fail("no key type");
                }
                sharedFunctions.enterKey();
                availableCreditChecked = true;
            }
            if (partialDeliveryNetTotal.doubleValue() > highCreditValue && !availableCreditChecked && !highCreditChecked && salesMode.equals("Credit")) {
                if (!sharedFunctions.elementExists(alertTitleLocator)) {
                    softAssert.fail("No high credit warning");
                } else {
                    wait.until(ExpectedConditions.visibilityOfElementLocated(alertTitleLocator));
                    String messageDescription = driver.findElement(alertMessageLocator).getText().trim().toLowerCase();
                    if (!messageDescription.contains("customer") || !messageDescription.contains("about")
                            || !messageDescription.contains("reach") || !messageDescription.contains("credit") || !messageDescription.contains("limit")) {
                        softAssert.fail("message isn't correct");
                    }
                    driver.findElement(yesSaveLocator).click();
                }
            }

//endregion

            wait.until(ExpectedConditions.visibilityOfElementLocated(summarySaveButtonLocator));
            salesSummaryNetTotal = (BigDecimal) df.parse(driver.findElement(salesSummaryNetTotalLocator).getText().trim());
            softAssert.assertEquals(salesSummaryNetTotal, partialDeliveryNetTotal, "Wrong Net Total At Summary");
            driver.findElement(summarySaveButtonLocator).click();
            sharedFunctions.skipAppiumActivityOverActivityHanging(1000);
        if(sharedFunctions.getMenuName().equals("Delivery")){
                driver.navigate().back();
            }
            sharedFunctions.checkMenuName("Customer Menu");

            //region Comparing created complementary order to expected complementary order
            List<List<List<String>>> savedOrder = orders.checkSavedOrder(complementaryOrderID);
            if(savedOrder.size()>0) {
                savedComplementaryOrderHeader = savedOrder.get(0).get(0);
                savedComplementaryOrderDetail = savedOrder.get(1);
                sortLists.sortListofLists(savedComplementaryOrderDetail, 0, true, 1, true);
                sortLists.sortListofLists(expectedSavingOfComplementaryOrderDetailInSqlite, 0, true, 1, true);
            }
            softAssert.assertEquals(savedComplementaryOrderHeader,expectedSavingOfComplementaryOrderHeaderInSqlite,"Wrong Saving of Complementary Order Header Data");
            softAssert.assertEquals(savedComplementaryOrderDetail,expectedSavingOfComplementaryOrderDetailInSqlite,"Wrong Saving of Complementary Order Details Data : "+complementaryOrderID);
            //endregion
        } else {
            //region Handling exit after (Quantity more than original case)
            driver.navigate().back();
            driver.navigate().back();
            driver.findElement(okButtonLocator).click();
            driver.navigate().back();
            //endregion
        }
        softAssert.assertAll();
    }
}
