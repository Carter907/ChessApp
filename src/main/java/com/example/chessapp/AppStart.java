package com.example.chessapp;

import com.example.chessapp.board.Board;
import com.example.chessapp.misc.AnimateUtils;

import javafx.application.*;

import javafx.geometry.Insets;
import javafx.scene.*;

import javafx.scene.control.CheckBox;

import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.*;

import java.util.*;

public class AppStart extends Application {

    private HashMap<String, Node> nodeManager;
    private Stage window;

    @Override
    public void start(Stage stage) {

        final int SCREEN_WIDTH = 800;
        final int SCREEN_HEIGHT = 500;

        window = stage;

        Pane root = new Pane();
        BorderPane outerBorder = new BorderPane();
        Pane boardBacker = new Pane();
        HBox top = new HBox();
        Board board = new Board(SCREEN_HEIGHT/9);
        board.setPiecePane(boardBacker);

        outerBorder.layoutXProperty().bind(stage.widthProperty().divide(2).subtract(board.getSquareSize()*4));
        CheckBox partyMode = new CheckBox("party mode");
        partyMode.setOnAction(e -> {
            AnimateUtils.partyMode(e, board);
        });
        CheckBox debug = new CheckBox("debug mode");
        debug.setOnAction(board::debug);


        top.getChildren().addAll(partyMode, debug);
        boardBacker.getChildren().add(board);

        board.init("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");

        outerBorder.setCenter(boardBacker);
        outerBorder.setTop(top);

        root.getChildren().add(outerBorder);
        Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT);

        stage.setScene(scene);
        stage.setResizable(true);
        stage.setTitle("Chess");
        stage.show();

        stage.setOnCloseRequest(e -> System.exit(0)); // program exit on close regardless of threads active
    }


    public static void main(String[] args) {
        launch(args);
    }
}