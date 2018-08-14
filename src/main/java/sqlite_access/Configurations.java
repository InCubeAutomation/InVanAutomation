package sqlite_access;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Configurations extends SqliteAccess {
    public String keyValue = null;
    String sqlStatement = null;
    ResultSet res;

    public String generalConfigurationValue(String keyName, String employeeCode, String selectedDivisionCode) throws IOException, SQLException {
        copySqlliteToDevice();
        sqlStatement = "select keyvalue from Configuration inner join employee on " +
                "Configuration.employeeid = employee.employeeid and  employee.EmployeeCode = '" +
                employeeCode + "' and Configuration.KeyName = '" + keyName + "'";
        res = querySqliteData(sqlStatement);
        if (res.getFetchSize() > 0) {
            keyValue = res.getString(1);
        } else {
            connection.close();
            // Closing the connection so you are able to connect again
            sqlStatement = "select keyvalue from ConfigurationSecurityGroup inner join operatorsecuritygroup on " +
                    "operatorsecuritygroup.securitygroupid = ConfigurationSecurityGroup.securitygroupid " +
                    "inner join employeeoperator on employeeoperator.OperatorID = operatorsecuritygroup.OperatorID " +
                    "inner join employee on employee.EmployeeID = employeeoperator.EmployeeID " +
                    "and employee.EmployeeCode = '" + employeeCode + "' and ConfigurationSecurityGroup.KeyName = '" + keyName + "' Limit 1";
            res = querySqliteData(sqlStatement);
            if (res.getFetchSize() > 0) {
                keyValue = res.getString(1);
            } else {
                 connection.close();
                 sqlStatement = "select keyvalue from ConfigurationDivision inner join Division on " +
                        "Division.DivisionID = ConfigurationDivision.DivisionID " +
                        "and Division.DivisionCode = '" + selectedDivisionCode + "' and ConfigurationDivision.KeyName = '" + keyName + "'";
                res = querySqliteData(sqlStatement);
                if (res.getFetchSize() > 0) {
                    keyValue = res.getString(1);
                } else {
                    connection.close();
                    sqlStatement = "select keyvalue from ConfigurationOrganization inner join Employee on " +
                            "Employee.OrganizationID = ConfigurationOrganization.OrganizationID " +
                            "and employee.EmployeeCode = '" + employeeCode + "' and ConfigurationOrganization.KeyName = '" + keyName + "'";
                    res = querySqliteData(sqlStatement);
                }
                if (res.getFetchSize() > 0) {
                    keyValue = res.getString(1);
                } else {
                        connection.close();
                        sqlStatement = "select keyvalue from Configuration where employeeid = -1 and Configuration.KeyName = '" + keyName + "'";
                        res = querySqliteData(sqlStatement);
                        keyValue = res.getString(1);
                }

            }
        }
        if(!connection.isClosed()) {
            connection.close();
        }
        return keyValue;
    }

    public String customerConfigurationValue(String keyName, String outletCode, String employeeCode, String selectedDivisionCode) throws IOException, SQLException {
        copySqlliteToDevice();
        sqlStatement = "select keyvalue from Configurationcustout inner join customeroutlet on " +
                "Configurationcustout.customerid =   customeroutlet.customerid and Configurationcustout.outletid =   customeroutlet.outletid " +
                "and customeroutlet.customercode = '" + outletCode + "' and Configurationcustout.keyname = '" + keyName + "'";
        res = querySqliteData(sqlStatement);
        if (res.getFetchSize() > 0) {
            keyValue = res.getString(1);
        } else {
            connection.close();
            // Closing the connection so you are able to connect again
            sqlStatement = "select keyvalue from Configurationcustomergroup inner join customeroutletgroup on " +
                    "Configurationcustomergroup.groupid = customeroutletgroup.groupid " +
                    "inner join customeroutlet on customeroutletgroup.customerid =   customeroutlet.customerid and customeroutletgroup.outletid = customeroutlet.outletid " +
                    "and customeroutlet.customercode = '" + outletCode + "' and Configurationcustomergroup.keyname = '" + keyName + "' Limit 1";
            res = querySqliteData(sqlStatement);
            if (res.getFetchSize() > 0) {
                keyValue = res.getString(1);
            } else {
                connection.close();
                sqlStatement = "select keyvalue from ConfigurationChannel inner join customergroup on " +
                        "customergroup.SubChannelID = ConfigurationChannel.SubChannelID " +
                        "inner join customeroutletgroup on customergroup.GroupID = customeroutletgroup.GroupID " +
                        "inner join customeroutlet on customeroutletgroup.customerid =   customeroutlet.customerid and customeroutletgroup.outletid = customeroutlet.outletid " +
                        "and customeroutlet.customercode = '" + outletCode + "' and ConfigurationChannel.keyname = '" + keyName + "' Limit 1";
                res = querySqliteData(sqlStatement);
                if (res.getFetchSize() > 0) {
                    keyValue = res.getString(1);
                }
                else {
                    connection.close();
                    sqlStatement = "select keyvalue from ConfigurationChannel inner join customergroup on " +
                            "customergroup.ChannelID = ConfigurationChannel.ChannelID " +
                            "inner join customeroutletgroup on customergroup.GroupID = customeroutletgroup.GroupID " +
                            "inner join customeroutlet on customeroutletgroup.customerid =   customeroutlet.customerid and customeroutletgroup.outletid = customeroutlet.outletid " +
                            "and customeroutlet.customercode = '" + outletCode + "' and ConfigurationChannel.keyname = '" + keyName + "' Limit 1";
                    res = querySqliteData(sqlStatement);
                    if (res.getFetchSize() > 0) {
                        keyValue = res.getString(1);
                    } else {
                        connection.close();
                        keyValue=generalConfigurationValue(keyName,employeeCode,selectedDivisionCode);
                    }
                }
            }
        }
        if(!connection.isClosed()){
            connection.close();
        }
        return keyValue;
    }
}
