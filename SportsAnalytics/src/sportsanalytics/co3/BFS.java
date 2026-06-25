package sportsanalytics.co3;

import java.util.*;

public class BFS {
    public static List<Integer> traverse(Graph graph, int startVertex) {
        List<Integer> visitedOrder = new ArrayList<>();
        if (!graph.getVertices().contains(startVertex)) {
            return visitedOrder;
        }

        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();

        queue.add(startVertex);
        visited.add(startVertex);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            visitedOrder.add(current);

            for (Graph.Edge edge : graph.getNeighbors(current)) {
                if (!visited.contains(edge.dest)) {
                    visited.add(edge.dest);
                    queue.add(edge.dest);
                }
            }
        }

        return visitedOrder;
    }
}
