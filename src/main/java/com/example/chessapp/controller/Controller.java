package com.example.chessapp.controller;

import com.example.chessapp.view.View;

public class Controller {

    protected View view;

    protected Controller(View view) {
        this.view = view;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }
}
