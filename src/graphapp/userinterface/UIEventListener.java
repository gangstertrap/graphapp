package graphapp.userinterface;

import graphapp.constants.ToolMode;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public interface UIEventListener
{
    void onModeChange(ToolMode mode);

    void onKeyPressed(KeyEvent event);

    void onMouseClicked(MouseEvent event);

    void onMousePressed(MouseEvent event);

    void onMouseReleased(MouseEvent event);

    void onMouseDragged(MouseEvent event);

    void onVertexEntered(MouseEvent event);

    void onVertexExited(MouseEvent event);

    void onEditMenuShowing(Event event);

    void onKeyTyped(KeyEvent event);

    void onMenuItemClicked(ActionEvent actionEvent);
}
