package sportsanalytics.gui;

import sportsanalytics.model.Athlete;
import sportsanalytics.co1.AthleteBST;
import sportsanalytics.co1.AthleteAVL;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AthletePanel extends JPanel {
    private final MainFrame parentFrame;
    private JTable athleteTable;
    private DefaultTableModel tableModel;
    
    // Forms
    private JTextField txtId, txtName, txtSport, txtRating, txtTeamId, txtScores;
    private JTextField txtSearchId;
    
    // Visualizer canvas
    private TreeVisualizer treeVisualizer;
    private JRadioButton rbtnBST, rbtnAVL;
    private Integer highlightNodeId = null;

    public AthletePanel(MainFrame frame) {
        this.parentFrame = frame;
        setLayout(new BorderLayout());
        setBackground(Theme.BG_COLOR);
        
        initComponents();
        refreshData();
    }

    private void initComponents() {
        // --- Left Control Panel (Forms & Actions) ---
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(Theme.BG_COLOR);
        leftPanel.setPreferredSize(new Dimension(360, 800));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 10));

        // Form Card
        JPanel formCard = Theme.createCardPanel();
        formCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("Runtime Athlete Input");
        lblTitle.setFont(Theme.FONT_SUBTITLE);
        lblTitle.setForeground(Theme.ACCENT);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formCard.add(lblTitle, gbc);
        gbc.gridwidth = 1;

        String[] labels = {"Athlete ID:", "Name:", "Sport:", "Rating (e.g. 85.5):", "Team ID:", "Match Scores (csv):"};
        JTextField[] fields = {
            txtId = new JTextField(),
            txtName = new JTextField(),
            txtSport = new JTextField(),
            txtRating = new JTextField(),
            txtTeamId = new JTextField(),
            txtScores = new JTextField("90,85,95,88")
        };

        for (int i = 0; i < labels.length; i++) {
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(Theme.FONT_BOLD);
            lbl.setForeground(Theme.TEXT_MAIN);
            gbc.gridx = 0; gbc.gridy = i + 1;
            formCard.add(lbl, gbc);

            Theme.styleTextField(fields[i]);
            gbc.gridx = 1;
            formCard.add(fields[i], gbc);
        }

        // Add & Delete Buttons
        JButton btnAdd = new JButton("Add / Update Athlete");
        Theme.styleButton(btnAdd, Theme.SUCCESS, Theme.SIDEBAR_BG);
        btnAdd.addActionListener(e -> handleAddAthlete());

        JButton btnDelete = new JButton("Delete Athlete");
        Theme.styleButton(btnDelete, Theme.ERROR, Theme.TEXT_MAIN);
        btnDelete.addActionListener(e -> handleDeleteAthlete());

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        formCard.add(btnAdd, gbc);

        gbc.gridy = 8;
        gbc.insets = new Insets(5, 5, 5, 5);
        formCard.add(btnDelete, gbc);

        leftPanel.add(formCard);
        leftPanel.add(Box.createVerticalStrut(15));

        // Search & Traversals Card
        JPanel actionCard = Theme.createCardPanel();
        actionCard.setLayout(new GridBagLayout());
        
        JLabel lblActions = new JLabel("Search & Traversals");
        lblActions.setFont(Theme.FONT_SUBTITLE);
        lblActions.setForeground(Theme.ACCENT);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        actionCard.add(lblActions, gbc);
        gbc.gridwidth = 1;
        gbc.weightx = 0;

        JLabel lblSearchId = new JLabel("Search Athlete ID:");
        lblSearchId.setFont(Theme.FONT_BOLD);
        lblSearchId.setForeground(Theme.TEXT_MAIN);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        actionCard.add(lblSearchId, gbc);

        txtSearchId = new JTextField();
        txtSearchId.setColumns(10);
        Theme.styleTextField(txtSearchId);
        txtSearchId.setPreferredSize(new Dimension(220, 36));
        txtSearchId.setMinimumSize(new Dimension(180, 36));
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        actionCard.add(txtSearchId, gbc);

        JButton btnSearch = new JButton("Search ID");
        Theme.styleButton(btnSearch, Theme.ACCENT, Theme.SIDEBAR_BG);
        btnSearch.addActionListener(e -> handleSearch());
        gbc.gridy = 3;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        actionCard.add(btnSearch, gbc);

        // Traversal Buttons
        JButton btnInOrder = new JButton("In-Order");
        Theme.styleButton(btnInOrder, new Color(49, 50, 68), Theme.TEXT_MAIN);
        btnInOrder.addActionListener(e -> showTraversal("In-Order"));

        JButton btnPreOrder = new JButton("Pre-Order");
        Theme.styleButton(btnPreOrder, new Color(49, 50, 68), Theme.TEXT_MAIN);
        btnPreOrder.addActionListener(e -> showTraversal("Pre-Order"));

        JButton btnPostOrder = new JButton("Post-Order");
        Theme.styleButton(btnPostOrder, new Color(49, 50, 68), Theme.TEXT_MAIN);
        btnPostOrder.addActionListener(e -> showTraversal("Post-Order"));

        JPanel travPanel = new JPanel(new GridLayout(1, 3, 5, 0));
        travPanel.setOpaque(false);
        travPanel.add(btnInOrder);
        travPanel.add(btnPreOrder);
        travPanel.add(btnPostOrder);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        actionCard.add(travPanel, gbc);

        leftPanel.add(actionCard);
        leftPanel.add(Box.createVerticalStrut(15));

        // JTable for Athletes
        String[] colNames = {"ID", "Name", "Sport", "Rating", "Team"};
        tableModel = new DefaultTableModel(colNames, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        athleteTable = new JTable(tableModel);
        athleteTable.setBackground(Theme.CARD_BG);
        athleteTable.setForeground(Theme.TEXT_MAIN);
        athleteTable.getTableHeader().setBackground(Theme.SIDEBAR_BG);
        athleteTable.getTableHeader().setForeground(Theme.ACCENT);
        athleteTable.getTableHeader().setFont(Theme.FONT_BOLD);
        athleteTable.setFont(Theme.FONT_BODY);
        athleteTable.setGridColor(new Color(49, 50, 68));
        athleteTable.setRowHeight(25);
        
        // Load details back to form on select
        athleteTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = athleteTable.getSelectedRow();
            if (selectedRow >= 0) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                Athlete athlete = parentFrame.getAthletes().stream().filter(a -> a.getAthleteId() == id).findFirst().orElse(null);
                if (athlete != null) {
                    txtId.setText(String.valueOf(athlete.getAthleteId()));
                    txtName.setText(athlete.getName());
                    txtSport.setText(athlete.getSport());
                    txtRating.setText(String.valueOf(athlete.getPerformanceRating()));
                    txtTeamId.setText(String.valueOf(athlete.getTeamId()));
                    StringBuilder sb = new StringBuilder();
                    for (int score : athlete.getMatchScores()) {
                        if (sb.length() > 0) sb.append(",");
                        sb.append(score);
                    }
                    txtScores.setText(sb.toString());
                    
                    highlightNodeId = athlete.getAthleteId();
                    treeVisualizer.repaint();
                }
            }
        });

        JScrollPane scrollTable = new JScrollPane(athleteTable);
        scrollTable.getViewport().setBackground(Theme.CARD_BG);
        scrollTable.setBorder(BorderFactory.createLineBorder(new Color(69, 71, 90), 1));
        
        leftPanel.add(scrollTable);

        add(leftPanel, BorderLayout.WEST);

        // --- Right Visualizer Panel (Tree Visualizer) ---
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Theme.BG_COLOR);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 15));

        JPanel controlVizPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlVizPanel.setOpaque(false);

        JLabel lblTreeSelector = new JLabel("Tree Index View: ");
        lblTreeSelector.setFont(Theme.FONT_BOLD);
        lblTreeSelector.setForeground(Theme.TEXT_MAIN);
        controlVizPanel.add(lblTreeSelector);

        rbtnBST = new JRadioButton("AthleteBST (by ID)");
        rbtnBST.setSelected(true);
        rbtnBST.setFont(Theme.FONT_BODY);
        rbtnBST.setForeground(Theme.TEXT_MAIN);
        rbtnBST.setOpaque(false);

        rbtnAVL = new JRadioButton("AthleteAVL (by Rating)");
        rbtnAVL.setFont(Theme.FONT_BODY);
        rbtnAVL.setForeground(Theme.TEXT_MAIN);
        rbtnAVL.setOpaque(false);

        ButtonGroup group = new ButtonGroup();
        group.add(rbtnBST);
        group.add(rbtnAVL);

        rbtnBST.addActionListener(e -> treeVisualizer.repaint());
        rbtnAVL.addActionListener(e -> treeVisualizer.repaint());

        controlVizPanel.add(rbtnBST);
        controlVizPanel.add(rbtnAVL);

        rightPanel.add(controlVizPanel, BorderLayout.NORTH);

        treeVisualizer = new TreeVisualizer();
        rightPanel.add(treeVisualizer, BorderLayout.CENTER);

        add(rightPanel, BorderLayout.CENTER);
    }

    private void handleAddAthlete() {
        try {
            int id = Integer.parseInt(txtId.getText().trim());
            String name = txtName.getText().trim();
            String sport = txtSport.getText().trim();
            double rating = Double.parseDouble(txtRating.getText().trim());
            int teamId = Integer.parseInt(txtTeamId.getText().trim());
            
            String[] scoresStr = txtScores.getText().trim().split(",");
            int[] scores = new int[scoresStr.length];
            for (int i = 0; i < scoresStr.length; i++) {
                scores[i] = Integer.parseInt(scoresStr[i].trim());
            }

            if (name.isEmpty() || sport.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Fields cannot be empty!", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if athlete exists
            Athlete existing = parentFrame.getAthletes().stream().filter(a -> a.getAthleteId() == id).findFirst().orElse(null);
            if (existing != null) {
                // Remove first, to re-insert cleanly (AVL tree balances properly this way)
                parentFrame.removeAthlete(existing);
            }

            Athlete a = new Athlete(id, name, sport, rating, teamId, scores);
            parentFrame.addAthlete(a);

            refreshData();
            highlightNodeId = id;
            treeVisualizer.repaint();
            
            // Clear inputs
            txtId.setText("");
            txtName.setText("");
            txtSport.setText("");
            txtRating.setText("");
            txtTeamId.setText("");
            txtScores.setText("90,85,95,88");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numerical values for ID, Rating, Team ID, and Scores.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteAthlete() {
        try {
            int id = Integer.parseInt(txtId.getText().trim());
            Athlete existing = parentFrame.getAthletes().stream().filter(a -> a.getAthleteId() == id).findFirst().orElse(null);
            if (existing == null) {
                JOptionPane.showMessageDialog(this, "Athlete not found!", "Delete Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            parentFrame.removeAthlete(existing);
            refreshData();
            highlightNodeId = null;
            treeVisualizer.repaint();
            txtId.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please select an athlete or enter a valid ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSearch() {
        try {
            int id = Integer.parseInt(txtSearchId.getText().trim());
            Athlete a = parentFrame.getBst().search(id);
            if (a != null) {
                highlightNodeId = id;
                treeVisualizer.repaint();
                JOptionPane.showMessageDialog(this, "Athlete Found:\n" + a.getName() + " (" + a.getSport() + ")\nRating: " + a.getPerformanceRating(), "Search Result", JOptionPane.INFORMATION_MESSAGE);
            } else {
                highlightNodeId = null;
                treeVisualizer.repaint();
                JOptionPane.showMessageDialog(this, "Athlete ID not found in database.", "Search Result", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numerical ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showTraversal(String type) {
        List<Athlete> traversalResult;
        if (type.equals("In-Order")) {
            traversalResult = parentFrame.getBst().getInOrder();
        } else if (type.equals("Pre-Order")) {
            traversalResult = parentFrame.getBst().getPreOrder();
        } else {
            traversalResult = parentFrame.getBst().getPostOrder();
        }

        StringBuilder sb = new StringBuilder("--- BST " + type + " Traversal ---\n\n");
        for (Athlete a : traversalResult) {
            sb.append(String.format("ID: %d | Name: %s | Sport: %s | Rating: %.1f\n",
                    a.getAthleteId(), a.getName(), a.getSport(), a.getPerformanceRating()));
        }

        JTextArea textArea = new JTextArea(15, 30);
        textArea.setText(sb.toString());
        textArea.setEditable(false);
        textArea.setBackground(Theme.CARD_BG);
        textArea.setForeground(Theme.TEXT_MAIN);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setBorder(null);
        
        JOptionPane.showMessageDialog(this, scroll, type + " Output", JOptionPane.PLAIN_MESSAGE);
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        for (Athlete a : parentFrame.getAthletes()) {
            tableModel.addRow(new Object[]{a.getAthleteId(), a.getName(), a.getSport(), a.getPerformanceRating(), a.getTeamId()});
        }
        treeVisualizer.repaint();
    }

    // Inner Canvas class for custom drawing of tree structure
    private class TreeVisualizer extends JPanel {
        public TreeVisualizer() {
            setBackground(Theme.CARD_BG);
            setBorder(BorderFactory.createLineBorder(new Color(69, 71, 90), 1));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (rbtnBST.isSelected()) {
                AthleteBST.Node bstRoot = parentFrame.getBst().getRoot();
                if (bstRoot == null) {
                    drawEmptyMsg(g2);
                } else {
                    drawBSTNode(g2, bstRoot, getWidth() / 2, 40, getWidth() / 4);
                }
            } else {
                AthleteAVL.Node avlRoot = parentFrame.getAvl().getRoot();
                if (avlRoot == null) {
                    drawEmptyMsg(g2);
                } else {
                    drawAVLNode(g2, avlRoot, getWidth() / 2, 40, getWidth() / 4);
                }
            }
        }

        private void drawEmptyMsg(Graphics2D g2) {
            g2.setColor(Theme.TEXT_MUTED);
            g2.setFont(Theme.FONT_SUBTITLE);
            g2.drawString("Tree Database Empty", getWidth() / 2 - 80, getHeight() / 2);
        }

        private void drawBSTNode(Graphics2D g2, AthleteBST.Node node, int x, int y, int hGap) {
            int radius = 20;

            if (node.left != null) {
                g2.setColor(Theme.TEXT_MUTED);
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(x, y, x - hGap, y + 65);
                drawBSTNode(g2, node.left, x - hGap, y + 65, hGap / 2);
            }

            if (node.right != null) {
                g2.setColor(Theme.TEXT_MUTED);
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(x, y, x + hGap, y + 65);
                drawBSTNode(g2, node.right, x + hGap, y + 65, hGap / 2);
            }

            // Draw Node circle
            boolean isHighlighted = highlightNodeId != null && node.athlete.getAthleteId() == highlightNodeId;
            g2.setColor(isHighlighted ? Theme.SUCCESS : Theme.BG_COLOR);
            g2.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);

            g2.setColor(isHighlighted ? Theme.SIDEBAR_BG : Theme.PURPLE);
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(x - radius, y - radius, 2 * radius, 2 * radius);

            // Draw Label
            g2.setColor(isHighlighted ? Theme.SIDEBAR_BG : Theme.TEXT_MAIN);
            g2.setFont(Theme.FONT_BOLD);
            String idStr = String.valueOf(node.athlete.getAthleteId());
            FontMetrics fm = g2.getFontMetrics();
            int labelWidth = fm.stringWidth(idStr);
            g2.drawString(idStr, x - labelWidth / 2, y + 5);

            // Print Rating/Name under node
            g2.setColor(Theme.TEXT_MUTED);
            g2.setFont(Theme.FONT_SMALL);
            String label = node.athlete.getName().split(" ")[0];
            int lW = fm.stringWidth(label);
            g2.drawString(label, x - lW / 2, y + radius + 15);
        }

        private void drawAVLNode(Graphics2D g2, AthleteAVL.Node node, int x, int y, int hGap) {
            int radius = 20;

            if (node.left != null) {
                g2.setColor(Theme.TEXT_MUTED);
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(x, y, x - hGap, y + 65);
                drawAVLNode(g2, node.left, x - hGap, y + 65, hGap / 2);
            }

            if (node.right != null) {
                g2.setColor(Theme.TEXT_MUTED);
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(x, y, x + hGap, y + 65);
                drawAVLNode(g2, node.right, x + hGap, y + 65, hGap / 2);
            }

            // Draw Node circle
            boolean isHighlighted = highlightNodeId != null && node.athlete.getAthleteId() == highlightNodeId;
            g2.setColor(isHighlighted ? Theme.SUCCESS : Theme.BG_COLOR);
            g2.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);

            g2.setColor(isHighlighted ? Theme.SIDEBAR_BG : Theme.ACCENT);
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(x - radius, y - radius, 2 * radius, 2 * radius);

            // Draw Rating Label (AVL is sorted by rating)
            g2.setColor(isHighlighted ? Theme.SIDEBAR_BG : Theme.TEXT_MAIN);
            g2.setFont(Theme.FONT_BOLD);
            String ratingStr = String.format("%.1f", node.athlete.getPerformanceRating());
            FontMetrics fm = g2.getFontMetrics();
            int labelWidth = fm.stringWidth(ratingStr);
            g2.drawString(ratingStr, x - labelWidth / 2, y + 5);

            // Print Athlete ID under node
            g2.setColor(Theme.TEXT_MUTED);
            g2.setFont(Theme.FONT_SMALL);
            String label = "ID: " + node.athlete.getAthleteId();
            int lW = fm.stringWidth(label);
            g2.drawString(label, x - lW / 2, y + radius + 15);
        }
    }
}
