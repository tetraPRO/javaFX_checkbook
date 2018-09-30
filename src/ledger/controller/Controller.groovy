package ledger.controller

import javafx.event.ActionEvent
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.layout.AnchorPane
import ledger.util.DBUtil

import java.sql.SQLException
import java.text.NumberFormat
import java.time.LocalDate

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
    public Label displayBalance

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

        String balance = data.getBalance("Bank")
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US)

        displayBalance.setText nf.format(Double.parseDouble(balance))
    }

    /**
     * Adds all input fields into the database
     * @param actionEvent
     */
    void updateNow() {

        if (update.text == "Get it!") {
            String account = from_account.getValue().toString()
            String balance = data.getBalance(account)
            amount.setText(balance)

            update.setText("Update")

        } else if (update.text == "Budget"){
            String now = date.getValue()
            String account = to_account.getValue().toString()
            String budget = amount.getText()
            String note = notes.getText()
            String[] params = [now,account,budget,note]
            if(data.setBudget(params)){
                clearAll()
            }
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
            def isSuccessful = data.executeUpdate("INSERT INTO ledger (date, from_account, to_account, amount, notes) VALUES (?,?,?,?,?);", params)
            if(isSuccessful){
                clearAll()
            }
        }

        if(!date.isVisible()){
            date.setVisible(true)
        }
        if(!to_account.isVisible()){
            to_account.setVisible(true)
        }
        if(!from_account.isVisible()){
            from_account.setVisible(true)
        }
    }

    /**
     * Sets initial account balance to be added
     *  to database
     * @param actionEvent
     */
    void initialBalance(ActionEvent actionEvent) {
        clearAll(actionEvent)
        from_account.setPromptText("Account")
        to_account.setVisible(false)
        notes.setText("Initial Balance")

    }

    /**
     * Retrieves current account balance from database
     * @param actionEvent
     */
    void accountBalance(ActionEvent actionEvent) {
        clearAll(actionEvent)

        date.setVisible(false)
        to_account.setVisible(false)
        notes.setText("Current Balance")
        update.setText("Get it!")
    }

    /**
     * Clears all input fields
     * @param actionEvent
     */
    void clearAll() {
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

    void setBudget() {
        date.setValue(LocalDate.now())
        from_account.setVisible(false)
        to_account.setValue("Select Account")
        notes.setText("Budget")
        update.setText("Budget")
    }

    void addDeposit() {
        from_account.setValue("Into Account")
        to_account.setVisible(false)
        notes.setText("Deposit")
    }
}
