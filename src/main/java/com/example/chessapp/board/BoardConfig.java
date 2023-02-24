package com.example.chessapp.board;

public enum BoardConfig {

    INSTANCE;
    private volatile boolean turnBased;
    private volatile boolean squaresCheckedHighlighting;
    private volatile boolean mobilityHighlighting;

    BoardConfig() {
        turnBased = true;
        squaresCheckedHighlighting = false;
        mobilityHighlighting = true;
    }

    public synchronized boolean isTurnBased() {
        return turnBased;
    }

    public synchronized void setTurnBased(boolean turnBased) {
        this.turnBased = turnBased;
    }

    public synchronized boolean hasSquaresCheckedHighlighting() {
        return squaresCheckedHighlighting;
    }

    public synchronized void setSquaresCheckedHighlighting(boolean squaresCheckedHighlighting) {
        this.squaresCheckedHighlighting = squaresCheckedHighlighting;
    }

    public synchronized boolean hasMobilityHighlighting() {
        return mobilityHighlighting;
    }

    public synchronized void setMobilityHighlighting(boolean mobilityHighlighting) {
        this.mobilityHighlighting = mobilityHighlighting;
    }
}
