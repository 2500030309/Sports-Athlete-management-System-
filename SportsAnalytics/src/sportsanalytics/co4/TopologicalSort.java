package sportsanalytics.co4;

import sportsanalytics.co3.Graph;
import java.util.*;

public class TopologicalSort {
    public static List<Integer> sort(Graph graph) {
        List<Integer> result = new ArrayList<>();
        Set<Integer> vertices = graph.getVertices();
        
        // Compute in-degrees
        Map<Integer, Integer> inDegree = new HashMap<>();
        for (int v : vertices) {
            inDegree.put(v, 0);
        }

        for (int u : vertices) {
            for (Graph.Edge edge : graph.getNeighbors(u)) {
                inDegree.put(edge.dest, inDegree.getOrDefault(edge.dest, 0) + 1);
            }
        }

        // Initialize queue with vertices having in-degree 0
        Queue<Integer> queue = new LinkedList<>();
        for (int v : vertices) {
            if (inDegree.get(v) == 0) {
                queue.add(v);
            }
        }

        int count = 0;
        while (!queue.isEmpty()) {
            int u = queue.poll();
            result.add(u);
            count++;

            for (Graph.Edge edge : graph.getNeighbors(u)) {
                int v = edge.dest;
                inDegree.put(v, inDegree.get(v) - 1);
                if (inDegree.get(v) == 0) {
                    queue.add(v);
                }
            }
        }

        // Cycle detected
        if (count != vertices.size()) {
            return new ArrayList<>(); // Return empty list indicating cycle exists
        }

        return result;
    }
}
