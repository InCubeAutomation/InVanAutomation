package sqlite_access;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PackQuantities extends SqliteAccess {

    public List<List<String>> getPackQuantities (String itemCodes) throws IOException, SQLException {
        List<List<String>> packQuantities = new ArrayList<List<String>>();
        copySqlliteToDevice();
        String sqlStatement="select item.ItemCode,packtypelanguage.Description,Pack.quantity from item inner join pack on pack.ItemID = item.ItemID " +
                "inner join itemlanguage on item.ItemID = itemlanguage.ItemID and itemlanguage.LanguageID = 1 " +
                "inner join packtypelanguage on packtypelanguage.PackTypeID = pack.PackTypeID " +
                "and packtypelanguage.LanguageID = 1 where item.ItemCode in ("+itemCodes+")";
        ResultSet res = querySqliteData(sqlStatement);
        while(res.next()){
            List<String> rowToAdd = new ArrayList<String>();
            rowToAdd.add(res.getString("ItemCode"));
            rowToAdd.add(res.getString("Description"));
            rowToAdd.add(res.getString("quantity"));
            packQuantities.add(rowToAdd);
        }
        connection.close();
        return packQuantities;

    }
}
