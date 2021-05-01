package graphapp.userinterface;

import graphapp.constants.ToolMode;
import graphapp.graphtheory.Edge;
import graphapp.graphtheory.Graph;
import graphapp.graphtheory.Vertex;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;

/* Possibly merge onPaneClicked and onVertexClicked into onMouseClicked and just add the same listener to everything?
* Not sure how well that would work
*/
public class Controller implements UIEventListener
{
    private Graph graph;
    private UserInterface ui;
    private ToolMode currentMode = ToolMode.SELECT;
    private VertexLabel newEdgeFirstVertex = null;
    private double vertexDeltaX = 0;
    private double vertexDeltaY = 0;

    public Controller(Graph graph, UserInterface ui)
    {
        this.graph = graph;
        this.ui = ui;
    }

    @Override
    public void onPaneClicked(MouseEvent event)
    {
        switch(currentMode)
        {
            case SELECT:
                break;
            case NEW_VERTEX:
                Vertex v = graph.addVertex(event.getX(), event.getY());
                ui.addNewVertexLabel(v);
                break;

        }
    }

    @Override
    public void onVertexClicked(MouseEvent event)
    {
        switch(currentMode)
        {
            case NEW_EDGE:
                System.out.println("in edge mode");
                if (event.getSource() instanceof VertexLabel && newEdgeFirstVertex == null)
                {
                    System.out.println("choosing first vertex");
                    newEdgeFirstVertex = (VertexLabel)event.getSource();
                    newEdgeFirstVertex.setSelected(true);
                }
                else if (event.getSource() instanceof VertexLabel && !newEdgeFirstVertex.equals(event.getSource()))
                {
                    System.out.println("choosing second vertex");
                    Edge e = graph.addEdgeOn(newEdgeFirstVertex.getVertex(), ((VertexLabel)event.getSource()).getVertex(), 0);
                    ui.addNewEdge(e);
                    newEdgeFirstVertex.setSelected(false);
                    newEdgeFirstVertex = null;
                }
                else
                {
                    System.out.println("clearing first vertex in onVertexClicked");
                    newEdgeFirstVertex.setSelected(false);
                    newEdgeFirstVertex = null;
                }
                break;
        }
    }

    @Override
    public void onVertexPressed(MouseEvent event)
    {
        if(currentMode == ToolMode.MOVE && event.getSource() instanceof VertexLabel)
        {
            VertexLabel label = (VertexLabel)event.getSource();
            label.getScene().setCursor(Cursor.MOVE);
            vertexDeltaX = label.getLayoutX() - event.getSceneX();
            vertexDeltaY = label.getLayoutY() - event.getSceneY();
        }
    }

    @Override
    public void onVertexReleased(MouseEvent event)
    {
        if(event.getSource() instanceof VertexLabel && currentMode == ToolMode.MOVE)
        {
            VertexLabel label = (VertexLabel)event.getSource();
            if (!event.isPrimaryButtonDown())
            {
                label.getScene().setCursor(Cursor.HAND);
            }
        }
    }

    @Override
    public void onVertexDragged(MouseEvent event)
    {
        if(currentMode == ToolMode.MOVE && event.getSource() instanceof VertexLabel)
        {
            VertexLabel label = (VertexLabel)event.getSource();
            Vertex v = label.getVertex();
            graph.updateVertexPosition(v, event.getSceneX() + vertexDeltaX, event.getSceneY() + vertexDeltaY);
            ui.updateVertexLabel(v);

        }
    }

    @Override
    public void onVertexEntered(MouseEvent event)
    {
        if(event.getSource() instanceof VertexLabel && currentMode == ToolMode.MOVE)
        {
            VertexLabel label = (VertexLabel)event.getSource();
            if (!event.isPrimaryButtonDown())
            {
                label.getScene().setCursor(Cursor.MOVE);
            }
        }
    }

    @Override
    public void onVertexExited(MouseEvent event)
    {
        if(event.getSource() instanceof VertexLabel && currentMode == ToolMode.MOVE)
        {
            VertexLabel label = (VertexLabel)event.getSource();
            if (!event.isPrimaryButtonDown())
            {
                label.getScene().setCursor(Cursor.DEFAULT);
            }
        }
    }

    @Override
    public void onModeChange(ToolMode mode)
    {
        currentMode = mode;
        newEdgeFirstVertex = null;
    }

}
