package com.example.chessapp.model;

import com.example.chessapp.board.Board;
import com.example.chessapp.peices.Piece;

import java.util.Arrays;
import java.util.function.Consumer;

public class BoardManager {
    private Board board;
    private int team, currentRank, currentFile;
    private PieceType type;

    public BoardManager(Board board) {
        this.board = board;
    }

    public Integer[] positionIsLegal(PieceModel pieceModel, int setRank, int setFile) {

        team = pieceModel.getTeam();
        currentRank = pieceModel.getRank();
        currentFile = pieceModel.getFile();
        type = pieceModel.getType();

        switch (pieceModel.getType()) {
            case PAWN_B, PAWN_W -> {
                return pawnPositionLegal(setRank, setFile);
            }
            case NIGHT_B, NIGHT_W -> {
                return knightPositionLegal(setRank, setFile);
            }
            case BISHOP_B, BISHOP_W -> {
                return bishopPositionLegal(setRank, setFile);
            }
            case ROOK_B, ROOK_W -> {
                return rookPositionLegal(setRank, setFile);
            }
            case KING_B, KING_W -> {
                return kingPositionLegal(setRank, setFile);
            }
            case QUEEN_B, QUEEN_W -> {
                return queenPositionLegal(setRank, setFile);
            }
        }
        return null;
    }

    private Integer[] queenPositionLegal(int setRank, int setFile) {


        if (currentRank != setRank && currentFile != setFile)
            return bishopPositionLegal(setRank, setFile);
        else
            return rookPositionLegal(setRank, setFile);

    }

    private Integer[] setConstraints(Integer[] indices, MoveType... constraints) {

        for (MoveType constraint : constraints) {
            switch (constraint) {
                case CAPTURE -> {
                    for (Integer n : indices) {
                        Board.Square square = board.findSquare(n);
                        square.getMoveTypes().replace(MoveType.CAPTURE, true);
                    }
                }
                case CLEAR -> {
                    for (Integer n : indices) {
                        Board.Square square = board.findSquare(n);
                        square.getMoveTypes().replace(MoveType.CLEAR, true);
                    }
                }
                case EN_PASSANT -> {
                    for (Integer n : indices) {
                        Board.Square square = board.findSquare(n);
                        square.getMoveTypes().replace(MoveType.EN_PASSANT, true);
                        square.setPositionTurn(board.getTurnCount());

                    }
                }
                case SHORT_CASTLE -> {
                    for (Integer n : indices) {
                        Board.Square square = board.findSquare(n);
                        square.getMoveTypes().replace(MoveType.SHORT_CASTLE, true);

                    }
                }
            }
        }


        return indices;
    }

    public Integer[] resetConstraints(Integer[] indices) {
        for (Integer n : indices) {
            Board.Square square = board.findSquare(n);
            square.getMoveTypes().replace(MoveType.CAPTURE, false);
            square.getMoveTypes().replace(MoveType.CLEAR, false);

            square.getMoveTypes().replace(MoveType.SHORT_CASTLE, false);

        }
        return indices;
    }

    public void applyToAllSquares(Integer[] indices, Consumer<Board.Square> consumer) {

        Arrays.stream(indices).map(board::findSquare).forEach(consumer);

    }

    private Integer[] kingPositionLegal(int setRank, int setFile) {


        if ((currentFile + 1 == setFile || currentFile - 1 == setFile)
                && (setRank < currentRank + 2 && setRank > currentRank - 2))
            return setConstraints(new Integer[]{posToIndex(setRank, setFile)}, MoveType.CAPTURE, MoveType.CLEAR);
        if ((currentFile == setFile) && (setRank < currentRank + 2 && setRank > currentRank - 2))
            return setConstraints(new Integer[]{posToIndex(setRank, setFile)}, MoveType.CAPTURE, MoveType.CLEAR);

        if (currentRank == setRank && setFile == currentFile+2)
            return setConstraints(new Integer[]{posToIndex(setRank, setFile)}, MoveType.SHORT_CASTLE);
        if (currentRank == setRank && setFile == currentFile-3)
            return setConstraints(new Integer[]{posToIndex(setRank, setFile)}, MoveType.LONG_CASTLE);


        return null;
    }

    private Integer[] rookPositionLegal(int setRank, int setFile) {


        Integer[] squareArr = new Integer[0];
        if (currentFile == setFile ^ currentRank == setRank) {
            if (currentFile == setFile) {
                if (setRank > currentRank) {
                    for (int i = setRank; i > currentRank; i--) {
                        squareArr = Arrays.copyOf(squareArr, squareArr.length + 1);
                        squareArr[squareArr.length - 1] = posToIndex(i, setFile);
                    }

                } else if (setRank < currentRank) {
                    for (int i = setRank; i < currentRank; i++) {
                        squareArr = Arrays.copyOf(squareArr, squareArr.length + 1);
                        squareArr[squareArr.length - 1] = posToIndex(i, setFile);
                    }
                }
            } else if (setFile > currentFile) {

                for (int i = setFile; i > currentFile; i--) {
                    squareArr = Arrays.copyOf(squareArr, squareArr.length + 1);
                    squareArr[squareArr.length - 1] = posToIndex(setRank, i);
                }
            } else if (setFile < currentFile) {
                for (int i = setFile; i < currentFile; i++) {
                    squareArr = Arrays.copyOf(squareArr, squareArr.length + 1);
                    squareArr[squareArr.length - 1] = posToIndex(setRank, i);
                }
            }

            return setConstraints(squareArr, MoveType.CAPTURE, MoveType.CLEAR);
        }
        return null;
    }

    private Integer[] bishopPositionLegal(int setRank, int setFile) {

        int actualIndex = posToIndex(currentRank, currentFile);
        int setIndex = posToIndex(setRank, setFile);
        //capture clear
        //TODO: figure out how to get all squares of a moving bishop
        if (actualIndex != setIndex) {
            Integer[] squareArr = new Integer[0];
            if ((currentFile - currentRank) == (setFile - setRank)) {

                if (currentRank < setRank) {
                    for (int i = setRank, y = 0; i > currentRank; i--, y++) {
                        squareArr = Arrays.copyOf(squareArr, squareArr.length + 1);
                        squareArr[squareArr.length - 1] = posToIndex(i, setFile - y);
                    }
                } else {
                    for (int i = setRank, y = 0; i < currentRank; i++, y++) {
                        squareArr = Arrays.copyOf(squareArr, squareArr.length + 1);
                        squareArr[squareArr.length - 1] = posToIndex(i, setFile + y);
                    }
                }
                return setConstraints(squareArr, MoveType.CAPTURE, MoveType.CLEAR);
            } else if ((currentRank + currentFile) == (setRank + setFile)) {

                if (currentRank < setRank) {
                    for (int i = setRank, y = 0; i > currentRank; i--, y++) {
                        squareArr = Arrays.copyOf(squareArr, squareArr.length + 1);
                        squareArr[squareArr.length - 1] = posToIndex(i, setFile + y);
                    }
                } else {
                    for (int i = setRank, y = 0; i < currentRank; i++, y++) {
                        squareArr = Arrays.copyOf(squareArr, squareArr.length + 1);
                        squareArr[squareArr.length - 1] = posToIndex(i, setFile - y);
                    }
                }
                return setConstraints(squareArr, MoveType.CAPTURE, MoveType.CLEAR);

            }
        }
        return null;

    }

    public int posToIndex(int rank, int file) {

        return ((8 - rank) * 8) + file - 1;
    }

    private Integer[] pawnPositionLegal(int setRank, int setFile) {


        if (type == PieceType.PAWN_B) {
            if (setFile == currentFile) {
                if (currentRank + 1 == setRank) {

                    return new Integer[]{posToIndex(setRank, setFile)};
                }
                if (currentRank + 2 == setRank && currentRank == 2)
                    return new Integer[]{setConstraints(new Integer[]{posToIndex(setRank - 1, setFile)}, MoveType.EN_PASSANT)[0], posToIndex(setRank, setFile)};

            }
            if (currentRank + 1 == setRank) {
                if (currentFile + 1 == setFile || currentFile - 1 == setFile)
                    return setConstraints(new Integer[]{posToIndex(setRank, setFile)}, MoveType.CAPTURE);

            }
        } else if (type == PieceType.PAWN_W) {
            if (setFile == currentFile) {
                if (currentRank - 1 == setRank) {
                    return new Integer[]{posToIndex(setRank, setFile)};
                }
                if (currentRank - 2 == setRank && currentRank == 7)
                    return new Integer[]{setConstraints(new Integer[]{posToIndex(setRank + 1, setFile)}, MoveType.EN_PASSANT)[0], posToIndex(setRank, setFile)};

            }
            if (currentRank - 1 == setRank) {
                if (currentFile - 1 == setFile || currentFile + 1 == setFile)
                    return setConstraints(new Integer[]{posToIndex(setRank, setFile)}, MoveType.CAPTURE);

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

    private Integer[] knightPositionLegal(int setRank, int setFile) {

        if ((setFile == currentFile - 1 || setFile == currentFile + 1) && (setRank == currentRank + 2 || setRank == currentRank - 2))
            return setConstraints(new Integer[]{posToIndex(setRank, setFile)}, MoveType.CLEAR, MoveType.CAPTURE);
        if ((setFile == currentFile - 2 || setFile == currentFile + 2) && (setRank == currentRank + 1 || setRank == currentRank - 1))
            return setConstraints(new Integer[]{posToIndex(setRank, setFile)}, MoveType.CLEAR, MoveType.CAPTURE);

        return null;
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
}
