package com.example.chessapp.model;

import com.example.chessapp.AppStart;
import javafx.scene.image.Image;

public enum PieceType {
    PAWN_W("sets/set1/white_pawn.png"),
    NIGHT_W("sets/set1/white_knight.png"),
    BISHOP_W("sets/set1/white_bishop.png"),
    ROOK_W("sets/set1/white_rook.png"),
    QUEEN_W("sets/set1/white_queen.png"),
    KING_W("sets/set1/white_king.png"),
    PAWN_B("sets/set1/black_pawn.png"),
    NIGHT_B("sets/set1/black_knight.png"),
    BISHOP_B("sets/set1/black_bishop.png"),
    ROOK_B("sets/set1/black_rook.png"),
    QUEEN_B("sets/set1/black_queen.png"),
    KING_B("sets/set1/black_king.png");

    private final String imagePath;

    private final Integer[] moveOffsets;

    public static final int PIECE_TEAM_WHITE = 0, PIECE_TEAM_BLACK = 1;

    private int team;
    private final static PieceType[] pieceTypes;

    static {
        pieceTypes = PieceType.values();
    }

    PieceType(String imagePath) {
        this.imagePath = imagePath;
        this.team = ("" + name().charAt(name().length() - 1)).equals("B") ? PIECE_TEAM_BLACK : PIECE_TEAM_WHITE;
        this.moveOffsets = PieceOffset.of(this);
    }

    private Integer[] setMoveOffsets() {
        return this.moveOffsets;
    }

    public boolean isStepPiece() {
        return switch (this.getSimplePieceName()) {
            case "pawn", "king", "knight" -> true;
            default -> false;
        };
    }

    public boolean isSlidingPiece() {
        return switch (this.getSimplePieceName()) {
            case "bishop", "rook", "queen" -> true;
            default -> false;
        };
    }

    public Integer[] getMoveOffsets() {
        return moveOffsets;
    }

    public static PieceType charToPieceType(char c) {
        for (PieceType piece : pieceTypes) {
            String name = piece.name();
            if (Character.isUpperCase(c)
                    && c == name.charAt(0)
                    && name.charAt(name.length() - 1) == 'B') {
                return piece;
            } else if (Character.isLowerCase(c)
                    && c == Character.toLowerCase(name.charAt(0))
                    && name.charAt(name.length() - 1) == 'W') {
                return piece;
            }
        }
        return null;
    }

    public String getSimplePieceName() {
        String simplified = this.name().substring(0, this.name().indexOf("_")).toLowerCase();
        if (simplified.equals("night"))
            return "knight";
        return simplified;
    }

    public static boolean isPiece(String pieceName, PieceType type) {

        pieceName = pieceName.toLowerCase();
        String typeName = type.getSimplePieceName();
        switch (pieceName) {
            case "rook", "pawn", "king", "bishop", "queen", "knight" -> {
                return pieceName.equals(typeName);
            }
        }

        return false;
    }

    public int getTeam() {
        return team;
    }

    public Image getImage() {
        return new Image(AppStart.class.getResource(imagePath).toExternalForm());
    }

    public boolean oppositeTeamOf(PieceType type) {
        return !(type.team == this.team);
    }

    public static PieceType[] getPieceTypes() {
        return pieceTypes;
    }

}

