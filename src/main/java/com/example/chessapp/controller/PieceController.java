package com.example.chessapp.controller;

import com.example.chessapp.ChessMoveException;
import com.example.chessapp.board.Board;
import com.example.chessapp.board.BoardConfig;
import com.example.chessapp.model.*;
import com.example.chessapp.peices.Piece;
import com.example.chessapp.view.PieceView;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PieceController {

    private final PieceView pieceView;
    private final Piece piece;

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

        final BoardManager manager = piece.getBoard().getBoardManager();
        final Board board = piece.getBoard();
        try {
            pieceAction(manager, board);
        } catch (ChessMoveException e) {
            new Alert(
                    Alert.AlertType.ERROR,
                    e.getMessage()
            );
        }
    }

    private void pieceAction(BoardManager manager, Board board) throws ChessMoveException {

        // checking for turn and other conditions are met to see if the position is legal. If not, the piece
        // is automatically reset and the method returns.
        System.out.println(board.getTurnCount());


        if (BoardConfig.IS_TURN_BASED) {
            if ((board.getTurnCount() % 2 == 1 && piece.getTeam() == PieceType.PIECE_TEAM_WHITE) ||
                    (board.getTurnCount() % 2 == 0 && piece.getTeam() == PieceType.PIECE_TEAM_BLACK)) {
                piece.resetPosition();
                return;
            }
        }

        // programmatically calculate the rank and file based on the x and y cords of the piece location

        int rank = manager.yToRank(piece.getBoard().getSquareSize(), pieceView.getY() - piece.getBoard().getSquareSize());
        int file = manager.xToFile(piece.getBoard().getSquareSize(), pieceView.getX());

        // get the squares that player wants to check for their moveTo

        Integer[] squares = manager.positionIsLegal(new PieceModel(piece.getType(), piece.getRank(), piece.getFile()), rank, file);

        if (squares == null || Arrays.binarySearch(squares, -1) >= 0) {
            piece.resetPosition();
            return;
        }
        // check the legality of the moveTo based on pieces on the board and other conditions that game up the game of Chess

        Map<MoveType, Board.Square> values = board.checkSquares(piece, squares, rank, file);

        // clean up. reset the constraints of the squares so that there aren't sideffects for subsquent moves

        refreshBoard(manager, board, squares);


        /* get the move type for the move. What kind of move was it
         * a capture, a regular move with no peices in the way is called 'CLEAR',
         * while a blocked move is called BLOCK.
         */

        MoveType moveType = values.entrySet().iterator().next().getKey();
        Board.Square checkedSquare = values.entrySet().iterator().next().getValue();

        Board.Square targetSquare = board.findSquare(rank, file);

        System.out.println(values);


        // determine what action to take based on what happend

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
                if (canCastleShort(board, targetSquare)) {
                    piece.moveTo(targetSquare);
                    Piece rook = getCastleRook(board, targetSquare, MoveType.SHORT_CASTLE);

                    rook.moveTo(board.findSquare(rank, file - 1));

                } else
                    piece.resetPosition();

            }
            case LONG_CASTLE -> {
                if (canCastleLong(board, targetSquare)) {
                    piece.moveTo(targetSquare);
                    Piece rook = getCastleRook(board, targetSquare, MoveType.LONG_CASTLE);

                    rook.moveTo(board.findSquare(rank, file + 1));

                } else
                    piece.resetPosition();

            }
        }

        // set turn changes based on which action occured

        updateTurn(moveType, board);

        // go through each piece and check movable squares
        if (BoardConfig.HAS_MOBILITY_HIGHLIGHTING)
            updateMovabilityHighlighting(board, manager);

    }

    private void updateMovabilityHighlighting(Board board, BoardManager manager) {
        ArrayList<Integer> squares = new ArrayList<>();
        for (int rank = 1; rank <= 8; rank++) {
            for (int file = 1; file <= 8; file++) {


                Integer[] squareInts = manager.positionIsLegal(new PieceModel(piece.getType(), piece.getRank(), piece.getFile()), rank, file);
                if (squareInts == null || Arrays.binarySearch(squareInts, -1) >= 0)
                    continue;
                manager.resetConstraints(squareInts);

                squares.addAll(List.of(squareInts));
            }
        }


        for (Integer index : squares) {
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

    private boolean canCastleLong(Board board, Board.Square targetSquare) throws ChessMoveException{

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

    private boolean canCastleShort(Board board, Board.Square target) {

        //TODO: add checks for castling short

        Piece piece = board.findSquare(target.getRank(), target.getFile() + 1).getPiece();
        System.out.println("checked if this piece was a rook:" + piece);
        if (piece.isPiece("rook")) {
            return true;
        }
        return false;
    }

    private Piece getCastleRook(Board board, Board.Square targetSquare, MoveType type) {

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

    private void refreshBoard(BoardManager manager, Board board, Integer[] squares) {
        manager.resetConstraints(squares);
        board.refreshAllSquares();
        if (BoardConfig.HAS_MOVE_HIGHLIGHTING)
            manager.applyToAllSquares(squares, s -> s.setHighlighted(true));
    }

    private void updateTurn(MoveType type, Board board) {

        if (type == MoveType.BLOCKED)
            return;

        board.setTurnCount(board.getTurnCount() + 1);

    }


}
