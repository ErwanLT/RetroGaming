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
    
    // Contrôles
    private boolean up1, down1, up2, down2;
    
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
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W: up1 = true; break;
                    case KeyEvent.VK_S: down1 = true; break;
                    case KeyEvent.VK_UP: up2 = true; break;
                    case KeyEvent.VK_DOWN: down2 = true; break;
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W: up1 = false; break;
                    case KeyEvent.VK_S: down1 = false; break;
                    case KeyEvent.VK_UP: up2 = false; break;
                    case KeyEvent.VK_DOWN: down2 = false; break;
                }
            }
        });
        setFocusable(true);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Dessiner les raquettes
        g.setColor(Color.WHITE);
        g.fillRect(50, paddle1Y, PADDLE_WIDTH, PADDLE_HEIGHT);
        g.fillRect(WIDTH-50-PADDLE_WIDTH, paddle2Y, PADDLE_WIDTH, PADDLE_HEIGHT);
        
        // Dessiner la balle
        g.fillOval(ballX, ballY, BALL_SIZE, BALL_SIZE);
        
        // Dessiner la ligne centrale
        g.drawLine(WIDTH/2, 0, WIDTH/2, HEIGHT);
        
        // Dessiner les scores
        g.setFont(new Font("Arial", Font.PLAIN, 30));
        g.drawString(String.valueOf(score1), WIDTH/4, 50);
        g.drawString(String.valueOf(score2), 3*WIDTH/4, 50);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        // Mouvement des raquettes
        if (up1 && paddle1Y > 0) paddle1Y -= 5;
        if (down1 && paddle1Y < HEIGHT-PADDLE_HEIGHT) paddle1Y += 5;
        if (up2 && paddle2Y > 0) paddle2Y -= 5;
        if (down2 && paddle2Y < HEIGHT-PADDLE_HEIGHT) paddle2Y += 5;
        
        // Mouvement de la balle
        ballX += ballXSpeed;
        ballY += ballYSpeed;
        
        // Collision avec les bords supérieur et inférieur
        if (ballY <= 0 || ballY >= HEIGHT-BALL_SIZE) {
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
        if (ballX >= WIDTH-BALL_SIZE) {
            score1++;
            resetBall();
        }
        
        repaint();
    }
    
    private void resetBall() {
        ballX = WIDTH/2;
        ballY = HEIGHT/2;
        ballXSpeed = -ballXSpeed;
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