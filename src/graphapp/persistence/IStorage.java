package graphapp.persistence;

import graphapp.graphtheory.Edge;
import graphapp.graphtheory.Vertex;

public interface IStorage
{
    void vertexUpdate(Vertex v);

    void edgeUpdate(Edge e);
}
