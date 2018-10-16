package sqlite_access;

import java.io.IOException;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

public class Orders extends SqliteAccess {

    public List<List<List<String>>> checkSavedOrder(String orderID) throws IOException, SQLException {
        List<List<String>> savedOrderDetail = new ArrayList<List<String>>();
        List<List<String>> savedOrderHeader = new ArrayList<List<String>>();
        copySqlliteToDevice();
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("#,###.###",symbols);
        df.setParseBigDecimal(true);
        String sqlStatement="select item.ItemCode,ptl.description UOM,SOD.Quantity,SOD.price,SOD.Discount PackDiscount, " +
                "SOD.DiscountTypeID PackDiscountType,SOD.PromotedDiscount PackPromotedDiscount,SOD.Tax PackTax,SOD.SalesTransactionTypeID, " +
                "CO.CustomerCode,SO.OrderID,So.GrossTotal OrderGross,So.Discount OrderDiscount,SO.TAX OrderTax, " +
                "SO.NetTotal OrderNet,SO.OrderStatusID,SO.OrderTypeID,So.RouteHistoryID,So.EmployeeID,SO.divisionID,SO.ReferenceOrderID " +
                "from  salesorder SO inner join salesorderdetail SOD on SO.orderid = SOD.OrderID " +
                "and SO.divisionid = SOD.divisionid and SO.orderid = '" + orderID + "' " +
                "inner join customeroutlet CO on  SO.CustomerID = CO.CustomerID and  SO.OutletID = CO.OutletID " +
                "and SOD.CustomerID = CO.CustomerID and  SOD.OutletID = CO.OutletID " +
                "inner join pack on pack.PackID = SOD.PackID inner join item on item.itemid = pack.itemid " +
                "inner join packtypelanguage ptl on ptl.PackTypeID = pack.PackTypeID and ptl.LanguageID = 1";
        ResultSet res = querySqliteData(sqlStatement);
        while(res.next()){
            if(savedOrderHeader.size() == 0) {
                List<String> orderHeader = new ArrayList<String>();
                orderHeader.add(res.getString("CustomerCode"));
                orderHeader.add(res.getString("OrderID"));
                orderHeader.add(df.format(res.getBigDecimal("OrderGross")));
                orderHeader.add(df.format(res.getBigDecimal("OrderDiscount")));
                orderHeader.add(df.format(res.getBigDecimal("OrderTax")));
                orderHeader.add(df.format(res.getBigDecimal("OrderNet")));
                orderHeader.add(String.valueOf(res.getInt("OrderStatusID")));
                orderHeader.add(String.valueOf(res.getInt("OrderTypeID")));
                orderHeader.add(String.valueOf(res.getInt("RouteHistoryID")));
                orderHeader.add(String.valueOf(res.getInt("EmployeeID")));
                orderHeader.add(String.valueOf(res.getInt("divisionID")));
                orderHeader.add(res.getString("ReferenceOrderID"));
                if (orderHeader.size() > 0) {
                    savedOrderHeader.add(orderHeader);
                }
            }
            List<String> orderDetail = new ArrayList<String>();
            orderDetail.add(res.getString("ItemCode"));
            orderDetail.add(res.getString("UOM"));
            orderDetail.add(df.format(res.getBigDecimal("Quantity")));
            orderDetail.add(df.format(res.getBigDecimal("price")));
            orderDetail.add(df.format(res.getBigDecimal("PackDiscount")));
            orderDetail.add(String.valueOf(res.getInt("PackDiscountType")));
            orderDetail.add(df.format(res.getBigDecimal("PackPromotedDiscount")));
            orderDetail.add(df.format(res.getBigDecimal("PackTax")));
            orderDetail.add(String.valueOf(res.getInt("SalesTransactionTypeID")));
            if(orderDetail.size()>0) {
                savedOrderDetail.add(orderDetail);
            }
        }

        List<List<List<String>>> orderHeaderAndDetailArray = new ArrayList<List<List<String>>>();
        if(savedOrderHeader.size()>0) {
            orderHeaderAndDetailArray.add(savedOrderHeader);
            orderHeaderAndDetailArray.add(savedOrderDetail);
        }
        return orderHeaderAndDetailArray;
    }
}
