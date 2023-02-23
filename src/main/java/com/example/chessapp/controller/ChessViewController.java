package com.example.chessapp.controller;

import com.example.chessapp.AppStart;
import com.example.chessapp.board.BoardConfig;
import com.example.chessapp.misc.AnimateUtils;
import com.example.chessapp.model.SquareTeam;
import com.example.chessapp.view.ChessView;
import javafx.event.ActionEvent;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;

public class ChessViewController extends Controller {

    private ChessView chessView;

    public ChessViewController(ChessView view) {
        super(view);

        chessView = view;

        view.getPartyMode().setOnAction(e -> AnimateUtils.partyMode(e, view.getBoard()));
        view.getDebug().setOnAction(view.getBoard()::debug);
        view.getPicker().setOnAction(this::changeBoardColor);
        view.getMobilityHighlighting().setOnAction(this::onMobilityHighlighting);
        view.getTurnBased().setOnAction(this::onTurnBased);
        view.getSquaresCheckedHighlighting().setOnAction(this::onSquaresCheckedHighlighting);
    }

    private void onSquaresCheckedHighlighting(ActionEvent event) {
        BoardConfig.INSTANCE.setSquaresCheckedHighlighting(!BoardConfig.INSTANCE.hasSquaresCheckedHighlighting());
        if (BoardConfig.INSTANCE.hasSquaresCheckedHighlighting())
            chessView.getSquaresCheckedHighlighting().getStyleableNode().setStyle("-fx-background-color: lime");
        else chessView.getSquaresCheckedHighlighting().getStyleableNode().setStyle("");

    }

    private void onTurnBased(ActionEvent event) {
        BoardConfig.INSTANCE.setTurnBased(!BoardConfig.INSTANCE.isTurnBased());
        if (BoardConfig.INSTANCE.isTurnBased())
            chessView.getTurnBased().getStyleableNode().setStyle("-fx-background-color: lime");
        else chessView.getTurnBased().getStyleableNode().setStyle("");
    }
    private void onMobilityHighlighting(ActionEvent event) {
        BoardConfig.INSTANCE.setMobilityHighlighting(!BoardConfig.INSTANCE.hasMobilityHighlighting());
        if (BoardConfig.INSTANCE.hasMobilityHighlighting())
            chessView.getMobilityHighlighting().getStyleableNode().setStyle("-fx-background-color: lime");
        else chessView.getMobilityHighlighting().getStyleableNode().setStyle("");
    }

    private void changeBoardColor(ActionEvent event) {
        chessView.getBoard().applyToAllSquares(s -> {
            if (s.getTeam() == SquareTeam.DARK) {
                s.setColor(chessView.getPicker().getValue().darker());
            } else {
                s.setColor(chessView.getPicker().getValue().brighter().brighter());
            }

        });

        chessView.getBoard().refreshAllSquares();
    }


}
