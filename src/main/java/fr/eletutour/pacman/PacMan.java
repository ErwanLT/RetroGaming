package fr.eletutour.pacman;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class PacMan extends JFrame {
    private static final int TILE_SIZE = 20;
    private static final int GAME_WIDTH = 400;
    private static final int GAME_HEIGHT = 420; // Corrigé à 420 pour 21 lignes
    private static final int INFO_HEIGHT = 30;

    public PacMan() {
        setTitle("Pac-Man");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        InfoPanel infoPanel = new InfoPanel();
        add(infoPanel, BorderLayout.NORTH);

        GamePanel gamePanel = new GamePanel(infoPanel);
        add(gamePanel, BorderLayout.CENTER);

        setSize(GAME_WIDTH, GAME_HEIGHT + INFO_HEIGHT);
        setVisible(true);
    }

    static class InfoPanel extends JPanel {
        private final JLabel scoreLabel;
        private final JLabel livesLabel;

        public InfoPanel() {
            setPreferredSize(new Dimension(GAME_WIDTH, INFO_HEIGHT));
            setBackground(Color.BLACK);
            setLayout(new FlowLayout(FlowLayout.LEFT));

            scoreLabel = new JLabel("Score: 0");
            scoreLabel.setForeground(Color.WHITE);
            scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
            add(scoreLabel);

            livesLabel = new JLabel("Lives: 3");
            livesLabel.setForeground(Color.WHITE);
            livesLabel.setFont(new Font("Arial", Font.BOLD, 16));
            add(livesLabel);
        }

        public void update(int score, int lives) {
            scoreLabel.setText("Score: " + score);
            livesLabel.setText("Lives: " + lives);
        }
    }

    static class GamePanel extends JPanel {
        private final int[][] initialMaze = {
                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                {1,2,2,2,2,2,2,2,2,1,1,2,2,2,2,2,2,2,2,1},
                {1,2,1,1,1,1,2,1,2,1,1,2,1,2,1,1,1,1,2,1},
                {1,2,1,0,0,1,2,1,2,2,2,2,1,2,1,0,0,1,2,1},
                {1,4,1,1,1,1,2,1,1,1,1,1,1,2,1,1,1,1,4,1},
                {1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1},
                {1,2,1,1,1,1,2,1,1,1,1,1,1,2,1,1,1,1,2,1},
                {1,2,1,2,2,2,2,1,2,2,2,2,1,2,2,2,2,1,2,1},
                {1,2,1,1,1,1,2,1,1,0,0,1,1,2,1,1,1,1,2,1},
                {1,2,2,2,2,1,2,0,0,0,0,0,0,2,1,2,2,2,2,1},
                {1,1,1,1,2,1,2,1,1,0,0,1,1,2,1,2,1,1,1,1},
                {1,0,0,1,2,1,2,1,1,1,1,1,1,2,1,2,1,0,0,1},
                {1,1,1,1,2,1,2,2,2,2,2,2,2,2,1,2,1,1,1,1},
                {1,2,2,2,2,2,2,1,1,1,1,1,1,2,2,2,2,2,2,1},
                {1,2,1,1,1,1,2,1,2,1,1,2,1,2,1,1,1,1,2,1},
                {1,2,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,2,1},
                {1,2,1,1,2,1,1,1,1,1,1,1,1,1,1,2,1,1,2,1},
                {1,2,2,2,2,2,2,2,2,1,1,2,2,2,2,2,2,2,2,1},
                {1,2,1,1,1,1,1,1,2,1,1,2,1,1,1,1,1,1,2,1},
                {1,4,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,4,1},
                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
        };

        private final int[][] maze;
        private int pacX = 9;
        private int pacY = 15;
        private int score = 0;
        private int lives = 3;
        private boolean mouthOpen = true;
        private final ArrayList<Ghost> ghosts = new ArrayList<>();
        private int fruitX = -1;
        private int fruitY = -1;
        private int fruitTimer = 0;
        private final Random rand = new Random();
        private final InfoPanel infoPanel;
        private String currentFruitEmoji;
        private final String[] fruitEmojis = {"🍒", "🍓", "🍎", "🍌", "🍊"};
        private boolean ghostsVulnerable = false;
        private int vulnerableTimer = 0;

        class Ghost {
            int x, y;
            String emoji;
            boolean isDead = false;
            int respawnTimer = 0;
            private static final String NORMAL_EMOJI = "👻";
            private static final String VULNERABLE_EMOJI = "💀";
            private static final int RESPAWN_DELAY = 15;

            Ghost(int x, int y) {
                this.x = x;
                this.y = y;
                this.emoji = NORMAL_EMOJI;
            }

            void setVulnerable(boolean vulnerable) {
                if (!isDead) {
                    this.emoji = vulnerable ? VULNERABLE_EMOJI : NORMAL_EMOJI;
                }
            }

            void move() {
                if (isDead) {
                    respawnTimer--;
                    if (respawnTimer <= 0) {
                        isDead = false;
                        setVulnerable(ghostsVulnerable);
                    }
                    return;
                }

                if (ghostsVulnerable) {
                    int dx = Integer.compare(pacX, x) * -1;
                    int dy = Integer.compare(pacY, y) * -1;
                    if (rand.nextBoolean() && canMove(x + dx, y)) {
                        x += dx;
                    } else if (canMove(x, y + dy)) {
                        y += dy;
                    }
                } else {
                    int dx = Integer.compare(pacX, x);
                    int dy = Integer.compare(pacY, y);
                    if (rand.nextInt(100) < 70) {
                        if (rand.nextBoolean() && canMove(x + dx, y)) {
                            x += dx;
                        } else if (canMove(x, y + dy)) {
                            y += dy;
                        }
                    } else {
                        int direction = rand.nextInt(4);
                        switch (direction) {
                            case 0: if (canMove(x, y-1)) y--; break;
                            case 1: if (canMove(x, y+1)) y++; break;
                            case 2: if (canMove(x-1, y)) x--; break;
                            case 3: if (canMove(x+1, y)) x++; break;
                        }
                    }
                }
            }

            void die() {
                isDead = true;
                respawnTimer = RESPAWN_DELAY;
                x = 9 + (ghosts.indexOf(this) % 2);
                y = 9;
                emoji = NORMAL_EMOJI;
            }

            boolean canMove(int newX, int newY) {
                return newX >= 0 && newX < maze[0].length && newY >= 0 && newY < maze.length && maze[newY][newX] != 1;
            }
        }

        public GamePanel(InfoPanel infoPanel) {
            this.infoPanel = infoPanel;
            setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
            setBackground(Color.BLACK);

            maze = new int[initialMaze.length][initialMaze[0].length];
            resetLevel();

            addKeyListener(new KeyListener() {
                @Override
                public void keyPressed(KeyEvent e) {
                    movePacMan(e.getKeyCode());
                    repaint();
                }
                @Override
                public void keyTyped(KeyEvent e) {}
                @Override
                public void keyReleased(KeyEvent e) {}
            });

            Timer timer = new Timer(200, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mouthOpen = !mouthOpen;
                    for (Ghost ghost : ghosts) {
                        ghost.move();
                    }
                    updateFruit();
                    checkCollision();
                    checkLevelComplete(); // Ajouté ici pour s'assurer qu'il est appelé
                    if (ghostsVulnerable) {
                        vulnerableTimer--;
                        if (vulnerableTimer <= 0) {
                            ghostsVulnerable = false;
                            for (Ghost ghost : ghosts) {
                                if (!ghost.isDead) {
                                    ghost.setVulnerable(false);
                                }
                            }
                        }
                    }
                    repaint();
                }
            });
            timer.start();

            setFocusable(true);
            requestFocusInWindow();
        }

        private void movePacMan(int keyCode) {
            int newX = pacX;
            int newY = pacY;

            switch (keyCode) {
                case KeyEvent.VK_UP: newY--; break;
                case KeyEvent.VK_DOWN: newY++; break;
                case KeyEvent.VK_LEFT: newX--; break;
                case KeyEvent.VK_RIGHT: newX++; break;
            }

            if (newX < 0) newX = maze[0].length - 1;
            if (newX >= maze[0].length) newX = 0;

            if (maze[newY][newX] != 1) {
                pacX = newX;
                pacY = newY;
                if (maze[pacY][pacX] == 2) {
                    score += 10;
                    maze[pacY][pacX] = 0;
                    infoPanel.update(score, lives);
                } else if (maze[pacY][pacX] == 3) {
                    score += 50;
                    maze[pacY][pacX] = 0;
                    fruitX = -1;
                    fruitY = -1;
                    currentFruitEmoji = null;
                    infoPanel.update(score, lives);
                } else if (maze[pacY][pacX] == 4) {
                    score += 50;
                    maze[pacY][pacX] = 0;
                    ghostsVulnerable = true;
                    vulnerableTimer = 30;
                    for (Ghost ghost : ghosts) {
                        if (!ghost.isDead) {
                            ghost.setVulnerable(true);
                        }
                    }
                    infoPanel.update(score, lives);
                }
            }
        }

        private void updateFruit() {
            fruitTimer++;
            if (fruitX == -1 && fruitTimer > 50 && rand.nextInt(100) < 5) {
                do {
                    fruitX = rand.nextInt(maze[0].length);
                    fruitY = rand.nextInt(maze.length);
                } while (maze[fruitY][fruitX] != 0 && maze[fruitY][fruitX] != 2);
                maze[fruitY][fruitX] = 3;
                currentFruitEmoji = fruitEmojis[rand.nextInt(fruitEmojis.length)];
                fruitTimer = 0;
            }
        }

        private void checkCollision() {
            for (Ghost ghost : ghosts) {
                if (ghost.x == pacX && ghost.y == pacY && !ghost.isDead) {
                    if (ghostsVulnerable) {
                        score += 200;
                        ghost.die();
                        infoPanel.update(score, lives);
                    } else {
                        lives--;
                        infoPanel.update(score, lives);
                        if (lives <= 0) {
                            JOptionPane.showMessageDialog(this, "Game Over! Score: " + score);
                            System.exit(0);
                        } else {
                            pacX = 9;
                            pacY = 15;
                            ghost.x = 9 + (ghosts.indexOf(ghost) % 2);
                            ghost.y = 9;
                        }
                    }
                }
            }
        }

        private void checkLevelComplete() {
            boolean pointsLeft = false;
            for (int y = 0; y < maze.length; y++) {
                for (int x = 0; x < maze[y].length; x++) {
                    if (maze[y][x] == 2 || maze[y][x] == 4) {
                        pointsLeft = true;
                        break;
                    }
                }
                if (pointsLeft) break;
            }

            if (!pointsLeft) {
                JOptionPane.showMessageDialog(this, "Niveau terminé ! Score: " + score);
                resetLevel();
            }
        }

        private void resetLevel() {
            for (int y = 0; y < initialMaze.length; y++) {
                System.arraycopy(initialMaze[y], 0, maze[y], 0, initialMaze[y].length);
            }
            pacX = 9;
            pacY = 15;
            ghosts.clear();
            ghosts.add(new Ghost(9, 9));
            ghosts.add(new Ghost(10, 9));
            fruitX = -1;
            fruitY = -1;
            currentFruitEmoji = null;
            fruitTimer = 0;
            ghostsVulnerable = false;
            vulnerableTimer = 0;
            infoPanel.update(score, lives);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            for (int y = 0; y < maze.length; y++) {
                for (int x = 0; x < maze[y].length; x++) {
                    if (maze[y][x] == 1) {
                        g.setColor(Color.BLUE);
                        g.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    } else if (maze[y][x] == 2) {
                        g.setColor(Color.WHITE);
                        g.fillOval(x * TILE_SIZE + 8, y * TILE_SIZE + 8, 4, 4);
                    } else if (maze[y][x] == 3 && currentFruitEmoji != null) {
                        g.setColor(Color.RED);
                        g.setFont(new Font("Arial", Font.PLAIN, TILE_SIZE));
                        g.drawString(currentFruitEmoji, x * TILE_SIZE, (y + 1) * TILE_SIZE);
                    } else if (maze[y][x] == 4) {
                        g.setColor(Color.WHITE);
                        g.fillOval(x * TILE_SIZE + 6, y * TILE_SIZE + 6, 8, 8);
                    }
                }
            }

            for (Ghost ghost : ghosts) {
                if (!ghost.isDead) {
                    g.setColor(Color.WHITE);
                    g.setFont(new Font("Arial", Font.PLAIN, TILE_SIZE));
                    g.drawString(ghost.emoji, ghost.x * TILE_SIZE, (ghost.y + 1) * TILE_SIZE);
                }
            }

            g.setColor(Color.YELLOW);
            if (mouthOpen) {
                g.fillArc(pacX * TILE_SIZE, pacY * TILE_SIZE, TILE_SIZE, TILE_SIZE, 45, 270);
            } else {
                g.fillOval(pacX * TILE_SIZE, pacY * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PacMan::new);
    }
}