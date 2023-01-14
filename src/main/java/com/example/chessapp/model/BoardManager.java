package com.example.chessapp.model;

import com.example.chessapp.board.Board;
import com.example.chessapp.peices.Piece;

import java.util.Arrays;
import java.util.function.Consumer;

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
            case QUEEN_B, QUEEN_W -> {
                return queenPositionLegal(piece, setRank, setFile);
            }
        }
        return null;
    }

    private Integer[] queenPositionLegal(Piece piece, int setRank, int setFile) {
        int actualRank = piece.getRank();
        int actualFile = piece.getFile();

        if (actualRank != setRank && actualFile != setFile)
            return bishopPositionLegal(piece, setRank, setFile);
        else
            return rookPositionLegal(piece, setRank, setFile);

    }

    private Integer[] setConstraints(Integer[] indices, PositionType... constraints) {

        for (PositionType constraint : constraints) {
            switch (constraint) {
                case CAPTURE -> {
                    for (Integer n : indices) {
                        Board.Square square = board.findSquare(n);
                        square.setCaptureSquare(true);
                    }
                }
                case CLEAR -> {
                    for (Integer n : indices) {
                        Board.Square square = board.findSquare(n);
                        square.setClearSquare(true);
                    }
                }
                case EN_PASSANT -> {
                    for (Integer n : indices) {
                        Board.Square square = board.findSquare(n);
                        square.setEnPassant(true);
                        square.setPositionTurn(board.getTurnCount());

                    }

                }
            }
        }


        return indices;
    }

    public Integer[] resetConstraints(Integer[] indices) {
        for (Integer n : indices) {
            Board.Square square = board.findSquare(n);
            square.setCaptureSquare(false);
            square.setClearSquare(false);

        }
        return indices;
    }

    public void applyToAllSquares(Integer[] indices, Consumer<Board.Square> consumer) {

        Arrays.stream(indices).map(board::findSquare).forEach(consumer);

    }

    private Integer[] kingPositionLegal(Piece piece, int setRank, int setFile) {
        int actualRank = piece.getRank();
        int actualFile = piece.getFile();

        if ((actualFile + 1 == setFile || actualFile - 1 == setFile)
                && (setRank < actualRank + 2 && setRank > actualRank - 2))
            return setConstraints(new Integer[]{posToIndex(setRank, setFile)}, PositionType.CAPTURE, PositionType.CLEAR);
        if ((actualFile == setFile) && (setRank < actualRank + 2 && setRank > actualRank - 2))
            return setConstraints(new Integer[]{posToIndex(setRank, setFile)}, PositionType.CAPTURE, PositionType.CLEAR);

        return null;
    }

    private Integer[] rookPositionLegal(Piece piece, int setRank, int setFile) {
        int actualRank = piece.getRank();
        int actualFile = piece.getFile();
        Integer[] squareArr = new Integer[0];
        if (actualFile == setFile ^ actualRank == setRank) {
            if (actualFile == setFile) {
                if (setRank > actualRank) {
                    for (int i = setRank; i > actualRank; i--) {
                        squareArr = Arrays.copyOf(squareArr, squareArr.length + 1);
                        squareArr[squareArr.length - 1] = posToIndex(i, setFile);
                    }

                } else if (setRank < actualRank) {
                    for (int i = setRank; i < actualRank; i++) {
                        squareArr = Arrays.copyOf(squareArr, squareArr.length + 1);
                        squareArr[squareArr.length - 1] = posToIndex(i, setFile);
                    }
                }
            } else if (setFile > actualFile) {

                for (int i = setFile; i > actualFile; i--) {
                    squareArr = Arrays.copyOf(squareArr, squareArr.length + 1);
                    squareArr[squareArr.length - 1] = posToIndex(setRank, i);
                }
            } else if (setFile < actualFile) {
                for (int i = setFile; i < actualFile; i++) {
                    squareArr = Arrays.copyOf(squareArr, squareArr.length + 1);
                    squareArr[squareArr.length - 1] = posToIndex(setRank, i);
                }
            }

            return setConstraints(squareArr, PositionType.CAPTURE, PositionType.CLEAR);
        }
        return null;
    }

    private Integer[] bishopPositionLegal(Piece piece, int setRank, int setFile) {
        int actualRank = piece.getRank();
        int actualFile = piece.getFile();
        int actualIndex = posToIndex(actualRank, actualFile);
        int setIndex = posToIndex(setRank, setFile);
        //capture clear
        //TODO: figure out how to get all squares of a moving bishop
        if (actualIndex != setIndex) {
            Integer[] squareArr = new Integer[0];
            if ((actualFile - actualRank) == (setFile - setRank)) {
                System.out.println(squareArr);
                if (actualRank < setRank) {
                    for (int i = setRank, y = 0; i > actualRank; i--, y++) {
                        squareArr = Arrays.copyOf(squareArr, squareArr.length + 1);
                        squareArr[squareArr.length - 1] = posToIndex(i, setFile - y);
                    }
                    System.out.println(Arrays.toString(squareArr));
                } else {
                    for (int i = setRank, y = 0; i < actualRank; i++, y++) {
                        squareArr = Arrays.copyOf(squareArr, squareArr.length + 1);
                        squareArr[squareArr.length - 1] = posToIndex(i, setFile + y);
                    }
                    System.out.println(Arrays.toString(squareArr));
                }
                return setConstraints(squareArr, PositionType.CAPTURE, PositionType.CLEAR);
            } else if ((actualRank + actualFile) == (setRank + setFile)) {

                if (actualRank < setRank) {
                    for (int i = setRank, y = 0; i > actualRank; i--, y++) {
                        squareArr = Arrays.copyOf(squareArr, squareArr.length + 1);
                        squareArr[squareArr.length - 1] = posToIndex(i, setFile + y);
                    }
                    System.out.println(Arrays.toString(squareArr));
                } else {
                    for (int i = setRank, y = 0; i < actualRank; i++, y++) {
                        squareArr = Arrays.copyOf(squareArr, squareArr.length + 1);
                        squareArr[squareArr.length - 1] = posToIndex(i, setFile - y);
                    }
                    System.out.println(Arrays.toString(squareArr));
                }
                return setConstraints(squareArr, PositionType.CAPTURE, PositionType.CLEAR);

            }
        }
        return null;

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
                    return new Integer[]{setConstraints(new Integer[]{posToIndex(setRank - 1, setFile)}, PositionType.EN_PASSANT)[0], posToIndex(setRank, setFile)};

            }
            if (actualRank + 1 == setRank) {
                if (actualFile + 1 == setFile || actualFile - 1 == setFile)
                    return setConstraints(new Integer[]{posToIndex(setRank, setFile)}, PositionType.CAPTURE);

            }
        } else if (piece.getType() == Piece.PieceType.PAWN_W) {
            if (setFile == actualFile) {
                if (actualRank - 1 == setRank) {
                    return new Integer[]{posToIndex(setRank, setFile)};
                }
                if (actualRank - 2 == setRank && actualRank == 7)
                    return new Integer[]{setConstraints(new Integer[]{posToIndex(setRank + 1, setFile)}, PositionType.EN_PASSANT)[0], posToIndex(setRank, setFile)};

            }
            if (actualRank - 1 == setRank) {
                if (actualFile - 1 == setFile || actualFile + 1 == setFile)
                    return setConstraints(new Integer[]{posToIndex(setRank, setFile)}, PositionType.CAPTURE);

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

    private Integer[] knightPositionLegal(Piece piece, int setRank, int setFile) {
        int actualRank = piece.getRank();
        int actualFile = piece.getFile();

        if ((setFile == actualFile - 1 || setFile == actualFile + 1) && (setRank == actualRank + 2 || setRank == actualRank - 2))
            return setConstraints(new Integer[]{posToIndex(setRank, setFile)}, PositionType.CLEAR, PositionType.CAPTURE);
        if ((setFile == actualFile - 2 || setFile == actualFile + 2) && (setRank == actualRank + 1 || setRank == actualRank - 1))
            return setConstraints(new Integer[]{posToIndex(setRank, setFile)}, PositionType.CLEAR, PositionType.CAPTURE);

        return null;
    }


}
