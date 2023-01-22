package com.example.chessapp.view;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class View extends Pane {

    protected Pane root;
    protected Stage stage;
    public View(Stage stage) {
        this.stage = stage;
    }
}
