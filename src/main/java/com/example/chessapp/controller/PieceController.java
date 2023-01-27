package com.example.chessapp.controller;

import com.example.chessapp.board.Board;
import com.example.chessapp.model.BoardManager;
import com.example.chessapp.model.PieceModel;
import com.example.chessapp.model.PieceType;
import com.example.chessapp.model.PositionType;
import com.example.chessapp.peices.Piece;
import com.example.chessapp.view.PieceView;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

import java.util.Arrays;
import java.util.Map;

public class PieceController {

    private PieceView pieceView;

    public PieceController(PieceView pieceView) {


        pieceView.setOnMouseDragged(this::mouseReleased);
        pieceView.setOnMouseReleased(this::mouseDragged);
    }

    private void mouseDragged(MouseEvent event) {


        Piece piece = (Piece) event.getSource();

        piece.setX(event.getX() + piece.getTranslateX());
        piece.setY(event.getY() + piece.getTranslateY());
    }

    private void mouseReleased(MouseEvent event) {
        Piece piece = (Piece) event.getSource();
        final BoardManager MANAGER = piece.getBoard().getBoardManager();
        final Board BOARD = piece.getBoard();

        int rank = MANAGER.yToRank(piece.getBoard().getSquareSize(), piece.getY() - piece.getBoard().getSquareSize());
        int file = MANAGER.xToFile(piece.getBoard().getSquareSize(), piece.getX());

        Integer[] squares = MANAGER.positionIsLegal(new PieceModel(piece.getType(), piece.getRank(), piece.getFile()), rank, file);

        if (squares != null) {
            Map<PositionType, Board.Square> values = BOARD.checkSquares(piece, squares, rank, file);
            MANAGER.resetConstraints(squares);
            BOARD.refreshAllSquares();
            MANAGER.applyToAllSquares(squares, s -> s.setHighlighted(true));

            PositionType posType = values.entrySet().iterator().next().getKey();
            Board.Square square = values.entrySet().iterator().next().getValue();



            switch (posType) {
                case CLEAR -> piece.setSquarePosition(BOARD.findSquare(rank, file));
                case BLOCKED -> piece.resetPosition();
                case CAPTURE -> {
                    piece.capture(square.getPiece());
                    piece.setSquarePosition(BOARD.findSquare(rank, file));
                }
                case EN_PASSANT -> {
                    piece.setSquarePosition(BOARD.findSquare(rank, file));
                    piece.capture(BOARD.findSquare(rank + (piece.getTeam() == PieceType.PIECE_TEAM_WHITE ? 1 : -1), file).getPiece());
                }
                case CASTLE -> {
                }
            }
            switch (posType) {
                case CLEAR, CAPTURE, EN_PASSANT -> BOARD.setTurnCount(BOARD.getTurnCount() + 1);
            }


        } else {
            piece.resetPosition();
        }

    }




}
