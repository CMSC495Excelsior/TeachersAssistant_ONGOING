/**
 * FileName: TA_AlertPage.java
 * Author: Stephen James
 * Date: 11/03/19
 * Course: CMSC-495
 * 
 * Objective: To create a pop-up window used to prompt the user of errors/warnings.
*/

// Package
package teachersassistant;

// Import statements
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

// Main class
public class TA_AlertPage {

    // Create all JavaFX components
    private static Label confirmLabel;
    private static Stage alertPage;
    private static Button okButton;
    private static HBox buttonBox, textBox;
    private static GridPane gridPane;
    private static boolean yesContinue = false;

    // Method to create and return a basic alert page
    public static void alert(String title, String message, Color color){
        // Create values for setting the alert in the center of the stage
        double x = (TeachersAssistant.getMainStage().getWidth()/2) - (TeachersAssistant.getMainStage().getWidth()
                /6)+ TeachersAssistant.getMainStage().getX();

        double y = (TeachersAssistant.getMainStage().getHeight()/2 - (TeachersAssistant.getMainStage().getHeight()
                /6)) + TeachersAssistant.getMainStage().getY();

        // Initialize the alertPage
        alertPage = new Stage();
        alertPage.setResizable(false);

        // Make window modal
        alertPage.initModality(Modality.APPLICATION_MODAL);

        // Set stage properties
        alertPage.setTitle(title);
        alertPage.setMinWidth(250);
        alertPage.setX(x);
        alertPage.setY(y);

        // Create the GridPane
        makeGridPane(message, color);

        // Create Action Handling for the okButton
        okButton.setOnAction(event -> alertPage.close());
        
        // Create BorderPane
        BorderPane borderPane = new BorderPane();
        borderPane = TA_ConvenienceMethods.createBorderPane(borderPane, Color.web("#3b3a30"), null,
                null, null, null, gridPane);
        borderPane.setPadding(new Insets(10,10,10,10));

        // Set the layout to the Scene
        Scene scene = new Scene(borderPane);
        alertPage.setScene(scene);

        alertPage.showAndWait();
    }
    
    
    // Warning pop-up (doesn't prevent user from progressing, but it gives them a change to turn back)
    public static boolean alert(String title, String message, Color color, boolean warningOption){
        // Create values for setting the alert in the center of the stage
        double x = (TeachersAssistant.getMainStage().getWidth()/2) - (TeachersAssistant.getMainStage().getWidth()
                /6)+ TeachersAssistant.getMainStage().getX();

        double y = (TeachersAssistant.getMainStage().getHeight()/2 - (TeachersAssistant.getMainStage().getHeight()
                /6)) + TeachersAssistant.getMainStage().getY();

        // Initialize the alertPage
        alertPage = new Stage();
        alertPage.setResizable(false);

        // Make window modal
        alertPage.initModality(Modality.APPLICATION_MODAL);

        // Set stage properties
        alertPage.setTitle(title);
        alertPage.setMinWidth(250);
        alertPage.setX(x);
        alertPage.setY(y);
        
        Button yesButton = new Button("Yes");
        Button noButton = new Button("No");
        
        yesButton.setOnAction(event -> {
            // Set yesContinue
            yesContinue = true;
            
            // Close the alertPage
            alertPage.close();
        });

        noButton.setOnAction(event -> {
            // Set yesContinue
            yesContinue = false;
            
            // Close the alertPage
            alertPage.close();
        });
        
        // Create new HBox
        HBox warningBox = null;
        warningBox = TA_ConvenienceMethods.createHBox(warningBox, Pos.CENTER, Double.MAX_VALUE, 10, null, 5, 5, 5, 5, yesButton, noButton);

        // Create the GridPane
        makeGridPane(message, color);
        
        // Remove the ok button
        gridPane.getChildren().remove(buttonBox);
        
        // Add quiteBox
        gridPane.add(warningBox,0,1,3,1);
      
        // Create BorderPane
        BorderPane borderPane = new BorderPane();
        borderPane = TA_ConvenienceMethods.createBorderPane(borderPane, Color.web("#3b3a30"), null,
                null, null, null, gridPane);
        borderPane.setPadding(new Insets(10,10,10,10));

        // Set the layout to the Scene
        Scene scene = new Scene(borderPane);
        alertPage.setScene(scene);

        alertPage.showAndWait();
        
        // Return yesContinue
        return yesContinue;
    }
    
    

    // Overloaded method used as the QuitPage
    public static void alert(String title, String message, Color color, Button yesButton, Button noButton){
        // Create values for setting the alert in the center of the stage
        double x = (TeachersAssistant.getMainStage().getWidth()/2) - (TeachersAssistant.getMainStage().getWidth()
                /6)+ TeachersAssistant.getMainStage().getX();

        double y = (TeachersAssistant.getMainStage().getHeight()/2 - (TeachersAssistant.getMainStage().getHeight()
                /6)) + TeachersAssistant.getMainStage().getY();

        // Initialize the alertPage
        alertPage = new Stage();
        alertPage.setResizable(false);

        // Make window modal
        alertPage.initModality(Modality.APPLICATION_MODAL);

        // Set stage properties
        alertPage.setTitle(title);
        alertPage.setMinWidth(250);
        alertPage.setX(x);
        alertPage.setY(y);

        // Set action events
        yesButton.setOnAction(event -> System.exit(0));
        noButton.setOnAction(event -> alertPage.close());
        
        // Create new HBox
        HBox quitBox = null;
        quitBox = TA_ConvenienceMethods.createHBox(quitBox, Pos.CENTER, Double.MAX_VALUE, 10, null, 5, 5, 5, 5, yesButton, noButton);

        // Create the GridPane & add the quitBox
        makeGridPane(message, color);
        
        // Remove the ok button
        gridPane.getChildren().remove(buttonBox);
        
        // Add quiteBox
        gridPane.add(quitBox,0,1,3,1);
        
        // Create BorderPane
        BorderPane borderPane = new BorderPane();
        borderPane = TA_ConvenienceMethods.createBorderPane(borderPane, Color.web("#3b3a30"), null,
                null, null, null, gridPane);
        borderPane.setPadding(new Insets(10,10,10,10));

        // Set the layout to the Scene
        Scene scene = new Scene(borderPane);
        alertPage.setScene(scene);

        alertPage.showAndWait();
    }

    // Create method to create a GridPane
    private static GridPane makeGridPane(String message, Color color){
        // Create & customize GridPane
        gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.backgroundProperty().setValue(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
        gridPane.setAlignment(Pos.CENTER);

        // Create button
        okButton = new Button("Ok");

        // Create label
        confirmLabel = TA_ConvenienceMethods.createLabel(confirmLabel, message, Double.MAX_VALUE, Pos.CENTER,
                "Arial", FontWeight.BOLD, 17);

        // Create HBox
        textBox = TA_ConvenienceMethods.createHBox(textBox, Pos.CENTER, Double.MAX_VALUE, 10, null,
                5, 5, 5, 5, confirmLabel);
        
        // Create HBox
        buttonBox = TA_ConvenienceMethods.createHBox(buttonBox, Pos.CENTER, Double.MAX_VALUE, 20, null,
                20, 20, 10, 10, okButton);

        // Add everything to GridPane
        gridPane.add(textBox,0,0,3,1);
        gridPane.add(buttonBox,0,1,3,1);

        return gridPane;


    }
}
