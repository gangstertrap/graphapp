package graphapp;

import graphapp.graphtheory.Graph;
import graphapp.graphtheory.Vertex;
import graphapp.persistence.LocalStorage;
import graphapp.userinterface.Controller;
import graphapp.userinterface.UIEventListener;
import graphapp.userinterface.UserInterface;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.Optional;

public class GraphApplication extends Application
{

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        UserInterface ui = new UserInterface(primaryStage);
        LocalStorage storage = new LocalStorage();
        Graph graph = new Graph();
        UIEventListener uiLogic = new Controller(graph, ui, storage);
        ui.setController(uiLogic);


    }

    public static void main(String[] args)
    {
        launch(args);
    }
}