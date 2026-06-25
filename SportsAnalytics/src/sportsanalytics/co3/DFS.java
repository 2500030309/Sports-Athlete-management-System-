package sportsanalytics.co3;

import java.util.*;

public class DFS {
    public static List<Integer> traverse(Graph graph, int startVertex) {
        List<Integer> visitedOrder = new ArrayList<>();
        if (!graph.getVertices().contains(startVertex)) {
            return visitedOrder;
        }

        Set<Integer> visited = new HashSet<>();
        dfsRec(graph, startVertex, visited, visitedOrder);
        return visitedOrder;
    }

    private static void dfsRec(Graph graph, int current, Set<Integer> visited, List<Integer> visitedOrder) {
        visited.add(current);
        visitedOrder.add(current);

        for (Graph.Edge edge : graph.getNeighbors(current)) {
            if (!visited.contains(edge.dest)) {
                dfsRec(graph, edge.dest, visited, visitedOrder);
            }
        }
    }

    public static boolean hasCycle(Graph graph) {
        Set<Integer> visited = new HashSet<>();
        
        if (graph.isDirected()) {
            Set<Integer> recStack = new HashSet<>();
            for (int v : graph.getVertices()) {
                if (!visited.contains(v)) {
                    if (isCyclicDirected(graph, v, visited, recStack)) {
                        return true;
                    }
                }
            }
        } else {
            for (int v : graph.getVertices()) {
                if (!visited.contains(v)) {
                    if (isCyclicUndirected(graph, v, visited, -1)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean isCyclicDirected(Graph graph, int v, Set<Integer> visited, Set<Integer> recStack) {
        visited.add(v);
        recStack.add(v);

        for (Graph.Edge edge : graph.getNeighbors(v)) {
            int neighbor = edge.dest;
            if (!visited.contains(neighbor)) {
                if (isCyclicDirected(graph, neighbor, visited, recStack)) {
                    return true;
                }
            } else if (recStack.contains(neighbor)) {
                return true;
            }
        }

        recStack.remove(v);
        return false;
    }

    private static boolean isCyclicUndirected(Graph graph, int v, Set<Integer> visited, int parent) {
        visited.add(v);

        for (Graph.Edge edge : graph.getNeighbors(v)) {
            int neighbor = edge.dest;
            if (!visited.contains(neighbor)) {
                if (isCyclicUndirected(graph, neighbor, visited, v)) {
                    return true;
                }
            } else if (neighbor != parent) {
                return true;
            }
        }
        return false;
    }
}
