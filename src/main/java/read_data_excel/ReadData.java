package read_data_excel;

import org.apache.poi.ss.usermodel.*;
import org.testng.Assert;
import testng_config_methods.TestNGConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReadData extends TestNGConfig {

    public  List<List<String>> readData (String filePath, String readDataMode) {
        List<List<String>> itemsData = new ArrayList<List<String>>();
        try {
            Sheet sheetToRead = null;
        DataFormatter dataFormatter = new DataFormatter();
        File file = new File(filePath);
        Workbook transactionsData = WorkbookFactory.create(file);
        Iterator<Sheet> sheetIterator = transactionsData.sheetIterator();

        for (Sheet sheet : transactionsData) {
            if (readDataMode.equals(sheet.getSheetName())) {
                sheetToRead = sheet;
            }
            sheetIterator.next();
        }

        Iterator<Row> rowIterator = sheetToRead.rowIterator();
        rowIterator.next();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            List<String> itemData = new ArrayList<String> ();
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                itemData.add(dataFormatter.formatCellValue(cell));
            }
            itemsData.add(itemData);
        }
        } catch (Throwable throwable) {// In case exception happen
            Assert.fail("Read Item Data failed, " +
                    "please check log: \n" + throwable.getMessage());
        }
return  itemsData;
    }
}
