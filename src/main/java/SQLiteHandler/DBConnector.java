/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SQLiteHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author benehiko
 */
public class DBConnector {
    private Connection conn;
    
    public DBConnector(String dbName){
        conn = null;
        try{
            String url = "jdbc:sqlite:../"+dbName+".db";
            conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite Established");
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
    
    public Connection getConnection(){
        return this.conn;
    }
}
