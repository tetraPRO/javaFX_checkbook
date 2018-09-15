package sample.controller

import javafx.event.ActionEvent
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.layout.AnchorPane
import sample.util.DBUtil

import java.sql.SQLException

class Controller implements Initializable {

    public AnchorPane anchor
    public DatePicker date
    public ComboBox from_account
    public ComboBox to_account
    public TextField amount
    public TextField notes
    public Button update
    public ProgressBar progressBar
    static DBUtil data = null

    @Override
    void initialize(URL url, ResourceBundle resourceBundle) {
        update.setDefaultButton(true)
        Date today = new Date()
        String[] parts = today.toString().split(" ")
        progressBar.setProgress(Double.parseDouble(parts[2])/30)//double value representing the day of the month as a fraction

        data = new DBUtil()
        try {
            data.connectDB()

        } catch (SQLException e) {
            e.printStackTrace()
        } catch (ClassNotFoundException e) {
            e.printStackTrace()
        }
    }

    /**
     * Adds all input fields into the database
     * @param actionEvent
     */
    void updateNow(ActionEvent actionEvent) {

        if (update.text == "Get it!") {//no need for date here
            //select query here...
            def balance = data.getBalance(from_account.getValue().toString())
            amount.setText(balance)
            notes.setText("Current Balance")
        } else if (update.text == "Budget Amount"){
            //possibly a new table to hold budget data...?
        }else {
            String today = date.getValue().toString()
            String from = from_account.getValue().toString()
            String to = to_account.getValue().toString()
            String amountOfTrans = amount.getText()
            String notesOfTrans = notes.getText()

            String[] params = new String[5]
            params[0] = today
            params[1] = from
            params[2] = to
            params[3] = amountOfTrans
            params[4] = notesOfTrans
            data.executeUpdate("INSERT INTO ledger (date, from_account, to_account, amount, notes) VALUES (?,?,?,?,?);", params)
        }

        clearAll(actionEvent)
    }

    /**
     * Sets initial account balance to be added
     *  to database
     * @param actionEvent
     */
    void initialBalance(ActionEvent actionEvent) {
        clearAll(actionEvent)
        from_account.setPromptText("Account")
        to_account.setValue("Set Balance")
        notes.setText("Initial Balance")

    }

    /**
     * Retrieves current account balance from database
     * @param actionEvent
     */
    void accountBalance(ActionEvent actionEvent) {
        clearAll(actionEvent)

        to_account.setVisible(false)
        String account = from_account.getSelectionModel().getSelectedItem().toString()
        String balance = data.getBalance(account)
        amount.setText(balance)
        notes.setText("Current Balance")
        update.setText("Get it!")
    }

    /**
     * Clears all input fields
     * @param actionEvent
     */
    void clearAll(ActionEvent actionEvent) {
        //clean up fields
        date.setValue(null)
        date.setPromptText("Date")
        from_account.getSelectionModel().clearSelection()
        from_account.setPromptText("From Account")
        to_account.getSelectionModel().clearSelection()
        to_account.setPromptText("To Account")
        amount.setText("")
        amount.setPromptText("Amount")
        notes.setText("")
        notes.setPromptText("Notes")
    }

    void setBudget(ActionEvent actionEvent) {
        from_account.setValue("Select Account")
        to_account.setVisible(false)
        notes.setText("Budget Amount")
    }

    void addDeposit(ActionEvent actionEvent) {
        from_account.setValue("Into Account")
        to_account.setVisible(false)
        notes.setText("Deposit")
    }
}
