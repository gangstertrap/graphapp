package graphapp.userinterface;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class YesNoDialog extends Dialog<Integer> {

    public YesNoDialog(Stage primaryStage, String trueButton, String falseButton) {
        super();
        initOwner(primaryStage);
        initStyle(StageStyle.DECORATED);
        initModality(Modality.APPLICATION_MODAL);

        ButtonType trueButtonType = new ButtonType(trueButton, ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType falseButtonType = new ButtonType(falseButton, ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        setResultConverter((btnType) -> {
            if(btnType.equals(trueButtonType))
                return 1;
            if(btnType.equals(falseButtonType))
                return 0;
            return -1;
        });

        getDialogPane().getButtonTypes().addAll(trueButtonType, falseButtonType, cancelButtonType);

    }
}
