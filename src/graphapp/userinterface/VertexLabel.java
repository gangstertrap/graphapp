package graphapp.userinterface;

import graphapp.constants.AppColors;
import graphapp.graphtheory.Vertex;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class VertexLabel extends Label
{
    public static final int RADIUS = 15;

    private final Vertex vertex;

    private final Circle circle;

    private boolean selected;

    public VertexLabel(double x, double y, Vertex vertex)
    {
        super(vertex.getId() + "");
        setLayoutX(x - RADIUS);
        setLayoutY(y - RADIUS);
        this.vertex = vertex;

        selected = false;

        circle = new Circle(x, y, RADIUS, AppColors.CIRCLE_COLOR);
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(1);

        setGraphic(circle);
        setContentDisplay(ContentDisplay.CENTER);

    }

    public String getVertexId()
    {
        return vertex.getId();
    }

    public Vertex getVertex()
    {
        return vertex;
    }

    public void setSelected(boolean bool)
    {
        this.selected = bool;
        circle.setStroke(bool ? AppColors.SELECTED_COLOR : Color.BLACK);
    }

    public void updateId(String s) {
        if(!s.equals(vertex.getId()))
            System.out.println("VERY BAD???");
        this.setText(s);
    }
}