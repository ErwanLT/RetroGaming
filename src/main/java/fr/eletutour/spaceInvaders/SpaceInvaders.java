package fr.eletutour.spaceInvaders;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class SpaceInvaders extends JFrame {
    private final ScorePanel scorePanel;

    public SpaceInvaders() {
        setTitle("Space Invaders");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        scorePanel = new ScorePanel();  // Initialisé en premier
        GamePanel gamePanel = new GamePanel();    // Puis GamePanel
        gamePanel.initializeGame();     // Appel explicite après création des deux panels

        add(gamePanel, BorderLayout.CENTER);
        add(scorePanel, BorderLayout.EAST);

        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    class GamePanel extends JPanel {
        private int playerX = 350;
        private ArrayList<Rectangle> invaders;
        private ArrayList<Rectangle> bullets;
        private ArrayList<Rectangle> enemyBullets;
        private boolean gameRunning = true;
        private int invaderDirection = 1;
        private int level = 1;
        private int lives = 3;
        private final Random random = new Random();
        private int moveCounter = 0;

        public GamePanel() {
            setPreferredSize(new Dimension(600, 600));
            setBackground(Color.BLACK);

            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (gameRunning) {
                        if (e.getKeyCode() == KeyEvent.VK_LEFT && playerX > 0) playerX -= 10;
                        if (e.getKeyCode() == KeyEvent.VK_RIGHT && playerX < 550) playerX += 10;
                        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                            bullets.add(new Rectangle(playerX + 22, 550, 5, 10));
                        }
                    } else {
                        if (e.getKeyCode() == KeyEvent.VK_R) {
                            restartGame();
                        }
                        if (e.getKeyCode() == KeyEvent.VK_Q) {
                            System.exit(0);
                        }
                    }
                    repaint();
                }
            });
            setFocusable(true);

            Timer timer = new Timer(16, e -> {
                if (gameRunning) {
                    updateGame();
                    repaint();
                    scorePanel.repaint();
                }
            });
            timer.start();
        }

        public void initializeGame() {
            invaders = new ArrayList<>();
            bullets = new ArrayList<>();
            enemyBullets = new ArrayList<>();
            spawnInvaders();
            playerX = 350;
            lives = 3;
            level = 1;
            gameRunning = true;
            scorePanel.resetScore();  // Maintenant safe car scorePanel est déjà créé
        }

        private void spawnInvaders() {
            invaders.clear();
            int rows = 3 + level;
            int cols = 5 + level/2;
            for (int i = 0; i < cols; i++) {
                for (int j = 0; j < rows; j++) {
                    invaders.add(new Rectangle(50 + i * 60, 50 + j * 40, 30, 20));
                }
            }
        }

        private void restartGame() {
            initializeGame();
            repaint();
        }

        private void updateGame() {
            for (int i = bullets.size() - 1; i >= 0; i--) {
                Rectangle bullet = bullets.get(i);
                bullet.y -= 5;
                if (bullet.y < 0) bullets.remove(i);
            }

            for (int i = enemyBullets.size() - 1; i >= 0; i--) {
                Rectangle bullet = enemyBullets.get(i);
                bullet.y += 3;
                if (bullet.y > 600) enemyBullets.remove(i);
            }

            moveCounter++;
            if (moveCounter >= 15 - level) {
                boolean edgeReached = false;
                for (Rectangle invader : invaders) {
                    invader.x += 7 * invaderDirection;
                    if (invader.x <= 0 || invader.x >= 570) edgeReached = true;
                }
                if (edgeReached) {
                    invaderDirection *= -1;
                    for (Rectangle invader : invaders) {
                        invader.y += 20;
                    }
                }
                moveCounter = 0;
            }

            if (random.nextInt(100) < level && !invaders.isEmpty()) {
                Rectangle shooter = invaders.get(random.nextInt(invaders.size()));
                enemyBullets.add(new Rectangle(shooter.x + 12, shooter.y + 20, 5, 10));
            }

            Rectangle player = new Rectangle(playerX, 550, 50, 20);
            for (int i = enemyBullets.size() - 1; i >= 0; i--) {
                if (player.intersects(enemyBullets.get(i))) {
                    lives--;
                    enemyBullets.remove(i);
                    if (lives <= 0) gameRunning = false;
                }
            }

            for (int i = invaders.size() - 1; i >= 0; i--) {
                Rectangle invader = invaders.get(i);
                for (int j = bullets.size() - 1; j >= 0; j--) {
                    if (invader.intersects(bullets.get(j))) {
                        invaders.remove(i);
                        bullets.remove(j);
                        scorePanel.incrementScore(10);
                        break;
                    }
                }
            }

            if (invaders.isEmpty()) {
                level++;
                spawnInvaders();
            }
        }

        private void drawInvader(Graphics g, int x, int y) {
            g.setColor(Color.RED);
            g.fillRect(x + 5, y + 5, 20, 15);
            g.fillRect(x + 10, y, 10, 5);
            g.fillRect(x, y + 5, 5, 5);
            g.fillRect(x + 25, y + 5, 5, 5);
            g.setColor(Color.YELLOW);
            g.fillRect(x + 12, y + 7, 3, 3);
            g.fillRect(x + 17, y + 7, 3, 3);
            g.setColor(Color.RED);
            g.fillRect(x + 5, y + 20, 5, 5);
            g.fillRect(x + 20, y + 20, 5, 5);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.setColor(Color.GREEN);
            g.fillRect(playerX, 550, 50, 20);
            g.setColor(Color.GRAY);
            g.fillRect(playerX + 20, 545, 10, 5);

            for (Rectangle invader : invaders) {
                drawInvader(g, invader.x, invader.y);
            }

            g.setColor(Color.WHITE);
            for (Rectangle bullet : bullets) {
                g.fillRect(bullet.x, bullet.y, 5, 10);
            }

            g.setColor(Color.YELLOW);
            for (Rectangle bullet : enemyBullets) {
                g.fillRect(bullet.x, bullet.y, 5, 10);
            }

            g.setColor(Color.WHITE);
            g.drawString("Vies: " + lives, 10, 20);
            g.drawString("Niveau: " + level, 10, 40);

            if (!gameRunning) {
                g.setFont(new Font("Arial", Font.BOLD, 30));
                g.setColor(Color.RED);
                g.drawString("GAME OVER", 200, 300);
                g.setFont(new Font("Arial", Font.PLAIN, 20));
                g.setColor(Color.WHITE);
                g.drawString("Press R to Restart", 230, 350);
                g.drawString("Press Q to Quit", 240, 380);
            }
        }
    }

    static class ScorePanel extends JPanel {
        private int score = 0;

        public ScorePanel() {
            setPreferredSize(new Dimension(200, 600));
            setBackground(Color.DARK_GRAY);
        }

        public void incrementScore(int points) {
            score += points;
            repaint();
        }

        public void resetScore() {
            score = 0;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Score: " + score, 50, 50);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SpaceInvaders::new);
    }
}