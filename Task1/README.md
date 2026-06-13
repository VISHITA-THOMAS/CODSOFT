# 🎯 Number Guessing Game — Java Swing GUI

A polished desktop GUI version of the Number Guessing Game built with Java Swing.
Features a dark neon arcade aesthetic with custom-painted buttons, animated feedback,
multi-screen navigation, and a session scoreboard.

---

## 📁 Project Structure

```
NumberGuessingGameSwing/
├── src/
│   └── NumberGuessingGameSwing.java   ← All source code
├── out/
│   ├── NumberGuessingGameSwing.class
│   ├── GameFrame.class
│   ├── WelcomePanel.class
│   ├── GamePanel.class
│   ├── GameOverPanel.class
│   └── NeonButton.class
└── README.md
```

---

## 🚀 How to Compile & Run

### Prerequisites
- Java JDK 8 or higher
- A terminal / command prompt

### Compile
```bash
javac -d out src/NumberGuessingGameSwing.java
```

### Run
```bash
java -cp out NumberGuessingGameSwing
```

---

## 🖥️ Screens

| Screen | Description |
|---|---|
| **Welcome / Menu** | Title, rules summary, Start Game button |
| **Game Screen** | Attempt dots, feedback label, guess input, history log |
| **Game Over** | Win/loss result, answer reveal, session score, play-again |

---

## 🏗️ OOP Design

| Class | Role |
|---|---|
| `NumberGuessingGameSwing` | Entry point — launches the EDT and creates `GameFrame` |
| `GameFrame` | Top-level JFrame; owns `CardLayout` for screen switching and the session score |
| `WelcomePanel` | Menu screen with rules card and start button |
| `GamePanel` | Active round: input handling, comparison logic, dots, history log, shake animation |
| `GameOverPanel` | Result display; receives data from `GameFrame` via `update()` |
| `NeonButton` | Custom `JButton` with neon border glow and hover animation |

---

## 🎨 UI Highlights

- **Dark neon arcade** colour palette (navy + electric cyan + hot pink + mint green)
- **Attempt dots** (● used / ○ remaining) updated after every guess
- **Shake animation** on invalid input
- **Scrollable history log** listing every guess with direction tag
- **1.5-second delay** before screen flip so players can read the result
- **Enter key** submits a guess (no need to click the button)
