package graphapp.userinterface;

import graphapp.constants.ToolMode;
import graphapp.graphtheory.Edge;
import graphapp.graphtheory.Graph;
import graphapp.graphtheory.Vertex;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import javafx.geometry.Point2D;
import java.util.HashSet;
import java.util.Set;

public class Controller implements UIEventListener {
    private Graph graph;
    private UserInterface ui;
    private ToolMode currentMode = ToolMode.SELECT;

    private Point2D selectRectStart = Point2D.ZERO;

    private boolean selectRectEnabled = false;

    private VertexLabel newEdgeFirstVertex = null;


    private double vertexDeltaX = 0;
    private double vertexDeltaY = 0;

    private Set<Node> selectedNodes;
    private Point2D panStart;

    public Controller(Graph graph, UserInterface ui) {
        this.graph = graph;
        this.ui = ui;
        selectedNodes = new HashSet<>();
    }

    @Override
    public void onKeyPressed(KeyEvent event) {
        switch(event.getCode())
        {
            case UP:
                ui.translateGroup(ui.getGroupTranslateX(), ui.getGroupTranslateY()+20);
                break;
            case DOWN:
                ui.translateGroup(ui.getGroupTranslateX(), ui.getGroupTranslateY()-20);
                break;
            case LEFT:
                ui.translateGroup(ui.getGroupTranslateX()+20, ui.getGroupTranslateY());
                break;
            case RIGHT:
                ui.translateGroup(ui.getGroupTranslateX()-20, ui.getGroupTranslateY());
                break;
            case DELETE:
                if(!selectedNodes.isEmpty())
                    deleteSelected();
                break;
            case EQUALS:
                if(event.isShiftDown())
                    ui.incrementScale();
                break;
            case MINUS:
                if(event.isShiftDown())
                    ui.decrementScale();
                break;
        }
    }

    @Override
    public void onKeyTyped(KeyEvent event) {

        if( (event.getCode().equals(KeyCode.ENTER)
                || event.getCharacter().getBytes()[0] == '\n'
                || event.getCharacter().getBytes()[0] == '\r')
                && selectedNodes.size() == 1) {
            VertexLabel vl = (VertexLabel)(selectedNodes.toArray()[0]);
            renameVertex(vl);
        }
    }

    @Override
    public void onMenuItemClicked(ActionEvent actionEvent) {
        switch(((MenuItem)actionEvent.getSource()).getText()) {
            case "Delete":
                if(!selectedNodes.isEmpty())
                    deleteSelected();
                break;
            case "Rename":
                renameVertex( (VertexLabel) (selectedNodes.toArray()[0]));
                break;
            case "Zoom In":
                ui.incrementScale();
                break;
            case "Zoom Out":
                ui.decrementScale();
                break;
            case "Reset Zoom":
                ui.resetScale();
                break;
            case "Recenter":
                ui.resetGroupTranslate();
                break;
        }
    }

    private void deleteSelected()
    {
        for(Node n : selectedNodes)
        {
            if(n instanceof VertexLabel) {
                VertexLabel label = (VertexLabel)n;
                Vertex v = label.getVertex();
                graph.removeVertex(v);
                ui.removeVertex(v);
            }
            if(n instanceof EdgeGroup) {
                EdgeGroup eg = (EdgeGroup)n;
                Edge e = eg.getEdge();
                if(graph.getEdges().contains(e))
                    graph.removeEdge(e);
                ui.removeEdge(e);
            }
        }
    }


    @Override
    public void onMouseClicked(MouseEvent event) {
        //System.out.println("onMouseClicked: " + event.getSource());
        if(event.getButton() == MouseButton.PRIMARY) {
            switch (currentMode) {
                case SELECT:
                    if (event.getSource() instanceof VertexLabel || event.getSource() instanceof EdgeGroup) {
                        event.consume();

                        if (event.isShiftDown()) {
                            setNodeSelected((Node) event.getSource(), true);
                        } else if (selectedNodes.isEmpty()) {
                            setNodeSelected((Node) event.getSource(), true);
                        } else {
                            clearSelectedNodes();
                            setNodeSelected((Node) event.getSource(), true);
                        }
                    } else if (!selectRectEnabled) {
                        clearSelectedNodes();
                    } else {
                        selectRectEnabled = false;
                    }
                    break;
                case MOVE:
                    if(!(event.getSource() instanceof VertexLabel)) {
                        clearSelectedNodes();
                    } else{
                        event.consume();
                    }
                    break;
                case NEW_VERTEX:
                    if (!(event.getSource() instanceof VertexLabel)) {
                        Point2D newPoint = ui.groupParentToLocal(new Point2D(event.getX(), event.getY()));
                        Vertex v = graph.addVertex(newPoint.getX(), newPoint.getY());
                        VertexLabel vl = ui.addNewVertexLabel(v);
                    }
                    break;
                case NEW_EDGE:
                    if (event.getSource() instanceof VertexLabel) {
                        event.consume();
                        if (newEdgeFirstVertex == null) {
                            newEdgeFirstVertex = (VertexLabel) event.getSource();
                            newEdgeFirstVertex.setSelected(true);
                        } else if (!newEdgeFirstVertex.equals(event.getSource())) {
                            Edge e = graph.addEdgeOn(newEdgeFirstVertex.getVertex(), ((VertexLabel) event.getSource()).getVertex(), 0);
                            ui.addNewEdge(e);
                            deselectFirstVertex();
                        } else {
                            deselectFirstVertex();
                        }
                    } else {
                        deselectFirstVertex();
                    }
                    break;
            }
        }
    }
    private void deselectFirstVertex()
    {
        if (newEdgeFirstVertex != null)
            newEdgeFirstVertex.setSelected(false);
        newEdgeFirstVertex = null;
    }

    private void setNodeSelected(Node node, boolean isSelected)
    {
        if(node instanceof VertexLabel) {
            ((VertexLabel) node).setSelected(isSelected);
        }
        if(node instanceof EdgeGroup) {
            ((EdgeGroup) node).setSelected(isSelected);
        }
        if(isSelected) {
            selectedNodes.add(node);
        }
    }

    public void onMousePressed(MouseEvent event) {
        System.out.println("onMousePressed: " + event.getSource());
        if(event.getButton() == MouseButton.PRIMARY) {
            switch (currentMode) {
                case MOVE:
                    if (event.getSource() instanceof VertexLabel) {
                        VertexLabel label = (VertexLabel) event.getSource();
                        label.getScene().setCursor(Cursor.MOVE);
                        vertexDeltaX = label.getLayoutX() - event.getSceneX();
                        vertexDeltaY = label.getLayoutY() - event.getSceneY();
                        if (selectedNodes.size() < 2 || !selectedNodes.contains(event.getSource()))
                            clearSelectedNodes();
                        event.consume();
                    }
                    break;
                case SELECT:
                    //if(!(event.getSource() instanceof VertexLabel)) {
                        selectRectStart = new Point2D(event.getX(), event.getY());
                        if (!event.isShiftDown()) {
                            clearSelectedNodes();
                        }
                    //}
                    System.out.println(selectRectStart);
                    break;
                case PAN:
                    panStart = new Point2D((event.getScreenX() - ui.getGroupTranslateX()) * ui.getGroupScale(),
                            (event.getScreenY() - ui.getGroupTranslateY()) * ui.getGroupScale());
                    break;
            }
        }
    }

    public void onMouseReleased(MouseEvent event) {
        //System.out.println("onMouseReleased: " + event.getSource());
        if(event.getButton() == MouseButton.PRIMARY) {
            switch (currentMode) {
                case MOVE:
                    if (event.getSource() instanceof VertexLabel) {
                        VertexLabel label = (VertexLabel) event.getSource();
                        if (!event.isPrimaryButtonDown()) {
                            label.getScene().setCursor(Cursor.DEFAULT);
                        }
                        event.consume();
                    }
                    break;
                case SELECT:
                    Rectangle2D rect = new Rectangle2D(
                            Math.min(selectRectStart.getX(), event.getX()),
                            Math.min(selectRectStart.getY(), event.getY()),
                            Math.abs(event.getX() - selectRectStart.getX()),
                            Math.abs(event.getY() - selectRectStart.getY()));
                    for (VertexLabel vl : ui.getVertices()) {
                        Point2D point = new Point2D(vl.getLayoutX()+VertexLabel.RADIUS, vl.getLayoutY()+VertexLabel.RADIUS);
                        Point2D gL2P = ui.groupLocalToParent(point);
                        if (gL2P.getX() > rect.getMinX() - VertexLabel.RADIUS * ui.getGroupScale()
                                && gL2P.getX() < rect.getMaxX() + VertexLabel.RADIUS * ui.getGroupScale()
                                && gL2P.getY() > rect.getMinY() - VertexLabel.RADIUS * ui.getGroupScale()
                                && gL2P.getY() < rect.getMaxY() + VertexLabel.RADIUS * ui.getGroupScale()) {
                            setNodeSelected(vl, true);
                        }
                    }
                    for (EdgeGroup eg : ui.getEdges()) {
                        Point2D startGL2P = ui.groupLocalToParent(eg.getLineStart());
                        Point2D endGL2P = ui.groupLocalToParent(eg.getLineEnd());
                        if (rect.contains(startGL2P) || rect.contains(endGL2P) || lineIntersectRect(startGL2P, endGL2P, rect)) {
                            setNodeSelected(eg, true);
                        }
                    }
                    ui.updateSelectRect(null);
                    event.consume();
                    break;
            }
        }
    }

    public void onMouseDragged(MouseEvent event) {
        //System.out.println("onMouseDragged: " + event.getSource());
        if(event.getButton() == MouseButton.PRIMARY) {
            switch (currentMode) {
                case MOVE:
                    if (event.getSource() instanceof VertexLabel) {
                        VertexLabel label = (VertexLabel) event.getSource();
                        Vertex v = label.getVertex();
                        if (selectedNodes.size() > 1) {
                            double vx = v.getX();
                            double vy = v.getY();
                            for (Node n : selectedNodes) {
                                if (n instanceof VertexLabel) {
                                    Vertex vn = ((VertexLabel) n).getVertex();
                                    graph.updateVertexPos(vn,
                                            vn.getX() + (event.getSceneX() + vertexDeltaX - vx),
                                            vn.getY() + (event.getSceneY() + vertexDeltaY - vy));
                                    ui.updateVertexLabel(vn);
                                }
                            }
                        } else {

                            graph.updateVertexPos(v, event.getSceneX() + vertexDeltaX, event.getSceneY() + vertexDeltaY);
                            ui.updateVertexLabel(v);
                        }
                        event.consume();
                    }
                    break;
                case SELECT:
                    Rectangle2D rect = new Rectangle2D(
                            Math.min(selectRectStart.getX(), event.getX()),
                            Math.min(selectRectStart.getY(), event.getY()),
                            Math.abs(event.getX() - selectRectStart.getX()),
                            Math.abs(event.getY() - selectRectStart.getY()));
                    ui.updateSelectRect(rect);
                    selectRectEnabled = true;
                    break;
                case PAN:
                    ui.translateGroup(event.getScreenX() - panStart.getX(), event.getScreenY() - panStart.getY());
                    break;
            }
        }
    }

    @Override
    public void onVertexEntered(MouseEvent event) {
        if (event.getSource() instanceof VertexLabel && currentMode == ToolMode.MOVE) {
            VertexLabel label = (VertexLabel) event.getSource();
            if (!event.isPrimaryButtonDown()) {
                label.getScene().setCursor(Cursor.MOVE);
            }
        }
    }

    @Override
    public void onVertexExited(MouseEvent event) {
        if (event.getSource() instanceof VertexLabel && currentMode == ToolMode.MOVE) {
            VertexLabel label = (VertexLabel) event.getSource();
            if (!event.isPrimaryButtonDown()) {
                label.getScene().setCursor(Cursor.DEFAULT);
            }
        }
    }

    @Override
    public void onModeChange(ToolMode mode) {
        currentMode = mode;
        newEdgeFirstVertex = null;
        if(currentMode == ToolMode.NEW_VERTEX || currentMode == ToolMode.NEW_EDGE) {
            clearSelectedNodes();
        }
    }


    @Override
    public void onEditMenuShowing(Event event) {
        ui.deleteEditSetDisable(selectedNodes.isEmpty());
        ui.renameEditSetDisable(selectedNodes.size() != 1 || !(selectedNodes.toArray()[0] instanceof VertexLabel));
    }

    private void renameVertex(VertexLabel vl){
        String newId = ui.showRenameDialog(vl.getVertexId());
        boolean successful = graph.changeVertexId(vl.getVertex(), newId, false);
        if(successful)
            vl.setText("" + newId);
        else
            ui.showRenameErrorAlert();
    }

    private boolean lineIntersectRect(Point2D start, Point2D end, Rectangle2D rect) {
        Point2D tl = new Point2D(rect.getMinX(), rect.getMinY());
        Point2D tr = new Point2D(rect.getMaxX(), rect.getMinY());
        if(linesIntersect(start, end, tl, tr))
            return true;
        Point2D bl = new Point2D(rect.getMinX(), rect.getMaxY());
        if(linesIntersect(start, end, tl, bl))
            return true;
        Point2D br = new Point2D(rect.getMaxX(), rect.getMaxY());
        if(linesIntersect(start, end, tr, br))
            return true;
        return linesIntersect(start, end, bl, br);
    }

    private boolean linesIntersect(Point2D start1, Point2D end1, Point2D start2, Point2D end2) {
        return orientation(start1, end1, start2) != orientation(start1, end1, end2)
                && orientation(start2, end2, start1) != orientation(start2, end2 ,end1);
    }

    private int orientation(Point2D p, Point2D q, Point2D r) {
        int val = (int) ( (q.getY() - p.getY()) * (r.getX() - q.getX()) - (q.getX() - p.getX()) * (r.getY() - q.getY()) );
        if (val == 0) return 0;
        return (val > 0)? 1 : 2;
    }

    private void clearSelectedNodes()
    {
        for(Node n : selectedNodes) {
            setNodeSelected(n, false);
        }
        selectedNodes = new HashSet<>();
    }

}
