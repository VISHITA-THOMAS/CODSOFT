import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ╔══════════════════════════════════════════════════════════╗
 * ║          STUDENT GRADE CALCULATOR — Java Swing           ║
 * ║                                                          ║
 * ║  A complete GUI application to calculate student grades  ║
 * ║  with input validation, OOP design, and modern styling.  ║
 * ╚══════════════════════════════════════════════════════════╝
 *
 * ENTRY POINT: Creates the main application window.
 */
public class StudentGradeCalculator {

    public static void main(String[] args) {
        // Always start Swing apps on the Event Dispatch Thread (EDT) — this is thread-safe.
        SwingUtilities.invokeLater(() -> {
            try {
                // Use the system look-and-feel as a base, then override with custom styles
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {}

            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}


// ════════════════════════════════════════════════════════════════
//  COLOUR PALETTE & THEME CONSTANTS
// ════════════════════════════════════════════════════════════════

/**
 * AppTheme holds every colour and font used across the UI.
 * Centralising them here means one change updates the whole app.
 */
class AppTheme {

    // ── Colours ──────────────────────────────────────────────
    static final Color BG_BASE       = new Color(245, 247, 252); // light cool grey
    static final Color BG_CARD       = Color.WHITE;
    static final Color BG_HEADER     = new Color(37,  99,  235); // vivid blue
    static final Color BG_RESULT     = new Color(238, 242, 255); // soft lavender
    static final Color ACCENT_BLUE   = new Color(37,  99,  235);
    static final Color ACCENT_GREEN  = new Color(22,  163, 74);
    static final Color ACCENT_RED    = new Color(220, 38,  38);
    static final Color ACCENT_ORANGE = new Color(234, 88,  12);
    static final Color ACCENT_PURPLE = new Color(124, 58,  237);
    static final Color TEXT_DARK     = new Color(15,  23,  42);
    static final Color TEXT_MID      = new Color(71,  85,  105);
    static final Color TEXT_LIGHT    = new Color(148, 163, 184);
    static final Color BORDER_COLOR  = new Color(226, 232, 240);

    // ── Fonts ─────────────────────────────────────────────────
    static final Font FONT_TITLE   = new Font("SansSerif", Font.BOLD,  22);
    static final Font FONT_HEADING = new Font("SansSerif", Font.BOLD,  15);
    static final Font FONT_LABEL   = new Font("SansSerif", Font.PLAIN, 13);
    static final Font FONT_INPUT   = new Font("SansSerif", Font.PLAIN, 14);
    static final Font FONT_RESULT  = new Font("SansSerif", Font.BOLD,  14);
    static final Font FONT_GRADE   = new Font("SansSerif", Font.BOLD,  48);
    static final Font FONT_BUTTON  = new Font("SansSerif", Font.BOLD,  13);

    // ── Grade colour mapping ───────────────────────────────────
    static Color gradeColor(String grade) {
        return switch (grade) {
            case "A+" -> new Color(16,  185, 129);  // emerald
            case "A"  -> new Color(34,  197, 94);   // green
            case "B"  -> new Color(59,  130, 246);  // blue
            case "C"  -> new Color(234, 179, 8);    // yellow
            case "D"  -> new Color(249, 115, 22);   // orange
            default   -> new Color(239, 68,  68);   // red  (F)
        };
    }
}


// ════════════════════════════════════════════════════════════════
//  DATA MODEL — Student
// ════════════════════════════════════════════════════════════════

/**
 * Student is a plain data class (model) that holds all the
 * calculated results for one student.
 * Keeping data separate from the UI is good OOP practice.
 */
class Student {

    private final String       name;        // Student's name
    private final List<Double> marks;       // Individual subject marks
    private final int          numSubjects; // How many subjects

    // Calculated fields
    private double totalMarks;
    private double averagePercent;
    private String grade;

    /** Constructor — stores inputs and immediately calculates results. */
    Student(String name, List<Double> marks) {
        this.name        = name;
        this.marks       = marks;
        this.numSubjects = marks.size();
        calculate();
    }

    /**
     * calculate() — derives total, average, and grade from the marks list.
     * Private because it is called only from the constructor.
     */
    private void calculate() {
        totalMarks = 0;
        for (double m : marks) totalMarks += m;          // sum all marks

        averagePercent = totalMarks / numSubjects;        // simple average

        grade = assignGrade(averagePercent);              // lookup grade
    }

    /**
     * assignGrade() — maps a percentage to a letter grade.
     * Static so it can also be used independently (e.g. per-subject colouring).
     */
    static String assignGrade(double avg) {
        if (avg >= 90) return "A+";
        if (avg >= 80) return "A";
        if (avg >= 70) return "B";
        if (avg >= 60) return "C";
        if (avg >= 50) return "D";
        return "F";
    }

    // ── Getters ────────────────────────────────────────────────
    String       getName()          { return name; }
    List<Double> getMarks()         { return marks; }
    int          getNumSubjects()   { return numSubjects; }
    double       getTotalMarks()    { return totalMarks; }
    double       getAveragePercent(){ return averagePercent; }
    String       getGrade()         { return grade; }
}


// ════════════════════════════════════════════════════════════════
//  MAIN WINDOW  —  JFrame container
// ════════════════════════════════════════════════════════════════

/**
 * MainWindow is the top-level JFrame.
 * It contains three logical sections stacked vertically:
 *   1. HeaderPanel  — app title bar
 *   2. ContentPanel — left input form + right results panel (side by side)
 *   3. (buttons embedded inside ContentPanel)
 */
class MainWindow extends JFrame {

    private InputPanel  inputPanel;
    private ResultPanel resultPanel;

    MainWindow() {
        setTitle("Student Grade Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setMinimumSize(new Dimension(780, 600));

        buildUI();
        pack();
        setLocationRelativeTo(null);   // centre on screen
    }

    private void buildUI() {
        // Root panel with light grey background
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(AppTheme.BG_BASE);

        // ── Header ──────────────────────────────────────────────
        root.add(new HeaderPanel(), BorderLayout.NORTH);

        // ── Main content (input LEFT | results RIGHT) ───────────
        JPanel contentRow = new JPanel(new GridBagLayout());
        contentRow.setBackground(AppTheme.BG_BASE);
        contentRow.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.BOTH;
        gbc.insets  = new Insets(0, 0, 0, 16);
        gbc.gridy   = 0;
        gbc.weighty = 1.0;

        // Input panel — takes 55% of width
        inputPanel = new InputPanel(this);
        gbc.gridx   = 0;
        gbc.weightx = 0.55;
        contentRow.add(inputPanel, gbc);

        // Result panel — takes 45% of width
        resultPanel = new ResultPanel();
        gbc.gridx   = 1;
        gbc.weightx = 0.45;
        gbc.insets  = new Insets(0, 0, 0, 0);
        contentRow.add(resultPanel, gbc);

        root.add(contentRow, BorderLayout.CENTER);
        setContentPane(root);
    }

    /** Called by InputPanel after a successful calculation. */
    void displayResult(Student student) {
        resultPanel.showResult(student);
    }

    /** Called by InputPanel's Reset button. */
    void clearResult() {
        resultPanel.clearResult();
    }
}


// ════════════════════════════════════════════════════════════════
//  HEADER PANEL
// ════════════════════════════════════════════════════════════════

/**
 * The blue title bar at the top.
 * Uses custom paintComponent() to draw a gradient background.
 */
class HeaderPanel extends JPanel {

    HeaderPanel() {
        setPreferredSize(new Dimension(780, 80));
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;

        JLabel icon = new JLabel("🎓");
        icon.setFont(new Font("SansSerif", Font.PLAIN, 30));
        gbc.insets = new Insets(0, 0, 0, 12);
        add(icon, gbc);

        JPanel textBlock = new JPanel();
        textBlock.setOpaque(false);
        textBlock.setLayout(new BoxLayout(textBlock, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Student Grade Calculator");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Calculate grades, totals & averages instantly");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(new Color(199, 210, 254));

        textBlock.add(title);
        textBlock.add(subtitle);

        gbc.gridx = 1; gbc.insets = new Insets(0, 0, 0, 0);
        add(textBlock, gbc);
    }

    /** Paint a left-to-right blue gradient. */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint gp = new GradientPaint(
            0, 0, new Color(37, 99, 235),
            getWidth(), getHeight(), new Color(124, 58, 237)
        );
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }
}


// ════════════════════════════════════════════════════════════════
//  INPUT PANEL  —  Left side: form fields + buttons
// ════════════════════════════════════════════════════════════════

/**
 * InputPanel contains:
 *   - Student name field
 *   - Number-of-subjects spinner
 *   - "Set Subjects" button → dynamically builds subject mark fields
 *   - Subject mark text fields (created dynamically)
 *   - Calculate / Reset / Exit buttons
 *
 * All validation and calculation logic lives here, using the Student model.
 */
class InputPanel extends JPanel {

    private final MainWindow      owner;

    // ── Static fields ──────────────────────────────────────────
    private JTextField            nameField;
    private JSpinner              subjectSpinner;
    private FlatButton            setSubjectsBtn;

    // ── Dynamic subject fields (built when user clicks Set Subjects) ──
    private JPanel                subjectsContainer;  // scrollable area
    private List<JTextField>      markFields;         // one field per subject
    private List<JLabel>          markLabels;         // "Subject N" labels

    InputPanel(MainWindow owner) {
        this.owner      = owner;
        this.markFields = new ArrayList<>();
        this.markLabels = new ArrayList<>();
        buildUI();
    }

    // ─────────────────────────────────────────────────────────────
    //  UI Construction
    // ─────────────────────────────────────────────────────────────

    private void buildUI() {
        setBackground(AppTheme.BG_CARD);
        setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(AppTheme.BORDER_COLOR, 1, true),
            new EmptyBorder(24, 24, 24, 24)
        ));
        setLayout(new BorderLayout(0, 16));

        // ── Section heading ──
        JLabel heading = new JLabel("📝  Student Information");
        heading.setFont(AppTheme.FONT_HEADING);
        heading.setForeground(AppTheme.TEXT_DARK);
        heading.setBorder(new EmptyBorder(0, 0, 8, 0));
        add(heading, BorderLayout.NORTH);

        // ── Form area (scrollable) ──
        JPanel formWrap = new JPanel(new BorderLayout());
        formWrap.setBackground(AppTheme.BG_CARD);

        // Top fixed fields: name + subjects
        JPanel topFields = new JPanel(new GridBagLayout());
        topFields.setBackground(AppTheme.BG_CARD);
        buildTopFields(topFields);
        formWrap.add(topFields, BorderLayout.NORTH);

        // Dynamic subjects container
        subjectsContainer = new JPanel();
        subjectsContainer.setBackground(AppTheme.BG_CARD);
        subjectsContainer.setLayout(new BoxLayout(subjectsContainer, BoxLayout.Y_AXIS));

        JScrollPane scroll = new JScrollPane(subjectsContainer);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setBackground(AppTheme.BG_CARD);
        scroll.getViewport().setBackground(AppTheme.BG_CARD);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        formWrap.add(scroll, BorderLayout.CENTER);

        add(formWrap, BorderLayout.CENTER);

        // ── Button row ──
        add(buildButtonRow(), BorderLayout.SOUTH);
    }

    /**
     * buildTopFields() — builds the Name and Number-of-Subjects rows.
     * Uses GridBagLayout for precise label + field alignment.
     */
    private void buildTopFields(JPanel panel) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill  = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 8);

        // Row 0: Student Name
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(formLabel("Student Name:"), gbc);

        nameField = styledTextField("e.g. Alice Johnson");
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.gridwidth = 2;
        panel.add(nameField, gbc);

        // Row 1: Number of Subjects + Set button
        gbc.gridwidth = 1; gbc.weightx = 0;
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(formLabel("No. of Subjects:"), gbc);

        // Spinner: integer, range 1–20, starts at 3
        SpinnerNumberModel spinModel = new SpinnerNumberModel(3, 1, 20, 1);
        subjectSpinner = new JSpinner(spinModel);
        subjectSpinner.setFont(AppTheme.FONT_INPUT);
        subjectSpinner.setPreferredSize(new Dimension(70, 36));
        ((JSpinner.DefaultEditor) subjectSpinner.getEditor()).getTextField()
            .setHorizontalAlignment(JTextField.CENTER);
        gbc.gridx = 1; gbc.weightx = 0.3;
        panel.add(subjectSpinner, gbc);

        setSubjectsBtn = new FlatButton("Set Subjects", AppTheme.ACCENT_BLUE, Color.WHITE);
        setSubjectsBtn.addActionListener(e -> buildSubjectFields());
        gbc.gridx = 2; gbc.weightx = 0.7;
        panel.add(setSubjectsBtn, gbc);

        // Divider
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3; gbc.insets = new Insets(12, 0, 8, 0);
        panel.add(makeDivider("Subject Marks (0 – 100)"), gbc);
    }

    /**
     * buildSubjectFields() — dynamically creates one labelled text field
     * per subject based on the spinner value.
     * Old fields are removed and replaced each time.
     */
    private void buildSubjectFields() {
        int count = (Integer) subjectSpinner.getValue();

        // Clear previous fields
        subjectsContainer.removeAll();
        markFields.clear();
        markLabels.clear();

        for (int i = 1; i <= count; i++) {
            JPanel row = new JPanel(new GridBagLayout());
            row.setBackground(AppTheme.BG_CARD);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(4, 0, 4, 8); gbc.fill = GridBagConstraints.HORIZONTAL;

            JLabel lbl = formLabel("Subject " + i + ":");
            gbc.gridx = 0; gbc.weightx = 0.35;
            row.add(lbl, gbc);

            JTextField tf = styledTextField("0 – 100");
            gbc.gridx = 1; gbc.weightx = 0.65;
            row.add(tf, gbc);

            markFields.add(tf);
            markLabels.add(lbl);
            subjectsContainer.add(row);
        }

        subjectsContainer.revalidate();
        subjectsContainer.repaint();
    }

    /**
     * buildButtonRow() — three action buttons: Calculate, Reset, Exit.
     */
    private JPanel buildButtonRow() {
        JPanel row = new JPanel(new GridLayout(1, 3, 10, 0));
        row.setBackground(AppTheme.BG_CARD);
        row.setBorder(new EmptyBorder(8, 0, 0, 0));

        FlatButton calcBtn  = new FlatButton("✔  Calculate",  AppTheme.ACCENT_GREEN,  Color.WHITE);
        FlatButton resetBtn = new FlatButton("↺  Reset",      AppTheme.ACCENT_ORANGE, Color.WHITE);
        FlatButton exitBtn  = new FlatButton("✕  Exit",       AppTheme.ACCENT_RED,    Color.WHITE);

        calcBtn .addActionListener(e -> handleCalculate());
        resetBtn.addActionListener(e -> handleReset());
        exitBtn .addActionListener(e -> handleExit());

        row.add(calcBtn);
        row.add(resetBtn);
        row.add(exitBtn);
        return row;
    }

    // ─────────────────────────────────────────────────────────────
    //  Button Handlers
    // ─────────────────────────────────────────────────────────────

    /**
     * handleCalculate() — validates every field, builds a Student object,
     * and asks the main window to display the result.
     * All errors are shown via JOptionPane dialogs.
     */
    private void handleCalculate() {
        try {
            // ── Validate name ──────────────────────────────────
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Student name cannot be empty.");
            }

            // ── Validate subjects are set ──────────────────────
            if (markFields.isEmpty()) {
                throw new IllegalArgumentException("Please click \"Set Subjects\" first.");
            }

            // ── Parse and validate each mark ──────────────────
            List<Double> marks = new ArrayList<>();
            for (int i = 0; i < markFields.size(); i++) {
                String raw = markFields.get(i).getText().trim();

                if (raw.isEmpty()) {
                    throw new IllegalArgumentException(
                        "Mark for Subject " + (i + 1) + " is empty.");
                }

                double mark;
                try {
                    mark = Double.parseDouble(raw);
                } catch (NumberFormatException ex) {
                    // NumberFormatException — not a valid number
                    throw new IllegalArgumentException(
                        "Invalid mark for Subject " + (i + 1) + ": \"" + raw + "\" is not a number.");
                }

                if (mark < 0 || mark > 100) {
                    throw new IllegalArgumentException(
                        "Mark for Subject " + (i + 1) + " must be between 0 and 100.");
                }

                marks.add(mark);
            }

            // ── All valid — create Student and display result ──
            Student student = new Student(name, marks);
            owner.displayResult(student);

        } catch (IllegalArgumentException ex) {
            // Show a friendly error dialog
            JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "⚠  Input Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /** handleReset() — clears all fields and removes the result display. */
    private void handleReset() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to clear all fields?",
            "Reset Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            nameField.setText("");
            subjectSpinner.setValue(3);
            subjectsContainer.removeAll();
            subjectsContainer.revalidate();
            subjectsContainer.repaint();
            markFields.clear();
            owner.clearResult();
        }
    }

    /** handleExit() — asks for confirmation, then closes the app. */
    private void handleExit() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to exit?",
            "Exit",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  UI Utility Helpers
    // ─────────────────────────────────────────────────────────────

    /** Creates a right-aligned form label. */
    private JLabel formLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(AppTheme.FONT_LABEL);
        lbl.setForeground(AppTheme.TEXT_MID);
        lbl.setHorizontalAlignment(SwingConstants.LEFT);
        return lbl;
    }

    /** Creates a styled single-line text field with placeholder text. */
    private JTextField styledTextField(String placeholder) {
        JTextField tf = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Draw placeholder text when the field is empty and unfocused
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    g2.setColor(AppTheme.TEXT_LIGHT);
                    Insets ins = getInsets();
                    g2.drawString(placeholder, ins.left + 2,
                        getHeight() / 2 + g2.getFontMetrics().getAscent() / 2 - 2);
                    g2.dispose();
                }
            }
        };
        tf.setFont(AppTheme.FONT_INPUT);
        tf.setForeground(AppTheme.TEXT_DARK);
        tf.setBackground(new Color(249, 250, 251));
        tf.setPreferredSize(new Dimension(0, 36));
        tf.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(AppTheme.BORDER_COLOR, 1, true),
            new EmptyBorder(4, 10, 4, 10)
        ));

        // Highlight border on focus
        tf.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(AppTheme.ACCENT_BLUE, 2, true),
                    new EmptyBorder(3, 9, 3, 9)
                ));
            }
            @Override public void focusLost(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(AppTheme.BORDER_COLOR, 1, true),
                    new EmptyBorder(4, 10, 4, 10)
                ));
            }
        });
        return tf;
    }

    /** Creates a horizontal divider with a centred label. */
    private JPanel makeDivider(String label) {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        p.setBackground(AppTheme.BG_CARD);
        JSeparator left  = new JSeparator();  left.setForeground(AppTheme.BORDER_COLOR);
        JSeparator right = new JSeparator(); right.setForeground(AppTheme.BORDER_COLOR);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        lbl.setForeground(AppTheme.TEXT_LIGHT);
        p.add(left,  BorderLayout.WEST);
        p.add(lbl,   BorderLayout.CENTER);
        p.add(right, BorderLayout.EAST);
        return p;
    }
}


// ════════════════════════════════════════════════════════════════
//  RESULT PANEL  —  Right side: shows calculated results
// ════════════════════════════════════════════════════════════════

/**
 * ResultPanel shows:
 *   - Student name
 *   - Large grade badge
 *   - Total marks
 *   - Average percentage + progress bar
 *   - Per-subject marks table
 *
 * It starts in "empty" state and populates when showResult() is called.
 */
class ResultPanel extends JPanel {

    private JPanel     contentArea;   // Swapped between emptyState and resultsView
    private CardLayout cards = new CardLayout();

    // Result widgets
    private JLabel  gradeLabel;
    private JLabel  nameLabel;
    private JLabel  totalLabel;
    private JLabel  avgLabel;
    private JProgressBar avgBar;
    private DefaultTableModel tableModel;

    ResultPanel() {
        setBackground(AppTheme.BG_CARD);
        setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(AppTheme.BORDER_COLOR, 1, true),
            new EmptyBorder(24, 24, 24, 24)
        ));
        setLayout(new BorderLayout(0, 16));
        buildUI();
    }

    private void buildUI() {
        JLabel heading = new JLabel("📊  Results");
        heading.setFont(AppTheme.FONT_HEADING);
        heading.setForeground(AppTheme.TEXT_DARK);
        heading.setBorder(new EmptyBorder(0, 0, 8, 0));
        add(heading, BorderLayout.NORTH);

        contentArea = new JPanel(cards);
        contentArea.setBackground(AppTheme.BG_CARD);
        contentArea.add(buildEmptyState(), "empty");
        contentArea.add(buildResultsView(), "results");
        cards.show(contentArea, "empty");

        add(contentArea, BorderLayout.CENTER);
    }

    /** Placeholder shown before any calculation is done. */
    private JPanel buildEmptyState() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(AppTheme.BG_RESULT);
        p.setBorder(new LineBorder(AppTheme.BORDER_COLOR, 1, true));

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

        JLabel icon = centredLabel("📋", 48, AppTheme.TEXT_LIGHT);
        JLabel msg1 = centredLabel("No results yet", 15, AppTheme.TEXT_MID);
        JLabel msg2 = centredLabel("Fill in the form and click Calculate", 12, AppTheme.TEXT_LIGHT);

        inner.add(icon);
        inner.add(Box.createVerticalStrut(8));
        inner.add(msg1);
        inner.add(Box.createVerticalStrut(4));
        inner.add(msg2);

        p.add(inner);
        return p;
    }

    /** The actual result layout built once and reused. */
    private JPanel buildResultsView() {
        JPanel p = new JPanel();
        p.setBackground(AppTheme.BG_CARD);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        // ── Grade badge ────────────────────────────────────────
        JPanel badgeCard = new JPanel(new GridBagLayout());
        badgeCard.setBackground(AppTheme.BG_RESULT);
        badgeCard.setBorder(new LineBorder(AppTheme.BORDER_COLOR, 1, true));
        badgeCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        badgeCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        gradeLabel = new JLabel("A+");
        gradeLabel.setFont(AppTheme.FONT_GRADE);
        gradeLabel.setForeground(AppTheme.ACCENT_GREEN);

        nameLabel = centredLabel("Student Name", 14, AppTheme.TEXT_DARK);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        JPanel badgeInner = new JPanel();
        badgeInner.setOpaque(false);
        badgeInner.setLayout(new BoxLayout(badgeInner, BoxLayout.Y_AXIS));
        gradeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel .setAlignmentX(Component.CENTER_ALIGNMENT);
        badgeInner.add(gradeLabel);
        badgeInner.add(nameLabel);
        badgeCard.add(badgeInner);
        p.add(badgeCard);
        p.add(Box.createVerticalStrut(12));

        // ── Stats ──────────────────────────────────────────────
        JPanel statsPanel = new JPanel(new GridBagLayout());
        statsPanel.setBackground(AppTheme.BG_CARD);
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(4, 0, 4, 8);

        totalLabel = resultLabel("Total: 0 / 0");
        avgLabel   = resultLabel("Average: 0.00%");
        avgBar     = new JProgressBar(0, 100);
        avgBar.setStringPainted(true);
        avgBar.setFont(AppTheme.FONT_LABEL);
        avgBar.setForeground(AppTheme.ACCENT_GREEN);
        avgBar.setBackground(AppTheme.BORDER_COLOR);
        avgBar.setPreferredSize(new Dimension(0, 22));

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1;
        statsPanel.add(totalLabel, gbc);
        gbc.gridy = 1;
        statsPanel.add(avgLabel, gbc);
        gbc.gridy = 2;
        statsPanel.add(avgBar, gbc);

        p.add(statsPanel);
        p.add(Box.createVerticalStrut(12));

        // ── Per-subject table ──────────────────────────────────
        JLabel tblHeading = new JLabel("Subject Breakdown");
        tblHeading.setFont(new Font("SansSerif", Font.BOLD, 12));
        tblHeading.setForeground(AppTheme.TEXT_MID);
        tblHeading.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(tblHeading);
        p.add(Box.createVerticalStrut(6));

        tableModel = new DefaultTableModel(
            new String[]{"Subject", "Marks", "Grade"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(tableModel);
        table.setFont(AppTheme.FONT_LABEL);
        table.setRowHeight(28);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setGridColor(AppTheme.BORDER_COLOR);
        table.setBackground(AppTheme.BG_CARD);
        table.setSelectionBackground(new Color(224, 231, 255));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setBackground(AppTheme.BG_RESULT);
        table.getTableHeader().setForeground(AppTheme.TEXT_MID);
        table.getTableHeader().setBorder(new LineBorder(AppTheme.BORDER_COLOR, 1));

        // Centre-align the Marks and Grade columns
        DefaultTableCellRenderer centreRenderer = new DefaultTableCellRenderer();
        centreRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(1).setCellRenderer(centreRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centreRenderer);

        // Custom renderer to colour the Grade cell
        table.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(tbl, value, sel, focus, row, col);
                setHorizontalAlignment(CENTER);
                setFont(new Font("SansSerif", Font.BOLD, 12));
                if (value != null) setForeground(AppTheme.gradeColor(value.toString()));
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(AppTheme.BORDER_COLOR, 1));
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(scroll);

        return p;
    }

    // ─────────────────────────────────────────────────────────────
    //  Public API
    // ─────────────────────────────────────────────────────────────

    /** Populate all widgets and flip to the results card. */
    void showResult(Student s) {
        // Grade badge
        gradeLabel.setText(s.getGrade());
        gradeLabel.setForeground(AppTheme.gradeColor(s.getGrade()));
        nameLabel.setText(s.getName());

        // Stats
        int maxTotal = s.getNumSubjects() * 100;
        totalLabel.setText(String.format("Total Marks:  %.0f / %d", s.getTotalMarks(), maxTotal));
        avgLabel  .setText(String.format("Average:  %.2f%%", s.getAveragePercent()));
        avgBar.setValue((int) Math.round(s.getAveragePercent()));
        avgBar.setString(String.format("%.2f%%", s.getAveragePercent()));
        avgBar.setForeground(AppTheme.gradeColor(s.getGrade()));

        // Per-subject table
        tableModel.setRowCount(0);   // clear old rows
        List<Double> marks = s.getMarks();
        for (int i = 0; i < marks.size(); i++) {
            double m = marks.get(i);
            tableModel.addRow(new Object[]{
                "Subject " + (i + 1),
                String.format("%.1f", m),
                Student.assignGrade(m)
            });
        }

        cards.show(contentArea, "results");
        revalidate(); repaint();
    }

    /** Return to the empty placeholder. */
    void clearResult() {
        cards.show(contentArea, "empty");
        revalidate(); repaint();
    }

    // ─────────────────────────────────────────────────────────────
    //  Helpers
    // ─────────────────────────────────────────────────────────────
    private JLabel centredLabel(String text, int size, Color color) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, size));
        lbl.setForeground(color);
        return lbl;
    }
    private JLabel resultLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(AppTheme.FONT_RESULT);
        lbl.setForeground(AppTheme.TEXT_DARK);
        return lbl;
    }
}


// ════════════════════════════════════════════════════════════════
//  FLAT BUTTON  —  Custom painted button with hover effect
// ════════════════════════════════════════════════════════════════

/**
 * FlatButton is a custom JButton with:
 *   - Solid fill background colour
 *   - White text
 *   - Slightly darker background on hover
 *   - Rounded corners
 */
class FlatButton extends JButton {

    private final Color baseColor;   // Normal background
    private       Color current;     // Background drawn right now
    private boolean     hovered = false;

    FlatButton(String text, Color bg, Color fg) {
        super(text);
        this.baseColor = bg;
        this.current   = bg;

        setFont(AppTheme.FONT_BUTTON);
        setForeground(fg);
        setBackground(bg);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(130, 40));

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                hovered = true;
                current = baseColor.darker();
                repaint();
            }
            @Override public void mouseExited(MouseEvent e) {
                hovered = false;
                current = baseColor;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Rounded rectangle fill
        g2.setColor(current);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

        // Slight top highlight for a subtle 3-D feel
        g2.setColor(new Color(255, 255, 255, 30));
        g2.fillRoundRect(0, 0, getWidth(), getHeight() / 2, 8, 8);

        g2.dispose();
        super.paintComponent(g);   // draws the label text on top
    }
}
