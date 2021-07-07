package graphapp.userinterface;

import graphapp.constants.AppColors;
import graphapp.constants.ToolMode;
import graphapp.graphtheory.Edge;
import graphapp.graphtheory.Vertex;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.HashMap;

public class UserInterface
{
    private final BorderPane borderPane;
    //private final Pane pane;
    private final Group group;
    private UIEventListener controller;
    private final HashMap<Vertex, VertexLabel> vertices;
    private final HashMap<Edge, EdgeGroup> edges;

    private final Rectangle selectRectangle;

    private MenuItem deleteEdit;
    private MenuItem renameEdit;

    public UserInterface(Stage stage)
    {
        this.borderPane = new BorderPane();
        vertices = new HashMap<>();
        edges = new HashMap<>();
        Pane pane = new Pane();
        group = new Group();

        borderPane.setCenter(pane);
        pane.getChildren().add(group);

        initTopBar();
        initBottomInfoBar();

        selectRectangle = new Rectangle(0,0,0,0);
        pane.getChildren().add(selectRectangle);
        selectRectangle.setVisible(false);
        selectRectangle.setFill(Color.TRANSPARENT);
        selectRectangle.setStroke(AppColors.SELECTED_COLOR);
        selectRectangle.getStrokeDashArray().addAll(10d,10d);

        Scene scene = new Scene(borderPane, 800, 600);
        stage.setScene(scene);
        stage.show();
        //pane.setOnKeyPressed(event -> controller.onKeyPressed(event));

        pane.setOnKeyTyped(event -> controller.onKeyTyped(event));
        pane.setOnMouseClicked(event -> controller.onMouseClicked(event));
        pane.setOnMousePressed(event -> controller.onMousePressed(event));
        pane.setOnMouseReleased(event -> controller.onMouseReleased(event));
        pane.setOnMouseDragged(event -> controller.onMouseDragged(event));
        borderPane.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            keyEvent.consume();
            controller.onKeyPressed(keyEvent);
        });
        borderPane.setFocusTraversable(false);
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
        Menu viewMenu = new Menu("View");
        topMenuBar.getMenus().add(fileMenu);
        topMenuBar.getMenus().add(editMenu);
        topMenuBar.getMenus().add(viewMenu);

        MenuItem newFile = new MenuItem("New");
        MenuItem openFile = new MenuItem("Open");
        MenuItem saveFile = new MenuItem("Save");

        deleteEdit = new MenuItem("Delete");
        renameEdit = new MenuItem("Rename");
        MenuItem undoEdit = new MenuItem("Undo");
        MenuItem redoEdit = new MenuItem("Redo");
        MenuItem copyEdit = new MenuItem("Copy");
        MenuItem pasteEdit = new MenuItem("Paste");

        MenuItem recenter = new MenuItem("Recenter");
        MenuItem zoomIn = new MenuItem("Zoom In");
        MenuItem zoomOut = new MenuItem("Zoom Out");
        MenuItem zoomReset = new MenuItem("Reset Zoom");

        deleteEdit.setOnAction(actionEvent -> controller.onMenuItemClicked(actionEvent));
        renameEdit.setOnAction(actionEvent -> controller.onMenuItemClicked(actionEvent));
        recenter.setOnAction(actionEvent -> controller.onMenuItemClicked(actionEvent));
        zoomIn.setOnAction(actionEvent -> controller.onMenuItemClicked(actionEvent));
        zoomOut.setOnAction(actionEvent -> controller.onMenuItemClicked(actionEvent));
        zoomReset.setOnAction(actionEvent -> controller.onMenuItemClicked(actionEvent));

        editMenu.setOnShowing(event -> controller.onEditMenuShowing(event));

        deleteEdit.setDisable(true);
        renameEdit.setDisable(true);

        fileMenu.getItems().addAll(newFile, openFile, saveFile);
        editMenu.getItems().addAll(deleteEdit, renameEdit, undoEdit, redoEdit, copyEdit, pasteEdit);
        viewMenu.getItems().addAll(recenter, zoomIn, zoomOut, zoomReset);
        topMenuBar.setFocusTraversable(false);

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
        RadioButton rb5 = new RadioButton("Pan");
        rb1.setUserData(ToolMode.SELECT);
        rb2.setUserData(ToolMode.MOVE);
        rb3.setUserData(ToolMode.NEW_VERTEX);
        rb4.setUserData(ToolMode.NEW_EDGE);
        rb5.setUserData(ToolMode.PAN);

        rb1.setToggleGroup(tgroup);
        rb2.setToggleGroup(tgroup);
        rb3.setToggleGroup(tgroup);
        rb4.setToggleGroup(tgroup);
        rb5.setToggleGroup(tgroup);
        rb1.setSelected(true);
        topToolBar.getItems().addAll(rb1, rb2, rb3, rb4, rb5);

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

    public VertexLabel addNewVertexLabel(Vertex v)
    {
        VertexLabel vl = new VertexLabel(v.getX(), v.getY(), v);
        addListenersToVertexLabel(vl);
        group.getChildren().add(vl);
        vertices.put(v, vl);
        return vl;
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
        group.getChildren().add(edge);
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
        group.getChildren().remove(vertices.get(v));
        for(Edge e: v.getEdges()) {
            removeEdge(e);
        }
        vertices.remove(v);
    }

    public void removeEdge(Edge e) {
        group.getChildren().remove(edges.get(e));
        edges.remove(e);
    }

    public void deleteEditSetDisable(boolean b) {
        deleteEdit.setDisable(b);
    }

    public void renameEditSetDisable(boolean b) {
        renameEdit.setDisable(b);
    }

    public void updateSelectRect(Rectangle2D rect) {
        if(rect != null) {
            selectRectangle.setX(rect.getMinX());
            selectRectangle.setY(rect.getMinY());
            selectRectangle.setWidth(rect.getWidth());
            selectRectangle.setHeight(rect.getHeight());
            selectRectangle.setVisible(true);
        }
        else
            selectRectangle.setVisible(false);
    }

    public VertexLabel[] getVertices()
    {
        return vertices.values().toArray(new VertexLabel[0]);
    }

    public EdgeGroup[] getEdges() {
        return edges.values().toArray(new EdgeGroup[0]);
    }

    public String showRenameDialog(String oldId) {
        TextInputDialog dialog = new TextInputDialog(oldId);
        TextField field = dialog.getEditor();
        TextFormatter<String> formatter = new TextFormatter<>(change -> {
            if (!change.getText().matches("\\d+")) {
                change.setText("");
                /*change.setRange(
                        change.getRangeStart(),
                        change.getRangeStart()
                );*/
            }
            return change;
        });
        field.setTextFormatter(formatter);
        dialog.setTitle("Rename Vertex");
        dialog.setGraphic(null);
        dialog.setHeaderText("");
        dialog.setContentText("Input must be an integer.");
        dialog.showAndWait();

        try {
            int i = Integer.parseInt(dialog.getResult());
        } catch(NumberFormatException e) {
            return oldId;
        }
        return dialog.getResult();
    }

    public void showRenameErrorAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error in renaming vertex");
        alert.setGraphic(null);
        alert.setHeaderText("");
        alert.setContentText("Unable to rename vertex:\nA vertex with that label already exists.");
        alert.show();
    }


    public double getGroupTranslateX()
    {
        return group.getTranslateX();
    }

    public double getGroupTranslateY()
    {
        return group.getTranslateY();
    }

    public void translateGroup(double x, double y)
    {
        group.setTranslateX(x);
        group.setTranslateY(y);
    }

    public void incrementScale()
    {
        if(group.getScaleX() < 2.25) {
            group.setScaleX(group.getScaleX() + 0.25);
            group.setScaleY(group.getScaleY() + 0.25);
        }
    }
    public void decrementScale()
    {
        if(group.getScaleX()> 0.5) {
            group.setScaleX(group.getScaleX() - 0.25);
            group.setScaleY(group.getScaleY() - 0.25);
        }
    }

    public void resetScale()
    {
        group.setScaleX(1);
        group.setScaleY(1);
    }

    public double getGroupScale() {
        return group.getScaleX();
    }

    public Point2D groupParentToLocal(Point2D point)
    {
        return group.parentToLocal(point);
    }

    public Point2D groupLocalToParent(Point2D point) {
        return group.localToParent(point);
    }

    public void resetGroupTranslate() {
        group.setTranslateX(0);
        group.setTranslateY(0);
    }
}
