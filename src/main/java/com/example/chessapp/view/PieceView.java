package com.example.chessapp.view;

import com.example.chessapp.board.Board;
import com.example.chessapp.model.PieceType;
import com.example.chessapp.peices.Piece;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;

public class PieceView extends ImageView {

    public PieceView(PieceType type, Board board) {

        setImage(type.getImage());
        setViewport(new Rectangle2D(0, 0, this.getImage().getWidth(), this.getImage().getHeight()));
        final double imgAspectRatio = this.getImage().getWidth() / this.getImage().getHeight();

        setFitHeight(board.getSquareSize() - 8);
        setFitWidth(getFitHeight() * imgAspectRatio);

        setTranslateX((-getFitWidth() / 2));
        setTranslateY((-getFitHeight() / 2));

    }


}
