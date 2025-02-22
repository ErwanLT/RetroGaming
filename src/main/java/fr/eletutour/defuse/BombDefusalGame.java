package fr.eletutour.defuse;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class BombDefusalGame extends JFrame {
    private JLabel timerLabel;
    private JLabel[] greenLEDs;
    private JLabel[] redLEDs;
    private JButton[] wires;
    private JButton hintButton;
    private JTextArea hintArea;
    private Timer gameTimer;
    private int timeLeft = 60;
    private int correctCuts = 0;
    private int wrongCuts = 0;
    private boolean[] correctWires;

    public BombDefusalGame() {
        // Initialisation des fils corrects (3 au hasard parmi les 15) et log au démarrage
        correctWires = new boolean[15];
        Random rand = new Random();
        int correctCount = 0;
        System.out.println("Initialisation des fils corrects...");
        while (correctCount < 3) {
            int index = rand.nextInt(15);
            if (!correctWires[index]) {
                correctWires[index] = true;
                correctCount++;
                String position = getVisualPosition(index);
                System.out.println("Fil correct ajouté : " + getColorName(index) + " (" + position + ")");
            }
        }

        // Log des fils corrects au démarrage
        logCorrectWires();

        // Configuration de la fenêtre
        setTitle("Bomb Defusal Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setSize(600, 700);
        getContentPane().setBackground(new Color(139, 69, 19)); // Fond marron clair

        // Panneau principal avec bordure
        JPanel mainPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(139, 69, 19));

        // 1. Compteur à rebours (haut)
        timerLabel = new JLabel("00:60", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Digital-7", Font.BOLD, 48));
        timerLabel.setForeground(Color.YELLOW);
        timerLabel.setOpaque(true);
        timerLabel.setBackground(Color.BLACK);
        timerLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

        // 2. Panneau pour les LEDs (milieu)
        JPanel ledPanel = new JPanel(new GridLayout(1, 6, 10, 10));
        ledPanel.setBackground(new Color(139, 69, 19));
        greenLEDs = new JLabel[3];
        redLEDs = new JLabel[3];

        for (int i = 0; i < 3; i++) {
            greenLEDs[i] = new JLabel("●", SwingConstants.CENTER);
            greenLEDs[i].setForeground(Color.GRAY);
            greenLEDs[i].setFont(new Font("Arial", Font.BOLD, 20));
            redLEDs[i] = new JLabel("●", SwingConstants.CENTER);
            redLEDs[i].setForeground(Color.GRAY);
            redLEDs[i].setFont(new Font("Arial", Font.BOLD, 20));
            ledPanel.add(greenLEDs[i]);
            ledPanel.add(redLEDs[i]);
        }

        // 3. Panneau pour les fils (bas) - Disposition 5x3 avec Bleu, Rouge, Jaune par ligne
        JPanel wirePanel = new JPanel(new GridLayout(5, 3, 10, 10));
        wirePanel.setBackground(new Color(139, 69, 19));
        wires = new JButton[15]; // 15 fils au total

        // Création des boutons pour les fils avec style, dans l’ordre visuel : Bleu, Rouge, Jaune par ligne
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 3; col++) {
                int index = row * 3 + col; // Calcul de l’index dans le tableau wires
                String color;
                if (col == 0) color = "Blue";   // Colonne 1 : Bleu
                else if (col == 1) color = "Red";  // Colonne 2 : Rouge
                else color = "Yellow";         // Colonne 3 : Jaune
                wires[index] = new JButton();
                wires[index].setBackground(getColor(color));
                wires[index].setOpaque(true);
                wires[index].setBorderPainted(false);
                wires[index].setPreferredSize(new Dimension(60, 20));
                final int wireIndex = index;
                wires[index].addActionListener(e -> cutWire(wireIndex));
                wirePanel.add(wires[index]);
            }
        }

        // 4. Panneau pour le bouton indice et la zone de texte
        JPanel hintPanel = new JPanel(new BorderLayout(10, 10));
        hintPanel.setBackground(new Color(139, 69, 19));

        hintButton = new JButton("Indice");
        hintButton.setBackground(Color.LIGHT_GRAY);
        hintButton.addActionListener(e -> showHint());

        hintArea = new JTextArea(2, 20);
        hintArea.setEditable(false);
        hintArea.setBackground(Color.DARK_GRAY);
        hintArea.setForeground(Color.WHITE);
        hintArea.setFont(new Font("Arial", Font.PLAIN, 14));
        hintArea.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

        hintPanel.add(hintButton, BorderLayout.NORTH);
        hintPanel.add(new JScrollPane(hintArea), BorderLayout.CENTER);

        // Ajout des composants à la fenêtre
        mainPanel.add(timerLabel);
        mainPanel.add(ledPanel);
        mainPanel.add(wirePanel);
        mainPanel.add(hintPanel);
        add(mainPanel, BorderLayout.CENTER);

        // Initialisation du timer
        gameTimer = new Timer(1000, e -> {
            timeLeft--;
            updateTimerDisplay();
            if (timeLeft <= 0) {
                gameOver(false); // La bombe explose si le temps est écoulé
            }
        });
        gameTimer.start();
    }

    private Color getColor(String colorName) {
        switch(colorName) {
            case "Blue": return Color.BLUE;
            case "Red": return Color.RED;
            case "Yellow": return Color.YELLOW;
            default: return Color.BLACK;
        }
    }

    private void updateTimerDisplay() {
        String minutes = String.format("%02d", timeLeft / 60);
        String seconds = String.format("%02d", timeLeft % 60);
        timerLabel.setText("00:" + seconds);
    }

    private void cutWire(int index) {
        wires[index].setEnabled(false);
        wires[index].setBackground(Color.GRAY); // Griser le fil coupé

        if (correctWires[index]) {
            greenLEDs[correctCuts].setForeground(Color.GREEN);
            correctCuts++;
            if (correctCuts == 3) {
                gameOver(true);
            }
        } else {
            redLEDs[wrongCuts].setForeground(Color.RED);
            wrongCuts++;
            if (wrongCuts == 3) {
                gameOver(false);
            }
        }
    }

    private void showHint() {
        // Compter le nombre de fils corrects par couleur
        int blueCorrect = 0, redCorrect = 0, yellowCorrect = 0;
        for (int i = 0; i < correctWires.length; i++) {
            if (correctWires[i]) {
                int col = i % 3; // Colonne : 0 = Bleu, 1 = Rouge, 2 = Jaune
                if (col == 0) blueCorrect++;
                else if (col == 1) redCorrect++;
                else yellowCorrect++;
            }
        }

        // Construire le message d'indice
        StringBuilder hint = new StringBuilder("Indice : ");
        boolean first = true;
        if (blueCorrect > 0) {
            hint.append("Bleu").append(blueCorrect > 1 ? "s" : "");
            first = false;
        }
        if (redCorrect > 0) {
            if (!first) hint.append(", ");
            hint.append("Rouge").append(redCorrect > 1 ? "s" : "");
            first = false;
        }
        if (yellowCorrect > 0) {
            if (!first) hint.append(", ");
            hint.append("Jaune").append(yellowCorrect > 1 ? "s" : "");
        }
        if (first) hint.append("Aucun fil correct trouvé !");

        hintArea.setText(hint.toString());
    }

    private void gameOver(boolean won) {
        gameTimer.stop();
        String message;
        StringBuilder correctWiresInfo = new StringBuilder();

        // Construire la liste des fils corrects
        correctWiresInfo.append("\nFils corrects à couper :");
        for (int i = 0; i < correctWires.length; i++) {
            if (correctWires[i]) {
                String position = getVisualPosition(i);
                correctWiresInfo.append("\n- Fil ").append(getColorName(i)).append(" (" + position + ")");
            }
        }

        // Déterminer le message final
        if (won) {
            message = "Bomb Defused! You Win!" + correctWiresInfo.toString();
        } else {
            message = "BOOM! Game Over!" + correctWiresInfo.toString();
        }

        // Afficher le message avec les fils corrects
        JOptionPane.showMessageDialog(this, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    private void logCorrectWires() {
        StringBuilder logMessage = new StringBuilder("Fils corrects au démarrage : ");
        boolean first = true;
        for (int i = 0; i < correctWires.length; i++) {
            if (correctWires[i]) {
                String position = getVisualPosition(i);
                if (!first) logMessage.append(", ");
                logMessage.append("Fil ").append(getColorName(i)).append(" (" + position + ")");
                first = false;
            }
        }
        System.out.println(logMessage.toString());
    }

    private String getColorName(int index) {
        int col = index % 3; // Colonne : 0 = Bleu, 1 = Rouge, 2 = Jaune
        if (col == 0) return "Bleu";
        else if (col == 1) return "Rouge";
        else return "Jaune";
    }

    private String getVisualPosition(int index) {
        int row = index / 3 + 1; // Ligne (1 à 5)
        int col = index % 3 + 1; // Colonne (1 à 3)
        return "Ligne " + row + ", Colonne " + col;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BombDefusalGame game = new BombDefusalGame();
            game.setVisible(true);
        });
    }
}