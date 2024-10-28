package chess_project.piece;

import chess_project.main.GamePanel;

import java.io.IOException;

public class Queen extends Piece{
    public Queen(int color, int col, int row)  {
        super(color, col, row);
        if (color == GamePanel.WHITE) {
            image = getImage("/piece/w-queen");
        } else {
            image = getImage("/piece/b-queen");

        }
    }
}
