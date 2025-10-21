package HallBite;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HallBite {

    public static void main(String[] args) {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Connect to database
            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/mysql", 
                "root", 
                "0000"
            );
            
            System.out.println( con);
            
            con.close();
            
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(HallBite.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
