package com.example.chessapp.peices;

import com.example.chessapp.AppStart;
import com.example.chessapp.board.Board;
import com.example.chessapp.board.BoardManager;
import com.example.chessapp.board.PositionType;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.util.Arrays;
import java.util.Map;


public class Piece extends ImageView {
    private final PieceType type;
    private Board board;
    private BoardManager boardManager;
    private Board.Square square;
    private int rank;
    private int file;

    public Piece(PieceType type) {
        this.rank = rank;
        this.file = file;
        this.type = type;
        this.setImage(type.getImage());
        this.setViewport(new Rectangle2D(0, 0, this.getImage().getWidth(), this.getImage().getHeight()));
        this.boardManager = new BoardManager(new Board());
        this.setOnMouseDragged(PieceMouseEvents.MOUSE_DRAGGED.eventHandler);
    }

    public Piece(PieceType type, Board board, int rank, int file) {
        this.type = type;
        this.board = board;
        this.rank = rank;
        this.file = file;
        this.boardManager = board.getBoardManager();

        this.setImage(type.getImage());
        this.setViewport(new Rectangle2D(0, 0, this.getImage().getWidth(), this.getImage().getHeight()));
        this.setPreserveRatio(true);
        this.setFitHeight(board.getSquareSize() - 10);
        double aspectRatio = this.getImage().getWidth() / this.getImage().getHeight();
        this.setTranslateX((-getFitHeight() * aspectRatio / 2));
        this.setTranslateY((-getFitHeight() / aspectRatio / 2));
        if (type == PieceType.PAWN_B || type == PieceType.PAWN_W)
            this.setTranslateY(this.getTranslateY() + 5);


        this.setOnMouseDragged(PieceMouseEvents.MOUSE_DRAGGED.eventHandler);
        this.setOnMouseReleased(PieceMouseEvents.MOUSE_RELEASED.eventHandler);

    }

    public Board.Square getSquare() {
        return square;
    }

    public Board getBoard() {
        return board;
    }

    public void setSquarePosition(Board.Square square) {
        if (square == null) {
            this.square = null;
            return;
        }
        double x = boardManager.fileToX(board.getSquareSize(), square.getFile()) + (board.getSquareSize() / 2);
        double y = boardManager.rankToY(board.getSquareSize(), square.getRank()) + (board.getSquareSize() / 2);

        this.boardManager.bindPieceAndSquare(this, square);
        this.rank = square.getRank();
        this.file = square.getFile();

        this.setX(x);
        this.setY(y);

//        System.out.println(this);

    }

    public boolean hasSquare() {
        return this.square != null;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public PieceType getType() {
        return type;
    }

    public void setSquare(Board.Square square) {
        this.square = square;
    }


    private enum PieceMouseEvents {
        MOUSE_RELEASED(event -> {

            Piece piece = (Piece) event.getSource();


            int rank = piece.boardManager.yToRank(piece.getBoard().getSquareSize(), piece.getY() - piece.getBoard().getSquareSize());
            int file = piece.boardManager.xToFile(piece.getBoard().getSquareSize(), piece.getX());

            Integer[] squares = piece.boardManager.positionIsLegal(piece, rank, file);

            if (squares != null) {
                Map<PositionType, Board.Square> values = piece.board.checkSquares(piece, squares, rank, file);
                piece.boardManager.resetConstraints(squares);
                piece.board.refreshAllSquares();
                piece.boardManager.applyToAllSquares(squares, s -> s.setHighlighted(true));

                PositionType posType = values.entrySet().iterator().next().getKey();
                Board.Square square = values.entrySet().iterator().next().getValue();
                System.out.println("squares:" + Arrays.toString(squares));
                System.out.println(values);



                switch (posType) {
                    case CLEAR -> piece.setSquarePosition(piece.board.findSquare(rank, file));
                    case BLOCKED -> piece.resetPosition();
                    case CAPTURE -> {
                        piece.capture(square.getPiece());
                        piece.setSquarePosition(piece.board.findSquare(rank, file));
                    }

                    case CASTLE -> {

                    }
                }


            } else {
                piece.resetPosition();
            }


        }),
        MOUSE_DRAGGED(event -> {

            Piece piece = (Piece) event.getSource();

            piece.setX(event.getX() + piece.getTranslateX());
            piece.setY(event.getY() + piece.getTranslateY());

        });


        private final EventHandler<MouseEvent> eventHandler;

        PieceMouseEvents(EventHandler<MouseEvent> eventHandler) {
            this.eventHandler = eventHandler;
        }
    }

    private void capture(Piece piece) {
        System.out.println("captured: there is a " + piece.getType()
                + " on the " + piece.getRank() + " rank and " + piece.getFile() + " file");
        piece.getSquare().setPiece(null);
        piece.getBoard().getPeicePane().getChildren().remove(piece);

    }

    public void resetPosition() {
        this.board.addPiece(this, rank, file);
    }

    public void setFile(int file) {
        this.file = file;
        resetPosition();
    }

    public void setPosition(int rank, int file) {
        this.rank = rank;
        this.file = file;
        this.resetPosition();
    }

    public int getFile() {
        return this.file;
    }

    public void setRank(int rank) {
        this.rank = rank;
        resetPosition();

    }

    public int getRank() {
        return this.rank;
    }

    public enum PieceType {

        PAWN_W(new Image(AppStart.class.getResource("sets/set1/white_pawn.png").toExternalForm())),
        NIGHT_W(new Image(AppStart.class.getResource("sets/set1/white_knight.png").toExternalForm())),
        BISHOP_W(new Image(AppStart.class.getResource("sets/set1/white_bishop.png").toExternalForm())),
        ROOK_W(new Image(AppStart.class.getResource("sets/set1/white_rook.png").toExternalForm())),
        QUEEN_W(new Image(AppStart.class.getResource("sets/set1/white_queen.png").toExternalForm())),
        KING_W(new Image(AppStart.class.getResource("sets/set1/white_king.png").toExternalForm())),
        PAWN_B(new Image(AppStart.class.getResource("sets/set1/black_pawn.png").toExternalForm())),
        NIGHT_B(new Image(AppStart.class.getResource("sets/set1/black_knight.png").toExternalForm())),
        BISHOP_B(new Image(AppStart.class.getResource("sets/set1/black_bishop.png").toExternalForm())),
        ROOK_B(new Image(AppStart.class.getResource("sets/set1/black_rook.png").toExternalForm())),
        QUEEN_B(new Image(AppStart.class.getResource("sets/set1/black_queen.png").toExternalForm())),
        KING_B(new Image(AppStart.class.getResource("sets/set1/black_king.png").toExternalForm()));

        private final Image sprite;
        private final String team;
        private static PieceType[] pieceTypes = PieceType.values();

        PieceType(Image sprite) {
            this.sprite = sprite;
            this.team = ""+name().charAt(name().length()-1);
        }

        public static PieceType charToPieceType(char c) {
            for (PieceType piece : pieceTypes) {
                String name = piece.name();
                if (Character.isUpperCase(c)
                        && c == name.charAt(0)
                        && name.charAt(name.length() - 1) == 'B') {
                    return piece;
                } else if (Character.isLowerCase(c)
                        && c == Character.toLowerCase(name.charAt(0))
                        && name.charAt(name.length() - 1) == 'W') {
                    return piece;
                }
            }
            return null;
        }
        public Image getImage() {
            return sprite;
        }

        public boolean isOppositeTeam(PieceType type) {
            System.out.println(!type.team.equals(this.team));
            return !type.team.equals(this.team);
        }
    }

    @Override
    public String toString() {
        return String.format("<piece square=%s rank=%d file=%d board=%s/>", square, rank, file, board);
    }

}
