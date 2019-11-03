/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package teachersassistant;

/**
 *
 * @author SoftwareEng
 */
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class TA_ConvenienceMethods {

    // Create a method for creating VBoxes
    public static VBox createVBox(VBox vBox, Pos position, Double width, double spacing, Color color, int paddingLeft,
                                  int paddingRight, int paddingTop, int paddingBottom, Node... child){
        // Create new VBox for changing information
        vBox = new VBox();
        vBox.getChildren().addAll(child);
        vBox.setAlignment(position);
        vBox.setPadding(new Insets(paddingTop,paddingRight,paddingBottom,paddingLeft));
        vBox.setSpacing(spacing);
        vBox.setMaxWidth(width);
        vBox.backgroundProperty().setValue(new Background(
                new BackgroundFill(color, null, null)));

        // Return the newly created VBox
        return vBox;
    }

    // Create a method for creating HBoxes
    public static HBox createHBox(HBox hBox, Pos position, Double width, double spacing, Color color, int paddingLeft,
                                  int paddingRight, int paddingTop, int paddingBottom, Node... child){
        // Create new VBox for changing information
        hBox = new HBox();
        hBox.getChildren().addAll(child);
        hBox.setAlignment(position);
        hBox.setPadding(new Insets(paddingTop,paddingRight,paddingBottom,paddingLeft));
        hBox.setSpacing(spacing);
        hBox.setMaxWidth(width);
        hBox.backgroundProperty().setValue(new Background(
                new BackgroundFill(color, null, null)));

        // Return the newly created HBox
        return hBox;
    }

    // Create a method for creating Labels
    public static Label createLabel(Label label, String labelText, Double width, Pos position, String font,
                                    FontWeight fontStyle, int fontSize){
        // Create label
        label = new Label(labelText);
        label.setMaxWidth(width);
        label.setAlignment(position);
        label.setFont(Font.font(font, fontStyle, fontSize));

        // Return the newly created Label
        return label;
    }

    // Create a method for creating TextFields
    public static TextField createTextField(TextField textField, int columnCount, double maxWidth,
                                            double maxHeight, int paddingLeft, int paddingRight,
                                            int paddingTop, int paddingBottom){
        textField = new TextField();
        textField.setPrefColumnCount(columnCount);
        textField.setMaxSize(maxWidth,maxHeight);
        textField.setPadding(new Insets(paddingTop,paddingRight,paddingBottom,paddingLeft));

        // Return the newly created TextField
        return textField;
    }

    // Create a method for creating BorderPanes
    public static BorderPane createBorderPane(BorderPane borderPane, Color color, Node topChild,
                                              Node bottomChild, Node leftChild, Node rightChild, Node centerChild){
        // Create new BorderPane to nest layouts inside
        borderPane = new BorderPane();
        borderPane.backgroundProperty().setValue(new Background(new BackgroundFill(color,
                CornerRadii.EMPTY, Insets.EMPTY)));
        borderPane.setTop(topChild);
        borderPane.setBottom(bottomChild);
        borderPane.setCenter(centerChild);
        borderPane.setRight(rightChild);
        borderPane.setLeft(leftChild);

        // Return the newly created BorderPane
        return borderPane;
    }
}
