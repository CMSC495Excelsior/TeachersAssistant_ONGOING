/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package teachersassistant;

import java.io.File;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author SoftwareEng
 */
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

    @Override
    public void start(Stage primaryStage){
        // Create & customize primary stage
        primaryStage = new Stage();
        mainStage = primaryStage;
        primaryStage.setTitle("Excel Editor");
        primaryStage.setWidth(750);
        primaryStage.setHeight(650);
        primaryStage.setMaxWidth(1000);
        primaryStage.setMaxHeight(1000);
        primaryStage.setResizable(true);
        primaryStage.setOnCloseRequest(event -> {
            // Create local Buttons for the quitPage
            Button yesButton = new Button("Yes");
            Button noButton = new Button("No");
            // Customize Buttons
            yesButton.setMaxWidth(Double.MAX_VALUE);
            noButton.setMaxWidth(Double.MAX_VALUE);
            yesButton.setAlignment(Pos.CENTER);
            noButton.setAlignment(Pos.CENTER);

            TA_AlertPage.alert("Quit?", "Are you sure you'd like to quit?", null, yesButton,
                    noButton);
            // Consume the even if the user selects no, otherwise the program will terminate
            event.consume();
        });


        // Make BorderPane
        makeBorderPane();


        mainScene= new Scene(borderPane);
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    private BorderPane makeBorderPane(){
        makeGridPane();

        borderPane = TA_ConvenienceMethods.createBorderPane(borderPane, Color.BEIGE,greetingLabel,
                nextButton, null, null, gridPane);

        return borderPane;
    }

    private GridPane makeGridPane(){
        // Initialize and customize GridPane
        gridPane = new GridPane();
        gridPane.setPadding(new Insets(10,10,10,10));
        gridPane.setAlignment(Pos.CENTER);

        // Create a Label for the greeting
        greetingLabel = TA_ConvenienceMethods.createLabel(greetingLabel, "Please select an Excel Workbook or .csv file",
                Double.MAX_VALUE, Pos.CENTER, "Arial", FontWeight.BOLD, 20);
        greetingLabel.setPadding(new Insets(10,10,30,10));

        // Create Button for opening a file
        openButton = new Button("Choose File");
        openButton.setMaxWidth(Double.MAX_VALUE);

        // Next Button
        nextButton = new Button("Next");
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
                chosenFile = chooser.showOpenDialog(mainStage);
                pathField.setText(chosenFile.getAbsolutePath());
                System.out.println("Opening " + chosenFile.getAbsolutePath());
                nextButton.setVisible(true);
            }catch (NullPointerException e){
                e.printStackTrace();
                TA_AlertPage.alert("ALERT", "Please select a file", Color.RED);
            }
        });

        nextButton.setOnAction(event -> {
            System.out.println("You clicked next");
            
            // Try batch upload
            //TA_SQLFunctions.batchUpload("FILE_PATH");
            //mainStage.setScene(GUI_ChooseTaskPage.chooseTaskPage());
            boolean test = TA_SQLFunctions.batchUpload(chosenFile);
        });


        // Add all nodes to the GridPane
        gridPane.add(chooseFileBox,0,1,2,1);


        return gridPane;
    }





    // Get & Set methods
    public static Stage getMainStage(){
        return mainStage;
    }

    public static Scene getMainScene(){
        return mainScene;
    }

    public static File getChosenFile(){
        return chosenFile;
    }
    
    
}
