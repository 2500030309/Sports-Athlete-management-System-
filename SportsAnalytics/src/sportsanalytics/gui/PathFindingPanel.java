package sportsanalytics.gui;

import sportsanalytics.co3.Graph;
import sportsanalytics.co4.Dijkstra;
import sportsanalytics.co4.BellmanFord;
import sportsanalytics.co4.FloydWarshall;
import sportsanalytics.co4.TopologicalSort;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class PathFindingPanel extends JPanel {
    private final MainFrame parentFrame;

    private static final Font TIMES_ROMAN_BODY = new Font("Times New Roman", Font.PLAIN, 14);
    private static final Font TIMES_ROMAN_BOLD = new Font("Times New Roman", Font.BOLD, 13);
    private static final Font TIMES_ROMAN_SMALL = new Font("Times New Roman", Font.PLAIN, 11);

    // Components
    private JComboBox<Integer> srcCombo, destCombo;
    private JTextArea txtPathOutput;
    private JTable matrixTable;
    private DefaultTableModel matrixModel;
    private JPanel topoTimelinePanel;

    // Dynamic input components
    private JTextField txtAddLocId, txtAddLocName;
    private JTextField txtAddRouteSrc, txtAddRouteDest, txtAddRouteWeight;
    private JTextField txtAddMatchId, txtAddMatchName;
    private JTextField txtAddDepSrc, txtAddDepDest;

    public PathFindingPanel(MainFrame frame) {
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
        leftPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 10));

        // Card 1: Single Source Shortest Path (Dijkstra / Bellman-Ford)
        JPanel routeCard = Theme.createCardPanel();
        routeCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("CO4: Shortest Path Routing");
        lblTitle.setFont(Theme.FONT_SUBTITLE);
        lblTitle.setForeground(Theme.ACCENT);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        routeCard.add(lblTitle, gbc);
        gbc.gridwidth = 1;

        gbc.gridy = 1; gbc.gridx = 0;
        JLabel lblSrc = new JLabel("Source Camp:");
        lblSrc.setFont(Theme.FONT_BOLD);
        lblSrc.setForeground(Theme.TEXT_MAIN);
        routeCard.add(lblSrc, gbc);

        srcCombo = new JComboBox<>();
        Theme.styleComboBox(srcCombo);
        gbc.gridx = 1;
        routeCard.add(srcCombo, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        JLabel lblDest = new JLabel("Dest Camp:");
        lblDest.setFont(Theme.FONT_BOLD);
        lblDest.setForeground(Theme.TEXT_MAIN);
        routeCard.add(lblDest, gbc);

        destCombo = new JComboBox<>();
        Theme.styleComboBox(destCombo);
        gbc.gridx = 1;
        routeCard.add(destCombo, gbc);

        JButton btnDijkstra = new JButton("Run Dijkstra's Path");
        Theme.styleButton(btnDijkstra, Theme.ACCENT, Theme.SIDEBAR_BG);
        btnDijkstra.addActionListener(e -> runDijkstra());

        JButton btnBellman = new JButton("Run Bellman-Ford");
        Theme.styleButton(btnBellman, Theme.PURPLE, Theme.SIDEBAR_BG);
        btnBellman.addActionListener(e -> runBellmanFord());

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        routeCard.add(btnDijkstra, gbc);
        gbc.gridy = 4;
        gbc.insets = new Insets(5, 5, 5, 5);
        routeCard.add(btnBellman, gbc);
        gbc.gridwidth = 1;

        leftPanel.add(routeCard);
        leftPanel.add(Box.createVerticalStrut(15));

        // Card 2: Matrix & Schedulers
        JPanel actionCard = Theme.createCardPanel();
        actionCard.setLayout(new GridBagLayout());

        JLabel lblGlobal = new JLabel("Global Analytics & Scheduling");
        lblGlobal.setFont(Theme.FONT_SUBTITLE);
        lblGlobal.setForeground(Theme.ACCENT);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        actionCard.add(lblGlobal, gbc);
        gbc.gridwidth = 1;

        JButton btnFloyd = new JButton("Compute Floyd-Warshall Matrix");
        Theme.styleButton(btnFloyd, Theme.SUCCESS, Theme.SIDEBAR_BG);
        btnFloyd.addActionListener(e -> runFloydWarshall());

        JButton btnTopo = new JButton("Run Match Topological Sort");
        Theme.styleButton(btnTopo, Theme.PURPLE, Theme.SIDEBAR_BG);
        btnTopo.addActionListener(e -> runTopologicalSort());

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 5, 5, 5);
        actionCard.add(btnFloyd, gbc);

        JLabel lblFloydHint = new JLabel("Updates all-pairs distance matrix on the right.");
        lblFloydHint.setFont(Theme.FONT_SMALL);
        lblFloydHint.setForeground(Theme.TEXT_MUTED);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 5, 8, 5);
        actionCard.add(lblFloydHint, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(5, 5, 5, 5);
        actionCard.add(btnTopo, gbc);

        JLabel lblTopoHint = new JLabel("Updates match schedule timeline below.");
        lblTopoHint.setFont(Theme.FONT_SMALL);
        lblTopoHint.setForeground(Theme.TEXT_MUTED);
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 5, 5, 5);
        actionCard.add(lblTopoHint, gbc);

        leftPanel.add(actionCard);
        leftPanel.add(Box.createVerticalStrut(15));

        // Card 3: Dynamic Vertex/Edge Editing at Runtime
        leftPanel.add(buildDynamicInputsPanel());
        leftPanel.add(Box.createVerticalGlue());

        JScrollPane scrollLeft = Theme.createCustomScrollPane(leftPanel);
        scrollLeft.setPreferredSize(new Dimension(380, 800));
        scrollLeft.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollLeft, BorderLayout.WEST);

        // --- Right Workspace ---
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Theme.BG_COLOR);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 15));

        // Block 1: Routing output (Dijkstra/Bellman-Ford results)
        JPanel routeOutPanel = Theme.createCardPanel();
        routeOutPanel.setLayout(new BorderLayout());
        routeOutPanel.setMaximumSize(new Dimension(800, 140));
        
        JLabel lblOut = new JLabel("Route Path Finding Logs");
        lblOut.setFont(Theme.FONT_SUBTITLE);
        lblOut.setForeground(Theme.ACCENT);
        routeOutPanel.add(lblOut, BorderLayout.NORTH);

        txtPathOutput = new JTextArea(3, 40);
        txtPathOutput.setBackground(new Color(49, 50, 68));
        txtPathOutput.setForeground(Theme.TEXT_MAIN);
        txtPathOutput.setFont(TIMES_ROMAN_BODY);
        txtPathOutput.setEditable(false);
        txtPathOutput.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        routeOutPanel.add(txtPathOutput, BorderLayout.CENTER);

        rightPanel.add(routeOutPanel);
        rightPanel.add(Box.createVerticalStrut(15));

        // Block 2: Floyd-Warshall All-Pairs matrix
        JPanel matrixOutPanel = Theme.createCardPanel();
        matrixOutPanel.setLayout(new BorderLayout());
        matrixOutPanel.setMaximumSize(new Dimension(800, 240));

        JLabel lblMat = new JLabel("Floyd-Warshall All-Pairs Distance Comparison Matrix (km)");
        lblMat.setFont(Theme.FONT_SUBTITLE);
        lblMat.setForeground(Theme.ACCENT);
        matrixOutPanel.add(lblMat, BorderLayout.NORTH);

        matrixModel = new DefaultTableModel();
        matrixTable = new JTable(matrixModel);
        matrixTable.setBackground(Theme.CARD_BG);
        matrixTable.setForeground(Theme.TEXT_MAIN);
        matrixTable.getTableHeader().setBackground(Theme.SIDEBAR_BG);
        matrixTable.getTableHeader().setForeground(Theme.ACCENT);
        matrixTable.getTableHeader().setFont(TIMES_ROMAN_BOLD);
        matrixTable.setFont(TIMES_ROMAN_BODY);
        matrixTable.setRowHeight(22);
        matrixTable.setGridColor(new Color(49, 50, 68));

        JScrollPane scrollTable = new JScrollPane(matrixTable);
        scrollTable.getViewport().setBackground(Theme.CARD_BG);
        scrollTable.setBorder(BorderFactory.createLineBorder(new Color(69, 71, 90), 1));
        matrixOutPanel.add(scrollTable, BorderLayout.CENTER);

        rightPanel.add(matrixOutPanel);
        rightPanel.add(Box.createVerticalStrut(15));

        // Block 3: Topological sort Match schedule progression
        JPanel topoOutPanel = Theme.createCardPanel();
        topoOutPanel.setLayout(new BorderLayout());
        topoOutPanel.setMaximumSize(new Dimension(800, 200));

        JLabel lblTopo = new JLabel("Topological Sort: Tournament Match Schedules");
        lblTopo.setFont(Theme.FONT_SUBTITLE);
        lblTopo.setForeground(Theme.ACCENT);
        topoOutPanel.add(lblTopo, BorderLayout.NORTH);

        topoTimelinePanel = new JPanel();
        topoTimelinePanel.setOpaque(false);
        topoTimelinePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));
        
        JScrollPane scrollTimeline = new JScrollPane(topoTimelinePanel);
        scrollTimeline.getViewport().setBackground(Theme.CARD_BG);
        scrollTimeline.setBorder(BorderFactory.createLineBorder(new Color(69, 71, 90), 1));
        topoOutPanel.add(scrollTimeline, BorderLayout.CENTER);

        rightPanel.add(topoOutPanel);

        add(rightPanel, BorderLayout.CENTER);
    }

    private void runDijkstra() {
        if (srcCombo.getSelectedItem() == null || destCombo.getSelectedItem() == null) return;
        int src = (int) srcCombo.getSelectedItem();
        int dest = (int) destCombo.getSelectedItem();

        Graph g = parentFrame.getFacilityGraph();
        Dijkstra.Result res = Dijkstra.findShortestPath(g, src);
        
        List<Integer> path = res.getPath(dest);
        double dist = res.distances.getOrDefault(dest, Double.MAX_VALUE);

        StringBuilder sb = new StringBuilder();
        sb.append("ALGORITHM: Dijkstra's Single Source Shortest Path\n");
        if (path.isEmpty() || dist == Double.MAX_VALUE) {
            sb.append(String.format("No route exists between %s and %s.\n", g.getVertexName(src), g.getVertexName(dest)));
        } else {
            sb.append("Optimal path: ");
            for (int i = 0; i < path.size(); i++) {
                sb.append(g.getVertexName(path.get(i)));
                if (i < path.size() - 1) sb.append(" -> ");
            }
            sb.append(String.format("\nTotal Distance: %.1f km\n", dist));
        }
        txtPathOutput.setText(sb.toString());
    }

    private void runBellmanFord() {
        if (srcCombo.getSelectedItem() == null || destCombo.getSelectedItem() == null) return;
        int src = (int) srcCombo.getSelectedItem();
        int dest = (int) destCombo.getSelectedItem();

        Graph g = parentFrame.getFacilityGraph();
        BellmanFord.Result res = BellmanFord.findShortestPath(g, src);

        StringBuilder sb = new StringBuilder();
        sb.append("ALGORITHM: Bellman-Ford Shortest Path Solver\n");
        if (res.hasNegativeCycle) {
            sb.append("WARNING: Negative-weight cycle detected! Rankings/Distances are inconsistent!\n");
        } else {
            List<Integer> path = res.getPath(dest);
            double dist = res.distances.getOrDefault(dest, Double.MAX_VALUE);

            if (path.isEmpty() || dist == Double.MAX_VALUE) {
                sb.append(String.format("No route exists between %s and %s.\n", g.getVertexName(src), g.getVertexName(dest)));
            } else {
                sb.append("Optimal path: ");
                for (int i = 0; i < path.size(); i++) {
                    sb.append(g.getVertexName(path.get(i)));
                    if (i < path.size() - 1) sb.append(" -> ");
                }
                sb.append(String.format("\nTotal Distance: %.1f km\n", dist));
            }
        }
        txtPathOutput.setText(sb.toString());
    }

    private void runFloydWarshall() {
        Graph g = parentFrame.getFacilityGraph();
        FloydWarshall.Result res = FloydWarshall.computeAllPairsShortestPath(g);

        // Setup columns (v names)
        Vector<String> colNames = new Vector<>();
        colNames.add("Location");
        for (int v : res.vertices) {
            colNames.add(g.getVertexName(v));
        }

        Vector<Vector<Object>> data = new Vector<>();
        for (int u : res.vertices) {
            Vector<Object> row = new Vector<>();
            row.add(g.getVertexName(u));
            for (int v : res.vertices) {
                double dist = res.getDistance(u, v);
                if (dist == Double.MAX_VALUE) {
                    row.add("INF");
                } else {
                    row.add(String.format("%.1f", dist));
                }
            }
            data.add(row);
        }

        matrixModel.setDataVector(data, colNames);

        List<Integer> unconnected = new ArrayList<>();
        for (int u : res.vertices) {
            boolean hasConnection = false;
            for (int v : res.vertices) {
                if (u != v) {
                    if (res.getDistance(u, v) != Double.MAX_VALUE || res.getDistance(v, u) != Double.MAX_VALUE) {
                        hasConnection = true;
                        break;
                    }
                }
            }
            if (!hasConnection) {
                unconnected.add(u);
            }
        }

        StringBuilder log = new StringBuilder("ALGORITHM: Floyd-Warshall (All-Pairs Shortest Path)\n" +
                "Updated matrix with shortest distances between all camps.\n" +
                "Rows/columns show each camp; INF means no route.");
        if (!unconnected.isEmpty()) {
            log.append("\n\nNOTE: Unconnected locations detected (showing INF): ");
            for (int i = 0; i < unconnected.size(); i++) {
                int uv = unconnected.get(i);
                log.append(g.getVertexName(uv)).append(" (ID: ").append(uv).append(")");
                if (i < unconnected.size() - 1) log.append(", ");
            }
            log.append(".\nAdd routes in the panel to connect them to the network.");
        }
        txtPathOutput.setText(log.toString());
    }

    private void runTopologicalSort() {
        Graph depGraph = parentFrame.getDependencyGraph();
        List<Integer> sorted = TopologicalSort.sort(depGraph);

        topoTimelinePanel.removeAll();
        if (sorted.isEmpty()) {
            JLabel lblCycle = new JLabel("Cycle Detected in Match Schedules! Impossible to Schedule.");
            lblCycle.setFont(TIMES_ROMAN_BOLD);
            lblCycle.setForeground(Theme.ERROR);
            topoTimelinePanel.add(lblCycle);
            txtPathOutput.setText(
                    "ALGORITHM: Topological Sort\n" +
                    "Cycle detected in dependency graph, so schedule order cannot be formed."
            );
        } else {
            for (int i = 0; i < sorted.size(); i++) {
                int matchId = sorted.get(i);
                String name = depGraph.getVertexName(matchId);

                // Create a stylized stage panel for each match
                JPanel stage = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(Theme.SIDEBAR_BG);
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                        g2.setColor(Theme.ACCENT);
                        g2.setStroke(new BasicStroke(1.5f));
                        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                        g2.dispose();
                    }
                };
                stage.setOpaque(false);
                stage.setPreferredSize(new Dimension(140, 50));
                stage.setLayout(new BorderLayout());

                JLabel lblMatch = new JLabel("Match " + matchId, JLabel.CENTER);
                lblMatch.setFont(TIMES_ROMAN_BOLD);
                lblMatch.setForeground(Theme.TEXT_MAIN);
                stage.add(lblMatch, BorderLayout.NORTH);

                JLabel lblName = new JLabel(name, JLabel.CENTER);
                lblName.setFont(TIMES_ROMAN_SMALL);
                lblName.setForeground(Theme.TEXT_MAIN);
                stage.add(lblName, BorderLayout.CENTER);

                topoTimelinePanel.add(stage);

                // Draw connector arrow between stages
                if (i < sorted.size() - 1) {
                    JLabel lblArrow = new JLabel("➔");
                    lblArrow.setFont(new Font("SansSerif", Font.PLAIN, 18));
                    lblArrow.setForeground(Theme.TEXT_MUTED);
                    topoTimelinePanel.add(lblArrow);
                }
            }
            txtPathOutput.setText(
                    "ALGORITHM: Topological Sort\n" +
                    "Schedule order generated successfully.\n" +
                    "Timeline cards show match execution sequence."
            );
        }

        topoTimelinePanel.revalidate();
        topoTimelinePanel.repaint();
    }

    private void updateComboBoxesOnly() {
        Object prevSrc = srcCombo.getSelectedItem();
        Object prevDest = destCombo.getSelectedItem();

        srcCombo.removeAllItems();
        destCombo.removeAllItems();

        Graph g = parentFrame.getFacilityGraph();
        List<Integer> sortedVertices = new ArrayList<>(g.getVertices());
        Collections.sort(sortedVertices);
        for (int v : sortedVertices) {
            srcCombo.addItem(v);
            destCombo.addItem(v);
        }

        if (prevSrc != null && g.getVertices().contains(prevSrc)) {
            srcCombo.setSelectedItem(prevSrc);
        }
        if (prevDest != null && g.getVertices().contains(prevDest)) {
            destCombo.setSelectedItem(prevDest);
        }
    }

    public void refreshData() {
        updateComboBoxesOnly();
        // Auto-run matrix on switch
        runFloydWarshall();
        runTopologicalSort();
    }

    private JPanel buildDynamicInputsPanel() {
        JPanel card = Theme.createCardPanel();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title: Dynamic Network Inputs
        JLabel lblTitle = new JLabel("Dynamic Inputs");
        lblTitle.setFont(Theme.FONT_SUBTITLE);
        lblTitle.setForeground(Theme.ACCENT);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(lblTitle, gbc);
        gbc.gridwidth = 1;

        int row = 1;

        // --- Sub-section: Facilities / Floyd-Warshall ---
        JLabel lblFacilitySec = new JLabel("Facility Graph (Floyd-Warshall)");
        lblFacilitySec.setFont(Theme.FONT_BOLD);
        lblFacilitySec.setForeground(Theme.SUCCESS);
        gbc.gridy = row++; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 5, 4, 5);
        card.add(lblFacilitySec, gbc);
        gbc.gridwidth = 1;
        gbc.insets = new Insets(4, 5, 4, 5);

        // Add Vertex/Location
        JLabel lblLocId = new JLabel("Loc ID:");
        lblLocId.setFont(Theme.FONT_BOLD);
        lblLocId.setForeground(Theme.TEXT_MAIN);
        gbc.gridy = row; gbc.gridx = 0;
        card.add(lblLocId, gbc);

        txtAddLocId = new JTextField();
        Theme.styleTextField(txtAddLocId);
        gbc.gridx = 1;
        card.add(txtAddLocId, gbc);

        row++;
        JLabel lblLocName = new JLabel("Loc Name:");
        lblLocName.setFont(Theme.FONT_BOLD);
        lblLocName.setForeground(Theme.TEXT_MAIN);
        gbc.gridy = row; gbc.gridx = 0;
        card.add(lblLocName, gbc);

        txtAddLocName = new JTextField();
        Theme.styleTextField(txtAddLocName);
        gbc.gridx = 1;
        card.add(txtAddLocName, gbc);

        row++;
        JButton btnAddLoc = new JButton("Add Location");
        Theme.styleButton(btnAddLoc, new Color(49, 50, 68), Theme.TEXT_MAIN);
        btnAddLoc.addActionListener(e -> handleAddLocation());
        gbc.gridy = row; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(4, 5, 8, 5);
        card.add(btnAddLoc, gbc);
        gbc.gridwidth = 1;
        gbc.insets = new Insets(4, 5, 4, 5);

        // Add Route
        row++;
        JLabel lblRouteSrc = new JLabel("Src ID:");
        lblRouteSrc.setFont(Theme.FONT_BOLD);
        lblRouteSrc.setForeground(Theme.TEXT_MAIN);
        gbc.gridy = row; gbc.gridx = 0;
        card.add(lblRouteSrc, gbc);

        txtAddRouteSrc = new JTextField();
        Theme.styleTextField(txtAddRouteSrc);
        gbc.gridx = 1;
        card.add(txtAddRouteSrc, gbc);

        row++;
        JLabel lblRouteDest = new JLabel("Dest ID:");
        lblRouteDest.setFont(Theme.FONT_BOLD);
        lblRouteDest.setForeground(Theme.TEXT_MAIN);
        gbc.gridy = row; gbc.gridx = 0;
        card.add(lblRouteDest, gbc);

        txtAddRouteDest = new JTextField();
        Theme.styleTextField(txtAddRouteDest);
        gbc.gridx = 1;
        card.add(txtAddRouteDest, gbc);

        row++;
        JLabel lblRouteWeight = new JLabel("Weight (km):");
        lblRouteWeight.setFont(Theme.FONT_BOLD);
        lblRouteWeight.setForeground(Theme.TEXT_MAIN);
        gbc.gridy = row; gbc.gridx = 0;
        card.add(lblRouteWeight, gbc);

        txtAddRouteWeight = new JTextField("1.0");
        Theme.styleTextField(txtAddRouteWeight);
        gbc.gridx = 1;
        card.add(txtAddRouteWeight, gbc);

        row++;
        JButton btnAddRoute = new JButton("Add Route");
        Theme.styleButton(btnAddRoute, new Color(49, 50, 68), Theme.TEXT_MAIN);
        btnAddRoute.addActionListener(e -> handleAddRoute());
        gbc.gridy = row; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(4, 5, 12, 5);
        card.add(btnAddRoute, gbc);
        gbc.gridwidth = 1;
        gbc.insets = new Insets(4, 5, 4, 5);

        // --- Sub-section: Matches / Topological Sort ---
        row++;
        JLabel lblMatchSec = new JLabel("Tournament Matches (Topo)");
        lblMatchSec.setFont(Theme.FONT_BOLD);
        lblMatchSec.setForeground(Theme.PURPLE);
        gbc.gridy = row; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 5, 4, 5);
        card.add(lblMatchSec, gbc);
        gbc.gridwidth = 1;
        gbc.insets = new Insets(4, 5, 4, 5);

        // Add Match
        row++;
        JLabel lblMatchId = new JLabel("Match ID:");
        lblMatchId.setFont(Theme.FONT_BOLD);
        lblMatchId.setForeground(Theme.TEXT_MAIN);
        gbc.gridy = row; gbc.gridx = 0;
        card.add(lblMatchId, gbc);

        txtAddMatchId = new JTextField();
        Theme.styleTextField(txtAddMatchId);
        gbc.gridx = 1;
        card.add(txtAddMatchId, gbc);

        row++;
        JLabel lblMatchName = new JLabel("Match Name:");
        lblMatchName.setFont(Theme.FONT_BOLD);
        lblMatchName.setForeground(Theme.TEXT_MAIN);
        gbc.gridy = row; gbc.gridx = 0;
        card.add(lblMatchName, gbc);

        txtAddMatchName = new JTextField();
        Theme.styleTextField(txtAddMatchName);
        gbc.gridx = 1;
        card.add(txtAddMatchName, gbc);

        row++;
        JButton btnAddMatch = new JButton("Add Match");
        Theme.styleButton(btnAddMatch, new Color(49, 50, 68), Theme.TEXT_MAIN);
        btnAddMatch.addActionListener(e -> handleAddMatch());
        gbc.gridy = row; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(4, 5, 8, 5);
        card.add(btnAddMatch, gbc);
        gbc.gridwidth = 1;
        gbc.insets = new Insets(4, 5, 4, 5);

        // Add Dependency
        row++;
        JLabel lblDepSrc = new JLabel("Prereq ID:");
        lblDepSrc.setFont(Theme.FONT_BOLD);
        lblDepSrc.setForeground(Theme.TEXT_MAIN);
        gbc.gridy = row; gbc.gridx = 0;
        card.add(lblDepSrc, gbc);

        txtAddDepSrc = new JTextField();
        Theme.styleTextField(txtAddDepSrc);
        gbc.gridx = 1;
        card.add(txtAddDepSrc, gbc);

        row++;
        JLabel lblDepDest = new JLabel("Dep ID:");
        lblDepDest.setFont(Theme.FONT_BOLD);
        lblDepDest.setForeground(Theme.TEXT_MAIN);
        gbc.gridy = row; gbc.gridx = 0;
        card.add(lblDepDest, gbc);

        txtAddDepDest = new JTextField();
        Theme.styleTextField(txtAddDepDest);
        gbc.gridx = 1;
        card.add(txtAddDepDest, gbc);

        row++;
        JButton btnAddDep = new JButton("Add Dependency");
        Theme.styleButton(btnAddDep, new Color(49, 50, 68), Theme.TEXT_MAIN);
        btnAddDep.addActionListener(e -> handleAddDependency());
        gbc.gridy = row; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(4, 5, 5, 5);
        card.add(btnAddDep, gbc);

        return card;
    }

    private void handleAddLocation() {
        try {
            int id = Integer.parseInt(txtAddLocId.getText().trim());
            String name = txtAddLocName.getText().trim();
            if (name.isEmpty()) {
                name = "Facility " + id;
            }

            Graph facilityGraph = parentFrame.getFacilityGraph();
            facilityGraph.addVertex(id, name);

            txtPathOutput.setText("Added Location: " + name + " (ID: " + id + ")\n" +
                    "Click 'Compute Floyd-Warshall Matrix' to update the table.");
            updateComboBoxesOnly();
            txtAddLocId.setText("");
            txtAddLocName.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid integer for Location ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleAddRoute() {
        try {
            int src = Integer.parseInt(txtAddRouteSrc.getText().trim());
            int dest = Integer.parseInt(txtAddRouteDest.getText().trim());
            double w = Double.parseDouble(txtAddRouteWeight.getText().trim());

            Graph facilityGraph = parentFrame.getFacilityGraph();
            facilityGraph.addVertex(src, "Facility " + src);
            facilityGraph.addVertex(dest, "Facility " + dest);
            facilityGraph.addEdge(src, dest, w);

            txtPathOutput.setText("Added Route: Facility " + src + " ➔ Facility " + dest + " (" + w + " km)\n" +
                    "Click 'Compute Floyd-Warshall Matrix' to update the table.");
            updateComboBoxesOnly();
            txtAddRouteSrc.setText("");
            txtAddRouteDest.setText("");
            txtAddRouteWeight.setText("1.0");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter valid numeric values for Route parameters.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleAddMatch() {
        try {
            int id = Integer.parseInt(txtAddMatchId.getText().trim());
            String name = txtAddMatchName.getText().trim();
            if (name.isEmpty()) {
                name = "Match " + id;
            }

            Graph dependencyGraph = parentFrame.getDependencyGraph();
            dependencyGraph.addVertex(id, name);

            txtPathOutput.setText("Added Tournament Match: " + name + " (ID: " + id + ")\n" +
                    "Click 'Run Match Topological Sort' to update the timeline.");
            txtAddMatchId.setText("");
            txtAddMatchName.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid integer for Match ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleAddDependency() {
        try {
            int src = Integer.parseInt(txtAddDepSrc.getText().trim());
            int dest = Integer.parseInt(txtAddDepDest.getText().trim());

            Graph dependencyGraph = parentFrame.getDependencyGraph();
            dependencyGraph.addVertex(src, "Match " + src);
            dependencyGraph.addVertex(dest, "Match " + dest);
            dependencyGraph.addEdge(src, dest, 1.0);

            txtPathOutput.setText("Added Match Dependency: Match " + src + " ➔ Match " + dest + "\n" +
                    "Click 'Run Match Topological Sort' to update the timeline.");
            txtAddDepSrc.setText("");
            txtAddDepDest.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter valid integer values for Match IDs.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
