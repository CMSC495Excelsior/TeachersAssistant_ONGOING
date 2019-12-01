/**
 * FileName: TA_AttendancePage.java
 * Author: Stephen James
 * Date: 11/20/19
 * Course: CMSC-495
 * 
 * Objective: To create an application page that will be used for entering and tracking student attendance.
*/

// Package
package teachersassistant;

// Import statements
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.TreeSet;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

// Main Class
public class TA_AttendancePage {
    
    // Create GUI components
    private static Scene attendanceScene;
    private static BorderPane borderPane;
    private static GridPane gridPane;
    private static Button gradesPageButton, classPageButton, reportsPageButton,
            saveButton, allPresent, allAbsent, allTardy, allWeatherClosure;
    private static int course_ID;
    private static Label datePrompt = null;
   
    // Main method used to return Scene object to the Main Stage
    public static Scene startAttendancePage(int courseID_Arg){
        // Save the course_ID
        course_ID = courseID_Arg;
        
        // Make BorderPane
        makeBorderPane();

        // Set static vars
        attendanceScene = new Scene(borderPane);
        
        // Return Scene object
        return attendanceScene;
    }

    
    
    // ----------------------------------- Supplemental Methods Below -----------------------------------
    
    
    
    // Method for creating the borderpane
    private static BorderPane makeBorderPane(){
        // Create ALL buttons
        classPageButton = new Button("Change Class");
        classPageButton.setAlignment(Pos.CENTER);
        classPageButton.setPadding(new Insets(10,10,10,10));
        
        gradesPageButton = new Button("Grades");
        gradesPageButton.setAlignment(Pos.CENTER);
        gradesPageButton.setPadding(new Insets(10,10,10,10));
        
        reportsPageButton = new Button("Reports");
        reportsPageButton.setAlignment(Pos.CENTER);
        reportsPageButton.setPadding(new Insets(10,10,10,10));
        
        saveButton = new Button("Save Attendance");
        saveButton.setAlignment(Pos.CENTER);
        saveButton.setPadding(new Insets(10,10,10,10));
        
        allPresent = new Button("All Present");
        allPresent.setAlignment(Pos.CENTER);
        allPresent.setPadding(new Insets(5,5,5,5));
        
        allAbsent = new Button("All Absent");
        allAbsent.setAlignment(Pos.CENTER);
        allAbsent.setPadding(new Insets(5,5,5,5));
        
        allTardy = new Button("All Tardy");
        allTardy.setAlignment(Pos.CENTER);
        allTardy.setPadding(new Insets(5,5,5,5));
        
        allWeatherClosure = new Button("Weather Closure");
        allWeatherClosure.setAlignment(Pos.CENTER);
        allWeatherClosure.setPadding(new Insets(5,5,5,5));
        
        // Make the GridPane
        makeGridPane();
        
        // Create HBox for all navigation buttons
        HBox buttonBox = null;
        buttonBox = TA_ConvenienceMethods.createHBox(buttonBox, Pos.CENTER, Double.MAX_VALUE,
                15, Color.LIGHTBLUE, 10, 10, 10, 10, gradesPageButton, classPageButton, reportsPageButton);
        
        // Create a Label for the greeting
        Label greetingLabel = null;
        greetingLabel = TA_ConvenienceMethods.createLabel(greetingLabel, TeachersAssistant.getSchoolName() + 
                " > " + TeachersAssistant.getCourseName() + " > ATTENDANCE",
                Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.BOLD, 20);
        greetingLabel.setPadding(new Insets(10,10,30,10));

        // Create borderPane
        borderPane = TA_ConvenienceMethods.createBorderPane(borderPane, Color.BEIGE,greetingLabel,
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
        ArrayList<LocalDate> attendanceList = TA_SQLFunctions.populateAttendanceList(course_ID);
        TreeMap<Integer, String> studentNamesMap = TA_SQLFunctions.populateStudentsMap(course_ID);
        TreeMap<Integer, ToggleGroup> studentAttendanceEntryMap = new TreeMap<>();
        NavigableSet<LocalDate> dateSet = new TreeSet<LocalDate>(attendanceList);
        
        // Default ScrollPane value
        ScrollPane scrollPane = null;
        
        // Initialize and customize GridPane
        gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(5,5,5,5));
        gridPane.setAlignment(Pos.CENTER);
        gridPane.gridLinesVisibleProperty();
        
        // Initialize all ObservableLists for the ComboBoxes
        ObservableList<LocalDate> dateSelectionList = FXCollections.observableArrayList();
        
        // Create ComboBox for date changing
        ComboBox dateSelectionBox = new ComboBox(dateSelectionList);
        
        // Temp boolean variables for dateSelectionBox population
        boolean dateIsEqual = false;
        boolean skipCompare = false;
        
        // Populate the ComboBox list & set value of dateSelectionBox
        LocalDate now = LocalDate.now();
        for(LocalDate date : attendanceList){
            dateSelectionList.add(date);
            // If the current date matches a date in the list, set the value of dateSelectionBox
            if(!skipCompare){
                if(now.isEqual(date)){
                    dateIsEqual = true;
                    skipCompare = true;
                }  
                else
                    dateIsEqual = false;
            }                
        }
        
        // Set appropriate date
        if(dateIsEqual)
            dateSelectionBox.setValue(now);
        else
            dateSelectionBox.setValue(dateSet.lower(now));
        
        // -------------------------------------- CREATE Labels --------------------------------------
        
        Label attendanceNotice = null;
        attendanceNotice = TA_ConvenienceMethods.createLabel(attendanceNotice, "NOTICE:\n\t- In cases of an excused absence, student will be marked present."
                + "\n\t- If a student is less than 15 minutes late, they are considered TARDY."
                + "\n\t- If a student is more than 15 minutes late, they are considered ABSENT.", Double.MAX_VALUE, Pos.CENTER_LEFT, "Arial", FontWeight.BOLD, 15);
        attendanceNotice.setPadding(new Insets(10,0,0,0));
        
        datePrompt = TA_ConvenienceMethods.createLabel(datePrompt, "Date: " + dateSelectionBox.getValue(), Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.BOLD, 20);
        
        Label idLabel = null;
        idLabel = TA_ConvenienceMethods.createLabel(idLabel, "ID", Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.BOLD, 10);
        
        Label studentLabel = null;
        studentLabel = TA_ConvenienceMethods.createLabel(studentLabel, "Student", Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.BOLD, 10);
        
        Label presentLabel = null;
        presentLabel = TA_ConvenienceMethods.createLabel(presentLabel, "Present", Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.BOLD, 10);
        
        Label absentLabel = null;
        absentLabel = TA_ConvenienceMethods.createLabel(absentLabel, "Absent", Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.BOLD, 10);
        
        Label tardyLabel = null;
        tardyLabel = TA_ConvenienceMethods.createLabel(tardyLabel, "Tardy", Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.BOLD, 10);
        
        Label weatherClosureLabel = null;
        weatherClosureLabel = TA_ConvenienceMethods.createLabel(weatherClosureLabel, "Weather Closure", Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.BOLD, 10);
        
        // --------------- Duplicate Labels for the right-side attendance pane ---------------
        
        
        Label idLabel2 = null;
        idLabel2 = TA_ConvenienceMethods.createLabel(idLabel2, "ID", Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.BOLD, 10);
        
        Label studentLabel2 = null;
        studentLabel2 = TA_ConvenienceMethods.createLabel(studentLabel2, "Student", Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.BOLD, 10);
        
        Label presentLabel2 = null;
        presentLabel2 = TA_ConvenienceMethods.createLabel(presentLabel2, "Present", Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.BOLD, 10);
        
        Label absentLabel2 = null;
        absentLabel2 = TA_ConvenienceMethods.createLabel(absentLabel2, "Absent", Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.BOLD, 10);
        
        Label tardyLabel2 = null;
        tardyLabel2 = TA_ConvenienceMethods.createLabel(tardyLabel2, "Tardy", Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.BOLD, 10);
        
        Label weatherClosureLabel2 = null;
        weatherClosureLabel2 = TA_ConvenienceMethods.createLabel(weatherClosureLabel2, "Weather Closure", Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.BOLD, 10);
        
        
        // -------------------------------------- CREATE HBoxes/VBoxes --------------------------------------
        
        // Create HBoxs for the TOP TITLE that will display the currently selected date and an option to change the date
        HBox dateDisplayBox = null;
        dateDisplayBox = TA_ConvenienceMethods.createHBox(dateDisplayBox, Pos.CENTER, Double.MAX_VALUE,
                50, Color.LIGHTGRAY, 5, 5, 5, 5, datePrompt);
        
        HBox dateChangeBox = null;
        dateChangeBox = TA_ConvenienceMethods.createHBox(dateChangeBox, Pos.CENTER, Double.MAX_VALUE,
                50, null, 5, 5, 5, 5, dateSelectionBox);
        
        
        HBox attendanceBoxLeft_TITLE = null;
        attendanceBoxLeft_TITLE = TA_ConvenienceMethods.createHBox(attendanceBoxLeft_TITLE, Pos.CENTER, Double.MAX_VALUE,
                25, Color.WHITE, 5, 5, 5, 5, idLabel, studentLabel, presentLabel, absentLabel, tardyLabel, weatherClosureLabel);
        
        
        HBox attendanceBoxRight_TITLE = null;
        attendanceBoxRight_TITLE = TA_ConvenienceMethods.createHBox(attendanceBoxRight_TITLE, Pos.CENTER, Double.MAX_VALUE,
                25, Color.WHITE, 5, 5, 5, 5, idLabel2, studentLabel2, presentLabel2, absentLabel2, tardyLabel2, weatherClosureLabel);
        
        
        
        // Create HBoxes for the MAIN BODY of the attendance page that will display all students and attendance options
        VBox attendanceBoxLeft = null;
        attendanceBoxLeft = TA_ConvenienceMethods.createVBox(attendanceBoxLeft, Pos.CENTER, Double.MAX_VALUE,
                10, Color.CADETBLUE, 5, 5, 5, 5);
        
        VBox attendanceBoxRight = null;
        attendanceBoxRight = TA_ConvenienceMethods.createVBox(attendanceBoxRight, Pos.CENTER, Double.MAX_VALUE,
                10, Color.CADETBLUE, 5, 5, 5, 5);
        
        VBox sideButtonBox = null;
        sideButtonBox = TA_ConvenienceMethods.createVBox(sideButtonBox, Pos.CENTER, Double.MAX_VALUE,
                10, null, 5, 5, 5, 5, allPresent, allAbsent, allTardy, allWeatherClosure);
        
        
        
        // Create MASTER HBox which will contain EVERYTHING in the body of the page
        HBox attendanceBody_MASTER = null;
        attendanceBody_MASTER = TA_ConvenienceMethods.createHBox(attendanceBody_MASTER, Pos.CENTER, Double.MAX_VALUE,
                10, Color.LIGHTGRAY, 5, 5, 5, 5, attendanceBoxLeft, attendanceBoxRight);
        attendanceBody_MASTER.setPrefHeight(250);
        attendanceBody_MASTER.setPrefWidth(700);
        
        // Create Nodes array
        Node[] labelArray = {
            idLabel,idLabel2,
            studentLabel,studentLabel2,
            presentLabel,presentLabel2,
            absentLabel,absentLabel2,
            tardyLabel,tardyLabel2,
            attendanceBoxLeft_TITLE,attendanceBoxRight_TITLE,
            attendanceBoxLeft,attendanceBoxRight,
            attendanceBody_MASTER
        };
        
        
        // Iterate through ALL nodes and set Hgrow
        for(int i = 0; i < labelArray.length; i++){
            // Set Hgrow
            HBox.setHgrow(labelArray[i], Priority.ALWAYS);
        }
        
        
        // -------------------------------------- Populate Attendance Body --------------------------------------
        
        
        /** 
         * NOTE:
            * We need to save each CheckBox inside a TreeMap<Integer, ArrayList<CheckBox>> 
                * so we can track which Box is active for each student so we can successfully enter attendance
                * 
            * Now...iterate through the Body Boxes and append a new HBox for each student
        */
        
        // Variables to aid controlling which side a record will be added to
        int count = 0;
        int divideBody = studentNamesMap.size()/2;
        
        // Iterate through the studentNamesMap to create/add all needed GUI elements to the page
        for(Map.Entry<Integer, String> entry : studentNamesMap.entrySet()){
            // Create new Labels for Student ID/Name
            Label studentName = null;
            studentName = TA_ConvenienceMethods.createLabel(studentName, entry.getValue(), Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.NORMAL, 10);
            studentName.setPrefWidth(100);
            
            Label studentID = null;
            studentID = TA_ConvenienceMethods.createLabel(studentID, entry.getKey().toString(), Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.NORMAL, 10);
            studentID.setPrefWidth(50);
            
            // Create ToggleGroup for RadioButtons for each student listed
            ToggleGroup attendanceGroup = new ToggleGroup();
            
            // Create RadioButtons for attendance keeping
            RadioButton presentOption = new RadioButton();
            presentOption.setToggleGroup(attendanceGroup);
            presentOption.setUserData(1);
            
            RadioButton absentOption = new RadioButton();
            absentOption.setToggleGroup(attendanceGroup);
            absentOption.setUserData(0);
            
            RadioButton tardyOption = new RadioButton();
            tardyOption.setToggleGroup(attendanceGroup);
            tardyOption.setUserData(2);
            
            RadioButton weatherClosure = new RadioButton();
            weatherClosure.setToggleGroup(attendanceGroup);
            weatherClosure.setUserData(3);
            
            // Save student_ID/ToggleGroup pair
            studentAttendanceEntryMap.put(entry.getKey(), attendanceGroup);
            
            // Create new HBox for new student
            HBox newStudentBox = null;
            newStudentBox = TA_ConvenienceMethods.createHBox(newStudentBox, Pos.CENTER_LEFT, Double.MAX_VALUE, 25, Color.CORAL,
                    5, 5, 5, 5, studentID, studentName, presentOption, absentOption, tardyOption, weatherClosure);
            newStudentBox.setPrefWidth(500);
            
            // Modify Hgrow attributes
            HBox.setHgrow(newStudentBox, Priority.ALWAYS);
            HBox.setHgrow(studentID, Priority.ALWAYS);
            HBox.setHgrow(studentName, Priority.ALWAYS);
            HBox.setHgrow(presentOption, Priority.ALWAYS);
            HBox.setHgrow(absentOption, Priority.ALWAYS);
            HBox.setHgrow(tardyOption, Priority.ALWAYS);
            
            
            // Switch statements for appending records to the appropriate boxes
            switch(studentNamesMap.size()){
                case 1:
                    // Adjust title box widths
                    attendanceBoxLeft_TITLE.setPrefWidth(attendanceBody_MASTER.getPrefWidth());
                    attendanceBoxRight_TITLE.setPrefWidth(attendanceBody_MASTER.getPrefWidth());
                    
                    // Add newStudentBox to the LEFT & set the RIGHT box invisible
                    attendanceBoxLeft.getChildren().add(newStudentBox);
                    attendanceBoxRight.setVisible(false);
                    break;
                default:
                    // Adjust title box widths
                    attendanceBoxLeft_TITLE.setPrefWidth(attendanceBody_MASTER.getPrefWidth()/2);
                    attendanceBoxRight_TITLE.setPrefWidth(attendanceBody_MASTER.getPrefWidth()/2);
                    
                    // Determine which body section to add the records to
                    if(count < divideBody){
                        // Add newStudentBox to the LEFT
                        attendanceBoxLeft.getChildren().add(newStudentBox);
                    }
                    else{
                        // Add newStudentBox to the RIGHT
                        attendanceBoxRight.getChildren().add(newStudentBox);
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
            scrollPane.setContent(attendanceBody_MASTER);
        }
        
        
        // -------------------------------------- Set Action Listeners --------------------------------------
        
        
        // Add action listener to classGradeLevel to change the classNumbers displayed depending on what gradeLevel is selected
        dateSelectionBox.valueProperty().addListener(new ChangeListener<LocalDate>(){
            @Override public void changed(ObservableValue obValue, LocalDate oldValue, LocalDate newValue){
                datePrompt.setText("Date: " + newValue.toString());
            }
            
        });
        
        
        // Setup Button action listeners
        classPageButton.setOnAction(event -> {
            System.out.println("Moving to SelectClassPage");
            TeachersAssistant.getMainStage().setScene(TA_SelectClassPage.startSelectClassPage());
        });
        
        gradesPageButton.setOnAction(event -> {
            System.out.println("Moving to GradesPage");
            TeachersAssistant.getMainStage().setScene(TA_GradesPage.startGradesPage(course_ID));
        });
        
        reportsPageButton.setOnAction(event -> {
            System.out.println("Moving to ReportsPage");
            TeachersAssistant.getMainStage().setScene(TA_ReportsPage.startReportsPage(course_ID));
        });
        
        saveButton.setOnAction(event -> {
            // Temp boolean
            boolean attendanceFilledOut = true;
            
            // Check all CheckBoxes
            for(Map.Entry<Integer, ToggleGroup> entry : studentAttendanceEntryMap.entrySet()){
                // Check to see if each student attendance entry has been filled out
                ToggleGroup tempGroup = entry.getValue();
                if(tempGroup.getSelectedToggle() == null){
                    // Change attendanceFilledOut to false
                    attendanceFilledOut = false;
                }
                else{
                    // Change attendanceFilledOut to true
                    attendanceFilledOut = true;
                }
            }
            
            // Use the attendanceFilledOut boolean to either save or prevent user from saving attendance
            if(attendanceFilledOut)
                if(TA_SQLFunctions.updateAttendance(studentAttendanceEntryMap, (LocalDate)dateSelectionBox.getValue(), course_ID))
                    TA_AlertPage.alert("Attendance Success", "Attendance successfully saved!", Color.CORAL);
                else
                    TA_AlertPage.alert("Attendance Failure", "Attendance failed to save. Please try again.", Color.CORAL);
            else
                TA_AlertPage.alert("Attendance Error", "Please fill out all student attendance.", Color.CORAL);
        });
        
        allAbsent.setOnAction(event -> {
            // Iterate through each group and set the toggle
            for(Map.Entry<Integer, ToggleGroup> entry : studentAttendanceEntryMap.entrySet()){
                // Set toggle
                ToggleGroup tempGroup = entry.getValue();
                ObservableList<Toggle> toggleList = tempGroup.getToggles();
                for(Toggle toggle : toggleList){
                    //System.out.println("Current Toggle for ID " + entry.getKey() + " is: " + toggle.getUserData().toString());
                    if(toggle.getUserData().toString().equals("0"))
                        toggle.setSelected(true);
                }
            }
        });
        
        allPresent.setOnAction(event -> {
            // Iterate through each group and set the toggle
            for(Map.Entry<Integer, ToggleGroup> entry : studentAttendanceEntryMap.entrySet()){
                // Set toggle
                ToggleGroup tempGroup = entry.getValue();
                ObservableList<Toggle> toggleList = tempGroup.getToggles();
                for(Toggle toggle : toggleList){
                    //System.out.println("Current Toggle for ID " + entry.getKey() + " is: " + toggle.getUserData().toString());
                    if(toggle.getUserData().toString().equals("1"))
                        toggle.setSelected(true);
                }
            }
        });
        
        allTardy.setOnAction(event -> {
            // Iterate through each group and set the toggle
            for(Map.Entry<Integer, ToggleGroup> entry : studentAttendanceEntryMap.entrySet()){
                // Set toggle
                ToggleGroup tempGroup = entry.getValue();
                ObservableList<Toggle> toggleList = tempGroup.getToggles();
                for(Toggle toggle : toggleList){
                    //System.out.println("Current Toggle for ID " + entry.getKey() + " is: " + toggle.getUserData().toString());
                    if(toggle.getUserData().toString().equals("2"))
                        toggle.setSelected(true);
                }
            }
        });
        
        allWeatherClosure.setOnAction(event -> {
            // Iterate through each group and set the toggle
            for(Map.Entry<Integer, ToggleGroup> entry : studentAttendanceEntryMap.entrySet()){
                // Set toggle
                ToggleGroup tempGroup = entry.getValue();
                ObservableList<Toggle> toggleList = tempGroup.getToggles();
                for(Toggle toggle : toggleList){
                    //System.out.println("Current Toggle for ID " + entry.getKey() + " is: " + toggle.getUserData().toString());
                    if(toggle.getUserData().toString().equals("3"))
                        toggle.setSelected(true);
                }
            }
        });
               
        // Add the top "title" boxes (dateDisplayBox and dateChangeBox)
        
        gridPane.add(dateDisplayBox,1,1,2,1);
        gridPane.add(dateChangeBox, 3,1,2,1);
        gridPane.add(sideButtonBox, 4,3,2,1);
        gridPane.add(attendanceBoxLeft_TITLE,1,2,1,1);
        gridPane.add(saveButton, 1,4,2,1);
        gridPane.add(attendanceNotice, 0,5,4,1);
        
        // Add Children to GridPane
        if(scrollPane != null){
            // Add gridPane to scrollPane
            gridPane.add(scrollPane,1,3,2,1);
            gridPane.add(attendanceBoxRight_TITLE,2,2,1,1);
        }
        else{
            // Add the body "attendance" boxes
            gridPane.add(attendanceBoxLeft, 1,3,2,1);
            gridPane.add(attendanceBoxRight, 3,3,2,1);
        }
        

        // Return the gridPane
        return gridPane;
    }

    //  --------------------------------------------- GET/SET METHODS ---------------------------------------------
    public static Scene getScene(){
        return attendanceScene;
    }
    
}
