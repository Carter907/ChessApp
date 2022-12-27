package com.example.chessapp.board;

import com.example.chessapp.peices.Piece;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


public class Board extends TilePane {

    public final int SIZE = 8;
    private int squareCount;
    public final Color defaultDark = Color.web("#8e5e3e").darker();
    public final Color defaultLight = Color.web("#eec5aa").brighter();
    private int squareSize;
    private Color darkSquareColor;
    private Color lightSquareColor;
    private Pane piecePane;
    private BooleanProperty debugging;
    private String currentPosition;

    public Board(int squareSize) {
        this.squareSize = squareSize;
        this.darkSquareColor = defaultDark;
        this.lightSquareColor = defaultLight;
        this.debugging = new SimpleBooleanProperty(false);

        this.setAlignment(Pos.CENTER);
        this.setPrefColumns(SIZE);
        this.setPrefRows(SIZE);

        this.setOnMouseClicked(this::highlightHandler);

    }

    private void highlightHandler(MouseEvent e) {
        if (debugging.get()) {
            int rank = BoardUtils.yToRank(squareSize, e.getY());
            int file = BoardUtils.xToFile(squareSize, e.getX());
            Square squareToHighlight = findSquare(rank, file);
            if (squareToHighlight.isHighlighted()) {
                squareToHighlight.setHighlighted(false);

            } else {
                squareToHighlight.setHighlighted(true);
            }
        }

    }

    public Board(int squareSize, Color darkSquareColor, Color lightSquareColor) {
        this.squareSize = squareSize;
        this.darkSquareColor = darkSquareColor;
        this.lightSquareColor = lightSquareColor;


        this.setAlignment(Pos.CENTER);
        this.setPrefColumns(SIZE);
        this.setPrefRows(SIZE);


    }

    private Square newSquare(int rank, int file, Color color) {


        Square square = new Square(rank, file, color);

        this.addSquare(square);

        return square;
    }

    public Square findSquare(int rank, int file) {

        return (Square) this.getChildren().get(((8 - rank) * 8) + file - 1);
    }

    public void addPiece(Piece piece, int rank, int file) {

        Square s = findSquare(rank, file);
        piece.setSquare(s);
        if (!piecePane.getChildren().contains(piece)) {
            piecePane.getChildren().add(piece);
        }
    }

    private void addSquare(Square square) {
        squareCount++;
        if (squareCount > 64)
            throw new RuntimeException("board size full: cannot add another square");

        this.getChildren().add(square);

    }

    public void init(String fen) {

        if (!this.getChildren().isEmpty()) {
            this.getChildren().clear();
            squareCount = 0;
        }

        for (int ra = SIZE; ra > 0; ra--) {
            for (int fi = 1; fi <= SIZE; fi++) {
                if ((ra + fi) % 2 == 0)
                    this.newSquare(ra, fi, darkSquareColor);
                else
                    this.newSquare(ra, fi, lightSquareColor);
            }
        }
        this.currentPosition = fen;
        setPieces(currentPosition);

    }

    public void init() {

        if (!this.getChildren().isEmpty()) {
            this.getChildren().clear();
            squareCount = 0;
        }

        for (int ra = SIZE; ra > 0; ra--) {
            for (int fi = 1; fi <= SIZE; fi++) {
                if ((ra + fi) % 2 == 0)
                    this.newSquare(ra, fi, darkSquareColor);
                else
                    this.newSquare(ra, fi, lightSquareColor);
            }
        }


    }

    public boolean isDebugging() {
        return debugging.get();
    }

    public BooleanProperty debuggingProperty() {
        return debugging;
    }

    public void setDebugging(boolean debugging) {
        this.debugging.set(debugging);
    }

    public Pane getPeicePane() {
        return piecePane;
    }

    public void setPiecePane(Pane peicePane) {
        this.piecePane = peicePane;
    }

    private void setPieces(String fen) {
        char ch;
        Piece piece;
        int rank = 8;
        int file = 1;
        for (int i = 0; i < fen.length(); i++, file++) {
            ch = fen.charAt(i);

            if (ch == '/') {
                rank--;
                file = 0;
                continue;
            } else if (ch == '8')
                continue;

            piece = new Piece(Piece.PieceType.charToPieceType(ch), this, rank, file);
            System.out.println("add a new " + piece);
            addPiece(piece, rank, file);
        }

    }

    public int getSquareSize() {
        return squareSize;
    }

    public void setDarkSquareColor(Color darkSquareColor) {
        this.darkSquareColor = darkSquareColor;
        this.init();
    }

    public void resetColor() {
        this.setLightSquareColor(this.defaultLight);
        this.setDarkSquareColor(this.defaultDark);
    }

    public void setLightSquareColor(Color lightSquareColor) {
        this.lightSquareColor = lightSquareColor;
        this.init();
    }

    public Color getDarkSquareColor() {
        return darkSquareColor;
    }

    public Color getLightSquareColor() {
        return lightSquareColor;
    }


    public void debug(ActionEvent event) {

        if (event.getSource() instanceof CheckBox) {

            CheckBox source = (CheckBox) event.getSource();

            if (source.isSelected()) {
                debugging.set(true);
                this.getChildren().forEach(node -> {

                    if (node instanceof Square) {
                        Square square = (Square) node;
                        square.debug = true;
                        square.paint();
                    }
                });
            } else {
                debugging.set(false);
                resetColor();
                this.getChildren().forEach(node -> {

                    if (node instanceof Square) {
                        Square square = (Square) node;
                        square.debug = false;
                        square.paint();
                    }
                });
            }
        }
    }

    public class Square extends StackPane {
        private boolean debug;
        private int rank;
        private int file;
        private Color color;
        private Piece piece;
        private Rectangle surface;
        private boolean highlighted;

        private Square(int rank, int file, Color color) {
            this.rank = rank;
            this.file = file;
            this.color = color;
            this.piece = null;
            this.debug = false;

            paint();

        }

        private void paint() {
            this.getChildren().clear();
            Rectangle square = new Rectangle(squareSize, squareSize, color);
            this.surface = square;
            if (debug) {
                Label label = new Label("rank=" + rank + "\nfile=" + file);
                label.setTextFill(Color.RED);
                this.getChildren().add(square);
                this.getChildren().add(label);
            } else {
                this.getChildren().add(square);
            }


        }

        public Rectangle getSurface() {
            return surface;
        }

        public int getRank() {
            return rank;
        }

        public int getFile() {
            return file;
        }

        public Color getColor() {
            return this.color;
        }

        public Piece getPiece() {
            return piece;
        }

        public void setPiece(Piece piece) {
            this.piece = piece;
            this.paint();
        }

        public void setHighlighted(boolean b) {

            if (b)
                this.surface.setFill(Color.YELLOW);
            else
                this.surface.setFill(color);
            this.highlighted = b;
        }

        public boolean isHighlighted() {
            return highlighted;
        }
    }
}
