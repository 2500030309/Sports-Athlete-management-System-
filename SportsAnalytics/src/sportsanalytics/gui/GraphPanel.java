package sportsanalytics.gui;

import sportsanalytics.co3.Graph;
import sportsanalytics.co3.BFS;
import sportsanalytics.co3.DFS;
import sportsanalytics.co3.PrimsMST;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class GraphPanel extends JPanel {
    private final MainFrame parentFrame;
    private Graph activeGraph;
    
    // UI Elements
    private JComboBox<Integer> startNodeCombo;
    private JTextArea txtOutput;
    private JRadioButton rbtnFacilities, rbtnMatchups;
    private JTextField txtAddVertexId, txtAddVertexName;
    private JTextField txtEdgeSrc, txtEdgeDest, txtEdgeWeight;

    // Visual states
    private List<Integer> traversalHighlight = new ArrayList<>();
    private List<Graph.Edge> mstHighlight = new ArrayList<>();
    private boolean drawMST = false;

    // Fixed coordinates for layout
    private final Map<Integer, Point> nodePositions = new HashMap<>();

    public GraphPanel(MainFrame frame) {
        this.parentFrame = frame;
        this.activeGraph = parentFrame.getFacilityGraph(); // Default undirected graph
        
        setLayout(new BorderLayout());
        setBackground(Theme.BG_COLOR);

        initPositions();
        initComponents();
        refreshData();
    }

    private void initPositions() {
        // Pre-defined coordinates for layout (Hexagonal Shape)
        nodePositions.put(1, new Point(250, 200));
        nodePositions.put(2, new Point(100, 100));
        nodePositions.put(3, new Point(400, 100));
        nodePositions.put(4, new Point(100, 300));
        nodePositions.put(5, new Point(400, 300));
        nodePositions.put(6, new Point(250, 400));
        
        // Matchups DAG Coordinates
        nodePositions.put(10, new Point(80, 80));
        nodePositions.put(11, new Point(80, 180));
        nodePositions.put(12, new Point(80, 280));
        nodePositions.put(13, new Point(80, 380));
        nodePositions.put(14, new Point(250, 130));
        nodePositions.put(15, new Point(250, 330));
        nodePositions.put(16, new Point(420, 230));
    }

    private void initComponents() {
        // --- Left Control Panel ---
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(Theme.BG_COLOR);
        leftPanel.setPreferredSize(new Dimension(360, 800));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 10));

        // Card 1: Graph Type Selection
        JPanel typeCard = Theme.createCardPanel();
        typeCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("CO3: Graph Representation");
        lblTitle.setFont(Theme.FONT_SUBTITLE);
        lblTitle.setForeground(Theme.ACCENT);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        typeCard.add(lblTitle, gbc);
        gbc.gridwidth = 1;

        rbtnFacilities = new JRadioButton("Training Facilities (Undirected)");
        rbtnFacilities.setSelected(true);
        rbtnFacilities.setFont(Theme.FONT_BODY);
        rbtnFacilities.setForeground(Theme.TEXT_MAIN);
        rbtnFacilities.setOpaque(false);
        rbtnFacilities.addActionListener(e -> selectGraph(true));

        rbtnMatchups = new JRadioButton("Tournament Matchups (Directed DAG)");
        rbtnMatchups.setFont(Theme.FONT_BODY);
        rbtnMatchups.setForeground(Theme.TEXT_MAIN);
        rbtnMatchups.setOpaque(false);
        rbtnMatchups.addActionListener(e -> selectGraph(false));

        ButtonGroup group = new ButtonGroup();
        group.add(rbtnFacilities);
        group.add(rbtnMatchups);

        gbc.gridy = 1; gbc.gridx = 0; gbc.gridwidth = 2;
        typeCard.add(rbtnFacilities, gbc);
        gbc.gridy = 2;
        typeCard.add(rbtnMatchups, gbc);
        gbc.gridwidth = 1;

        leftPanel.add(typeCard);
        leftPanel.add(Box.createVerticalStrut(15));

        // Card 2: Traversals & Algorithms
        JPanel algoCard = Theme.createCardPanel();
        algoCard.setLayout(new GridBagLayout());

        JLabel lblAlgo = new JLabel("Graph Algorithms");
        lblAlgo.setFont(Theme.FONT_SUBTITLE);
        lblAlgo.setForeground(Theme.ACCENT);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        algoCard.add(lblAlgo, gbc);
        gbc.gridwidth = 1;

        gbc.gridy = 1; gbc.gridx = 0;
        JLabel lblStart = new JLabel("Start Vertex:");
        lblStart.setFont(Theme.FONT_BOLD);
        lblStart.setForeground(Theme.TEXT_MAIN);
        algoCard.add(lblStart, gbc);

        startNodeCombo = new JComboBox<>();
        Theme.styleComboBox(startNodeCombo);
        gbc.gridx = 1;
        algoCard.add(startNodeCombo, gbc);

        JButton btnBFS = new JButton("Run BFS");
        Theme.styleButton(btnBFS, Theme.ACCENT, Theme.SIDEBAR_BG);
        btnBFS.addActionListener(e -> runBFS());

        JButton btnDFS = new JButton("Run DFS & Detect Loops");
        Theme.styleButton(btnDFS, Theme.PURPLE, Theme.SIDEBAR_BG);
        btnDFS.addActionListener(e -> runDFS());

        JButton btnMST = new JButton("Run Prim's MST");
        Theme.styleButton(btnMST, Theme.SUCCESS, Theme.SIDEBAR_BG);
        btnMST.addActionListener(e -> runPrimMST());

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 5, 5, 5);
        algoCard.add(btnBFS, gbc);
        gbc.gridy = 3;
        algoCard.add(btnDFS, gbc);
        gbc.gridy = 4;
        algoCard.add(btnMST, gbc);
        gbc.gridwidth = 1;

        leftPanel.add(algoCard);
        leftPanel.add(Box.createVerticalStrut(15));

        // Card 3: Dynamic Vertex/Edge Editing at Runtime
        JPanel editCard = Theme.createCardPanel();
        editCard.setLayout(new GridBagLayout());

        JLabel lblEdit = new JLabel("Dynamic Graph Inputs");
        lblEdit.setFont(Theme.FONT_SUBTITLE);
        lblEdit.setForeground(Theme.ACCENT);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        editCard.add(lblEdit, gbc);
        gbc.gridwidth = 1;

        // Add Vertex
        gbc.gridy = 1; gbc.gridx = 0; gbc.insets = new Insets(5, 5, 5, 5);
        JLabel lblVId = new JLabel("Vertex ID:");
        lblVId.setFont(Theme.FONT_BOLD);
        lblVId.setForeground(Theme.TEXT_MAIN);
        editCard.add(lblVId, gbc);

        txtAddVertexId = new JTextField();
        Theme.styleTextField(txtAddVertexId);
        gbc.gridx = 1;
        editCard.add(txtAddVertexId, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        JLabel lblVName = new JLabel("Name:");
        lblVName.setFont(Theme.FONT_BOLD);
        lblVName.setForeground(Theme.TEXT_MAIN);
        editCard.add(lblVName, gbc);

        txtAddVertexName = new JTextField();
        Theme.styleTextField(txtAddVertexName);
        gbc.gridx = 1;
        editCard.add(txtAddVertexName, gbc);

        JButton btnAddVertex = new JButton("Add Vertex");
        Theme.styleButton(btnAddVertex, new Color(49, 50, 68), Theme.TEXT_MAIN);
        btnAddVertex.addActionListener(e -> handleAddVertex());
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 5, 10, 5);
        editCard.add(btnAddVertex, gbc);
        gbc.gridwidth = 1;

        // Add Edge
        gbc.gridy = 4; gbc.gridx = 0; gbc.insets = new Insets(5, 5, 5, 5);
        JLabel lblSrc = new JLabel("Edge Src ID:");
        lblSrc.setFont(Theme.FONT_BOLD);
        lblSrc.setForeground(Theme.TEXT_MAIN);
        editCard.add(lblSrc, gbc);

        txtEdgeSrc = new JTextField();
        Theme.styleTextField(txtEdgeSrc);
        gbc.gridx = 1;
        editCard.add(txtEdgeSrc, gbc);

        gbc.gridy = 5; gbc.gridx = 0;
        JLabel lblDest = new JLabel("Edge Dest ID:");
        lblDest.setFont(Theme.FONT_BOLD);
        lblDest.setForeground(Theme.TEXT_MAIN);
        editCard.add(lblDest, gbc);

        txtEdgeDest = new JTextField();
        Theme.styleTextField(txtEdgeDest);
        gbc.gridx = 1;
        editCard.add(txtEdgeDest, gbc);

        gbc.gridy = 6; gbc.gridx = 0;
        JLabel lblWeight = new JLabel("Edge Weight:");
        lblWeight.setFont(Theme.FONT_BOLD);
        lblWeight.setForeground(Theme.TEXT_MAIN);
        editCard.add(lblWeight, gbc);

        txtEdgeWeight = new JTextField("1.0");
        Theme.styleTextField(txtEdgeWeight);
        gbc.gridx = 1;
        editCard.add(txtEdgeWeight, gbc);

        JButton btnAddEdge = new JButton("Add Edge");
        Theme.styleButton(btnAddEdge, new Color(49, 50, 68), Theme.TEXT_MAIN);
        btnAddEdge.addActionListener(e -> handleAddEdge());
        gbc.gridy = 7; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 5, 5, 5);
        editCard.add(btnAddEdge, gbc);

        leftPanel.add(editCard);

        add(leftPanel, BorderLayout.WEST);

        // --- Right Workspace ---
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Theme.BG_COLOR);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 15));

        // Graph Drawing Canvas
        GraphVisualizer canvas = new GraphVisualizer();
        rightPanel.add(canvas, BorderLayout.CENTER);

        // Console logger area
        txtOutput = new JTextArea(4, 30);
        txtOutput.setBackground(Theme.CARD_BG);
        txtOutput.setForeground(Theme.TEXT_MAIN);
        txtOutput.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtOutput.setEditable(false);
        txtOutput.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(43, 44, 64)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        JScrollPane scrollLog = new JScrollPane(txtOutput);
        scrollLog.setBorder(null);
        rightPanel.add(scrollLog, BorderLayout.SOUTH);

        add(rightPanel, BorderLayout.CENTER);
    }

    private void selectGraph(boolean isFacilities) {
        if (isFacilities) {
            activeGraph = parentFrame.getFacilityGraph();
        } else {
            activeGraph = parentFrame.getDependencyGraph();
        }
        traversalHighlight.clear();
        mstHighlight.clear();
        drawMST = false;
        txtOutput.setText("Selected graph: " + (isFacilities ? "Training Facilities Network" : "Tournament Matchups Graph") + "\n");
        refreshData();
    }

    private void runBFS() {
        if (startNodeCombo.getSelectedItem() == null) return;
        int start = (int) startNodeCombo.getSelectedItem();
        
        List<Integer> order = BFS.traverse(activeGraph, start);
        traversalHighlight = order;
        mstHighlight.clear();
        drawMST = false;

        StringBuilder sb = new StringBuilder();
        sb.append("--- BFS Traversal Output ---\n");
        sb.append("Start Node: ").append(activeGraph.getVertexName(start)).append("\n");
        sb.append("Order: ");
        for (int i = 0; i < order.size(); i++) {
            sb.append(activeGraph.getVertexName(order.get(i)));
            if (i < order.size() - 1) sb.append(" -> ");
        }
        sb.append("\n");
        txtOutput.setText(sb.toString());

        repaint();
    }

    private void runDFS() {
        if (startNodeCombo.getSelectedItem() == null) return;
        int start = (int) startNodeCombo.getSelectedItem();

        List<Integer> order = DFS.traverse(activeGraph, start);
        traversalHighlight = order;
        mstHighlight.clear();
        drawMST = false;

        boolean hasCycle = DFS.hasCycle(activeGraph);

        StringBuilder sb = new StringBuilder();
        sb.append("--- DFS Traversal Output ---\n");
        sb.append("Start Node: ").append(activeGraph.getVertexName(start)).append("\n");
        sb.append("Order: ");
        for (int i = 0; i < order.size(); i++) {
            sb.append(activeGraph.getVertexName(order.get(i)));
            if (i < order.size() - 1) sb.append(" -> ");
        }
        sb.append("\n\n");
        sb.append("--- Cycle / Loop Detection ---\n");
        if (hasCycle) {
            sb.append("RESULT: LOOP DETECTED! There is a cyclic dependency or circular route in the graph.\n");
        } else {
            sb.append("RESULT: CYCLE-FREE! The graph structure is a valid DAG or acyclic tree.\n");
        }
        txtOutput.setText(sb.toString());

        repaint();
    }

    private void runPrimMST() {
        if (activeGraph.isDirected()) {
            JOptionPane.showMessageDialog(this, "Prim's MST algorithm requires an undirected graph.", "Algorithm Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (startNodeCombo.getSelectedItem() == null) return;
        int start = (int) startNodeCombo.getSelectedItem();

        List<Graph.Edge> mst = PrimsMST.findMST(activeGraph, start);
        mst = removeCycleEdges(mst, activeGraph.getVertexCount());
        mstHighlight = mst;
        drawMST = true;
        traversalHighlight.clear();

        double totalWeight = 0;
        StringBuilder sb = new StringBuilder();
        sb.append("--- Prim's Minimum Spanning Tree ---\n");
        sb.append("Start Node: ").append(activeGraph.getVertexName(start)).append("\n");
        for (Graph.Edge e : mst) {
            sb.append(activeGraph.getVertexName(e.src))
              .append(" - ")
              .append(activeGraph.getVertexName(e.dest))
              .append(" (")
              .append(e.weight)
              .append(" km)\n");
            totalWeight += e.weight;
        }
        sb.append("\nTotal MST Cost: ").append(totalWeight).append(" km\n");
        txtOutput.setText(sb.toString());

        repaint();
    }

    private List<Graph.Edge> removeCycleEdges(List<Graph.Edge> edges, int vertexCount) {
        List<Graph.Edge> filtered = new ArrayList<>();
        Map<Integer, Integer> parent = new HashMap<>();
        Map<Integer, Integer> rank = new HashMap<>();

        for (int v : activeGraph.getVertices()) {
            parent.put(v, v);
            rank.put(v, 0);
        }

        for (Graph.Edge e : edges) {
            int srcRoot = find(parent, e.src);
            int destRoot = find(parent, e.dest);
            if (srcRoot == destRoot) {
                continue;
            }
            union(parent, rank, srcRoot, destRoot);
            filtered.add(e);
            if (filtered.size() == Math.max(0, vertexCount - 1)) {
                break;
            }
        }
        return filtered;
    }

    private int find(Map<Integer, Integer> parent, int v) {
        if (parent.get(v) != v) {
            parent.put(v, find(parent, parent.get(v)));
        }
        return parent.get(v);
    }

    private void union(Map<Integer, Integer> parent, Map<Integer, Integer> rank, int a, int b) {
        if (a == b) {
            return;
        }
        int rankA = rank.get(a);
        int rankB = rank.get(b);
        if (rankA < rankB) {
            parent.put(a, b);
        } else if (rankA > rankB) {
            parent.put(b, a);
        } else {
            parent.put(b, a);
            rank.put(a, rankA + 1);
        }
    }

    private void handleAddVertex() {
        try {
            int id = Integer.parseInt(txtAddVertexId.getText().trim());
            String name = txtAddVertexName.getText().trim();
            if (name.isEmpty()) {
                name = "V" + id;
            }

            activeGraph.addVertex(id, name);
            
            // Assign a random visual coordinate if not predefined
            if (!nodePositions.containsKey(id)) {
                Random rand = new Random();
                int x = 120 + rand.nextInt(300);
                int y = 80 + rand.nextInt(300);
                nodePositions.put(id, new Point(x, y));
            }

            txtOutput.append("Added Vertex: " + name + " (ID: " + id + ")\n");
            refreshData();
            txtAddVertexId.setText("");
            txtAddVertexName.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid integer for Vertex ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleAddEdge() {
        try {
            int src = Integer.parseInt(txtEdgeSrc.getText().trim());
            int dest = Integer.parseInt(txtEdgeDest.getText().trim());
            double w = Double.parseDouble(txtEdgeWeight.getText().trim());

            if (!activeGraph.getVertices().contains(src) || !activeGraph.getVertices().contains(dest)) {
                JOptionPane.showMessageDialog(this, "Source and Destination vertices must exist first!", "Edge Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            activeGraph.addEdge(src, dest, w);
            txtOutput.append(String.format("Added Edge: %s to %s (w: %.1f)\n", 
                    activeGraph.getVertexName(src), activeGraph.getVertexName(dest), w));
            
            repaint();
            txtEdgeSrc.setText("");
            txtEdgeDest.setText("");
            txtEdgeWeight.setText("1.0");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter valid numbers for vertices and weight.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refreshData() {
        startNodeCombo.removeAllItems();
        for (int v : activeGraph.getVertices()) {
            startNodeCombo.addItem(v);
        }
        repaint();
    }

    // Inner panel class for custom rendering of graphs
    private class GraphVisualizer extends JPanel {
        public GraphVisualizer() {
            setBackground(Theme.CARD_BG);
            setBorder(BorderFactory.createLineBorder(new Color(69, 71, 90), 1));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (activeGraph.getVertexCount() == 0) return;

            // 1. Draw Edges
            List<Graph.Edge> allEdges = activeGraph.getAllEdges();
            for (Graph.Edge edge : allEdges) {
                Point p1 = nodePositions.get(edge.src);
                Point p2 = nodePositions.get(edge.dest);
                if (p1 == null || p2 == null) continue;

                // Check if this edge is highlighted in MST
                boolean isMSTEdge = false;
                if (drawMST) {
                    for (Graph.Edge mstE : mstHighlight) {
                        if ((mstE.src == edge.src && mstE.dest == edge.dest) || 
                            (!activeGraph.isDirected() && mstE.src == edge.dest && mstE.dest == edge.src)) {
                            isMSTEdge = true;
                            break;
                        }
                    }
                }

                g2.setStroke(new BasicStroke(isMSTEdge ? 4 : 2));
                g2.setColor(isMSTEdge ? Theme.SUCCESS : new Color(69, 71, 90));
                
                if (activeGraph.isDirected()) {
                    drawArrowLine(g2, p1.x, p1.y, p2.x, p2.y, 10, 5);
                } else {
                    g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                }

                // Draw edge weight
                int midX = (p1.x + p2.x) / 2;
                int midY = (p1.y + p2.y) / 2;
                g2.setColor(Theme.WARNING);
                g2.setFont(Theme.FONT_SMALL);
                g2.drawString(String.format("%.1f", edge.weight), midX, midY - 5);
            }

            // 2. Draw Vertices
            for (int v : activeGraph.getVertices()) {
                Point p = nodePositions.get(v);
                if (p == null) continue;

                int radius = 22;

                // Highlight traversal order
                boolean isHighlighted = false;
                int highlightIndex = -1;
                if (traversalHighlight.contains(v)) {
                    isHighlighted = true;
                    highlightIndex = traversalHighlight.indexOf(v);
                }

                g2.setColor(isHighlighted ? Theme.PURPLE : Theme.BG_COLOR);
                g2.fillOval(p.x - radius, p.y - radius, 2 * radius, 2 * radius);

                g2.setColor(isHighlighted ? Theme.SIDEBAR_BG : Theme.ACCENT);
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(p.x - radius, p.y - radius, 2 * radius, 2 * radius);

                // Vertex Name & ID
                g2.setColor(isHighlighted ? Theme.SIDEBAR_BG : Theme.TEXT_MAIN);
                g2.setFont(Theme.FONT_BOLD);
                String idStr = String.valueOf(v);
                FontMetrics fm = g2.getFontMetrics();
                int w = fm.stringWidth(idStr);
                g2.drawString(idStr, p.x - w / 2, p.y + 5);

                // Outer label
                g2.setColor(Theme.TEXT_MUTED);
                g2.setFont(Theme.FONT_SMALL);
                String labelStr = activeGraph.getVertexName(v);
                if (isHighlighted) {
                    labelStr += " (" + (highlightIndex + 1) + ")";
                }
                int wL = fm.stringWidth(labelStr);
                g2.drawString(labelStr, p.x - wL / 2, p.y - radius - 5);
            }
        }

        // Draw arrow edge helper for directed graphs
        private void drawArrowLine(Graphics2D g2, int x1, int y1, int x2, int y2, int d, int h) {
            int dx = x2 - x1, dy = y2 - y1;
            double D = Math.sqrt(dx * dx + dy * dy);
            double xm = D - 22; // stop at vertex boundary (radius 22)
            double xn = xm - d;
            double ym = 0;
            double yn = h;
            double x;
            double sin = dy / D;
            double cos = dx / D;

            // Rotate coordinates to standard direction
            x = xm * cos - ym * sin + x1;
            ym = xm * sin + ym * cos + y1;
            xm = x;

            x = xn * cos - yn * sin + x1;
            yn = xn * sin + yn * cos + y1;
            xn = x;

            double xq = (D - 22 - d) * cos - (-h) * sin + x1;
            double yq = (D - 22 - d) * sin + (-h) * cos + y1;

            int[] xpoints = {(int) xm, (int) xn, (int) xq};
            int[] ypoints = {(int) ym, (int) yn, (int) yq};

            g2.drawLine(x1, y1, (int) xm, (int) ym);
            g2.fillPolygon(xpoints, ypoints, 3);
        }
    }
}
