package chess_project.piece;

import chess_project.main.GamePanel;

import java.io.IOException;

public class Rook extends Piece {
    public Rook(int color, int col, int row) {
        super(color, col, row);
        if (color == GamePanel.WHITE) {
            image = getImage("/piece/w-rook");
        } else {
            image = getImage("/piece/b-rook");

        }
    }
}
