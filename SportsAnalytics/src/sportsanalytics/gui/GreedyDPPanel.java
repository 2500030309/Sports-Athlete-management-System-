package sportsanalytics.gui;

import sportsanalytics.model.Athlete;
import sportsanalytics.co6.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GreedyDPPanel extends JPanel {
    private final MainFrame parentFrame;

    // Selector sidebar
    private JList<String> listTopics;
    private JPanel mainCardPanel;
    private CardLayout cardLayout;

    // Activity Selection components
    private List<ActivitySelection.Activity> activities;
    private List<ActivitySelection.Activity> selectedActivities = new ArrayList<>();
    private TimelinePanel activityTimeline;

    // Fractional Knapsack components
    private List<FractionalKnapsack.Item> nutritionItems;
    private JTextField txtFractionalCap;
    private JTextArea txtFractionalResult;

    // 0/1 Knapsack components
    private List<Knapsack01.Item> sponsorItems;
    private JTextField txt01Capacity;
    private JTextArea txt01Result;

    // LIS components
    private JComboBox<String> lisAthleteCombo;
    private LISStreakPanel lisStreakPanel;
    private List<Integer> lisHighlights = new ArrayList<>();

    public GreedyDPPanel(MainFrame frame) {
        this.parentFrame = frame;
        setLayout(new BorderLayout());
        setBackground(Theme.BG_COLOR);

        initSampleData();
        initComponents();
    }

    private void initSampleData() {
        // Sample Activities (Training sessions)
        activities = new ArrayList<>();
        activities.add(new ActivitySelection.Activity("Morning Cardio", 6, 8));
        activities.add(new ActivitySelection.Activity("Weight Training", 8, 10));
        activities.add(new ActivitySelection.Activity("Yoga Recovery", 9, 11));
        activities.add(new ActivitySelection.Activity("Nutrition Seminar", 10, 12));
        activities.add(new ActivitySelection.Activity("Team Practice", 13, 15));
        activities.add(new ActivitySelection.Activity("Tactical Meeting", 14, 16));
        activities.add(new ActivitySelection.Activity("Physio Session", 15, 17));
        activities.add(new ActivitySelection.Activity("Evening Swim", 17, 19));

        // Sample Nutrition Items
        nutritionItems = new ArrayList<>();
        nutritionItems.add(new FractionalKnapsack.Item("Whey Protein", 3.0, 120.0));
        nutritionItems.add(new FractionalKnapsack.Item("Creatine Pow", 1.0, 50.0));
        nutritionItems.add(new FractionalKnapsack.Item("Carb Gel Pack", 2.0, 40.0));
        nutritionItems.add(new FractionalKnapsack.Item("Energy Bars", 4.0, 60.0));
        nutritionItems.add(new FractionalKnapsack.Item("Multivitamins", 0.5, 30.0));

        // Sample Sponsor Items for 0/1 Knapsack
        sponsorItems = new ArrayList<>();
        sponsorItems.add(new Knapsack01.Item("Nike Gold Pack", 3, 10));
        sponsorItems.add(new Knapsack01.Item("Adidas Elite Pack", 4, 12));
        sponsorItems.add(new Knapsack01.Item("RedBull Fuel", 2, 7));
        sponsorItems.add(new Knapsack01.Item("Gatorade Hydration", 5, 13));
        sponsorItems.add(new Knapsack01.Item("UnderArmour Gear", 6, 15));
    }

    private void initComponents() {
        // --- Inner Sidebar for CO6 Topics ---
        JPanel innerSidebar = new JPanel(new BorderLayout());
        innerSidebar.setBackground(Theme.SIDEBAR_BG);
        innerSidebar.setPreferredSize(new Dimension(240, 800));
        innerSidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(43, 44, 64)));

        JLabel lblSidebarTitle = new JLabel("  CO6 Topics & Algos", JLabel.LEFT);
        lblSidebarTitle.setFont(Theme.FONT_SUBTITLE);
        lblSidebarTitle.setForeground(Theme.ACCENT);
        lblSidebarTitle.setPreferredSize(new Dimension(240, 50));
        innerSidebar.add(lblSidebarTitle, BorderLayout.NORTH);

        String[] topics = {
            "Activity Selection",
            "Fractional Knapsack",
            "0/1 Knapsack (DP)",
            "LIS Streak Visualizer"
        };

        listTopics = new JList<>(topics);
        listTopics.setBackground(Theme.SIDEBAR_BG);
        listTopics.setForeground(Theme.TEXT_MUTED);
        listTopics.setFont(Theme.FONT_BOLD);
        listTopics.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listTopics.setFixedCellHeight(45);
        listTopics.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
                if (isSelected) {
                    lbl.setBackground(Theme.BG_COLOR);
                    lbl.setForeground(Theme.SUCCESS);
                } else {
                    lbl.setBackground(Theme.SIDEBAR_BG);
                    lbl.setForeground(Theme.TEXT_MUTED);
                }
                return lbl;
            }
        });

        listTopics.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = listTopics.getSelectedValue();
                cardLayout.show(mainCardPanel, selected);
            }
        });

        innerSidebar.add(new JScrollPane(listTopics), BorderLayout.CENTER);
        add(innerSidebar, BorderLayout.WEST);

        // --- Main Workspace Area with CardLayout ---
        cardLayout = new CardLayout();
        mainCardPanel = new JPanel(cardLayout);
        mainCardPanel.setBackground(Theme.BG_COLOR);

        mainCardPanel.add(buildActivitySelectionPanel(), "Activity Selection");
        mainCardPanel.add(buildFractionalKnapsackPanel(), "Fractional Knapsack");
        mainCardPanel.add(buildKnapsack01Panel(), "0/1 Knapsack (DP)");
        mainCardPanel.add(buildLisPanel(), "LIS Streak Visualizer");

        add(mainCardPanel, BorderLayout.CENTER);
        listTopics.setSelectedIndex(0); // select first by default
    }

    // ==========================================
    // 1. Activity Selection Card Build
    // ==========================================
    private JPanel buildActivitySelectionPanel() {
        JPanel card = Theme.createCardPanel();
        card.setLayout(new BorderLayout(10, 10));

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        controlPanel.setOpaque(false);

        JLabel lblInfo = new JLabel("<html><b>Activity Selection (Greedy Scheduler):</b> Maximize non-overlapping training sessions.</html>");
        lblInfo.setFont(Theme.FONT_BODY);
        lblInfo.setForeground(Theme.TEXT_MAIN);
        controlPanel.add(lblInfo);

        JButton btnSolve = new JButton("Run Greedy Selection");
        Theme.styleButton(btnSolve, Theme.SUCCESS, Theme.SIDEBAR_BG);
        btnSolve.addActionListener(e -> {
            selectedActivities = ActivitySelection.selectActivities(activities);
            activityTimeline.repaint();
        });
        controlPanel.add(btnSolve);

        JButton btnReset = new JButton("Reset");
        Theme.styleButton(btnReset, Theme.TEXT_MUTED, Theme.SIDEBAR_BG);
        btnReset.addActionListener(e -> {
            selectedActivities.clear();
            activityTimeline.repaint();
        });
        controlPanel.add(btnReset);

        card.add(controlPanel, BorderLayout.NORTH);

        activityTimeline = new TimelinePanel();
        card.add(activityTimeline, BorderLayout.CENTER);

        return card;
    }

    private class TimelinePanel extends JPanel {
        TimelinePanel() {
            setBackground(Theme.CARD_BG);
            setBorder(BorderFactory.createLineBorder(new Color(69, 71, 90), 1));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int startHour = 5;
            int endHour = 21;
            int leftMargin = 90;
            int rightMargin = 50;
            int timelineWidth = getWidth() - leftMargin - rightMargin;

            // Draw hour timeline
            g2.setColor(new Color(69, 71, 90));
            g2.drawLine(leftMargin, 80, leftMargin + timelineWidth, 80);

            for (int h = startHour; h <= endHour; h++) {
                int x = leftMargin + (h - startHour) * (timelineWidth / (endHour - startHour));
                g2.drawLine(x, 75, x, 85);
                g2.setFont(Theme.FONT_SMALL);
                g2.setColor(Theme.TEXT_MUTED);
                String hourStr = String.format("%02d:00", h);
                g2.drawString(hourStr, x - g2.getFontMetrics().stringWidth(hourStr) / 2, 65);
            }

            // Draw Activity bars
            int startY = 120;
            int rowHeight = 35;
            for (int i = 0; i < activities.size(); i++) {
                ActivitySelection.Activity act = activities.get(i);
                int xStart = leftMargin + (act.start - startHour) * (timelineWidth / (endHour - startHour));
                int xEnd = leftMargin + (act.end - startHour) * (timelineWidth / (endHour - startHour));
                int width = xEnd - xStart;
                int y = startY + i * rowHeight;

                boolean isSelected = selectedActivities.contains(act);
                g2.setColor(isSelected ? Theme.SUCCESS : new Color(49, 50, 68));
                g2.fillRoundRect(xStart, y, width, 25, 6, 6);

                g2.setColor(isSelected ? Theme.SIDEBAR_BG : Theme.TEXT_MAIN);
                g2.setFont(Theme.FONT_BOLD);
                g2.drawString(act.name, xStart + 10, y + 17);

                // Print start/end hours to the left of the bar to prevent overlapping
                g2.setFont(Theme.FONT_SMALL);
                g2.setColor(Theme.TEXT_MUTED);
                String times = String.format("%02d:00 - %02d:00", act.start, act.end);
                int timesWidth = g2.getFontMetrics().stringWidth(times);
                g2.drawString(times, xStart - timesWidth - 8, y + 16);
            }
        }
    }

    // ==========================================
    // 2. Fractional Knapsack Card Build
    // ==========================================
    private JPanel buildFractionalKnapsackPanel() {
        JPanel card = Theme.createCardPanel();
        card.setLayout(new BorderLayout(10, 10));

        // Instructions
        JLabel lblHeader = new JLabel("Fractional Knapsack: Nutrition Kit Value Optimizer (Greedy)");
        lblHeader.setFont(Theme.FONT_SUBTITLE);
        lblHeader.setForeground(Theme.ACCENT);
        card.add(lblHeader, BorderLayout.NORTH);

        // Center split
        JPanel center = new JPanel(new GridLayout(1, 2, 15, 0));
        center.setOpaque(false);

        // Left inputs
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        left.add(createLabel("Available Supplement Items (to pack by weight value ratio):"));
        StringBuilder sbItems = new StringBuilder("<html><ul>");
        for (FractionalKnapsack.Item it : nutritionItems) {
            sbItems.append(String.format("<li><b>%s</b>: weight %s kg, value $%s (Ratio: %.2f)</li>",
                    it.name, it.weight, it.value, it.getRatio()));
        }
        sbItems.append("</ul></html>");
        JLabel lblList = new JLabel(sbItems.toString());
        lblList.setForeground(Theme.TEXT_MAIN);
        left.add(lblList);
        left.add(Box.createVerticalStrut(15));

        left.add(createLabel("Set Backpack Weight Capacity (kg):"));
        txtFractionalCap = new JTextField("5.0");
        Theme.styleTextField(txtFractionalCap);
        left.add(txtFractionalCap);
        left.add(Box.createVerticalStrut(15));

        JButton btnRun = new JButton("Run Greedy Fractional Knapsack");
        Theme.styleButton(btnRun, Theme.ACCENT, Theme.SIDEBAR_BG);
        btnRun.addActionListener(e -> {
            try {
                double cap = Double.parseDouble(txtFractionalCap.getText().trim());
                FractionalKnapsack.Result res = FractionalKnapsack.solve(nutritionItems, cap);

                StringBuilder sbRes = new StringBuilder("OPTIMIZATION LOGS:\n\n");
                sbRes.append(String.format("Optimized Total Supplement Value: $%.2f%n", res.totalValue));
                sbRes.append("Packed items:\n");
                for (FractionalKnapsack.SelectedItem si : res.selectedItems) {
                    sbRes.append(String.format(" - %s: Pack %.1f%% (wt: %.2f kg, val: $%.2f)%n",
                            si.item.name, si.fraction * 100, si.item.weight * si.fraction, si.item.value * si.fraction));
                }
                txtFractionalResult.setText(sbRes.toString());
            } catch (Exception ex) {
                txtFractionalResult.setText("Invalid capacity input!");
            }
        });
        left.add(btnRun);
        left.add(Box.createVerticalGlue());

        center.add(left);

        // Right outputs
        txtFractionalResult = new JTextArea();
        txtFractionalResult.setBackground(Theme.BG_COLOR);
        txtFractionalResult.setForeground(Theme.SUCCESS);
        txtFractionalResult.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtFractionalResult.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(69, 71, 90)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        center.add(new JScrollPane(txtFractionalResult));

        card.add(center, BorderLayout.CENTER);
        return card;
    }

    // ==========================================
    // 3. 0/1 Knapsack Card Build (DP)
    // ==========================================
    private JPanel buildKnapsack01Panel() {
        JPanel card = Theme.createCardPanel();
        card.setLayout(new BorderLayout(10, 10));

        JLabel lblHeader = new JLabel("0/1 Knapsack: Sponsor Packages Budget Selection (DP)");
        lblHeader.setFont(Theme.FONT_SUBTITLE);
        lblHeader.setForeground(Theme.ACCENT);
        card.add(lblHeader, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 2, 15, 0));
        center.setOpaque(false);

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        left.add(createLabel("Available Sponsor Package Options:"));
        StringBuilder sbSponsors = new StringBuilder("<html><ul>");
        for (Knapsack01.Item it : sponsorItems) {
            sbSponsors.append(String.format("<li><b>%s</b>: Cost %d, Marketing Value %d</li>",
                    it.name, it.weight, it.value));
        }
        sbSponsors.append("</ul></html>");
        JLabel lblList = new JLabel(sbSponsors.toString());
        lblList.setForeground(Theme.TEXT_MAIN);
        left.add(lblList);
        left.add(Box.createVerticalStrut(15));

        left.add(createLabel("Set Budget Capacity (wt limit):"));
        txt01Capacity = new JTextField("6");
        Theme.styleTextField(txt01Capacity);
        left.add(txt01Capacity);
        left.add(Box.createVerticalStrut(15));

        JButton btnRun = new JButton("Solve Optimal Sponsorship");
        Theme.styleButton(btnRun, Theme.SUCCESS, Theme.SIDEBAR_BG);
        btnRun.addActionListener(e -> {
            try {
                int cap = Integer.parseInt(txt01Capacity.getText().trim());
                long start = System.nanoTime();
                Knapsack01.Result res = Knapsack01.solveTabulation(sponsorItems, cap);
                long end = System.nanoTime();

                // Memo test
                int[][] memo = new int[sponsorItems.size() + 1][cap + 1];
                for (int[] row : memo) Arrays.fill(row, -1);
                Knapsack01.solveMemoization(sponsorItems, sponsorItems.size(), cap, memo);

                StringBuilder sbRes = new StringBuilder("OPTIMAL SOLVER REPORT (DP):\n\n");
                sbRes.append(String.format("Tabulation solved value: %d units%n", res.totalValue));
                sbRes.append(String.format("DP Backtracked Packages selected:%n"));
                for (Knapsack01.Item it : res.selectedItems) {
                    sbRes.append(String.format("  - %s (Cost: %d, Value: %d)%n", it.name, it.weight, it.value));
                }
                sbRes.append(String.format("%nPerformance: Tabulation completed in %.3f ms", (end - start) / 1e6));
                txt01Result.setText(sbRes.toString());
            } catch (Exception ex) {
                txt01Result.setText("Invalid Budget Input!");
            }
        });
        left.add(btnRun);
        left.add(Box.createVerticalGlue());

        center.add(left);

        txt01Result = new JTextArea();
        txt01Result.setBackground(Theme.BG_COLOR);
        txt01Result.setForeground(Theme.SUCCESS);
        txt01Result.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txt01Result.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(69, 71, 90)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        center.add(new JScrollPane(txt01Result));

        card.add(center, BorderLayout.CENTER);
        return card;
    }

    // ==========================================
    // 4. LIS Streak Card Build
    // ==========================================
    private JPanel buildLisPanel() {
        JPanel card = Theme.createCardPanel();
        card.setLayout(new BorderLayout(10, 10));

        JPanel control = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        control.setOpaque(false);

        JLabel lblSel = new JLabel("Athlete:");
        lblSel.setFont(Theme.FONT_BOLD);
        lblSel.setForeground(Theme.TEXT_MAIN);
        control.add(lblSel);

        lisAthleteCombo = new JComboBox<>();
        Theme.styleComboBox(lisAthleteCombo);
        lisAthleteCombo.addActionListener(e -> handleLisAthleteSelected());
        control.add(lisAthleteCombo);

        JButton btnCalculate = new JButton("Find Longest Performance Improvement Streak (LIS)");
        Theme.styleButton(btnCalculate, Theme.SUCCESS, Theme.SIDEBAR_BG);
        btnCalculate.addActionListener(e -> runLIS());
        control.add(btnCalculate);

        card.add(control, BorderLayout.NORTH);

        lisStreakPanel = new LISStreakPanel();
        card.add(lisStreakPanel, BorderLayout.CENTER);

        return card;
    }

    private void handleLisAthleteSelected() {
        lisHighlights.clear();
        lisStreakPanel.repaint();
    }

    private void runLIS() {
        int idx = lisAthleteCombo.getSelectedIndex();
        if (idx < 0) return;

        Athlete a = parentFrame.getAthletes().get(idx);
        LIS.Result res = LIS.compute(a.getMatchScores());

        lisHighlights = res.sequence;
        lisStreakPanel.repaint();
    }

    private class LISStreakPanel extends JPanel {
        LISStreakPanel() {
            setBackground(Theme.CARD_BG);
            setBorder(BorderFactory.createLineBorder(new Color(69, 71, 90), 1));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int idx = lisAthleteCombo.getSelectedIndex();
            if (idx < 0) return;

            Athlete a = parentFrame.getAthletes().get(idx);
            int[] scores = a.getMatchScores();

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int startX = 60;
            int gapX = 100;
            int y = getHeight() / 2;
            int radius = 22;

            g2.setColor(Theme.TEXT_MAIN);
            g2.setFont(Theme.FONT_SUBTITLE);
            g2.drawString(a.getName() + "'s Match Score Timeline", startX, y - 80);

            // Draw line through timeline
            g2.setColor(new Color(69, 71, 90));
            g2.setStroke(new BasicStroke(2));
            if (scores.length > 1) {
                g2.drawLine(startX, y, startX + (scores.length - 1) * gapX, y);
            }

            // Draw circles
            List<Point> hlPoints = new ArrayList<>();
            for (int i = 0; i < scores.length; i++) {
                int score = scores[i];
                int x = startX + i * gapX;

                boolean inLIS = lisHighlights.contains(score);
                g2.setColor(inLIS ? Theme.SUCCESS : Theme.BG_COLOR);
                g2.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);

                g2.setColor(inLIS ? Theme.SIDEBAR_BG : Theme.PURPLE);
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(x - radius, y - radius, 2 * radius, 2 * radius);

                g2.setColor(inLIS ? Theme.SIDEBAR_BG : Theme.TEXT_MAIN);
                g2.setFont(Theme.FONT_BOLD);
                String scoreStr = String.valueOf(score);
                int w = g2.getFontMetrics().stringWidth(scoreStr);
                g2.drawString(scoreStr, x - w / 2, y + 5);

                g2.setColor(Theme.TEXT_MUTED);
                g2.setFont(Theme.FONT_SMALL);
                String matchLabel = "Match " + i;
                int mlW = g2.getFontMetrics().stringWidth(matchLabel);
                g2.drawString(matchLabel, x - mlW / 2, y + radius + 18);

                if (inLIS) {
                    hlPoints.add(new Point(x, y));
                }
            }

            // Draw arrows showing LIS connections
            if (hlPoints.size() > 1) {
                g2.setColor(Theme.SUCCESS);
                g2.setStroke(new BasicStroke(3));
                for (int i = 0; i < hlPoints.size() - 1; i++) {
                    Point p1 = hlPoints.get(i);
                    Point p2 = hlPoints.get(i + 1);
                    // Draw curved arrow or thick link above
                    g2.drawArc(p1.x, p1.y - 35, p2.x - p1.x, 50, 0, 180);
                }
                g2.setFont(Theme.FONT_BOLD);
                g2.drawString("Longest Improvement Streak: LIS subset highlighted in Teal!", startX, y + 80);
            }
        }
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Theme.TEXT_MAIN);
        label.setFont(Theme.FONT_BOLD);
        return label;
    }

    public void refreshData() {
        lisAthleteCombo.removeAllItems();
        for (Athlete a : parentFrame.getAthletes()) {
            lisAthleteCombo.addItem(a.getName());
        }
        handleLisAthleteSelected();
    }
}
