package com.example.chessapp.board;

public enum BoardConfig {

    INSTANCE;
    private boolean turnBased;
    private boolean squaresCheckedHighlighting;
    private boolean mobilityHighlighting;
    private boolean isWhite;

    BoardConfig() {
        turnBased = true;
        squaresCheckedHighlighting = false;
        mobilityHighlighting = true;
        isWhite = false;
    }

    public synchronized boolean isWhite() {
        return isWhite;
    }

    public synchronized void setWhite(boolean white) {
        isWhite = white;
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
