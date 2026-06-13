import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Random;

/**
 * ============================================================
 *   NUMBER GUESSING GAME — Java Swing GUI
 *   Aesthetic: Dark arcade / neon retro-futuristic
 * ============================================================
 * Entry point. Creates and shows the main game window
 * on the Swing Event Dispatch Thread (EDT) — the safe way
 * to start any Swing application.
 */
public class NumberGuessingGameSwing {

    public static void main(String[] args) {
        // SwingUtilities.invokeLater ensures the GUI is built on the correct thread
        SwingUtilities.invokeLater(() -> {
            GameFrame frame = new GameFrame();
            frame.setVisible(true);
        });
    }
}


// ================================================================
//  GAME FRAME  —  The main window
// ================================================================

/**
 * GameFrame is the top-level JFrame (window).
 * It holds a CardLayout so we can flip between screens:
 *   - "menu"  → WelcomePanel
 *   - "game"  → GamePanel
 *   - "over"  → GameOverPanel
 */
class GameFrame extends JFrame {

    // ---- Constants shared across the whole app ----
    static final int    MIN_NUM      = 1;
    static final int    MAX_NUM      = 100;
    static final int    MAX_ATTEMPTS = 5;

    // ---- Colour palette (dark neon arcade) ----
    static final Color BG_DARK    = new Color(10,  12,  30);   // near-black navy
    static final Color BG_PANEL   = new Color(18,  22,  48);   // slightly lighter navy
    static final Color NEON_CYAN  = new Color(0,   230, 255);  // electric cyan
    static final Color NEON_PINK  = new Color(255, 50,  180);  // hot pink
    static final Color NEON_GREEN = new Color(50,  255, 140);  // mint green
    static final Color NEON_GOLD  = new Color(255, 210, 50);   // arcade gold
    static final Color TEXT_MAIN  = new Color(220, 230, 255);  // soft white-blue
    static final Color TEXT_DIM   = new Color(100, 120, 180);  // muted blue

    // CardLayout lets us swap panels without opening new windows
    private final CardLayout  cardLayout = new CardLayout();
    private final JPanel      cardPanel  = new JPanel(cardLayout);

    // The three screens
    private final WelcomePanel  welcomePanel;
    private final GamePanel     gamePanel;
    private final GameOverPanel gameOverPanel;

    // Session score (persists across rounds)
    private int totalRoundsPlayed = 0;
    private int totalRoundsWon    = 0;

    public GameFrame() {
        setTitle("🎯 Number Guessing Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Build each screen and add to the card stack
        welcomePanel  = new WelcomePanel(this);
        gamePanel     = new GamePanel(this);
        gameOverPanel = new GameOverPanel(this);

        cardPanel.add(welcomePanel,  "menu");
        cardPanel.add(gamePanel,     "game");
        cardPanel.add(gameOverPanel, "over");

        // Dark background for the card container itself
        cardPanel.setBackground(BG_DARK);
        add(cardPanel);

        pack();
        setLocationRelativeTo(null); // centre on screen
        showMenu();
    }

    // ---- Navigation helpers called by child panels ----

    /** Show the welcome / menu screen */
    void showMenu() { cardLayout.show(cardPanel, "menu"); }

    /** Start a fresh game round */
    void startGame() {
        gamePanel.resetRound();
        cardLayout.show(cardPanel, "game");
        gamePanel.focusInput();
    }

    /** Show the game-over / round-end screen */
    void showGameOver(boolean won, int secretNumber, int attemptsUsed) {
        totalRoundsPlayed++;
        if (won) totalRoundsWon++;
        gameOverPanel.update(won, secretNumber, attemptsUsed, totalRoundsPlayed, totalRoundsWon);
        cardLayout.show(cardPanel, "over");
    }

    int getTotalRoundsPlayed() { return totalRoundsPlayed; }
    int getTotalRoundsWon()    { return totalRoundsWon;    }
}


// ================================================================
//  WELCOME PANEL  —  The start / menu screen
// ================================================================

/**
 * Shown first. Displays the game title, rules summary,
 * and a "Start Game" button.
 * Custom painting draws a glowing grid background.
 */
class WelcomePanel extends JPanel {

    private final GameFrame frame;

    WelcomePanel(GameFrame frame) {
        this.frame = frame;
        setPreferredSize(new Dimension(520, 520));
        setBackground(GameFrame.BG_DARK);
        setLayout(new GridBagLayout());
        buildUI();
    }

    private void buildUI() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 40, 8, 40);

        // ---- Title ----
        JLabel titleIcon = makeLabel("🎯", 56, GameFrame.NEON_CYAN);
        titleIcon.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0; gbc.insets = new Insets(40, 40, 0, 40);
        add(titleIcon, gbc);

        JLabel title = makeLabel("NUMBER QUEST", 30, GameFrame.NEON_CYAN);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1; gbc.insets = new Insets(0, 40, 4, 40);
        add(title, gbc);

        JLabel subtitle = makeLabel("Guess the Secret Number", 14, GameFrame.TEXT_DIM);
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 2; gbc.insets = new Insets(0, 40, 30, 40);
        add(subtitle, gbc);

        // ---- Rules card ----
        JPanel rulesCard = buildRulesCard();
        gbc.gridy = 3; gbc.insets = new Insets(0, 30, 30, 30);
        add(rulesCard, gbc);

        // ---- Start button ----
        NeonButton startBtn = new NeonButton("▶  START GAME", GameFrame.NEON_GREEN, GameFrame.BG_DARK);
        startBtn.addActionListener(e -> frame.startGame());
        gbc.gridy = 4; gbc.insets = new Insets(0, 60, 50, 60);
        add(startBtn, gbc);
    }

    private JPanel buildRulesCard() {
        JPanel card = new JPanel();
        card.setBackground(GameFrame.BG_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(GameFrame.NEON_CYAN, 1, true),
            new EmptyBorder(16, 20, 16, 20)
        ));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        String[] rules = {
            "🔢  Guess a number between 1 and 100",
            "⚡  You have 5 attempts per round",
            "↑↓  Hot / Cold hints after each guess",
            "🏆  Win rounds to build your score"
        };

        for (String rule : rules) {
            JLabel lbl = makeLabel(rule, 13, GameFrame.TEXT_MAIN);
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            lbl.setBorder(new EmptyBorder(3, 0, 3, 0));
            card.add(lbl);
        }
        return card;
    }

    // ---- Utility: make a styled JLabel ----
    private JLabel makeLabel(String text, int size, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Monospaced", Font.BOLD, size));
        lbl.setForeground(color);
        return lbl;
    }

    // ---- Custom painting: draw a faint dot grid for atmosphere ----
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(0, 180, 255, 18));  // very faint cyan dots
        for (int x = 0; x < getWidth(); x += 24) {
            for (int y = 0; y < getHeight(); y += 24) {
                g2.fillOval(x, y, 2, 2);
            }
        }
    }
}


// ================================================================
//  GAME PANEL  —  The active guessing screen
// ================================================================

/**
 * The heart of the UI. Shows:
 *   - The secret number range and attempt tracker (dots)
 *   - A text field for entering a guess
 *   - A "Guess" button
 *   - A feedback message (Too High / Too Low / Correct)
 *   - A scrollable history log of all guesses this round
 */
class GamePanel extends JPanel {

    private final GameFrame frame;
    private final Random    random = new Random();

    // Round state
    private int secretNumber;
    private int attemptsLeft;
    private int attemptsMade;

    // UI components we need to update dynamically
    private JLabel        feedbackLabel;
    private JLabel        attemptsLabel;
    private JTextField    guessField;
    private JPanel        dotsPanel;
    private JTextArea     historyArea;
    private NeonButton    guessBtn;

    GamePanel(GameFrame frame) {
        this.frame = frame;
        setPreferredSize(new Dimension(520, 520));
        setBackground(GameFrame.BG_DARK);
        setLayout(new BorderLayout(0, 0));
        buildUI();
    }

    // ---- Build the static UI skeleton ----
    private void buildUI() {

        // ===== TOP BAR =====
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(GameFrame.BG_PANEL);
        topBar.setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel rangeLabel = styledLabel("Range: 1 – 100", 13, GameFrame.TEXT_DIM);
        attemptsLabel     = styledLabel("Attempts: 5 left", 13, GameFrame.NEON_GOLD);
        topBar.add(rangeLabel,    BorderLayout.WEST);
        topBar.add(attemptsLabel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // ===== CENTRE SECTION =====
        JPanel centre = new JPanel();
        centre.setBackground(GameFrame.BG_DARK);
        centre.setLayout(new BoxLayout(centre, BoxLayout.Y_AXIS));
        centre.setBorder(new EmptyBorder(24, 40, 0, 40));

        // Attempt dots (filled = used, empty = remaining)
        dotsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        dotsPanel.setBackground(GameFrame.BG_DARK);
        dotsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centre.add(dotsPanel);
        centre.add(Box.createVerticalStrut(24));

        // Feedback message (Too High / Too Low / Correct / …)
        feedbackLabel = styledLabel("Enter your first guess!", 18, GameFrame.NEON_CYAN);
        feedbackLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centre.add(feedbackLabel);
        centre.add(Box.createVerticalStrut(24));

        // Input row: [text field]  [Guess button]
        JPanel inputRow = new JPanel(new BorderLayout(12, 0));
        inputRow.setBackground(GameFrame.BG_DARK);
        inputRow.setMaximumSize(new Dimension(340, 48));
        inputRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        guessField = new JTextField();
        guessField.setFont(new Font("Monospaced", Font.BOLD, 22));
        guessField.setForeground(GameFrame.NEON_CYAN);
        guessField.setBackground(GameFrame.BG_PANEL);
        guessField.setCaretColor(GameFrame.NEON_CYAN);
        guessField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(GameFrame.NEON_CYAN, 1),
            new EmptyBorder(4, 10, 4, 10)
        ));
        guessField.setHorizontalAlignment(JTextField.CENTER);
        // Allow pressing Enter to submit
        guessField.addActionListener(e -> handleGuess());

        guessBtn = new NeonButton("GUESS", GameFrame.NEON_PINK, GameFrame.BG_DARK);
        guessBtn.setPreferredSize(new Dimension(110, 48));
        guessBtn.addActionListener(e -> handleGuess());

        inputRow.add(guessField, BorderLayout.CENTER);
        inputRow.add(guessBtn,   BorderLayout.EAST);
        centre.add(inputRow);
        centre.add(Box.createVerticalStrut(24));

        // History log label
        JLabel histLabel = styledLabel("GUESS HISTORY", 11, GameFrame.TEXT_DIM);
        histLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centre.add(histLabel);
        centre.add(Box.createVerticalStrut(6));

        // Scrollable history text area
        historyArea = new JTextArea(5, 28);
        historyArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        historyArea.setForeground(GameFrame.TEXT_MAIN);
        historyArea.setBackground(GameFrame.BG_PANEL);
        historyArea.setBorder(new EmptyBorder(8, 12, 8, 12));
        historyArea.setEditable(false);
        historyArea.setLineWrap(false);

        JScrollPane scroll = new JScrollPane(historyArea);
        scroll.setBorder(new LineBorder(GameFrame.TEXT_DIM, 1));
        scroll.setBackground(GameFrame.BG_PANEL);
        scroll.getViewport().setBackground(GameFrame.BG_PANEL);
        scroll.setAlignmentX(Component.CENTER_ALIGNMENT);
        scroll.setMaximumSize(new Dimension(440, 130));
        centre.add(scroll);

        add(centre, BorderLayout.CENTER);

        // ===== BOTTOM BAR =====
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 12));
        bottomBar.setBackground(GameFrame.BG_PANEL);

        NeonButton menuBtn = new NeonButton("MENU", GameFrame.TEXT_DIM, GameFrame.BG_PANEL);
        menuBtn.setFont(new Font("Monospaced", Font.BOLD, 12));
        menuBtn.addActionListener(e -> frame.showMenu());
        bottomBar.add(menuBtn);

        add(bottomBar, BorderLayout.SOUTH);
    }

    // ---- Called every time a new round starts ----
    void resetRound() {
        // Pick a new secret number
        secretNumber  = random.nextInt(GameFrame.MAX_NUM) + GameFrame.MIN_NUM;
        attemptsLeft  = GameFrame.MAX_ATTEMPTS;
        attemptsMade  = 0;

        feedbackLabel.setText("Enter your first guess!");
        feedbackLabel.setForeground(GameFrame.NEON_CYAN);
        attemptsLabel.setText("Attempts: " + attemptsLeft + " left");
        guessField.setText("");
        guessField.setEnabled(true);
        guessBtn.setEnabled(true);
        historyArea.setText("");

        refreshDots();
    }

    // ---- Handle a guess submission ----
    private void handleGuess() {
        String input = guessField.getText().trim();

        // Validate: must be a number
        int guess;
        try {
            guess = Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            shake(feedbackLabel);
            feedbackLabel.setText("⚠  Please enter a number!");
            feedbackLabel.setForeground(GameFrame.NEON_GOLD);
            return;
        }

        // Validate: must be in range
        if (guess < GameFrame.MIN_NUM || guess > GameFrame.MAX_NUM) {
            shake(feedbackLabel);
            feedbackLabel.setText("⚠  Enter between 1 and 100!");
            feedbackLabel.setForeground(GameFrame.NEON_GOLD);
            return;
        }

        attemptsLeft--;
        attemptsMade++;
        guessField.setText("");
        refreshDots();

        // --- Compare guess to secret ---
        if (guess == secretNumber) {
            // ✅ Correct!
            feedbackLabel.setText("🎉  CORRECT!  It was " + secretNumber + "!");
            feedbackLabel.setForeground(GameFrame.NEON_GREEN);
            appendHistory(guess, "✅ CORRECT");
            endRound(true);

        } else {
            String direction;
            String histTag;

            if (guess > secretNumber) {
                feedbackLabel.setText("↓  Too High!  Try lower.");
                feedbackLabel.setForeground(GameFrame.NEON_PINK);
                direction = "↓ Too High";
                histTag   = "↓ HIGH";
            } else {
                feedbackLabel.setText("↑  Too Low!  Try higher.");
                feedbackLabel.setForeground(GameFrame.NEON_CYAN);
                direction = "↑ Too Low";
                histTag   = "↑ LOW";
            }

            appendHistory(guess, histTag);
            attemptsLabel.setText("Attempts: " + attemptsLeft + " left");

            if (attemptsLeft == 0) {
                // ❌ No attempts remaining
                feedbackLabel.setText("💀  The number was " + secretNumber + "!");
                feedbackLabel.setForeground(GameFrame.NEON_PINK);
                endRound(false);
            }
        }
    }

    /** Disable input and schedule the transition to the game-over screen */
    private void endRound(boolean won) {
        guessField.setEnabled(false);
        guessBtn.setEnabled(false);

        // Short pause so the player can read the result before screen flips
        Timer timer = new Timer(1500, e -> frame.showGameOver(won, secretNumber, attemptsMade));
        timer.setRepeats(false);
        timer.start();
    }

    /** Rebuild the attempt dots (● = used, ○ = remaining) */
    private void refreshDots() {
        dotsPanel.removeAll();
        for (int i = 0; i < GameFrame.MAX_ATTEMPTS; i++) {
            JLabel dot = new JLabel(i < attemptsMade ? "●" : "○");
            dot.setFont(new Font("Monospaced", Font.BOLD, 22));
            dot.setForeground(i < attemptsMade ? GameFrame.NEON_PINK : GameFrame.TEXT_DIM);
            dotsPanel.add(dot);
        }
        dotsPanel.revalidate();
        dotsPanel.repaint();
    }

    /** Add a line to the history log */
    private void appendHistory(int guess, String result) {
        String line = String.format("  #%d  →  %-4d  %s%n", attemptsMade, guess, result);
        historyArea.append(line);
        // Auto-scroll to the latest entry
        historyArea.setCaretPosition(historyArea.getDocument().getLength());
    }

    /** Request keyboard focus on the text field */
    void focusInput() {
        SwingUtilities.invokeLater(() -> guessField.requestFocusInWindow());
    }

    /** Shake animation on the feedback label for invalid input */
    private void shake(JComponent comp) {
        Point origin = comp.getLocation();
        int[] offsets = {-8, 8, -6, 6, -4, 4, 0};
        Timer timer = new Timer(40, null);
        int[] step = {0};
        timer.addActionListener(e -> {
            if (step[0] < offsets.length) {
                comp.setLocation(origin.x + offsets[step[0]++], origin.y);
            } else {
                comp.setLocation(origin);
                timer.stop();
            }
        });
        timer.start();
    }

    // Utility: create a styled label quickly
    private JLabel styledLabel(String text, int size, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Monospaced", Font.BOLD, size));
        lbl.setForeground(color);
        return lbl;
    }
}


// ================================================================
//  GAME OVER PANEL  —  Round result + play-again screen
// ================================================================

/**
 * Shown after each round ends (win or loss).
 * Displays the result, score, and two buttons:
 *   "Play Again" → starts a new round
 *   "Main Menu"  → returns to the welcome screen
 */
class GameOverPanel extends JPanel {

    private final GameFrame frame;

    private JLabel resultIcon;
    private JLabel resultTitle;
    private JLabel resultSub;
    private JLabel scoreLabel;

    GameOverPanel(GameFrame frame) {
        this.frame = frame;
        setPreferredSize(new Dimension(520, 520));
        setBackground(GameFrame.BG_DARK);
        setLayout(new GridBagLayout());
        buildUI();
    }

    private void buildUI() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 60, 10, 60);

        resultIcon  = centredLabel("🎉", 60, GameFrame.NEON_GREEN);
        resultTitle = centredLabel("YOU WIN!", 32, GameFrame.NEON_GREEN);
        resultSub   = centredLabel("The number was 42", 15, GameFrame.TEXT_DIM);
        scoreLabel  = centredLabel("Rounds won: 0 / 0", 15, GameFrame.NEON_GOLD);

        gbc.gridy = 0; gbc.insets = new Insets(50, 60, 4, 60);  add(resultIcon,  gbc);
        gbc.gridy = 1; gbc.insets = new Insets(0, 60, 4, 60);   add(resultTitle, gbc);
        gbc.gridy = 2; gbc.insets = new Insets(0, 60, 24, 60);  add(resultSub,   gbc);
        gbc.gridy = 3; gbc.insets = new Insets(0, 60, 40, 60);  add(scoreLabel,  gbc);

        NeonButton playAgainBtn = new NeonButton("▶  PLAY AGAIN", GameFrame.NEON_GREEN, GameFrame.BG_DARK);
        playAgainBtn.addActionListener(e -> frame.startGame());
        gbc.gridy = 4; gbc.insets = new Insets(0, 60, 12, 60);
        add(playAgainBtn, gbc);

        NeonButton menuBtn = new NeonButton("MAIN MENU", GameFrame.NEON_CYAN, GameFrame.BG_DARK);
        menuBtn.addActionListener(e -> frame.showMenu());
        gbc.gridy = 5; gbc.insets = new Insets(0, 60, 50, 60);
        add(menuBtn, gbc);
    }

    /**
     * Called by GameFrame to populate the panel before showing it.
     *
     * @param won           Did the player guess correctly?
     * @param secretNumber  The number that was hidden
     * @param attemptsUsed  How many guesses were used
     * @param played        Total rounds played this session
     * @param wonTotal      Total rounds won this session
     */
    void update(boolean won, int secretNumber, int attemptsUsed, int played, int wonTotal) {
        if (won) {
            resultIcon .setText("🏆");
            resultTitle.setText("YOU WIN!");
            resultTitle.setForeground(GameFrame.NEON_GREEN);
            resultSub  .setText("Guessed " + secretNumber + " in " + attemptsUsed + " attempt(s)!");
        } else {
            resultIcon .setText("💀");
            resultTitle.setText("GAME OVER");
            resultTitle.setForeground(GameFrame.NEON_PINK);
            resultSub  .setText("The number was " + secretNumber + ". Better luck next time!");
        }
        scoreLabel.setText("Rounds won: " + wonTotal + " / " + played);
    }

    private JLabel centredLabel(String text, int size, Color color) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("Monospaced", Font.BOLD, size));
        lbl.setForeground(color);
        return lbl;
    }
}


// ================================================================
//  NEON BUTTON  —  Custom painted button
// ================================================================

/**
 * A reusable custom JButton that draws:
 *   - A dark filled rectangle with rounded corners
 *   - A glowing coloured border
 *   - Coloured text
 *   - A subtle inner glow on hover
 */
class NeonButton extends JButton {

    private final Color neonColor;   // Border + text colour
    private final Color bgColor;     // Background fill colour
    private boolean     hovered = false;

    NeonButton(String text, Color neonColor, Color bgColor) {
        super(text);
        this.neonColor = neonColor;
        this.bgColor   = bgColor;

        setFont(new Font("Monospaced", Font.BOLD, 14));
        setForeground(neonColor);
        setBackground(bgColor);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);   // We handle painting ourselves
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(200, 44));

        // Track hover state to change the glow
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
            @Override public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight(), arc = 10;

        // Background fill
        g2.setColor(hovered ? neonColor.darker().darker() : bgColor);
        g2.fillRoundRect(0, 0, w, h, arc, arc);

        // Outer glow (paint the border with alpha layers)
        if (hovered) {
            g2.setColor(new Color(neonColor.getRed(), neonColor.getGreen(), neonColor.getBlue(), 60));
            g2.setStroke(new BasicStroke(6));
            g2.drawRoundRect(2, 2, w - 4, h - 4, arc, arc);
        }

        // Border
        g2.setColor(neonColor);
        g2.setStroke(new BasicStroke(hovered ? 2f : 1.5f));
        g2.drawRoundRect(1, 1, w - 2, h - 2, arc, arc);

        g2.dispose();

        // Let Swing draw the label text on top
        super.paintComponent(g);
    }
}
