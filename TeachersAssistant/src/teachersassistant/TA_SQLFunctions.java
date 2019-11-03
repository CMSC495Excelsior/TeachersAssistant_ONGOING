package teachersassistant;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

// Import
//import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import java.io.File;
import java.sql.*;
import java.util.TreeMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author SoftwareEng
 */
public class TA_SQLFunctions {
    
    
    // Create batch upload method
    public static boolean batchUpload(File file){
        // Save Excel data
        TreeMap<Integer, Row> sheetData = TA_ExcelFunctions.storeSheetData(file);
        int student_ID = 0;
        String firstName = null;
        String middleName = null;
        String lastName = null;
        int gradeLevel = 0;
        
        // Iterate through sheet
        for(int i = 1; i < sheetData.size(); i++){
            // Define row
            Row row = sheetData.get(i);
            // Iterate through row
            for(int j = 0; j < row.getPhysicalNumberOfCells(); j++){
                // Define cell
                Cell cell = row.getCell(j);
                if(cell == null)
                    continue;
                switch(cell.getCellType()){
                        case Cell.CELL_TYPE_STRING:
                            System.out.println("Cell STRING: " + cell.getStringCellValue());
                            firstName = (j == 1) ? cell.getStringCellValue() : firstName;
                            middleName = (j == 2) ? cell.getStringCellValue() : middleName;
                            lastName = (j == 3) ? cell.getStringCellValue() : lastName;
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            System.out.println("Cell INT: " + cell.getNumericCellValue());
                            student_ID = (j == 0) ? (int) cell.getNumericCellValue() : student_ID;
                            gradeLevel = (j == 4) ? (int) cell.getNumericCellValue() : gradeLevel;
                            break;
                        case Cell.CELL_TYPE_BLANK:
                            System.out.println("Cell is BLANK");
                            break;
                        default:
                            System.out.println("Cell is different type");
                }            
            }
            
            // Insert into database
            Connection con = connectDB();
            if(con != null){
                try{
                    PreparedStatement stmt = con.prepareStatement("INSERT INTO students_test (student_ID, firstName, middleName, lastName, gradeLevel)"
                        + " values (?,?,?,?,?)");
                    stmt.setInt(1, student_ID);
                    stmt.setString(2, firstName);
                    stmt.setString(3, middleName);
                    stmt.setString(4, lastName);
                    stmt.setInt(5, gradeLevel);

                    // Execute query
                    stmt.execute();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
            else{
                System.out.println("Connection failed");
            }
        }
        return true;    
    }
    
    private static Connection connectDB(){
        // Try connecting to database
        try{
            MysqlDataSource dataSource = new MysqlDataSource();
            dataSource.setUser("root");
            dataSource.setPassword("r00t_@cCe$_Plz");
            dataSource.setServerName("localhost");
            dataSource.setPortNumber(3306); // or 1527
            dataSource.setDatabaseName("testing");
            
            Connection con = dataSource.getConnection();
            System.out.println("MySQL CONNECTED!!");
            return con;
            
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("MySQL FAILED TO CONNECT");
            Connection con = null;
            return con;
        }
        
    }
    
}


/*

for(int i = 0; i < sheetData.size(); i++){
            Row row = sheetData.get(i);
            for(int j = 0; j < row.getPhysicalNumberOfCells(); j++){
                Cell cell = row.getCell(j);
                if(cell == null)
                    continue;

                if(matchExactly){
                    equalsPattern(cell, value, tempValue);
                }
                else{
                    containsPattern(cell, value, tempValue);
                }
            }
        }
*/