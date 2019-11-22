/**
 * FileName: TA_SelectClassPage.java
 * Author: Stephen James
 * Date: 11/18/19
 * Course: CMSC-495
 * 
 * Objective: To create an application page that will be used for selecting a class.
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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

// Main Class
public class TA_SelectClassPage{
    
    // Create GUI components
    private static Scene selectClassScene;
    private static BorderPane borderPane;
    private static GridPane gridPane;
    private static Button confirmButton;
    
    // TreeMap basic structure for storing Course data
    /*
    Subject: English
        Grade Level: 10
            Class Number: 1
            Class Number: 2
        Grades Level: 11
            Class Number: 1
    
    */
   
    // Main Method used for returning a Scene object to the Main Stage
    public static Scene startSelectClassPage(){        
        // Make BorderPane
        makeBorderPane();

        // Set static vars
        selectClassScene = new Scene(borderPane);
        
        // Return Scene
        return selectClassScene;
    }
    
    
    // ----------------------------------- Supplemental Methods Below -----------------------------------
    

    // Method for creating the borderpane
    private static BorderPane makeBorderPane(){
        
        
        // Create Confirm Button - action listener set in "makeGridPane()"
        confirmButton = new Button("Confirm");
        confirmButton.setAlignment(Pos.CENTER);
        confirmButton.setPadding(new Insets(10,10,10,10));
        
        // Make the GridPane
        makeGridPane();
        
        // Create HBox for confirmButton
        HBox buttonBox = null;
        buttonBox = TA_ConvenienceMethods.createHBox(buttonBox, Pos.CENTER, Double.MAX_VALUE, 15, Color.LIGHTBLUE, 10, 10, 10, 10, confirmButton);
        
        
        // Create a Label for the greeting
        Label greetingLabel = null;
        greetingLabel = TA_ConvenienceMethods.createLabel(greetingLabel, "SCHOOL NAME > ",
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
        // TreeMap<String, TreeMap<Integer, ArrayList<Integer>>>
        // Initialize and customize GridPane
        gridPane = new GridPane();
        gridPane.setPadding(new Insets(10,10,10,10));
        gridPane.setAlignment(Pos.CENTER);
        
        // Initialize all ObservableLists for the ComboBoxes
        ObservableList<String> classSubjectList = FXCollections.observableArrayList();
        ObservableList<Integer> classGradeLevelList = FXCollections.observableArrayList();
        ObservableList<Integer> classNumberList = FXCollections.observableArrayList();
        
        
        // Create all ComboBoxes
        ComboBox classSubject = new ComboBox(classSubjectList); 
        classSubject.setPromptText("Subject");
        ComboBox classGradeLevel = new ComboBox(classGradeLevelList);
        classGradeLevel.setPromptText("Grade Level");
        ComboBox classNumber = new ComboBox(classNumberList);
        classNumber.setPromptText("Class Number");
                
        
        // Initialize masterList returned by TA_SQLFunctions.generateCourseMap()
        ArrayList<TreeMap> masterList = TA_SQLFunctions.generateCourseMap();
        
        // Use the subject from the masterList to populate the classSubjectList
        for(TreeMap<String, TreeMap<Integer, ArrayList<Integer>>> map : masterList){
            // Iterate through each subject
            for(Map.Entry<String, TreeMap<Integer, ArrayList<Integer>>> entry : map.entrySet()){
                // Add each subject as an option in the Class Subject List
                classSubjectList.add(entry.getKey());
            }
        }
        
        
        // Add action listener to classSubject to change the gradeLevels displayed depending on what subject is selected
        classSubject.valueProperty().addListener(new ChangeListener<String>(){
            @Override public void changed(ObservableValue obValue, String oldValue, String newValue){
                System.out.println("Old String Value is: " + oldValue);
                System.out.println("New String Value is: " + newValue);
                
                // Clear the list
                classGradeLevelList.clear();
                
                // Change GradeLevel List
                for(TreeMap<String, TreeMap<Integer, ArrayList<Integer>>> map : masterList){
                    // Try getting the Map with the correct newValue
                    try{
                        TreeMap<Integer, ArrayList<Integer>> tempGradeLevelMap = map.get(newValue);
                        Integer[] gradeLevels = tempGradeLevelMap.keySet().toArray(new Integer[tempGradeLevelMap.keySet().size()]);
                        for(int i = 0; i < gradeLevels.length; i++){
                            classGradeLevelList.add((int) gradeLevels[i]);
                        }
                    }
                    catch(Exception e){
                        System.out.println(newValue + " not found at current index. Shifting to next index.");
                    }
                }
            }
            
        });
        
        
        // Add action listener to classGradeLevel to change the classNumbers displayed depending on what gradeLevel is selected
        classGradeLevel.valueProperty().addListener(new ChangeListener<Integer>(){
            @Override public void changed(ObservableValue obValue, Integer oldValue, Integer newValue){
                System.out.println("Old Integer Value is: " + oldValue);
                System.out.println("New Integer Value is: " + newValue);
                
                // Clear the list
                classNumberList.clear();
                
                // Change GradeLevel List
                for(TreeMap<String, TreeMap<Integer, ArrayList<Integer>>> map : masterList){
                    // Iterate through the Map
                    
                    // Try getting the Map with the correct newValue
                    try{
                        // Save temporary gradeLevelMap
                        TreeMap<Integer, ArrayList<Integer>> tempGradeLevelMap = map.get(String.valueOf(classSubject.getValue()));
                        ArrayList<Integer> tempClassNumberList = tempGradeLevelMap.get(newValue);
                        
                        // Iterate through the tempClassNumberList and save values to the classNumber ComboBox
                        for(int i = 0; i < tempClassNumberList.size(); i++){
                            classNumberList.add(tempClassNumberList.get(i));
                        }
                    }
                    catch(Exception e){
                        System.out.println(newValue + " not found at current index. Shifting to next index.");
                    }
                }
            }
            
        });
        
        // Create event listener for openButton
        confirmButton.setOnAction(event -> {
            try {
                // Create temp array to check all ComboBox values
                ComboBox[] boxArray = {
                    classSubject,
                    classGradeLevel,
                    classNumber
                };
                
                // Temp boolean value for determining if any values are null
                boolean isNull = false;
                
                // Iterate through boxArray and check values for each ComboBox
                for(int i = 0; i < boxArray.length; i++){
                    if(boxArray[i].getValue() == null){
                        isNull = true;
                    }
                    else{
                        isNull = false;
                        if(i == boxArray.length-1){
                            // Save the courseID
                            int newCourseID = TA_SQLFunctions.getCourseID(String.valueOf(classSubject.getValue()), 
                                    (int)classGradeLevel.getValue(), (int)classNumber.getValue());
                            // Progress the application, pass the courseID as an argument to the attendancePage
                            TeachersAssistant.getMainStage().setScene(TA_AttendancePage.startAttendancePage(newCourseID));
                            
                            // Use courseID to populate the next page (attendancePage)
                            
                        }                            
                    }                    
                }
                // Prompt user about error
                if(isNull)
                    TA_AlertPage.alert("Class Selection Error", "Please make a selection for ALL drop-down menus.", Color.CORAL);
                
                // Send user to proper class page
                System.out.println("Going to class page...");
            }catch (NullPointerException e){
                e.printStackTrace();
                
            }
        });
        
        // Create HBox
        HBox chooseClassBox = null;
        chooseClassBox = TA_ConvenienceMethods.createHBox(chooseClassBox, Pos.CENTER, Double.MAX_VALUE, 50, Color.CORAL, 10, 10, 10, 10, classSubject,
                classGradeLevel, classNumber);
        
        Label prompt = null;
        prompt = TA_ConvenienceMethods.createLabel(prompt, "Select Class: ", Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.BOLD, 20);
        prompt.setPadding(new Insets(10,10,10,10));
        
        
               
        // Add all dropdown boxes for center of scene
        gridPane.add(prompt,0,1,2,1);
        gridPane.add(chooseClassBox,0,2,2,1);

        // Return the gridPane
        return gridPane;
    }
    
    

    //  --------------------------------------------- GET/SET METHODS ---------------------------------------------
    public static Scene getScene(){
        return selectClassScene;
    }
    
}
