package graphapp;

import graphapp.graphtheory.Graph;
import graphapp.persistence.IStorage;
import graphapp.persistence.LocalStorage;
import graphapp.userinterface.Controller;
import graphapp.userinterface.UIEventListener;
import graphapp.userinterface.UserInterface;
import javafx.application.Application;
import javafx.stage.Stage;

public class GraphApplication extends Application
{

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        UserInterface ui = new UserInterface(primaryStage);
        IStorage storage = new LocalStorage();
        Graph graph = new Graph(storage);

        UIEventListener uiLogic = new Controller(graph, ui);
        ui.setController(uiLogic);

    }

    public static void main(String[] args)
    {
        launch(args);
    }
}