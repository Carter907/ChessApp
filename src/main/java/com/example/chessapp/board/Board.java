package com.example.chessapp.board;

import com.example.chessapp.model.*;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;


public class Board extends TilePane implements Cloneable{

    public final int SIZE = 8;
    private int squareCount;
    public final Color defaultDark = Color.web("#7f9a70").darker().brighter();
    public final Color defaultLight = Color.web("#eec5aa").brighter();
    private double squareSize;
    private Color darkSquareColor;
    private Color lightSquareColor;
    private Pane piecePane;
    private BooleanProperty debugging;
    private String currentPosition;
    private GameState gameState;
    private BoardManager boardManager;
    private int turnCount;

    public Board() {
    }

    public Board(double squareSize) {
        this.gameState = GameState.PLAYING;
        this.squareSize = squareSize;
        this.darkSquareColor = defaultDark;
        this.lightSquareColor = defaultLight;
        this.debugging = new SimpleBooleanProperty(false);
        this.boardManager = new BoardManager(this);
        this.turnCount = 0;
        this.setAlignment(Pos.CENTER);
        this.setPrefColumns(SIZE);
        this.setPrefRows(SIZE);

        this.setOnMouseClicked(this::highlightHandler);

    }
    public Board(double squareSize, GameState state, int turnCount, BoardManager manager, BooleanProperty debugging) {
        this.gameState = state;
        this.squareSize = squareSize;
        this.darkSquareColor = defaultDark;
        this.lightSquareColor = defaultLight;
        this.debugging = debugging;
        this.boardManager = manager;
        this.turnCount = turnCount;
        this.setAlignment(Pos.CENTER);
        this.setPrefColumns(SIZE);
        this.setPrefRows(SIZE);

        this.setOnMouseClicked(this::highlightHandler);
    }


    private void highlightHandler(MouseEvent e) {
        if (debugging.get()) {
            int rank = boardManager.yToRank(squareSize, e.getY() - squareSize);
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

    public GameState getState() {
        return gameState;
    }

    public void setState(GameState gameState) {
        this.gameState = gameState;
    }

    private Square newSquare(int rank, int file, SquareTeam team) {


        Square square = new Square(rank, file, team);

        this.addSquare(square);

        return square;
    }

    public void applyToAllSquares(Consumer<Square> consumer) {

        this.getChildren().stream().filter(e -> e instanceof Square).map(n -> (Square) n).forEach(consumer);

    }

    public Square findSquare(int rank, int file) {

        Square square = (Square) this.getChildren().get(((8 - rank) * 8) + file - 1);


        return square;
    }

    public void refreshAllSquares() {
        this.getChildren().stream().map(n -> (Square) n).forEach(Square::paint);
    }

    public Square findSquare(int index) {
        if (index >= this.getChildren().size() || index < 0) {
            return null;
        }
        return (Square) this.getChildren().get(index);
    }

    public void addPiece(Piece piece, int rank, int file) {

        Square s = findSquare(rank, file);

        piece.moveTo(s);
        if (!piecePane.getChildren().contains(piece.getView())) {
            piecePane.getChildren().add(piece.getView());
        }
    }

    private void addSquare(Square square) {
        squareCount++;
        if (squareCount > 64) throw new RuntimeException("board size full: cannot add another square");

        this.getChildren().add(square);

    }

    public void init(String fen) {

        if (!this.getChildren().isEmpty()) {
            this.getChildren().clear();
            squareCount = 0;
        }

        for (int ra = SIZE; ra > 0; ra--) {
            for (int fi = 1; fi <= SIZE; fi++) {
                if ((ra + fi) % 2 == 0) this.newSquare(ra, fi, SquareTeam.LIGHT);
                else this.newSquare(ra, fi, SquareTeam.DARK);
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
                if ((ra + fi) % 2 == 0) this.newSquare(ra, fi, SquareTeam.DARK);
                else this.newSquare(ra, fi, SquareTeam.LIGHT);
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
                file += Integer.parseInt(ch + "") - 1;
                continue;
            }

            piece = new Piece(PieceType.charToPieceType(ch), this, rank, file);
            System.out.println("add a new " + piece);
            addPiece(piece, rank, file);
        }

    }

    public void setTurnCount(int turnCount) {
        this.turnCount = turnCount;
    }

    public int getTurnCount() {
        return turnCount;
    }

    public int getSquareCount() {
        return squareCount;
    }

    public void setSquareCount(int squareCount) {
        this.squareCount = squareCount;
    }

    public double getSquareSize() {
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

    public Map<MoveType, Square> checkSquares(Piece active, Integer[] squareIndexes, int targetRank, int targetFile) {


        Square square = null;
        for (int index : squareIndexes) {

            // get square to check

            square = findSquare(index);

            // does unchecked square contain a piece?

            if (square.hasPiece()) {
                Piece dormant = square.getPiece();
                if (dormant.getType().oppositeTeamOf(active.getType())) {
                    // piece can capture another piece
                    if (square.equals(findSquare(targetRank, targetFile)) && square.moveTypes.get(MoveType.CAPTURE))
                        return Collections.singletonMap(MoveType.CAPTURE, square);
                    else {

//                        System.out.println("target rank: " + targetRank);
//                        System.out.println("target file: " + targetFile);
//                        System.out.println("dormant file: " + dormant.getRank());
//                        System.out.println("dormant rank: " + dormant.getFile());
//                        System.out.println("dormant: " + dormant);
//                        System.out.println(square.moveTypes);

                        // piece is blocked by another piece
                        return Collections.singletonMap(MoveType.BLOCKED, square);
                    }
                } else
                    // piece is the same team
                    return Collections.singletonMap(MoveType.BLOCKED, square);

                // unchecked square does not have a piece on it

                // is unchecked square an enpassant square?
            } else if (square.moveTypes.get(MoveType.EN_PASSANT) &&
                    square.equals(findSquare(targetRank, targetFile)) &&
                    square.positionTurn + 1 == turnCount && active.isPiece("pawn"))
                return Collections.singletonMap(MoveType.EN_PASSANT, square);

                // is unchecked square a capture but not a clear square?
            else if (square.moveTypes.get(MoveType.CAPTURE) && !square.moveTypes.get(MoveType.CLEAR))
                return Collections.singletonMap(MoveType.BLOCKED, square);

                // is unchecked square a short castle square?
            else if (square.moveTypes.get(MoveType.SHORT_CASTLE))
                return Collections.singletonMap(MoveType.SHORT_CASTLE, square);
                // is unchecked square a long castle square?
            else if (square.moveTypes.get(MoveType.LONG_CASTLE))
                return Collections.singletonMap(MoveType.LONG_CASTLE, square);

        }


        return Collections.singletonMap(MoveType.CLEAR, square);
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "<board squareSize=%f squareCount=%d/>", squareSize, squareCount);
    }

    @Override
    public Board clone() {

//            double squareSize, GameState state, int turnCount, BoardManager manager, BooleanProperty debugging
            Board clone = new Board(squareSize, gameState, turnCount, boardManager, debugging);
            clone.getChildren().setAll(getChildren().stream().map(n -> ((Square)n).clone()).collect(Collectors.toList()));

            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;

    }



    public class Square extends StackPane implements Cloneable {

        private boolean debug;
        private int rank;
        private int file;
        private Color color;
        private Piece piece;
        private Rectangle surface;
        private HashMap<MoveType, Boolean> moveTypes;
        private boolean highlighted;
        private int positionTurn;
        private SquareTeam team;


        private Square(int rank, int file, SquareTeam team) {
            this.rank = rank;
            this.file = file;
            this.team = team;
            this.color = team == SquareTeam.DARK ? darkSquareColor : team == SquareTeam.NULL ? Color.RED : lightSquareColor;
            this.piece = null;
            this.debug = false;
            this.moveTypes = new HashMap<>();
            fillSquareTypes();

            paint();

        }

        public Square(boolean debug, int rank, int file, Color color,
                      Piece piece, Rectangle surface, HashMap<MoveType,
                Boolean> moveTypes, boolean highlighted, int positionTurn,
                      SquareTeam team) {
            this.debug = debug;
            this.rank = rank;
            this.file = file;
            this.color = color;
            this.piece = piece;
            this.surface = surface;
            this.moveTypes = new HashMap<>(moveTypes);
            this.highlighted = highlighted;
            this.positionTurn = positionTurn;
            this.team = team;
        }

        private void fillSquareTypes() {

            for (MoveType type : MoveType.values) {
                moveTypes.put(type, false);
            }
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

        public HashMap<MoveType, Boolean> getMoveTypes() {
            return moveTypes;
        }

        public SquareTeam getTeam() {
            return team;
        }


        public Rectangle getSurface() {
            return surface;
        }

        public int getRank() {
            return rank;
        }

        public int getPositionTurn() {
            return positionTurn;
        }

        public void setPositionTurn(int positionTurn) {
            this.positionTurn = positionTurn;
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

        public void setHighlighted(boolean isHighlighted) {

            if (isHighlighted) {
                int r = Math.min((int)((color.getRed()*255)*1.3), 255);
                int g = Math.min((int)((color.getGreen()*255)*1.3), 255);
                int b = (int)(color.getBlue()*255);
                this.surface.setFill(
                        Color.rgb(r, g, b));
            }
            else
                this.surface.setFill(color);
            this.highlighted = isHighlighted;
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


        @Override
        public String toString() {
            return String.format("<square rank=%d file=%d board=%s/>", rank, file, Board.this);
        }

        public void setRank(int rank) {
            this.rank = rank;
        }

        public void setFile(int file) {
            this.file = file;
        }

        @Override
        public Square clone() {

                Square clone = new Square(debug, rank, file, color,
                        piece, surface, moveTypes, highlighted, positionTurn, team);
                return clone;

        }

        public void restore(Square square) {
            file = square.file;
            rank = square.rank;
            moveTypes = square.moveTypes;
            highlighted = square.highlighted;
            surface = square.surface;
            team = square.team;
            piece = square.piece;
            color = square.color;
        }
    }
}
