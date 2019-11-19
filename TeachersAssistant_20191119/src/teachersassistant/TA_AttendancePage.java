/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package teachersassistant;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

/**
 *
 * @author SoftwareEng
 */
public class TA_AttendancePage {
    
    // Create GUI components
    private static Scene selectClassScene;
    private static BorderPane borderPane;
    private static GridPane gridPane;
    private static Button gradesPageButton, classPageButton, reportsPageButton;
    private static int course_ID;
    
    
    // TreeMap basic structure
    /*
    Subject: English
        Grade Level: 10
            Class Number: 1
            Class Number: 2
        Grades Level: 11
            Class Number: 1
    
    */

   
    public static Scene startAttendancePage(int courseID_Arg){
        // Save the course_ID
        course_ID = courseID_Arg;
        // Make BorderPane
        makeBorderPane();

        // Set static vars
        selectClassScene = new Scene(borderPane);
        
        return selectClassScene;
    }

    // Method for creating the borderpane
    private static BorderPane makeBorderPane(){
        
        
        // Create Confirm Button - action listener set in "makeGridPane()"
        classPageButton = new Button("Change Class");
        classPageButton.setAlignment(Pos.CENTER);
        classPageButton.setPadding(new Insets(10,10,10,10));
        
        gradesPageButton = new Button("Grades");
        gradesPageButton.setAlignment(Pos.CENTER);
        gradesPageButton.setPadding(new Insets(10,10,10,10));
        
        reportsPageButton = new Button("Reports");
        reportsPageButton.setAlignment(Pos.CENTER);
        reportsPageButton.setPadding(new Insets(10,10,10,10));
        
        // Make the GridPane
        makeGridPane();
        
        // Create HBox for confirmButton
        HBox buttonBox = null;
        buttonBox = TA_ConvenienceMethods.createHBox(buttonBox, Pos.CENTER, Double.MAX_VALUE,
                15, Color.LIGHTBLUE, 10, 10, 10, 10, gradesPageButton, classPageButton, reportsPageButton);
        
        
        // Create a Label for the greeting
        Label greetingLabel = null;
        greetingLabel = TA_ConvenienceMethods.createLabel(greetingLabel, "SCHOOL NAME > CLASS NAME > ATTENDANCE",
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
        
        // Initialize and customize GridPane
        gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(5,5,5,5));
        gridPane.setAlignment(Pos.CENTER);
        gridPane.gridLinesVisibleProperty();
        
        
        // Initialize all ObservableLists for the ComboBoxes
        ObservableList<String> dateSelectionList = FXCollections.observableArrayList();
        
        // Populate the ComboBox list
        for(LocalDate date : attendanceList){
            dateSelectionList.add(date.toString());
        }
        
        // Create ComboBox for date changing
        ComboBox dateSelectionBox = new ComboBox(dateSelectionList); 
        dateSelectionBox.setPromptText("Change Date");
        
        // -------------------------------------- CREATE Labels --------------------------------------
        
        Label datePrompt = null;
        datePrompt = TA_ConvenienceMethods.createLabel(datePrompt, "Date: " + LocalDate.now(), Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.BOLD, 10);
        datePrompt.setPadding(new Insets(5,5,5,5));
        
        Label studentLabel = null;
        studentLabel = TA_ConvenienceMethods.createLabel(studentLabel, "Student", Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.BOLD, 10);
        studentLabel.setPadding(new Insets(5,5,5,5));
        
        Label presentLabel = null;
        presentLabel = TA_ConvenienceMethods.createLabel(presentLabel, "Present", Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.BOLD, 10);
        presentLabel.setPadding(new Insets(5,5,5,5));
        
        Label absentLabel = null;
        absentLabel = TA_ConvenienceMethods.createLabel(absentLabel, "Absent", Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.BOLD, 10);
        absentLabel.setPadding(new Insets(5,5,5,5));
        
        Label tardyLabel = null;
        tardyLabel = TA_ConvenienceMethods.createLabel(tardyLabel, "Tardy", Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.BOLD, 10);
        tardyLabel.setPadding(new Insets(5,5,5,5));
        
        
        
        
        Label studentLabel2 = null;
        studentLabel2 = TA_ConvenienceMethods.createLabel(studentLabel2, "Student", Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.BOLD, 10);
        studentLabel2.setPadding(new Insets(5,5,5,5));
        
        Label presentLabel2 = null;
        presentLabel2 = TA_ConvenienceMethods.createLabel(presentLabel2, "Present", Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.BOLD, 10);
        presentLabel2.setPadding(new Insets(5,5,5,5));
        
        Label absentLabel2 = null;
        absentLabel2 = TA_ConvenienceMethods.createLabel(absentLabel2, "Absent", Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.BOLD, 10);
        absentLabel2.setPadding(new Insets(5,5,5,5));
        
        Label tardyLabel2 = null;
        tardyLabel2 = TA_ConvenienceMethods.createLabel(tardyLabel2, "Tardy", Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.BOLD, 10);
        tardyLabel2.setPadding(new Insets(5,5,5,5));
        
        
        // -------------------------------------- CREATE HBoxes --------------------------------------
        
        // Create HBoxs for the TOP TITLE that will display the currently selected date and an option to change the date
        HBox dateDisplayBox = null;
        dateDisplayBox = TA_ConvenienceMethods.createHBox(dateDisplayBox, Pos.CENTER, Double.MAX_VALUE,
                50, Color.CORAL, 5, 5, 5, 5, datePrompt);
        
        HBox dateChangeBox = null;
        dateChangeBox = TA_ConvenienceMethods.createHBox(dateChangeBox, Pos.CENTER, Double.MAX_VALUE,
                50, Color.LIGHTBLUE, 5, 5, 5, 5, dateSelectionBox);
        
        
        HBox attendanceBoxLeft_TITLE = null;
        attendanceBoxLeft_TITLE = TA_ConvenienceMethods.createHBox(attendanceBoxLeft_TITLE, Pos.CENTER, Double.MAX_VALUE,
                50, Color.WHITE, 5, 5, 5, 5, studentLabel, presentLabel, absentLabel, tardyLabel);
        
        
        HBox attendanceBoxRight_TITLE = null;
        attendanceBoxRight_TITLE = TA_ConvenienceMethods.createHBox(attendanceBoxRight_TITLE, Pos.CENTER, Double.MAX_VALUE,
                50, Color.WHITE, 5, 5, 5, 5, studentLabel2, presentLabel2, absentLabel2, tardyLabel2);
        
        // Create HBoxes for the MAIN BODY of the attendance page that will display all students and attendance options
        VBox attendanceBoxLeft = null;
        attendanceBoxLeft = TA_ConvenienceMethods.createVBox(attendanceBoxLeft, Pos.CENTER, Double.MAX_VALUE,
                50, Color.CADETBLUE, 5, 5, 5, 5, attendanceBoxLeft_TITLE);
        
        VBox attendanceBoxRight = null;
        attendanceBoxRight = TA_ConvenienceMethods.createVBox(attendanceBoxRight, Pos.CENTER, Double.MAX_VALUE,
                50, Color.TOMATO, 5, 5, 5, 5, attendanceBoxRight_TITLE);
        
        
        // -------------------------------------- Populate Attendance Body --------------------------------------
        
        
        // Need to save each CheckBox in an ArrayList<CheckBox> inside a TreeMap<Integer, ArrayList<CheckBox>> 
        //  so we can track which Box is active for each student so we can successfully enter attendance
        // Iterate through the Body Boxes and append a new HBox for each student
        int count = 0;
        for(Map.Entry<Integer, String> entry : studentNamesMap.entrySet()){
            // Create new Label for Student Name
            Label studentName = null;
            studentName = TA_ConvenienceMethods.createLabel(studentName, entry.getValue(), Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.NORMAL, 10);
            Label studentID = null;
            studentID = TA_ConvenienceMethods.createLabel(studentID, entry.getKey().toString(), Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.NORMAL, 10);
            
            // Create CheckBoxes for attendance keeping
            CheckBox presentBox = new CheckBox();
            CheckBox absentBox = new CheckBox();
            CheckBox tardyBox = new CheckBox();
            
            HBox newStudentBox = null;
            newStudentBox = TA_ConvenienceMethods.createHBox(newStudentBox, Pos.CENTER, Double.MAX_VALUE, 25, Color.CORAL,
                    5, 5, 5, 5, studentID, studentName, presentBox, absentBox, tardyBox);
            
            // Determine which side to add the HBox to
            int divideBody = studentNamesMap.size()/2;
            if(count < divideBody){
                // Add newStudentBox to the left
                attendanceBoxLeft.getChildren().add(newStudentBox);
            }
            else{
                // Add newStudentBox to the right
                attendanceBoxRight.getChildren().add(newStudentBox);
            }
            count++;
        }
        
        
        
        
        // Setup Button action listeners
        classPageButton.setOnAction(event -> {
            System.out.println("Moving to SelectClassPage");
            TeachersAssistant.getMainStage().setScene(TA_SelectClassPage.startSelectClassPage());
        });
        
        gradesPageButton.setOnAction(event -> {
            System.out.println("Moving to GradesPage");
        });
        
        reportsPageButton.setOnAction(event -> {
            System.out.println("Moving to ReportsPage");
        });
        
        
               
        // Add the top "title" boxes (dateDisplayBox and dateChangeBox)
        gridPane.add(dateDisplayBox,1,1,2,1);
        gridPane.add(dateChangeBox, 3,1,2,1);
        
        // Add the body "attendance" boxes
        gridPane.add(attendanceBoxLeft, 1,3,2,1);
        gridPane.add(attendanceBoxRight, 3,3,2,1);
        

        // Return the gridPane
        return gridPane;
    }
    
    

    //  --------------------------------------------- GET/SET METHODS ---------------------------------------------
    public static Scene getScene(){
        return selectClassScene;
    }
    
}
