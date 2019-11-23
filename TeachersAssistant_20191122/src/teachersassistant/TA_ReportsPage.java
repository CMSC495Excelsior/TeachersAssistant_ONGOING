/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package teachersassistant;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

/**
 * NOTE:
 *  - Read line 239 for next functionality
 */
public class TA_ReportsPage {
    
    
    // Create GUI components
    private static Scene reportsScene;
    private static BorderPane borderPane;
    private static GridPane gridPane;
    private static Button generateReportButton, classPageButton, attendanceButton, 
            gradesPageButton, viewStudents_CurrentClass, viewStudents_ALL, selectAllStudents;
    private static int course_ID;
    private static ScrollPane scrollPane = null;
    
    // Create only the larger H/VBox containers
    private static HBox reportsBoxLeft_TITLE, reportsBoxRight_TITLE, reportsBodyCURRENT_MASTER, reportsBodyALL_MASTER;
    private static VBox reportsBoxLeft_CURRENT, reportsBoxRight_CURRENT, reportsBoxLeft_ALL, reportsBoxRight_ALL;
    
    // Create all required lists
    private static TreeMap<Integer, CheckBox> studentReportsMap_ALL = new TreeMap<>();
    private static TreeMap<Integer, CheckBox> studentReportsMap_CURRENT = new TreeMap<>();
    
    
    // Main Method used for returning a Scene object to the Main Stage
    public static Scene startReportsPage(int courseID_Arg){ 
        // Set the courseID
        course_ID = courseID_Arg;
        
        // Make BorderPane
        makeBorderPane();

        // Set static vars
        reportsScene = new Scene(borderPane);
        
        // Return Scene
        return reportsScene;
    }
    
    
    // ----------------------------------- Supplemental Methods Below -----------------------------------
    

    // Method for creating the borderpane
    private static BorderPane makeBorderPane(){
        // Create ALL buttons
        classPageButton = new Button("Change Class");
        classPageButton.setAlignment(Pos.CENTER);
        classPageButton.setPadding(new Insets(10,10,10,10));
        
        attendanceButton = new Button("Attendance");
        attendanceButton.setAlignment(Pos.CENTER);
        attendanceButton.setPadding(new Insets(10,10,10,10));
        
        // Create Confirm Button - action listener set in "makeGridPane()"
        generateReportButton = new Button("Generate Report");
        generateReportButton.setAlignment(Pos.CENTER);
        generateReportButton.setPadding(new Insets(10,10,10,10));
        
        gradesPageButton = new Button("Grades");
        gradesPageButton.setAlignment(Pos.CENTER);
        gradesPageButton.setPadding(new Insets(10,10,10,10));
        
        viewStudents_CurrentClass = new Button("View Current Class");
        viewStudents_CurrentClass.setAlignment(Pos.CENTER);
        viewStudents_CurrentClass.setPadding(new Insets(5,5,5,5));
        
        viewStudents_ALL = new Button("View ALL Students");
        viewStudents_ALL.setAlignment(Pos.CENTER);
        viewStudents_ALL.setPadding(new Insets(5,5,5,5));
        
        selectAllStudents = new Button("Select All Currently Displayed");
        selectAllStudents.setAlignment(Pos.CENTER);
        selectAllStudents.setPadding(new Insets(5,5,5,5));
        
        // Make the GridPane
        makeGridPane();
        
        // Create HBox for confirmButton
        HBox buttonBox = null;
        buttonBox = TA_ConvenienceMethods.createHBox(buttonBox, Pos.CENTER, Double.MAX_VALUE, 15, Color.LIGHTBLUE,
                10, 10, 10, 10, gradesPageButton, classPageButton, attendanceButton);
        
        
        // Create a Label for the greeting
        Label greetingLabel = null;
        greetingLabel = TA_ConvenienceMethods.createLabel(greetingLabel, "SCHOOL NAME > COURSE NAME > REPORTS",
                Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.BOLD, 20);
        greetingLabel.setPadding(new Insets(10,10,30,10));

        // Create borderPane
        borderPane = TA_ConvenienceMethods.createBorderPane(borderPane, Color.BEIGE,greetingLabel,
                buttonBox, null, null, gridPane);
        borderPane.setPadding(new Insets(10,10,10,10));
        

        // Return borderPane
        return borderPane;
    }

    // Create the GridPane
    private static GridPane makeGridPane(){
        // Initialize and customize GridPane
        gridPane = new GridPane();
        gridPane.setPadding(new Insets(10,10,10,10));
        gridPane.setHgap(15);
        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.CENTER);
        
        
        // ----------------------------------------- CREATE LABELS -----------------------------------------
        
        Label idLabel = null;
        idLabel = TA_ConvenienceMethods.createLabel(idLabel, "ID", Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.BOLD, 10);
        
        Label studentLabel = null;
        studentLabel = TA_ConvenienceMethods.createLabel(studentLabel, "Student", Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.BOLD, 10);
        
        
        // --------------- Duplicate Labels for the right-side attendance pane ---------------
        
        
        Label idLabel2 = null;
        idLabel2 = TA_ConvenienceMethods.createLabel(idLabel2, "ID", Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.BOLD, 10);
        
        Label studentLabel2 = null;
        studentLabel2 = TA_ConvenienceMethods.createLabel(studentLabel2, "Student", Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.BOLD, 10);
        
        // ----------------------------------------- CREATE H/VBOXES -----------------------------------------
        
        VBox sideButtonBox = null;
        sideButtonBox = TA_ConvenienceMethods.createVBox(sideButtonBox, Pos.CENTER, Double.MAX_VALUE,
                10, null, 5, 5, 5, 5, selectAllStudents, viewStudents_CurrentClass, viewStudents_ALL);
        
        // Create all boxes
        createAllBoxes(idLabel, idLabel2, studentLabel, studentLabel2);
        
        // Create Nodes array
        Node[] labelArray = {
            idLabel,idLabel2,
            studentLabel,studentLabel2,
            reportsBoxLeft_TITLE,reportsBoxRight_TITLE,
            reportsBoxLeft_CURRENT,reportsBoxRight_CURRENT,
            reportsBoxLeft_ALL, reportsBoxRight_ALL,
            reportsBodyCURRENT_MASTER, reportsBodyALL_MASTER
        };
        
        
        // Iterate through ALL nodes and set Hgrow
        for(int i = 0; i < labelArray.length; i++){
            // Set Hgrow
            HBox.setHgrow(labelArray[i], Priority.ALWAYS);
        }
        
        
        
        // ---------------------------------- POPULATE Student Lists ----------------------------------
        
        // Populate list for current course
        populateStudentList_CURRENT();
        
        // Populate list for ALL courses
        populateStudentList_ALL();
        
        // Set the action listeners
        setActionListeners();
        
        // Create HBox
        HBox reportButtonBox = null;
        reportButtonBox = TA_ConvenienceMethods.createHBox(reportButtonBox, Pos.CENTER, Double.MAX_VALUE, 50, null, 5, 5, 5, 5, generateReportButton);
        
        // Create prompt
        Label prompt = null;
        prompt = TA_ConvenienceMethods.createLabel(prompt, "Please select the students you'd like to generate a report for.", Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.BOLD, 20);
        prompt.setPadding(new Insets(25,25,25,25));
        
        gridPane.add(prompt,1,1,2,1);
        gridPane.add(reportButtonBox, 4,3,2,1);
        gridPane.add(sideButtonBox, 4,4,2,1);
        gridPane.add(reportsBoxLeft_TITLE,1,2,1,1);
        
        // Add Children to GridPane
        if(scrollPane != null){
            // Add gridPane to scrollPane
            gridPane.add(scrollPane,1,3,2,1);
            gridPane.add(reportsBoxRight_TITLE,2,2,1,1);
        }
        else{
            // Add the body "attendance" boxes
            gridPane.add(reportsBoxLeft_CURRENT, 1,3,2,1);
            gridPane.add(reportsBoxRight_CURRENT, 3,3,2,1);
        }

        // Return the gridPane
        return gridPane;
    }
    
    
    // Method to setup action listeners
    private static void setActionListeners(){
    
        // Create event listener for openButton
        generateReportButton.setOnAction(event -> {
            // Create new TreeMap to hold all SELECTED students
            TreeMap<Integer, CheckBox> selectedStudentsMap = new TreeMap<>();
            
            // Save all selected CheckBoxes to the new TreeMap
            if(scrollPane.getContent().equals(reportsBodyCURRENT_MASTER)){
                // Iterate through the CURRENT COURSE Body
                for(Map.Entry<Integer, CheckBox> tempCurrentMaster : studentReportsMap_CURRENT.entrySet()){
                    // Add all selected boxes to the selectedStudentsMap
                    if(tempCurrentMaster.getValue().isSelected())
                        selectedStudentsMap.put(tempCurrentMaster.getKey(), tempCurrentMaster.getValue());
                }
            }
            else{
                // Iterate through the ALL Students Body
                for(Map.Entry<Integer, CheckBox> tempCurrentMaster : studentReportsMap_ALL.entrySet()){
                    // Add all selected boxes to the selectedStudentsMap
                    if(tempCurrentMaster.getValue().isSelected())
                        selectedStudentsMap.put(tempCurrentMaster.getKey(), tempCurrentMaster.getValue());
                }
            }
            
            // Now, try creating report with the selected students (if there are any)
            try {                
                if(selectedStudentsMap.isEmpty()){
                    if(TA_AlertPage.alert("CheckBox Selection Warning", "You haven\'t selected any students from the provided list.\n"
                            + "As such, a report for ALL students will be created by default.\n"
                            + "Would you like to continue?", Color.CORAL, true)){
                        // Save masterStudentList
                        ArrayList<TreeMap<Integer, ArrayList<TreeMap<String, ArrayList<TreeMap<String, String>>>>>> masterStudentReportList = 
                                TA_SQLFunctions.createMasterStudentReportList();
                        
                        // Check if any CheckBoxes have been selected
                        if(masterStudentReportList != null){
                            // Use masterStudentList to create a report card for each student
                            TA_WordFunctions.makeStudentReport_Default(masterStudentReportList);
                            System.out.println("\nMaster Student List has been successfully created.");
                        }
                        else{
                            // Prompt user of their choice
                            TA_AlertPage.alert("Reports Prevention", "You have opted to not generate reports.", Color.CORAL);
                        }
                        
                    }
                }
                else{
                    // Save masterStudentList using the TreeMap<Integer, CheckBox> to create a master list of ONLY the students selected
                    ArrayList<TreeMap<Integer, ArrayList<TreeMap<String, ArrayList<TreeMap<String, String>>>>>> masterStudentReportList = 
                            TA_SQLFunctions.createMasterStudentReportList(selectedStudentsMap);
                    // Check if any CheckBoxes have been selected
                    if(masterStudentReportList != null){
                        // Use masterStudentList to create a report card for each student
                        TA_WordFunctions.makeStudentReport_Default(masterStudentReportList);
                        System.out.println("\nMaster Student List has been successfully created.");
                    }
                    else{
                        // Prompt user of their choice
                        TA_AlertPage.alert("Reports Prevention", "You have opted to not generate reports.", Color.CORAL);
                    }
                }
            }catch (NullPointerException e){
                e.printStackTrace();
                TA_AlertPage.alert("NULL Value", "There appears to be a NULL value in the Word Processing method.", Color.CORAL);
            }catch(Exception f){
                f.printStackTrace();
                TA_AlertPage.alert("Error", "Sorry, we were unable to create your document, please try again.", Color.CORAL);
            }
        });
        
        // Setup Button action listeners
        classPageButton.setOnAction(event -> {
            System.out.println("Moving to SelectClassPage");
            TeachersAssistant.getMainStage().setScene(TA_SelectClassPage.startSelectClassPage());
        });
        
        attendanceButton.setOnAction(event -> {
            System.out.println("Moving to AttendancePage");
            TeachersAssistant.getMainStage().setScene(TA_AttendancePage.startAttendancePage(course_ID));
        });
        
        gradesPageButton.setOnAction(event -> {
            System.out.println("Moving to GradesPage");
            TeachersAssistant.getMainStage().setScene(TA_GradesPage.startGradesPage(course_ID));
        });
        
        viewStudents_CurrentClass.setOnAction(event -> {
            // Set the ScrollPane content
            scrollPane.setContent(reportsBodyCURRENT_MASTER);
            
            // Unset all checks in the other pane
            for(Map.Entry<Integer, CheckBox> tempCurrentMaster : studentReportsMap_ALL.entrySet()){
                // Mark all boxes
                tempCurrentMaster.getValue().setSelected(false);
            }
        });
        
        viewStudents_ALL.setOnAction(event -> {
            // Set the ScrollPane content
            scrollPane.setContent(reportsBodyALL_MASTER);
            
            // Unset all checks in the other pane
            for(Map.Entry<Integer, CheckBox> tempCurrentMaster : studentReportsMap_CURRENT.entrySet()){
                // Mark all boxes
                tempCurrentMaster.getValue().setSelected(false);
            }
            
        });
        
        selectAllStudents.setOnAction(event -> {
            // Get the content from ScrollPane
            HBox tempBox = (HBox)scrollPane.getContent();
            if(tempBox.equals(reportsBodyCURRENT_MASTER)){
                // Mark all boxes on that page - Iterate through TreeMap
                for(Map.Entry<Integer, CheckBox> tempCurrentMaster : studentReportsMap_CURRENT.entrySet()){
                    // Mark all boxes
                    if(tempCurrentMaster.getValue().isSelected())
                        tempCurrentMaster.getValue().setSelected(false);
                    else
                        tempCurrentMaster.getValue().setSelected(true);
                }
            }
            else{
                // Mark all boxes on the ALL Master page
                for(Map.Entry<Integer, CheckBox> tempCurrentMaster : studentReportsMap_ALL.entrySet()){
                    // Mark all boxes
                    if(tempCurrentMaster.getValue().isSelected())
                        tempCurrentMaster.getValue().setSelected(false);
                    else
                        tempCurrentMaster.getValue().setSelected(true);
                }
            }
        });
    }
    
    
    // Method to populate the student listing of the CURRENT course
    private static void populateStudentList_CURRENT(){
        // Save studentNamesMap
        TreeMap<Integer, String> studentNamesMap_CURRENT = TA_SQLFunctions.populateStudentsMap(course_ID);
        
        // Variables to aid controlling which side a record will be added to
        int count = 0;
        int divideBody = studentNamesMap_CURRENT.size()/2;
        
        // Iterate through the studentNamesMap to create/add all needed GUI elements to the page
        for(Map.Entry<Integer, String> entry : studentNamesMap_CURRENT.entrySet()){
            // Create new Labels for Student ID/Name
            Label studentName = null;
            studentName = TA_ConvenienceMethods.createLabel(studentName, entry.getValue(), Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.NORMAL, 10);
            studentName.setPrefWidth(100);
            
            Label studentID = null;
            studentID = TA_ConvenienceMethods.createLabel(studentID, entry.getKey().toString(), Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.NORMAL, 10);
            studentID.setPrefWidth(50);
            
            // Put CheckBox into a TreeMap<Integer (studentID), CheckBox> to see which students the user has selected
            CheckBox studentCheckBox = new CheckBox();
            
            studentReportsMap_CURRENT.put(entry.getKey(), studentCheckBox);
            
            // Create new HBox for new student
            HBox newStudentBox = null;
            newStudentBox = TA_ConvenienceMethods.createHBox(newStudentBox, Pos.CENTER_LEFT, Double.MAX_VALUE, 25, Color.CORAL,
                    5, 5, 5, 5, studentCheckBox, studentID, studentName);
            newStudentBox.setPrefWidth(500);
            
            // Modify Hgrow attributes
            HBox.setHgrow(newStudentBox, Priority.ALWAYS);
            HBox.setHgrow(studentID, Priority.ALWAYS);
            HBox.setHgrow(studentName, Priority.ALWAYS);
            
            
            // Switch statements for appending records to the appropriate boxes
            switch(studentNamesMap_CURRENT.size()){
                case 1:
                    // Adjust title box widths
                    reportsBoxLeft_TITLE.setPrefWidth(reportsBodyCURRENT_MASTER.getPrefWidth());
                    reportsBoxRight_TITLE.setPrefWidth(reportsBodyCURRENT_MASTER.getPrefWidth());
                    
                    // Add newStudentBox to the LEFT & set the RIGHT box invisible
                    reportsBoxLeft_CURRENT.getChildren().add(newStudentBox);
                    reportsBoxRight_CURRENT.setVisible(false);
                    break;
                default:
                    // Adjust title box widths
                    reportsBoxLeft_TITLE.setPrefWidth(reportsBodyCURRENT_MASTER.getPrefWidth()/2);
                    reportsBoxRight_TITLE.setPrefWidth(reportsBodyCURRENT_MASTER.getPrefWidth()/2);
                    
                    // Determine which body section to add the records to
                    if(count < divideBody){
                        // Add newStudentBox to the LEFT
                        reportsBoxLeft_CURRENT.getChildren().add(newStudentBox);
                    }
                    else{
                        // Add newStudentBox to the RIGHT
                        reportsBoxRight_CURRENT.getChildren().add(newStudentBox);
                    }
                    break;
            }
            
            // Increment counter
            count++;
        }
        
        // Create ScrollPane for ease of viewing bulk records
        if(studentNamesMap_CURRENT.size() > 10){
            scrollPane = new ScrollPane();
            scrollPane.setFitToHeight(true);
            scrollPane.setFitToWidth(true);
            scrollPane.setContent(reportsBodyCURRENT_MASTER);
        }
    
    }
    
    
    // Method to populate the student listing of the CURRENT course
    private static void populateStudentList_ALL(){
        // TreeMap for tracking which students the user wants to report on
        TreeMap<Integer, String> studentNamesMap_ALL = TA_SQLFunctions.populateStudentsMap_ReportPage();
        
        // Variables to aid controlling which side a record will be added to
        int count = 0;
        int divideBody = studentNamesMap_ALL.size()/2;
        
        // Iterate through the studentNamesMap to create/add all needed GUI elements to the page
        for(Map.Entry<Integer, String> entry : studentNamesMap_ALL.entrySet()){
            // Create new Labels for Student ID/Name
            Label studentName = null;
            studentName = TA_ConvenienceMethods.createLabel(studentName, entry.getValue(), Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.NORMAL, 10);
            studentName.setPrefWidth(100);
            
            Label studentID = null;
            studentID = TA_ConvenienceMethods.createLabel(studentID, entry.getKey().toString(), Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.NORMAL, 10);
            studentID.setPrefWidth(50);
            
            // Put CheckBox into a TreeMap<Integer (studentID), CheckBox> to see which students the user has selected
            CheckBox studentCheckBox = new CheckBox();
            studentReportsMap_ALL.put(entry.getKey(), studentCheckBox);
            
            // Create new HBox for new student
            HBox newStudentBox = null;
            newStudentBox = TA_ConvenienceMethods.createHBox(newStudentBox, Pos.CENTER_LEFT, Double.MAX_VALUE, 25, Color.CORAL,
                    5, 5, 5, 5, studentCheckBox, studentID, studentName);
            newStudentBox.setPrefWidth(500);
            
            // Modify Hgrow attributes
            HBox.setHgrow(newStudentBox, Priority.ALWAYS);
            HBox.setHgrow(studentID, Priority.ALWAYS);
            HBox.setHgrow(studentName, Priority.ALWAYS);
            
            
            // Switch statements for appending records to the appropriate boxes
            switch(studentNamesMap_ALL.size()){
                case 1:
                    // Adjust title box widths
                    reportsBoxLeft_TITLE.setPrefWidth(reportsBodyALL_MASTER.getPrefWidth());
                    reportsBoxRight_TITLE.setPrefWidth(reportsBodyALL_MASTER.getPrefWidth());
                    
                    // Add newStudentBox to the LEFT & set the RIGHT box invisible
                    reportsBoxLeft_ALL.getChildren().add(newStudentBox);
                    reportsBoxRight_ALL.setVisible(false);
                    break;
                default:
                    // Adjust title box widths
                    reportsBoxLeft_TITLE.setPrefWidth(reportsBodyALL_MASTER.getPrefWidth()/2);
                    reportsBoxRight_TITLE.setPrefWidth(reportsBodyALL_MASTER.getPrefWidth()/2);
                    
                    // Determine which body section to add the records to
                    if(count < divideBody){
                        // Add newStudentBox to the LEFT
                        reportsBoxLeft_ALL.getChildren().add(newStudentBox);
                    }
                    else{
                        // Add newStudentBox to the RIGHT
                        reportsBoxRight_ALL.getChildren().add(newStudentBox);
                    }
                    break;
            }
            
            // Increment counter
            count++;
        }
        
        // Create ScrollPane for ease of viewing bulk records
        if(studentNamesMap_ALL.size() > 10){
            scrollPane = new ScrollPane();
            scrollPane.setFitToHeight(true);
            scrollPane.setFitToWidth(true);
            scrollPane.setContent(reportsBodyCURRENT_MASTER);
        }
    
    }
    
    
    private static void createAllBoxes(Label idLabel, Label idLabel2, Label studentLabel, Label studentLabel2){
        // Create and initialize ALL H/VBoxes
        
        // Titl boxes
        reportsBoxLeft_TITLE = null;
        reportsBoxLeft_TITLE = TA_ConvenienceMethods.createHBox(reportsBoxLeft_TITLE, Pos.CENTER, Double.MAX_VALUE,
                25, Color.WHITE, 5, 5, 5, 5, idLabel, studentLabel);
        
        
        reportsBoxRight_TITLE = null;
        reportsBoxRight_TITLE = TA_ConvenienceMethods.createHBox(reportsBoxRight_TITLE, Pos.CENTER, Double.MAX_VALUE,
                25, Color.WHITE, 5, 5, 5, 5, idLabel2, studentLabel2);
        
        
        // Create HBoxes for the MAIN BODY of the attendance page that will display all students and attendance options
        reportsBoxLeft_CURRENT = null;
        reportsBoxLeft_CURRENT = TA_ConvenienceMethods.createVBox(reportsBoxLeft_CURRENT, Pos.CENTER, Double.MAX_VALUE,
                10, Color.CADETBLUE, 5, 5, 5, 5);
        
        reportsBoxRight_CURRENT = null;
        reportsBoxRight_CURRENT = TA_ConvenienceMethods.createVBox(reportsBoxRight_CURRENT, Pos.CENTER, Double.MAX_VALUE,
                10, Color.CADETBLUE, 5, 5, 5, 5);
        
        reportsBoxLeft_ALL = null;
        reportsBoxLeft_ALL = TA_ConvenienceMethods.createVBox(reportsBoxLeft_ALL, Pos.CENTER, Double.MAX_VALUE,
                10, Color.CADETBLUE, 5, 5, 5, 5);
        
        reportsBoxRight_ALL = null;
        reportsBoxRight_ALL = TA_ConvenienceMethods.createVBox(reportsBoxRight_ALL, Pos.CENTER, Double.MAX_VALUE,
                10, Color.CADETBLUE, 5, 5, 5, 5);
        
        // Create MASTER HBox which will contain students in the CURRENT course in the body of the page
        reportsBodyCURRENT_MASTER = null;
        reportsBodyCURRENT_MASTER = TA_ConvenienceMethods.createHBox(reportsBodyCURRENT_MASTER, Pos.CENTER, Double.MAX_VALUE,
                10, Color.LIGHTGRAY, 5, 5, 5, 5, reportsBoxLeft_CURRENT, reportsBoxRight_CURRENT);
        reportsBodyCURRENT_MASTER.setPrefHeight(250);
        reportsBodyCURRENT_MASTER.setPrefWidth(700);
        
        // Create another MASTER HBox which will contain students for ALL courses
        reportsBodyALL_MASTER = null;
        reportsBodyALL_MASTER = TA_ConvenienceMethods.createHBox(reportsBodyALL_MASTER, Pos.CENTER, Double.MAX_VALUE,
                10, Color.LIGHTGRAY, 5, 5, 5, 5, reportsBoxLeft_ALL, reportsBoxRight_ALL);
        reportsBodyALL_MASTER.setPrefHeight(250);
        reportsBodyALL_MASTER.setPrefWidth(700);
    }
    

    //  --------------------------------------------- GET/SET METHODS ---------------------------------------------
    public static Scene getScene(){
        return reportsScene;
    }
    
}
