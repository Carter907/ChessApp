package com.example.chessapp.board;

import com.example.chessapp.peices.Piece;

import java.util.Arrays;
import java.util.stream.Collectors;

public class BoardManager {
    private Board board;


    public BoardManager(Board board) {
        this.board = board;
    }
    public int xToFile(double squareWidth, double x) {
        return (int) ((x + squareWidth) / squareWidth);
    }

    public int yToRank(double squareHeight, double y) {
        return (int) (((squareHeight * 8) - y) / squareHeight);
    }

    public double fileToX(double squareWidth, int file) {
        return (file - 1) * squareWidth;
    }

    public double rankToY(double squareHeight, int rank) {
        return (8 - rank) * squareHeight;
    }

    public Integer[] positionIsLegal(Piece piece, int setRank, int setFile) {

        switch (piece.getType()) {
            case PAWN_B, PAWN_W -> {
                return pawnPositionLegal(piece, setRank, setFile);
            }
//            case NIGHT_B, NIGHT_W -> {
//                return knightPositionLegal(piece, setRank, setFile);
//            }
//            case BISHOP_B, BISHOP_W -> {
//                return bishopPositionLegal(piece, setRank, setFile);
//            }
//            case ROOK_B, ROOK_W -> {
//                return rookPositionLegal(piece, setRank, setFile);
//            }
//            case KING_B, KING_W -> {
//                return kingPositionLegal(piece, setRank, setFile);
//            }
        }
        return null;
    }

    private Integer[] setCaptureSquares(Integer[] indices) {
        for (Integer n : indices) {
            Board.Square square = board.findSquare(n);
            square.setCaptureSquare(true);
        }
        return indices;
    }

    public Integer[] resetConstraints(Integer[] indices) {
        for (Integer n : indices) {
            Board.Square square = board.findSquare(n);
            square.setCaptureSquare(false);
        }
        return indices;
    }

    private boolean kingPositionLegal(Piece piece, int setRank, int setFile) {
        int actualRank = piece.getRank();
        int actualFile = piece.getFile();

        if ((actualFile + 1 == setFile || actualFile - 1 == setFile)
                && (setRank < actualRank + 2 && setRank > actualRank - 2))
            return true;
        if ((actualFile == setFile) && (setRank < actualRank + 2 && setRank > actualRank - 2))
            return true;

        return false;
    }

    private boolean rookPositionLegal(Piece piece, int setRank, int setFile) {
        int actualRank = piece.getRank();
        int actualFile = piece.getFile();

        if (actualFile == setFile ^ actualRank == setRank)
            return true;
        return false;
    }

    private boolean bishopPositionLegal(Piece piece, int setRank, int setFile) {
        int actualRank = piece.getRank();
        int actualFile = piece.getFile();

        if ((actualRank + actualFile) == (setRank + setFile))
            return true;

        return false;

    }

    public int posToIndex(int rank, int file) {

        return ((8 - rank) * 8) + file - 1;
    }

    private Integer[] pawnPositionLegal(Piece piece, int setRank, int setFile) {
        int actualRank = piece.getRank();
        int actualFile = piece.getFile();

        if (piece.getType() == Piece.PieceType.PAWN_B) {
            if (setFile == actualFile) {
                if (actualRank + 1 == setRank) {

                    return new Integer[]{posToIndex(setRank, setFile)};
                }
                if (actualRank + 2 == setRank && actualRank == 2)
                    return new Integer[]{posToIndex(setRank - 1, setFile), posToIndex(setRank, setFile)};

            }
            if (actualRank + 1 == setRank) {
                if (actualFile + 1 == setFile || actualFile - 1 == setFile)
                    return setCaptureSquares(new Integer[]{posToIndex(setRank, setFile)});

            }
        } else if (piece.getType() == Piece.PieceType.PAWN_W) {
            if (setFile == actualFile) {
                if (actualRank - 1 == setRank) {
                    return new Integer[]{posToIndex(setRank, setFile)};
                }
                if (actualRank - 2 == setRank && actualRank == 7)
                    return new Integer[]{posToIndex(setRank + 1, setFile), posToIndex(setRank, setFile)};

            }
            if (actualRank - 1 == setRank) {
                if (actualFile - 1 == setFile || actualFile + 1 == setFile)
                    return setCaptureSquares(new Integer[]{posToIndex(setRank, setFile)});

            }
        }


        return null;
    }

    public void bindPieceAndSquare(Piece piece, Board.Square square) {
        if (piece.hasSquare()) {
            piece.getSquare().setPiece(null);
        }
        if (square.hasPiece()) {
            square.getPiece().setSquare(null);
        }

        piece.setSquare(square);
        square.setPiece(piece);

    }

    private boolean knightPositionLegal(Piece piece, int setRank, int setFile) {
        int actualRank = piece.getRank();
        int actualFile = piece.getFile();

        if ((setFile == actualFile - 1 || setFile == actualFile + 1) && (setRank == actualRank + 2 || setRank == actualRank - 2))
            return true;
        if ((setFile == actualFile - 2 || setFile == actualFile + 2) && (setRank == actualRank + 1 || setRank == actualRank - 1))
            return true;

        return false;
    }


}
