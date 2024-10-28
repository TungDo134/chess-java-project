package chess_project.main;

import chess_project.piece.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class GamePanel extends JPanel implements Runnable {
    public static final int WIDTH = 1100;
    public static final int HEIGHT = 800;
    final int FPS = 60;
    Thread gameThread;
    Board board = new Board();
    Mouse mouse = new Mouse();

    // PIECES
    public static ArrayList<Piece> pieces = new ArrayList<>();
    public static ArrayList<Piece> simPieces = new ArrayList<>();
    Piece activeP;

    // COLOR
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    int currentColor = WHITE;

    // BOOLEANS
    boolean canMove;
    boolean validSquare;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.black);
        addMouseMotionListener(mouse);
        addMouseListener(mouse);

        setPieces();
        copyPieces(pieces, simPieces);
    }

    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void setPieces() {

        // White team
        pieces.add(new Pawn(WHITE, 0, 6));
        pieces.add(new Pawn(WHITE, 1, 6));
        pieces.add(new Pawn(WHITE, 2, 6));
        pieces.add(new Pawn(WHITE, 3, 6));
        pieces.add(new Pawn(WHITE, 4, 6));
        pieces.add(new Pawn(WHITE, 5, 6));
        pieces.add(new Pawn(WHITE, 6, 6));
        pieces.add(new Pawn(WHITE, 7, 6));
        pieces.add(new Rook(WHITE, 0, 7));
        pieces.add(new Rook(WHITE, 7, 7));
//        pieces.add(new Knight(WHITE, 1, 7));
        pieces.add(new Knight(WHITE, 4, 4));
        pieces.add(new Knight(WHITE, 6, 7));
        pieces.add(new Bishop(WHITE, 2, 7));
        pieces.add(new Bishop(WHITE, 5, 7));
        pieces.add(new Queen(WHITE, 3, 7));
        pieces.add(new King(WHITE, 4, 7));


        // Black team
        pieces.add(new Pawn(BLACK, 0, 1));
        pieces.add(new Pawn(BLACK, 1, 1));
        pieces.add(new Pawn(BLACK, 2, 1));
        pieces.add(new Pawn(BLACK, 3, 1));
        pieces.add(new Pawn(BLACK, 4, 1));
        pieces.add(new Pawn(BLACK, 5, 1));
        pieces.add(new Pawn(BLACK, 6, 1));
        pieces.add(new Pawn(BLACK, 7, 1));
        pieces.add(new Rook(BLACK, 0, 0));
        pieces.add(new Rook(BLACK, 7, 0));
        pieces.add(new Knight(BLACK, 1, 0));
        pieces.add(new Knight(BLACK, 6, 0));
        pieces.add(new Bishop(BLACK, 2, 0));
        pieces.add(new Bishop(BLACK, 5, 0));
        pieces.add(new Queen(BLACK, 3, 0));
        pieces.add(new King(BLACK, 4, 0));
    }

    public void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target) {
        target.clear();
        target.addAll(source);
    }

    @Override
    public void run() {
        // Game loop
        /*
        - Process run continuously as long as the game running
        - Use nanoTIme() to measure call update and repaint methods once every 1/60 of a second
         */
        double drawInterval = (double) 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;
            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    // update chess, pos x/y of chess
    private void update() {

        // MOUSE BUTTON PRESSED //
        if (mouse.pressed) {
            if (activeP == null) {
                // If activeP is null, check if you can pick up the piece
                for (Piece piece : simPieces) {
                    // If the mouse is ally the piece, pick it up as the activeP
                    if (piece.color == currentColor
                            && piece.col == mouse.x / Board.SQUARE_SIZE
                            && piece.row == mouse.y / Board.SQUARE_SIZE) {

                        activeP = piece;
                    }
                }
            } else {
                // If the player is holding the piece, simulate the move (Thinking phases)
                simulate();
            }
        }

        // MOUSE BUTTON RELEASED //
        if (!mouse.pressed) {
            if (activeP != null) {
                if (validSquare) {
                    // MOVE CONFIRMED

                    //Update the piece list in case a piece has been captured and removed during the simulation
                    copyPieces(simPieces, pieces);
                    activeP.updatePosition();
                } else {

                    // The move is not valid so reset everything
                    copyPieces(simPieces, pieces);
                    activeP.resetPosition();
                    activeP = null;
                }
            }
        }
    }

    private void simulate() {

        canMove = false;
        validSquare = false;
        // Reset the piece list in every loop
        // This is basically for restoring the removed piece during the simulation
        copyPieces(pieces, simPieces);

        // If the piece is being held, update its pos
        activeP.x = mouse.x - Board.HALF_SQUARE_SIZE; //  make mouse center a piece
        activeP.y = mouse.y - Board.HALF_SQUARE_SIZE; //  make mouse center a piece
        activeP.col = activeP.getCol(activeP.x);
        activeP.row = activeP.getRow(activeP.y);

        // Check if the piece is hovering over a reachable square
        if (activeP.canMove(activeP.col, activeP.row)) {
            canMove = true;

            // If hitting the piece, remove it from the list
            if (activeP.hittingP != null) simPieces.remove(activeP.hittingP.getIndex());

            validSquare = true;
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        // BOARD
        board.draw(g2);

        // PIECES
        for (Piece p : simPieces) {
            p.draw(g2);
        }
        if (activeP != null) {
            if (canMove) {
                g2.setColor(Color.white);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                g2.fillRect(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }

            // Draw the active piece in the end so it won't be hidden (look like hover when hold a piece and moves it)
            activeP.draw(g2);
        }
    }


}
