package sportsanalytics.gui;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import sportsanalytics.co2.BPlusTree;
import sportsanalytics.co2.FenwickTree;
import sportsanalytics.co2.SegmentTree;
import sportsanalytics.model.Athlete;

public class PerformancePanel extends JPanel {
    private final MainFrame parentFrame;

    private JComboBox<String> athleteCombo;
    private JLabel lblScoreTable;

    private JTextField txtRangeL, txtRangeR;
    private JTextField txtUpdateIndex, txtUpdateValue;
    private JLabel lblSumResult, lblMinResult, lblMaxResult, lblFenwickResult;

    private JTextField txtScoreFrom, txtScoreTo;
    private JTextArea txtScoreRangeResult;

    private Athlete selectedAthlete;
    private SegmentTree segmentTree;
    private FenwickTree fenwickTree;
    private BPlusTree scoreBPlusTree;

    private SegmentTreeVisualizer stVisualizer;
    private int highlightL = -1;
    private int highlightR = -1;

    public PerformancePanel(MainFrame frame) {
        this.parentFrame = frame;
        setLayout(new BorderLayout());
        setBackground(Theme.BG_COLOR);
        initComponents();
        refreshData();
    }

    private void initComponents() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Theme.SIDEBAR_BG);
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(43, 44, 64)));
        JLabel lblSel = new JLabel("Select Athlete: ");
        lblSel.setFont(Theme.FONT_BOLD);
        lblSel.setForeground(Theme.TEXT_MAIN);
        topPanel.add(lblSel);
        athleteCombo = new JComboBox<>();
        Theme.styleComboBox(athleteCombo);
        athleteCombo.addActionListener(e -> handleAthleteSelected());
        topPanel.add(athleteCombo);
        add(topPanel, BorderLayout.NORTH);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(Theme.BG_COLOR);
        leftPanel.setPreferredSize(new Dimension(400, 720));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 8));

        lblScoreTable = new JLabel(" ");
        lblScoreTable.setAlignmentX(Component.LEFT_ALIGNMENT);

        // --- Step 1: B+ tree (placed early so it is visible without scrolling) ---
        JPanel scoreCard = Theme.createCardPanel();
        makeCompactCard(scoreCard);
        scoreCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = compactInsets();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JLabel lblStep2B = new JLabel("Step 1 — Find scores in a range (B+ Tree)");
        lblStep2B.setFont(Theme.FONT_SUBTITLE);
        lblStep2B.setForeground(Theme.ACCENT);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        scoreCard.add(lblStep2B, gbc);

        JLabel lblStep2BHelp = new JLabel(
                "<html>Enter a score range and search the current athlete's score tree.</html>");
        lblStep2BHelp.setFont(Theme.FONT_SMALL);
        lblStep2BHelp.setForeground(Theme.TEXT_MUTED);
        gbc.gridy = 1;
        scoreCard.add(lblStep2BHelp, gbc);

        gbc.gridy = 2; gbc.gridx = 0; gbc.gridwidth = 2;
        scoreCard.add(lblScoreTable, gbc);

        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 1;
        JLabel lblFrom = new JLabel("Lowest score:");
        lblFrom.setFont(Theme.FONT_BOLD);
        lblFrom.setForeground(Theme.TEXT_MAIN);
        scoreCard.add(lblFrom, gbc);
        txtScoreFrom = new JTextField();
        txtScoreFrom.setColumns(8);
        Theme.styleTextField(txtScoreFrom);
        makeCompactTextField(txtScoreFrom);
        gbc.gridx = 1;
        scoreCard.add(txtScoreFrom, gbc);

        gbc.gridy = 4; gbc.gridx = 0;
        JLabel lblTo = new JLabel("Highest score:");
        lblTo.setFont(Theme.FONT_BOLD);
        lblTo.setForeground(Theme.TEXT_MAIN);
        scoreCard.add(lblTo, gbc);
        txtScoreTo = new JTextField();
        txtScoreTo.setColumns(8);
        Theme.styleTextField(txtScoreTo);
        makeCompactTextField(txtScoreTo);
        gbc.gridx = 1;
        scoreCard.add(txtScoreTo, gbc);

        JButton btnFindScores = new JButton("Find scores in range");
        Theme.styleButton(btnFindScores, Theme.SUCCESS, Theme.SIDEBAR_BG);
        makeCompactButton(btnFindScores);
        btnFindScores.addActionListener(e -> runScoreRangeQuery());
        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(6, 3, 3, 3);
        scoreCard.add(btnFindScores, gbc);
        gbc.insets = compactInsets();

        txtScoreRangeResult = new JTextArea(2, 20);
        txtScoreRangeResult.setEditable(false);
        txtScoreRangeResult.setLineWrap(true);
        txtScoreRangeResult.setWrapStyleWord(true);
        txtScoreRangeResult.setBackground(new Color(49, 50, 68));
        txtScoreRangeResult.setForeground(Theme.TEXT_MAIN);
        txtScoreRangeResult.setFont(Theme.FONT_SMALL);
        txtScoreRangeResult.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
        txtScoreRangeResult.setText("Enter a score range and click Find scores in range.");
        gbc.gridy = 6; gbc.gridwidth = 2;
        scoreCard.add(txtScoreRangeResult, gbc);

        leftPanel.add(scoreCard);
        leftPanel.add(Box.createVerticalStrut(8));

        // --- Step 3: segment tree by index (matches tree diagram on the right) ---
        JPanel queryCard = Theme.createCardPanel();
        makeCompactCard(queryCard);
        queryCard.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.insets = compactInsets();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JLabel lblStep3 = new JLabel("Step 2 — Segment tree (by match position)");
        lblStep3.setFont(Theme.FONT_SUBTITLE);
        lblStep3.setForeground(Theme.ACCENT);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        queryCard.add(lblStep3, gbc);

        JLabel lblStep3Help = new JLabel("Uses match # 0–7. See the tree diagram on the right.");
        lblStep3Help.setFont(Theme.FONT_SMALL);
        lblStep3Help.setForeground(Theme.TEXT_MUTED);
        gbc.gridy = 1;
        queryCard.add(lblStep3Help, gbc);
        gbc.gridwidth = 1;

        gbc.gridy = 2; gbc.gridx = 0;
        JLabel lblL = new JLabel("From match #:");
        lblL.setFont(Theme.FONT_BOLD);
        lblL.setForeground(Theme.TEXT_MAIN);
        queryCard.add(lblL, gbc);
        txtRangeL = new JTextField("0");
        Theme.styleTextField(txtRangeL);
        makeCompactTextField(txtRangeL);
        gbc.gridx = 1;
        queryCard.add(txtRangeL, gbc);

        gbc.gridy = 3; gbc.gridx = 0;
        JLabel lblR = new JLabel("To match #:");
        lblR.setFont(Theme.FONT_BOLD);
        lblR.setForeground(Theme.TEXT_MAIN);
        queryCard.add(lblR, gbc);
        txtRangeR = new JTextField("3");
        Theme.styleTextField(txtRangeR);
        makeCompactTextField(txtRangeR);
        gbc.gridx = 1;
        queryCard.add(txtRangeR, gbc);

        JButton btnQuery = new JButton("Calculate range");
        Theme.styleButton(btnQuery, Theme.ACCENT, Theme.SIDEBAR_BG);
        makeCompactButton(btnQuery);
        btnQuery.addActionListener(e -> runRangeQueries());
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(6, 3, 3, 3);
        queryCard.add(btnQuery, gbc);
        gbc.insets = compactInsets();

        String[] resLabels = {"Sum:", "Min:", "Max:", "Fenwick sum:"};
        JLabel[] valLabels = {
            lblSumResult = new JLabel("-"),
            lblMinResult = new JLabel("-"),
            lblMaxResult = new JLabel("-"),
            lblFenwickResult = new JLabel("-")
        };
        for (int i = 0; i < resLabels.length; i++) {
            gbc.gridy = i + 5; gbc.gridx = 0; gbc.gridwidth = 1;
            JLabel lblRes = new JLabel(resLabels[i]);
            lblRes.setFont(Theme.FONT_BOLD);
            lblRes.setForeground(Theme.TEXT_MUTED);
            queryCard.add(lblRes, gbc);
            valLabels[i].setFont(Theme.FONT_BOLD);
            valLabels[i].setForeground(Theme.SUCCESS);
            gbc.gridx = 1;
            queryCard.add(valLabels[i], gbc);
        }
        leftPanel.add(queryCard);
        leftPanel.add(Box.createVerticalStrut(8));

        // --- Point update ---
        JPanel updateCard = Theme.createCardPanel();
        makeCompactCard(updateCard);
        updateCard.setLayout(new GridBagLayout());
        GridBagConstraints updateGbc = new GridBagConstraints();
        updateGbc.insets = compactInsets();
        updateGbc.fill = GridBagConstraints.HORIZONTAL;
        updateGbc.weightx = 1;

        JLabel lblUpTitle = new JLabel("Edit one score");
        lblUpTitle.setFont(Theme.FONT_SUBTITLE);
        lblUpTitle.setForeground(Theme.ACCENT);
        updateGbc.gridx = 0; updateGbc.gridy = 0; updateGbc.gridwidth = 2;
        updateCard.add(lblUpTitle, updateGbc);
        updateGbc.gridwidth = 1;

        updateGbc.gridy = 1; updateGbc.gridx = 0;
        JLabel lblIndex = new JLabel("Match #:");
        lblIndex.setFont(Theme.FONT_BOLD);
        lblIndex.setForeground(Theme.TEXT_MAIN);
        updateCard.add(lblIndex, updateGbc);
        txtUpdateIndex = new JTextField();
        Theme.styleTextField(txtUpdateIndex);
        makeCompactTextField(txtUpdateIndex);
        updateGbc.gridx = 1;
        updateCard.add(txtUpdateIndex, updateGbc);

        updateGbc.gridy = 2; updateGbc.gridx = 0;
        JLabel lblValue = new JLabel("New score:");
        lblValue.setFont(Theme.FONT_BOLD);
        lblValue.setForeground(Theme.TEXT_MAIN);
        updateCard.add(lblValue, updateGbc);
        txtUpdateValue = new JTextField();
        Theme.styleTextField(txtUpdateValue);
        makeCompactTextField(txtUpdateValue);
        updateGbc.gridx = 1;
        updateCard.add(txtUpdateValue, updateGbc);

        JButton btnUpdate = new JButton("Save score");
        Theme.styleButton(btnUpdate, Theme.PURPLE, Theme.SIDEBAR_BG);
        makeCompactButton(btnUpdate);
        btnUpdate.addActionListener(e -> runPointUpdate());
        updateGbc.gridy = 3; updateGbc.gridx = 0; updateGbc.gridwidth = 2;
        updateGbc.insets = new Insets(6, 3, 3, 3);
        updateCard.add(btnUpdate, updateGbc);
        updateGbc.insets = compactInsets();
        leftPanel.add(updateCard);

        JScrollPane leftScroll = Theme.createCustomScrollPane(leftPanel);
        leftScroll.setPreferredSize(new Dimension(400, 0));
        leftScroll.setBorder(null);
        leftScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(leftScroll, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Theme.BG_COLOR);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 15));

        JPanel headViz = new JPanel();
        headViz.setLayout(new BoxLayout(headViz, BoxLayout.Y_AXIS));
        headViz.setOpaque(false);
        JLabel lblVizTitle = new JLabel("Tree diagram");
        lblVizTitle.setFont(Theme.FONT_SUBTITLE);
        lblVizTitle.setForeground(Theme.ACCENT);
        JLabel lblVizHelp = new JLabel(
                "Circle = score (or total inside a group). Yellow number under circle = match position (0–7).");
        lblVizHelp.setFont(Theme.FONT_SMALL);
        lblVizHelp.setForeground(Theme.TEXT_MUTED);
        headViz.add(lblVizTitle);
        headViz.add(Box.createVerticalStrut(4));
        headViz.add(lblVizHelp);
        rightPanel.add(headViz, BorderLayout.NORTH);

        stVisualizer = new SegmentTreeVisualizer();
        rightPanel.add(stVisualizer, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.CENTER);
    }

    private static Insets compactInsets() {
        return new Insets(3, 3, 3, 3);
    }

    private static void makeCompactCard(JPanel card) {
        card.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
    }

    private static void makeCompactButton(JButton button) {
        button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
    }

    private static void makeCompactTextField(JTextField textField) {
        Dimension compactSize = new Dimension(100, 30);
        textField.setMinimumSize(compactSize);
        textField.setPreferredSize(compactSize);
        textField.setFont(Theme.FONT_BODY);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(69, 71, 90), 2),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
    }

    private void handleAthleteSelected() {
        int selectedIndex = athleteCombo.getSelectedIndex();
        if (selectedIndex < 0) return;

        selectedAthlete = parentFrame.getAthletes().get(selectedIndex);
        segmentTree = new SegmentTree(selectedAthlete.getMatchScores());
        fenwickTree = new FenwickTree(selectedAthlete.getMatchScores());

        highlightL = -1;
        highlightR = -1;
        lblSumResult.setText("-");
        lblMinResult.setText("-");
        lblMaxResult.setText("-");
        lblFenwickResult.setText("-");

        syncScoreData();
        stVisualizer.repaint();
    }

    private void syncScoreData() {
        if (selectedAthlete == null) {
            lblScoreTable.setText("No athlete selected.");
            scoreBPlusTree = null;
            txtScoreRangeResult.setText("Select an athlete first.");
            return;
        }

        int[] scores = selectedAthlete.getMatchScores();
        lblScoreTable.setText(buildScoreTableHtml(scores));

        scoreBPlusTree = new BPlusTree();
        for (int score : scores) {
            scoreBPlusTree.insert(score);
        }

        txtScoreFrom.setText("");
        txtScoreTo.setText("");
        txtScoreRangeResult.setText("Enter a score range and click Find scores in range.");
    }

    private static String buildScoreTableHtml(int[] scores) {
        StringBuilder html = new StringBuilder("<html><table cellspacing='6' cellpadding='2'>");
        html.append("<tr><td><b>Match #</b></td>");
        for (int i = 0; i < scores.length; i++) {
            html.append("<td align='center'>").append(i).append("</td>");
        }
        html.append("</tr><tr><td><b>Score</b></td>");
        for (int score : scores) {
            html.append("<td align='center'><b>").append(score).append("</b></td>");
        }
        html.append("</tr></table></html>");
        return html.toString();
    }

    private void runRangeQueries() {
        if (segmentTree == null) return;
        try {
            int L = Integer.parseInt(txtRangeL.getText().trim());
            int R = Integer.parseInt(txtRangeR.getText().trim());
            int maxIdx = selectedAthlete.getMatchScores().length - 1;

            if (L < 0 || R > maxIdx || L > R) {
                JOptionPane.showMessageDialog(this,
                        "Use match numbers from 0 to " + maxIdx + " (From <= To).",
                        "Range Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            highlightL = L;
            highlightR = R;
            lblSumResult.setText(String.valueOf(segmentTree.querySum(L, R)));
            lblMinResult.setText(String.valueOf(segmentTree.queryMin(L, R)));
            lblMaxResult.setText(String.valueOf(segmentTree.queryMax(L, R)));
            lblFenwickResult.setText(String.valueOf(fenwickTree.rangeQuery(L, R)));
            stVisualizer.repaint();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter whole numbers for match positions.", "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void runPointUpdate() {
        if (segmentTree == null) return;
        try {
            int idx = Integer.parseInt(txtUpdateIndex.getText().trim());
            int val = Integer.parseInt(txtUpdateValue.getText().trim());
            int maxIdx = selectedAthlete.getMatchScores().length - 1;

            if (idx < 0 || idx > maxIdx) {
                JOptionPane.showMessageDialog(this, "Match # must be 0 to " + maxIdx, "Update Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            selectedAthlete.getMatchScores()[idx] = val;
            segmentTree.update(idx, val);
            fenwickTree.update(idx, val);
            syncScoreData();
            runRangeQueries();
            stVisualizer.repaint();
            txtUpdateIndex.setText("");
            txtUpdateValue.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter whole numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void runScoreRangeQuery() {
        if (scoreBPlusTree == null || selectedAthlete == null) {
            txtScoreRangeResult.setText("Select an athlete first.");
            return;
        }
        try {
            int lower = Integer.parseInt(txtScoreFrom.getText().trim());
            int upper = Integer.parseInt(txtScoreTo.getText().trim());
            if (lower > upper) {
                txtScoreRangeResult.setText("Lowest score must be less than or equal to highest score.");
                return;
            }

            List<Integer> found = scoreBPlusTree.rangeQuery(lower, upper);
            int min = Arrays.stream(selectedAthlete.getMatchScores()).min().orElse(0);
            int max = Arrays.stream(selectedAthlete.getMatchScores()).max().orElse(0);

            if (found.isEmpty()) {
                txtScoreRangeResult.setText(String.format(
                        "No scores between %d and %d.%nThis athlete's scores run from %d to %d.",
                        lower, upper, min, max));
            } else {
                txtScoreRangeResult.setText(String.format(
                        "Scores between %d and %d:%n%s%n%n(%d score(s) found — same values as in the athlete score list, sorted by B+ Tree)",
                        lower, upper, found, found.size()));
            }
        } catch (NumberFormatException ex) {
            txtScoreRangeResult.setText("Please enter whole numbers for lowest and highest score.");
        }
    }

    public void refreshData() {
        athleteCombo.removeAllItems();
        for (Athlete a : parentFrame.getAthletes()) {
            athleteCombo.addItem(a.getName() + " (" + a.getSport() + ")");
        }
        handleAthleteSelected();
    }

    private class SegmentTreeVisualizer extends JPanel {
        SegmentTreeVisualizer() {
            setBackground(Theme.CARD_BG);
            setBorder(BorderFactory.createLineBorder(new Color(69, 71, 90), 1));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (segmentTree == null || segmentTree.getRoot() == null) return;

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            drawSTNode(g2, segmentTree.getRoot(), getWidth() / 2, 40, getWidth() / 4);
        }

        private void drawSTNode(Graphics2D g2, SegmentTree.SegmentNode node, int x, int y, int hGap) {
            int radius = 26;
            boolean isLeaf = node.start == node.end;

            if (node.left != null) {
                g2.setColor(Theme.TEXT_MUTED);
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(x, y, x - hGap, y + 70);
                drawSTNode(g2, node.left, x - hGap, y + 70, hGap / 2);
            }
            if (node.right != null) {
                g2.setColor(Theme.TEXT_MUTED);
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(x, y, x + hGap, y + 70);
                drawSTNode(g2, node.right, x + hGap, y + 70, hGap / 2);
            }

            boolean inQueryRange = false;
            if (highlightL != -1 && highlightR != -1) {
                if (!(node.start > highlightR || node.end < highlightL)) {
                    inQueryRange = true;
                }
            }

            g2.setColor(inQueryRange ? Theme.SUCCESS : Theme.BG_COLOR);
            g2.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);
            g2.setColor(inQueryRange ? Theme.SIDEBAR_BG : Theme.PURPLE);
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(x - radius, y - radius, 2 * radius, 2 * radius);

            g2.setColor(inQueryRange ? Theme.SIDEBAR_BG : Theme.TEXT_MAIN);
            g2.setFont(isLeaf ? new Font(Theme.FONT_BOLD.getName(), Font.BOLD, 14) : Theme.FONT_BOLD);
            String insideText = String.valueOf(node.sum);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(insideText, x - fm.stringWidth(insideText) / 2, y + 5);

            String indexText = isLeaf ? String.valueOf(node.start) : (node.start + "–" + node.end);
            g2.setColor(inQueryRange ? Theme.SIDEBAR_BG : Theme.WARNING);
            g2.setFont(Theme.FONT_SMALL);
            int indexW = g2.getFontMetrics().stringWidth(indexText);
            g2.drawString(indexText, x - indexW / 2, y + radius + 16);
        }
    }
}
