package graphapp.userinterface;

import graphapp.constants.AppColors;
import graphapp.graphtheory.Edge;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class EdgeGroup extends Group {
    private Edge edge;
    private Line line;
    private Label label;

    static boolean weightsVisible = false;

    public EdgeGroup(Edge e, Line ln) {
        this.edge = e;
        this.line = ln;

        line.setStrokeWidth(1);
        line.setStroke(Color.BLACK);
        line.toBack();
        this.getChildren().add(line);

        label = new Label(edge.getWeight() + "");
        label.setLayoutX((line.getStartX() + line.getEndX()) * 0.5);
        label.setLayoutY((line.getStartY() + line.getEndY()) * 0.5);
        label.setVisible(weightsVisible);
        this.getChildren().add(label);
    }

    public void setLineStartPoint(double x, double y)
    {
        line.setStartX(x);
        line.setStartY(y);
        label.setLayoutX((line.getStartX() + line.getEndX()) * 0.5);
        label.setLayoutY((line.getStartY() + line.getEndY()) * 0.5);
    }

    public void setLineEndPoint(double x, double y)
    {
        line.setEndX(x);
        line.setEndY(y);
        label.setLayoutX((line.getStartX() + line.getEndX()) * 0.5);
        label.setLayoutY((line.getStartY() + line.getEndY()) * 0.5);
    }

    public double getLineStartX() {
        return line.getStartX();
    }

    public double getLineStartY() {
        return line.getStartY();
    }

    public double getLineEndX() {
        return line.getEndX();
    }

    public double getLineEndY() {
        return line.getEndY();
    }

    public void setSelected(boolean b) {
        line.setStroke(b ? AppColors.SELECTED_COLOR : Color.BLACK);
    }

    public Edge getEdge() {
        return edge;
    }
}
