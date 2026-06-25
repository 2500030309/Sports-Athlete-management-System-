package sportsanalytics.gui;

import sportsanalytics.model.Athlete;
import sportsanalytics.co5.MergeSort;
import sportsanalytics.co5.QuickSort;
import sportsanalytics.co5.HeapSort;
import sportsanalytics.co5.CountingSort;
import sportsanalytics.co5.RadixSort;
import sportsanalytics.co5.SorterBenchmark;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class SortingPanel extends JPanel {
    private final MainFrame parentFrame;

    // UI elements
    private JComboBox<String> fieldCombo;
    private JTable sortedTable;
    private DefaultTableModel tableModel;
    
    private JComboBox<Integer> benchmarkSizeCombo;
    private JTable benchmarkTable;
    private DefaultTableModel benchmarkModel;
    private ChartVisualizer chartVisualizer;

    // Benchmarking data
    private Map<String, Double> benchmarkResults = new HashMap<>();

    public SortingPanel(MainFrame frame) {
        this.parentFrame = frame;
        setLayout(new BorderLayout());
        setBackground(Theme.BG_COLOR);

        initComponents();
        refreshData();
    }

    private void initComponents() {
        // --- Left Control Panel ---
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(Theme.BG_COLOR);
        leftPanel.setPreferredSize(new Dimension(380, 800));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 10));

        // Card 1: Roster Sorting Controls
        JPanel sortCard = Theme.createCardPanel();
        sortCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("CO5: Roster Sorting Algorithms");
        lblTitle.setFont(Theme.FONT_SUBTITLE);
        lblTitle.setForeground(Theme.ACCENT);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        sortCard.add(lblTitle, gbc);
        gbc.gridwidth = 1;

        gbc.gridy = 1; gbc.gridx = 0;
        JLabel lblField = new JLabel("Sort Attribute:");
        lblField.setFont(Theme.FONT_BOLD);
        lblField.setForeground(Theme.TEXT_MAIN);
        sortCard.add(lblField, gbc);

        fieldCombo = new JComboBox<>(new String[]{
            "Athlete ID", "Performance Rating", "Team ID", "Match Score Sum"
        });
        Theme.styleComboBox(fieldCombo);
        gbc.gridx = 1;
        sortCard.add(fieldCombo, gbc);

        // Sorting buttons
        JButton btnMerge = new JButton("Run Merge Sort");
        Theme.styleButton(btnMerge, Theme.ACCENT, Theme.SIDEBAR_BG);
        btnMerge.addActionListener(e -> executeSort("Merge"));

        JButton btnQuick = new JButton("Run Quick Sort");
        Theme.styleButton(btnQuick, Theme.PURPLE, Theme.SIDEBAR_BG);
        btnQuick.addActionListener(e -> executeSort("Quick"));

        JButton btnHeap = new JButton("Run Heap Sort");
        Theme.styleButton(btnHeap, Theme.ERROR, Theme.SIDEBAR_BG);
        btnHeap.addActionListener(e -> executeSort("Heap"));

        JButton btnCounting = new JButton("Run Counting Sort");
        Theme.styleButton(btnCounting, Theme.SUCCESS, Theme.SIDEBAR_BG);
        btnCounting.addActionListener(e -> executeSort("Counting"));

        JButton btnRadix = new JButton("Run Radix Sort");
        Theme.styleButton(btnRadix, Theme.WARNING, Theme.SIDEBAR_BG);
        btnRadix.addActionListener(e -> executeSort("Radix"));

        gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 6, 4, 6);
        gbc.gridy = 2; sortCard.add(btnMerge, gbc);
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.gridy = 3; sortCard.add(btnQuick, gbc);
        gbc.gridy = 4; sortCard.add(btnHeap, gbc);
        gbc.gridy = 5; sortCard.add(btnCounting, gbc);
        gbc.gridy = 6; sortCard.add(btnRadix, gbc);

        leftPanel.add(sortCard);
        leftPanel.add(Box.createVerticalStrut(15));

        // Card 2: Sorted Athlete Table
        JPanel tableCard = Theme.createCardPanel();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(69, 71, 90)), "Sorted Result View",
            0, 0, Theme.FONT_BOLD, Theme.TEXT_MUTED
        ));

        String[] colNames = {"ID", "Name", "Rating", "Team", "Score Sum"};
        tableModel = new DefaultTableModel(colNames, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        sortedTable = new JTable(tableModel);
        sortedTable.setBackground(Theme.CARD_BG);
        sortedTable.setForeground(Theme.TEXT_MAIN);
        sortedTable.getTableHeader().setBackground(Theme.SIDEBAR_BG);
        sortedTable.getTableHeader().setForeground(Theme.ACCENT);
        sortedTable.getTableHeader().setFont(Theme.FONT_BOLD);
        sortedTable.setFont(Theme.FONT_BODY);
        sortedTable.setGridColor(new Color(49, 50, 68));
        sortedTable.setRowHeight(22);

        JScrollPane scrollTable = new JScrollPane(sortedTable);
        scrollTable.getViewport().setBackground(Theme.CARD_BG);
        scrollTable.setBorder(BorderFactory.createLineBorder(new Color(69, 71, 90), 1));
        scrollTable.setPreferredSize(new Dimension(350, 250));
        tableCard.add(scrollTable, BorderLayout.CENTER);

        leftPanel.add(tableCard);
        add(leftPanel, BorderLayout.WEST);

        // --- Right Workspace (Benchmarking Suite) ---
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Theme.BG_COLOR);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 15));

        // Benchmark Controller Card
        JPanel benchmarkCtrlCard = Theme.createCardPanel();
        benchmarkCtrlCard.setLayout(new GridBagLayout());
        GridBagConstraints gbcB = new GridBagConstraints();
        gbcB.insets = new Insets(6, 12, 6, 12);
        gbcB.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblBenchTitle = new JLabel("Sorting Performance Benchmarks");
        lblBenchTitle.setFont(Theme.FONT_SUBTITLE);
        lblBenchTitle.setForeground(Theme.ACCENT);
        gbcB.gridx = 0; gbcB.gridy = 0; gbcB.gridwidth = 3;
        benchmarkCtrlCard.add(lblBenchTitle, gbcB);
        gbcB.gridwidth = 1;

        gbcB.gridy = 1; gbcB.gridx = 0;
        JLabel lblSize = new JLabel("Dataset Size (N):");
        lblSize.setFont(Theme.FONT_BOLD);
        lblSize.setForeground(Theme.TEXT_MAIN);
        benchmarkCtrlCard.add(lblSize, gbcB);

        benchmarkSizeCombo = new JComboBox<>(new Integer[]{100, 1000, 5000, 10000});
        Theme.styleComboBox(benchmarkSizeCombo);
        gbcB.gridx = 1;
        benchmarkCtrlCard.add(benchmarkSizeCombo, gbcB);

        JButton btnRunBenchmark = new JButton("Run Performance Test");
        Theme.styleButton(btnRunBenchmark, Theme.SUCCESS, Theme.SIDEBAR_BG);
        btnRunBenchmark.addActionListener(e -> runPerformanceTest());
        gbcB.gridx = 2;
        benchmarkCtrlCard.add(btnRunBenchmark, gbcB);

        rightPanel.add(benchmarkCtrlCard);
        rightPanel.add(Box.createVerticalStrut(15));

        // Result split
        JPanel benchmarkResultsPanel = Theme.createCardPanel();
        benchmarkResultsPanel.setLayout(new BorderLayout());
        
        JLabel lblResultsTitle = new JLabel("Benchmark Execution Analysis");
        lblResultsTitle.setFont(Theme.FONT_SUBTITLE);
        lblResultsTitle.setForeground(Theme.ACCENT);
        benchmarkResultsPanel.add(lblResultsTitle, BorderLayout.NORTH);

        JPanel gridSplit = new JPanel(new GridLayout(1, 2, 15, 0));
        gridSplit.setOpaque(false);

        // Benchmark Grid
        String[] benchColNames = {"Algorithm", "Time (ms)"};
        benchmarkModel = new DefaultTableModel(benchColNames, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        benchmarkTable = new JTable(benchmarkModel);
        benchmarkTable.setBackground(Theme.CARD_BG);
        benchmarkTable.setForeground(Theme.TEXT_MAIN);
        benchmarkTable.getTableHeader().setBackground(Theme.SIDEBAR_BG);
        benchmarkTable.getTableHeader().setForeground(Theme.ACCENT);
        benchmarkTable.getTableHeader().setFont(Theme.FONT_BOLD);
        benchmarkTable.setFont(Theme.FONT_BODY);
        benchmarkTable.setRowHeight(25);
        benchmarkTable.setGridColor(new Color(49, 50, 68));

        JScrollPane benchScroll = new JScrollPane(benchmarkTable);
        benchScroll.getViewport().setBackground(Theme.CARD_BG);
        benchScroll.setBorder(BorderFactory.createLineBorder(new Color(69, 71, 90), 1));
        gridSplit.add(benchScroll);

        // Chart Visualizer
        chartVisualizer = new ChartVisualizer();
        gridSplit.add(chartVisualizer);

        benchmarkResultsPanel.add(gridSplit, BorderLayout.CENTER);
        rightPanel.add(benchmarkResultsPanel);

        add(rightPanel, BorderLayout.CENTER);
    }

    private void executeSort(String algoType) {
        List<Athlete> list = new ArrayList<>(parentFrame.getAthletes());
        if (list.isEmpty()) return;

        Athlete[] arr = list.toArray(new Athlete[0]);
        String field = (String) fieldCombo.getSelectedItem();

        // Build Comparator and key extractor
        Comparator<Athlete> comparator;
        java.util.function.ToIntFunction<Athlete> keyExtractor;

        if (field.equals("Athlete ID")) {
            comparator = Comparator.comparingInt(Athlete::getAthleteId);
            keyExtractor = Athlete::getAthleteId;
        } else if (field.equals("Performance Rating")) {
            comparator = Comparator.comparingDouble(Athlete::getPerformanceRating);
            keyExtractor = a -> (int) (a.getPerformanceRating() * 10.0);
        } else if (field.equals("Team ID")) {
            comparator = Comparator.comparingInt(Athlete::getTeamId);
            keyExtractor = Athlete::getTeamId;
        } else { // Match Score Sum
            comparator = Comparator.comparingInt(a -> Arrays.stream(a.getMatchScores()).sum());
            keyExtractor = a -> Arrays.stream(a.getMatchScores()).sum();
        }

        try {
            switch (algoType) {
                case "Merge":
                    MergeSort.sort(arr, comparator);
                    break;
                case "Quick":
                    QuickSort.sort(arr, comparator);
                    break;
                case "Heap":
                    HeapSort.sort(arr, comparator);
                    break;
                case "Counting":
                    CountingSort.sort(arr, keyExtractor);
                    break;
                case "Radix":
                    RadixSort.sort(arr, keyExtractor);
                    break;
            }

            // Update Result View Table
            tableModel.setRowCount(0);
            for (Athlete a : arr) {
                int sum = Arrays.stream(a.getMatchScores()).sum();
                tableModel.addRow(new Object[]{
                    a.getAthleteId(), a.getName(), a.getPerformanceRating(), a.getTeamId(), sum
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Sorting Failed: " + ex.getMessage(), "Sort Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void runPerformanceTest() {
        int size = (int) benchmarkSizeCombo.getSelectedItem();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        try {
            benchmarkResults = SorterBenchmark.runBenchmark(size);

            benchmarkModel.setRowCount(0);
            String[] algos = {"Merge Sort", "Quick Sort", "Heap Sort", "Counting Sort", "Radix Sort", "Built-in Sort"};
            for (String algo : algos) {
                if (benchmarkResults.containsKey(algo)) {
                    double time = benchmarkResults.get(algo);
                    benchmarkModel.addRow(new Object[]{algo, String.format("%.4f ms", time)});
                }
            }

            chartVisualizer.repaint();
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    public void refreshData() {
        executeSort("Quick");
    }

    // Chart Visualizer Panel
    private class ChartVisualizer extends JPanel {
        public ChartVisualizer() {
            setBackground(Theme.CARD_BG);
            setBorder(BorderFactory.createLineBorder(new Color(69, 71, 90), 1));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (benchmarkResults.isEmpty()) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(Theme.TEXT_MUTED);
                g2.setFont(Theme.FONT_SUBTITLE);
                g2.drawString("No Benchmark Run Yet", getWidth() / 2 - 80, getHeight() / 2);
                return;
            }

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            String[] algos = {"Merge Sort", "Quick Sort", "Heap Sort", "Counting Sort", "Radix Sort", "Built-in Sort"};
            Color[] colors = {Theme.ACCENT, Theme.PURPLE, Theme.ERROR, Theme.SUCCESS, Theme.WARNING, Theme.TEXT_MUTED};

            double maxVal = benchmarkResults.values().stream().max(Double::compareTo).orElse(1.0);
            if (maxVal == 0.0) maxVal = 1.0;

            int padding = 40;
            int chartWidth = getWidth() - 2 * padding;
            int chartHeight = getHeight() - 2 * padding - 20;

            g2.setColor(new Color(69, 71, 90));
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(padding, getHeight() - padding, getWidth() - padding, getHeight() - padding); // X-axis
            g2.drawLine(padding, padding, padding, getHeight() - padding); // Y-axis

            int barWidth = chartWidth / algos.length - 15;

            for (int i = 0; i < algos.length; i++) {
                String algo = algos[i];
                if (!benchmarkResults.containsKey(algo)) continue;

                double val = benchmarkResults.get(algo);
                int barHeight = (int) ((val / maxVal) * chartHeight);

                int x = padding + i * (chartWidth / algos.length) + 8;
                int y = getHeight() - padding - barHeight;

                g2.setColor(colors[i]);
                g2.fillRect(x, y, barWidth, barHeight);
                g2.setColor(Theme.SIDEBAR_BG);
                g2.drawRect(x, y, barWidth, barHeight);

                g2.setColor(Theme.TEXT_MAIN);
                g2.setFont(Theme.FONT_SMALL);
                String valStr = String.format("%.2f", val);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(valStr, x + (barWidth - fm.stringWidth(valStr)) / 2, y - 5);

                g2.setColor(Theme.TEXT_MUTED);
                g2.setFont(Theme.FONT_SMALL);
                String shortName = algo.replace(" Sort", "");
                int labelW = fm.stringWidth(shortName);
                g2.drawString(shortName, x + (barWidth - labelW) / 2, getHeight() - padding + 18);
            }
        }
    }
}


