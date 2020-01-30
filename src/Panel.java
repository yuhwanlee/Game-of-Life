import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

public class Panel extends JPanel {
    private static final long serialVersionUID = -7688638666699701851L;
    static boolean[][] board;
    static int zoom;
    static int length;
    static Game game;

    public static void main(String[] args) {
        game = new Game();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(Color.white);
        g.setColor(Color.black);

        board = game.getBoard();
        zoom = game.getZoomValue();
        length = game.getLength();
        int currentBoardHeight = (int) (length / zoom);
        int offset = (int) ((length - currentBoardHeight) / 2);
        int xPos = game.getXPos();
        int yPos = game.getYPos();

        for (int y =  offset ; y < length - offset; y++) {
            for (int x =  offset ; x < length - offset; x++) {
                if (board[y + yPos][x + xPos]) {
                    g.fillRect(x * zoom - offset * zoom, y * zoom - offset * zoom, zoom, zoom);

                }
            }
        }
    }
}


