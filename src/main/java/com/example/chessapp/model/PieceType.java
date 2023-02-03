package com.example.chessapp.model;

import com.example.chessapp.AppStart;
import javafx.scene.image.Image;

public enum PieceType {


    PAWN_W(new Image(AppStart.class.getResource("sets/set1/white_pawn.png").toExternalForm())),
    NIGHT_W(new Image(AppStart.class.getResource("sets/set1/white_knight.png").toExternalForm())),
    BISHOP_W(new Image(AppStart.class.getResource("sets/set1/white_bishop.png").toExternalForm())),
    ROOK_W(new Image(AppStart.class.getResource("sets/set1/white_rook.png").toExternalForm())),
    QUEEN_W(new Image(AppStart.class.getResource("sets/set1/white_queen.png").toExternalForm())),
    KING_W(new Image(AppStart.class.getResource("sets/set1/white_king.png").toExternalForm())),
    PAWN_B(new Image(AppStart.class.getResource("sets/set1/black_pawn.png").toExternalForm())),
    NIGHT_B(new Image(AppStart.class.getResource("sets/set1/black_knight.png").toExternalForm())),
    BISHOP_B(new Image(AppStart.class.getResource("sets/set1/black_bishop.png").toExternalForm())),
    ROOK_B(new Image(AppStart.class.getResource("sets/set1/black_rook.png").toExternalForm())),
    QUEEN_B(new Image(AppStart.class.getResource("sets/set1/black_queen.png").toExternalForm())),
    KING_B(new Image(AppStart.class.getResource("sets/set1/black_king.png").toExternalForm()));

    private final Image sprite;

    public static final int PIECE_TEAM_WHITE = 0, PIECE_TEAM_BLACK = 1;

    private int team;
    private static PieceType[] pieceTypes = PieceType.values();

    PieceType(Image sprite) {
        this.sprite = sprite;
        this.team = ("" + name().charAt(name().length() - 1)).equals("B") ? PIECE_TEAM_BLACK : PIECE_TEAM_WHITE;
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

    public static boolean isPiece(String pieceName, PieceType type) {

        pieceName = pieceName.toLowerCase();
        String typeName = type.name().substring(0, type.name().indexOf("_")).toLowerCase();
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
        return sprite;
    }

    public boolean isOppositeTeam(PieceType type) {
        return !(type.team == this.team);
    }

    public Image getSprite() {
        return sprite;
    }

    public static PieceType[] getPieceTypes() {
        return pieceTypes;
    }

}

