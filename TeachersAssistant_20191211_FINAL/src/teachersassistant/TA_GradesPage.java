/**
 * FileName: TA_GradesPage.java
 * Author: Stephen James
 * Date: 11/21/19
 * Course: CMSC-495
 * 
 * Objective: To create an application page that will be used for entering and tracking student grades.
*/

// Package
package teachersassistant;

// Import statements
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

// Main Class
public class TA_GradesPage {
    
    // Create GUI components
    private static Scene gradesScene;
    private static BorderPane borderPane;
    private static GridPane gridPane;
    private static Button attendanceButton, classPageButton, reportsPageButton, saveButton;
    private static Label assignmentPrompt, idLabel, studentLabel, gradeLabel, idLabel2, studentLabel2, gradeLabel2;
    private static HBox gradesBoxLeft_TITLE, gradesBoxRight_TITLE, gradesBody_MASTER;
    private static VBox gradesBoxLeft, gradesBoxRight;
    private static ComboBox assignmentSelectionBox;
    private static ScrollPane scrollPane;
    
    // Save Class-wide assignment details
    private static String assignmentName = "";
    private static String assignmentType = "";
    private static String assignmentDueDate = "";
    private static String assignmentPoints = "";
    private static String assignmentWeight = "";
    private static int assignmentID = 0, course_ID;
    
     // Save attendanceDates List & studentNamesMap
    private static ArrayList<TreeMap<Integer, TreeMap<String, String>>> tempMasterList;
    private static TreeMap<Integer, String> studentNamesMap;
    private static TreeMap<Integer, TextField> studentGradesEntryMap;
    
    // Main Method to return Scene object to Main Stage   
    public static Scene startGradesPage(int courseID_Arg){
        // Save the course_ID
        course_ID = courseID_Arg;
        
        // Make BorderPane
        makeBorderPane();

        // Set static vars
        gradesScene = new Scene(borderPane);
        
        // Return Scene object
        return gradesScene;
    }

    // ----------------------------------- Supplemental Methods Below -----------------------------------

    // Method for creating the borderpane
    private static BorderPane makeBorderPane(){        
        // Create ALL buttons
        classPageButton = new Button("Change Class");
        classPageButton.setAlignment(Pos.CENTER);
        classPageButton.setPadding(new Insets(5,5,5,5));
        classPageButton.setPrefSize(110, 20);
        
        attendanceButton = new Button("Attendance");
        attendanceButton.setAlignment(Pos.CENTER);
        attendanceButton.setPadding(new Insets(5,5,5,5));
        attendanceButton.setPrefSize(110, 20);
        
        reportsPageButton = new Button("Reports");
        reportsPageButton.setAlignment(Pos.CENTER);
        reportsPageButton.setPadding(new Insets(5,5,5,5));
        reportsPageButton.setPrefSize(110, 20);
        
        saveButton = new Button("Save Grades");
        saveButton.setAlignment(Pos.CENTER);
        saveButton.setPadding(new Insets(5,5,5,5));
        saveButton.setPrefSize(110, 20);
        saveButton.setStyle("-fx-background-color: #3b3a30;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: white");
        
        // Make the GridPane
        makeGridPane();
        
        // Create HBox for navigation buttons
        HBox buttonBox = null;
        buttonBox = TA_ConvenienceMethods.createHBox(buttonBox, Pos.CENTER, Double.MAX_VALUE,
                15, Color.web("#3b3a30"), 10, 10, 10, 10, attendanceButton, classPageButton, reportsPageButton);
        
        // Create a Label for the greeting
        Label greetingLabel = null;
        greetingLabel = TA_ConvenienceMethods.createLabel(greetingLabel, TeachersAssistant.getSchoolName() + 
                " > " + TeachersAssistant.getCourseName() + " > GRADES",
                Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.BOLD, 20);
        greetingLabel.setPadding(new Insets(10,10,30,10));

        // Create borderPane
        borderPane = TA_ConvenienceMethods.createBorderPane(borderPane, Color.web("#c0ded9"),greetingLabel,
                buttonBox, null, null, gridPane);
        borderPane.setPadding(new Insets(10,10,10,10));

        // Return borderPane
        return borderPane;
    }

    /**
     * NOTE: Iterate through the students in the given course_ID and create a NEW HBox for EACH entry and nest it inside the
     *      Body Boxes. Simply divide the number of records (the count) by 2 and distribute the nested HBoxes evenly between the
     *      two Body Boxes (left & right).
     */
    
    // Create the GridPane
    private static GridPane makeGridPane(){
        // Save attendanceDates List & studentNamesMap
        tempMasterList = TA_SQLFunctions.populateAssignmentList(course_ID);
        studentNamesMap = TA_SQLFunctions.populateStudentsMap_GradePage(course_ID);
        studentGradesEntryMap = new TreeMap<>();
        
        // Initialize and customize GridPane
        gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(5,5,5,5));
        gridPane.setAlignment(Pos.CENTER);
        gridPane.gridLinesVisibleProperty();
        
        
        // -------------------------------- Configure Assignment Menu --------------------------------
        
        
        // Initialize all ObservableLists for the ComboBoxes
        ObservableList<String> assignmentSelectionList = FXCollections.observableArrayList();
        
        // Create ComboBox for assignment changing
        assignmentSelectionBox = new ComboBox(assignmentSelectionList);
        
        // Populate the assignmentSelectionList using the assignment_ID and assignmentName
        for(TreeMap<Integer, TreeMap<String, String>> map : tempMasterList){
            // Iterate through each sub-TreeMap
            for(Map.Entry<Integer, TreeMap<String, String>> entry : map.entrySet()){
                // Save the assignment_ID
                int loopAssignmentID = entry.getKey();
                String loopAssignmentName = entry.getValue().get("AssignmentName");
                
                // Add the data to the assignmentSelectionList
                assignmentSelectionList.add("ID: " + loopAssignmentID + " | Name: " + loopAssignmentName);
            }
        }
        
        // Try setting the default value for assignments
        try{
            // Set default value in the assignmentSelectionBox to the first assignment
            assignmentSelectionBox.setValue(assignmentSelectionList.get(0));
        }
        catch(Exception e){
            TA_AlertPage.alert("Assignment Error", "There are no saved assignments for this class.", Color.web("#eaece5"));
        }
        
        
        // Save parsed assignment_ID
        String originalValue = String.valueOf(assignmentSelectionBox.getValue());
        String parsedValue = originalValue.substring((originalValue.indexOf(":")+1), (originalValue.indexOf("|")-1));

        // Save integer value of parsedValue for the assignmentID to use during grade insertion
        assignmentID = Integer.parseInt(parsedValue.trim());
        
        
        // Update assignment detail Strings - ONE TIME 
        // (ALL following updates take place inside the action listener on the assignmentSelectionBox)
        for(TreeMap<Integer, TreeMap<String, String>> map : tempMasterList){
            // Iterate through each sub-TreeMap
            for(Map.Entry<Integer, TreeMap<String, String>> entry : map.entrySet()){
                // Set assignment details
                if(entry.getKey() == assignmentID){
                    assignmentName = entry.getValue().get("AssignmentName");
                    assignmentType = entry.getValue().get("AssignmentType");
                    assignmentDueDate = entry.getValue().get("DueDate");
                    assignmentPoints = entry.getValue().get("Points");
                    assignmentWeight = entry.getValue().get("Weight");
                }
            }
        }
        
        // -------------------------------------- CREATE Labels --------------------------------------
        
        // Make all Labels
        makeLabels();
        
        
        // -------------------------------------- CREATE HBoxes/VBoxes --------------------------------------
        
        // Create HBoxs for the TOP TITLE that will display the currently selected date and an option to change the date
        HBox assignmentDisplayBox = null;
        assignmentDisplayBox = TA_ConvenienceMethods.createHBox(assignmentDisplayBox, Pos.CENTER_LEFT, Double.MAX_VALUE,
                50, Color.web("#3b3a30"), 5, 5, 5, 5, assignmentPrompt);
        
        HBox assignmentChangeBox = null;
        assignmentChangeBox = TA_ConvenienceMethods.createHBox(assignmentChangeBox, Pos.CENTER, Double.MAX_VALUE,
                50, null, 5, 5, 5, 5, assignmentSelectionBox);
        
        // Make all other MAIN boxes
        makeBoxes();
        
        // Create Nodes array to adjust width growth
        Node[] labelArray = {
            idLabel,idLabel2,
            studentLabel,studentLabel2,
            gradeLabel,gradeLabel2,
            gradesBoxLeft_TITLE,gradesBoxRight_TITLE,
            gradesBoxLeft,gradesBoxRight,
            gradesBody_MASTER
        };
        
        // Iterate through ALL nodes and set Hgrow
        for(int i = 0; i < labelArray.length; i++){
            // Set Hgrow
            HBox.setHgrow(labelArray[i], Priority.ALWAYS);
        } 
        
        // -------------------------------------- Populate Grades Body --------------------------------------
        
        // Populate the grades body
        populateGradesBody();
        
        // -------------------------------------- Set Action Listeners --------------------------------------
        
        // Create action listeners
        setActionListeners();
        
        
        // -------------------------------------- Add everything to the GridPane --------------------------------------
               
        // Add the top "title" boxes (dateDisplayBox and dateChangeBox)
        gridPane.add(assignmentDisplayBox,1,1,2,1);
        gridPane.add(assignmentChangeBox, 3,1,2,1);
        gridPane.add(saveButton, 3,3,2,1);
        
        // Add Children to GridPane
        if(scrollPane != null){
            // Add gridPane to scrollPane
            gridPane.add(scrollPane,1,3,2,1);
        }
        else{
            // Add the body "attendance" boxes
            gridPane.add(gradesBoxLeft, 1,3,2,1);
            gridPane.add(gradesBoxRight, 3,3,2,1);
        }

        // Return the gridPane
        return gridPane;
    }
    
    
    // Method to create all the H/VBoxes
    private static void makeBoxes(){
        // Make all boxes
        gradesBoxLeft_TITLE = null;
        gradesBoxLeft_TITLE = TA_ConvenienceMethods.createHBox(gradesBoxLeft_TITLE, Pos.CENTER, Double.MAX_VALUE,
                25, Color.WHITE, 5, 5, 5, 5, idLabel, studentLabel, gradeLabel);
        
        gradesBoxRight_TITLE = null;
        gradesBoxRight_TITLE = TA_ConvenienceMethods.createHBox(gradesBoxRight_TITLE, Pos.CENTER, Double.MAX_VALUE,
                25, Color.WHITE, 5, 5, 5, 5, idLabel2, studentLabel2, gradeLabel2);
        
        // Create HBoxes for the MAIN BODY of the attendance page that will display all students and attendance options
        gradesBoxLeft = null;
        gradesBoxLeft = TA_ConvenienceMethods.createVBox(gradesBoxLeft, Pos.CENTER, Double.MAX_VALUE,
                10, Color.CADETBLUE, 5, 5, 5, 5);
        
        gradesBoxRight = null;
        gradesBoxRight = TA_ConvenienceMethods.createVBox(gradesBoxRight, Pos.CENTER, Double.MAX_VALUE,
                10, Color.CADETBLUE, 5, 5, 5, 5);
        
        
        // Create MASTER HBox which will contain EVERYTHING in the body of the page
        gradesBody_MASTER = null;
        gradesBody_MASTER = TA_ConvenienceMethods.createHBox(gradesBody_MASTER, Pos.CENTER, Double.MAX_VALUE,
                10, Color.web("#3b3a30"), 5, 5, 5, 5, gradesBoxLeft, gradesBoxRight);
        gradesBody_MASTER.setPrefHeight(250);
        gradesBody_MASTER.setPrefWidth(700);
    }
    
    // Method to make all labels for the GradesPage
    private static void makeLabels(){
         // Make all labels
        assignmentPrompt = TA_ConvenienceMethods.createLabel(assignmentPrompt,
                "Assignment Details:"
                + "\n\tID:\t\t\t" + assignmentID
                + "\n\tName:\t\t" + assignmentName
                + "\n\tType:\t\t" + assignmentType
                + "\n\tDue Date:\t" + assignmentDueDate
                + "\n\tPoints:\t\t" + assignmentPoints
                + "\n\tWeight:\t\t" + assignmentWeight + "%", Double.MAX_VALUE, Pos.CENTER_LEFT, "Arial", FontWeight.BOLD, 15, Color.web("#eaece5"));
        
        idLabel = null;
        idLabel = TA_ConvenienceMethods.createLabel(idLabel, "ID", Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.BOLD, 10);
        
        studentLabel = null;
        studentLabel = TA_ConvenienceMethods.createLabel(studentLabel, "Student", Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.BOLD, 10);
        
        gradeLabel = null;
        gradeLabel = TA_ConvenienceMethods.createLabel(gradeLabel, "Grade", Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.BOLD, 10);
        
        
        
        idLabel2 = null;
        idLabel2 = TA_ConvenienceMethods.createLabel(idLabel2, "ID", Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.BOLD, 10);
        
        studentLabel2 = null;
        studentLabel2 = TA_ConvenienceMethods.createLabel(studentLabel2, "Student", Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.BOLD, 10);
        
        gradeLabel2 = null;
        gradeLabel2 = TA_ConvenienceMethods.createLabel(gradeLabel2, "Grade", Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.BOLD, 10);
    }
    
    
    // Method to set all action listeners for all buttons and interactive GUI elements
    private static void setActionListeners(){        
        // Set mouse listeners to change the save button style when hovering over button
        saveButton.setOnMouseEntered(event ->{
            // Change background color
            saveButton.setStyle("-fx-background-color: #696969;"
                    + "-fx-font-weight: bold;"
                    + "-fx-text-fill: white");
        });
        
        saveButton.setOnMouseExited(event ->{
            // Change background color
            saveButton.setStyle("-fx-background-color: #3b3a30;"
                    + "-fx-font-weight: bold;"
                    + "-fx-text-fill: white");
        });
        
        // Add action listener to classGradeLevel to change the classNumbers displayed depending on what gradeLevel is selected
        assignmentSelectionBox.valueProperty().addListener(new ChangeListener<String>(){
            @Override public void changed(ObservableValue obValue, String oldValue, String newValue){
                // Save parsed assignment_ID
                String tempParsedValue = newValue.substring((newValue.indexOf(":")+1),
                        (newValue.indexOf("|")-1));

                // Save integer value of parsedValue for the assignmentID to use during grade insertion
                assignmentID = Integer.parseInt(tempParsedValue.trim());
                
                // Update assignment detail Strings
                for(TreeMap<Integer, TreeMap<String, String>> map : tempMasterList){
                    // Iterate through each sub-TreeMap
                    for(Map.Entry<Integer, TreeMap<String, String>> entry : map.entrySet()){
                        // Set assignment details
                        if(entry.getKey() == assignmentID){
                            assignmentName = entry.getValue().get("AssignmentName");
                            assignmentType = entry.getValue().get("AssignmentType");
                            assignmentDueDate = entry.getValue().get("DueDate");
                            assignmentPoints = entry.getValue().get("Points");
                            assignmentWeight = entry.getValue().get("Weight");
                        }
                    }
                }
                
                // Update assignment title label
                assignmentPrompt.setText("Assignment Details:"
                                        + "\n\tID:\t\t\t" + assignmentID
                                        + "\n\tName:\t\t" + assignmentName
                                        + "\n\tType:\t\t" + assignmentType
                                        + "\n\tDue Date:\t" + assignmentDueDate
                                        + "\n\tPoints:\t\t" + assignmentPoints
                                        + "\n\tWeight:\t\t" + assignmentWeight + "%");
                
                
                // Get any previously entered grades
                TreeMap<Integer, Float> tempStudentGrades = TA_SQLFunctions.getStudentAssignmentGrades(studentGradesEntryMap, assignmentID);
                
                // Update grade records if found
                if(tempStudentGrades == null){
                    // Update the TextField text
                    for(Map.Entry<Integer, TextField> entry : studentGradesEntryMap.entrySet()){
                        // Update each textfield
                        entry.getValue().setText("");
                        entry.getValue().setPromptText("Points/" + assignmentPoints);
                    }
                }
                else{
                    // Iterate through the tempStudentAttendanceMap (we can assume both maps are in the same order since tempStudent..Map is based on the studentAttendanceEntryMap)
                    for(Map.Entry<Integer, Float> entry : tempStudentGrades.entrySet()){
                        // Iterate through the studentAttendanceEntryMap
                        for(Map.Entry<Integer, TextField> fieldEntry : studentGradesEntryMap.entrySet()){
                            // If the studentIDs are identical, set the proper Toggle active
                            if(fieldEntry.getKey().equals(entry.getKey())){
                                // Set the TextField text
                                fieldEntry.getValue().setText(entry.getValue().toString());
                            }
                        }
                    }
                }
            }
            
        });
        
        // Setup Button action listeners
        classPageButton.setOnAction(event -> {
            System.out.println("Moving to SelectClassPage");
            TeachersAssistant.getMainStage().setScene(TA_SelectClassPage.startSelectClassPage());
        });
        
        attendanceButton.setOnAction(event -> {
            System.out.println("Moving to attendancePage");
            TeachersAssistant.getMainStage().setScene(TA_AttendancePage.startAttendancePage(course_ID));
        });
        
        reportsPageButton.setOnAction(event -> {
            System.out.println("Moving to ReportsPage");
            TeachersAssistant.getMainStage().setScene(TA_ReportsPage.startReportsPage(course_ID));
        });
        
        saveButton.setOnAction(event -> {
            // Temp variables
            boolean gradesFilledOut = true;
            boolean tooHighGrades = false;
            boolean tooLowGrades = false;
            String errorMessage = "";
            String highMessage = "";
            String lowMessage = "";
            float numericTest = 0.0f;
            
            // Check all TextFields
            for(Map.Entry<Integer, TextField> entry : studentGradesEntryMap.entrySet()){
                // Check to see if each student grades entry has been filled out
                TextField tempField = entry.getValue();
                if(tempField.getText().isEmpty()){
                    // Change gradesFilledOut to false
                    gradesFilledOut = false;
                    errorMessage = "Please fill out all student grades.";
                    break;
                }
                else{
                    // Try parsing the value to a float to ensure the entered value is numeric
                    try{
                        numericTest = Float.parseFloat(tempField.getText());
                        // Make sure the entered grade DOES NOT exceed the assignment points
                        if(numericTest > Float.parseFloat(assignmentPoints)){
                            // Update highMessage
                            highMessage = highMessage + "\n\t* " + numericTest;
                            
                            // Prompt the user with a warning
                            tooHighGrades = true;  
                        }
                        else if(numericTest < 0){
                            // Update lowMessage
                            lowMessage = lowMessage + "\n\t* " + numericTest;
                            
                            // Update tooLowGrades
                            tooLowGrades = true;
                        }
                        else{
                            gradesFilledOut = true;
                        }
                        
                    }
                    catch(Exception e){
                        // Set gradesFilledOut to false
                        gradesFilledOut = false;
                        errorMessage = "Invalid grade entry type. Grades MUST be decimal values (EX: 99.0, 34.35).";
                        break;
                    }
                }
            }
            
            // If the grades are filled out, check if they're too high
            if(gradesFilledOut){
                // If the grades are too high, prompt the teacher, otherwise, proceed with grades update
                if(tooLowGrades){
                    // Prompt the teacher and prevent user from inserting negative values
                    TA_AlertPage.alert("Grades Error", "You\'ve inserted negative values. Negative values are NOT supported. Please update the following grades:" + lowMessage, Color.web("#eaece5"));
                }
                else if(tooHighGrades){
                    // If the teacher says yes, allow the grades to be updated
                    if(TA_AlertPage.alert("Confirm Extra Credit", "You\'ve entered grade(s) (seen below) higher than the current assignment points.\nWould you like to continue?" + highMessage, Color.web("#eaece5"), true)){
                        if(TA_SQLFunctions.updateGrades(studentGradesEntryMap, assignmentID, course_ID))
                            TA_AlertPage.alert("Grades Success", "Grades successfully saved!", Color.web("#eaece5"));
                        else
                            TA_AlertPage.alert("Grades Failure", "Grades failed to save. Please try again.", Color.web("#eaece5"));
                    }
                    else{
                        errorMessage = "You\'ve opted not proceed with grade entries.";
                        TA_AlertPage.alert("Grades Error", errorMessage, Color.web("#eaece5"));
                    }
                }
                else{
                    // Perform the grades update and prompt user
                    if(TA_SQLFunctions.updateGrades(studentGradesEntryMap, assignmentID, course_ID))
                        TA_AlertPage.alert("Grades Success", "Grades successfully saved!", Color.web("#eaece5"));
                    else
                        TA_AlertPage.alert("Grades Failure", "Grades failed to save. Please try again.", Color.web("#eaece5"));
                }
            }
            else{
                TA_AlertPage.alert("Grades Error", errorMessage, Color.web("#eaece5"));
            }
        });
    }
    
    
    // Method to populate the body of the GradesPage
    private static void populateGradesBody(){
        /** 
         * NOTES:
            * We need to save each TextField inside a TreeMap<Integer, TextField>
                * to track the contents of each TextField for each student so we can successfully enter grades.
                * 
            * Now...iterate through the Body Boxes and append a new HBox for each student.
        */
        
        // Variables to aid controlling which side a record will be added to
        int count = 0;
        int divideBody = studentNamesMap.size()/2;
        
        // Add the titles to the GradesBody
        gradesBoxLeft.getChildren().add(gradesBoxLeft_TITLE);
        gradesBoxRight.getChildren().add(gradesBoxRight_TITLE);
        
        // Iterate through the studentNamesMap to create/add all needed GUI elements to the page
        for(Map.Entry<Integer, String> entry : studentNamesMap.entrySet()){
            // Create new Labels for Student ID/Name
            Label studentName = null;
            studentName = TA_ConvenienceMethods.createLabel(studentName, entry.getValue(), Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.NORMAL, 10, Color.web("#eaece5"));
            studentName.setPrefWidth(100);
            Label studentID = null;
            studentID = TA_ConvenienceMethods.createLabel(studentID, entry.getKey().toString(), Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.NORMAL, 10, Color.web("#eaece5"));
            studentID.setPrefWidth(50);
            
            // Create small textfield for grade entry
            TextField gradeEntryField = null;
            gradeEntryField = TA_ConvenienceMethods.createTextField(gradeEntryField, 10, Double.MAX_VALUE, Double.MAX_VALUE, 
                    5,5,5,5);
            gradeEntryField.setPromptText("Points/" + assignmentPoints);
            
            // Save student_ID/TextField entry pair
            studentGradesEntryMap.put(entry.getKey(), gradeEntryField);
            
            // Create new HBox for new student
            HBox newStudentBox = null;
            newStudentBox = TA_ConvenienceMethods.createHBox(newStudentBox, Pos.CENTER_LEFT, Double.MAX_VALUE, 25, Color.web("#3b3a30"),
                    5, 5, 5, 5, studentID, studentName, gradeEntryField);
            newStudentBox.setPrefWidth(500);
            
            // Modify Hgrow attributes
            HBox.setHgrow(newStudentBox, Priority.ALWAYS);
            HBox.setHgrow(studentID, Priority.ALWAYS);
            HBox.setHgrow(studentName, Priority.ALWAYS);
            HBox.setHgrow(gradeEntryField, Priority.ALWAYS);
            
            
            // Switch statements for appending records to the appropriate boxes
            switch(studentNamesMap.size()){
                case 1:
                    // Add newStudentBox to the LEFT & set the RIGHT box invisible
                    gradesBoxLeft.getChildren().add(newStudentBox);
                    gradesBoxRight.setVisible(false);
                    break;
                default:
                    // Determine which body section to add the records to
                    if(count < divideBody){
                        // Add newStudentBox to the LEFT
                        gradesBoxLeft.getChildren().add(newStudentBox);
                    }
                    else{
                        // Add newStudentBox to the RIGHT
                        gradesBoxRight.getChildren().add(newStudentBox);
                    }
                    break;
            }
            // Increment counter
            count++;
        }
        
        // Create ScrollPane for ease of viewing bulk records
        if(studentNamesMap.size() > 1){
            scrollPane = new ScrollPane();
            scrollPane.setFitToHeight(true);
            scrollPane.setFitToWidth(true);
            scrollPane.setContent(gradesBody_MASTER);
        }
        
        
        // Get any previously entered grades - this happens ONLY ONCE during the initial page load
        TreeMap<Integer, Float> tempStudentGrades = TA_SQLFunctions.getStudentAssignmentGrades(studentGradesEntryMap, assignmentID);

        // Update grade records if found
        if(tempStudentGrades == null){
            // Update the TextField text
            for(Map.Entry<Integer, TextField> entry : studentGradesEntryMap.entrySet()){
                // Update each textfield
                entry.getValue().setText("");
                entry.getValue().setPromptText("Points/" + assignmentPoints);
            }
        }
        else{
            // Iterate through the tempStudentAttendanceMap (we can assume both maps are in the same order since tempStudent..Map is based on the studentAttendanceEntryMap)
            for(Map.Entry<Integer, Float> entry : tempStudentGrades.entrySet()){
                // Iterate through the studentAttendanceEntryMap
                for(Map.Entry<Integer, TextField> fieldEntry : studentGradesEntryMap.entrySet()){
                    // If the studentIDs are identical, set the proper Toggle active
                    if(fieldEntry.getKey().equals(entry.getKey())){
                        // Set the TextField text
                        fieldEntry.getValue().setText(entry.getValue().toString());
                    }
                }
            }
        }
    }

    //  --------------------------------------------- GET/SET METHODS ---------------------------------------------
    public static Scene getScene(){
        return gradesScene;
    }
    
}
