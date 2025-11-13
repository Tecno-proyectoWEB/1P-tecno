/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package postgresConecction;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nnn
 */
public class PostgresSqlConecction {
    public static void main(String[] args){
        try {
            SqlConnection sqlConnection = new SqlConnection("grupo11sc","mail.tecnoweb.org.bo","5432","grup011grup011", "db_grupo11sc*");
            String query = "Select * from persona";
            if(sqlConnection.connect() == null){
                System.out.println("nula conexxion");
            }else{
                PreparedStatement ps = sqlConnection.connect().prepareStatement(query);
                ResultSet rs = ps.executeQuery();
                System.out.println("resultado : " + rs.next());
            }
        } catch (SQLException ex) {
            Logger.getLogger(PostgresSqlConecction.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
