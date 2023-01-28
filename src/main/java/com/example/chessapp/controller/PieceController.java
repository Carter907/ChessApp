package com.example.chessapp.controller;

import com.example.chessapp.board.Board;
import com.example.chessapp.model.BoardManager;
import com.example.chessapp.model.PieceModel;
import com.example.chessapp.model.PieceType;
import com.example.chessapp.model.MoveType;
import com.example.chessapp.peices.Piece;
import com.example.chessapp.view.PieceView;
import javafx.scene.input.MouseEvent;

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

        pieceAction(manager, board);

    }

    private void pieceAction(BoardManager manager, Board board) {

        // programmatically calculate the rank and file based on the x and y cords of the piece location

        int rank = manager.yToRank(piece.getBoard().getSquareSize(), pieceView.getY() - piece.getBoard().getSquareSize());
        int file = manager.xToFile(piece.getBoard().getSquareSize(), pieceView.getX());

        // get the squares that player wants to check for their move

        Integer[] squares = manager.positionIsLegal(new PieceModel(piece.getType(), piece.getRank(), piece.getFile()), rank, file);

        // if squares is null that means the position was not legal.

        if (squares == null) {
            piece.resetPosition();
            return;
        }

        // check the legality of the move based on pieces on the board and other conditions that game up the game of Chess

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


        // determine what action to take based on what happend

        switch (moveType) {
            case CLEAR -> piece.setSquarePosition(targetSquare);
            case BLOCKED -> piece.resetPosition();
            case CAPTURE -> {
                piece.capture(checkedSquare.getPiece());
                piece.setSquarePosition(targetSquare);
            }
            case EN_PASSANT -> {
                piece.capture(board.findSquare(rank + (piece.getTeam() == PieceType.PIECE_TEAM_WHITE ? 1 : -1), file).getPiece());
                piece.setSquarePosition(targetSquare);
            }
            case CASTLE -> {
            }
        }

        // set turn changes based on which action occured

        updateTurn(moveType, board);
    }

    private void refreshBoard(BoardManager manager, Board board, Integer[] squares) {
        manager.resetConstraints(squares);
        board.refreshAllSquares();
        manager.applyToAllSquares(squares, s -> s.setHighlighted(true));
    }

    private void updateTurn(MoveType type, Board board) {

        switch (type) {
            case CLEAR, CAPTURE, EN_PASSANT -> board.setTurnCount(board.getTurnCount() + 1);
        }
    }


}
