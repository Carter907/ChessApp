package com.example.chessapp.screen;

import com.example.chessapp.AppStart;
import com.example.chessapp.board.Board;

public class GameScreen {

    private final int width;
    private final int height;
    private final AppStart app;

    private Board board;
    public GameScreen(AppStart app, int width, int height) {
        this.width = width;
        this.app = app;
        this.height = height;

    }

    public Board getBoard() {
        return board;
    }
    public void setBoard(Board board) {
        this.board = board;
    }
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public AppStart getApp() {
        return app;
    }
}
