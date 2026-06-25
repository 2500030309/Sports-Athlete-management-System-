package sportsanalytics.util;

import sportsanalytics.model.Athlete;
import sportsanalytics.model.Team;
import sportsanalytics.co1.AthleteBST;
import sportsanalytics.co1.AthleteAVL;
import sportsanalytics.co2.BTree;
import sportsanalytics.co2.BPlusTree;
import sportsanalytics.co3.Graph;

import java.util.ArrayList;
import java.util.List;

public class SampleData {
    public static List<Athlete> getSampleAthletes() {
        List<Athlete> list = new ArrayList<>();
        list.add(new Athlete(101, "Alistair Brown", "Running", 94.5, 1, new int[]{95, 92, 98, 91, 99, 93, 97, 96}));
        list.add(new Athlete(102, "Bianca Visser", "Running", 88.2, 1, new int[]{85, 90, 88, 86, 92, 89, 87, 91}));
        list.add(new Athlete(103, "Chloe Chen", "Swimming", 95.8, 2, new int[]{96, 94, 95, 97, 98, 93, 99, 96}));
        list.add(new Athlete(104, "Daniel Miller", "Swimming", 79.4, 2, new int[]{80, 78, 82, 79, 81, 77, 83, 76}));
        list.add(new Athlete(105, "Ethan Hunt", "Basketball", 91.0, 3, new int[]{90, 92, 88, 91, 93, 90, 94, 92}));
        list.add(new Athlete(106, "Fiona Gallagher", "Basketball", 85.6, 3, new int[]{86, 84, 85, 87, 83, 86, 88, 85}));
        list.add(new Athlete(107, "George Brooks", "Tennis", 92.3, 4, new int[]{92, 90, 94, 91, 95, 93, 91, 93}));
        return list;
    }

    public static List<Team> getSampleTeams() {
        List<Team> list = new ArrayList<>();
        list.add(new Team(1, "Veloce Striders", "Marcus Aurelius", 450.0));
        list.add(new Team(2, "Aqua Sirens", "Sarah Connor", 480.0));
        list.add(new Team(3, "Titan Ballers", "Phil Jackson", 420.0));
        list.add(new Team(4, "Apex Aces", "Ivan Lendl", 460.0));
        return list;
    }

    public static void populateCO1(List<Athlete> athletes, AthleteBST bst, AthleteAVL avl) {
        bst.clear();
        avl.clear();
        for (Athlete a : athletes) {
            bst.insert(a);
            avl.insert(a);
        }
    }

    public static void populateCO2(List<Athlete> athletes, BTree bTree, BPlusTree bPlusTree) {
        bTree.clear();
        bPlusTree.clear();
        for (Athlete a : athletes) {
            bTree.insert(a.getAthleteId());
            bPlusTree.insert(a.getAthleteId());
        }
    }

    public static Graph getSampleFacilityGraph() {
        Graph graph = new Graph(false); // Undirected graph
        
        // Vertices (facilities)
        graph.addVertex(1, "Main Stadium");
        graph.addVertex(2, "High-Perf Gym");
        graph.addVertex(3, "Aquatic Complex");
        graph.addVertex(4, "Rehab Center");
        graph.addVertex(5, "Altitude Camp");
        graph.addVertex(6, "Sports Hostel");

        // Edges (distances in km)
        graph.addEdge(1, 2, 5.0);
        graph.addEdge(1, 3, 8.0);
        graph.addEdge(2, 3, 4.0);
        graph.addEdge(2, 4, 12.0);
        graph.addEdge(3, 4, 7.0);
        graph.addEdge(3, 5, 15.0);
        graph.addEdge(4, 5, 6.0);
        graph.addEdge(4, 6, 9.0);
        graph.addEdge(5, 6, 10.0);

        return graph;
    }

    public static Graph getSampleDependencyGraph() {
        Graph graph = new Graph(true); // Directed graph

        // Vertices representing matches
        graph.addVertex(10, "Quarterfinal 1");
        graph.addVertex(11, "Quarterfinal 2");
        graph.addVertex(12, "Quarterfinal 3");
        graph.addVertex(13, "Quarterfinal 4");
        graph.addVertex(14, "Semifinal 1");
        graph.addVertex(15, "Semifinal 2");
        graph.addVertex(16, "Championship");

        // Edges representing dependencies
        graph.addEdge(10, 14, 1.0);
        graph.addEdge(11, 14, 1.0);
        graph.addEdge(12, 15, 1.0);
        graph.addEdge(13, 15, 1.0);
        graph.addEdge(14, 16, 1.0);
        graph.addEdge(15, 16, 1.0);

        return graph;
    }
}
