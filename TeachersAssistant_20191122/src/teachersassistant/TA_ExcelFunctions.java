/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package teachersassistant;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import javafx.scene.paint.Color;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



/**
 *
 * @author SoftwareEng
 */

 
public class TA_ExcelFunctions {
    
    // Sheet Map
    private static TreeMap<Integer, Row> sheetData = new TreeMap<>();
    
    // Method for saving the SheetData
    public static TreeMap<Integer, Row> storeSheetData(File file){
        // Reset the TreeMaps
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
    
    
    /**
     *  NOTE: Implement functionality to check the Row for any blank cells.
     */
    
    // Method for saving the SheetData
    public static ArrayList<TreeMap<Integer, Row>> storeWorkBookData(File file){
        // Initialize local TreeMaps for EACH sheet
        TreeMap<Integer, Row> studentData = new TreeMap<>();
        TreeMap<Integer, Row> courseData = new TreeMap<>();
        TreeMap<Integer, Row> studentCourseData = new TreeMap<>();
        TreeMap<Integer, Row> assignmentData = new TreeMap<>();
        TreeMap<Integer, Row> studentAssignmentData = new TreeMap<>();
        TreeMap<Integer, Row> courseAssignmentData = new TreeMap<>();
        TreeMap<Integer, Row> attendanceData = new TreeMap<>();
        
        // Initialize local ArrayList to store EACH TreeMap
        ArrayList<TreeMap<Integer, Row>> sheetList = new ArrayList<>();
        

        // Iterate through the sheet and store all data
        try{
            // Create Excel Workbook/Sheet instances
            XSSFWorkbook workbook = new XSSFWorkbook(file.getAbsolutePath());
            
            String error = checkWorkbook(workbook);
            
            // First, check the sheet names/amounts
            if(!error.isEmpty()){
                // Prompt user
                TA_AlertPage.alert("Exel Workbook Error", error, Color.CORAL);
                
                // Return null
                return null;
            }
            
            boolean cancelStorage = false;
            // Iterate through each sheet
            for(int i = 0; i < workbook.getNumberOfSheets(); i++){ 
                // Create local int for keeping track of the rows
                int j = 0;
                System.out.println("Num of sheets: " + workbook.getNumberOfSheets());
                // Local sheet variable
                XSSFSheet sheet = workbook.getSheetAt(i);

                // Create Iterator to use for the ROWS
                Iterator<org.apache.poi.ss.usermodel.Row> rows = sheet.iterator();
                
                //if(cancelStorage){
                  //  return null;
                //}
                    

                // Save the rows
                while(rows.hasNext()){
                    // Start iterating through the rows
                    Row row = rows.next();
                    
                    // Check Row
                    if(!checkRowForBlankCells(sheet, row)){
                        return null;
                    }

                    // Add each row to sheetData
                    switch(i){
                        case 0:
                            studentData.put(j, row);
                            break;
                        case 1:
                            courseData.put(j, row);
                            break;
                        case 2:
                            studentCourseData.put(j, row);
                            break;
                        case 3:
                            assignmentData.put(j, row);
                            break;
                        case 4:
                            studentAssignmentData.put(j, row);
                            break;
                        case 5:
                            courseAssignmentData.put(j, row);
                            break;
                        case 6:
                            attendanceData.put(j, row);
                            break;
                    }
                    
                    // Increment counter
                    j++;
                }
            }
            
            // Add all lists to sheetList
            sheetList.add(studentData);
            sheetList.add(courseData);
            sheetList.add(studentCourseData);
            sheetList.add(assignmentData);
            sheetList.add(studentAssignmentData);
            sheetList.add(courseAssignmentData);
            sheetList.add(attendanceData);
            

        }catch (IOException e){
            e.printStackTrace();
        }
        
        // Print list for testing
        for(int u = 0; u < sheetList.size(); u++){
            TreeMap<Integer, Row> temp = sheetList.get(u);
            System.out.println("Sheet #" + u + ": ");
            // Iterate through sheet
            for(int i = 0; i < temp.size(); i++){
                // Define row
                Row row = temp.get(i);
                System.out.println("\tRow #" + i + ": ");
                // Iterate through row
                for(int j = 0; j < row.getPhysicalNumberOfCells(); j++){
                    // Define cell
                    Cell cell = row.getCell(j);
                    if(cell == null)
                        continue;
                    switch(cell.getCellType()){
                            case Cell.CELL_TYPE_STRING:
                                System.out.println("\t\tCell #" + j + ": " + cell.getStringCellValue());
                                break;
                            case Cell.CELL_TYPE_NUMERIC:
                                System.out.println("\t\tCell #" + j + ": " + cell.getNumericCellValue());
                                break;
                            case Cell.CELL_TYPE_BLANK:
                                System.out.println("Cell is BLANK");
                                break;
                            default:
                                System.out.println("Cell is different type");
                    }  
                }
            }
        }
        
        // Return sheetList
        return sheetList;
    }
    
    
    // Check rows for blank cells
    private static boolean checkRowForBlankCells(XSSFSheet sheet, Row row){
        // Local variables
        String error = "";
        int cellNum = 0;
        String sheetName = sheet.getSheetName();
        try{
            System.out.println("In TRY block");
            // Iterate through Row
            for(int i = 0; i < row.getPhysicalNumberOfCells(); i++){
                //System.out.println("In " + i + " iteration");
                Cell cell = row.getCell(i);
                cellNum = i;
                //System.out.println("Cell type is: " + cell.getCellType());
                switch(cell.getCellType()){
                    case Cell.CELL_TYPE_STRING:
                        //System.out.println("String value - at cell " + i + " of row " + row.getRowNum() + " in sheet: " + sheetName);
                        if(cell.getStringCellValue().isEmpty()){
                            System.out.println("\n\n\nEMPTY VALUE\n\n\n");
                            throw new Exception();                            
                        }
                        System.out.println("Cell value: " + cell.getStringCellValue());
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                        //System.out.println("Numeric value - at cell " + i + " of row " + row.getRowNum() + " in sheet: " + sheetName);
                        if(String.valueOf(cell.getNumericCellValue()).isEmpty()){
                            System.out.println("\n\n\nEMPTY VALUE\n\n\n");
                            throw new Exception();   
                        }
                        break;
                    case Cell.CELL_TYPE_FORMULA:
                        //System.out.println("Formula value - at cell " + i + " of row " + row.getRowNum() + " in sheet: " + sheetName);
                        if(cell.getCellFormula().isEmpty()){
                            System.out.println("\n\n\nEMPTY VALUE\n\n\n");
                            throw new Exception(); 
                        }
                        break;
                    case Cell.CELL_TYPE_BOOLEAN:
                        //System.out.println("Boolean value - at cell " + i + " of row " + row.getRowNum() + " in sheet: " + sheetName);
                        if(String.valueOf(cell.getBooleanCellValue()).isEmpty()){
                            throw new Exception(); 
                        }
                        break;
                    case Cell.CELL_TYPE_ERROR:
                        //System.out.println("Error value - at cell " + i + " of row " + row.getRowNum() + " in sheet: " + sheetName);
                        if(String.valueOf(cell.getErrorCellValue()).isEmpty()){
                            System.out.println("\n\n\nEMPTY VALUE\n\n\n");
                            throw new Exception(); 
                        }
                        break;
                    case Cell.CELL_TYPE_BLANK:
                        //System.out.println("Blank value - at cell " + i + " of row " + row.getRowNum() + " in sheet: " + sheetName);
                        if(String.valueOf(cell.getStringCellValue()).isEmpty()){
                            System.out.println("\n\n\nEMPTY VALUE\n\n\n");
                            throw new Exception(); 
                        }
                        break;
                    default:
                        //System.out.println("Default value - at cell " + i + " of row " + row.getRowNum() + " in sheet: " + sheetName);
                        if(String.valueOf(cell.getDateCellValue()).isEmpty()){
                            System.out.println("\n\n\nEMPTY VALUE\n\n\n");
                            throw new Exception(); 
                        }
                        error = "UNHANDLED EXCEPTION";
                            throw new Exception(); 
                            
                }
            }
            // Return true
            return true;
        }catch(Exception e){
            error = "ERROR! Empty Cell at...\n\tSheet Name: " + sheetName + 
                                    "\n\t\tRow Number: " + (row.getRowNum()+1) + 
                                    "\n\t\t\tCell Number: " + (cellNum+1);
            TA_AlertPage.alert("Workbook Error", error, Color.CORAL);
            return false;
        }
    }
    
    
    // Check workbook method
    private static String checkWorkbook(XSSFWorkbook workbook){
        /**
         * NOTE: Excel's default date format is: MM/DD/YYYY.
         *       Consider changing accepted date format from (YYYY/MM/DD) to (MM/DD/YYYY).
         */
        
        // Error messages
        String studentInfoError = "\n\t+StudentInfo:"
                                    + "\n\t\t-student_ID (integer)"
                                    + "\n\t\t-firstName (text)"
                                    + "\n\t\t-middleName (text)"
                                    + "\n\t\t-lastName (text)"
                                    + "\n\t\t-major (text)"
                                    + "\n\t\t-gradeLevel (integer)";
        
        String courseInfoError = "\n\t+CourseInfo:"
                                    + "\n\t\t-course_ID (integer)"
                                    + "\n\t\t-courseSubject (text)"
                                    + "\n\t\t-gradeLevel (integer)"
                                    + "\n\t\t-courseNumber (integer)"
                                    + "\n\t\t-semester (text)";
        
        String studentCourseInfoError = "\n\t+StudentCourseInfo:"
                                    + "\n\t\t-student_ID (integer)"
                                    + "\n\t\t-course_ID (integer)";
        
        
        String assignmentInfoError = "\n\t+AssignmentInfo:"
                                    + "\n\t\t-assignment_ID (integer)"
                                    + "\n\t\t-assignmentName (text)"
                                    + "\n\t\t-assignmentType (text)"
                                    + "\n\t\t-dueDate (YYYY/MM/DD)"
                                    + "\n\t\t-points (decimal EX: 1.0, 33.75)"
                                    + "\n\t\t-weight (decimal EX: 1.0, 33.75)";
        
        String studentAssignmentInfoError = "\n\t+StudentAssignmentInfo:"
                                    + "\n\t\t-student_ID (integer)"
                                    + "\n\t\t-assignment_ID (integer)";
        
        
        String courseAssignmentInfoError = "\n\t+courseAssignmentInfo:"
                                    + "\n\t\t-course_ID (integer)"
                                    + "\n\t\t-assignment_ID(integer)";
        
        
        String attendanceInfoError = "\n\t+attendanceInfo:"
                                    + "\n\t\t-attendanceDate (YYYY/MM/DD)";
        
        
        int numOfSheets = workbook.getNumberOfSheets();
        
        // There needs to be 3 sheets (1 for StudentInfo, 1 for CourseInfo, 1 for StudentCourseInfo)
        if(numOfSheets != 7)
            return "Excel WorkBook does not have 7 sheets."
                    + "\nREQUIRED SHEET LISTING & ORDER:"
                    + studentInfoError
                    + courseInfoError
                    + studentCourseInfoError
                    + assignmentInfoError
                    + studentAssignmentInfoError
                    + courseAssignmentInfoError
                    + attendanceInfoError;
        
              
        // Check names
        if(!workbook.getSheetAt(0).getSheetName().equalsIgnoreCase("StudentInfo"))
            return "StudentInfo sheet not recognized. First Sheet MUST be named \"StudentInfo\"";
        else if(!workbook.getSheetAt(1).getSheetName().equalsIgnoreCase("CourseInfo"))
            return "CourseInfo sheet not recognized. Second Sheet MUST be named \"CourseInfo\"";
        else if(!workbook.getSheetAt(2).getSheetName().equalsIgnoreCase("StudentCourseInfo"))
            return "StudentCourseInfo sheet not recognized. Third Sheet MUST be named \"StudentCourseInfo\"";
        else if(!workbook.getSheetAt(3).getSheetName().equalsIgnoreCase("AssignmentInfo"))
            return "AssignmentInfo sheet not recognized. Fourth Sheet MUST be named \"AssignmentInfo\"";
        else if(!workbook.getSheetAt(4).getSheetName().equalsIgnoreCase("StudentAssignmentInfo"))
            return "StudentAssignmentInfo sheet not recognized. Fifth Sheet MUST be named \"StudentAssignmentInfo\"";
        else if(!workbook.getSheetAt(5).getSheetName().equalsIgnoreCase("CourseAssignmentInfo"))
            return "CourseAssignmentInfo sheet not recognized. Sixth Sheet MUST be named \"CourseAssignmentInfo\"";
        else if(!workbook.getSheetAt(6).getSheetName().equalsIgnoreCase("AttendanceInfo"))
            return "AttendanceInfo sheet not recognized. Seventh Sheet MUST be named \"AttendanceInfo\"";
        else{
            return "";
        }
    }
    
}
