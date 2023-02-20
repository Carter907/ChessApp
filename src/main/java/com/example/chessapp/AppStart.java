package com.example.chessapp;
import com.example.chessapp.controller.ChessViewController;
import com.example.chessapp.view.ChessView;
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.stage.*;

public class AppStart extends Application {

    private Stage window;

    @Override
    public void start(Stage stage) {
        window = stage;
        ChessViewController chessViewController = new ChessViewController(new ChessView(window));
        Scene scene = new Scene(chessViewController.getView());
        scene.getStylesheets().add(getClass().getResource("css/Application.css").toExternalForm());
        stage.getIcons().add(new Image(AppStart.class.getResource("icons/app_icon.png").toExternalForm()));
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setTitle("ChessFX");
        stage.show();

        stage.setOnCloseRequest(e -> Platform.exit()); // program exit on close regardless of threads active
    }


    public static void main(String[] args) {
        launch(args);
    }
}