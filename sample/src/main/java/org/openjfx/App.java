package org.openjfx;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Scanner;

/**
 * JavaFX App
 */
public class App extends Application {

//    @Override
//    public void start(Stage stage) {
//        var javaVersion = SystemInfo.javaVersion();
//        var javafxVersion = SystemInfo.javafxVersion();
//
//        var label = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
//        var scene = new Scene(new StackPane(label), 640, 480);
//        stage.setScene(scene);
//        stage.show();
//    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Group root  = new Group(); // arrange all nodes
        Scene scene = new Scene(root,600,600 ,Color.rgb(5,10,6)); // add root node to the scene

//        Image icon = new Image("dollar.png"); // add icon

        // place all images in main/resources and use the line below to get them
        Image icon = new Image(getClass().getResource("/dollar.png").toExternalForm());
        stage.getIcons().add(icon);

        // Set title of the Window
        stage.setTitle("Personal Finance Tracker");

        // set Width and height of window
//        stage.setWidth(420);
//        stage.setHeight(640);

        // place window in the middle by default or somwhere else ?
//        stage.setX();
//        stage.setY();

        // allow window to be resized ?
//        stage.setResizable(false);

        // window Fullscreen by default ?
//        stage.setFullScreen(true);

        stage.setScene(scene); // set the scene before presenting it
        stage.show();
    }
}