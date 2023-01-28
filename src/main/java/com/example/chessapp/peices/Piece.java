package com.example.chessapp.peices;

import com.example.chessapp.board.Board;
import com.example.chessapp.controller.PieceController;
import com.example.chessapp.model.BoardManager;
import com.example.chessapp.model.PieceType;
import com.example.chessapp.view.PieceView;


public class Piece {
    private final PieceType type;
    private Board board;
    private final BoardManager boardManager;
    private Board.Square square;
    private PieceView view;
    private final PieceController controller;
    private int rank;
    private int file;
    private int team;

    public Piece(PieceType type, Board board, int rank, int file) {
        this.type = type;
        this.board = board;
        this.rank = rank;
        this.file = file;
        this.boardManager = board.getBoardManager();
        team = type.getTeam();
        view = new PieceView(type, board);
        controller = new PieceController(view, this);

    }

    public Board.Square getSquare() {
        return square;
    }

    public Board getBoard() {
        return board;
    }

    public void setSquarePosition(Board.Square square) {
        if (square == null) {
            this.square = null;
            return;
        }
        double x = boardManager.fileToX(board.getSquareSize(), square.getFile()) + (board.getSquareSize() / 2);
        double y = boardManager.rankToY(board.getSquareSize(), square.getRank()) + (board.getSquareSize() / 2);

        this.boardManager.bindPieceAndSquare(this, square);
        this.rank = square.getRank();
        this.file = square.getFile();

        this.view.setX(x);
        this.view.setY(y);

//        System.out.println(this);

    }

    public PieceView getView() {
        return view;
    }

    public PieceController getController() {
        return controller;
    }

    public boolean hasSquare() {
        return this.square != null;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public PieceType getType() {
        return type;
    }

    public void setSquare(Board.Square square) {
        this.square = square;
    }




    public BoardManager getBoardManager() {
        return boardManager;
    }

    public int getTeam() {
        return team;
    }

    public void capture(Piece piece) {
        System.out.println("captured: there is a " + piece.getType()
                + " on the " + piece.getRank() + " rank and " + piece.getFile() + " file");
        piece.getSquare().setPiece(null);
        piece.getBoard().getPeicePane().getChildren().remove(piece.getView());
    }
    public boolean isPiece(String pieceName) {
        return PieceType.isPiece(pieceName, type);
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



    @Override
    public String toString() {
        return String.format("<piece:%s square=%s rank=%d file=%d board=%s/>",type, square, rank, file, board);
    }

}
