package com.currencyconverter.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}

        setTitle("Currency Converter");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true);
        setMinimumSize(new Dimension(500, 500));

        getContentPane().setBackground(new Color(240, 245, 255));
        add(new ConverterPanel());

        pack();
        setSize(Math.max(getWidth(), 520), Math.max(getHeight(), 580));
        setLocationRelativeTo(null);
    }
}
