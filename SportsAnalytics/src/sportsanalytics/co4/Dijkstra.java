package sportsanalytics.co4;

import sportsanalytics.co3.Graph;
import java.util.*;

public class Dijkstra {
    public static class Result {
        public Map<Integer, Double> distances;
        public Map<Integer, Integer> parents;

        public Result(Map<Integer, Double> distances, Map<Integer, Integer> parents) {
            this.distances = distances;
            this.parents = parents;
        }

        public List<Integer> getPath(int target) {
            List<Integer> path = new ArrayList<>();
            if (!distances.containsKey(target) || distances.get(target) == Double.MAX_VALUE) {
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
        Set<Integer> visited = new HashSet<>();

        for (int v : graph.getVertices()) {
            distances.put(v, Double.MAX_VALUE);
            parents.put(v, null);
        }
        distances.put(startVertex, 0.0);

        // Priority Queue elements: [vertexId, distance]
        PriorityQueue<double[]> pq = new PriorityQueue<>(Comparator.comparingDouble(a -> a[1]));
        pq.add(new double[]{startVertex, 0.0});

        while (!pq.isEmpty()) {
            double[] current = pq.poll();
            int u = (int) current[0];

            if (!visited.add(u)) {
                continue;
            }

            for (Graph.Edge edge : graph.getNeighbors(u)) {
                int v = edge.dest;
                if (!visited.contains(v)) {
                    double newDist = distances.get(u) + edge.weight;
                    if (newDist < distances.get(v)) {
                        distances.put(v, newDist);
                        parents.put(v, u);
                        pq.add(new double[]{v, newDist});
                    }
                }
            }
        }

        return new Result(distances, parents);
    }
}
