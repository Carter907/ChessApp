package com.example.chessapp.board;

import com.example.chessapp.peices.Piece;

public class BoardUtils {

    public static int xToFile(double squareWidth, double x) {
        return (int) ((x + squareWidth) / squareWidth);
    }

    public static int yToRank(double squareHeight, double y) {
        return (int) (((squareHeight * 8) - y) / squareHeight);
    }

    public static double fileToX(double squareWidth, int file) {
        return (file - 1) * squareWidth;
    }

    public static double rankToY(double squareHeight, int rank) {
        return (8 - rank) * squareHeight;
    }

    public static boolean positionIsLegal(Piece piece, int setRank, int setFile) {

        switch (piece.getType()) {
            case PAWN_B, PAWN_W -> {
                return pawnPositionLegal(piece, setRank, setFile);
            }
            case NIGHT_B, NIGHT_W -> {
                return knightPositionLegal(piece, setRank, setFile);
            }
            case BISHOP_B, BISHOP_W -> {
                return bishopPositionLegal(piece, setRank, setFile);
            }
            case ROOK_B, ROOK_W -> {
                return rookPositionLegal(piece, setRank, setFile);
            }
            case KING_B, KING_W -> {
                return kingPositionLegal(piece, setRank, setFile);
            }
        }
        return false;
    }

    private static boolean kingPositionLegal(Piece piece, int setRank, int setFile) {
        int actualRank = piece.getRank();
        int actualFile = piece.getFile();

        if ((actualFile+1 == setFile || actualFile-1 == setFile)
                && (setRank < actualRank+2 && setRank > actualRank-2))
            return true;
        if ((actualFile == setFile) && (setRank < actualRank+2 && setRank > actualRank-2))
            return true;

        return false;
    }

    private static boolean rookPositionLegal(Piece piece, int setRank, int setFile) {
        int actualRank = piece.getRank();
        int actualFile = piece.getFile();

        if (actualFile == setFile ^ actualFile == setRank)
            return true;
        return false;
    }

    private static boolean bishopPositionLegal(Piece piece, int setRank, int setFile) {
        int actualRank = piece.getRank();
        int actualFile = piece.getFile();

        if ((actualRank + actualFile) == (setRank + setFile))
            return true;

        return false;

    }


    private static boolean pawnPositionLegal(Piece piece, int setRank, int setFile) {
        int actualRank = piece.getRank();
        int actualFile = piece.getFile();

        if (piece.getType() == Piece.PieceType.PAWN_B) {
            if (setFile == actualFile) {
                if (actualRank + 1 == setRank) {
                    return true;
                }
                if (actualRank + 2 == setRank && actualRank == 2)
                    return true;
            }

        } else if (piece.getType() == Piece.PieceType.PAWN_W) {
            if (setFile == actualFile) {
                if (actualRank - 1 == setRank) {
                    return true;
                }
                if (actualRank - 2 == setRank && actualRank == 7)
                    return true;
            }
        }


        return false;
    }

    private static boolean knightPositionLegal(Piece piece, int setRank, int setFile) {
        int actualRank = piece.getRank();
        int actualFile = piece.getFile();

        if ((setFile == actualFile - 1 || setFile == actualFile + 1) && (setRank == actualRank + 2 || setRank == actualRank - 2))
            return true;
        if ((setFile == actualFile - 2 || setFile == actualFile + 2) && (setRank == actualRank + 1 || setRank == actualRank - 1))
            return true;

        return false;
    }


}
