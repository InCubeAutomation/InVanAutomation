package sqlite_access;


import testng_config_methods.TestNGConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;


//readDataFromSqlite
public  class SqliteAccess extends TestNGConfig {
    public static Connection connection = null;
    ResultSet set = null;
    String filePath = "D://TestDB//";
    // connection is created static so you are able to close it in another classes after using it
    public void copySqlliteToDevice() throws IOException {
        File directory = new File(filePath);
        if (!directory.exists()) {
            try {
                Files.createDirectories(Paths.get(filePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        byte[] fileBase64 = driver.pullFile("/sdcard/InCube/data/InCube.sqlite");
        Files.write(Paths.get(filePath + "InCube.sqlite"), fileBase64);
    }

    public ResultSet querySqliteData (String sqlStatement) throws SQLException {
        if (connection!= null){
            connection.close();
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
