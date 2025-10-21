/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package HallBite;

/**
 *
 * @author jabir
 */

import java.sql.*;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/hallbite";
    private static final String USER = "root"; // change if needed
    private static final String PASSWORD = "0000"; // add your MySQL password if any

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }
}
