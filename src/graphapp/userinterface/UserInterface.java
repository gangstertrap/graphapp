package graphapp.userinterface;

import graphapp.constants.ToolMode;
import graphapp.graphtheory.Edge;
import graphapp.graphtheory.Vertex;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.util.HashMap;

public class UserInterface
{
    private final Stage stage;
    private final BorderPane borderPane;
    private final Pane pane;
    private UIEventListener controller;
    private final HashMap<Vertex, VertexLabel> vertices;
    private final HashMap<Edge, EdgeGroup> edges;


    private MenuItem deleteEdit;

    // add data structure for edges

    public UserInterface(Stage stage)
    {
        this.stage = stage;
        this.borderPane = new BorderPane();
        vertices = new HashMap<>();
        edges = new HashMap<>();
        pane = new Pane();

        borderPane.setCenter(pane);

        initTopBar();
        initBottomInfoBar();

        Scene scene = new Scene(borderPane, 800, 600);
        stage.setScene(scene);
        stage.show();
        scene.setOnKeyPressed(event -> controller.onKeyPressed(event));
        pane.setOnMouseClicked(event -> controller.onMouseClicked(event));
        pane.setOnMousePressed(event -> controller.onMousePressed(event));
        pane.setOnMouseReleased(event -> controller.onMouseReleased(event));
        pane.setOnMouseDragged(event -> controller.onMouseDragged(event));
    }

    private void initTopBar()
    {
        VBox vbox = new VBox();
        initMenuBar(vbox);
        initTopToolBar(vbox);
        borderPane.setTop(vbox);
    }

    private void initMenuBar(VBox vbox)
    {
        MenuBar topMenuBar = new MenuBar();
        topMenuBar.setPrefHeight(20);
        topMenuBar.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY.brighter(),null,null)));

        Border borderTop = new Border(new BorderStroke(null, null, Color.rgb(200,200,200), null, null, null, BorderStrokeStyle.SOLID, null, null, null, null));
        topMenuBar.setBorder(borderTop);

        Menu fileMenu = new Menu("File");
        Menu editMenu = new Menu("Edit");
        topMenuBar.getMenus().add(fileMenu);
        topMenuBar.getMenus().add(editMenu);

        MenuItem newFile = new MenuItem("New");
        MenuItem openFile = new MenuItem("Open");
        MenuItem saveFile = new MenuItem("Save");

        deleteEdit = new MenuItem("Delete");
        MenuItem undoEdit = new MenuItem("Undo");
        MenuItem redoEdit = new MenuItem("Redo");
        MenuItem copyEdit = new MenuItem("Copy");
        MenuItem pasteEdit = new MenuItem("Paste");
        deleteEdit.setDisable(true);

        deleteEdit.setOnAction(actionEvent -> controller.onMenuDeleteClicked(actionEvent));

        fileMenu.getItems().addAll(newFile, openFile, saveFile);
        editMenu.getItems().addAll(deleteEdit, undoEdit, redoEdit, copyEdit, pasteEdit);

        vbox.getChildren().add(topMenuBar);
    }

    private void initTopToolBar(VBox vbox)
    {
        ToolBar topToolBar = new ToolBar();

        topToolBar.setPrefHeight(25);
        topToolBar.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY.brighter(),null,null)));
        Border border = new Border(new BorderStroke(
                null, null, Color.rgb(200,200,200), null,
                null, null, BorderStrokeStyle.SOLID, null,
                null, null, null));
        topToolBar.setBorder(border);

        ToggleGroup tgroup = new ToggleGroup();
        RadioButton rb1 = new RadioButton("Select");
        RadioButton rb2 = new RadioButton("Move");
        RadioButton rb3 = new RadioButton("New Vertex");
        RadioButton rb4 = new RadioButton("New Edge");
        rb1.setUserData(ToolMode.SELECT);
        rb2.setUserData(ToolMode.MOVE);
        rb3.setUserData(ToolMode.NEW_VERTEX);
        rb4.setUserData(ToolMode.NEW_EDGE);

        rb1.setToggleGroup(tgroup);
        rb2.setToggleGroup(tgroup);
        rb3.setToggleGroup(tgroup);
        rb4.setToggleGroup(tgroup);
        rb1.setSelected(true);
        topToolBar.getItems().addAll(rb1, rb2, rb3, rb4);

        tgroup.selectedToggleProperty().addListener(
                (observableValue, oldToggle, newToggle) ->
                        controller.onModeChange((ToolMode)tgroup.getSelectedToggle().getUserData()));

        vbox.getChildren().add(topToolBar);
    }

    private void initBottomInfoBar()
    {
        ToolBar bottomToolBar = new ToolBar();

        bottomToolBar.setPrefHeight(20);

        bottomToolBar.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY.brighter(),null,null)));
        Border borderBottom = new Border(new BorderStroke(
                Color.rgb(200,200,200), null, null, null,
                BorderStrokeStyle.SOLID, null, null, null,
                null, null, null));

        bottomToolBar.setBorder(borderBottom);

        borderPane.setBottom(bottomToolBar);
    }

    public void addNewVertexLabel(Vertex v)
    {
        VertexLabel vl = new VertexLabel(v.getX(), v.getY(), v);
        addListenersToVertexLabel(vl);
        pane.getChildren().add(vl);
        vertices.put(v, vl);
    }

    public void updateVertexLabel(Vertex v)
    {
        VertexLabel label = vertices.get(v);
        double x = v.getX();
        double y = v.getY();
        label.setLayoutX(x);
        label.setLayoutY(y);

        for(Edge e : v.getEdges())
        {
            if(v.equals(e.getVertex1()))
            {
                edges.get(e).setLineStartPoint(x + VertexLabel.RADIUS, y + VertexLabel.RADIUS);
            }
            if(v.equals(e.getVertex2()))
            {
                edges.get(e).setLineEndPoint(x + VertexLabel.RADIUS, y + VertexLabel.RADIUS);
            }
        }
    }

    public void addNewEdge(Edge e)
    {
        VertexLabel vl1 = vertices.get(e.getVertex1());
        VertexLabel vl2 = vertices.get(e.getVertex2());

        Line line = new Line(vl1.getLayoutX() + VertexLabel.RADIUS, vl1.getLayoutY() + VertexLabel.RADIUS,
                vl2.getLayoutX() + VertexLabel.RADIUS, vl2.getLayoutY() + VertexLabel.RADIUS);

        EdgeGroup edge = new EdgeGroup(e, line);
        edge.setOnMouseClicked(event -> controller.onMouseClicked(event));
        pane.getChildren().add(edge);
        edge.toBack();
        edges.put(e, edge);
    }

    private void addListenersToVertexLabel(VertexLabel vl)
    {
        vl.setOnMouseClicked(event -> controller.onMouseClicked(event));
        vl.setOnMousePressed(event -> controller.onMousePressed(event));
        vl.setOnMouseReleased(event -> controller.onMouseReleased(event));
        vl.setOnMouseDragged(event -> controller.onMouseDragged(event));
        vl.setOnMouseEntered(event -> controller.onVertexEntered(event));
        vl.setOnMouseExited(event -> controller.onVertexExited(event));
    }

    public void setController(UIEventListener controller)
    {
        this.controller = controller;
    }

    public void removeVertex(Vertex v) {
        pane.getChildren().remove(vertices.get(v));
        for(Edge e: v.getEdges()) {
            removeEdge(e);
        }
        vertices.remove(v);
    }

    public void removeEdge(Edge e) {
        pane.getChildren().remove(edges.get(e));
        edges.remove(e);
    }

    public void nodesAreSelected(boolean b) {
        deleteEdit.setDisable(!b);
    }
}
