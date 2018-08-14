package sqlite_access;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.*;

public class Accounts extends SqliteAccess  {
Configurations configurations = new Configurations();
    public Double[]  outletBalanceAndCredit(String outletCode) throws SQLException, IOException {
        copySqlliteToDevice();
        Double[] returnedBalanceeAndCredit  = new Double[3];
        String sqlStatement="select Account.CreditLimit,Account.Balance from account inner join accountcustout on accountcustout.accountid = account.accountid" +
                " inner join customeroutlet on customeroutlet.customerid = accountcustout.customerid and " +
                "customeroutlet.outletid = accountcustout.outletid where customeroutlet.customercode='"+outletCode+"'";

        ResultSet res = querySqliteData(sqlStatement);
        returnedBalanceeAndCredit[0] = res.getDouble("CreditLimit");
        returnedBalanceeAndCredit[1] = res.getDouble("Balance");
        returnedBalanceeAndCredit[2] = Double.valueOf(configurations.customerConfigurationValue("CreditLimitTolerance",outletCode,EmployeeCode,""));
        // Closing the connection so you are able to connect again
        connection.close();
        return returnedBalanceeAndCredit;
    }

    public Double[] customerBalanceAndCredit(String outletCode) throws SQLException, IOException {
        copySqlliteToDevice();
        Double[] returnedBalanceeAndCredit  = new Double[3];
        String sqlStatement="select CreditLimit,Balance from account inner join accountcust on accountcust.accountid = account.accountid" +
                " inner join customeroutlet on customeroutlet.customerid = accountcustout.customerid " +
                " where customeroutlet.customercode='"+outletCode+"'";

        ResultSet res = querySqliteData(sqlStatement);
        returnedBalanceeAndCredit[0] = res.getDouble("CreditLimit");
        returnedBalanceeAndCredit[1] = res.getDouble("Balance");
        returnedBalanceeAndCredit[2] = Double.valueOf(configurations.customerConfigurationValue("CreditLimitTolerance",outletCode,EmployeeCode,""));
        // Closing the connection so you are able to connect again
        connection.close();
        return returnedBalanceeAndCredit;
    }

    public Double[] divisionBalanceAndCredit(String outletCode, String divisionCode) throws SQLException, IOException {
        copySqlliteToDevice();
        Double[] returnedBalanceeAndCredit  = new Double[3];
        String sqlStatement="select CreditLimit,Balance from account inner join accountcustoutdiv on accountcustoutdiv.accountid = account.account\n" +
                "inner join customeroutlet on customeroutlet.customerid = accountcustoutdiv.customerid and customeroutlet.outletid = accountcustoutdiv.outletid\n" +
                "inner join division on accountcustoutdiv.divisionid = division.divisionid \n" +
                "where customeroutlet.customercode= '" +outletCode+"' and division.divisioncode = ''"+divisionCode+"'";

        ResultSet res = null;

            res = querySqliteData(sqlStatement);

        returnedBalanceeAndCredit[0] = res.getDouble("CreditLimit");
        returnedBalanceeAndCredit[1] = res.getDouble("Balance");
        returnedBalanceeAndCredit[2] = Double.valueOf(configurations.customerConfigurationValue("CreditLimitTolerance",outletCode,EmployeeCode,""));
        // Closing the connection so you are able to connect again
        connection.close();
        return returnedBalanceeAndCredit;
    }

}
