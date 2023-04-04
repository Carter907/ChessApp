package com.example.chessapp.controller;

import com.example.chessapp.ChessMoveException;
import com.example.chessapp.board.Board;
import com.example.chessapp.board.BoardConfig;
import com.example.chessapp.model.*;
import com.example.chessapp.peices.Piece;
import com.example.chessapp.view.PieceView;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;

import java.util.*;
import java.util.stream.Stream;

public class PieceController {

    private final PieceView pieceView;
    private final Piece piece;
    private BoardManager manager;
    private Board board;

    public PieceController(PieceView pieceView, Piece piece) {

        this.pieceView = pieceView;
        this.piece = piece;

        pieceView.setOnMousePressed(this::pressedPiece);
        pieceView.setOnMouseDragged(this::dragPiece);
        pieceView.setOnMouseReleased(this::movePiece);
    }

    private void pressedPiece(MouseEvent mouseEvent) {

        pieceView.toFront();
    }


    private void dragPiece(MouseEvent event) {

        pieceView.setX(event.getX() + pieceView.getTranslateX());
        pieceView.setY(event.getY() + pieceView.getTranslateY());
    }

    private void movePiece(MouseEvent event) {

        manager = piece.getBoard().getBoardManager();
        board = piece.getBoard();

        try {
            pieceAction();
        } catch (ChessMoveException e) {
            new Alert(
                    Alert.AlertType.ERROR,
                    e.getMessage()
            );
        }
    }

    private boolean pieceAction() throws ChessMoveException {

        // checking for turn and other conditions are met to see if the position is legal. If not, the piece
        // is automatically reset and the method returns.
        System.out.println(board.getTurnCount());

        // view piece moves:
        if (BoardConfig.INSTANCE.hasMobilityHighlighting())
            updateMobilityHighlighting();


        if (BoardConfig.INSTANCE.isTurnBased()) {
            if ((board.getTurnCount() % 2 == 1 && piece.getTeam() == PieceType.PIECE_TEAM_WHITE) ||
                    (board.getTurnCount() % 2 == 0 && piece.getTeam() == PieceType.PIECE_TEAM_BLACK)) {
                piece.resetPosition();
                return false;
            }
        }


        // programmatically calculate the rank and file based on the x and y cords of the piece location

        int rank = manager.yToRank(piece.getBoard().getSquareSize(), pieceView.getY() - piece.getBoard().getSquareSize());
        int file = manager.xToFile(piece.getBoard().getSquareSize(), pieceView.getX());

        // get the squares that player wants to check for their moveTo


        if (completeMoveAction(rank, file) && BoardConfig.INSTANCE.hasMobilityHighlighting())
            updateMobilityHighlighting();

        return true;
    }

    private boolean completeMoveAction(int rank, int file) throws ChessMoveException {


        Integer[] squares = manager.positionIsLegal(new PieceModel(piece.getType(), piece.getRank(), piece.getFile()), rank, file);

        if (squares == null || Arrays.binarySearch(squares, -1) >= 0) {
            piece.resetPosition();
            return false;
        }
        // check the legality of the moveTo based on pieces on the board and other conditions that game up the game of Chess

        Map<MoveType, Board.Square> values = board.checkSquares(piece, squares, rank, file);

        // clean up. reset the constraints of the squares so that there aren't sideffects for subsquent moves

        refreshBoard(squares);


        /* get the move type for the move. What kind of move was it
         * a capture, a regular move with no peices in the way is called 'CLEAR',
         * while a blocked move is called BLOCK.
         */

        MoveType moveType = values.entrySet().iterator().next().getKey();
        Board.Square checkedSquare = values.entrySet().iterator().next().getValue();

        Board.Square targetSquare = board.findSquare(rank, file);

//        System.out.println(values);
        System.out.println(moveType);

        // check if the move reveals the check of the friendly king.


        // determine what action to take based on what happens

        switch (moveType) {
            case CLEAR -> piece.moveTo(targetSquare);
            case BLOCKED -> piece.resetPosition();
            case CAPTURE -> {
                Piece enemy = checkedSquare.getPiece();

                if (enemy.isPiece("king"))
                    captureKing(enemy);

                piece.capture(enemy);
                piece.moveTo(targetSquare);


            }
            case EN_PASSANT -> {
                piece.capture(board.findSquare(rank + (piece.getTeam() == PieceType.PIECE_TEAM_WHITE ? 1 : -1), file).getPiece());
                piece.moveTo(targetSquare);
            }
            case SHORT_CASTLE -> {
                if (canCastleShort(targetSquare)) {
                    piece.moveTo(targetSquare);
                    Piece rook = getCastleRook(targetSquare, MoveType.SHORT_CASTLE);

                    rook.moveTo(board.findSquare(rank, file - 1));

                } else
                    piece.resetPosition();

            }
            case LONG_CASTLE -> {
                if (canCastleLong(targetSquare)) {
                    piece.moveTo(targetSquare);
                    Piece rook = getCastleRook(targetSquare, MoveType.LONG_CASTLE);

                    rook.moveTo(board.findSquare(rank, file + 1));

                } else
                    piece.resetPosition();

            }
        }

        // set turn changes based on which action occured

        updateTurn(moveType);
        // go through each piece and check movable squares


        return true;
    }

    private ArrayList<Integer> getAllPossibleMoves() {
        ArrayList<Integer> squares = new ArrayList<>();
        PieceModel pieceModel = new PieceModel(piece.getType(), piece.getRank(), piece.getFile());
        int rank = pieceModel.getRank(), file = pieceModel.getFile();

        manager.beginPeer();
        board = manager.getBoard();


        Integer[] offsets = pieceModel.getType().getMoveOffsets();

        for (int i = 0; i < offsets.length - 1; i += 2) {
            try {
                if (!isOffsetLegal(offsets[i], offsets[i + 1], rank, file))
                    continue;

                if (piece.getType().isStepPiece())
                    squares.addAll(List.of(
                            getPossibleSquares(pieceModel, rank += offsets[i], file += offsets[i + 1])
                    ));
                else
                    while (rank <= 8 && rank > 0 && file <= 8 && file > 0) {
                        if (!isOffsetLegal(offsets[i], offsets[i + 1], rank, file))
                            break;
                        squares.addAll(List.of(
                                getPossibleSquares(pieceModel, rank += offsets[i], file += offsets[i + 1])
                        ));

                    }
                rank = pieceModel.getRank();
                file = pieceModel.getFile();
            } catch (NullPointerException e) {
                System.err.println(e.getMessage());
                System.err.println("rank = " + rank);
                System.err.println("file = " + file);
                System.err.println("offsetRank = " + offsets[i]);
                System.err.println("offsetFile = " + offsets[i + 1]);
                System.err.println();
            }
        }

        manager.endPeer();
        board = manager.getBoard();


        return squares;
    }

    private boolean isOffsetLegal(int offsetRank, int offsetFile, int rank, int file) {
        if (offsetRank + rank > 8 || offsetRank + rank <= 0)
            return false;
        else if (offsetFile + file > 8 || offsetFile + file <= 0)
            return false;
        return true;
    }

    private Integer[] getPossibleSquares(PieceModel pieceModel, int rank, int file) {

        Integer[] possibleMoves = manager.positionIsLegal(pieceModel, rank, file);


        if (possibleMoves == null || Arrays.binarySearch(possibleMoves, -1) >= 0)
            return new Integer[0];
        Integer[] finalPossibleMoves = possibleMoves;

        possibleMoves = Stream.of(possibleMoves)
                .filter(n -> {
                    int targetRank = board.findSquare(n).getRank();
                    int targetFile = board.findSquare(n).getFile();
                    MoveType type = board.checkSquares(piece, finalPossibleMoves,
                           targetRank , targetFile
                    ).entrySet().iterator().next().getKey();

                    System.out.println(type + " to " + targetRank + "rank and " + targetFile + " file");
                    System.out.println("moveTypes: " + board.findSquare(n).getMoveTypes());


                    return type != MoveType.BLOCKED;
                })
                .toArray(Integer[]::new);

        pieceChecksKing(possibleMoves);


        return possibleMoves;
    }

    private void pieceChecksKing(Integer[] possibleMoves) {
        for (int squareIndex : possibleMoves) {
            if (manager.inCheck(manager.posToIndex(piece.getRank(), piece.getFile()), squareIndex)) {
                new Alert(
                        Alert.AlertType.INFORMATION,
                        "King is in Check!"
                ).show();
                break;

            }

        }
    }

    private void pieceRevealsCheck() {

    }

    private void updateMobilityHighlighting() {

        board.applyToAllSquares(s -> {
            if (s.isHighlighted()) {
                s.setHighlighted(false);
            }
        });

        ArrayList<Integer> allPossibleMoves = getAllPossibleMoves();


        for (Integer index : allPossibleMoves) {
            Board.Square square = board.findSquare(index);
            square.setHighlighted(true);

        }

    }

    private void captureKing(Piece enemy) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if (enemy.getTeam() == PieceType.PIECE_TEAM_WHITE)
            alert.setContentText("Black has captured the white king!");
        else
            alert.setContentText("White has captured the black king!");

        alert.show();
    }

    private boolean canCastleLong(Board.Square targetSquare) throws ChessMoveException {

        // TODO: add checks for castling long
        try {
            Piece piece = board.findSquare(targetSquare.getRank(), targetSquare.getFile() - 2).getPiece();
            System.out.println("checked if this piece was a rook:" + piece);
            if (piece.isPiece("rook")) {
                return true;
            }
            return false;
        } catch (NullPointerException e) {
            throw new ChessMoveException("wrong piece or piece is null", e);
        }

    }

    private boolean canCastleShort(Board.Square target) {

        //TODO: add checks for castling short

        Piece piece = board.findSquare(target.getRank(), target.getFile() + 1).getPiece();
        System.out.println("checked if this piece was a rook:" + piece);
        if (piece.isPiece("rook")) {
            return true;
        }
        return false;
    }

    private Piece getCastleRook(Board.Square targetSquare, MoveType type) {

        Piece piece = null;
        if (type == MoveType.SHORT_CASTLE)
            piece = board.findSquare(targetSquare.getRank(), targetSquare.getFile() + 1).getPiece();
        else if (type == MoveType.LONG_CASTLE)
            piece = board.findSquare(targetSquare.getRank(), targetSquare.getFile() - 2).getPiece();
        if (piece.isPiece("rook"))
            return piece;
        else
            throw new IllegalStateException("piece is not a rook or pieces have moved");

    }

    private void refreshBoard(Integer[] squares) {
        manager.resetConstraints(squares);
        board.refreshAllSquares();
        if (BoardConfig.INSTANCE.hasSquaresCheckedHighlighting())
            manager.applyToAllSquares(squares, s -> s.setHighlighted(true));
    }

    private void updateTurn(MoveType type) {

        if (type == MoveType.BLOCKED)
            return;

        board.setTurnCount(board.getTurnCount() + 1);

    }


}
