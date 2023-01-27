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
        setPreserveRatio(true);
        setFitHeight(board.getSquareSize() - 10);

        final double ASPECT_RATIO = this.getImage().getWidth() / this.getImage().getHeight();
        setTranslateX((-getFitHeight() * ASPECT_RATIO / 2));
        setTranslateY((-getFitHeight() / ASPECT_RATIO / 2));

        if (type == PieceType.PAWN_B || type == PieceType.PAWN_W)
            this.setTranslateY(this.getTranslateY() + 5);
    }
}
