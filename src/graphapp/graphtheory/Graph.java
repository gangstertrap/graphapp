package graphapp.graphtheory;

import graphapp.persistence.IStorage;

import java.util.*;

public class Graph {
    private final Set<Edge> edges;
    private final Set<Vertex> vertices;
    boolean isWeighted;

    public Graph(IStorage storage) {
        edges = new HashSet<>();
        vertices = new HashSet<>();
    }

    public Set<Edge> getEdges() {
        return edges;
    }

    private Set<Vertex> getVertices() {
        return vertices;
    }

    public Vertex addVertex(double x, double y) {
        Vertex v = new Vertex(x, y);
        vertices.add(v);

        return v;
    }

    public Edge addEdgeOn(Vertex a, Vertex b, int weight) {
        Edge e = new Edge(a, b, weight);
        a.getEdges().add(e);
        b.getEdges().add(e);
        edges.add(e);

        return e;
    }

    public void removeEdge(Edge e) {
        e.getVertex1().getEdges().remove(e);
        e.getVertex2().getEdges().remove(e);
        edges.remove(e);
    }

    public void removeVertex(Vertex v) {
        for (Edge e : v.getEdges()) {
            edges.remove(e);
        }
        vertices.remove(v);
    }

    public void updateVertexPosition(Vertex v, double x, double y) {
        v.setX(x);
        v.setY(y);
    }
}
