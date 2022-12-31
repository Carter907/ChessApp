package com.example.chessapp.board;

import com.example.chessapp.peices.Piece;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;


public class Board extends TilePane {

    public final int SIZE = 8;
    private int squareCount;
    public final Color defaultDark = Color.web("#8e5e3e").darker().brighter();
    public final Color defaultLight = Color.web("#eec5aa").brighter();
    private int squareSize;
    private Color darkSquareColor;
    private Color lightSquareColor;
    private Pane piecePane;
    private BooleanProperty debugging;
    private String currentPosition;
    private BoardManager boardManager;


    public Board() {}
    public Board(int squareSize) {
        this.squareSize = squareSize;
        this.darkSquareColor = defaultDark;
        this.lightSquareColor = defaultLight;
        this.debugging = new SimpleBooleanProperty(false);
        this.boardManager = new BoardManager(this);

        this.setAlignment(Pos.CENTER);
        this.setPrefColumns(SIZE);
        this.setPrefRows(SIZE);

        this.setOnMouseClicked(this::highlightHandler);

    }

    private void highlightHandler(MouseEvent e) {
        if (debugging.get()) {
            int rank = boardManager.yToRank(squareSize, e.getY()-squareSize);
            int file = boardManager.xToFile(squareSize, e.getX());
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

    public Square findSquare(int index) {
        return (Square) this.getChildren().get(index);
    }

    public void addPiece(Piece piece, int rank, int file) {

        Square s = findSquare(rank, file);

        piece.setSquarePosition(s);
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
                    this.newSquare(ra, fi, lightSquareColor);
                else
                    this.newSquare(ra, fi, darkSquareColor);
            }
        }
        this.currentPosition = fen;
        setFenPosition(currentPosition);

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

    public BoardManager getBoardManager() {
        return boardManager;
    }

    public void setBoardManager(BoardManager boardManager) {
        this.boardManager = boardManager;
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

    private void setFenPosition(String fen) {
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
            } else if (Character.isDigit(ch)) {
                file+=Integer.parseInt(ch+"")-1;
                continue;
            }

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
    }

    public void setLightSquareColor(Color lightSquareColor) {

        this.lightSquareColor = lightSquareColor;

    }
    public void resetColor() {

        this.setLightSquareColor(this.defaultLight);
        this.setDarkSquareColor(this.defaultDark);
        // need to update board in order for changes to apply
    }

    public void refreshAllSquares() {
        this.getChildren().stream().map(n -> (Square)n).forEach(Square::paint);
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

    public Map<PositionType, Square> checkSquares(Piece active, Integer[] squareIndexes, int targetRank, int targetFile) {

        Square square;
        for (int index : squareIndexes) {
            square = findSquare(index);
            if (square.hasPiece()) {
                Piece dormant = square.getPiece();
                if (dormant.getType().isOppositeTeam(active.getType())) {

                    // piece can capture another piece

                    if (targetRank == dormant.getRank() && targetFile == dormant.getFile() && square.isCaptureSquare())
                        return Collections.singletonMap(PositionType.CAPTURE, square);
                    else

                        // piece is blocked by another piece

                        return Collections.singletonMap(PositionType.BLOCKED, square);
                } else

                    // piece is the same team

                    return Collections.singletonMap(PositionType.BLOCKED, new Square(99, 99, Color.RED));
            }
            // piece is capturable only if it is not a clear square

            else if (square.isCaptureSquare() && !square.isClearSquare()) {
                return Collections.singletonMap(PositionType.BLOCKED, null);
            }
        }


        return Collections.singletonMap(PositionType.CLEAR, null);
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "<board squareSize=%d squareCount=%d/>", squareSize, squareCount);
    }

    public class Square extends StackPane {
        private boolean debug;
        private int rank;
        private int file;
        private Color color;
        private Piece piece;
        private Rectangle surface;
        private boolean highlighted;
        private boolean captureSquare;
        private boolean clearSquare;

        private Square(int rank, int file, Color color) {
            this.rank = rank;
            this.file = file;
            this.color = color;
            this.piece = null;
            this.debug = false;
            this.captureSquare = false;
            paint();

        }

        public void refresh() {
            paint();
        }

        private void paint() {
            this.getChildren().clear();
            Rectangle square = new Rectangle(squareSize, squareSize, color);
            this.surface = square;
            if (debug) {
                Label label = new Label("rank=" + rank + "\nfile=" + file);
                //label.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
                label.setTextFill(Color.LIME);
                this.getChildren().add(square);
                this.getChildren().add(label);
            } else {
                this.getChildren().add(square);
            }


        }

        public boolean isClearSquare() {
            return clearSquare;
        }

        public void setClearSquare(boolean clearSquare) {
            this.clearSquare = clearSquare;
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
        }

        public void setHighlighted(boolean b) {

            if (b)
                this.surface.setFill(Color.YELLOW);
            else
                this.surface.setFill(color);
            this.highlighted = b;
        }

        public Integer getIndex() {

            return boardManager.posToIndex(this.rank, this.file);
        }

        public boolean isHighlighted() {
            return highlighted;
        }

        public boolean hasPiece() {
            return this.piece != null;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public void setCaptureSquare(boolean captureSquare) {
            this.captureSquare = captureSquare;
        }
        public boolean isCaptureSquare() {
            return this.captureSquare;
        }
        @Override
        public String toString() {
            return String.format("<square rank=%d file=%d board=%s/>", rank, file, Board.this);
        }



    }
}
