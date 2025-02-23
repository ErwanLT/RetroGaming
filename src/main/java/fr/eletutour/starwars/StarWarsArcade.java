package fr.eletutour.starwars;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class StarWarsArcade extends JPanel implements ActionListener, KeyListener {
    private Timer timer;
    private int playerX = 300, playerY = 400; // Position du joueur (X-Wing)
    private ArrayList<Bullet> bullets = new ArrayList<>(); // Tirs du joueur
    private ArrayList<Enemy> enemies = new ArrayList<>(); // Ennemis (TIE Fighters ou tourelles)
    private Random rand = new Random();
    private int score = 0;
    private int phase = 1; // 1: Espace, 2: Surface, 3: Tranchée
    private boolean gameOver = false;
    private int shields = 3; // Boucliers du joueur
    private int trenchProgress = 0; // Progression dans la tranchée

    public StarWarsArcade() {
        setFocusable(true);
        addKeyListener(this);
        timer = new Timer(20, this); // 50 FPS
        timer.start();
        spawnEnemies();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(Color.BLACK);

        if (!gameOver) {
            switch (phase) {
                case 1: // Phase 1: Combat spatial
                    drawSpaceBackground(g);
                    drawXWing(g, playerX, playerY);
                    for (Enemy enemy : enemies) drawTIEFighter(g, enemy.x, enemy.y);
                    break;
                case 2: // Phase 2: Surface de l'Étoile de la Mort
                    drawDeathStarSurface(g);
                    drawXWing(g, playerX, playerY);
                    for (Enemy enemy : enemies) drawTurret(g, enemy.x, enemy.y);
                    break;
                case 3: // Phase 3: Tranchée
                    drawTrench(g);
                    drawXWing(g, playerX, playerY);
                    break;
            }

            // Tirs
            g.setColor(Color.RED);
            for (Bullet bullet : bullets) {
                g.fillRect(bullet.x, bullet.y, 4, 12);
            }

            // HUD
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            g.drawString("Score: " + score, 10, 20);
            g.drawString("Shields: " + shields, 10, 40);
            g.drawString("Phase: " + (phase == 1 ? "Space" : phase == 2 ? "Surface" : "Trench"), 10, 60);
        } else {
            // Écran de fin
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString(phase == 3 && shields > 0 ? "Victory!" : "Game Over", 200, 200);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Score final: " + score, 230, 250);
            g.drawString("Appuyez sur R pour rejouer", 210, 300);
            g.drawString("Appuyez sur Q pour quitter", 210, 330);
        }
    }

    private void drawSpaceBackground(Graphics g) {
        g.setColor(Color.WHITE);
        for (int i = 0; i < 50; i++) {
            int x = rand.nextInt(600);
            int y = rand.nextInt(500);
            g.fillRect(x, y, 2, 2); // Étoiles
        }
    }

    private void drawDeathStarSurface(Graphics g) {
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, 600, 500);
        g.setColor(Color.DARK_GRAY);
        for (int i = 0; i < 10; i++) {
            g.fillRect(rand.nextInt(600), rand.nextInt(500), 20, 20); // Structures
        }
    }

    private void drawTrench(Graphics g) {
        g.setColor(Color.GRAY);
        int trenchWidth = 200 - (trenchProgress / 10); // Rétrécissement progressif
        g.fillRect(0, 0, (300 - trenchWidth / 2), 500); // Mur gauche
        g.fillRect(300 + trenchWidth / 2, 0, 600, 500); // Mur droit
        g.setColor(Color.DARK_GRAY);
        for (int y = 0; y < 500; y += 20) {
            g.fillRect(300 - trenchWidth / 2, y, trenchWidth, 10); // Lignes horizontales
        }
    }

    private void drawXWing(Graphics g, int x, int y) {
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(x - 10, y, 20, 10); // Corps
        g.setColor(Color.GRAY);
        g.fillRect(x - 20, y - 5, 10, 5); // Aile gauche haute
        g.fillRect(x - 20, y + 5, 10, 5); // Aile gauche basse
        g.fillRect(x + 10, y - 5, 10, 5); // Aile droite haute
        g.fillRect(x + 10, y + 5, 10, 5); // Aile droite basse
        g.setColor(Color.RED);
        g.fillOval(x - 3, y - 2, 6, 6); // Cockpit
    }

    private void drawTIEFighter(Graphics g, int x, int y) {
        g.setColor(Color.DARK_GRAY);
        g.fillOval(x - 10, y - 10, 20, 20); // Corps
        g.setColor(Color.GRAY);
        g.drawLine(x - 20, y - 5, x - 10, y); // Aile gauche
        g.drawLine(x + 20, y - 5, x + 10, y); // Aile droite
    }

    private void drawTurret(Graphics g, int x, int y) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(x - 10, y - 10, 20, 20); // Base
        g.setColor(Color.RED);
        g.fillRect(x - 2, y - 15, 4, 10); // Canon
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            // Mouvement des tirs
            for (int i = bullets.size() - 1; i >= 0; i--) {
                bullets.get(i).y -= 10;
                if (bullets.get(i).y < 0) bullets.remove(i);
            }

            // Mouvement des ennemis (ou progression tranchée)
            if (phase == 1 || phase == 2) {
                for (int i = enemies.size() - 1; i >= 0; i--) {
                    enemies.get(i).y += 3;
                    if (enemies.get(i).y > 500) enemies.remove(i);
                }
            } else if (phase == 3) {
                trenchProgress += 2;
                if (trenchProgress >= 1000) { // Fin de la tranchée
                    gameOver = true; // Victoire
                    timer.stop();
                }
            }

            // Collisions tirs/ennemis
            if (phase != 3) {
                for (int i = bullets.size() - 1; i >= 0; i--) {
                    for (int j = enemies.size() - 1; j >= 0; j--) {
                        if (bullets.get(i).intersects(enemies.get(j))) {
                            bullets.remove(i);
                            enemies.remove(j);
                            score += 10;
                            if (enemies.isEmpty()) nextPhase();
                            break;
                        }
                    }
                }
            }

            // Collision joueur/ennemi
            if (phase != 3) {
                for (Enemy enemy : enemies) {
                    if (Math.abs(playerX - enemy.x) < 30 && Math.abs(playerY - enemy.y) < 30) {
                        shields--;
                        enemies.remove(enemy);
                        if (shields <= 0) {
                            gameOver = true;
                            timer.stop();
                        }
                        break;
                    }
                }
            }

            // Réapparition des ennemis
            if ((phase == 1 || phase == 2) && rand.nextInt(100) < 3) spawnEnemies();

            repaint();
        }
    }

    private void spawnEnemies() {
        if (phase == 1 || phase == 2) {
            enemies.add(new Enemy(rand.nextInt(560) + 20, -20));
        }
    }

    private void nextPhase() {
        phase++;
        enemies.clear();
        bullets.clear();
        if (phase <= 3) spawnEnemies();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (!gameOver) {
            if (key == KeyEvent.VK_LEFT && playerX > 30) playerX -= 10;
            if (key == KeyEvent.VK_RIGHT && playerX < 570) playerX += 10;
            if (key == KeyEvent.VK_SPACE) bullets.add(new Bullet(playerX, playerY - 20));
        } else {
            if (key == KeyEvent.VK_R) {
                playerX = 300;
                playerY = 400;
                bullets.clear();
                enemies.clear();
                score = 0;
                shields = 3;
                phase = 1;
                gameOver = false;
                timer.start();
                spawnEnemies();
            } else if (key == KeyEvent.VK_Q) {
                System.exit(0);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}

    class Bullet {
        int x, y;
        Bullet(int x, int y) {
            this.x = x;
            this.y = y;
        }
        boolean intersects(Enemy enemy) {
            return (Math.abs(x - enemy.x) < 15 && Math.abs(y - enemy.y) < 15);
        }
    }

    class Enemy {
        int x, y;
        Enemy(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Star Wars Arcade (Atari 1983)");
        StarWarsArcade game = new StarWarsArcade();
        frame.add(game);
        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}