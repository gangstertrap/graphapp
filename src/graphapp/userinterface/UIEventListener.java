package graphapp.userinterface;

import graphapp.constants.ToolMode;
import javafx.scene.input.MouseEvent;

public interface UIEventListener
{
    void onPaneClicked(MouseEvent event);

    void onModeChange(ToolMode mode);

    void onVertexClicked(MouseEvent event);

    void onVertexPressed(MouseEvent event);

    void onVertexReleased(MouseEvent event);

    void onVertexDragged(MouseEvent event);

    void onVertexEntered(MouseEvent event);

    void onVertexExited(MouseEvent event);
}
