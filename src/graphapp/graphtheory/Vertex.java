package graphapp.graphtheory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Vertex
{
    private static int totalId = 0;

    private String id;
    private double x, y;
    private final Set<Edge> edges;

    public Vertex()
    {
        id = nextId();
        x = 0;
        y = 0;
        edges = new HashSet<>();
    }

    public Vertex(double x, double y)
    {
        id = nextId();
        this.x = x;
        this.y = y;
        edges = new HashSet<>();
    }

    public static String nextId()
    {
        totalId++;
        return "" + totalId;
    }

    public String getId()
    {
        return id;
    }

    /**
     * DO NOT ALLOW DUPLICATE IDS!!! BE CAREFUL!!!
     **/
    protected void setId(String id)
    {
        this.id = id;
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    protected void setX(double x)
    {
        this.x = x;
    }

    protected void setY(double y)
    {
        this.y = y;
    }

    public Set<Edge> getEdges()
    {
        return edges;
    }
}
