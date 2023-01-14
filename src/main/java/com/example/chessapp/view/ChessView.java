package com.example.chessapp.view;

import com.example.chessapp.board.Board;
import com.example.chessapp.misc.AnimateUtils;
import com.example.chessapp.model.SquareTeam;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ChessView extends View {

    private Board board;

    public ChessView(Stage stage) {


        final int SCREEN_WIDTH = 500;
        final int SCREEN_HEIGHT = 500;
        root = new Pane();
        root.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
        root.setMinWidth(SCREEN_WIDTH);
        root.setMinHeight(SCREEN_HEIGHT);
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
                    s.setColor(picker.getValue().brighter().brighter());
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
        this.getChildren().add(root);
    }
}
