package sportsanalytics.co4;

import sportsanalytics.co3.Graph;
import java.util.*;

public class BellmanFord {
    public static class Result {
        public Map<Integer, Double> distances;
        public Map<Integer, Integer> parents;
        public boolean hasNegativeCycle;

        public Result(Map<Integer, Double> distances, Map<Integer, Integer> parents, boolean hasNegativeCycle) {
            this.distances = distances;
            this.parents = parents;
            this.hasNegativeCycle = hasNegativeCycle;
        }

        public List<Integer> getPath(int target) {
            List<Integer> path = new ArrayList<>();
            if (hasNegativeCycle || !distances.containsKey(target) || distances.get(target) == Double.MAX_VALUE) {
                return path;
            }
            Integer current = target;
            while (current != null) {
                path.add(0, current);
                current = parents.get(current);
            }
            return path;
        }
    }

    public static Result findShortestPath(Graph graph, int startVertex) {
        Map<Integer, Double> distances = new HashMap<>();
        Map<Integer, Integer> parents = new HashMap<>();
        
        for (int v : graph.getVertices()) {
            distances.put(v, Double.MAX_VALUE);
            parents.put(v, null);
        }
        distances.put(startVertex, 0.0);

        int numVertices = graph.getVertexCount();
        List<Graph.Edge> allEdges = graph.getAllEdges();

        // Relax edges V-1 times
        for (int i = 1; i < numVertices; i++) {
            boolean anyChange = false;
            for (Graph.Edge edge : allEdges) {
                double uDist = distances.get(edge.src);
                if (uDist != Double.MAX_VALUE) {
                    double vDist = distances.get(edge.dest);
                    if (uDist + edge.weight < vDist) {
                        distances.put(edge.dest, uDist + edge.weight);
                        parents.put(edge.dest, edge.src);
                        anyChange = true;
                    }
                }
            }
            if (!anyChange) {
                break; // Optimization: exit early if no changes
            }
        }

        // Check for negative-weight cycles
        boolean hasNegativeCycle = false;
        for (Graph.Edge edge : allEdges) {
            double uDist = distances.get(edge.src);
            if (uDist != Double.MAX_VALUE) {
                double vDist = distances.get(edge.dest);
                if (uDist + edge.weight < vDist) {
                    hasNegativeCycle = true;
                    break;
                }
            }
        }

        return new Result(distances, parents, hasNegativeCycle);
    }
}
