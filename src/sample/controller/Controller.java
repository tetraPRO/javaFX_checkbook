package sample.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import sample.util.DBUtil;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    public Circle connectionIndicator;
    public AnchorPane anchor;
    public DatePicker date;
    public ComboBox from_account;
    public ComboBox to_account;
    public TextField amount;
    public TextField notes;
    public Button update;
    static DBUtil data = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        update.setDefaultButton(true);

        data = new DBUtil();
        try {
            data.connectDB();

            //somehow need to change connectionIndicator when database is connected.
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void updateNow(ActionEvent actionEvent) {

        String today = date.getValue().toString();
        String from = from_account.getValue().toString();
        String to = to_account.getValue().toString();
        String amountOfTrans = amount.getText();
        String notesOfTrans = notes.getText();

        String[] params = new String[5];
        params[0] = today;
        params[1] = from;
        params[2] = to;
        params[3] = amountOfTrans;
        params[4] = notesOfTrans;
        data.executeUpdate("INSERT INTO ledger (date, from_account, to_account, amount, notes) VALUES (?,?,?,?,?);", params);

        //clean up fields
        date.setValue(null);
        date.setPromptText("Date");
        from_account.getSelectionModel().clearSelection();
        from_account.setPromptText("From Account");
        to_account.getSelectionModel().clearSelection();
        to_account.setPromptText("To Account");
        amount.setText("");
        amount.setPromptText("Amount");
        notes.setText("");
        notes.setPromptText("Notes");
    }
}
