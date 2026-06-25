package sportsanalytics.co3;

import java.util.*;

public class Graph {
    public static class Edge {
        public int src;
        public int dest;
        public double weight;

        public Edge(int src, int dest, double weight) {
            this.src = src;
            this.dest = dest;
            this.weight = weight;
        }

        @Override
        public String toString() {
            return src + " -> " + dest + " (" + weight + ")";
        }
    }

    private final Map<Integer, List<Edge>> adjList;
    private final Map<Integer, String> vertexNames;
    private boolean directed;

    public Graph(boolean directed) {
        this.adjList = new HashMap<>();
        this.vertexNames = new HashMap<>();
        this.directed = directed;
    }

    public boolean isDirected() {
        return directed;
    }

    public void setDirected(boolean directed) {
        this.directed = directed;
    }

    public void addVertex(int v, String name) {
        adjList.putIfAbsent(v, new ArrayList<>());
        vertexNames.putIfAbsent(v, name);
    }

    public void addEdge(int src, int dest, double weight) {
        addVertex(src, String.valueOf(src));
        addVertex(dest, String.valueOf(dest));

        Edge edge = new Edge(src, dest, weight);
        adjList.get(src).add(edge);

        if (!directed) {
            Edge backEdge = new Edge(dest, src, weight);
            adjList.get(dest).add(backEdge);
        }
    }

    public List<Edge> getNeighbors(int v) {
        return adjList.getOrDefault(v, new ArrayList<>());
    }

    public Set<Integer> getVertices() {
        return adjList.keySet();
    }

    public String getVertexName(int v) {
        return vertexNames.getOrDefault(v, "Vertex " + v);
    }

    public void setVertexName(int v, String name) {
        vertexNames.put(v, name);
    }

    public List<Edge> getAllEdges() {
        List<Edge> allEdges = new ArrayList<>();
        Set<String> seenUndirected = new HashSet<>();
        
        for (int u : adjList.keySet()) {
            for (Edge e : adjList.get(u)) {
                if (directed) {
                    allEdges.add(e);
                } else {
                    String pair = Math.min(e.src, e.dest) + "-" + Math.max(e.src, e.dest);
                    if (!seenUndirected.contains(pair)) {
                        seenUndirected.add(pair);
                        allEdges.add(e);
                    }
                }
            }
        }
        return allEdges;
    }

    public int getVertexCount() {
        return adjList.size();
    }

    public void clear() {
        adjList.clear();
        vertexNames.clear();
    }
}
