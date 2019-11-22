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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

/**
 *
 * @author SoftwareEng
 */
public class TA_ReportsPage {
    
    
    // Create GUI components
    private static Scene reportsScene;
    private static BorderPane borderPane;
    private static GridPane gridPane;
    private static Button generateReportButton, classPageButton, attendanceButton, gradesPageButton;
    private static int course_ID;
    
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
        gridPane.setAlignment(Pos.CENTER);
        
        // Create event listener for openButton
        generateReportButton.setOnAction(event -> {
            try {
                // Save masterStudentList
                ArrayList<TreeMap<Integer, ArrayList<TreeMap<String, ArrayList<TreeMap<String, String>>>>>> masterStudentReportList = 
                        TA_SQLFunctions.createMasterStudentReportList();
                
                // Use masterStudentList to create a report card for each student
                TA_WordFunctions.makeStudentReport(masterStudentReportList);
                System.out.println("\nMaster Student List has been successfully created.");
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
        
        // Create HBox
        HBox reportButtonBox = null;
        reportButtonBox = TA_ConvenienceMethods.createHBox(reportButtonBox, Pos.CENTER, Double.MAX_VALUE, 50, Color.CORAL, 10, 10, 10, 10, generateReportButton);
        
        // Create prompt
        Label prompt = null;
        prompt = TA_ConvenienceMethods.createLabel(prompt, "Click to generate a report!", Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.BOLD, 20);
        prompt.setPadding(new Insets(10,10,10,10));
        
        
               
        // Add all dropdown boxes for center of scene
        gridPane.add(prompt,0,1,2,1);
        gridPane.add(reportButtonBox,0,2,2,1);

        // Return the gridPane
        return gridPane;
    }
    
    

    //  --------------------------------------------- GET/SET METHODS ---------------------------------------------
    public static Scene getScene(){
        return reportsScene;
    }
    
}
