/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SQLiteHandler;

import ImageBase.Template.ImageTemplate;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 *
 * @author benehiko
 */
public class SQLHandler {
    private DBConnector dbc;
    
    public SQLHandler(String dbName){
        this.dbc = new DBConnector(dbName);
    }
    
    public void createPlatesTable(){
        String sql = "CREATE TABLE IF NOT EXISTS plates (\n"
                + "image BLOB NOT NULL,\n"
                + "extract TEXT NOT NULL,\n"
                + "province TEXT NOT NULL\n"
                + ");";
        try{
            Statement stmt = this.dbc.getConnection().createStatement();
            stmt.execute(sql);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public ArrayList<ImageTemplate> getPlates() throws IOException {
        String sql = "SELECT image, extract, province FROM plates";
        ArrayList<ImageTemplate> arrTemp = new ArrayList<>();
        
        try{
            Statement stmt = this.dbc.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while(rs.next()){
                BufferedImage btemp = ImageIO.read(rs.getBinaryStream("image"));
                arrTemp.add(new ImageTemplate(btemp,rs.getString("extract"), rs.getString("province")));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return arrTemp;
    }
    
    public void addPlates(ByteArrayOutputStream bos, String extracted, String province){
        String sql = "INSERT INTO plates(image, extract, province) VALUES(?,?,?)";
        
        try{
            PreparedStatement pstmt = dbc.getConnection().prepareStatement(sql);
            pstmt.setBytes(1, bos.toByteArray());
            pstmt.setString(2, extracted);
            pstmt.setString(3, province);
            pstmt.executeQuery();
            System.out.println("Values inserted");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
