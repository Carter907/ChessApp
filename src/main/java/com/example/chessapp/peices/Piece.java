package com.example.chessapp.peices;

import com.example.chessapp.AppStart;
import com.example.chessapp.board.Board;
import com.example.chessapp.board.BoardUtils;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class Piece extends ImageView {
    private final PieceType type;
    private Board board;
    private Board.Square square;
    private int rank;
    private int file;

    public Piece(PieceType type) {
        this.rank = rank;
        this.file = file;
        this.type = type;
        this.setImage(type.getImage());
        this.setViewport(new Rectangle2D(0, 0, this.getImage().getWidth(), this.getImage().getHeight()));

        this.setOnMouseDragged(PieceMouseEvents.MOUSE_DRAGGED.eventHandler);
    }

    public Piece(PieceType type, Board board, int rank, int file) {
        this.type = type;
        this.board = board;
        this.rank = rank;
        this.file = file;

        this.setImage(type.getImage());
        this.setViewport(new Rectangle2D(0, 0, this.getImage().getWidth(), this.getImage().getHeight()));
        this.setPreserveRatio(true);
        this.setFitHeight(board.getSquareSize() - 10);
        double aspectRatio = this.getImage().getWidth() / this.getImage().getHeight();
        this.setTranslateX((-getFitHeight() * aspectRatio / 2));
        this.setTranslateY((-getFitHeight() / aspectRatio / 2));
        if (type == PieceType.PAWN_B || type == PieceType.PAWN_W)
            this.setTranslateY(this.getTranslateY()+5);


        this.setOnMouseDragged(PieceMouseEvents.MOUSE_DRAGGED.eventHandler);
        this.setOnMouseReleased(PieceMouseEvents.MOUSE_RELEASED.eventHandler);

    }

    public Board.Square getSquare() {
        return square;
    }

    public Board getBoard() {
        return board;
    }

    public void setSquare(Board.Square square) {
        if (square == null) {
            this.square = null;
            return;
        }
        double x = BoardUtils.fileToX(board.getSquareSize(), square.getFile()) + (board.getSquareSize() / 2);
        double y = BoardUtils.rankToY(board.getSquareSize(), square.getRank()) + (board.getSquareSize() / 2);

        System.out.println("square was sent to the " + square.getFile() + " file");
        System.out.println("square was sent to the " + square.getRank() + " rank");

        this.setX(x);
        this.setY(y);
        this.square = square;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public PieceType getType() {
        return type;
    }

    private enum PieceMouseEvents {
        MOUSE_RELEASED(event -> {

            Piece piece = (Piece)event.getSource();


            int rank = BoardUtils.yToRank(piece.getBoard().getSquareSize(), piece.getY()-piece.getBoard().getSquareSize());
            int file = BoardUtils.xToFile(piece.getBoard().getSquareSize(), piece.getX());
            if (BoardUtils.positionIsLegal(piece, rank, file)) {
                piece.setPosition(rank, file);

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
        private static PieceType[] pieceTypes = PieceType.values();

        PieceType(Image sprite) {

            this.sprite = sprite;
        }

        public static PieceType charToPieceType(char c) {
            for (PieceType piece : pieceTypes) {
                String name = piece.name();
                if (Character.isUpperCase(c)
                        && c == name.charAt(0)
                        && name.charAt(name.length()-1) == 'B') {
                    return piece;
                } else if (Character.isLowerCase(c)
                        && c == Character.toLowerCase(name.charAt(0))
                        && name.charAt(name.length()-1) == 'W') {
                    return piece;
                }
            }
            return null;
        }

        public Image getImage() {
            return sprite;
        }
    }
    @Override
    public String toString() {
        return this.getType().name() + " on board " + board;
    }

}
