package sportsanalytics.co4;

import sportsanalytics.co3.Graph;
import java.util.*;

public class FloydWarshall {
    public static class Result {
        public Map<Integer, Map<Integer, Double>> distMatrix;
        public List<Integer> vertices;

        public Result(Map<Integer, Map<Integer, Double>> distMatrix, List<Integer> vertices) {
            this.distMatrix = distMatrix;
            this.vertices = vertices;
        }

        public double getDistance(int u, int v) {
            if (distMatrix.containsKey(u) && distMatrix.get(u).containsKey(v)) {
                return distMatrix.get(u).get(v);
            }
            return Double.MAX_VALUE;
        }
    }

    public static Result computeAllPairsShortestPath(Graph graph) {
        List<Integer> verticesList = new ArrayList<>(graph.getVertices());
        Map<Integer, Map<Integer, Double>> distMatrix = new HashMap<>();

        // Initialize matrix
        for (int u : verticesList) {
            distMatrix.put(u, new HashMap<>());
            for (int v : verticesList) {
                if (u == v) {
                    distMatrix.get(u).put(v, 0.0);
                } else {
                    distMatrix.get(u).put(v, Double.MAX_VALUE);
                }
            }
        }

        // Add edge weights
        for (int u : verticesList) {
            for (Graph.Edge edge : graph.getNeighbors(u)) {
                distMatrix.get(u).put(edge.dest, edge.weight);
            }
        }

        // Run Floyd-Warshall DP transitions
        for (int k : verticesList) {
            for (int i : verticesList) {
                for (int j : verticesList) {
                    double ik = distMatrix.get(i).get(k);
                    double kj = distMatrix.get(k).get(j);
                    double ij = distMatrix.get(i).get(j);

                    if (ik != Double.MAX_VALUE && kj != Double.MAX_VALUE) {
                        if (ik + kj < ij) {
                            distMatrix.get(i).put(j, ik + kj);
                        }
                    }
                }
            }
        }

        return new Result(distMatrix, verticesList);
    }
}
