package fr.eletutour;

import fr.eletutour.pong.Pong;
import fr.eletutour.tetris.Tetris;
import fr.eletutour.pacman.PacMan;
import fr.eletutour.asteroids.Asteroids;
import fr.eletutour.defuse.BombDefusalGame;
import fr.eletutour.starwars.StarWarsArcade;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ArcadeMenu extends JFrame {
    private int selectedGame = 0; // Index du jeu sélectionné (0 à 5)
    private final String[] gameNames = {
            "Pong", "Tetris", "Pac-Man", "Asteroids", "Désamorcer la Bombe", "Star Wars"
    };

    public ArcadeMenu() {
        setTitle("Menu Arcade");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 450);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());

                // Titre
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 30));
                g.drawString("Choisissez un jeu", getWidth() / 2 - 110, 50);

                // Liste des jeux avec curseur
                g.setFont(new Font("Arial", Font.PLAIN, 20));
                for (int i = 0; i < gameNames.length; i++) {
                    int y = 100 + i * 40;
                    if (i == selectedGame) {
                        g.setColor(Color.YELLOW); // Curseur jaune pour le jeu sélectionné
                        g.drawString("> " + gameNames[i], getWidth() / 2 - 100, y);
                    } else {
                        g.setColor(Color.WHITE);
                        g.drawString("  " + gameNames[i], getWidth() / 2 - 100, y);
                    }
                }

                // Instructions
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.PLAIN, 16));
                g.drawString("Flèches Haut/Bas pour choisir, Entrée pour jouer",
                        getWidth() / 2 - 150, getHeight() - 30);
            }
        };
        panel.setFocusable(true);

        // Gestion des touches
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_UP && selectedGame > 0) {
                    selectedGame--;
                    panel.repaint();
                } else if (key == KeyEvent.VK_DOWN && selectedGame < gameNames.length - 1) {
                    selectedGame++;
                    panel.repaint();
                } else if (key == KeyEvent.VK_ENTER) {
                    launchGame(selectedGame);
                }
            }
        });

        add(panel);
        panel.requestFocusInWindow(); // Assure que le panel reçoit les événements clavier
    }

    private void launchGame(int gameIndex) {
        switch (gameIndex) {
            case 0: launchPong(); break;
            case 1: launchTetris(); break;
            case 2: launchPacMan(); break;
            case 3: launchAsteroids(); break;
            case 4: launchBombDefusal(); break;
            case 5: launchStarWars(); break;
        }
    }

    private void launchPong() {
        JFrame pongFrame = new JFrame("Pong");
        Pong pongGame = new Pong();
        pongFrame.add(pongGame);
        pongFrame.pack();
        pongFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pongFrame.setLocationRelativeTo(null);
        pongFrame.setVisible(true);
    }

    private void launchTetris() {
        JFrame tetrisFrame = new JFrame("Tetris");
        Tetris tetrisGame = new Tetris();
        tetrisFrame.add(tetrisGame);
        tetrisFrame.pack();
        tetrisFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        tetrisFrame.setLocationRelativeTo(null);
        tetrisFrame.setVisible(true);
    }

    private void launchPacMan() {
        SwingUtilities.invokeLater(() -> {
            PacMan pacManGame = new PacMan();
            pacManGame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        });
    }

    private void launchAsteroids() {
        JFrame asteroidsFrame = new JFrame("Asteroids");
        Asteroids asteroidsGame = new Asteroids();
        asteroidsFrame.add(asteroidsGame);
        asteroidsFrame.pack();
        asteroidsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        asteroidsFrame.setLocationRelativeTo(null);
        asteroidsFrame.setVisible(true);
    }

    private void launchBombDefusal() {
        SwingUtilities.invokeLater(() -> {
            BombDefusalGame bombGame = new BombDefusalGame();
            bombGame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            bombGame.setVisible(true);
        });
    }

    private void launchStarWars() {
        JFrame starWarsFrame = new JFrame("Star Wars Arcade (Atari 1983)");
        StarWarsArcade starWarsGame = new StarWarsArcade();
        starWarsFrame.add(starWarsGame);
        starWarsFrame.setSize(600, 500);
        starWarsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        starWarsFrame.setLocationRelativeTo(null);
        starWarsFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ArcadeMenu menu = new ArcadeMenu();
            menu.setVisible(true);
        });
    }
}