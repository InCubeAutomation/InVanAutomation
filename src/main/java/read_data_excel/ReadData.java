package read_data_excel;

import org.apache.poi.ss.usermodel.*;
import org.testng.Assert;
import testng_config_methods.TestNGConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReadData extends TestNGConfig {
    Iterator<Row> rowIterator;

    public  List<List<String>> readData (String filePath, String readDataMode) {
        List<List<String>> rowsData = new ArrayList<List<String>>();
        try {
            Sheet sheetToRead = null;
        DataFormatter dataFormatter = new DataFormatter();
        File file = new File(filePath);
        Workbook excelData = WorkbookFactory.create(file);
        Iterator<Sheet> sheetIterator = excelData.sheetIterator();

        for (Sheet sheet : excelData) {
            if (readDataMode.equals(sheet.getSheetName())) {
                sheetToRead = sheet;
            }
            sheetIterator.next();
        }
        if(sheetToRead!=null) {
            rowIterator = sheetToRead.rowIterator();
        } else  {
            Assert.fail("Sheet doesn't exist in path");
        }
        rowIterator.next();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            List<String> rowData = new ArrayList<String> ();
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                rowData.add(dataFormatter.formatCellValue(cell));
            }
            rowsData.add(rowData);
        }
        } catch (Throwable throwable) {// In case exception happen
            Assert.fail("Read Item Data failed, " +
                    "please check log: \n" + throwable.getMessage());
        }
return  rowsData;
    }
}

