package graphapp.userinterface;

import graphapp.constants.AppColors;
import graphapp.constants.ToolMode;
import graphapp.graphtheory.Edge;
import graphapp.graphtheory.Graph;
import graphapp.graphtheory.Vertex;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class UserInterface {
    private final BorderPane borderPane;
    //private final Pane pane;
    private final Pane group;
    private final Stage stage;
    private UIEventListener controller;

    //private final Region rect;

    private final HashMap<Vertex, VertexLabel> vertices;
    private final HashMap<Edge, EdgeGroup> edges;

    private CheckBox graphSettingsWeightedCheck;
    private CheckBox graphSettingsDirectedCheck;

    private final Rectangle selectRectangle;

    private final Stage graphSettingsStage;

    private MenuItem deleteVertex;
    private MenuItem renameVertex;
    private TextField settingsField;
    private MenuItem setEdgeWeight;
    private MenuItem switchEdgeDirection;

    private boolean showWeights;
    private boolean showDirected;
    private Text verticesCountText;
    private Text edgesCountText;

    public UserInterface(Stage stage) {
        this.borderPane = new BorderPane();
        vertices = new HashMap<>();
        edges = new HashMap<>();
        Pane pane = new Pane();
        group = new Pane();
        this.stage = stage;

        /*rect = new Region();
        rect.setBackground(new Background(new BackgroundImage(createGridPattern().getImage(), BackgroundRepeat.REPEAT,
                BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)));
        rect.prefWidthProperty().bind(pane.widthProperty());
        rect.prefHeightProperty().bind(pane.widthProperty());
        pane.getChildren().add(rect);*/

        showWeights = false;
        showDirected = false;
        borderPane.setCenter(pane);
        pane.getChildren().add(group);

        initTopBar();
        initBottomInfoBar();

        selectRectangle = new Rectangle(0, 0, 0, 0);
        setupSelectRect();
        pane.getChildren().add(selectRectangle);

        Scene scene = new Scene(borderPane, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Untitled");
        stage.show();

        graphSettingsStage = new Stage();
        setupSettings();


        setupEventListeners(pane);
        //group.setBackground(new Background(new BackgroundImage(createGridPattern().getImage(), BackgroundRepeat.REPEAT,
                //BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)));
        group.prefWidthProperty().bind(pane.widthProperty().multiply(2));
        group.prefHeightProperty().bind(pane.widthProperty().multiply(2));
    }

    private void setupSettings() {
        BorderPane bp = new BorderPane();
        GridPane gp = new GridPane();
        bp.setCenter(gp);
        ToolBar tb = new ToolBar();
        Button okay = new Button("OK");
        Button cancel = new Button("Cancel");
        Button apply = new Button("Apply");
        okay.setOnAction(event -> controller.onGraphSettingsButtonClicked(event));
        cancel.setOnAction(event -> controller.onGraphSettingsButtonClicked(event));
        apply.setOnAction(event -> controller.onGraphSettingsButtonClicked(event));
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        bp.setBottom(tb);
        tb.getItems().addAll(spacer, okay, cancel, apply);
        VBox vbox = new VBox();
        Scene settingsScene = new Scene(bp, 400, 300);
        gp.getColumnConstraints().addAll(new ColumnConstraints(20), new ColumnConstraints(200));
        gp.getRowConstraints().addAll(new RowConstraints(20), new RowConstraints(50), new RowConstraints(50));
        graphSettingsStage.setScene(settingsScene);
        graphSettingsStage.setTitle("Graph Settings");
        graphSettingsWeightedCheck = new CheckBox();
        Text text0 = new Text("Enable weight: ");
        HBox hbox0 = new HBox();
        graphSettingsDirectedCheck = new CheckBox();
        Text text1 = new Text("Enable directed: ");
        HBox hbox1 = new HBox();
        hbox0.getChildren().addAll(text0, graphSettingsWeightedCheck);
        hbox1.getChildren().addAll(text1, graphSettingsDirectedCheck);
        gp.add(hbox0, 1, 1);
        gp.add(hbox1, 1, 2);
        graphSettingsStage.setResizable(false);

    }

    public boolean getWeightedCheckValue() {
        return graphSettingsWeightedCheck.isSelected();
    }

    public boolean getDirectedCheckValue() {
        return graphSettingsDirectedCheck.isSelected();
    }

    private void setupEventListeners(Pane pane) {
        //pane.setOnKeyTyped(event -> controller.onKeyTyped(event));
        pane.setOnMouseClicked(event -> controller.onMouseClicked(event));
        pane.setOnMousePressed(event -> controller.onMousePressed(event));
        pane.setOnMouseReleased(event -> controller.onMouseReleased(event));
        pane.setOnMouseDragged(event -> controller.onMouseDragged(event));
        /*borderPane.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            keyEvent.consume();
            controller.onKeyPressed(keyEvent);
        });*/
    }

    private void setupSelectRect() {
        selectRectangle.setVisible(false);
        selectRectangle.setFill(Color.TRANSPARENT);
        selectRectangle.setStroke(AppColors.SELECTED_COLOR);
        selectRectangle.getStrokeDashArray().addAll(10d, 10d);
    }

    private void initTopBar() {
        VBox vbox = new VBox();
        initMenuBar(vbox);
        initTopToolBar(vbox);
        borderPane.setTop(vbox);
    }

    private void initMenuBar(VBox vbox) {
        MenuBar topMenuBar = new MenuBar();
        topMenuBar.setPrefHeight(20);
        topMenuBar.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY.brighter(), null, null)));

        Border borderTop = new Border(new BorderStroke(null, null, Color.rgb(200, 200, 200), null, null, null, BorderStrokeStyle.SOLID, null, null, null, null));
        topMenuBar.setBorder(borderTop);

        Menu fileMenu = new Menu("File");
        Menu editMenu = new Menu("Edit");
        Menu viewMenu = new Menu("View");
        topMenuBar.getMenus().add(fileMenu);
        topMenuBar.getMenus().add(editMenu);
        topMenuBar.getMenus().add(viewMenu);

        MenuItem newFile = new MenuItem("New");
        MenuItem openFile = new MenuItem("Open...");
        MenuItem saveFile = new MenuItem("Save");
        MenuItem saveAsFile = new MenuItem("Save As...");
        MenuItem openGraphSettings = new MenuItem("Graph Settings...");
        //MenuItem exportAsImage = new MenuItem("Export as Image...");

        KeyCombination newFileKeyCombo = new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN);
        KeyCombination openFileKeyCombo = new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN);
        KeyCombination saveFileKeyCombo = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
        KeyCombination saveAsFileKeyCombo = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN);
        newFile.setAccelerator(newFileKeyCombo);
        openFile.setAccelerator(openFileKeyCombo);
        saveFile.setAccelerator(saveFileKeyCombo);
        saveAsFile.setAccelerator(saveAsFileKeyCombo);

        deleteVertex = new MenuItem("Delete");
        renameVertex = new MenuItem("Rename Vertex...");
        setEdgeWeight = new MenuItem("Set Edge Weight...");
        switchEdgeDirection = new MenuItem("Switch Edge Direction");
        KeyCode kc = KeyCode.ENTER;
        KeyCombination deleteVertexKeyCombo = new KeyCodeCombination(KeyCode.DELETE);
        KeyCombination renameVertexKeyCombo = new KeyCodeCombination(KeyCode.R, KeyCombination.SHORTCUT_DOWN);
        KeyCombination setWeightKeyCombo = new KeyCodeCombination(KeyCode.T,KeyCombination.SHORTCUT_DOWN);
        KeyCombination switchDirectionKeyCombo = new KeyCodeCombination(KeyCode.Y,KeyCombination.SHORTCUT_DOWN);
        deleteVertex.setAccelerator(deleteVertexKeyCombo);
        renameVertex.setAccelerator(renameVertexKeyCombo);
        setEdgeWeight.setAccelerator(setWeightKeyCombo);
        switchEdgeDirection.setAccelerator(switchDirectionKeyCombo);

        MenuItem zoomIn = new MenuItem("Zoom In");
        MenuItem zoomOut = new MenuItem("Zoom Out");
        MenuItem zoomReset = new MenuItem("Reset Zoom");
        Menu panSubMenu = new Menu("Pan...");
        MenuItem recenter = new MenuItem("Recenter");
        MenuItem panLeft = new MenuItem("Pan Left");
        MenuItem panRight = new MenuItem("Pan Right");
        MenuItem panUp = new MenuItem("Pan Up");
        MenuItem panDown = new MenuItem("Pan Down");
        panSubMenu.getItems().addAll(recenter, panLeft,panRight,panUp,panDown);

        KeyCombination zoomInKeyCombo = new KeyCodeCombination(KeyCode.EQUALS, KeyCombination.CONTROL_DOWN);
        KeyCombination zoomOutKeyCombo = new KeyCodeCombination(KeyCode.MINUS, KeyCombination.CONTROL_DOWN);
        KeyCombination zoomResetKeyCombo = new KeyCodeCombination(KeyCode.DIGIT0, KeyCombination.CONTROL_DOWN);
        KeyCombination panLeftKeyCombo = new KeyCodeCombination(KeyCode.LEFT, KeyCombination.CONTROL_DOWN);
        KeyCombination panRightKeyCombo = new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.CONTROL_DOWN);
        KeyCombination panUpKeyCombo = new KeyCodeCombination(KeyCode.UP, KeyCombination.CONTROL_DOWN);
        KeyCombination panDownKeyCombo = new KeyCodeCombination(KeyCode.DOWN, KeyCombination.CONTROL_DOWN);
        zoomIn.setAccelerator(zoomInKeyCombo);
        zoomOut.setAccelerator(zoomOutKeyCombo);
        zoomReset.setAccelerator(zoomResetKeyCombo);
        panLeft.setAccelerator(panLeftKeyCombo);
        panRight.setAccelerator(panRightKeyCombo);
        panUp.setAccelerator(panUpKeyCombo);
        panDown.setAccelerator(panDownKeyCombo);

        /*deleteVertex.setDisable(true);
        renameVertex.setDisable(true);
        setEdgeWeight.setDisable(true);
        switchEdgeDirection.setDisable(true);*/

        fileMenu.getItems().addAll(newFile, openFile, saveFile, saveAsFile, openGraphSettings);
        editMenu.getItems().addAll(deleteVertex, renameVertex, setEdgeWeight, switchEdgeDirection);
        viewMenu.getItems().addAll(zoomIn, zoomOut, zoomReset, panSubMenu);
        editMenu.setOnShowing(event -> controller.onEditMenuShowing(event));
        editMenu.setOnHidden(event -> {
            deleteVertex.setDisable(false);
            renameVertex.setDisable(false);
            setEdgeWeight.setDisable(false);
            switchEdgeDirection.setDisable(false);
        });

        fileMenu.getItems().forEach((item) -> item.setOnAction(actionEvent -> controller.onMenuItemClicked(actionEvent)));
        editMenu.getItems().forEach((item) -> item.setOnAction(actionEvent -> controller.onMenuItemClicked(actionEvent)));
        viewMenu.getItems().forEach((item) -> item.setOnAction(actionEvent -> controller.onMenuItemClicked(actionEvent)));
        panSubMenu.getItems().forEach((item) -> item.setOnAction(actionEvent -> controller.onMenuItemClicked(actionEvent)));

        topMenuBar.setFocusTraversable(false);

        vbox.getChildren().add(topMenuBar);
    }

    public Pane getGroupPane(){
        return group;
    }

    public ImagePattern createGridPattern() {

        double w = 50;
        double h = 50;

        Canvas canvas = new Canvas(w, h);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setStroke(Color.BLACK);
        gc.setFill(Color.LIGHTGRAY.deriveColor(1, 1, 1, 0.2));
        gc.fillRect(0, 0, w, h);
        gc.strokeRect(0, 0, w, h);

        Image image = canvas.snapshot(new SnapshotParameters(), null);
        ImagePattern pattern = new ImagePattern(image, 0, 0, w, h, false);

        return pattern;

    }


    private void initTopToolBar(VBox vbox) {
        ToolBar topToolBar = new ToolBar();

        topToolBar.setPrefHeight(25);
        topToolBar.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY.brighter(), null, null)));
        Border border = new Border(new BorderStroke(
                null, null, Color.rgb(200, 200, 200), null,
                null, null, BorderStrokeStyle.SOLID, null,
                null, null, null));
        topToolBar.setBorder(border);

        ToggleGroup tgroup = new ToggleGroup();
        RadioButton rb1 = new RadioButton("Select");
        RadioButton rb2 = new RadioButton("Pan");
        RadioButton rb3 = new RadioButton("Move");
        RadioButton rb4 = new RadioButton("New Vertex");
        RadioButton rb5 = new RadioButton("New Edge");
        rb1.setUserData(ToolMode.SELECT);
        rb2.setUserData(ToolMode.PAN);
        rb3.setUserData(ToolMode.MOVE);
        rb4.setUserData(ToolMode.NEW_VERTEX);
        rb5.setUserData(ToolMode.NEW_EDGE);

        /*rb1.setGraphic(new ImageView(new Image("/select.png")));
        rb2.setGraphic(new ImageView(new Image("/pan.png")));
        rb3.setGraphic(new ImageView(new Image("/move.png")));
        rb4.setGraphic(new ImageView(new Image("/newvertex.png")));
        rb5.setGraphic(new ImageView(new Image("/newedge.png")));*/

        rb1.setAccessibleText("Select Tool");

        rb1.setToggleGroup(tgroup);
        rb2.setToggleGroup(tgroup);
        rb3.setToggleGroup(tgroup);
        rb4.setToggleGroup(tgroup);
        rb5.setToggleGroup(tgroup);
        rb1.setSelected(true);
        topToolBar.getItems().addAll(rb1, rb2, rb3, rb4, rb5);

        tgroup.selectedToggleProperty().addListener(
                (observableValue, oldToggle, newToggle) ->
                        controller.onModeChange((ToolMode) tgroup.getSelectedToggle().getUserData()));

        vbox.getChildren().add(topToolBar);

        topToolBar.setFocusTraversable(false);
    }

    private void initBottomInfoBar() {
        ToolBar bottomToolBar = new ToolBar();

        bottomToolBar.setPrefHeight(20);

        bottomToolBar.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY.brighter(), null, null)));
        Border borderBottom = new Border(new BorderStroke(
                Color.rgb(200, 200, 200), null, null, null,
                BorderStrokeStyle.SOLID, null, null, null,
                null, null, null));

        bottomToolBar.setBorder(borderBottom);

        borderPane.setBottom(bottomToolBar);

        verticesCountText = new Text("0 vertices");
        edgesCountText = new Text("0 edges");
        bottomToolBar.getItems().addAll(verticesCountText, new Separator(), edgesCountText);

    }

    public void updateVerticesCount(int count) {
        verticesCountText.setText(count == 1 ? count + " vertex" : count + " vertices");
    }

    public void updateEdgesCount(int count) {
        edgesCountText.setText(count == 1 ? count + " edge" : count + " edges");
    }

    public void openGraphSettings(Graph g) {
        graphSettingsWeightedCheck.setSelected(g.isWeighted());
        graphSettingsDirectedCheck.setSelected(g.isDirected());
        graphSettingsStage.show();
    }

    public void closeGraphSettings() {
        graphSettingsStage.hide();
    }

    public void updateGraphName(String name) {
        stage.setTitle(name);
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
        System.out.println(group.getLayoutX() + "," + group.getLayoutY());
    }

    public void addNewEdge(Edge e)
    {
        VertexLabel vl1 = vertices.get(e.getVertex1());
        VertexLabel vl2 = vertices.get(e.getVertex2());

        Line line = new Line(vl1.getLayoutX() + VertexLabel.RADIUS, vl1.getLayoutY() + VertexLabel.RADIUS,
                vl2.getLayoutX() + VertexLabel.RADIUS, vl2.getLayoutY() + VertexLabel.RADIUS);

        EdgeGroup edge = new EdgeGroup(e, line, showWeights, showDirected);
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
        deleteVertex.setDisable(b);
    }

    public void renameEditSetDisable(boolean b) {
        renameVertex.setDisable(b);
    }

    public void edgeWeightEditSetDisable(boolean b) {
        setEdgeWeight.setDisable(b);
    }

    public void switchEdgeDirectionSetDisable(boolean b) {
        switchEdgeDirection.setDisable(b);
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

    public int showSetWeightDialog(int oldWeight) {
        TextInputDialog dialog = new TextInputDialog("" + oldWeight);
        TextField field = dialog.getEditor();
        TextFormatter<String> formatter = new TextFormatter<>(change -> {
            if (!change.getText().matches("\\d+")) {
                change.setText("");
            }
            return change;
        });
        field.setTextFormatter(formatter);
        dialog.setTitle("Set Edge Weight");
        dialog.setGraphic(null);
        dialog.setHeaderText("");
        dialog.setContentText("Input must be an integer.");
        dialog.showAndWait();

        try {
            return Integer.parseInt(dialog.getResult());
        } catch(NumberFormatException e) {
            return oldWeight;
        }
    }

    public void showEditErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error in making edit");
        alert.setGraphic(null);
        alert.setHeaderText("");
        alert.setContentText(message);
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

    public void updateGraphSettingsItems(boolean weighted, boolean directed) {
        graphSettingsWeightedCheck.setSelected(weighted);
        graphSettingsDirectedCheck.setSelected(directed);
    }

    public void updateDirected(boolean b) {
        showDirected = b;
        for(Edge e : edges.keySet()) {
            EdgeGroup eg = edges.get(e);
            eg.setDirectedVisible(showDirected);
        }
    }

    public void updateWeighted(boolean b) {
        showWeights = b;
        for(Edge e : edges.keySet()) {
             EdgeGroup eg = edges.get(e);
             eg.setWeightVisible(showWeights);
        }
    }

    public File openSaveFileChooser(String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Graphapp Graphs", "*.graphapp"));
        return fileChooser.showSaveDialog(stage);
    }

    public File showOpenFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Graph...");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Graphapp Graphs", "*.graphapp"));
        return fileChooser.showOpenDialog(stage);
    }

    public int saveCurrentGraph() {
        YesNoDialog dialog = new YesNoDialog(stage, "Save", "Don't Save");
        dialog.setContentText("Do you want to save changes to the current graph?");
        dialog.showAndWait();
        return dialog.getResult();
    }

    public void updateContents(Collection<Vertex> values, Set<Edge> edges) {
        for(VertexLabel vl : this.vertices.values()) {
            group.getChildren().remove(vl);
        }
        for(EdgeGroup eg : this.edges.values()) {
            group.getChildren().remove(eg);
        }
        this.vertices.clear();
        this.edges.clear();
        for(Vertex v : values) {
            addNewVertexLabel(v);
        }
        for(Edge e : edges) {
            addNewEdge(e);
        }
    }

    public File openExportFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export as Image...");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image file", "*.png"));
        return fileChooser.showSaveDialog(stage);
    }
}
