package fr.eletutour.simon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;

public class SimonGame extends JFrame {
    private List<Integer> sequence;
    private List<Integer> playerInput;
    private int currentStep;
    private boolean playerTurn;
    private JButton[] buttons;
    private JLabel statusLabel;
    private Timer flashTimer;
    private int sequenceIndex;

    public SimonGame() {
        sequence = new ArrayList<>();
        playerInput = new ArrayList<>();
        currentStep = 0;
        playerTurn = false;

        // Set up the frame
        setTitle("Simon Game");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create buttons for colors
        buttons = new JButton[4];
        buttons[0] = createButton(Color.RED.darker(), 0);
        buttons[1] = createButton(Color.BLUE.darker(), 1);
        buttons[2] = createButton(Color.GREEN.darker(), 2);
        buttons[3] = createButton(Color.YELLOW.darker(), 3);

        // Panel for buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2));
        for (JButton button : buttons) {
            buttonPanel.add(button);
        }

        // Status label
        statusLabel = new JLabel("Watch the sequence!", SwingConstants.CENTER);
        add(statusLabel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);

        // Start button
        JButton startButton = new JButton("Start Game");
        startButton.addActionListener(e -> startNewGame());
        add(startButton, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JButton createButton(Color color, int index) {
        JButton button = new JButton();
        button.setBackground(color);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.addActionListener(e -> {
            if (playerTurn) {
                playerInput.add(index);
                flashButton(button, 200);
                checkPlayerInput();
            }
        });
        return button;
    }

    private void startNewGame() {
        sequence.clear();
        playerInput.clear();
        currentStep = 0;
        playerTurn = false;
        statusLabel.setText("Watch the sequence!");
        addColorToSequence();
        playSequence();
    }

    private void addColorToSequence() {
        Random random = new Random();
        sequence.add(random.nextInt(4));
        System.out.println("Sequence added: " + sequence);
    }

    private void playSequence() {
        playerTurn = false;
        currentStep = 0;
        sequenceIndex = 0;
        statusLabel.setText("Watch the sequence!");
        System.out.println("Starting sequence: " + sequence);

        // Use Timer for flashing sequence
        flashTimer = new Timer(1200, null); // Total cycle: 800ms flash + 400ms pause
        flashTimer.addActionListener(e -> {
            if (sequenceIndex >= sequence.size()) {
                flashTimer.stop();
                playerTurn = true;
                playerInput.clear();
                statusLabel.setText("Your turn!");
                System.out.println("Sequence finished, player's turn");
                return;
            }

            int colorIndex = sequence.get(sequenceIndex);
            JButton button = buttons[colorIndex];
            flashButton(button, 800); // Flash for 800ms
            sequenceIndex++;
        });
        flashTimer.setRepeats(true);
        flashTimer.setInitialDelay(1000); // Initial pause
        flashTimer.start();
    }

    private void flashButton(JButton button, int duration) {
        Color original = button.getBackground();
        Color brighter = original.brighter().brighter();

        // Turn on flash
        button.setBackground(brighter);
        button.revalidate();
        button.repaint();
        System.out.println("Flashing button with color: " + brighter);

        // Turn off flash after duration
        Timer offTimer = new Timer(duration, e -> {
            button.setBackground(original);
            button.revalidate();
            button.repaint();
            System.out.println("Restoring button color: " + original);
        });
        offTimer.setRepeats(false);
        offTimer.start();
    }

    private void checkPlayerInput() {
        if (playerInput.size() <= currentStep + 1) {
            if (!playerInput.get(currentStep).equals(sequence.get(currentStep))) {
                statusLabel.setText("Game Over! Score: " + (sequence.size() - 1));
                playerTurn = false;
                System.out.println("Game over, wrong input");
                return;
            }
            currentStep++;
        }

        if (playerInput.size() == sequence.size()) {
            statusLabel.setText("Correct! Next round...");
            addColorToSequence();
            playSequence();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SimonGame::new);
    }
}