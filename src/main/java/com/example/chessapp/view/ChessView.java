package com.example.chessapp.view;

import com.example.chessapp.AppStart;
import com.example.chessapp.board.Board;
import com.example.chessapp.board.BoardConfig;
import javafx.geometry.Insets;
import javafx.scene.control.*;
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
    private MenuItem mobilityHighlighting;
    private MenuItem squaresCheckedHighlighting;
    private MenuItem turnBased;

    final double SCREEN_WIDTH = 700;
    final double SCREEN_HEIGHT = 500;
    public ChessView(Stage stage) {

        super(stage);

        setDebugger();
        setMobilityHighlighting();
        setTurnBased();
        setSquaresCheckedHighlighting();
        setColorPicker();
        setPartyMode();
        setBoard();
        setBackground();

        setLayout();


        // starting position: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR

        board.init("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");


        this.getChildren().add(root);
    }

    private void setBoard() {
        boardBacker = new Pane();
        board = new Board(SCREEN_HEIGHT/8);
        board.setId("board");
        board.setPiecePane(boardBacker);
        boardBacker.getChildren().add(board);
        boardBacker.setMaxWidth(board.getWidth());

    }
    private void setBackground() {
        Image backgroundImg =
                new Image(AppStart.class.getResource("backgrounds/background.png")
                        .toExternalForm());

//        background = new Rectangle(0,0, new ImagePattern(backgroundImg,
//                0,0, getSCREEN_WIDTH(), getSCREEN_HEIGHT(), false));

        background = new Rectangle(0,0);

        background.widthProperty().bind(stage.widthProperty());
        background.heightProperty().bind(stage.heightProperty());
        background.getStyleClass().add("background");
    }
    private void setLayout() {

        root = new Pane();
        root.setMinWidth(SCREEN_WIDTH);
        root.setMinHeight(SCREEN_HEIGHT);

        VBox outerBorder = new VBox();
        outerBorder.setId("outerBorder");

        HBox top = new HBox();
        top.setSpacing(10);
        top.setPadding(new Insets(10));


        outerBorder.layoutXProperty().bind(stage.widthProperty().divide(2).subtract(board.getSquareSize()*4));

        MenuBar bar = new MenuBar();
        Menu options = new Menu("Options");
        options.getItems().addAll(mobilityHighlighting, turnBased, squaresCheckedHighlighting);
        bar.getMenus().add(options);
        top.getChildren().addAll(debug, picker, bar);

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

    private void setTurnBased() {
        turnBased = new MenuItem("turn based");


    }

    private void setMobilityHighlighting () {
        mobilityHighlighting = new MenuItem("show possible moves");

    }

    private void setSquaresCheckedHighlighting() {
        squaresCheckedHighlighting = new MenuItem("show squares checked");
    }

    private void setColorPicker() {
        picker = new ColorPicker();
        picker.setId("picker");

    }

    public MenuItem getMobilityHighlighting() {
        return mobilityHighlighting;
    }

    public MenuItem getSquaresCheckedHighlighting() {
        return squaresCheckedHighlighting;
    }

    public MenuItem getTurnBased() {
        return turnBased;
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
