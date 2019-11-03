/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package teachersassistant;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author SoftwareEng
 */
public class TA_AlertPage {

    // Create all JavaFX components
    private static Label confirmLabel;
    private static Stage alertPage;
    private static Button okButton;
    private static HBox buttonBox;
    private static GridPane gridPane;


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

        // Set the layout to the Scene
        Scene scene = new Scene(gridPane);
        alertPage.setScene(scene);

        alertPage.showAndWait();
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

        yesButton.setOnAction(event -> System.exit(0));

        noButton.setOnAction(event -> alertPage.close());


        // Create the GridPane
        makeGridPane(message, color);
        gridPane.add(yesButton,0,2);
        gridPane.add(noButton,1,2);
        // Remove the ok button
        gridPane.getChildren().remove(1,2);

        // Set the layout to the Scene
        Scene scene = new Scene(gridPane);
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
        buttonBox = TA_ConvenienceMethods.createHBox(buttonBox, Pos.CENTER, Double.MAX_VALUE, 20, null,
                20, 20, 10, 10, okButton);

        // Add everything to GridPane
        gridPane.add(confirmLabel,0,0,3,1);
        gridPane.add(buttonBox,0,1,3,1);

        return gridPane;


    }
}
