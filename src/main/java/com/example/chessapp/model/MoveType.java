package com.example.chessapp.model;

public enum MoveType {
    CLEAR,
    BLOCKED,
    CAPTURE,
    SHORT_CASTLE,
    LONG_CASTLE,
    EN_PASSANT;

    public static final MoveType[] values;

    static {
        values = values();
    }
}
