package graphapp.userinterface;

import graphapp.constants.AppColors;
import graphapp.graphtheory.Edge;
import javafx.beans.InvalidationListener;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.Arrays;

public class EdgeGroup extends Group {
    private final Edge edge;
    private final Line line;
    private final Label label;
    private final Line arrow1;
    private final Line arrow2;

    private static final double arrowLength = 10;
    private static final double arrowWidth = 7;
    private static final double w = 6.46875;
    private static final double h = 17.0;

    public EdgeGroup(Edge e, Line ln, boolean visible, boolean directed) {
        this.edge = e;
        this.line = ln;
        arrow1 = new Line();
        arrow2 = new Line();
        label = new Label(edge.getWeight() + "");
        arrow1.setStrokeWidth(1);
        arrow2.setStrokeWidth(1);
        arrow1.setStroke(Color.BLACK);
        arrow2.setStroke(Color.BLACK);
        arrow1.setVisible(directed);
        arrow2.setVisible(directed);

        line.setStrokeWidth(1);
        line.setStroke(Color.BLACK);
        line.toBack();
        label.setVisible(visible);
        this.getChildren().addAll(line, label, arrow1, arrow2);

        double[] offset = getLabelOffset(true);
        label.setLayoutX((line.getStartX() + line.getEndX()) * 0.5 - w/2 + offset[0]);
        label.setLayoutY((line.getStartY() + line.getEndY()) * 0.5 - h/2 + offset[1]);
        updateArrowhead();
    }

    private void updateArrowhead() {
        double ex = line.getEndX();
        double ey = line.getEndY();
        double sx = line.getStartX();
        double sy = line.getStartY();
        if(ex - sx == 0) {
            if(ey > sy) {
                ey -= VertexLabel.RADIUS;
            }
            else {
                ey += VertexLabel.RADIUS;
            }
        }
        else {
            System.out.println("oop");
            double[] vector = {line.getEndX() - line.getStartX(), line.getEndY() - line.getStartY()};
            double magnitude = Math.sqrt(Math.pow(vector[0], 2) + Math.pow(vector[1], 2));
            vector[0] *= (1 - VertexLabel.RADIUS/magnitude);
            vector[1] *= (1 - VertexLabel.RADIUS/magnitude);
            ex = line.getStartX() + vector[0];
            ey= line.getStartY() + vector[1];
        }
        arrow1.setEndX(ex);
        arrow1.setEndY(ey);
        arrow2.setEndX(ex);
        arrow2.setEndY(ey);

        if (ex == sx && ey == sy) {
            // arrow parts of length 0
            arrow1.setStartX(ex);
            arrow1.setStartY(ey);
            arrow2.setStartX(ex);
            arrow2.setStartY(ey);
        } else {
            double hypot = Math.hypot(sx - ex, sy - ey);
            double factor = arrowLength / hypot;
            double factorO = arrowWidth / hypot;

            // part in direction of main line
            double dx = (sx - ex) * factor;
            double dy = (sy - ey) * factor;

            // part ortogonal to main line
            double ox = (sx - ex) * factorO;
            double oy = (sy - ey) * factorO;

            arrow1.setStartX(ex + dx - oy);
            arrow1.setStartY(ey + dy + ox);
            arrow2.setStartX(ex + dx + oy);
            arrow2.setStartY(ey + dy - ox);
        }
    }

    public void refreshLabelOffset()
    {
        double[] offset = getLabelOffset(true);
        double w = 6.4875 * ("" + edge.getWeight()).length();
        label.setLayoutX((line.getStartX() + line.getEndX()) * 0.5 - w/2.0 + offset[0]);
        label.setLayoutY((line.getStartY() + line.getEndY()) * 0.5 - 17.0/2.0 + offset[1]);
    }

    private double[] getLabelOffset(boolean bullshit)
    {
        double x = getLineEndX() - getLineStartX();
        double y = getLineEndY() - getLineStartY();
        double magnitude = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        if(bullshit) {
            double w = 6.4875 * ("" + edge.getWeight()).length();
            return new double[]{(y / magnitude) * w * -0.8, (x / magnitude) * 17.0 * 0.8};
        }
        return new double[]{(y / magnitude) * label.getWidth() * -0.8, (x / magnitude) * label.getHeight() * 0.8};
    }

    public void setLineStartPoint(double x, double y)
    {
        line.setStartX(x);
        line.setStartY(y);

        double[] offset = getLabelOffset(false);
        label.setLayoutX((line.getStartX() + line.getEndX()) * 0.5 - label.getWidth() / 2.0 + offset[0]);
        label.setLayoutY((line.getStartY() + line.getEndY()) * 0.5 - label.getHeight() / 2.0 + offset[1]);

        updateArrowhead();
    }

    public void setLineEndPoint(double x, double y)
    {
        line.setEndX(x);
        line.setEndY(y);
        double[] offset = getLabelOffset(false);
        label.setLayoutX((line.getStartX() + line.getEndX()) * 0.5 - label.getWidth()/2.0 + offset[0]);
        label.setLayoutY((line.getStartY() + line.getEndY()) * 0.5 - label.getHeight()/2.0 + offset[1]);
        updateArrowhead();
    }

    public Point2D getLineStart() {
        return new Point2D(getLineStartX(), getLineStartY());
    }

    public Point2D getLineEnd() {
        return new Point2D(getLineEndX(), getLineEndY());
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
        arrow1.setStroke(b ? AppColors.SELECTED_COLOR : Color.BLACK);
        arrow2.setStroke(b ? AppColors.SELECTED_COLOR : Color.BLACK);
    }

    public Edge getEdge() {
        return edge;
    }

    public void updateWeightLabel(int newWeight) {
        if(newWeight != this.edge.getWeight())
            System.out.println("VERY BAD???");
        label.setText(newWeight + "");
        refreshLabelOffset();
    }

    public void setWeightVisible(boolean b)
    {
        label.setVisible(b);
    }

    public void setDirectedVisible(boolean showDirected) {
        arrow1.setVisible(showDirected);
        arrow2.setVisible(showDirected);
    }

    public void switchDirection() {
        double tempX = line.getStartX();
        double tempY = line.getStartY();
        this.setLineStartPoint(line.getEndX(), line.getEndY());
        this.setLineEndPoint(tempX,tempY);
    }
}