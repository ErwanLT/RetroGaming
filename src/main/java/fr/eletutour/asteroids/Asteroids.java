package fr.eletutour.asteroids;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class Asteroids extends JPanel implements ActionListener {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    class Ship {
        double x = WIDTH/2;
        double y = HEIGHT/2;
        double angle = 0;
        double speedX = 0;
        double speedY = 0;
        boolean thrusting = false;
        boolean exploded = false;
        int explosionFrame = 0; // Pour l'animation d'explosion
    }

    class Asteroid {
        double x, y;
        double speedX, speedY;
        int size;

        Asteroid() {
            Random rand = new Random();
            x = rand.nextInt(WIDTH);
            y = rand.nextInt(HEIGHT);
            speedX = rand.nextDouble() * 4 - 2;
            speedY = rand.nextDouble() * 4 - 2;
            size = 40;
        }
    }

    class Bullet {
        double x, y;
        double speedX, speedY;

        Bullet(double x, double y, double angle) {
            this.x = x;
            this.y = y;
            speedX = Math.cos(angle) * 10;
            speedY = Math.sin(angle) * 10;
        }
    }

    private Ship ship;
    private ArrayList<Asteroid> asteroids;
    private ArrayList<Bullet> bullets;
    private boolean left, right, up, space;
    private int score;
    private boolean gameOver;

    public Asteroids() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);

        initGame();

        Timer timer = new Timer(16, this);
        timer.start();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (gameOver && e.getKeyCode() == KeyEvent.VK_SPACE) {
                    initGame();
                    return;
                }
                if (!gameOver) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_LEFT: left = true; break;
                        case KeyEvent.VK_RIGHT: right = true; break;
                        case KeyEvent.VK_UP: up = true; break;
                        case KeyEvent.VK_SPACE: space = true; break;
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT: left = false; break;
                    case KeyEvent.VK_RIGHT: right = false; break;
                    case KeyEvent.VK_UP: up = false; break;
                    case KeyEvent.VK_SPACE: space = false; break;
                }
            }
        });
        setFocusable(true);
    }

    private void initGame() {
        ship = new Ship();
        asteroids = new ArrayList<>();
        bullets = new ArrayList<>();
        score = 0;
        gameOver = false;
        for (int i = 0; i < 5; i++) {
            asteroids.add(new Asteroid());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.WHITE);

        if (!ship.exploded) {
            // Dessiner le vaisseau normal
            g2d.translate(ship.x, ship.y);
            g2d.rotate(ship.angle);
            g2d.drawLine(-10, -10, 20, 0);
            g2d.drawLine(-10, 10, 20, 0);
            g2d.drawLine(-10, -10, -10, 10);
            if (ship.thrusting) {
                g2d.drawLine(-10, 0, -20, 0);
            }
            g2d.rotate(-ship.angle);
            g2d.translate(-ship.x, -ship.y);
        } else {
            // Animation d'explosion
            g2d.translate(ship.x, ship.y);
            for (int i = 0; i < 8; i++) {
                double angle = Math.PI * 2 * i / 8;
                int radius = ship.explosionFrame * 2;
                int ex = (int)(Math.cos(angle) * radius);
                int ey = (int)(Math.sin(angle) * radius);
                g2d.fillOval(ex-2, ey-2, 4, 4);
            }
            g2d.translate(-ship.x, -ship.y);
        }

        // Dessiner les astéroïdes
        for (Asteroid a : asteroids) {
            g2d.drawOval((int)a.x, (int)a.y, a.size, a.size);
        }

        // Dessiner les tirs
        for (Bullet b : bullets) {
            g2d.fillOval((int)b.x, (int)b.y, 5, 5);
        }

        // Dessiner le score
        g2d.setFont(new Font("Arial", Font.PLAIN, 20));
        g2d.drawString("Score: " + score, 10, 30);

        // Message de game over
        if (gameOver) {
            g2d.setFont(new Font("Arial", Font.BOLD, 40));
            String msg = "Game Over - Press Space to Restart";
            int msgWidth = g2d.getFontMetrics().stringWidth(msg);
            g2d.drawString(msg, (WIDTH - msgWidth) / 2, HEIGHT / 2);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver) return;

        if (!ship.exploded) {
            // Contrôle du vaisseau
            if (left) ship.angle -= 0.1;
            if (right) ship.angle += 0.1;
            if (up) {
                ship.speedX += Math.cos(ship.angle) * 0.2;
                ship.speedY += Math.sin(ship.angle) * 0.2;
                ship.thrusting = true;
            } else {
                ship.thrusting = false;
            }
            if (space && bullets.size() < 5) {
                bullets.add(new Bullet(ship.x, ship.y, ship.angle));
                space = false;
            }

            // Mouvement du vaisseau
            ship.x += ship.speedX;
            ship.y += ship.speedY;
            ship.speedX *= 0.99;
            ship.speedY *= 0.99;

            // Bordures
            if (ship.x < 0) ship.x = WIDTH;
            if (ship.x > WIDTH) ship.x = 0;
            if (ship.y < 0) ship.y = HEIGHT;
            if (ship.y > HEIGHT) ship.y = 0;
        } else {
            // Animation d'explosion
            ship.explosionFrame++;
            if (ship.explosionFrame > 20) {
                gameOver = true;
            }
        }

        // Mouvement des astéroïdes
        for (Asteroid a : asteroids) {
            a.x += a.speedX;
            a.y += a.speedY;
            if (a.x < 0) a.x = WIDTH;
            if (a.x > WIDTH) a.x = 0;
            if (a.y < 0) a.y = HEIGHT;
            if (a.y > HEIGHT) a.y = 0;

            // Vérifier collision avec le vaisseau
            if (!ship.exploded && Math.hypot(ship.x - a.x, ship.y - a.y) < a.size/2 + 15) {
                ship.exploded = true;
            }
        }

        // Mouvement et collision des tirs
        ArrayList<Bullet> bulletsToRemove = new ArrayList<>();
        ArrayList<Asteroid> asteroidsToRemove = new ArrayList<>();

        for (Bullet b : bullets) {
            b.x += b.speedX;
            b.y += b.speedY;
            if (b.x < 0 || b.x > WIDTH || b.y < 0 || b.y > HEIGHT) {
                bulletsToRemove.add(b);
            }

            for (Asteroid a : asteroids) {
                if (Math.hypot(b.x - a.x, b.y - a.y) < a.size/2 + 2) {
                    bulletsToRemove.add(b);
                    asteroidsToRemove.add(a);
                    score += 10;
                }
            }
        }

        bullets.removeAll(bulletsToRemove);
        asteroids.removeAll(asteroidsToRemove);

        if (asteroids.size() < 5 && !ship.exploded) {
            asteroids.add(new Asteroid());
        }

        repaint();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Asteroids");
        Asteroids game = new Asteroids();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}