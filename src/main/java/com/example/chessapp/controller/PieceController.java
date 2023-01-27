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
    private Piece piece;

    public PieceController(PieceView pieceView, Piece piece) {

        this.pieceView = pieceView;
        this.piece = piece;

        pieceView.setOnMouseDragged(this::mouseDragged);
        pieceView.setOnMouseReleased(this::mouseReleased);
    }

    private void mouseDragged(MouseEvent event) {



        pieceView.setX(event.getX() + pieceView.getTranslateX());
        pieceView.setY(event.getY() + pieceView.getTranslateY());
    }

    private void mouseReleased(MouseEvent event) {


        final BoardManager MANAGER = piece.getBoard().getBoardManager();
        final Board BOARD = piece.getBoard();

        int rank = MANAGER.yToRank(piece.getBoard().getSquareSize(), pieceView.getY() - piece.getBoard().getSquareSize());
        int file = MANAGER.xToFile(piece.getBoard().getSquareSize(), pieceView.getX());

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
