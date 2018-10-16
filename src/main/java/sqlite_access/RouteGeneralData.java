package sqlite_access;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RouteGeneralData extends SqliteAccess {
    public  static Boolean Uploaded = false;
    public  static String RouteHistoryID = null;
    public  static Integer RouteID = null;
    public  static Integer TerritoryID = null;
    public  static String EmployeeID = null;
    public  static Integer VehicleID = null;
    public  static Integer OrganizationID = null;
    public  static Integer SupervisorID = null;
    public  static Integer SalesManagerID = null;
    public  static Integer SalesRepID = null;
    public  static String EmployeeCode ="";
    public  static String SupervisorCode ="";
    public  static String SalesManagerCode ="";
    public  static String EmployeeName ="";



    public static void getGeneralData() throws IOException, SQLException {
        copySqlliteToDevice();
        String sqlStatement = "select RH.Uploaded,RH.RouteHistoryID,RH.RouteID,RH.TerritoryID,RH.EmployeeID,RH.VehicleID,RH.OrganizationID,RH.SupervisorID,RH.SalesManagerID, " +
                "RH.SalesRepID,emp.EmployeeCode,sprvsr.employeeCode SupervisorCode,slsmngr.employeeCode SalesManagerCode,employeelanguage.Description EmployeeName  from RouteHistory RH " +
                "inner join employee emp on emp.employeeID = RH.employeeID left outer join employee sprvsr on  sprvsr.employeeid  =RH.SupervisorID " +
                "Inner join employeelanguage on employeelanguage.EmployeeID = RH.EmployeeID and employeelanguage.languageid = 1 " +
                "left outer join employee slsmngr on  slsmngr.employeeid  =RH.SalesManagerID";
        ResultSet res = querySqliteData(sqlStatement);
        Uploaded = res.getBoolean("RouteHistoryID");
        RouteHistoryID = String.valueOf(res.getInt("RouteHistoryID"));
        RouteID = res.getInt("RouteID");
        TerritoryID = res.getInt("TerritoryID");
        EmployeeID = String.valueOf(res.getInt("EmployeeID"));
        if(res.getInt("VehicleID") !=0) {
            VehicleID = res.getInt("VehicleID");
        }
        OrganizationID = res.getInt("OrganizationID");
        SupervisorID = res.getInt("SupervisorID");
        if(res.getInt("SalesManagerID") !=0) {
            SalesManagerID = res.getInt("SalesManagerID");
        }
        if(res.getInt("SalesRepID") !=0) {
            SalesRepID = res.getInt("SalesRepID");
        }
        SupervisorCode = res.getString("EmployeeCode");
        EmployeeCode = res.getString("EmployeeCode");
        SalesManagerCode = res.getString("SalesManagerCode");
        EmployeeName = res.getString("EmployeeName");
    }
}
