package sqlite_access;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;


//readDataFromSqlite
public  class SqliteAccess {
    public ResultSet querySqliteData (String sqlStatement) throws SQLException {

        Connection connection = null;
        ResultSet set = null;
        String filePath = "D:/TestDB";
        File directory = new File(filePath);
        File file = new File(filePath + "/InCube.sqlite");
        if (!directory.exists()) {
            try {
                Files.createDirectories(Paths.get(filePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            Runtime.getRuntime().exec("adb -s emulator-5554 pull /sdcard/InCube/data/InCube.sqlite " +
                    filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:D:/TestDB/InCube.sqlite");
            set = connection.prepareStatement(sqlStatement).executeQuery();
        } catch (SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        }

        return  set;
    }
}
