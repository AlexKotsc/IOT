package maredowell.util;

import java.sql.*;

/**
 * Created by AlexKotsc on 10-03-2015.
 */
public class dbSpark {

    Connection conn;
    Statement stm;

    public dbSpark(){

        try {

            Class.forName("com.mysql.jdbc.Driver");

            conn = DriverManager.getConnection("jdbc:mysql://localhost/core", "root", "");

            stm = conn.createStatement();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public ResultSet execQ (String q){
        try {
            return stm.executeQuery(q);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public int execU (String q){
        try {
            return stm.executeUpdate(q);
        } catch (SQLException e){
            e.printStackTrace();
        }

        return 0;
    }

    public void closeConnection(){
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
