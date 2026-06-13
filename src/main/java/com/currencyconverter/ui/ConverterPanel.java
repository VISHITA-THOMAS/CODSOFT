package com.currencyconverter.ui;

import com.currencyconverter.models.ConversionResult;
import com.currencyconverter.models.Currency;
import com.currencyconverter.services.ExchangeRateService;
import com.currencyconverter.utils.CurrencyData;
import com.currencyconverter.utils.FormatUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Main converter UI.
 * Uses plain GridBagLayout — no custom painting — so it renders
 * correctly on every platform (Windows, Mac, Linux).
 */
public class ConverterPanel extends JPanel {

    // colors
    private static final Color C_BG       = new Color(240, 245, 255);
    private static final Color C_WHITE    = Color.WHITE;
    private static final Color C_BLUE     = new Color( 59, 130, 246);
    private static final Color C_BLUE2    = new Color( 37,  99, 235);
    private static final Color C_LBLUE    = new Color(219, 234, 254);
    private static final Color C_BORDER   = new Color(209, 219, 240);
    private static final Color C_DARK     = new Color( 15,  23,  42);
    private static final Color C_MUTED    = new Color(100, 116, 139);
    private static final Color C_GREEN    = new Color( 22, 163,  74);
    private static final Color C_RED      = new Color(220,  38,  38);
    private static final Color C_RESULT   = new Color(235, 245, 255);

    private final ExchangeRateService service = new ExchangeRateService();

    private JComboBox<Currency> fromCombo, toCombo;
    private JTextField          amountField;
    private JButton             convertBtn, clearBtn;
    private JProgressBar        loader;
    private JPanel              resultCard;
    private JLabel              resultLabel, rateLabel, statusLabel;

    public ConverterPanel() {
        setLayout(new BorderLayout());
        setBackground(C_BG);
        setBorder(new EmptyBorder(24, 24, 24, 24));
        add(buildCard(), BorderLayout.CENTER);
    }

    // ── card ─────────────────────────────────────────────────────────────────

    private JPanel buildCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(C_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(C_BORDER, 1),
                new EmptyBorder(24, 24, 24, 24)));

        card.add(titleArea());
        card.add(vgap(16));
        card.add(new JSeparator());
        card.add(vgap(16));
        card.add(currencyArea());
        card.add(vgap(14));
        card.add(label("Amount to Convert"));
        card.add(vgap(5));
        card.add(amountArea());
        card.add(vgap(16));
        card.add(buttonArea());
        card.add(vgap(8));
        card.add(loaderArea());
        card.add(vgap(10));
        card.add(resultArea());
        card.add(vgap(6));
        card.add(statusArea());

        return card;
    }

    private Component vgap(int h) {
        return Box.createRigidArea(new Dimension(0, h));
    }

    // ── title ─────────────────────────────────────────────────────────────────

    private JPanel titleArea() {
        JPanel p = row();
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setBackground(C_WHITE);

        JLabel title = new JLabel("  Currency Converter");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(C_DARK);

        JLabel sub = new JLabel("Live exchange rates  \u2022  Frankfurter / ECB  \u2022  No API key needed");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sub.setForeground(C_MUTED);
        sub.setBorder(new EmptyBorder(3, 4, 0, 0));

        col.add(title);
        col.add(sub);
        p.add(col);
        return p;
    }

    // ── currency dropdowns ────────────────────────────────────────────────────

    private JPanel currencyArea() {
        fromCombo = buildCombo("USD");
        toCombo   = buildCombo("INR");

        JButton swap = new JButton("\u21c4");  // ⇄
        swap.setFont(new Font("Segoe UI", Font.BOLD, 15));
        swap.setForeground(C_BLUE);
        swap.setBackground(C_LBLUE);
        swap.setFocusPainted(false);
        swap.setBorder(new LineBorder(new Color(199, 210, 254), 1));
        swap.setPreferredSize(new Dimension(44, 38));
        swap.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        swap.setToolTipText("Swap currencies");
        swap.addActionListener(e -> {
            Currency f = (Currency) fromCombo.getSelectedItem();
            Currency t = (Currency) toCombo.getSelectedItem();
            fromCombo.setSelectedItem(t);
            toCombo.setSelectedItem(f);
        });

        // Labels row
        JPanel labels = row();
        JLabel lFrom = label("From Currency");
        JLabel lTo   = label("To Currency");
        lFrom.setPreferredSize(new Dimension(180, 20));
        lTo.setPreferredSize(new Dimension(180, 20));
        labels.add(lFrom);
        labels.add(Box.createHorizontalStrut(52)); // gap for swap button
        labels.add(lTo);

        // Combos row
        JPanel combos = row();
        fromCombo.setPreferredSize(new Dimension(185, 38));
        toCombo.setPreferredSize(new Dimension(185, 38));
        combos.add(fromCombo);
        combos.add(Box.createHorizontalStrut(6));
        combos.add(swap);
        combos.add(Box.createHorizontalStrut(6));
        combos.add(toCombo);

        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setBackground(C_WHITE);
        col.setAlignmentX(LEFT_ALIGNMENT);
        col.add(labels);
        col.add(Box.createRigidArea(new Dimension(0, 5)));
        col.add(combos);
        return col;
    }

    private JComboBox<Currency> buildCombo(String defaultCode) {
        JComboBox<Currency> cb = new JComboBox<>(CurrencyData.asArray());
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cb.setBackground(C_WHITE);
        cb.setMaximumRowCount(12);
        select(cb, defaultCode);
        return cb;
    }

    // ── amount field ──────────────────────────────────────────────────────────

    private JTextField amountArea() {
        amountField = new JTextField();
        amountField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        amountField.setForeground(C_DARK);
        amountField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        amountField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(C_BORDER, 1),
                new EmptyBorder(8, 12, 8, 12)));
        amountField.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                amountField.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(C_BLUE, 2), new EmptyBorder(7, 11, 7, 11)));
            }
            @Override public void focusLost(FocusEvent e) {
                amountField.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(C_BORDER, 1), new EmptyBorder(8, 12, 8, 12)));
            }
        });
        amountField.addActionListener(e -> doConvert());
        return amountField;
    }

    // ── buttons ───────────────────────────────────────────────────────────────

    private JPanel buttonArea() {
        convertBtn = new JButton("Convert");
        convertBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        convertBtn.setForeground(C_WHITE);
        convertBtn.setBackground(C_BLUE);
        convertBtn.setOpaque(true);
        convertBtn.setBorderPainted(false);
        convertBtn.setFocusPainted(false);
        convertBtn.setPreferredSize(new Dimension(200, 44));
        convertBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        convertBtn.addActionListener(e -> doConvert());
        convertBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { convertBtn.setBackground(C_BLUE2); }
            public void mouseExited (MouseEvent e) { convertBtn.setBackground(C_BLUE);  }
        });

        clearBtn = new JButton("Clear");
        clearBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        clearBtn.setForeground(C_BLUE);
        clearBtn.setBackground(new Color(239, 246, 255));
        clearBtn.setOpaque(true);
        clearBtn.setBorder(new LineBorder(new Color(147, 197, 253), 1));
        clearBtn.setFocusPainted(false);
        clearBtn.setPreferredSize(new Dimension(110, 44));
        clearBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        clearBtn.addActionListener(e -> doClear());

        JPanel p = row();
        p.add(convertBtn);
        p.add(Box.createHorizontalStrut(10));
        p.add(clearBtn);
        return p;
    }

    // ── loader ────────────────────────────────────────────────────────────────

    private JProgressBar loaderArea() {
        loader = new JProgressBar();
        loader.setIndeterminate(true);
        loader.setVisible(false);
        loader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 5));
        loader.setPreferredSize(new Dimension(0, 5));
        loader.setForeground(C_BLUE);
        loader.setBackground(C_LBLUE);
        return loader;
    }

    // ── result card ───────────────────────────────────────────────────────────

    private JPanel resultArea() {
        resultCard = new JPanel();
        resultCard.setLayout(new BoxLayout(resultCard, BoxLayout.Y_AXIS));
        resultCard.setBackground(C_RESULT);
        resultCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(147, 197, 253), 1),
                new EmptyBorder(14, 16, 14, 16)));
        resultCard.setVisible(false);
        resultCard.setAlignmentX(LEFT_ALIGNMENT);

        resultLabel = new JLabel(" ");
        resultLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        resultLabel.setForeground(C_BLUE);

        rateLabel = new JLabel(" ");
        rateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rateLabel.setForeground(C_MUTED);
        rateLabel.setBorder(new EmptyBorder(6, 0, 0, 0));

        resultCard.add(resultLabel);
        resultCard.add(rateLabel);
        return resultCard;
    }

    // ── status ────────────────────────────────────────────────────────────────

    private JLabel statusArea() {
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(C_MUTED);
        statusLabel.setAlignmentX(LEFT_ALIGNMENT);
        return statusLabel;
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    /** A left-aligned, white-background horizontal flow panel */
    private JPanel row() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.setBackground(C_WHITE);
        p.setAlignmentX(LEFT_ALIGNMENT);
        return p;
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(C_DARK);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private void select(JComboBox<Currency> cb, String code) {
        for (int i = 0; i < cb.getItemCount(); i++)
            if (cb.getItemAt(i).getCode().equals(code)) { cb.setSelectedIndex(i); return; }
        cb.setSelectedIndex(0);
    }

    // ── actions ───────────────────────────────────────────────────────────────

    private void doConvert() {
        String txt = amountField.getText().trim();
        if (!FormatUtils.isValidAmount(txt)) {
            showError("Please enter a valid positive number.");
            amountField.requestFocus();
            return;
        }
        Currency from = (Currency) fromCombo.getSelectedItem();
        Currency to   = (Currency) toCombo.getSelectedItem();
        if (from != null && to != null && from.getCode().equals(to.getCode())) {
            showError("Please choose two different currencies.");
            return;
        }
        double amount = FormatUtils.parseAmount(txt);
        setLoading(true);

        new SwingWorker<ConversionResult, Void>() {
            @Override protected ConversionResult doInBackground() throws Exception {
                return service.convert(amount, from, to);
            }
            @Override protected void done() {
                setLoading(false);
                try { showResult(get()); }
                catch (Exception ex) {
                    Throwable c = ex.getCause() != null ? ex.getCause() : ex;
                    showError(c.getMessage());
                }
            }
        }.execute();
    }

    private void doClear() {
        amountField.setText("");
        select(fromCombo, "USD");
        select(toCombo,   "INR");
        resultCard.setVisible(false);
        statusLabel.setText(" ");
        amountField.requestFocus();
        revalidate(); repaint();
    }

    private void setLoading(boolean on) {
        loader.setVisible(on);
        convertBtn.setEnabled(!on);
        convertBtn.setText(on ? "Converting..." : "Convert");
        if (on) { statusLabel.setText("Fetching live rate..."); statusLabel.setForeground(C_MUTED); }
        resultCard.setVisible(false);
    }

    private void showResult(ConversionResult r) {
        resultLabel.setText(r.getFormattedResult());
        rateLabel.setText(r.getRateInfo());
        resultCard.setVisible(true);
        statusLabel.setText("Conversion successful");
        statusLabel.setForeground(C_GREEN);
        revalidate(); repaint();
    }

    private void showError(String msg) {
        statusLabel.setText(msg);
        statusLabel.setForeground(C_RED);
        resultCard.setVisible(false);
        revalidate(); repaint();
    }
}
