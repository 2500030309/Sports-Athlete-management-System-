package sportsanalytics.co3;

import java.util.*;

public class PrimsMST {
    public static List<Graph.Edge> findMST(Graph graph) {
        Set<Integer> vertices = graph.getVertices();
        if (vertices.isEmpty()) {
            return new ArrayList<>();
        }
        return findMST(graph, vertices.iterator().next());
    }

    public static List<Graph.Edge> findMST(Graph graph, int startVertex) {
        List<Graph.Edge> mstEdges = new ArrayList<>();
        Set<Integer> vertices = graph.getVertices();
        if (vertices.isEmpty() || !vertices.contains(startVertex)) {
            return mstEdges;
        }

        int start = startVertex;
        Set<Integer> inMST = new HashSet<>();
        inMST.add(start);

        // Priority queue stores edges ordered by weight
        PriorityQueue<Graph.Edge> pq = new PriorityQueue<>(Comparator.comparingDouble(e -> e.weight));

        // Add all neighbors of start to PQ
        for (Graph.Edge edge : graph.getNeighbors(start)) {
            pq.add(edge);
        }

        while (!pq.isEmpty() && inMST.size() < vertices.size()) {
            Graph.Edge edge = pq.poll();

            // If both vertices are already in MST, skip to avoid cycles
            if (inMST.contains(edge.src) && inMST.contains(edge.dest)) {
                continue;
            }

            // The new vertex to be added to MST
            int newVertex = inMST.contains(edge.src) ? edge.dest : edge.src;
            inMST.add(newVertex);
            mstEdges.add(edge);

            // Add all edges from the new vertex to PQ if their destination is not in MST
            for (Graph.Edge nextEdge : graph.getNeighbors(newVertex)) {
                if (!inMST.contains(nextEdge.dest)) {
                    pq.add(nextEdge);
                }
            }
        }

        return mstEdges;
    }
}
