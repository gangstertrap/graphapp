package graphapp.userinterface;

import graphapp.graphtheory.Vertex;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;

public class VertexLabel extends Label
{
    public static final int RADIUS = 15;
    private static final Color CIRCLE_COLOR = Color.rgb(240, 210, 190);
    private static final Color SELECTED_COLOR = Color.rgb(75, 210, 255);

    private final Vertex vertex;

    private final Circle circle;

    private boolean selected;

    public VertexLabel(double x, double y, Vertex vertex)
    {
        super(vertex.getId());
        setLayoutX(x - RADIUS);
        setLayoutY(y - RADIUS);
        this.vertex = vertex;

        selected = false;

        circle = new Circle(x, y, RADIUS, CIRCLE_COLOR);
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(1);

        setGraphic(circle);
        setContentDisplay(ContentDisplay.CENTER);

    }

    public Vertex getVertex()
    {
        return vertex;
    }

    public void setSelected(boolean bool)
    {
        this.selected = bool;
        circle.setStroke(bool ? SELECTED_COLOR : Color.BLACK);
    }


}