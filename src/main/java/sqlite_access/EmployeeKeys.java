package sqlite_access;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmployeeKeys extends SqliteAccess {
    public String getKey (int keyTypeID) throws SQLException, IOException {
        copySqlliteToDevice();
        String keyValue=null;
        String sqlStatement="select  employeekey.keyvalue from employeekey left outer join employeekeyhistory on " +
                "employeekeyhistory.KeyValue = employeekey.KeyValue where employeekeyhistory.KeyValue is null and " +
                "employeekey.KeyTypeID = "+String.valueOf(keyTypeID)+" Limit 1";
        ResultSet res = querySqliteData(sqlStatement);
        keyValue = res.getString(1);
        return keyValue;
    }
}
