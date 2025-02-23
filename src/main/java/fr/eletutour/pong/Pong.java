package fr.eletutour.pong;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Pong extends JPanel implements ActionListener {
    // Dimensions de la fenêtre
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    // Dimensions des raquettes et de la balle
    private static final int PADDLE_WIDTH = 10;
    private static final int PADDLE_HEIGHT = 60;
    private static final int BALL_SIZE = 15;

    // Positions et vitesses
    private int ballX = WIDTH/2;
    private int ballY = HEIGHT/2;
    private int ballXSpeed = 5;
    private int ballYSpeed = 5;

    private int paddle1Y = HEIGHT/2 - PADDLE_HEIGHT/2;
    private int paddle2Y = HEIGHT/2 - PADDLE_HEIGHT/2;

    // Scores
    private int score1 = 0;
    private int score2 = 0;

    // Contrôles joueur 1 et mode de jeu
    private boolean up1, down1, up2, down2;
    private boolean soloMode = false;
    private boolean gameStarted = false;
    private int menuSelection = 0; // 0 = Solo, 1 = 2 Joueurs

    // Timer
    private long startTime;
    private static final int GAME_DURATION = 60 * 1000; // 60 secondes en millisecondes
    private boolean gameOver = false;

    public Pong() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);

        // Timer pour l'animation
        Timer timer = new Timer(16, this);
        timer.start();

        // Gestion des touches
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!gameStarted) {
                    // Menu de sélection
                    if (e.getKeyCode() == KeyEvent.VK_UP) {
                        menuSelection = 0; // Mode Solo
                    } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                        menuSelection = 1; // Mode 2 Joueurs
                    } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        soloMode = (menuSelection == 0);
                        gameStarted = true;
                        startTime = System.currentTimeMillis(); // Démarrer le timer
                    }
                } else if (gameOver && e.getKeyCode() == KeyEvent.VK_SPACE) {
                    resetGame(); // Redémarrer après Game Over
                } else if (!gameOver) {
                    // Contrôles en jeu
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_S: up1 = true; break;
                        case KeyEvent.VK_W: down1 = true; break;
                        case KeyEvent.VK_UP: up2 = true; break;
                        case KeyEvent.VK_DOWN: down2 = true; break;
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (gameStarted && !gameOver) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_S: up1 = false; break;
                        case KeyEvent.VK_W: down1 = false; break;
                        case KeyEvent.VK_UP: up2 = false; break;
                        case KeyEvent.VK_DOWN: down2 = false; break;
                    }
                }
            }
        });
        setFocusable(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (!gameStarted) {
            // Menu de sélection
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("Choisissez un mode :", WIDTH/2 - 200, HEIGHT/2 - 50);
            g.setFont(new Font("Arial", Font.PLAIN, 30));

            String[] options = {"Mode Solo (vs IA)", "Mode 2 Joueurs"};
            for (int i = 0; i < options.length; i++) {
                int yPos = HEIGHT/2 + i * 50;
                if (i == menuSelection) {
                    g.setColor(Color.YELLOW);
                    g.drawString("> " + options[i], WIDTH/2 - 150, yPos);
                    g.setColor(Color.WHITE);
                } else {
                    g.drawString("  " + options[i], WIDTH/2 - 150, yPos);
                }
            }
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Utilisez les flèches haut/bas et Entrée pour sélectionner", WIDTH/2 - 250, HEIGHT/2 + 100);
        } else if (gameOver) {
            // Écran de fin
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            String winner = score1 > score2 ? "Joueur 1" : (score2 > score1 ? (soloMode ? "IA" : "Joueur 2") : "Égalité");
            g.drawString("Fin de la partie ! Vainqueur : " + winner, WIDTH/2 - 300, HEIGHT/2 - 50);
            g.drawString("Score : " + score1 + " - " + score2, WIDTH/2 - 100, HEIGHT/2);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Appuyez sur Espace pour rejouer", WIDTH/2 - 150, HEIGHT/2 + 50);
        } else {
            // Jeu en cours
            g.setColor(Color.WHITE);
            g.fillRect(50, paddle1Y, PADDLE_WIDTH, PADDLE_HEIGHT);
            g.fillRect(WIDTH-50-PADDLE_WIDTH, paddle2Y, PADDLE_WIDTH, PADDLE_HEIGHT);
            g.fillOval(ballX, ballY, BALL_SIZE, BALL_SIZE);
            g.drawLine(WIDTH/2, 0, WIDTH/2, HEIGHT);
            g.setFont(new Font("Arial", Font.PLAIN, 30));
            g.drawString(String.valueOf(score1), WIDTH/4, 50);
            g.drawString(String.valueOf(score2), 3*WIDTH/4, 50);

            // Afficher le timer
            long elapsedTime = System.currentTimeMillis() - startTime;
            int remainingSeconds = (int) ((GAME_DURATION - elapsedTime) / 1000);
            if (remainingSeconds < 0) remainingSeconds = 0;
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Temps restant : " + remainingSeconds + "s", WIDTH/2 - 80, 20);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameStarted) {
            repaint();
            return;
        }

        if (!gameOver) {
            // Vérifier si le temps est écoulé
            long elapsedTime = System.currentTimeMillis() - startTime;
            if (elapsedTime >= GAME_DURATION) {
                gameOver = true;
                return;
            }

            // Mouvement des raquettes
            if (soloMode) {
                if (paddle2Y + PADDLE_HEIGHT/2 < ballY && paddle2Y < HEIGHT - PADDLE_HEIGHT) {
                    paddle2Y += 4;
                }
                if (paddle2Y + PADDLE_HEIGHT/2 > ballY && paddle2Y > 0) {
                    paddle2Y -= 4;
                }
            } else {
                if (up2 && paddle2Y > 0) paddle2Y -= 5;
                if (down2 && paddle2Y < HEIGHT - PADDLE_HEIGHT) paddle2Y += 5;
            }

            if (up1 && paddle1Y > 0) paddle1Y -= 5;
            if (down1 && paddle1Y < HEIGHT - PADDLE_HEIGHT) paddle1Y += 5;

            // Mouvement de la balle
            ballX += ballXSpeed;
            ballY += ballYSpeed;

            // Collision avec les bords supérieur et inférieur
            if (ballY <= 0 || ballY >= HEIGHT - BALL_SIZE) {
                ballYSpeed = -ballYSpeed;
            }

            // Collision avec les raquettes
            Rectangle ballRect = new Rectangle(ballX, ballY, BALL_SIZE, BALL_SIZE);
            Rectangle paddle1Rect = new Rectangle(50, paddle1Y, PADDLE_WIDTH, PADDLE_HEIGHT);
            Rectangle paddle2Rect = new Rectangle(WIDTH-50-PADDLE_WIDTH, paddle2Y, PADDLE_WIDTH, PADDLE_HEIGHT);

            if (ballRect.intersects(paddle1Rect) || ballRect.intersects(paddle2Rect)) {
                ballXSpeed = -ballXSpeed;
            }

            // Score
            if (ballX <= 0) {
                score2++;
                resetBall();
            }
            if (ballX >= WIDTH - BALL_SIZE) {
                score1++;
                resetBall();
            }
        }

        repaint();
    }

    private void resetBall() {
        ballX = WIDTH/2;
        ballY = HEIGHT/2;
        ballXSpeed = -ballXSpeed;
    }

    private void resetGame() {
        ballX = WIDTH/2;
        ballY = HEIGHT/2;
        paddle1Y = HEIGHT/2 - PADDLE_HEIGHT/2;
        paddle2Y = HEIGHT/2 - PADDLE_HEIGHT/2;
        score1 = 0;
        score2 = 0;
        gameStarted = false;
        gameOver = false;
        menuSelection = 0;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Pong");
        Pong game = new Pong();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}