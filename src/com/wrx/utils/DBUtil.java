package com.wrx.utils;

import java.sql.*;
import java.util.ResourceBundle;

public class DBUtil {

    private static ResourceBundle resourceBundle=ResourceBundle.getBundle("resources.jdbc");
    private static String driver=resourceBundle.getString("driver");
    private static String url=resourceBundle.getString("url");
    private static String user=resourceBundle.getString("user");
    private static String password=resourceBundle.getString("password");

    static {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(url, user, password);
        return connection;
    }

    public static void close(Connection connection, Statement ps, ResultSet rs) throws SQLException {
        if(connection!=null) {
            connection.close();
        }
        if(ps!=null) {
            ps.close();
        }
        if(rs!=null) {
            rs.close();
        }
    }
}
