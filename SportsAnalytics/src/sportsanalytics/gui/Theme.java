package sportsanalytics.gui;

import javax.swing.*;
import java.awt.*;

public class Theme {
    public static final Color BG_COLOR = new Color(30, 30, 46);       // #1e1e2e (Deep Slate)
    public static final Color CARD_BG = new Color(24, 24, 37);        // #181825 (Dark Slate)
    public static final Color SIDEBAR_BG = new Color(17, 17, 27);     // #11111b (Very Dark)
    
    public static final Color TEXT_MAIN = new Color(205, 214, 244);   // #cdd6f4 (Off white)
    public static final Color TEXT_MUTED = new Color(166, 173, 200);  // #a6adc8 (Gray)
    
    public static final Color ACCENT = new Color(137, 180, 250);      // #89b4fa (Blue/Lavender)
    public static final Color SUCCESS = new Color(148, 226, 213);     // #94e2d5 (Teal)
    public static final Color WARNING = new Color(249, 226, 175);     // #f9e2af (Yellow)
    public static final Color ERROR = new Color(243, 139, 168);       // #f38ba8 (Coral)
    public static final Color PURPLE = new Color(203, 166, 247);      // #cba6f7 (Purple)

    public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font("SansSerif", Font.BOLD, 16);
    public static final Font FONT_BODY = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font("SansSerif", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 11);

    public static void styleButton(JButton button, Color bg, Color fg) {
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFont(FONT_BOLD);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /** Apply before creating text fields (Windows L&F otherwise uses low-contrast colors). */
    public static void configureInputDefaults() {
        Color darkBg = new Color(36, 39, 58);
        Color lightText = TEXT_MAIN;
        Color selectionBg = ACCENT;

        UIManager.put("TextField.background", darkBg);
        UIManager.put("TextField.foreground", lightText);
        UIManager.put("TextField.caretForeground", lightText);
        UIManager.put("TextField.inactiveForeground", TEXT_MUTED);
        UIManager.put("TextField.selectionBackground", selectionBg);
        UIManager.put("TextField.selectionForeground", SIDEBAR_BG);
        UIManager.put("TextField.opaque", Boolean.TRUE);

        UIManager.put("ComboBox.background", darkBg);
        UIManager.put("ComboBox.foreground", lightText);
        UIManager.put("ComboBox.selectionBackground", ACCENT);
        UIManager.put("ComboBox.selectionForeground", SIDEBAR_BG);
    }

    public static void styleTextField(JTextField textField) {
        textField.setOpaque(true);
        textField.setBackground(new Color(36, 39, 58));
        textField.setForeground(TEXT_MAIN);
        textField.setCaretColor(TEXT_MAIN);
        textField.setSelectedTextColor(SIDEBAR_BG);
        textField.setSelectionColor(ACCENT);
        textField.setFont(new Font(FONT_BODY.getName(), Font.PLAIN, 14));
        textField.setMinimumSize(new Dimension(100, 34));
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(69, 71, 90), 2),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                textField.setBackground(new Color(49, 50, 68));
                textField.setForeground(TEXT_MAIN);
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                textField.setBackground(new Color(36, 39, 58));
                textField.setForeground(TEXT_MAIN);
            }
        });
    }

    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setBackground(new Color(36, 39, 58));
        comboBox.setForeground(TEXT_MAIN);
        comboBox.setOpaque(true);
        comboBox.setFont(FONT_BODY);
        comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(ACCENT); // Accent blue highlight
                    c.setForeground(SIDEBAR_BG);
                } else {
                    c.setBackground(new Color(36, 39, 58));
                    c.setForeground(TEXT_MAIN);
                }
                return c;
            }
        });
        comboBox.setBorder(BorderFactory.createLineBorder(new Color(108, 112, 134), 1));
    }

    public static JScrollPane createCustomScrollPane(JComponent component) {
        JScrollPane scrollPane = new JScrollPane(component);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(BG_COLOR);
        return scrollPane;
    }

    public static JPanel createCardPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        return panel;
    }
}
