package sqlite_access;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.*;

public class Accounts extends SqliteAccess  {

    public Float[]  outletBalanceAndCredit(String outletCode) throws SQLException, IOException {
        Float[] returnedBalanceeAndCredit  = new Float[2];
        String sqlStatement="select Account.CreditLimit,Account.Balance from account inner join accountcustout on accountcustout.accountid = account.accountid" +
                " inner join customeroutlet on customeroutlet.customerid = accountcustout.customerid and " +
                "customeroutlet.outletid = accountcustout.outletid where customeroutlet.customercode='"+outletCode+"'";

        ResultSet res = querySqliteData(sqlStatement);
        returnedBalanceeAndCredit[0] = res.getFloat("CreditLimit");
        returnedBalanceeAndCredit[1] = res.getFloat("Balance");
        // Closing the connection so you are able to connect again
        connection.close();
        return returnedBalanceeAndCredit;
    }

    public Float[] customerBalanceAndCredit(String outletCode) throws SQLException, IOException {
        Float[] returnedBalanceeAndCredit  = new Float[2];
        String sqlStatement="select CreditLimit,Balance from account inner join accountcust on accountcust.accountid = account.accountid" +
                " inner join customeroutlet on customeroutlet.customerid = accountcustout.customerid " +
                " where customeroutlet.customercode='"+outletCode+"'";

        ResultSet res = querySqliteData(sqlStatement);
        returnedBalanceeAndCredit[0] = res.getFloat("CreditLimit");
        returnedBalanceeAndCredit[1] = res.getFloat("Balance");
        // Closing the connection so you are able to connect again
        connection.close();
        return returnedBalanceeAndCredit;
    }

    public Float[] divisionBalanceAndCredit(String outletCode, String divisionCode)throws SQLException{
        Float[] returnedBalanceeAndCredit  = new Float[2];
        String sqlStatement="select CreditLimit,Balance from account inner join accountcustoutdiv on accountcustoutdiv.accountid = account.account\n" +
                "inner join customeroutlet on customeroutlet.customerid = accountcustoutdiv.customerid and customeroutlet.outletid = accountcustoutdiv.outletid\n" +
                "inner join division on accountcustoutdiv.divisionid = division.divisionid \n" +
                "where customeroutlet.customercode= '" +outletCode+"' and division.divisioncode = ''"+divisionCode+"'";

        ResultSet res = null;
        try {
            res = querySqliteData(sqlStatement);
        } catch (IOException e) {
            e.printStackTrace();
        }
        returnedBalanceeAndCredit[0] = res.getFloat("CreditLimit");
        returnedBalanceeAndCredit[1] = res.getFloat("Balance");
        // Closing the connection so you are able to connect again
        connection.close();
        return returnedBalanceeAndCredit;
    }

}
