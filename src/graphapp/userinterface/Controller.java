package graphapp.userinterface;

import graphapp.constants.ToolMode;
import graphapp.graphtheory.Edge;
import graphapp.graphtheory.Graph;
import graphapp.graphtheory.Vertex;
import javafx.event.ActionEvent;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.HashSet;
import java.util.Set;

/* Possibly merge onPaneClicked and onVertexClicked into onMouseClicked and just add the same listener to everything?
 * Not sure how well that would work
 */
public class Controller implements UIEventListener {
    private Graph graph;
    private UserInterface ui;
    private ToolMode currentMode = ToolMode.SELECT;

    private VertexLabel newEdgeFirstVertex = null;

    private double vertexDeltaX = 0;
    private double vertexDeltaY = 0;

    private Set<Node> selectedNodes;

    public Controller(Graph graph, UserInterface ui) {
        this.graph = graph;
        this.ui = ui;
        selectedNodes = new HashSet<>();
    }

    @Override
    public void onKeyPressed(KeyEvent event) {

        if(event.getCode().equals(KeyCode.DELETE) && !selectedNodes.isEmpty()) {
            System.out.println(event.getCode());
            deleteSelected();
        }
    }

    private void deleteSelected()
    {
        for(Node n : selectedNodes)
        {
            if(n instanceof VertexLabel) {
                System.out.println("is vl");
                VertexLabel label = (VertexLabel)n;
                Vertex v = label.getVertex();
                graph.removeVertex(v);
                ui.removeVertex(v);
            }
            if(n instanceof EdgeGroup) {
                System.out.println("is ed");
                EdgeGroup eg = (EdgeGroup)n;
                Edge e = eg.getEdge();
                if(graph.getEdges().contains(e))
                    graph.removeEdge(e);
                ui.removeEdge(e);
            }
        }
        ui.nodesAreSelected(false);
    }


    @Override
    public void onMouseClicked(MouseEvent event) {
        switch (currentMode) {
            case SELECT:
                if(event.getSource() instanceof VertexLabel || event.getSource() instanceof EdgeGroup) {
                    event.consume();

                    if(event.isShiftDown()) {
                        setNodeSelected((Node)event.getSource(), true);
                    } else if(selectedNodes.isEmpty()) {
                        setNodeSelected((Node)event.getSource(), true);
                    } else {
                        for(Node n : selectedNodes) {
                            setNodeSelected(n, false);
                        }
                        selectedNodes = new HashSet<>();
                        setNodeSelected((Node)event.getSource(), true);
                    }
                } else {
                    for(Node n : selectedNodes) {
                        setNodeSelected(n, false);
                    }
                    selectedNodes = new HashSet<>();
                    ui.nodesAreSelected(false);
                }
                break;
            case MOVE:
                break;
            case NEW_VERTEX:
                if(!(event.getSource() instanceof VertexLabel)) {
                    System.out.println(event.getSource());
                    Vertex v = graph.addVertex(event.getX(), event.getY());
                    ui.addNewVertexLabel(v);
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
            ui.nodesAreSelected(true);
        }
    }

    public void onMousePressed(MouseEvent event) {
        if (currentMode == ToolMode.MOVE && event.getSource() instanceof VertexLabel) {
            VertexLabel label = (VertexLabel) event.getSource();
            label.getScene().setCursor(Cursor.MOVE);
            vertexDeltaX = label.getLayoutX() - event.getSceneX();
            vertexDeltaY = label.getLayoutY() - event.getSceneY();

            event.consume();
        }
    }

    public void onMouseReleased(MouseEvent event) {
        if (event.getSource() instanceof VertexLabel && currentMode == ToolMode.MOVE) {
            VertexLabel label = (VertexLabel) event.getSource();
            if (!event.isPrimaryButtonDown()) {
                label.getScene().setCursor(Cursor.HAND);
            }
            event.consume();
        }
    }

    public void onMouseDragged(MouseEvent event) {
        if (currentMode == ToolMode.MOVE && event.getSource() instanceof VertexLabel) {
            VertexLabel label = (VertexLabel) event.getSource();
            Vertex v = label.getVertex();
            graph.updateVertexPosition(v, event.getSceneX() + vertexDeltaX, event.getSceneY() + vertexDeltaY);
            ui.updateVertexLabel(v);

            event.consume();
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
            for(Node n : selectedNodes) {
                setNodeSelected(n, false);
            }
            selectedNodes = new HashSet<>();
        }
    }

    @Override
    public void onMenuDeleteClicked(ActionEvent event) {
        if(!selectedNodes.isEmpty())
        {
            deleteSelected();
        }
    }
}
