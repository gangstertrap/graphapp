package graphapp.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import graphapp.graphtheory.Edge;
import graphapp.graphtheory.Graph;
import graphapp.graphtheory.Vertex;
import javafx.util.Pair;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

public class LocalStorage
{
    private final Gson gson;

    public LocalStorage() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.enableComplexMapKeySerialization();
        gsonBuilder.setPrettyPrinting();
        gson = gsonBuilder.create();
    }
    public boolean saveGraph(Graph graph, String filename) {

        StoredGraph sgraph = new StoredGraph(graph);
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(gson.toJson(sgraph));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

     public static class StoredGraph {
        Map<Vertex, List<Pair<String, Integer>>> adjacencyList;
        String name;
        boolean weighted;
        boolean directed;
        int idTotal;

        public StoredGraph(Graph graph) {
            name = graph.getName();
            weighted = graph.isWeighted();
            directed = graph.isDirected();
            idTotal = graph.getIdTotal();
            adjacencyList = new HashMap<>();
            for(Vertex v : graph.getVertices().values()) {
                List<Pair<String,Integer>> connections = new ArrayList<>();
                for(Edge e : v.getEdges()) {
                    if(e.getVertex1().equals(v)) {
                        connections.add(new Pair<String,Integer>(e.getVertex2().getId(), e.getWeight()));
                    }
                }
                adjacencyList.put(v, connections);
            }
        }
    }

    public Graph loadGraph(String filename) {
        StoredGraph sgraph;
        try (Reader reader = new FileReader(filename)) {
            sgraph = gson.fromJson(reader, LocalStorage.StoredGraph.class);
        }catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        System.out.println("adjacencyList:" + sgraph.adjacencyList);
        Graph graph = new Graph();
            for(Vertex v : sgraph.adjacencyList.keySet()) {
                graph.getVertices().put(v.getId(), v);
            }
            for(Vertex v : sgraph.adjacencyList.keySet()) {
                for(Pair<String, Integer> pair : sgraph.adjacencyList.get(v)) {
                    Edge e = new Edge(v, graph.getVertices().get(pair.getKey()), pair.getValue());
                    graph.getEdges().add(e);
                }
            }
            for(Edge e : graph.getEdges()) {
                graph.getVertices().get(e.getVertex1().getId()).getEdges().add(e);
                graph.getVertices().get(e.getVertex2().getId()).getEdges().add(e);
            }
        graph.setName(sgraph.name);
        graph.setWeighted(sgraph.weighted);
        graph.setIdTotal(sgraph.idTotal);
        graph.setDirected(sgraph.directed);
        graph.setURI(filename);
        return graph;
    }


}