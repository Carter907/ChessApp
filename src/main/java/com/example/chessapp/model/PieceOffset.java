package com.example.chessapp.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static com.example.chessapp.model.PieceType.PIECE_TEAM_WHITE;
enum PieceOffset {
    DIAGONAL_OFFSET(new Integer[]{1, 1, -1, -1, -1, 1, 1, -1}),
    STRAIGHT_OFFSET(new Integer[]{1, 0, -1, 0, 0, 1, 0, -1}),
    PAWN_W_OFFSET(new Integer[]{-1, 0, -1, 1, -1, -1, -2, 0}),
    PAWN_B_OFFSET(new Integer[]{1, 0, 1, -1, 1, 1, 2, 0}),
    KNIGHT_OFFSET(new Integer[]{2, 1, 2, -1, 1, 2, 1, -2,-1, 2, -1, -2, -2, 1, -2, -1}),
    KING_OFFSET(new Integer[]{1, 0, 1, 1, 1, -1, 0, -1, 0, 1, -1, 0, -1, -1, -1, 1});

    private Integer[] offsets;
    PieceOffset(Integer[] offsets) {

        this.offsets = offsets;
    }

    public static Integer[] of(PieceType type) {
        if (type.isSlidingPiece())
            return switch (type.getSimplePieceName()) {
                case "bishop" -> DIAGONAL_OFFSET.offsets;
                case "rook" -> STRAIGHT_OFFSET.offsets;
                case "queen" -> DIAGONAL_OFFSET.plus(STRAIGHT_OFFSET);
                default -> new Integer[0];
            };
        else if (type.isStepPiece())
            switch (type.getSimplePieceName()) {
                case "pawn" -> {
                    return type.getTeam() == PIECE_TEAM_WHITE ? PAWN_W_OFFSET.offsets : PAWN_B_OFFSET.offsets;
                }
                case "knight" -> {
                    return KNIGHT_OFFSET.offsets;
                }
                case "king" -> {
                    return KING_OFFSET.offsets;
                }

            }

        return null;
    }

    private Integer[] plus(PieceOffset offset) {
        return List.of(offset.offsets, this.offsets)
                .stream()
                .flatMap(n -> Arrays.stream(n))
                .toArray(Integer[]::new);

    }
}
