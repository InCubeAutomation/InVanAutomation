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
    // connection is created static so you are able to close it in another classes after using it
    public static void copySqlliteToDevice() throws IOException {
        String filePath = "D://TestDB//";
        File directory = new File(filePath);
        if (!directory.exists()) {
            try {
                Files.createDirectories(Paths.get(filePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File file = new File(filePath+ "InCube.sqlite");
        if(file.exists()){
            file.delete();
        }

        byte[] fileBase64 = driver.pullFile("/sdcard/InCube/data/InCube.sqlite");
        Files.write(Paths.get(filePath + "InCube.sqlite"), fileBase64);
        while (!file.canRead()){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static ResultSet querySqliteData (String sqlStatement) throws SQLException {
        ResultSet set = null;
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
