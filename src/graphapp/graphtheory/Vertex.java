package graphapp.graphtheory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Vertex
{

    private String id;
    private double x, y;
    private transient final Set<Edge> edges = new HashSet<>();

    public Vertex() {

    }
    protected Vertex(String id)
    {
        this.id = id;
        x = 0;
        y = 0;
        //edges = new HashSet<>();
    }

    protected Vertex(double x, double y, String id)
    {
        this.id = id;
        this.x = x;
        this.y = y;
        //edges = new HashSet<>();
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
