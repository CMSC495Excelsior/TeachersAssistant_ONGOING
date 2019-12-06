/**
 * FileName: TA_SQLFunctions.java
 * Author: Stephen James
 * Date: 11/12/19
 * Course: CMSC-495
 * 
 * Objective: To create a Utility Class that will be used to interact with the database.
*/

// Package
package teachersassistant;

// Import statements
import com.mysql.cj.jdbc.MysqlDataSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Color;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;

// Main class
public class TA_SQLFunctions {
    
    // Save Class-wide ArrayLists and TreeMaps
    private static ArrayList<TreeMap<Integer, Row>> masterUploadList = new ArrayList<>();
    
    
    // Method to connect to the DB
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
    
    
    // Method to upload records to the DB in bulk via Excel files
    public static boolean batchUpload(File file){
        // Update ArrayList
        masterUploadList = TA_ExcelFunctions.storeWorkBookData(file);
        
        if(masterUploadList == null){
            // Return false
            return false;
        }
        else{
            // Student/Course pairings map for helping to populate the attendance table in case 6.
            ArrayList<LocalDate> attendanceDateList = new ArrayList<>();
            ArrayList<Integer> studentCourseIDList = null;
            TreeMap<Integer, ArrayList<Integer>> studentCoursePairs = new TreeMap<>();

            // Local StudentInfo variables for upload
            int student_ID = 0;
            String firstName = null;
            String middleName = null;
            String lastName = null;
            String major = null;
            int studentGradeLevel = 0;

            // Local CourseInfo variables for upload
            int course_ID = 0;
            String courseSubject = null;
            int courseNumber = 0;
            String semester = null;
            int courseGradeLevel = 0;


            // Local AssignmentInfo variables for upload
            int assignment_ID = 0;
            String assignmentName = null;
            String assignmentType = null;
            LocalDate dueDate = null;
            float points = 0.0f;
            float weight = 0.0f;

            // Local StudentAssignment variables for upload
            int studentAssignmentMap_ID = 0;
            int assignmentStudentMap_ID = 0;

            // Local CourseAssignment variables for upload
            int courseAssignmentMap_ID = 0;
            int assignmentCourseMap_ID = 0;

            // Create DB connection
            Connection con = connectDB();

            // Iterate through sheetList
            for(int sheetNumber = 0; sheetNumber < masterUploadList.size(); sheetNumber++){
                // Save temporary sheet
                TreeMap<Integer, Row> tempSheet = masterUploadList.get(sheetNumber);
                System.out.println("TempSheet size at sheet " + sheetNumber + " is: " + tempSheet.size());
                
                // Iterate through current sheet
                for(int rowNumber = 1; rowNumber < tempSheet.size(); rowNumber++){
                    studentCourseIDList = new ArrayList<>();
                    // Local StudentCourseInfo variables for upload
                    int studentMap_ID = 0;
                    int courseMap_ID = 0;

                    // Local AttendanceInfo variables for upload
                    LocalDate attendanceDate = null;

                    // Define row
                    Row row = tempSheet.get(rowNumber);
                    
                    // Iterate through row
                    for(int cellNumber = 0; cellNumber < row.getPhysicalNumberOfCells(); cellNumber++){
                        // Define cell
                        Cell cell = row.getCell(cellNumber);
                        if(cell == null)
                            continue;
                        try{
                            switch(cell.getCellType()){
                                    case Cell.CELL_TYPE_STRING:
                                        // Store student information
                                        firstName = (sheetNumber == 0 && cellNumber == 1) ? cell.getStringCellValue() : firstName;
                                        middleName = (sheetNumber == 0 && cellNumber == 2) ? cell.getStringCellValue() : middleName;
                                        lastName = (sheetNumber == 0 && cellNumber == 3) ? cell.getStringCellValue() : lastName;
                                        major = (sheetNumber == 0 && cellNumber == 5) ? cell.getStringCellValue() : major;

                                        // Store class information
                                        courseSubject = (sheetNumber == 1 && cellNumber == 1) ? cell.getStringCellValue() : courseSubject;
                                        semester = (sheetNumber == 1 && cellNumber == 4) ? cell.getStringCellValue() : semester;


                                        // Store assignment information
                                        assignmentName = (sheetNumber == 3 && cellNumber == 1) ? cell.getStringCellValue() : assignmentName;
                                        assignmentType = (sheetNumber == 3 && cellNumber == 2) ? cell.getStringCellValue() : assignmentType;


                                        break;
                                    case Cell.CELL_TYPE_NUMERIC:
                                        // Store student information
                                        student_ID = (sheetNumber == 0 && cellNumber == 0) ? (int) cell.getNumericCellValue() : student_ID;
                                        studentGradeLevel = (sheetNumber == 0 && cellNumber == 4) ? (int) cell.getNumericCellValue() : studentGradeLevel;

                                        // Store class information
                                        course_ID = (sheetNumber == 1 && cellNumber == 0) ? (int) cell.getNumericCellValue() : course_ID;
                                        courseNumber = (sheetNumber == 1 && cellNumber == 3) ? (int) cell.getNumericCellValue() : courseNumber;
                                        courseGradeLevel = (sheetNumber == 1 && cellNumber == 2) ? (int) cell.getNumericCellValue() : courseGradeLevel;

                                        // Store temporary student/course ID's for adding to ArrayList/TreeMaps
                                        courseMap_ID = (sheetNumber == 2 && cellNumber == 1) ? (int) cell.getNumericCellValue() : courseMap_ID;
                                        studentMap_ID = (sheetNumber == 2 && cellNumber == 0) ? (int) cell.getNumericCellValue() : studentMap_ID;

                                        // Add ID's to ArrayList/TreeMap
                                        try{
                                            // Try adding the course_ID's to an already existing key
                                            if(courseMap_ID != 0 && studentMap_ID != 0){
                                                studentCoursePairs.get(studentMap_ID).add(courseMap_ID);
                                                System.out.println("\n---- Adding " + courseMap_ID + " to pre-existing key: " + studentMap_ID + " ----\n");
                                            }
                                        }
                                        catch(Exception e){
                                            if(courseMap_ID != 0 && studentMap_ID != 0){
                                                System.out.println("\n---- Creating new key: " + studentMap_ID + " ----\n");
                                                // If there is no preexisting key, create a new one with that key value
                                                studentCourseIDList.add(courseMap_ID);
                                                studentCoursePairs.put(studentMap_ID, studentCourseIDList);
                                            }
                                            // Print stack trace - this will cause LOTS of NullPointerExceptions to be thrown when the StudentCourseInfo isn't being read
                                            //e.printStackTrace();

                                        }

                                        // Check for date formatting
                                        if (DateUtil.isCellDateFormatted(cell)){
                                            dueDate = (sheetNumber == 3 && cellNumber == 3) ? 
                                                    cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : dueDate;
                                            
                                            // Store attendance information
                                            attendanceDate = (sheetNumber == 6 && cellNumber == 0) ? 
                                                    cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : attendanceDate;
                                            if(attendanceDate != null)
                                                attendanceDateList.add(attendanceDate);
                                            if(dueDate != null)            
                                                System.out.println("Due Date is: " + dueDate.toString());
                                        }

                                        // Store assignment information
                                        assignment_ID = (sheetNumber == 3 && cellNumber == 0) ? (int) cell.getNumericCellValue() : assignment_ID;
                                        points = (sheetNumber == 3 && cellNumber == 4) ? (float) cell.getNumericCellValue() : points;
                                        weight = (sheetNumber == 3 && cellNumber == 5) ? (float) cell.getNumericCellValue() : weight;

                                        // Store student assignment information
                                        studentAssignmentMap_ID = (sheetNumber == 4 && cellNumber == 0) ? (int) cell.getNumericCellValue() : studentAssignmentMap_ID;
                                        assignmentStudentMap_ID = (sheetNumber == 4 && cellNumber == 1) ? (int) cell.getNumericCellValue() : assignmentStudentMap_ID;

                                        // Store class assignment information
                                        courseAssignmentMap_ID = (sheetNumber == 5 && cellNumber == 0) ? (int) cell.getNumericCellValue() : courseAssignmentMap_ID;
                                        assignmentCourseMap_ID = (sheetNumber == 5 && cellNumber == 1) ? (int) cell.getNumericCellValue() : assignmentCourseMap_ID;
                                        break;
                                    case Cell.CELL_TYPE_BLANK:
                                        System.out.println("Cell is BLANK");
                                        break;
                                    default:

                                        break;
                            }  
                        }
                        catch(Exception e){
                            TA_AlertPage.alert("Excel Parsing Error", "Error with parsing the selected Excel file."
                                    + "\nPlease ensure Excel Workbook data complies with the required order and type of data."
                                    + "\nFor more information, please review the User Guide", Color.CORAL);
                            e.printStackTrace();
                        }
                    }

                    //  Insert all records by using switch cases
                    if(con != null){
                        try{
                            switch(sheetNumber){
                                case 0:
                                    // Insert into students
                                    PreparedStatement studentInsert = con.prepareStatement("INSERT INTO Students (student_ID, firstName, middleName, lastName, gradeLevel, major)"
                                        + " values (?,?,?,?,?,?)");
                                    studentInsert.setInt(1, student_ID);
                                    studentInsert.setString(2, firstName);
                                    studentInsert.setString(3, middleName);
                                    studentInsert.setString(4, lastName);
                                    studentInsert.setInt(5, studentGradeLevel);
                                    studentInsert.setString(6, major);

                                    // Execute & close
                                    studentInsert.execute();
                                    studentInsert.close();
                                    
                                    // Insert into students
                                    PreparedStatement studentGPAInsert = con.prepareStatement("INSERT INTO StudentGPA (student_ID, gradeLevel)"
                                        + " values (?,?)");
                                    studentGPAInsert.setInt(1, student_ID);
                                    studentGPAInsert.setInt(2, studentGradeLevel);

                                    // Execute & close
                                    studentGPAInsert.execute();
                                    studentGPAInsert.close();
                                    break;
                                case 1:
                                    // Insert into courses
                                    PreparedStatement coursesInsert = con.prepareStatement("INSERT INTO Courses (course_ID, courseSubject, gradeLevel, courseNumber, semester)"
                                        + " values (?,?,?,?,?)");
                                    coursesInsert.setInt(1, course_ID);
                                    coursesInsert.setString(2, courseSubject);
                                    coursesInsert.setInt(3, courseGradeLevel);
                                    coursesInsert.setInt(4, courseNumber);
                                    coursesInsert.setString(5, semester);

                                    // Execute & close
                                    coursesInsert.execute();
                                    coursesInsert.close();
                                    break;
                                case 2:
                                    // Insert into student-course map
                                    PreparedStatement studentCoursesInsert = con.prepareStatement("INSERT INTO StudentCourseGrades (student_ID, course_ID)"
                                        + " values (?,?)");
                                    studentCoursesInsert.setInt(1, studentMap_ID);
                                    studentCoursesInsert.setInt(2, courseMap_ID);

                                    // Execute & close
                                    studentCoursesInsert.execute();
                                    studentCoursesInsert.close();
                                    break;

                                case 3:
                                    // Insert into assignments
                                    PreparedStatement assignmentInsert = con.prepareStatement("INSERT INTO Assignments (assignment_ID, assignmentName, assignmentType, dueDate, points, weight)"
                                        + " values (?,?,?,?,?,?)");
                                    System.out.printf("Assignment ID: %d | Assignment Name: %s | Assignment Type: %s | Assignment Date: %s | Assignment Points: %f | Assignment Weight: %f",
                                            assignment_ID, assignmentName, assignmentType, dueDate, points, weight);
                                    assignmentInsert.setInt(1, assignment_ID);
                                    assignmentInsert.setString(2, assignmentName);
                                    assignmentInsert.setString(3, assignmentType);
                                    assignmentInsert.setDate(4, java.sql.Date.valueOf(dueDate));
                                    assignmentInsert.setFloat(5, points);
                                    assignmentInsert.setFloat(6, weight);


                                    // Execute & close
                                    assignmentInsert.execute();
                                    assignmentInsert.close();
                                    break;

                                case 4:
                                    // Insert into student-assignment map
                                    PreparedStatement studentAssignmentInsert = con.prepareStatement("INSERT INTO StudentAssignmentGrades (student_ID, assignment_ID)"
                                        + " values (?,?)");
                                    studentAssignmentInsert.setInt(1, studentAssignmentMap_ID);
                                    studentAssignmentInsert.setInt(2, assignmentStudentMap_ID);

                                    // Execute & close
                                    studentAssignmentInsert.execute();
                                    studentAssignmentInsert.close();
                                    break;


                                case 5:
                                    // Insert into course-assignment map
                                    PreparedStatement courseAssignmentInsert = con.prepareStatement("INSERT INTO CourseAssignment (course_ID, assignment_ID)"
                                        + " values (?,?)");
                                    courseAssignmentInsert.setInt(1, courseAssignmentMap_ID);
                                    courseAssignmentInsert.setInt(2, assignmentCourseMap_ID);

                                    // Execute & close
                                    courseAssignmentInsert.execute();
                                    courseAssignmentInsert.close();
                                    break;


                                case 6:
                                    // Insert into attendance
                                    System.out.println("Attendance List size is: " + attendanceDateList.size());
                                    if(attendanceDateList.size() == tempSheet.size()-1){
                                        // Iterate through studentCoursePairs and make sure to add the attendanceDate for each student in each course
                                        for(Map.Entry<Integer, ArrayList<Integer>> entry : studentCoursePairs.entrySet()){
                                            System.out.println("Student_ID: " + entry.getKey());
                                            // Save temporary key and value
                                            int tempStudentID = entry.getKey();
                                            ArrayList<Integer> tempCourseList = entry.getValue();

                                            // Iterate through ArrayList of course_ID's and run SQL
                                            for(int tempCourseID : tempCourseList){
                                                System.out.println("\n\tCourse_ID: " + tempCourseID);
                                                // Iterate through attendanceDateList and insert attendanceDate for the current student_ID in the current course_ID
                                                for(LocalDate tempDate : attendanceDateList){
                                                    System.out.println("\n\t\tAttendanceDate: " + tempDate);
                                                    PreparedStatement attendanceInsert = con.prepareStatement("INSERT INTO StudentAttendance (student_ID, course_ID, attendanceDate)"
                                                        + " values (?,?,?)");
                                                    attendanceInsert.setInt(1, tempStudentID);
                                                    attendanceInsert.setInt(2, tempCourseID);
                                                    attendanceInsert.setDate(3, java.sql.Date.valueOf(tempDate));

                                                    // Execute & close
                                                    attendanceInsert.execute();
                                                    attendanceInsert.close();
                                                }
                                            }
                                        }
                                    }
                                    break;
                            }

                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                    else{
                        System.out.println("Connection failed");
                    }
                }
            }

            try{
                // Close connection
                con.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            // Return
            return true;   
        }
        
         
    }
    
    
    
    
    // Method to check for number of students (used to tell if the teacher has any open classes)
    public static int checkStudents(){
        // Connect to DB
        Connection con = connectDB();
        
        // Counting variable
        int count = 0;
        
        if(con != null){
            try{
                PreparedStatement stmt = con.prepareStatement("SELECT student_ID FROM Students");

                // Execute query
                ResultSet set = stmt.executeQuery();
                while(set.next()){
                    count++;
                    System.out.println("Count is: " + count);
                }
                
                // Close stmt/con
                stmt.close();
                con.close();
                
                return count;
            }
            catch(Exception e){
                e.printStackTrace();
                return -1;
            }
        }
        else{
            return -1;
        }
    }
    
    // Method to create a course map used during the selectcourse statement
    public static ArrayList generateCourseMap(){
        // Create local ArrayList to be returned
        ArrayList<TreeMap> masterList = new ArrayList<>();
       
        // Begin DB queries 
        try{
            // Establish connection
            Connection con = connectDB();

            // Get EACH subject...add each result to the masterCourseList
            PreparedStatement getSubjects = con.prepareStatement("SELECT DISTINCT courseSubject FROM courses");
            // Iterate through each subject to get gradeLevels for EACH subject...TreeMap<Integer, ArrayList<Integer>>
            PreparedStatement getGradeLevels = con.prepareStatement("SELECT DISTINCT gradeLevel FROM courses WHERE courseSubject = ?");
            // Iterate through each gradLevel results to get courseNumbers for EACH gradeLevel...ArrayList<Integer>
            PreparedStatement getCourseNumbers = con.prepareStatement("SELECT DISTINCT courseNumber FROM courses WHERE courseSubject = ? AND gradeLevel = ?");

            // Execute query
            ResultSet subjectSet = getSubjects.executeQuery();

            // Begin populating masterList
            while(subjectSet.next()){
                System.out.println("------Generating CourseMap------");
                // Initialize necessary local TreeMaps
                TreeMap<Integer, ArrayList<Integer>> gradeLevelMap = new TreeMap<>();
                TreeMap<String, TreeMap<Integer, ArrayList<Integer>>> courseMap = new TreeMap<>();
                
                // Save the current class subject
                String tempSubject = subjectSet.getString("courseSubject");
                System.out.println("Subject: " + tempSubject);

                // Bind params
                getGradeLevels.setString(1, tempSubject);

                // Execute gradeLevels query
                ResultSet gradeLevelSet = getGradeLevels.executeQuery();

                // Iterate through gradeLevels results
                while(gradeLevelSet.next()){
                    // Initialize necessary local ArrayList
                    ArrayList<Integer> classNums = new ArrayList<>();
                    
                    // Save the current grade level
                    int tempGradeLevel = gradeLevelSet.getInt("gradeLevel");
                    System.out.println("\tGrade Level: " + tempGradeLevel);

                    // Bind params
                    getCourseNumbers.setString(1, tempSubject);
                    getCourseNumbers.setInt(2, tempGradeLevel);

                    // Execute gradeLevels query
                    ResultSet courseNumberSet = getCourseNumbers.executeQuery();
                    
                    // Iterate through the courseNumbers
                    while(courseNumberSet.next()){
                        // Save the current course number
                        int tempCourseNumber = courseNumberSet.getInt("courseNumber");
                        System.out.println("\t\tCourse Number: " + tempCourseNumber);
                        
                        // Add all courseNumbers to list
                        classNums.add(tempCourseNumber);
                    }
                                       
                    // Add the class numbers for the current grade level
                    gradeLevelMap.put(tempGradeLevel, classNums);

                }
                
                // Add the grade levels & course numbers for the current subject
                courseMap.put(tempSubject, gradeLevelMap);
                
                // Finally, add entire subject directory to the masterList
               masterList.add(courseMap);
                //getSubjects.close();
            }
            
            // Close all statements & connection
            getSubjects.close();
            getGradeLevels.close();
            getCourseNumbers.close();
            con.close();
            
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        // Return master list
        return masterList;
    }
    
    
    // Create a method to retrieve all pertinent information given specific course attributes
    public static int getCourseID(String courseSubject, int gradeLevel, int courseNumber){
        // Default value for course_ID
        int course_ID = 0;
        try{
            // Get DB connection
            Connection con = connectDB();
            
            // Create prepared statements
            PreparedStatement courseLookupStmt = con.prepareStatement("SELECT course_ID FROM Courses WHERE "
                                                                        + "courseSubject = ? AND "
                                                                        + "gradeLevel = ? AND "
                                                                        + "courseNumber = ?");
            // Set params
            courseLookupStmt.setString(1, courseSubject);
            courseLookupStmt.setInt(2, gradeLevel);
            courseLookupStmt.setInt(3, courseNumber);
            
            // Execute & save course_ID
            ResultSet set = courseLookupStmt.executeQuery();
            while(set.next()){
                course_ID = set.getInt("course_ID");
            }
            
            // Use course_ID to retrieve other information
            System.out.println("\n\n---Course ID is: " + course_ID + "---\n\n");
            return course_ID;
        }
        catch(Exception e){
            // Return default value
            return course_ID;
        }
        
    }
    
    
    // Create a method to return an ArrayList for the attendanceDates
    public static ArrayList populateAttendanceList(int course_ID){
        // Temp ArrayList<LocalDate>
        ArrayList<LocalDate> tempAttendanceList = new ArrayList<>();
        
        // Start SQL query process and save results
        try{
            // Create connection to the DB
            Connection con = connectDB();
            
            // Create preparedStatement
            PreparedStatement getAttendanceStmt = con.prepareStatement("SELECT DISTINCT attendanceDate FROM StudentAttendance WHERE course_ID = ?");
            
            // Bind params & execute query
            getAttendanceStmt.setInt(1, course_ID);
            ResultSet set = getAttendanceStmt.executeQuery();
            
            // Save each attendanceDate to the List
            while(set.next()){
                // Add results to ArrayList
                tempAttendanceList.add(set.getDate("attendanceDate").toLocalDate());
            }
            
            // Return the ArrayList
            return tempAttendanceList;
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
        
        
    }
    
    // Create method to return a TreeMap<Integer, String> for the Student Details (<student_ID, studentFullName>)
    public static TreeMap populateStudentsMap(int course_ID){
        // Temp TreeMap to return
        TreeMap<Integer, String> studentNamesMap = new TreeMap<>();
        
        // Start SQL query process and save results
        try{
            // Create connection to the DB
            Connection con = connectDB();
            
            // Create preparedStatement
            PreparedStatement getStudentNames = con.prepareStatement("SELECT student_ID, firstName, lastName FROM Students WHERE student_ID = ANY " +
                                                                    "(SELECT DISTINCT student_ID FROM StudentAttendance WHERE course_ID = ?)");
            
            // Bind params & execute query
            getStudentNames.setInt(1, course_ID);
            ResultSet set = getStudentNames.executeQuery();
            
            // Save each attendanceDate to the List
            while(set.next()){
                // Add results to ArrayList
               studentNamesMap.put(set.getInt("student_ID"), set.getString("lastName") + ", " + set.getString("firstName"));
            }
            
            // Return the ArrayList
            return studentNamesMap;
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
        
        
    }
    
    
    // Create method to return a TreeMap<Integer, String> for the Student Details (<student_ID, studentFullName>)
    public static TreeMap populateStudentsMap_ReportPage(){
        // Temp TreeMap to return
        TreeMap<Integer, String> studentNamesMap = new TreeMap<>();
        
        // Start SQL query process and save results
        try{
            // Create connection to the DB
            Connection con = connectDB();
            
            // Create preparedStatement
            PreparedStatement getALLStudentNames = con.prepareStatement("SELECT student_ID, firstName, lastName FROM Students");
            
            // Execute query
            ResultSet set = getALLStudentNames.executeQuery();
            
            // Save each attendanceDate to the List
            while(set.next()){
                // Add results to ArrayList
               studentNamesMap.put(set.getInt("student_ID"), set.getString("lastName") + ", " + set.getString("firstName"));
            }
            
            // Return the ArrayList
            return studentNamesMap;
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
        
        
    }
    
    
    // Create method to return a TreeMap<Integer, String> for the Student Details (<student_ID, studentFullName>)
    public static TreeMap populateStudentsMap_GradePage(int course_ID){
        // Temp TreeMap to return
        TreeMap<Integer, String> studentNamesMap = new TreeMap<>();
        
        // Start SQL query process and save results
        try{
            // Create connection to the DB
            Connection con = connectDB();
            
            // Create preparedStatement
            PreparedStatement getStudentNames = con.prepareStatement("SELECT student_ID, firstName, lastName FROM Students WHERE student_ID = ANY " +
                                                                    "(SELECT DISTINCT student_ID FROM StudentCourseGrades WHERE course_ID = ?)");
            
            // Bind params & execute query
            getStudentNames.setInt(1, course_ID);
            ResultSet set = getStudentNames.executeQuery();
            
            // Save each attendanceDate to the List
            while(set.next()){
                // Add results to ArrayList
               studentNamesMap.put(set.getInt("student_ID"), set.getString("lastName") + ", " + set.getString("firstName"));
            }
            
            // Return the ArrayList
            return studentNamesMap;
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
        
        
    }
    
    
    // Create a method to return an ArrayList for the attendanceDates
    public static  ArrayList<TreeMap<Integer, TreeMap<String, String>>> populateAssignmentList(int course_ID){
        
        // Create MASTER list for holding ALL assignment information
        ArrayList<TreeMap<Integer, TreeMap<String, String>>> tempAssignmentList_MASTER = new ArrayList<>();
        
        // Start SQL query process and save results
        try{
            // Create connection to the DB
            Connection con = connectDB();
            
            // Create preparedStatement
            PreparedStatement getAssignmentInfo = con.prepareStatement("SELECT * FROM Assignments WHERE assignment_ID = ANY " +
                                                                       "(SELECT assignment_ID FROM courseAssignment WHERE course_ID = ?)");
            
            // Bind params & execute query
            getAssignmentInfo.setInt(1, course_ID);
            ResultSet set = getAssignmentInfo.executeQuery();
            
            // Save each attendanceDate to the List
            while(set.next()){
                // Create loop-scope nested TreeMaps
                TreeMap<String, String> assignmentAttributes = new TreeMap<>();
                TreeMap<Integer, TreeMap<String, String>> assignmentIDMap = new TreeMap<>();
                
                // Save all assignment attributes to the attribute map
                assignmentAttributes.put("AssignmentName", set.getString("assignmentName"));
                assignmentAttributes.put("AssignmentType", set.getString("assignmentType"));
                assignmentAttributes.put("DueDate", set.getDate("dueDate").toString());
                assignmentAttributes.put("Points", String.valueOf(set.getFloat("points")));
                assignmentAttributes.put("Weight", String.valueOf(set.getFloat("weight")));
                
                // Save the attributes map to the assignmentIDMap
                assignmentIDMap.put(set.getInt("assignment_ID"), assignmentAttributes);
                
                // Add the assignmentIDMap to the Master list
                tempAssignmentList_MASTER.add(assignmentIDMap);
            }
            
            // Close statement & connection
            getAssignmentInfo.close();
            con.close();
            
            // Return the ArrayList
            return tempAssignmentList_MASTER;
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
        
        
    }
    
    
    // Method to update the attendance records for the students
    public static boolean updateAttendance(TreeMap<Integer, ToggleGroup> studentAttendanceMap, LocalDate date, int course_ID){
        // Begin SQL connection
        try{
            // Establish connection
            Connection con = connectDB();
            
            // Create PreparedStatement
            PreparedStatement updateAttendanceStmt = con.prepareStatement("UPDATE StudentAttendance SET attendance = ? "
                                                                + "WHERE student_ID = ? AND course_ID = ? AND attendanceDate = ?");
            // Begin iterating through the studentAttendanceMap
            for(Map.Entry<Integer, ToggleGroup> entry : studentAttendanceMap.entrySet()){
                // Save temp values
                int tempStudentID = entry.getKey();
                ToggleGroup tempGroup = entry.getValue();
                int attendanceValue = Integer.parseInt(tempGroup.getSelectedToggle().getUserData().toString());
                System.out.println("Student ID #" + tempStudentID + " attendance value: " + attendanceValue);
                
                updateAttendanceStmt.setInt(1, attendanceValue);
                updateAttendanceStmt.setInt(2, tempStudentID);
                updateAttendanceStmt.setInt(3, course_ID);
                updateAttendanceStmt.setDate(4, java.sql.Date.valueOf(date));
                
                // Execute update query
                updateAttendanceStmt.executeUpdate();
            }
            
            // Close statement & connection 
            updateAttendanceStmt.close();
            con.close();
            
            // Return true
            return true;
        }
        catch(Exception e){
            // Print error
            e.printStackTrace();
            return false;
        }
    }
    
    
    
    // Method to update the attendance records for the students
    public static boolean updateGrades(TreeMap<Integer, TextField> studentGradesMap, int assignment_ID, int course_ID){
        // Begin SQL connection
        try{
            // Establish connection
            Connection con = connectDB();
            
            // Create PreparedStatement
            PreparedStatement updateGradesStmt = con.prepareStatement("UPDATE StudentAssignmentGrades SET grade = ? "
                                                                + "WHERE student_ID = ? AND assignment_ID = ?");
            // Begin iterating through the studentAttendanceMap
            for(Map.Entry<Integer, TextField> entry : studentGradesMap.entrySet()){
                // Save temp values
                int tempStudentID = entry.getKey();
                TextField tempField = entry.getValue();
                float gradeValue = Float.parseFloat(tempField.getText());
                
                // Print for debugging
                System.out.println("Student ID #" + tempStudentID + " grade value: " + gradeValue);
                
                // Bind Params
                updateGradesStmt.setFloat(1, gradeValue);
                updateGradesStmt.setInt(2, tempStudentID);
                updateGradesStmt.setInt(3, assignment_ID);
                
                // Execute update query
                updateGradesStmt.executeUpdate();
            }
            
            // Close statement & connection 
            updateGradesStmt.close();
            con.close();
            
            // Check if all assignments have been graded
            if(checkAssignmentGrades(course_ID)){
                System.out.println("YAY! All assignments have been graded!");
                
                // Move forward with calculating the overall grades of each student in THIS course
                calculateOverallCourseGrade(course_ID);
            }
            else{
                System.out.println("BOO! NOT all assignments have been graded.");
            }
            
            // Return true
            return true;
        }
        catch(Exception e){
            // Print error
            e.printStackTrace();
            return false;
        }
    }
    
    
    // Method to check if all assignments in a given course have been completed. 
    // If they have, we can proceed to calculate student OVERALL weighted grades for that particular course
    private static boolean checkAssignmentGrades(int course_ID){
        // Boolean to be returned
        boolean allAssignmentsGraded = false;
        // Begin by connection to DB and creating a PreparedStatement
        try{
            // Create connection
            Connection con = connectDB();
            
            // Create prepared statement
            PreparedStatement checkAssignmentIDStmt = con.prepareStatement("SELECT assignment_ID FROM CourseAssignment WHERE course_ID = ?");
            
            // Bind Params
            checkAssignmentIDStmt.setInt(1, course_ID);
            
            // Execute & get results
            ResultSet assignmentIDSet = checkAssignmentIDStmt.executeQuery();
            
            // Iterate through results
            while(assignmentIDSet.next()){
                // Save assignment_ID
                int tempAssignmentID = assignmentIDSet.getInt("assignment_ID");
                
                // Create next PreparedStatement to check a SINGLE grade using the assignment_ID
                PreparedStatement checkGradesStmt = con.prepareStatement("SELECT grade FROM StudentAssignmentGrades WHERE assignment_ID = ? LIMIT 1");
                
                // Bind Params
                checkGradesStmt.setInt(1, tempAssignmentID);
                
                // Execute & get results
                ResultSet gradesSet = checkGradesStmt.executeQuery();
                
                // Iterate through results
                while(gradesSet.next()){
                    // Save the grade
                    float tempGrade = gradesSet.getFloat("grade");
                    
                    if(tempGrade == 0){
                        // Prevent overall grade calculation
                        System.out.println("Not all assignments have had grades inserted. Preventing overall course grade calculation");
                        allAssignmentsGraded = false;
                    }
                    else{
                        // Set allAssignmentsGraded to true
                        allAssignmentsGraded = true;
                    }
                }
                // Close nested set and stmt
                gradesSet.close();
                checkGradesStmt.close();
            }
            // Close set, stmt, and connection
            assignmentIDSet.close();
            checkAssignmentIDStmt.close();
            con.close();
            
            return allAssignmentsGraded;
        }
        catch(Exception e){
            // Print stacktrace
            e.printStackTrace();
            return false;
        }
    }
    
    
    // Method to calculate the average course grade
    public static float calculateAverageCourseGrade(int courseID_ARG){
        // Save tempGrade
        float tempAverageGrade = 0.0f;
        int gradesCount = 0;
        
        // Start by creating connection
        try{
            // Create connection
            Connection con = connectDB();
            
            // Create PreparedStatement
            PreparedStatement getCourseGrades = con.prepareStatement("SELECT grade FROM StudentCourseGrades WHERE course_ID = ?");
            
            // Bind params
            getCourseGrades.setInt(1, courseID_ARG);
            
            // Execute
            ResultSet courseGradesSet = getCourseGrades.executeQuery();
            
            // Iterate through 
            while(courseGradesSet.next()){
                // Save temp grade
                float currentGrade = courseGradesSet.getFloat("grade");
                
                // Update average grade
                tempAverageGrade = tempAverageGrade + currentGrade;
                gradesCount++;
            }
            
            // Close stmt/set
            courseGradesSet.close();
            getCourseGrades.close();
            con.close();
            
            // Finalize average Grade
            tempAverageGrade = tempAverageGrade / gradesCount;
            
            // Return average grade
            return tempAverageGrade;
            
        }
        catch(Exception e){
            // Print issue
            e.printStackTrace();
            return -1.0f;
        }
    }
    
    
    // Method to calculate a student's overall weighted grade for THE PARTICULAR course using the assignment grades provided in parameteres
    private static void calculateOverallCourseGrade(int course_ID){
        System.out.println("\n\nInside course grades calculation!\n\n");
        try{
            // First, get a listing of ALL student_ID's
            Connection con = connectDB();
            
            // Create stmt
            PreparedStatement getStudentIDs = con.prepareStatement("SELECT student_ID FROM StudentCourseGrades WHERE course_ID = ?");
            
            // Bind Params
            getStudentIDs.setInt(1, course_ID);
            
            // Execute and iterate through results
            ResultSet studentIDSet = getStudentIDs.executeQuery();
            while(studentIDSet.next()){
                // Create new ArrayList to store each weighted assignment grade which we'll then use to determine the student's overall course grade
                ArrayList<Float> weightedAssignmentGrades = new ArrayList<>();
                float finalCourseGrade = 0.0f;
                
                // Save studentID
                int tempStudentID = studentIDSet.getInt("student_ID");
                
                // Create stmt for retrieving all pertinent assignment information
                PreparedStatement getAssignmentInfo = con.prepareStatement("SELECT assignment_ID, points, weight FROM Assignments WHERE assignment_ID = ANY " 
                        + "(SELECT assignment_ID FROM CourseAssignment WHERE course_ID = ?)");
                
                // Bind Params
                getAssignmentInfo.setInt(1, course_ID);
                
                // Execute and iterate through results
                ResultSet assignmentInfoSet = getAssignmentInfo.executeQuery();
                while(assignmentInfoSet.next()){
                    // Save all pertinent assignment information
                    int tempAssignmentID = assignmentInfoSet.getInt("assignment_ID");
                    float tempPoints = assignmentInfoSet.getFloat("points");
                    float tempWeight = assignmentInfoSet.getFloat("weight");
                    
                    // Create stmt for retrieving all grades from each assignment for each studentt
                    PreparedStatement getStudentAssignmentGrades = con.prepareStatement("SELECT grade FROM StudentAssignmentGrades WHERE student_ID = ? AND assignment_ID = ?");
                    
                    // Bind Params
                    getStudentAssignmentGrades.setInt(1, tempStudentID);
                    getStudentAssignmentGrades.setInt(2, tempAssignmentID);
                    
                    // Execute and iterate through results
                    ResultSet studentAssignmentGradesSet = getStudentAssignmentGrades.executeQuery();
                    while(studentAssignmentGradesSet.next()){
                        // Save the grade
                        float tempGradePoints = studentAssignmentGradesSet.getFloat("grade");
                        float tempRealGrade = tempGradePoints/tempPoints;
                        
                        // Begin course grade calculation
                        float weightDecimal = tempWeight/100;
                        float weightedGrade = tempRealGrade * weightDecimal;
                        
                        // Add the weightedGrade to list
                        weightedAssignmentGrades.add(weightedGrade);
                    }
                    
                    // Close set & stmt
                    studentAssignmentGradesSet.close();
                    getStudentAssignmentGrades.close();
                }
                // Use the weightedAssignmentGrades list to calculate the FNAL course grade
                for(float grade : weightedAssignmentGrades){
                    // Add grades up
                    finalCourseGrade = grade+finalCourseGrade;
                }
                
                // Update the student's course grade
                updateStudentCourseGrade(tempStudentID, course_ID, finalCourseGrade);
                
                // Close set & stmt
                assignmentInfoSet.close();
                getAssignmentInfo.close();
                
                
            }
            // Close set, stmt, and con
            studentIDSet.close();
            getStudentIDs.close();
            con.close();
            
        }
        catch(Exception e){
            // Print stack trace
            e.printStackTrace();
        }  
    }
    
    
    // Method to update the proper table and insert final course grade
    private static void updateStudentCourseGrade(int studentID, int course_ID, float finalCourseGrade){
        // First,create connection and statement
        try{
            // Create connection
            Connection con = connectDB();
            
            // Create PreparedStatement
            PreparedStatement updateStudentCourseGrade = con.prepareStatement("UPDATE StudentCourseGrades SET grade = ? "
                            + "WHERE student_ID = ? AND course_ID = ?");
            
            // Bind Params
            updateStudentCourseGrade.setFloat(1, finalCourseGrade);
            updateStudentCourseGrade.setInt(2, studentID);
            updateStudentCourseGrade.setInt(3, course_ID);
            
            // Execute update
            updateStudentCourseGrade.executeUpdate();
            
            // Close stmt and connection
            updateStudentCourseGrade.close();
            con.close();
            
            System.out.println("Final Course Grade for StudentID: " + studentID + " in courseID: " + course_ID + " is: " + (finalCourseGrade * 100));
        }
        catch(Exception e){
            // Print stack trace
            e.printStackTrace();
        }
    }
    
    // Method to determine student rankings per grade level based on overall GPA
    public static TreeMap<Integer, TreeMap<Float, ArrayList<Integer>>> calculateStudentRankings(){
        // Save temp variables
        TreeMap<Integer, TreeMap<Float, ArrayList<Integer>>> studentRankMap = new TreeMap<>();
                
        // First create a connection
        try{
            // Connection
            Connection con = connectDB();
            
            // Create PreparedStatement
            PreparedStatement getGradeLevels = con.prepareStatement("SELECT DISTINCT gradeLevel FROM StudentGPA");
            
            // Execute query
            ResultSet gradeLevelSet = getGradeLevels.executeQuery();
            
            // Iterate through the results
            while(gradeLevelSet.next()){
                // Create new TreeMap for storing unique GPAs for each gradeLevel
                TreeMap<Float, ArrayList<Integer>> gpaMap = new TreeMap<>(Collections.reverseOrder());
                // Save gradeLevel
                int tempGradeLevel = gradeLevelSet.getInt("gradeLevel");
                
                // Create next query
                PreparedStatement getGPAs = con.prepareStatement("SELECT DISTINCT studentGPA FROM StudentGPA WHERE gradeLevel = ?");
                
                // Bind params
                getGPAs.setInt(1, tempGradeLevel);
                
                // Execute query
                ResultSet studentGPASet = getGPAs.executeQuery();
                
                // Iterate through results
                while(studentGPASet.next()){
                    // Crate new ArrayList for each unique GPA to store studentID's that have that GPA
                    ArrayList<Integer> studentIDList = new ArrayList<>();
                    // Save GPA
                    float tempGPA = studentGPASet.getFloat("studentGPA");
                    
                    // Create next query
                    PreparedStatement getStudentIDs = con.prepareStatement("SELECT DISTINCT student_ID FROM StudentGPA WHERE gradeLevel = ? AND studentGPA = ?");
                    
                    // Bind Params
                    getStudentIDs.setInt(1, tempGradeLevel);
                    getStudentIDs.setFloat(2, tempGPA);
                    
                    // Execute query
                    ResultSet studentIDSet = getStudentIDs.executeQuery();
                    
                    // Iterate through resutls
                    while(studentIDSet.next()){
                        // Save temp student ID
                        int tempStudentID = studentIDSet.getInt("student_ID");
                        System.out.println("Adding " + tempStudentID + " to list for GPA " + tempGPA + " in gradeLevel " + tempGradeLevel);
                        
                        // Add studentID to the list
                        studentIDList.add(tempStudentID);
                    }
                    // Close the stmt
                    studentIDSet.close();
                    getStudentIDs.close();
                    
                    // Add the studentIDList to the TreeMap
                    gpaMap.put(tempGPA, studentIDList);
                }
                // Close the stmt
                studentGPASet.close();
                getGPAs.close();
                
                // Add the gpaMap to the master map
                studentRankMap.put(tempGradeLevel, gpaMap);
            }
            // Close the stmt
            gradeLevelSet.close();
            getGradeLevels.close();
            con.close();
            
            // Print map for testing
            System.out.println("TEST: " + studentRankMap.toString());
            
            // Return
            return studentRankMap;
        }
        catch(Exception e){
            // Print
            e.printStackTrace();
            
            return null;
        }
    }
    
    
    // Method to calculate the GPA for a given student
    private static float calculateGPA(ArrayList<TreeMap<String, String>> courseDataList_ARG, int studentID_ARG){
        // Save temp variables
        float studentGPA = 0.0f;
        ArrayList<String> courseGrades = new ArrayList<>();
        ArrayList<Float> courseGradesDigits = new ArrayList<>();
        // Iternate through the given Maps to calculate an overall GPA for the student
        for(TreeMap<String, String> map : courseDataList_ARG){
            // Save tempCourseGradeChar
            String tempCourseGradeChar = map.get("FinalCourseGradeChar");
            courseGrades.add(tempCourseGradeChar);
        }
        
        // Calculate the overall GPA
        for(String grade : courseGrades){
            // Determine the scale value of the grade
            switch(grade){
                case "A+":
                    courseGradesDigits.add(4.0f);
                    break;
                case "A":
                    courseGradesDigits.add(4.0f);
                    break;
                case "A-":
                    courseGradesDigits.add(3.7f);
                    break;
                case "B+":
                    courseGradesDigits.add(3.3f);
                    break;
                case "B":
                    courseGradesDigits.add(3.0f);
                    break;
                case "B-":
                    courseGradesDigits.add(2.7f);
                    break;
                case "C+":
                    courseGradesDigits.add(2.3f);
                    break;
                case "C":
                    courseGradesDigits.add(2.0f);
                    break;
                case "C-":
                    courseGradesDigits.add(1.7f);
                    break;
                case "D+":
                    courseGradesDigits.add(1.3f);
                    break;
                case "D":
                    courseGradesDigits.add(1.0f);
                    break;
                case "F":
                    courseGradesDigits.add(0.0f);
                    break;
            }
        }
        
        // Now, calculate the GPA
        for(Float digitGrade : courseGradesDigits){
            // Add all digitGrades together
            studentGPA = studentGPA + digitGrade;
        }
        
        // Divide studentGPA by number of classes
        studentGPA = studentGPA / Float.parseFloat(String.valueOf(courseGrades.size()));
        System.out.println("\n\tUpdating GPA for studentID: " + studentID_ARG + " GPA will be: " + studentGPA + "\n");
        // Save the GPA to the database
        updateStudentGPA(studentGPA, studentID_ARG);
        
        // Test
        //TreeMap<Integer, TreeMap<Float, ArrayList<Integer>>> test = calculateStudentRankings();
        //System.out.println("\n\nTEST: " + test.toString());
        
        return studentGPA;
    }
    
    // Method to update the given student's GPA
    private static void updateStudentGPA(float studentGPA_ARG, int studentID_ARG){
        // Create connection
        try{
            // Create con
            Connection con = connectDB();
            
            // Create PreparedStatement
            PreparedStatement insertStudentGPA = con.prepareStatement("UPDATE StudentGPA SET studentGPA = ? WHERE student_ID = ?");
            
            // Bind params
            insertStudentGPA.setFloat(1, studentGPA_ARG);
            insertStudentGPA.setInt(2, studentID_ARG);
            
            // Execute query
            insertStudentGPA.executeUpdate();
            
            // Close stmt and connection
            insertStudentGPA.close();
            con.close();
        }
        catch(Exception e){
            // Print
            e.printStackTrace();
        }
    }
    
    
    //---------------------------- StudentID , {"StudentInfoMap"["StudentName": "Joe", "gradeLevel": 7], "CourseInfoMap"["CourseID": 3, "courseName": "English"]}
    public static ArrayList<TreeMap<Integer, ArrayList<TreeMap<String, ArrayList<TreeMap<String, String>>>>>> createMasterStudentReportList(){
        /**
         * Step 1:
         *  - Get EACH studentID in the database
         * Step 2:
         *  - Use the StudentID to retrieve student information RELEVANT to the studentID (firstName, lastName, etc.)
         *  - Use the StudentID to retrieve course information RELEVANT to the studentID (what courses the studentID is paired with in the StudentCourseGrades)
         * Step 3:
         *  - Create a new TreeMap for each studentID & save student and course information
         */
        
        // Boolean value for warning
        boolean continueAnyway = false;
        // First check the grades to ensure that each student has had a course grade entered
        // (it should get automatically entered once EACH assignment grade for the course is no longer NULL/0)
        if(!checkCourseGrades()){
            // Prompt user and store the user's answer
            continueAnyway = TA_AlertPage.alert("WARNING!", "Not all course grades for each student have been filled out."
                    + "\nTo fix this, make sure EACH student has recieved a grade for EACH assignment in EACH course.\nAre you sure you want to proceed?", Color.CORAL, true);
        }
        
        // If the user wishes to continue, let them continue, otherwise prevent the reports from being generated
        if(!continueAnyway){
            // Prevent the reports from being generated
            return null;
        }
        else{
            // Create MASTER student list
            ArrayList<TreeMap<Integer, ArrayList<TreeMap<String, ArrayList<TreeMap<String, String>>>>>> masterStudentList = new ArrayList<>();

            try{
            // Create DB connection
            Connection con = connectDB();

            // Create FIRST PreparedStatement
            PreparedStatement getStudentID = con.prepareStatement("SELECT student_ID from Students");

            ResultSet studentIDSet = getStudentID.executeQuery();

            // Begin iterating through the studentIDSet results
            while(studentIDSet.next()){
                // Default value for studentGPA
                float studentGPA = 0.0f;
                
                // Save student_ID
                int studentID = studentIDSet.getInt("student_ID");
                
                System.out.println("Student ID: " + studentID);

                // Create new TreeMap for the student_ID
                TreeMap<Integer, ArrayList<TreeMap<String, ArrayList<TreeMap<String, String>>>>> studentIDMap = new TreeMap<>();

                // Create new ArrayList for holding all STUDENT and COURSE information
                ArrayList<TreeMap<String, ArrayList<TreeMap<String, String>>>> studentInformationList = new ArrayList<>();

                // Create new TreeMap for holding the STUDENT information (the ENTIRE studentData)
                TreeMap<String, ArrayList<TreeMap<String, String>>> studentDataMap = new TreeMap<>();
                // Create new ArrayList to hold all studentDataMaps
                ArrayList<TreeMap<String, String>> studentDataList = new ArrayList<>();

                // Create new TreeMap for holding the COURSE information RELEVANT to the studentID
                TreeMap<String, ArrayList<TreeMap<String, String>>> courseDataMap = new TreeMap<>();
                // Create new ArrayList to hold all courseDataMaps
                ArrayList<TreeMap<String, String>> courseDataList = new ArrayList<>();

                
                // ----------------------------------------------- COURSE QUERY -----------------------------------------------

                // Create next PreparedStatement for courses
                PreparedStatement getCourseRelevantInformation = con.prepareStatement("SELECT * FROM Courses WHERE course_ID = ANY "
                                                                                + "(SELECT course_ID FROM StudentCourseGrades WHERE student_ID = ?)");

                // Bind params
                getCourseRelevantInformation.setInt(1, studentID);

                // Execute SECOND query
                ResultSet courseInformationSet = getCourseRelevantInformation.executeQuery();

                // Begin iterating through the courseInformationSet results and add all courseData to the courseDataList
                while(courseInformationSet.next()){
                    // Create new TreeMap for holding the COURSE DATA (i.e. course_ID, courseSubject, etc.)
                    TreeMap<String, String> courseData = new TreeMap<>();
                    System.out.println("\tCourse ID: " + String.valueOf(courseInformationSet.getInt("course_ID")));

                    // Save data in appropriate value/key pairs
                    courseData.put("CourseID", String.valueOf(courseInformationSet.getInt("course_ID")));
                    courseData.put("CourseSubject", courseInformationSet.getString("courseSubject"));
                    courseData.put("GradeLevel", String.valueOf(courseInformationSet.getInt("gradeLevel")));
                    courseData.put("CourseNumber", String.valueOf(courseInformationSet.getInt("courseNumber")));
                    courseData.put("Semester", courseInformationSet.getString("semester"));


                    // ----------------------------------------------- COURSE-GRADES QUERY -----------------------------------------------


                    // Create next PreparedStatement to get the student's grade in THIS course
                    PreparedStatement getStudentCourseGrade = con.prepareStatement("SELECT grade FROM StudentCourseGrades WHERE student_ID = ? AND course_ID = ?");

                    // Bind params
                    getStudentCourseGrade.setInt(1, studentID);
                    getStudentCourseGrade.setInt(2, courseInformationSet.getInt("course_ID"));

                    // Execute THIRD query
                    ResultSet studentCourseGradeSet = getStudentCourseGrade.executeQuery();

                    // Begin iterating through the studentCourseGradeSet results and append the grade to THIS courseData map
                    while(studentCourseGradeSet.next()){
                        // Save tempGrade
                        float courseDigit = studentCourseGradeSet.getFloat("grade") * 100;
                        System.out.println("\t\tCourse Digit: " + courseDigit);
                        
                        // Store the raw number grade
                        courseData.put("FinalCourseGrade", String.valueOf(courseDigit));
                        
                        // FinalCourseGradeChar
                        String officialCourseGrade = "F";
                        officialCourseGrade = (courseDigit > 96.99) ? "A+" : officialCourseGrade;
                        officialCourseGrade = (courseDigit > 92.99 && courseDigit < 97.00) ? "A" : officialCourseGrade;
                        officialCourseGrade = (courseDigit > 89.99 && courseDigit < 93.00) ? "A-" : officialCourseGrade;

                        officialCourseGrade = (courseDigit > 86.99 && courseDigit < 90.00) ? "B+" : officialCourseGrade;
                        officialCourseGrade = (courseDigit > 82.99 && courseDigit < 87.00) ? "B" : officialCourseGrade;
                        officialCourseGrade = (courseDigit > 79.99 && courseDigit < 83.00) ? "B-" : officialCourseGrade;

                        officialCourseGrade = (courseDigit > 76.99 && courseDigit < 80.00) ? "C+" : officialCourseGrade;
                        officialCourseGrade = (courseDigit > 72.99 && courseDigit < 77.00) ? "C" : officialCourseGrade;
                        officialCourseGrade = (courseDigit > 69.99 && courseDigit < 73.00) ? "C-" : officialCourseGrade;

                        officialCourseGrade = (courseDigit > 66.99 && courseDigit < 70.00) ? "D+" : officialCourseGrade;
                        officialCourseGrade = (courseDigit > 64.99 && courseDigit < 67.00) ? "D" : officialCourseGrade;
                        officialCourseGrade = (courseDigit < 65.00) ? "F" : officialCourseGrade;
                        
                        // Store the letter grade
                        courseData.put("FinalCourseGradeChar", officialCourseGrade);
                        System.out.println("\t\tCourse Grade: " + officialCourseGrade);
                    }
                    // Add courseData to the courseDataList
                    courseDataList.add(courseData);

                    // Close courseGrades statements
                    studentCourseGradeSet.close();
                    getStudentCourseGrade.close();
                }
                // Calculate the student's GPA based on the FinalCourseGradeChar of each course
                studentGPA = calculateGPA(courseDataList, studentID);
                System.out.println("\t\tOverall GPA: " + studentGPA);
                
                // Add courseDataList to the courseDataMap
                courseDataMap.put("CourseInformation", courseDataList);

                // Close course statements
                courseInformationSet.close();
                getCourseRelevantInformation.close();
                

                // ----------------------------------------------- STUDENT QUERY -----------------------------------------------

                // Create next PreparedStatement
                PreparedStatement getStudentRelevantInformation = con.prepareStatement("SELECT firstName, middleName, lastName, gradeLevel, major FROM Students "
                                                                                        + "WHERE student_ID = ?");

                // Bind params
                getStudentRelevantInformation.setInt(1, studentID);

                // Execute SECOND query
                ResultSet studentInformationSet = getStudentRelevantInformation.executeQuery();

                // Begin iterating through the studentInformationSet results and add all studentData to the studentDataList
                while(studentInformationSet.next()){
                    // Create new TreeMap for holding the STUDENT DATA (i.e. firstName, lastName, etc.)
                    TreeMap<String, String> studentData = new TreeMap<>();

                    // Save data in appropriate value/key pairs
                    studentData.put("FirstName", studentInformationSet.getString("firstName"));
                    studentData.put("MiddleName", studentInformationSet.getString("middleName"));
                    studentData.put("LastName", studentInformationSet.getString("lastName"));
                    studentData.put("GradeLevel", studentInformationSet.getString("gradeLevel"));
                    studentData.put("Major", studentInformationSet.getString("major"));
                    
                    // Save the studentGPA
                    studentData.put("StudentGPA", String.valueOf(studentGPA));
                    System.out.println("Student GPA is: " + studentGPA);

                    studentDataList.add(studentData);
                }

                // Put studentData into the StudentDataMap
                studentDataMap.put("StudentInformation", studentDataList);

                // Close statement/set
                studentInformationSet.close();
                getStudentRelevantInformation.close();

                // Add each Data Map to the studentInformationList
                studentInformationList.add(studentDataMap);
                studentInformationList.add(courseDataMap);

                // Add the studentID and studentInformationList to the studentIDMap
                studentIDMap.put(studentID, studentInformationList);

                // Finally, add the studentIDMap to the MASTER list (masterStudentList)
                masterStudentList.add(studentIDMap);
            }


            // Close statements & connection
            studentIDSet.close();
            getStudentID.close();
            con.close();

            // print map
            printMasterStudentList(masterStudentList);

            // Return the MASTER list
            return masterStudentList;
            }
            catch(Exception e){
                // Prink stack trace
                e.printStackTrace();
                return null;
            }
        }
        
        
    }
    
    
    // Overloaded method used to create the masterStudentList when the user selects specific checkboxes on report page
    public static ArrayList<TreeMap<Integer, ArrayList<TreeMap<String, ArrayList<TreeMap<String, String>>>>>> createMasterStudentReportList(TreeMap<Integer,CheckBox> selectedStudentsMap){
        // Boolean for warning
        boolean continueAnyway = false;
        // First check the grades to ensure that each student has had a course grade entered
        // (it should get automatically entered once EACH assignment grade for the course is no longer NULL/0)
        if(!checkCourseGrades()){
            // Prompt user and store the user's answer
            continueAnyway = TA_AlertPage.alert("WARNING!", "Not all course grades for each student have been filled out."
                    + "\nTo fix this, make sure EACH student has recieved a grade for EACH assignment in EACH course.\nAre you sure you want to proceed?", Color.CORAL, true);
        }
        
        // If the user wishes to continue, let them continue, otherwise prevent the reports from being generated
        if(!continueAnyway){
            // Prevent the reports from being generated
            return null;
        }
        else{
            // Create MASTER student list
            ArrayList<TreeMap<Integer, ArrayList<TreeMap<String, ArrayList<TreeMap<String, String>>>>>> masterStudentList = new ArrayList<>();

            try{
                // Create DB connection
                Connection con = connectDB();

                // Iterate through the GIVEN TreeMap and use the key as the studentID rather than using a PreparedStatement
                for(Map.Entry<Integer, CheckBox> selectedStudent : selectedStudentsMap.entrySet()){
                    // Save the temp studentGPA
                    float studentGPA = 0.0f;
                    
                    // Save student_ID
                    int studentID = selectedStudent.getKey();

                    // Create new TreeMap for the student_ID
                    TreeMap<Integer, ArrayList<TreeMap<String, ArrayList<TreeMap<String, String>>>>> studentIDMap = new TreeMap<>();

                    // Create new ArrayList for holding all STUDENT and COURSE information
                    ArrayList<TreeMap<String, ArrayList<TreeMap<String, String>>>> studentInformationList = new ArrayList<>();

                    // Create new TreeMap for holding the STUDENT information (the ENTIRE studentData)
                    TreeMap<String, ArrayList<TreeMap<String, String>>> studentDataMap = new TreeMap<>();
                    // Create new ArrayList to hold all studentDataMaps
                    ArrayList<TreeMap<String, String>> studentDataList = new ArrayList<>();

                    // Create new TreeMap for holding the COURSE information RELEVANT to the studentID
                    TreeMap<String, ArrayList<TreeMap<String, String>>> courseDataMap = new TreeMap<>();
                    // Create new ArrayList to hold all courseDataMaps
                    ArrayList<TreeMap<String, String>> courseDataList = new ArrayList<>();


                    // ----------------------------------------------- COURSE QUERY -----------------------------------------------

                    // Create next PreparedStatement for courses
                    PreparedStatement getCourseRelevantInformation = con.prepareStatement("SELECT * FROM Courses WHERE course_ID = ANY "
                                                                                    + "(SELECT course_ID FROM StudentCourseGrades WHERE student_ID = ?)");

                    // Bind params
                    getCourseRelevantInformation.setInt(1, studentID);

                    // Execute SECOND query
                    ResultSet courseInformationSet = getCourseRelevantInformation.executeQuery();

                    // Begin iterating through the courseInformationSet results and add all courseData to the courseDataList
                    while(courseInformationSet.next()){
                        // Create new TreeMap for holding the COURSE DATA (i.e. course_ID, courseSubject, etc.)
                        TreeMap<String, String> courseData = new TreeMap<>();

                        // Save data in appropriate value/key pairs
                        courseData.put("CourseID", String.valueOf(courseInformationSet.getInt("course_ID")));
                        courseData.put("CourseSubject", courseInformationSet.getString("courseSubject"));
                        courseData.put("GradeLevel", String.valueOf(courseInformationSet.getInt("gradeLevel")));
                        courseData.put("CourseNumber", String.valueOf(courseInformationSet.getInt("courseNumber")));
                        courseData.put("Semester", courseInformationSet.getString("semester"));


                        // ----------------------------------------------- COURSE-GRADES QUERY -----------------------------------------------


                        // Create next PreparedStatement to get the student's grade in THIS course
                        PreparedStatement getStudentCourseGrade = con.prepareStatement("SELECT grade FROM StudentCourseGrades WHERE student_ID = ? AND course_ID = ?");

                        // Bind params
                        getStudentCourseGrade.setInt(1, studentID);
                        getStudentCourseGrade.setInt(2, courseInformationSet.getInt("course_ID"));

                        // Execute THIRD query
                        ResultSet studentCourseGradeSet = getStudentCourseGrade.executeQuery();

                        // Begin iterating through the studentCourseGradeSet results and append the grade to THIS courseData map
                        while(studentCourseGradeSet.next()){
                            // Save tempGrade
                            float courseDigit = studentCourseGradeSet.getFloat("grade") * 100;
                            System.out.println("\t\tCourse Digit: " + courseDigit);

                            // Store the raw number grade
                            courseData.put("FinalCourseGrade", String.valueOf(courseDigit));

                            // FinalCourseGradeChar
                            String officialCourseGrade = "NULL";
                            officialCourseGrade = (courseDigit > 96.99) ? "A+" : officialCourseGrade;
                            officialCourseGrade = (courseDigit > 92.99 && courseDigit < 97.00) ? "A" : officialCourseGrade;
                            officialCourseGrade = (courseDigit > 89.99 && courseDigit < 93.00) ? "A-" : officialCourseGrade;

                            officialCourseGrade = (courseDigit > 86.99 && courseDigit < 90.00) ? "B+" : officialCourseGrade;
                            officialCourseGrade = (courseDigit > 82.99 && courseDigit < 87.00) ? "B" : officialCourseGrade;
                            officialCourseGrade = (courseDigit > 79.99 && courseDigit < 83.00) ? "B-" : officialCourseGrade;

                            officialCourseGrade = (courseDigit > 76.99 && courseDigit < 80.00) ? "C+" : officialCourseGrade;
                            officialCourseGrade = (courseDigit > 72.99 && courseDigit < 77.00) ? "C" : officialCourseGrade;
                            officialCourseGrade = (courseDigit > 69.99 && courseDigit < 73.00) ? "C-" : officialCourseGrade;

                            officialCourseGrade = (courseDigit > 66.99 && courseDigit < 70.00) ? "D+" : officialCourseGrade;
                            officialCourseGrade = (courseDigit > 64.99 && courseDigit < 67.00) ? "D" : officialCourseGrade;
                            officialCourseGrade = (courseDigit < 65.00) ? "F" : officialCourseGrade;

                            // Store the letter grade
                            courseData.put("FinalCourseGradeChar", officialCourseGrade);
                            System.out.println("\t\tCourse Grade: " + officialCourseGrade);
                        
                        }
                        // Add courseData to the courseDataList
                        courseDataList.add(courseData);

                        // Close courseGrades statements
                        studentCourseGradeSet.close();
                        getStudentCourseGrade.close();
                    }
                    // Calculate the student's GPA based on the FinalCourseGradeChar of each course
                    studentGPA = calculateGPA(courseDataList, studentID);
                    System.out.println("\t\tOverall GPA: " + studentGPA);
                        
                    // Add courseDataList to the courseDataMap
                    courseDataMap.put("CourseInformation", courseDataList);

                    // Close course statements
                    courseInformationSet.close();
                    getCourseRelevantInformation.close();
                    
                    
                    // ----------------------------------------------- STUDENT QUERY -----------------------------------------------

                    // Create next PreparedStatement
                    PreparedStatement getStudentRelevantInformation = con.prepareStatement("SELECT firstName, middleName, lastName, gradeLevel, major FROM Students "
                                                                                            + "WHERE student_ID = ?");

                    // Bind params
                    getStudentRelevantInformation.setInt(1, studentID);

                    // Execute SECOND query
                    ResultSet studentInformationSet = getStudentRelevantInformation.executeQuery();

                    // Begin iterating through the studentInformationSet results and add all studentData to the studentDataList
                    while(studentInformationSet.next()){
                        // Create new TreeMap for holding the STUDENT DATA (i.e. firstName, lastName, etc.)
                        TreeMap<String, String> studentData = new TreeMap<>();

                        // Save data in appropriate value/key pairs
                        studentData.put("FirstName", studentInformationSet.getString("firstName"));
                        studentData.put("MiddleName", studentInformationSet.getString("middleName"));
                        studentData.put("LastName", studentInformationSet.getString("lastName"));
                        studentData.put("GradeLevel", studentInformationSet.getString("gradeLevel"));
                        studentData.put("Major", studentInformationSet.getString("major"));
                        
                        // Save the studentGPA
                        studentData.put("StudentGPA", String.valueOf(studentGPA));
                        System.out.println("Student GPA is: " + studentGPA);

                        studentDataList.add(studentData);
                    }

                    // Put studentData into the StudentDataMap
                    studentDataMap.put("StudentInformation", studentDataList);

                    // Close statement/set
                    studentInformationSet.close();
                    getStudentRelevantInformation.close();

                    // Add each Data Map to the studentInformationList
                    studentInformationList.add(studentDataMap);
                    studentInformationList.add(courseDataMap);

                    // Add the studentID and studentInformationList to the studentIDMap
                    studentIDMap.put(studentID, studentInformationList);

                    // Finally, add the studentIDMap to the MASTER list (masterStudentList)
                    masterStudentList.add(studentIDMap);
                }

            // Close Connection
            con.close();

            // print map
            printMasterStudentList(masterStudentList);

            // Return the MASTER list
            return masterStudentList;
            }
            catch(Exception e){
                // Prink stack trace
                e.printStackTrace();
                return null;
            }
        }
        
        
    }
    
    // Method to check the course grades of each student
    private static boolean checkCourseGrades(){
        // Begin by establishing a connection and creating proper PreparedStatements
        boolean gradesAreGood = false;
        try{
            // Create connection
            Connection con = connectDB();
            
            // Create PreparedStatement
            PreparedStatement checkCourseGradesStmt = con.prepareStatement("SELECT grade FROM StudentCourseGrades");
            
            // Execute stmt
            ResultSet set = checkCourseGradesStmt.executeQuery();
            
            // Iterate through set
            while(set.next()){
                // Check each record to see if its null or not
                float tempGrade = set.getInt("grade");
                
                if(tempGrade == 0){
                    // Set gradesAreGood to false
                    gradesAreGood = false;
                }
                else{
                    // Set to true
                    gradesAreGood = true;
                }
            }
            
            // Return true
            return gradesAreGood;
        }
        catch(Exception e){
            // Print
            e.printStackTrace();
            return false;
        }
    }
    
    
    
    // Method to save the StudentAssignmentGrades from the DB to a local data structure
    public static TreeMap<Integer, TreeMap<Integer, Float>> createCourseGradesList(){
        // Create ArrayList to return
        TreeMap<Integer, TreeMap<Integer, Float>> studentCourseGrades = new TreeMap<>();
        
        // Begin by connecting to DB 
        try{
            // Connect to DB
            Connection con = connectDB();
            
            // Create PreparedStatement to get all Student_ID's
            PreparedStatement getStudentIDs = con.prepareStatement("SELECT DISTINCT student_ID FROM StudentCourseGrades ORDER BY student_ID");
            
            ResultSet studentIDSet = getStudentIDs.executeQuery();
            
            // Iterate through the results and execute sub-queries
            while(studentIDSet.next()){
                // Save student_ID
                int tempStudentID = studentIDSet.getInt("student_ID");
                System.out.println("Student ID: " + tempStudentID);
                
                // NOW we generate one other map for each thing
                TreeMap<Integer, Float> courseGrades = new TreeMap<>();
                
                // Create PreparedStatement
                PreparedStatement getStudentCourseGrades = con.prepareStatement("SELECT course_ID, grade FROM StudentCourseGrades WHERE student_ID = ? ORDER BY student_ID");

                // Bind params
                getStudentCourseGrades.setInt(1, tempStudentID);
                
                // Execute query and save results
                ResultSet courseGradesSet = getStudentCourseGrades.executeQuery();

                // Iterate through results
                while(courseGradesSet.next()){
                    // Temp variables
                    int tempCourse_ID = courseGradesSet.getInt("course_ID");
                    float tempGrade = courseGradesSet.getFloat("grade");
                    
                    System.out.println("\tCourse ID:" + tempCourse_ID);
                    System.out.println("\t\tGrade: " + tempGrade);
                    
                    // Create new entry
                    courseGrades.put(tempCourse_ID, tempGrade);
                    
                }
                // Close set and stmt
                courseGradesSet.close();
                getStudentCourseGrades.close();
                
                // Add the assignmentGrades into the studentAssignmentGrades and the studentAssignmentGrades into the master list
                studentCourseGrades.put(tempStudentID, courseGrades);
                
            }
            // Close set, stmt and connection
            studentIDSet.close();
            getStudentIDs.close();
            con.close();
            
            // Return masterlist
            return studentCourseGrades;
            
        }
        catch(Exception e){
            // Print stack trace
            e.printStackTrace();
            return null;
        }
    }
    
    
    
    // Method to save the StudentAssignmentGrades from the DB to a local data structure
    public static TreeMap<Integer, TreeMap<Integer, Float>> createAssignmentGradesList(){
        // Create ArrayList to return
        TreeMap<Integer, TreeMap<Integer, Float>> studentAssignmentGrades = new TreeMap<>();
        
        // Begin by connecting to DB 
        try{
            // Connect to DB
            Connection con = connectDB();
            
            // Create PreparedStatement to get all Student_ID's
            PreparedStatement getStudentIDs = con.prepareStatement("SELECT DISTINCT student_ID FROM StudentAssignmentGrades ORDER BY student_ID");
            
            ResultSet studentIDSet = getStudentIDs.executeQuery();
            
            // Iterate through the results and execute sub-queries
            while(studentIDSet.next()){
                // Save student_ID
                int tempStudentID = studentIDSet.getInt("student_ID");
                System.out.println("Student ID: " + tempStudentID);
                
                // NOW we generate one other map for each thing
                TreeMap<Integer, Float> assignmentGrades = new TreeMap<>();
                
                // Create PreparedStatement
                PreparedStatement getStudentAssignmentGrades = con.prepareStatement("SELECT assignment_ID, grade FROM StudentAssignmentGrades WHERE student_ID = ? ORDER BY student_ID");

                // Bind params
                getStudentAssignmentGrades.setInt(1, tempStudentID);
                
                // Execute query and save results
                ResultSet assignmentGradesSet = getStudentAssignmentGrades.executeQuery();

                // Iterate through results
                while(assignmentGradesSet.next()){
                    // Temp variables
                    int tempAssignment_ID = assignmentGradesSet.getInt("assignment_ID");
                    float tempGrade = assignmentGradesSet.getFloat("grade");
                    
                    System.out.println("\tAssignment ID:" + tempAssignment_ID);
                    System.out.println("\t\tGrade: " + tempGrade);
                    
                    // Create new entry
                    assignmentGrades.put(tempAssignment_ID, tempGrade);
                    
                }
                // Close set and stmt
                assignmentGradesSet.close();
                getStudentAssignmentGrades.close();
                
                // Add the assignmentGrades into the studentAssignmentGrades and the studentAssignmentGrades into the master list
                studentAssignmentGrades.put(tempStudentID, assignmentGrades);
                
            }
            // Close set, stmt and connection
            studentIDSet.close();
            getStudentIDs.close();
            con.close();
            
            // Return masterlist
            return studentAssignmentGrades;
            
        }
        catch(Exception e){
            // Print stack trace
            e.printStackTrace();
            return null;
        }
    }
    
    // Method to save the StudentAssignmentGrades from the DB to a local data structure
    public static TreeMap<Integer, TreeMap<Integer, TreeMap<LocalDate, Integer>>> createStudentAttendanceMap(){  
        // Create the master map
        TreeMap<Integer, TreeMap<Integer, TreeMap<LocalDate, Integer>>> studentCourseAttendanceMap = new TreeMap<>();
        
        // Begin with connecting to the DB
        try{
            // Connecto to DB
            Connection con = connectDB();
            
            // Create PreparedStatement for the student_ID
            PreparedStatement getStudentIDs = con.prepareStatement("SELECT DISTINCT student_ID FROM StudentAttendance");
            
            // Execute stmt
            ResultSet studentIDSet = getStudentIDs.executeQuery();
            
            // Iterate through results
            while(studentIDSet.next()){
                // Create a new courseID TreeMap for each studentID
                TreeMap<Integer, TreeMap<LocalDate, Integer>> courseIDMap = new TreeMap<>();
                // Save studentID
                int tempStudentID = studentIDSet.getInt("student_ID");
                
                System.out.println("Student ID: " + tempStudentID);
                
                // Create next PreparedStatement
                PreparedStatement getCourseIDs = con.prepareStatement("SELECT DISTINCT course_ID FROM StudentAttendance WHERE student_ID = ?");
                
                // Bind params
                getCourseIDs.setInt(1, tempStudentID);
                
                // Execute stmt
                ResultSet courseIDSet = getCourseIDs.executeQuery();
                
                // Iterate through the results
                while(courseIDSet.next()){
                    // Create a new TreeMap for each courseID
                    TreeMap<LocalDate, Integer> courseIDAttendanceMap = new TreeMap<>();
                    
                    // Save the course ID
                    int tempCourseID = courseIDSet.getInt("course_ID");
                    
                    System.out.println("\tCourse ID: " + tempCourseID);
                    
                    // Create new PreparedStatement
                    PreparedStatement getCourseAttendance = con.prepareStatement("SELECT attendanceDate, attendance FROM StudentAttendance WHERE student_ID = ? AND course_ID = ?");
                    
                    // Bind params
                    getCourseAttendance.setInt(1, tempStudentID);
                    getCourseAttendance.setInt(2, tempCourseID);
                    
                    // Execute stmt
                    ResultSet courseAttendanceSet = getCourseAttendance.executeQuery();
                    
                    // Iterate through results
                    while(courseAttendanceSet.next()){
                        // Get results
                        LocalDate tempDate = courseAttendanceSet.getDate("attendanceDate").toLocalDate();
                        int tempValue =  courseAttendanceSet.getInt("attendance");
                        
                        System.out.println("\t\tAttendance Date: " + tempDate);
                        System.out.println("\t\t\tAttendance Value: " + tempValue);
                        
                        // Insert values into proper map
                        courseIDAttendanceMap.put(tempDate, tempValue);
                    }
                    // Close stmt and set
                    courseAttendanceSet.close();
                    getCourseAttendance.close();
                    
                    // Add courseIDAttendanceMap to the courseIDMap
                    courseIDMap.put(tempCourseID, courseIDAttendanceMap);
                }
                // Close stmt and set
                courseIDSet.close();
                getCourseIDs.close();
                
                // Add courseIDMap to the master map (studentCourseAttendanceMap)
                studentCourseAttendanceMap.put(tempStudentID, courseIDMap);
            }
            // Close final stmt and set
            studentIDSet.close();
            getStudentIDs.close();
            
            return studentCourseAttendanceMap;
        }
        catch(Exception e){
            // Print stack trace
            e.printStackTrace();
            return null;
        }
        
        
    }
    
    
    // Method to reset the Database ONLY during application failure (when user deletes the config file)
    public static boolean resetDatabase() throws SQLException{
        // Local variables
        String tempString = new String();
        StringBuffer stringBuff = new StringBuffer();
 
        // Start reading the SQL file and parsing each statement
        try{
            // Get file
            FileReader fr = new FileReader(new File("C:\\Users\\Public\\Documents\\NetBeansProjects\\TeachersAssistant\\SQLFiles\\CreateTables.sql"));
            BufferedReader buffReader = new BufferedReader(fr);
 
            // Iterate through the file and append each statement as a FULL String which will then be parsed
            while((tempString = buffReader.readLine()) != null){
                stringBuff.append(tempString);
            }
            
            // Close reader
            buffReader.close();
 
            // Split each statement and add statement to array
            String[] statementArray = stringBuff.toString().split(";");
 
            // Connect to DB and start executing each statement
            Connection con = connectDB();
            Statement tempStmt = con.createStatement();
 
            // Iterate through statement arrays
            for(int i = 0; i<statementArray.length; i++){
                if(!statementArray[i].trim().equals("")){
                    // Execute statemetn
                    tempStmt.executeUpdate(statementArray[i]);
                    System.out.println("EXECUTING: "+statementArray[i]);
                }
            }
            
            // Return true
            return true;
   
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
 
    }
    
    // Method to print master student map
    private static void printMasterStudentList(ArrayList<TreeMap<Integer, ArrayList<TreeMap<String, ArrayList<TreeMap<String, String>>>>>> masterList){
        // Iterate through top level
        for(TreeMap<Integer, ArrayList<TreeMap<String, ArrayList<TreeMap<String, String>>>>> studentMap_TOP : masterList){
            // Iterate through TOP map entries
            for(Map.Entry<Integer, ArrayList<TreeMap<String, ArrayList<TreeMap<String, String>>>>> studentIDMap : studentMap_TOP.entrySet()){
                // Print studentID
                System.out.println("Student ID: " + studentIDMap.getKey());
                // Iterate through studentIDMap entries
                for(TreeMap<String, ArrayList<TreeMap<String, String>>> studentMapList : studentIDMap.getValue()){
                    // Iterate through Map entries
                    for(Map.Entry<String, ArrayList<TreeMap<String, String>>> studentTopicMap : studentMapList.entrySet()){
                        // Print Map name
                        System.out.println("\tMap Name: " + studentTopicMap.getKey());
                        // Iterate through each studentTopicMap
                        for(TreeMap<String, String> bottomTopicMap : studentTopicMap.getValue()){
                            // Iterate through each entry
                            for(Map.Entry<String, String> bottomTopicMapEntry : bottomTopicMap.entrySet()){
                                // Print everything out
                                System.out.println("\t\t"+bottomTopicMapEntry.getKey() + ": " + bottomTopicMapEntry.getValue());
                            }
                        }
                    }
                }
            }
        }
        
    }
    
    
}


