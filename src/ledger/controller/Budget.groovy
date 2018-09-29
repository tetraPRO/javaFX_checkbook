package ledger.controller

import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.Initializable
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.util.Callback
import ledger.util.DBUtil

import java.sql.ResultSet


class Budget implements Initializable{

    public ComboBox account
    public ProgressBar budgetProgress
    public Label budgetLabel
    public Label budgetDisplay
    public TableView budgetTable

    DBUtil sql = null

    @Override
    void initialize(URL url, ResourceBundle resourceBundle) {

        sql = new DBUtil()

        budgetProgress.setVisible(false)
        budgetLabel.setVisible(false)
        budgetDisplay.setVisible(false)
        budgetTable.setVisible(false)

        setListener()
    }

    void setListener(){
        account.valueProperty().addListener(new ChangeListener() {
            @Override
            void changed(ObservableValue observableValue, Object o, Object t1) {

                budgetTable.getColumns().clear()

                budgetLabel.setVisible(true)
                budgetDisplay.setVisible(true)
                budgetProgress.setVisible(true)

                def budget = sql.getBudget(account.getValue().toString())
                //get amount spent and find % to setProgress
                def balance = sql.getSpent(account.getValue().toString())
                def percent = balance / budget

                budgetProgress.setProgress(percent)
                budgetDisplay.setText(String.valueOf(budget))
                buildData()
                budgetTable.setVisible(true)
            }
        })
    }

    void buildData(){

        final ObservableList<ObservableList<String>> data = null

        try{
            //ResultSet
            ResultSet rs = sql.getTransactions(account.getValue().toString())

            /**********************************
             * TABLE COLUMN ADDED DYNAMICALLY *
             **********************************/
            for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
                //We are using non property style for making dynamic table
                final int j = i
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1))
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
                    ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString())
                    }
                })

                budgetTable.getColumns().addAll(col)
            }

            /********************************
             * Data added to ObservableList *
             ********************************/
            while(rs.next()){
                //Iterate Row

                ObservableList<String> row = FXCollections.observableArrayList()
                for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++){
                    //Iterate Column
                    row.add(rs.getString(i))
                }
                println("Row [1] added "+row )
                data?.add(row)
            }

            //FINALLY ADDED TO TableView
            budgetTable.setItems(data)
        }catch(Exception e){
            e.printStackTrace()
            System.out.println("Error on Building Data")
        }
    }
}
