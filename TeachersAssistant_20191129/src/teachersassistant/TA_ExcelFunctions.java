/**
 * FileName: TA_ExcelFunctions.java
 * Author: Stephen James
 * Date: 11/15/19
 * Course: CMSC-495
 * 
 * Objective: To create a Utility Class used for any and all Excel interaction.
*/

// Package
package teachersassistant;

// Import statements
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import javafx.scene.paint.Color;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


// Main class
public class TA_ExcelFunctions {
    
    // Create Sheet Map
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
            
            // Save String returned from checkWorkbook
            String error = checkWorkbook(workbook);
            
            // First, check the sheet names/amounts
            if(!error.isEmpty()){
                // Prompt user
                TA_AlertPage.alert("Exel Workbook Error", error, Color.CORAL);
                
                // Return null
                return null;
            }
            
            // Iterate through each sheet
            for(int i = 0; i < workbook.getNumberOfSheets(); i++){ 
                // Create local int for keeping track of the rows
                int j = 0;
                System.out.println("Num of sheets: " + workbook.getNumberOfSheets());
                // Local sheet variable
                XSSFSheet sheet = workbook.getSheetAt(i);

                // Create Iterator to use for the ROWS
                Iterator<org.apache.poi.ss.usermodel.Row> rows = sheet.iterator();  

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
                // Save cell
                Cell cell = row.getCell(i);
                cellNum = i;
                // Determine Cell type
                switch(cell.getCellType()){
                    case Cell.CELL_TYPE_STRING:
                        if(cell.getStringCellValue().isEmpty()){
                            System.out.println("\n\n\nEMPTY VALUE\n\n\n");
                            throw new Exception();                            
                        }
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                        if(String.valueOf(cell.getNumericCellValue()).isEmpty()){
                            System.out.println("\n\n\nEMPTY VALUE\n\n\n");
                            throw new Exception();   
                        }
                        break;
                    case Cell.CELL_TYPE_FORMULA:
                        if(cell.getCellFormula().isEmpty()){
                            System.out.println("\n\n\nEMPTY VALUE\n\n\n");
                            throw new Exception(); 
                        }
                        break;
                    case Cell.CELL_TYPE_BOOLEAN:
                        if(String.valueOf(cell.getBooleanCellValue()).isEmpty()){
                            throw new Exception(); 
                        }
                        break;
                    case Cell.CELL_TYPE_ERROR:
                        if(String.valueOf(cell.getErrorCellValue()).isEmpty()){
                            System.out.println("\n\n\nEMPTY VALUE\n\n\n");
                            throw new Exception(); 
                        }
                        break;
                    case Cell.CELL_TYPE_BLANK:
                        if(String.valueOf(cell.getStringCellValue()).isEmpty()){
                            System.out.println("\n\n\nEMPTY VALUE\n\n\n");
                            throw new Exception(); 
                        }
                        break;
                    default:
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
        }
        // Catch Exception and print error via pop-up
        catch(Exception e){
            error = "ERROR! Empty Cell at...\n\tSheet Name: " + sheetName + 
                                    "\n\t\tRow Number: " + (row.getRowNum()+1) + 
                                    "\n\t\t\tCell Number: " + (cellNum+1);
            TA_AlertPage.alert("Workbook Error", error, Color.CORAL);
            return false;
        }
    }
    
    
    // Check workbook method
    private static String checkWorkbook(XSSFWorkbook workbook){
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
    
    // Method to download the Excel workbook
    public static void batchDownload(){
        // Create all neccesary data structures
        TreeMap<Integer, TreeMap<Integer, Float>> courseGradesMap = TA_SQLFunctions.createCourseGradesList();
        TreeMap<Integer, TreeMap<Integer, Float>> assignmentGradesMap = TA_SQLFunctions.createAssignmentGradesList();
        TreeMap<Integer, TreeMap<Integer, TreeMap<LocalDate, Integer>>> studentAttendanceMap = TA_SQLFunctions.createStudentAttendanceMap();
        
        // Create Arrays for header rows for each sheet
        String[] studentCourseSheetHeader = {
            "Student ID",
            "Course ID",
            "Grade"
        };
        
        String[] studentAssignmentSheetHeader = {
            "Student ID",
            "Assignment ID",
            "Grade"
        };
        
        String[] studentAttendanceSheetHeader = {
            "Student ID",
            "Course ID",
            "Attendance Date",
            "Attendance Value"
        };
        
        try{
            // Create new workbook
            XSSFWorkbook workbook = new XSSFWorkbook();

            FileOutputStream output = new FileOutputStream(new File("C:\\Users\\Public\\Documents\\ExcelDump_" + LocalDate.now() + ".xlsx"));
            
            // Create the sheets
            XSSFSheet studentCourseSheet = workbook.createSheet("StudentCourseGrades");
            XSSFSheet studentAssignmentSheet = workbook.createSheet("StudentAssignmentGrades");
            XSSFSheet studentAttendanceSheet = workbook.createSheet("StudentAttendance");
            
            // Create header rows for the studentCourseSheet
            XSSFRow studentCourseSheetHeaderRow = studentCourseSheet.createRow(0);
            XSSFRow studentAssignmentSheetHeaderRow = studentAssignmentSheet.createRow(0);
            XSSFRow studentAttendanceSheetHeaderRow = studentAttendanceSheet.createRow(0);
            
            // Create counters
            int studentCourseSheetCounter = 0;
            int studentAssignmentSheetCounter = 0;
            int studentAttendanceSheetCounter = 0;
            
            // Iterate through each array and add cells to the Rows
            for(String value : studentCourseSheetHeader){
                Cell newCell = studentCourseSheetHeaderRow.createCell(studentCourseSheetCounter++);
                newCell.setCellValue(value);
            }
            
            for(String value : studentAssignmentSheetHeader){
                Cell newCell = studentAssignmentSheetHeaderRow.createCell(studentAssignmentSheetCounter++);
                newCell.setCellValue(value);
            }
            
            for(String value : studentAttendanceSheetHeader){
                Cell newCell = studentAttendanceSheetHeaderRow.createCell(studentAttendanceSheetCounter++);
                newCell.setCellValue(value);
            }
            
            
            // Temp counter for Rows
            int rowCount = 1;
            // Iterate through the studentCourseInfoList and create neccessary cells
            for(Map.Entry<Integer, TreeMap<Integer, Float>> entry : courseGradesMap.entrySet()){
                // Save studentID
                int tempStudentID = entry.getKey();
                TreeMap<Integer, Float> courseIDGradesMap = entry.getValue();
                
                // Iterate through the tempMap
                for(Map.Entry<Integer, Float> courseIDGrade : courseIDGradesMap.entrySet()){
                    // Create new Row
                    XSSFRow tempRow = studentCourseSheet.createRow(rowCount++);
                    
                    // Create 3 Cells
                    Cell studentIDCell = tempRow.createCell(0);
                    studentIDCell.setCellValue(tempStudentID);
                    Cell courseIDCell = tempRow.createCell(1);
                    courseIDCell.setCellValue(courseIDGrade.getKey());
                    Cell gradeCell = tempRow.createCell(2);
                    gradeCell.setCellValue(courseIDGrade.getValue());
                }
                
            }
            
            
            // Reset temp counter for Rows
            rowCount = 1;
            // Iterate through the studentCourseInfoList and create neccessary cells
            for(Map.Entry<Integer, TreeMap<Integer, Float>> entry : assignmentGradesMap.entrySet()){
                // Save studentID
                int tempStudentID = entry.getKey();
                TreeMap<Integer, Float> assignmentIDGradesMap = entry.getValue();
                
                // Iterate through the tempMap
                for(Map.Entry<Integer, Float> courseIDGrade : assignmentIDGradesMap.entrySet()){
                    // Create new Row
                    XSSFRow tempRow = studentAssignmentSheet.createRow(rowCount++);
                    
                    // Create 3 Cells
                    Cell studentIDCell = tempRow.createCell(0);
                    studentIDCell.setCellValue(tempStudentID);
                    Cell assignmentIDCell = tempRow.createCell(1);
                    assignmentIDCell.setCellValue(courseIDGrade.getKey());
                    Cell gradeCell = tempRow.createCell(2);
                    gradeCell.setCellValue(courseIDGrade.getValue());
                }
                
            }
            
            
            // Reset temp counter for Rows
            rowCount = 1;
            // Iterate through the studentCourseInfoList and create neccessary cells
            for(Map.Entry<Integer, TreeMap<Integer, TreeMap<LocalDate, Integer>>> entry : studentAttendanceMap.entrySet()){
                // Save studentID
                int tempStudentID = entry.getKey();
                TreeMap<Integer, TreeMap<LocalDate, Integer>> courseIDMap = entry.getValue();
                
                // Iterate through the tempMap
                for(Map.Entry<Integer, TreeMap<LocalDate, Integer>> courseIDPair : courseIDMap.entrySet()){
                    // Save courseID
                    int tempCourseID = courseIDPair.getKey();
                    // Save TreeMap
                    TreeMap<LocalDate, Integer> attendanceMap = courseIDPair.getValue();
                    
                    // Iterate through attendanceMap
                    for(Map.Entry<LocalDate, Integer> attendanceEntry : attendanceMap.entrySet()){
                        // Create new Row
                        XSSFRow tempRow = studentAttendanceSheet.createRow(rowCount++);

                        // Create 4 Cells
                        Cell studentIDCell = tempRow.createCell(0);
                        studentIDCell.setCellValue(tempStudentID);
                        Cell courseIDCell = tempRow.createCell(1);
                        courseIDCell.setCellValue(tempCourseID);
                        Cell attendanceDateCell = tempRow.createCell(2);
                        attendanceDateCell.setCellValue(attendanceEntry.getKey().toString());
                        Cell attendanceValueCell = tempRow.createCell(3);
                        attendanceValueCell.setCellValue(attendanceEntry.getValue());
                        
                    }
                }
                
            }
            
            // Write to the workbook
            workbook.write(output);
            output.close();
            System.out.println("Workbook saved successfully");

        }
        catch(Exception e){
            // Print stack trace
            e.printStackTrace();
            
            // Prompt user
            TA_AlertPage.alert("ERROR", "There was an error downloading your file.\nPlease contact your administrator.", Color.CORAL);
        }
        
    }
    
}
