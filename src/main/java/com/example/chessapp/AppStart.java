package com.example.chessapp;

import com.example.chessapp.board.Board;
import com.example.chessapp.board.SquareTeam;
import com.example.chessapp.misc.AnimateUtils;

import javafx.application.*;

import javafx.geometry.Insets;
import javafx.scene.*;

import javafx.scene.control.*;

import javafx.scene.image.Image;
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
        root.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));

        BorderPane outerBorder = new BorderPane();
        outerBorder.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
        Pane boardBacker = new Pane();
        HBox top = new HBox();
        top.setSpacing(10);
        top.setPadding(new Insets(10));
        Board board = new Board(SCREEN_HEIGHT/9);
        board.setPiecePane(boardBacker);

        outerBorder.layoutXProperty().bind(stage.widthProperty().divide(2).subtract(board.getSquareSize()*4));
        CheckBox partyMode = new CheckBox("party mode");
        partyMode.setTextFill(Color.WHITE);
        partyMode.setOnAction(e -> {
            AnimateUtils.partyMode(e, board);
        });
        CheckBox debug = new CheckBox("debug mode");
        debug.setTextFill(Color.WHITE);
        debug.setOnAction(board::debug);
        partyMode.setDisable(true);
        ColorPicker picker = new ColorPicker();

        picker.setOnAction(e -> {
            board.applyToAllSquares(s -> {
                if (s.getTeam() == SquareTeam.DARK) {
                    s.setColor(picker.getValue().darker());
                } else {
                    s.setColor(picker.getValue().brighter());
                }

            });

            board.refreshAllSquares();

        });

        top.getChildren().addAll(partyMode, debug, picker);
        boardBacker.getChildren().add(board);

        // starting position: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR

        board.init("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");

        outerBorder.setCenter(boardBacker);
        outerBorder.setTop(top);

        root.getChildren().add(outerBorder);
        Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT);
        stage.getIcons().add(new Image(AppStart.class.getResource("application_assets/icons/app_icon.png").toExternalForm()));
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