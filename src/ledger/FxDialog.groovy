package ledger

import javafx.scene.control.TextInputDialog
import javafx.stage.StageStyle

class FxDialog {
    static String showTextInput(String title, String message, String defaultValue){
        TextInputDialog dialog = new TextInputDialog(defaultValue)
        dialog.initStyle(StageStyle.UTILITY)
        dialog.setTitle("Input")
        dialog.setHeaderText(title)
        dialog.setContentText(message)

        Optional<String> result = dialog.showAndWait()
        if(result.isPresent()){
            return result.get()
        }else{
            return null
        }
    }
}
