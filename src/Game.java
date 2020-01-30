import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.Timer;

public class Game extends JFrame implements MouseWheelListener, MouseListener {
    private static final long serialVersionUID = -5670525369779426437L;

    JComboBox<String> presetDropdown = new JComboBox<String>(Presets.PRESETS);
    JButton frameButton = new JButton("Frame");
    JButton resetButton = new JButton("Reset");
    JButton playPauseButton = new JButton("Play");
    JButton paintButton = new JButton("Paint");
    JButton skipButton = new JButton("Skip");
    JSlider speedSlider = new JSlider(0, 1, 500, 250);
    JTextField typingArea;
    Panel panel = new Panel();
    Timer timer;

    private int[] factors = Presets.FACTORS;
    private int xPos = 0;
    private int yPos = 0;
    private int initialX = 0;
    private int initialY = 0;
    private int endX = 0;
    private int endY = 0;
    private int zoomValue = 0;
    private int entireBoardHeight = 840;
    private int entireBoardLength = 840;
    private int proportionOfAlive = 5;
    private int notches = 0;
    private boolean paused = true;
    private boolean painting = false;
    private boolean started = false;
    private boolean[][] board1 = new boolean[entireBoardHeight][entireBoardLength];
    private boolean[][] board2 = new boolean[entireBoardHeight][entireBoardLength];
    private boolean myOneLineBCD = true;

    public Game() {
        super("Game of Life");
        buttonSetup();
        initializeBoard(board1);
        setupWindow();
    }

    public void updateBoard() {
        if (myOneLineBCD) {
            copyBoard(board1, board2);
            for (int y = 0; y < entireBoardLength; y++) {
                for (int x = 0; x < entireBoardLength; x++) {
                    int neighbors = getNumberAlive(x, y, board1);
                    if (neighbors == 3) {
                        board2[y][x] = true;
                    }
                    if (neighbors < 2) {
                        board2[y][x] = false;
                    }
                    if (neighbors > 3) {
                        board2[y][x] = false;
                    }
                }
            }
            copyBoard(board2, board1);
            panel.repaint();
            myOneLineBCD = false;
        } else {
            copyBoard(board2, board1);
            for (int y = 0; y < entireBoardLength; y++) {
                for (int x = 0; x < entireBoardLength; x++) {
                    int neighbors = getNumberAlive(x, y, board2);
                    if (neighbors == 3) {
                        board1[y][x] = true;
                    }
                    if (neighbors < 2) {
                        board1[y][x] = false;
                    }
                    if (neighbors > 3) {
                        board1[y][x] = false;
                    }
                }
            }
            copyBoard(board1, board2);
            panel.repaint();
            myOneLineBCD = true;
        }

    }

    public void drawBoard() {
        myOneLineBCD = true;
        for (int y = 0; y < entireBoardHeight; y++) {
            for (int x = 0; x < entireBoardLength; x++) {
                if ((int) (Math.random() * proportionOfAlive + 1) == 1) {
                    board1[y][x] = true;
                } else {
                    board1[y][x] = false;
                }
            }
        }
    }

    public void drawBoard(int[] preset) {
        boolean[][] nextBoard = new boolean[entireBoardHeight][entireBoardLength];
        initializeBoard(nextBoard);

        int presetLength = (int) (Math.sqrt(preset.length));
        int counter = 0;
        int offset = (entireBoardLength / 2) - presetLength / 2;

        for (int y = 0; y < presetLength; y++) {
            for (int x = 0; x < presetLength; x++) {
                if (preset[counter] == 1) {
                    nextBoard[offset + y][offset + x] = true;
                }
                counter++;
            }
        }
        board1 = nextBoard;
        myOneLineBCD = true;
    }

    public void copyBoard(boolean[][] board1, boolean[][] board2) {
        for (int x = 0; x < board1.length; x++) {
            for (int y = 0; y < board1.length; y++) {
                board2[y][x] = board1[y][x];
            }
        }
    }

    public void initializeBoard(boolean[][] board) {
        for (int y = 0; y < entireBoardHeight; y++) {
            for (int x = 0; x < entireBoardLength; x++) {
                board[y][x] = false;
            }
        }
    }

    public int[] hardCode(int x) {
        int[] hardCode = new int[x * x];
        for (int i = 0; i < x * x; i++) {
            if (i % 2 == 0) {
                hardCode[i] = 1;
            } else {
                hardCode[i] = 0;
            }
        }
        return hardCode;
    }

    public void parseComboBox() {
        initializeBoard(board1);
        String str = (String) presetDropdown.getSelectedItem();
        if (str.equals("Random")) {
            drawBoard();
        } else if (str.equals("Acorn")) {
            drawBoard(Presets.ACORN);
        } else if (str.equals("R")) {
            drawBoard(Presets.R);
        } else if (str.equals("Die Hard")) {
            drawBoard(Presets.DIE_HARD);
        } else if (str.equals("Pentadecathlon")) {
            drawBoard(Presets.PENTADECATHLON);
        } else if (str.equals("Pulsar")) {
            drawBoard(Presets.PULSAR);
        } else if (str.equals("Gosper Glider Gun")) {
            drawBoard(Presets.GOSPER_GLIDER_GUN);
        } else if (str.equals("Cloverleaf")) {
            drawBoard(Presets.CLOVER_LEAF);
        } else if (str.equals("Clock")) {
            drawBoard(Presets.CLOCK);
        } else if (str.equals("Large")) {
            drawBoard(Presets.LARGE);
        } else if (str.equals("High Life")) {
            drawBoard(Presets.HIGH_LIFE);
        } else if (str.equals("Figure 8")) {
            drawBoard(Presets.FIGURE_8);
        } else if (str.equals("Checkerboard")) {
            drawBoard(hardCode(835));
        }
        myOneLineBCD = true;
    }

    public void setupWindow() {
        setSize(entireBoardHeight + 5, entireBoardLength + 28);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, 0);
        setVisible(true);
    }

    public int getNumberAlive(int x, int y, boolean[][] entireBoard) {
        int aliveNeighbors = 0;
        if (checkInBounds(x, y - 1) && entireBoard[y - 1][x]) { // top
            aliveNeighbors++;
        }
        if (checkInBounds(x + 1, y - 1) && entireBoard[y - 1][x + 1]) { // top
            aliveNeighbors++;
        }
        if (checkInBounds(x + 1, y) && entireBoard[y][x + 1]) { // top
            aliveNeighbors++;
        }
        if (checkInBounds(x + 1, y + 1) && entireBoard[y + 1][x + 1]) { // top
            aliveNeighbors++;
        }
        if (checkInBounds(x, y + 1) && entireBoard[y + 1][x]) { // top
            aliveNeighbors++;
        }
        if (checkInBounds(x - 1, y + 1) && entireBoard[y + 1][x - 1]) { // top
            aliveNeighbors++;
        }
        if (checkInBounds(x - 1, y) && entireBoard[y][x - 1]) { // top
            aliveNeighbors++;
        }
        if (checkInBounds(x - 1, y - 1) && entireBoard[y - 1][x - 1]) { // top
            aliveNeighbors++;
        }
        return aliveNeighbors;
    }

    public boolean[][] getBoard() {
        if (myOneLineBCD) {
            return board1;
        } else {
            return board2;
        }
    }

    public int getZoomValue() {
        return factors[zoomValue];
    }

    public int getLength() {
        return entireBoardLength;
    }

    public void changeAlive(int x, int y, boolean[][] entireBoard) {
        entireBoard[y][x] = !entireBoard[y][x];
    }

    public void constrain() {
        int zoom = factors[zoomValue];
        int limit = (entireBoardHeight - (entireBoardHeight / zoom)) / 2;
        int lowerBorder = -1 * limit;
        int upperBorder = limit;

        if (xPos > upperBorder) {
            xPos = upperBorder;
        } else if (xPos <= lowerBorder) {
            xPos = lowerBorder;
        }

        if (yPos > upperBorder) {
            yPos = upperBorder;
        } else if (yPos < lowerBorder) {
            yPos = lowerBorder;
        }
    }

    private boolean checkInBounds(int x, int y) {
        return ((x >= 0) && (x < entireBoardLength)) && ((y >= 0) && (y < entireBoardHeight));
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        notches -= e.getWheelRotation();
        if (notches < 0) {
            notches = 0;
        } else if (notches > factors.length - 1) {
            notches = factors.length - 1;
        }
        zoomValue = notches;
        constrain();
        panel.repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (painting) {
            paused = true;
            playPauseButton.setText("Play");
            int y = e.getY() / factors[zoomValue];
            int x = e.getX() / factors[zoomValue];
            int offset = entireBoardLength / factors[zoomValue] / 2;
            if (myOneLineBCD) {
                changeAlive(x + xPos + entireBoardLength / 2 - offset, y + yPos + entireBoardLength / 2 - offset,
                        board1);
            } else {
                changeAlive(x + xPos + entireBoardLength / 2 - offset, y + yPos + entireBoardLength / 2 - offset,
                        board2);
            }
            constrain();
            repaint();
        } else {
            endY = e.getY() / factors[zoomValue];
            endX = e.getX() / factors[zoomValue];
            initialY = e.getY() / factors[zoomValue];
            initialX = e.getX() / factors[zoomValue];
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        endY = e.getY() / factors[zoomValue];
        endX = e.getX() / factors[zoomValue];
        initialY = e.getY() / factors[zoomValue];
        initialX = e.getX() / factors[zoomValue];
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    public void buttonSetup() {
        skipButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < 100; i ++) {
                    updateBoard();
                }
                constrain();
                panel.repaint();
            }
        });
        frameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (paused) {
                    updateBoard();
                } else {
                    paused = true;
                    playPauseButton.setText("Play");
                }
            }
        });
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parseComboBox();
                constrain();
                panel.repaint();
                if (painting) {
                    paused = true;
                    playPauseButton.setText("Play");
                }
            }
        });
        playPauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                paused = !paused;
                if (paused) {
                    playPauseButton.setText("Play");
                } else {
                    playPauseButton.setText("Pause");
                }
            }
        });
        paintButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!painting) {
                    paused = true;
                }
                painting = !painting;
                presetDropdown.setVisible(!painting);
                speedSlider.setVisible(!painting);
                playPauseButton.setText("Play");
            }
        });
        presetDropdown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parseComboBox();
                constrain();
                panel.repaint();

            }
        });
        timer = new Timer(1, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (!paused) {
                    timer.setDelay(speedSlider.getValue());
                    updateBoard();
                }
            }
        });
        setupKeyListeners();
        timer.start();
        playPauseButton.setPreferredSize(new Dimension(70, 26));
        panel.add(speedSlider);
        panel.add(frameButton);
        panel.add(playPauseButton);
        panel.add(resetButton);
        panel.add(paintButton);
        panel.add(presetDropdown);
        panel.add(skipButton);
        panel.addMouseWheelListener(this);
        panel.addMouseListener(this);
        setFocusable(true);
        add(panel);
    }

    public void setupKeyListeners() {
        KeyListener keyListener = new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                playPauseButton.requestFocus();
                int code = e.getKeyCode();
                if (code == 37) {
                    xPos -= 84/factors[zoomValue] + 1;
                } else if (code == 38) {
                    yPos -= 84/factors[zoomValue] + 1;
                } else if (code == 39) {
                    xPos+= 84/factors[zoomValue] + 1;
                } else if (code == 40) {
                    yPos+= 84/factors[zoomValue] + 1;
                }
                constrain();
                panel.repaint();
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }
        };
        panel.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (painting) {
                    paused = true;
                    playPauseButton.setText("Play");
                    int y = e.getY() / factors[zoomValue];
                    int x = e.getX() / factors[zoomValue];
                    int offset = entireBoardLength / factors[zoomValue] / 2;
                    if (myOneLineBCD) {
                        board1[y + yPos + entireBoardLength / 2 - offset][x + xPos + entireBoardLength / 2
                                - offset] = true;
                    } else {
                        board2[y + yPos + entireBoardLength / 2 - offset][x + xPos + entireBoardLength / 2
                                - offset] = true;
                    }
                    constrain();
                    repaint();
                } else {
                    if (started == false) {
                        endY = e.getY() / factors[zoomValue];
                        endX = e.getX() / factors[zoomValue];
                        started = true;
                    } else if (started) {
                        initialY = endY;
                        initialX = endX;
                        endY = e.getY() / factors[zoomValue];
                        endX = e.getX() / factors[zoomValue];
                        xPos += -(endX - initialX);
                        yPos += -(endY - initialY);
                    }
                    constrain();
                    panel.repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {

            }
        });
        frameButton.addKeyListener(keyListener);
        resetButton.addKeyListener(keyListener);
        playPauseButton.addKeyListener(keyListener);
        presetDropdown.addKeyListener(keyListener);
        speedSlider.addKeyListener(keyListener);
        paintButton.addKeyListener(keyListener);
    }

}


