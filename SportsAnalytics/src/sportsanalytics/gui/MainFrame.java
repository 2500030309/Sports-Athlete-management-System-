package sportsanalytics.gui;

import sportsanalytics.model.Athlete;
import sportsanalytics.model.Team;
import sportsanalytics.co1.AthleteBST;
import sportsanalytics.co1.AthleteAVL;
import sportsanalytics.co2.BTree;
import sportsanalytics.co2.BPlusTree;
import sportsanalytics.co3.Graph;
import sportsanalytics.util.SampleData;
import sportsanalytics.util.DatabaseHelper;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {
    // Shared Data Context
    private final List<Athlete> athletes;
    private final List<Team> teams;
    private final AthleteBST bst;
    private final AthleteAVL avl;
    private final BTree bTree;
    private final BPlusTree bPlusTree;
    private final Graph facilityGraph;
    private final Graph dependencyGraph;

    // GUI Elements
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JPanel sidebar;
    private List<JButton> sidebarButtons;

    public MainFrame() {
        // Initialize Shared Data
        DatabaseHelper.initializeDatabase(SampleData.getSampleAthletes());
        this.athletes = new ArrayList<>(DatabaseHelper.loadAthletes());
        this.teams = new ArrayList<>(SampleData.getSampleTeams());
        
        this.bst = new AthleteBST();
        this.avl = new AthleteAVL();
        SampleData.populateCO1(athletes, bst, avl);

        this.bTree = new BTree(3); // Degree 3
        this.bPlusTree = new BPlusTree();
        SampleData.populateCO2(athletes, bTree, bPlusTree);

        this.facilityGraph = SampleData.getSampleFacilityGraph();
        this.dependencyGraph = SampleData.getSampleDependencyGraph();

        // Configure Frame
        setTitle("SportsAnalytics - Athlete Performance & Tournament Management");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BG_COLOR);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Sidebar Panel
        sidebar = new JPanel();
        sidebar.setBackground(Theme.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(250, 800));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(43, 44, 64)));

        // Sidebar Header
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setMaximumSize(new Dimension(250, 100));
        headerPanel.setLayout(new GridBagLayout());
        JLabel logoLabel = new JLabel("SportsAnalytics");
        logoLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        logoLabel.setForeground(Theme.ACCENT);
        headerPanel.add(logoLabel);
        sidebar.add(headerPanel);
        sidebar.add(Box.createVerticalStrut(20));

        // Content Area (CardLayout)
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Theme.BG_COLOR);

        // Create Subpanels
        AthletePanel athletePanel = new AthletePanel(this);
        PerformancePanel performancePanel = new PerformancePanel(this);
        GraphPanel graphPanel = new GraphPanel(this);
        PathFindingPanel pathFindingPanel = new PathFindingPanel(this);
        SortingPanel sortingPanel = new SortingPanel(this);
        GreedyDPPanel greedyDPPanel = new GreedyDPPanel(this);

        contentPanel.add(athletePanel, "CO1");
        contentPanel.add(performancePanel, "CO2");
        contentPanel.add(graphPanel, "CO3");
        contentPanel.add(pathFindingPanel, "CO4");
        contentPanel.add(sortingPanel, "CO5");
        contentPanel.add(greedyDPPanel, "CO6");

        // Add Sidebar navigation buttons
        sidebarButtons = new ArrayList<>();
        addNavigationButton("CO1: Trees & Balanced (AVL/BST)", "CO1");
        addNavigationButton("CO2: Multiway & Ranges", "CO2");
        addNavigationButton("CO3: Facility Graphs & MST", "CO3");
        addNavigationButton("CO4: Shortest Paths & Scheduling", "CO4");
        addNavigationButton("CO5: Sorting & Benchmarks", "CO5");
        addNavigationButton("CO6: Greedy vs Dynamic Programming", "CO6");

        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        // Highlight first button
        highlightButton(0);
    }

    private void addNavigationButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(220, 45));
        btn.setFont(Theme.FONT_BOLD);
        btn.setForeground(Theme.TEXT_MUTED);
        btn.setBackground(Theme.SIDEBAR_BG);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        
        btn.addActionListener(e -> {
            cardLayout.show(contentPanel, cardName);
            // Refresh panels when switched
            switch (cardName) {
                case "CO1":
                    ((AthletePanel) contentPanel.getComponent(0)).refreshData();
                    break;
                case "CO2":
                    ((PerformancePanel) contentPanel.getComponent(1)).refreshData();
                    break;
                case "CO3":
                    ((GraphPanel) contentPanel.getComponent(2)).refreshData();
                    break;
                case "CO4":
                    ((PathFindingPanel) contentPanel.getComponent(3)).refreshData();
                    break;
                case "CO5":
                    ((SortingPanel) contentPanel.getComponent(4)).refreshData();
                    break;
                case "CO6":
                    ((GreedyDPPanel) contentPanel.getComponent(5)).refreshData();
                    break;
            }
            int index = sidebarButtons.indexOf(btn);
            highlightButton(index);
        });

        sidebar.add(btn);
        sidebar.add(Box.createVerticalStrut(10));
        sidebarButtons.add(btn);
    }

    private void highlightButton(int activeIndex) {
        for (int i = 0; i < sidebarButtons.size(); i++) {
            JButton btn = sidebarButtons.get(i);
            if (i == activeIndex) {
                btn.setForeground(Theme.SIDEBAR_BG);
                btn.setBackground(Theme.ACCENT);
            } else {
                btn.setForeground(Theme.TEXT_MUTED);
                btn.setBackground(Theme.SIDEBAR_BG);
            }
        }
    }

    // Getters for Panels to Access
    public List<Athlete> getAthletes() {
        return athletes;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public AthleteBST getBst() {
        return bst;
    }

    public AthleteAVL getAvl() {
        return avl;
    }

    public BTree getBTree() {
        return bTree;
    }

    public BPlusTree getBPlusTree() {
        return bPlusTree;
    }

    public Graph getFacilityGraph() {
        return facilityGraph;
    }

    public Graph getDependencyGraph() {
        return dependencyGraph;
    }

    public void addAthlete(Athlete a) {
        athletes.add(a);
        bst.insert(a);
        avl.insert(a);
        bTree.insert(a.getAthleteId());
        bPlusTree.insert(a.getAthleteId());
        DatabaseHelper.insertAthlete(a);
    }

    public void removeAthlete(Athlete a) {
        athletes.remove(a);
        bst.delete(a.getAthleteId());
        avl.delete(a);
        // Reinstate BTree & BPlusTree from scratch to clean deleted keys easily
        SampleData.populateCO2(athletes, bTree, bPlusTree);
        DatabaseHelper.deleteAthlete(a.getAthleteId());
    }
}


