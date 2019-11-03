/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package teachersassistant;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



/**
 *
 * @author SoftwareEng
 */

 
public class TA_ExcelFunctions {
    
    private static ArrayList<XSSFRow> rows = new ArrayList<>();
    // Make foundValues a HashMap with the Key being the Cell Row/Column and the Value being the actual value of
    // the Cell.
    private static HashMap<String, HashMap<String, String>> foundValues = new HashMap<>();

    // Master Map
    private static TreeMap<Integer, Row> sheetData = new TreeMap<>();
    private static TreeMap<String, String> rowContents = new TreeMap<>();

    // GUI Components
    private static BorderPane borderPane;
    private static GridPane gridPane;
    private static Label greetingLabel, optionsLabel;
    private static TextField searchField;
    private static Button searchButton, backButton, nextButton, selectAll;
    private static CheckBox copyResultsToClipboard, exactMatch;
    private static HBox searchBox, nextBackBox;
    private static VBox optionsBox;
    private static boolean isAllSelected, copy, matchExactly;
    
    public static TreeMap<Integer, Row> storeSheetData(File file){
        // Reset the TreeMaps
        rowContents.clear();
        sheetData.clear();

        // Create local int for keeping track of the rows
        int j = 0;

        // Iterate through the sheet and store all data
        try{
            // Create Excel Workbook/Sheet instances
            XSSFWorkbook workbook = new XSSFWorkbook(file.getAbsolutePath());
            XSSFSheet sheet = workbook.getSheetAt(0);

            // Create Iterator to use for the ROWS
            Iterator<org.apache.poi.ss.usermodel.Row> rows = sheet.iterator();

            // Need to iterate through each CELL in each ROW - requires multiple loops
            while(rows.hasNext()){
                // Start iterating through the rows
                Row row = rows.next();

                // Add each row to sheetData
                sheetData.put(j, row);
                j++;
            }

        }catch (IOException e){
            e.printStackTrace();
        }

        System.out.println("The size of the sheet Data is: " + sheetData.size());

        return sheetData;
    }
    
}
