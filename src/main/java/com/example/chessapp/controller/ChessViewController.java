package com.example.chessapp.controller;

import com.example.chessapp.misc.AnimateUtils;
import com.example.chessapp.model.SquareTeam;
import com.example.chessapp.view.ChessView;
import javafx.event.ActionEvent;

public class ChessViewController extends Controller {

    private ChessView chessView;

    public ChessViewController(ChessView view) {
        super(view);

        chessView = view;

        view.getPartyMode().setOnAction(e -> AnimateUtils.partyMode(e, view.getBoard()));
        view.getDebug().setOnAction(view.getBoard()::debug);
        view.getPicker().setOnAction(this::changeBoardColor);
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
