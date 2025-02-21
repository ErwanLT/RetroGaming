package fr.eletutour.tetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Random;

public class Tetris extends JPanel {
    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 20;
    private static final int BLOCK_SIZE = 30;

    private final Color[][] board;  // Changé en Color[][] pour stocker les couleurs
    private Tetromino currentPiece;
    private Tetromino nextPiece;
    private final Timer timer;
    private boolean gameOver = false;
    private boolean gameStarted = false;
    private int score = 0;
    private int level = 1;
    private int linesCleared = 0;

    private static final Color[] COLORS = {
            Color.CYAN, Color.BLUE, Color.ORANGE, Color.YELLOW,
            Color.GREEN, Color.MAGENTA, Color.RED
    };

    private static final int[][][] SHAPES = {
            {{1, 1, 1, 1}}, // I
            {{1, 1, 1}, {0, 0, 1}}, // J
            {{1, 1, 1}, {1, 0, 0}}, // L
            {{1, 1}, {1, 1}}, // O
            {{0, 1, 1}, {1, 1, 0}}, // S
            {{1, 1, 1}, {0, 1, 0}}, // T
            {{1, 1, 0}, {0, 1, 1}}  // Z
    };

    class Tetromino {
        int[][] shape;
        int x, y;
        Color color;

        Tetromino(int type) {
            shape = SHAPES[type];
            color = COLORS[type];
            x = BOARD_WIDTH / 2 - shape[0].length / 2;
            y = 0;
        }

        void move(int dx, int dy) {
            x += dx;
            y += dy;
        }

        void rotate() {
            int[][] newShape = new int[shape[0].length][shape.length];
            for (int i = 0; i < shape.length; i++) {
                for (int j = 0; j < shape[0].length; j++) {
                    newShape[j][shape.length - 1 - i] = shape[i][j];
                }
            }
            if (isValidPosition(newShape, x, y)) {
                shape = newShape;
            }
        }
    }

    public Tetris() {
        board = new Color[BOARD_HEIGHT][BOARD_WIDTH];  // Tableau de couleurs
        Arrays.stream(board).forEach(row -> Arrays.fill(row, null)); // Initialisation à null
        setPreferredSize(new Dimension(BOARD_WIDTH * BLOCK_SIZE + 150, BOARD_HEIGHT * BLOCK_SIZE));
        setFocusable(true);

        timer = new Timer(500, e -> {
            if (gameStarted && !gameOver) {
                moveDown();
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!gameStarted) {
                    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                        startGame();
                    }
                    return;
                }
                if (gameOver) return;

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT -> movePiece(-1, 0);
                    case KeyEvent.VK_RIGHT -> movePiece(1, 0);
                    case KeyEvent.VK_DOWN -> moveDown();
                    case KeyEvent.VK_UP -> currentPiece.rotate();
                }
                repaint();
            }
        });
    }

    private void startGame() {
        gameStarted = true;
        score = 0;
        level = 1;
        linesCleared = 0;
        Arrays.stream(board).forEach(row -> Arrays.fill(row, null));
        spawnPiece();
        timer.start();
        repaint();
    }

    private void spawnPiece() {
        Random rand = new Random();
        if (nextPiece == null) {
            nextPiece = new Tetromino(rand.nextInt(SHAPES.length));
        }
        currentPiece = nextPiece;
        nextPiece = new Tetromino(rand.nextInt(SHAPES.length));

        if (!isValidPosition(currentPiece.shape, currentPiece.x, currentPiece.y)) {
            gameOver = true;
            timer.stop();
        }
    }

    private boolean isValidPosition(int[][] shape, int x, int y) {
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[0].length; j++) {
                if (shape[i][j] == 0) continue;

                int newX = x + j;
                int newY = y + i;

                if (newX < 0 || newX >= BOARD_WIDTH || newY >= BOARD_HEIGHT ||
                        (newY >= 0 && board[newY][newX] != null)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void movePiece(int dx, int dy) {
        if (isValidPosition(currentPiece.shape, currentPiece.x + dx, currentPiece.y + dy)) {
            currentPiece.move(dx, dy);
        }
    }

    private void moveDown() {
        if (isValidPosition(currentPiece.shape, currentPiece.x, currentPiece.y + 1)) {
            currentPiece.move(0, 1);
        } else {
            placePiece();
            int lines = clearLines();
            updateScore(lines);
            spawnPiece();
        }
        repaint();
    }

    private void placePiece() {
        for (int i = 0; i < currentPiece.shape.length; i++) {
            for (int j = 0; j < currentPiece.shape[0].length; j++) {
                if (currentPiece.shape[i][j] != 0) {
                    board[currentPiece.y + i][currentPiece.x + j] = currentPiece.color;
                }
            }
        }
    }

    private int clearLines() {
        int lines = 0;
        for (int i = BOARD_HEIGHT - 1; i >= 0; i--) {
            boolean full = true;
            for (int j = 0; j < BOARD_WIDTH; j++) {
                if (board[i][j] == null) {
                    full = false;
                    break;
                }
            }
            if (full) {
                lines++;
                for (int k = i; k > 0; k--) {
                    System.arraycopy(board[k-1], 0, board[k], 0, BOARD_WIDTH);
                }
                Arrays.fill(board[0], null);
                i++;
            }
        }
        return lines;
    }

    private void updateScore(int lines) {
        linesCleared += lines;
        switch (lines) {
            case 1 -> score += 40 * level;
            case 2 -> score += 100 * level;
            case 3 -> score += 300 * level;
            case 4 -> score += 1200 * level;
        }
        level = 1 + linesCleared / 10;
        timer.setDelay(Math.max(100, 500 - (level - 1) * 40));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Fond du plateau de jeu (gris foncé)
        g.setColor(new Color(50, 50, 50));
        g.fillRect(0, 0, BOARD_WIDTH * BLOCK_SIZE, BOARD_HEIGHT * BLOCK_SIZE);

        // Fond du panneau d'info (gris plus clair)
        g.setColor(new Color(80, 80, 80));
        g.fillRect(BOARD_WIDTH * BLOCK_SIZE, 0, 150, BOARD_HEIGHT * BLOCK_SIZE);

        if (!gameStarted) {
            g.setColor(Color.WHITE);
            g.drawString("Press SPACE to start", BOARD_WIDTH * BLOCK_SIZE / 2 - 50, BOARD_HEIGHT * BLOCK_SIZE / 2);
            return;
        }

        // Dessiner le plateau avec les couleurs des pièces posées
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                if (board[i][j] != null) {
                    g.setColor(board[i][j]);
                    g.fillRect(j * BLOCK_SIZE, i * BLOCK_SIZE, BLOCK_SIZE - 1, BLOCK_SIZE - 1);
                }
            }
        }

        // Dessiner la pièce courante
        g.setColor(currentPiece.color);
        for (int i = 0; i < currentPiece.shape.length; i++) {
            for (int j = 0; j < currentPiece.shape[0].length; j++) {
                if (currentPiece.shape[i][j] != 0) {
                    g.fillRect((currentPiece.x + j) * BLOCK_SIZE,
                            (currentPiece.y + i) * BLOCK_SIZE,
                            BLOCK_SIZE - 1, BLOCK_SIZE - 1);
                }
            }
        }

        // Dessiner la prochaine pièce
        g.setColor(Color.WHITE);
        g.drawString("Next:", BOARD_WIDTH * BLOCK_SIZE + 10, 20);
        g.setColor(nextPiece.color);
        for (int i = 0; i < nextPiece.shape.length; i++) {
            for (int j = 0; j < nextPiece.shape[0].length; j++) {
                if (nextPiece.shape[i][j] != 0) {
                    g.fillRect(BOARD_WIDTH * BLOCK_SIZE + 10 + j * BLOCK_SIZE,
                            30 + i * BLOCK_SIZE,
                            BLOCK_SIZE - 1, BLOCK_SIZE - 1);
                }
            }
        }

        // Afficher le score et le niveau
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, BOARD_WIDTH * BLOCK_SIZE + 10, 100);
        g.drawString("Level: " + level, BOARD_WIDTH * BLOCK_SIZE + 10, 120);
        g.drawString("Lines: " + linesCleared, BOARD_WIDTH * BLOCK_SIZE + 10, 140);

        if (gameOver) {
            g.setColor(Color.RED);
            g.drawString("Game Over", BOARD_WIDTH * BLOCK_SIZE / 2 - 30, BOARD_HEIGHT * BLOCK_SIZE / 2);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Tetris");
        Tetris game = new Tetris();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}