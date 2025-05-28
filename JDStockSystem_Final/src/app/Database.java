package app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {


    public static Connection getConnection(String dbPath) throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + dbPath);
    }

}
