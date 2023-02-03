package com.example.chessapp.model;

import com.example.chessapp.board.Board;
import com.example.chessapp.peices.Piece;

public class PieceModel {

    private final PieceType type;
    private int rank;
    private int file;
    private int team;

    public PieceModel(PieceType type, int rank, int file) {
        this.type = type;
        this.rank = rank;
        this.file = file;
        team = type.getTeam();
    }

    public PieceType getType() {
        return type;
    }

    public int getRank() {
        return rank;
    }

    public int getFile() {
        return file;
    }

    public int getTeam() {
        return team;
    }
}
