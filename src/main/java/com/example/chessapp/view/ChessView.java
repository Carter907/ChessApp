package com.example.chessapp.view;

import com.example.chessapp.AppStart;
import com.example.chessapp.board.Board;
import com.example.chessapp.misc.AnimateUtils;
import com.example.chessapp.model.SquareTeam;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class ChessView extends View {

    private Board board;
    private Pane boardBacker;
    private ColorPicker picker;
    private CheckBox debug;
    private Rectangle background;
    private CheckBox partyMode;

    final double SCREEN_WIDTH = 700;
    final double SCREEN_HEIGHT = 500;
    public ChessView(Stage stage) {

        super(stage);

        setDebugger();
        setColorPicker();
        setPartyMode();
        setBoard();
        setBackground();

        setLayout();


        // starting position: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR

        board.init("8/8/8/8/4Q3/8/8/8");


        this.getChildren().add(root);
    }

    private void setBoard() {
        boardBacker = new Pane();
        board = new Board(SCREEN_HEIGHT/8);
        board.setPiecePane(boardBacker);
        boardBacker.getChildren().add(board);
        boardBacker.setMaxWidth(board.getWidth());

    }
    private void setBackground() {
        Image backgroundImg =
                new Image(AppStart.class.getResource("application_assets/backgrounds/background.png")
                        .toExternalForm());

        background = new Rectangle(0,0, new ImagePattern(backgroundImg,
                0,0, getSCREEN_WIDTH(), getSCREEN_HEIGHT(), false));

        background.widthProperty().bind(stage.widthProperty());
        background.heightProperty().bind(stage.heightProperty());
    }
    private void setLayout() {

        root = new Pane();
        root.setMinWidth(SCREEN_WIDTH);
        root.setMinHeight(SCREEN_HEIGHT);

        VBox outerBorder = new VBox();
        outerBorder.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));

        HBox top = new HBox();
        top.setSpacing(10);
        top.setPadding(new Insets(10));


        outerBorder.layoutXProperty().bind(stage.widthProperty().divide(2).subtract(board.getSquareSize()*4));


        top.getChildren().addAll(debug, picker);

        outerBorder.getChildren().addAll(top, boardBacker);

        root.getChildren().addAll(background, outerBorder);
    }

    private void setPartyMode() {
        partyMode = new CheckBox("party mode");
        partyMode.setTextFill(Color.WHITE);

    }

    private void setDebugger() {
        debug = new CheckBox("debug mode");
        debug.setTextFill(Color.WHITE);



    }


    private void setColorPicker() {
        picker = new ColorPicker();

    }

    public Pane getBoardBacker() {
        return boardBacker;
    }

    public Board getBoard() {
        return board;
    }

    public ColorPicker getPicker() {
        return picker;
    }

    public CheckBox getDebug() {
        return debug;
    }

    public CheckBox getPartyMode() {
        return partyMode;
    }

    public double getSCREEN_WIDTH() {
        return SCREEN_WIDTH;
    }

    public double getSCREEN_HEIGHT() {
        return SCREEN_HEIGHT;
    }
}
