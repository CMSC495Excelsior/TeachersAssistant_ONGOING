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
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Date;
import javafx.scene.paint.Color;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author SoftwareEng
 */
public class TA_SQLFunctions {
    
    // Save ArrayList
    private static ArrayList<TreeMap<Integer, Row>> masterUploadList = new ArrayList<>();
    
    private static ArrayList<Integer> masterClassNumberList = new ArrayList<>();
    
    private static ArrayList<TreeMap> masterCList = new ArrayList<>();
    private static TreeMap<Integer, ArrayList<Integer>> masterGradeLevelList = new TreeMap<>();
    
    private static TreeMap<String, TreeMap<Integer, ArrayList<Integer>>> masterCourseList = new TreeMap<>();
    
    
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
                                            System.out.println("\n\n---- It worked! Adding " + courseMap_ID + " to pre-existing key: " + studentMap_ID + " ----");
                                        }
                                    }
                                    catch(Exception e){
                                        if(courseMap_ID != 0 && studentMap_ID != 0){
                                            System.out.println("\n\n---- It failed. Creating new key: " + studentMap_ID + " ----");
                                            // If there is no preexisting key, create a new one with that key value
                                            studentCourseIDList.add(courseMap_ID);
                                            studentCoursePairs.put(studentMap_ID, studentCourseIDList);
                                            //e.printStackTrace();
                                        }
                                        
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
                                break;
                            case 1:
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
                                // Add courseMap_ID to ArrayList
                                PreparedStatement studentCoursesInsert = con.prepareStatement("INSERT INTO StudentCourseGrades (student_ID, course_ID)"
                                    + " values (?,?)");
                                studentCoursesInsert.setInt(1, studentMap_ID);
                                studentCoursesInsert.setInt(2, courseMap_ID);

                                // Execute & close
                                studentCoursesInsert.execute();
                                studentCoursesInsert.close();
                                break;
                                
                            case 3:
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
                                PreparedStatement studentAssignmentInsert = con.prepareStatement("INSERT INTO StudentAssignmentGrades (student_ID, assignment_ID)"
                                    + " values (?,?)");
                                studentAssignmentInsert.setInt(1, studentAssignmentMap_ID);
                                studentAssignmentInsert.setInt(2, assignmentStudentMap_ID);

                                // Execute & close
                                studentAssignmentInsert.execute();
                                studentAssignmentInsert.close();
                                break;
                                
                                
                            case 5:
                                PreparedStatement courseAssignmentInsert = con.prepareStatement("INSERT INTO CourseAssignment (course_ID, assignment_ID)"
                                    + " values (?,?)");
                                courseAssignmentInsert.setInt(1, courseAssignmentMap_ID);
                                courseAssignmentInsert.setInt(2, assignmentCourseMap_ID);

                                // Execute & close
                                courseAssignmentInsert.execute();
                                courseAssignmentInsert.close();
                                break;
                                
                                
                            case 6:
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
                                
                                // Clear list
                                //studentCourseIDList.clear();
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
    
    /*
    Issue: I can't simply use the 3 static lists in the while loop below as is, since the lists won't be cleared for each subject. If I DID clear the
            lists for each subject, I would end up with an empty list because, at the end of the day, all I'd be doing is overwriting the contents of
            each list. It's almost like I either need to create a NEW list for EVERYTHING or I shove each grade level into the same list and then prefix
            each value with some identifier which will be used later to parse and identify which grade level belongs to which subject, and by extension
            which class number belongs to which grade level.
    
    */
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
        
        // Print Test
        //printMasterCourseList();
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
            PreparedStatement getAttendanceStmt = con.prepareStatement("SELECT attendanceDate FROM StudentAttendance WHERE course_ID = ?");
            
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
    
    
    
    
    
    public static void printMasterCourseList(){
        ArrayList courseList = generateCourseMap();
        // TODO: Print list
        // TreeMap<String, TreeMap<Integer, ArrayList<Integer>>>
        System.out.println("Size is: " + courseList.size());
        /*for(int i = 0; i < courseList.size(); i++){
            System.out.println("Class is: " + courseList.get(i).getClass());
            
            //TreeMap<String, TreeMap<Integer, ArrayList<Integer>>> tempMap = courseList.get(i);
            
            
        }*/
    }
    
    
}


