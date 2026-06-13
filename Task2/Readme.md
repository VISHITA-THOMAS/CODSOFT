# 🎓 Student Grade Calculator — Java Swing GUI

A complete, professional-grade Java desktop application for calculating student grades,
built with Java Swing and a clean object-oriented architecture.

---

## 📁 Project Structure

```
StudentGradeCalculator/
├── src/
│   └── StudentGradeCalculator.java   ← Single source file (7 classes)
├── out/
│   ├── StudentGradeCalculator.class
│   ├── AppTheme.class
│   ├── Student.class
│   ├── MainWindow.class
│   ├── HeaderPanel.class
│   ├── InputPanel.class
│   ├── ResultPanel.class
│   └── FlatButton.class
└── README.md
```

---

## 🚀 How to Compile & Run

### Prerequisites
- Java JDK 8 or higher installed

### Terminal / Command Prompt

```bash
# Step 1 — Compile
javac -d out src/StudentGradeCalculator.java

# Step 2 — Run
java -cp out StudentGradeCalculator
```

---

## 💻 Run in IntelliJ IDEA

1. Open IntelliJ → **File → New → Project from Existing Sources**
2. Select the `StudentGradeCalculator/` folder
3. Choose **"Create project from existing sources"** → Finish
4. Right-click `src/StudentGradeCalculator.java` → **Run 'StudentGradeCalculator.main()'**

## 💻 Run in VS Code

1. Install the **"Extension Pack for Java"** from the VS Code marketplace
2. Open the `StudentGradeCalculator/` folder in VS Code
3. Open `StudentGradeCalculator.java`
4. Click the ▶ **Run** button above `main()`, or press `F5`

---

## 🏗️ OOP Design — 7 Classes

| Class | Type | Responsibility |
|-------|------|----------------|
| `StudentGradeCalculator` | Entry point | Starts the EDT and creates `MainWindow` |
| `AppTheme` | Constants class | All colours and fonts in one place |
| `Student` | Data model | Stores inputs, calculates total / average / grade |
| `MainWindow` | JFrame | Top-level window; routes data between panels |
| `HeaderPanel` | JPanel | Blue gradient title bar |
| `InputPanel` | JPanel | Form fields, validation, button handlers |
| `ResultPanel` | JPanel | Displays grade badge, stats, progress bar, table |
| `FlatButton` | JButton | Custom painted button with hover effect |

---

## 🎓 Grade Scale

| Grade | Range |
|-------|-------|
| A+    | 90 – 100 |
| A     | 80 – 89  |
| B     | 70 – 79  |
| C     | 60 – 69  |
| D     | 50 – 59  |
| F     | Below 50 |

---

## 📝 Step-by-Step Code Explanation

### 1. Entry Point
```java
SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
```
Always launch Swing on the Event Dispatch Thread (EDT) — the thread Swing is designed to run on.

### 2. AppTheme
Centralises every Color and Font constant. Changing one line here
updates the whole app's look — great for maintainability.

### 3. Student (Model)
- Constructor receives a name and a `List<Double>` of marks
- Immediately calls `calculate()` which sums marks, divides by count, and calls `assignGrade()`
- `assignGrade()` is `static` so it can be reused per-subject in the results table

### 4. MainWindow (JFrame)
- Uses `BorderLayout`: HeaderPanel NORTH, a side-by-side content row CENTER
- Content row uses `GridBagLayout` with `weightx` to give InputPanel 55% width
- Acts as a bridge — passes `Student` objects from InputPanel to ResultPanel

### 5. InputPanel (Form)
- Top fields (name + spinner) built with `GridBagLayout` for precise alignment
- `buildSubjectFields()` rebuilds the dynamic mark-entry rows on demand
- `handleCalculate()` wraps all parsing in try/catch:
  - Empty name → `IllegalArgumentException`
  - Non-numeric mark → catches `NumberFormatException`, re-throws as user-friendly message
  - Out-of-range mark → `IllegalArgumentException`
- All errors shown with `JOptionPane.showMessageDialog()`

### 6. ResultPanel (Output)
- Uses `CardLayout` to switch between an empty placeholder and the results view
- Grade badge: large JLabel whose colour comes from `AppTheme.gradeColor()`
- `JProgressBar` visualises the average percentage at a glance
- `JTable` with `DefaultTableModel` shows per-subject marks and individual grades
- Grade column uses a custom `TableCellRenderer` to colour each grade independently

### 7. FlatButton
- `setContentAreaFilled(false)` disables Swing's default button painting
- `paintComponent()` draws a rounded rectangle manually with `Graphics2D`
- `MouseAdapter` tracks hover state to darken the colour on mouse-over

---

## 🖥️ Sample Output

**Input:**
- Name: Alice Johnson
- Subjects: 5
- Marks: 92, 85, 78, 61, 88

**Output:**
```
Grade:    A  (84.80%)
Total:    404 / 500
Average:  80.80%

Subject 1 → 92.0  → A+
Subject 2 → 85.0  → A
Subject 3 → 78.0  → B
Subject 4 → 61.0  → C
Subject 5 → 88.0  → A
```

---

## 🎨 GUI Design Explanation

- **Header** — Blue-to-purple gradient (`GradientPaint`) with white title and subtitle
- **Input card** — White card with a subtle grey border; text fields highlight blue on focus
- **Result card** — Lavender badge for the grade; progress bar colour matches the grade letter
- **Table** — Clean, borderless rows with coloured grade text per letter
- **Buttons** — Three colour-coded flat buttons: green (calculate), orange (reset), red (exit)
- **Layout** — 55/45 split so the form and results sit comfortably side by side

---
