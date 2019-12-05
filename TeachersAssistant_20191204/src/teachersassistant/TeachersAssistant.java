/**
 * FileName: TeachersAssistant.java
 * Author: Stephen James
 * Date: 11/12/19
 * Course: CMSC-495
 * 
 * Objective: To create the main window and first opening scene (entrance point) for the application.
*/

// Package
package teachersassistant;

// Import statements
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.TreeMap;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

// Main class
public class TeachersAssistant extends Application {
    
    // Create GUI components
    private static Stage mainStage;
    private static Scene mainScene;
    private BorderPane borderPane;
    private GridPane gridPane;
    private Button openButton, nextButton;
    private TextField pathField;
    private Label greetingLabel;
    private HBox chooseFileBox;
    private static File chosenFile;
    
    // String variables to track the currently displayed page
    private static String courseName = "";
    private static String schoolName = "";

    // Start method (this creates the main window on which the rest of the application will run)
    @Override
    public void start(Stage primaryStage){        
        // Create & customize primary stage
        primaryStage = new Stage();
        mainStage = primaryStage;
        primaryStage.setTitle("Teacher's Assistant");
        primaryStage.setWidth(1000);
        primaryStage.setHeight(800);
        primaryStage.setMaxWidth(2000);
        primaryStage.setMaxHeight(2000);
        primaryStage.setMinHeight(800);
        primaryStage.setMinWidth(1000);
        primaryStage.setResizable(true);
        
        // Set on close request to avoid accidental closes
        primaryStage.setOnCloseRequest(event -> {
            // Create local Buttons for the quitPage
            Button yesButton = new Button("Yes");
            Button noButton = new Button("No");
            // Customize Buttons
            yesButton.setMaxWidth(Double.MAX_VALUE);
            noButton.setMaxWidth(Double.MAX_VALUE);
            yesButton.setAlignment(Pos.CENTER);
            noButton.setAlignment(Pos.CENTER);
            
            // Use the alertPage()
            TA_AlertPage.alert("Quit?", "Are you sure you'd like to quit?", null, yesButton,
                    noButton);
            
            // Consume the even if the user selects no, otherwise the program will terminate
            event.consume();
        });
        
        
        // Make BorderPane
        makeBorderPane();

        // Set static vars
        mainScene = new Scene(borderPane);
        
        // Set & show scene
        if(TA_SQLFunctions.checkStudents() == 0){
            primaryStage.setScene(mainScene);
        }
        else{
            TreeMap<String, String> tempConfigMap = TA_GeneralFunctions.checkConfig();
            if(tempConfigMap == null){
                // Prompt user
                if(TA_AlertPage.alert("Configuration File Error", "Configuration file missing. Database must be purged. Would you like to purge the Database now?", Color.CORAL, true)){
                    // Try purging the DB
                    try{
                        if(TA_SQLFunctions.resetDatabase()){
                            TA_AlertPage.alert("Database Purged", "Database successfully purged, please restart the application now.", Color.CORAL);
                            System.exit(1);
                        }
                        else{
                            //
                            TA_AlertPage.alert("Database Purge Error", "The Database failed to purge properly. Please contact your administrator.", Color.CORAL);
                        }
                    }catch(Exception e){
                        // Print the stack trace
                        e.printStackTrace();
                    }
                } 
                else
                    System.exit(1);
            }
            else{
                schoolName = tempConfigMap.get("SchoolName");
                primaryStage.setScene(TA_SelectClassPage.startSelectClassPage());
            }            
        }
        primaryStage.show();
    }

    // Method for creating the borderpane
    private BorderPane makeBorderPane(){
        // Make the GridPane
        makeGridPane();

        // Create borderPane
        borderPane = TA_ConvenienceMethods.createBorderPane(borderPane, Color.web("#eaece5"),greetingLabel,
                nextButton, null, null, gridPane);

        // Return borderPane
        return borderPane;
    }

    // Create the GridPane
    private GridPane makeGridPane(){
        // Initialize and customize GridPane
        gridPane = new GridPane();
        gridPane.setPadding(new Insets(10,10,10,10));
        gridPane.setAlignment(Pos.CENTER);

        // Create a Label for the greeting
        greetingLabel = TA_ConvenienceMethods.createLabel(greetingLabel, "Welcome to the Teacher's Assistant app!",
                Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.BOLD, 20);
        greetingLabel.setPadding(new Insets(10,10,30,10));
        
        // Default value of imageview
        ImageView imageView = null;

        // Try using the image
        try{
            // Create image
            Image image = new Image(new FileInputStream("C:\\Users\\Public\\Documents\\NetBeansProjects\\TeachersAssistant\\Pictures\\TeacherGuy.png"));
            imageView = new ImageView(image);

            imageView.setFitHeight(250);
            imageView.setFitWidth(250);
            imageView.setPreserveRatio(true);
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        }

        // Create Label for message
        Label greeting = null;
        greeting = TA_ConvenienceMethods.createLabel(greeting, "Welcome to Teacher's Assistant!\n"
                + "This is your first time logging in,\nplease upload your class list!", 
                Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.NORMAL, 15);

        // Add image & greeting
        gridPane.add(imageView,0,1,1,1);
        gridPane.add(greeting,1,1,1,1);

        // Create Button for opening a file
        openButton = new Button("Select Upload File");
        openButton.setMaxWidth(Double.MAX_VALUE);

        // Next Button
        nextButton = new Button("Begin Upload");
        nextButton.setMaxWidth(Double.MAX_VALUE);
        nextButton.setAlignment(Pos.CENTER);
        nextButton.setVisible(false);

        // Create TextField for displaying the chosen file
        pathField = TA_ConvenienceMethods.createTextField(pathField,30, Double.MAX_VALUE,
                5,10,10,10,10);
        pathField.setEditable(false);

        // Create HBox to store the button and field in
        chooseFileBox = TA_ConvenienceMethods.createHBox(chooseFileBox,Pos.CENTER,Double.MAX_VALUE, 50,
                null, 10,10,10,10, openButton, pathField);

        // Create a FileChooser object for the user to choose a file
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose Excel Workbook");
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Excel Workbooks",
                "*.xlsx"),
                new FileChooser.ExtensionFilter("CSV Files",
                        "*.csv"));  // USE .xlsb to save the file for Excel

        // Create event listener for openButton
        openButton.setOnAction(event -> {
            try {
                // Save chosenFile
                chosenFile = chooser.showOpenDialog(mainStage);
                pathField.setText(chosenFile.getAbsolutePath());
                // Set SchoolName
                schoolName = chosenFile.getAbsolutePath();
                schoolName = schoolName.substring(schoolName.lastIndexOf("\\")+1, schoolName.lastIndexOf("_"));
                
                // Create Config file
                TA_GeneralFunctions.createConfig(schoolName);
                
                if(schoolName.isEmpty())
                    schoolName = "Excelsior High School";
                
                System.out.println("Opening " + chosenFile.getAbsolutePath());
                nextButton.setVisible(true);
            }catch (NullPointerException e){
                e.printStackTrace();
                TA_AlertPage.alert("ALERT", "Please select a file", Color.RED);
            }
            catch(IndexOutOfBoundsException f){
                f.printStackTrace();
                TA_AlertPage.alert("ALERT", "Please check your filename. It should begin with your schoolname IMMEDIATELY followed by a \'_\'", Color.CORAL);
            }
        });

        nextButton.setOnAction(event -> {
            // Try batch upload
            if(TA_SQLFunctions.batchUpload(chosenFile)){
                ArrayList<String> test = TA_SQLFunctions.generateCourseMap();
                mainStage.setScene(TA_SelectClassPage.startSelectClassPage());
            }
            else{
            }
        });


        // Add all nodes to the GridPane
        gridPane.add(chooseFileBox,0,2,2,1);

        // Return the gridPane
        return gridPane;
           
    }

    
    //  --------------------------------------------- GET/SET METHODS ---------------------------------------------
    public static Stage getMainStage(){
        return mainStage;
    }

    public static Scene getMainScene(){
        return mainScene;
    }

    public static File getChosenFile(){
        return chosenFile;
    }
    
    public static String getCourseName(){
        return courseName;
    }
    
    public static void setCourseName(String newCourseName){
        courseName = newCourseName;
    }
    
    public static String getSchoolName(){
        return schoolName;
    }
    
    
}
