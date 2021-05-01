package graphapp.graphtheory;

public class Edge
{
    final Vertex vertex1;
    final Vertex vertex2;
    int weight;

    protected Edge(Vertex vertex1, Vertex vertex2, int weight)
    {
        this.vertex1 = vertex1;
        this.vertex2 = vertex2;
        this.weight = weight;
    }

    public Vertex getVertex1()
    {
        return vertex1;
    }

    public Vertex getVertex2()
    {
        return vertex2;
    }

    public int getWeight()
    {
        return weight;
    }

    protected void setWeight(int weight)
    {
        this.weight = weight;
    }

    public boolean hasVertex(Vertex v)
    {
        return vertex1.equals(v) || vertex2.equals(v);
    }
}
