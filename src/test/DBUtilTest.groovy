package test

import ledger.util.DBUtil

import java.sql.ResultSet

class DBUtilTest extends GroovyTestCase{



    void testDBConnection(){
        data.connectDB()
        def testConnection = data.connected
        assertTrue(testConnection)
    }

    void testExecuteUpdate(){
        def sql = "INSERT INTO ledger (date, from_account, to_account, amount, notes) VALUES (?,?,?,?,?)"
        def params = ["2018-09-15","Bank","Groceries/Food","0.00","Testing"] as String[]
        assert true == data.executeUpdate(sql, params)
    }

    void testExecuteQuery(){
        ResultSet rs =  null
        boolean empty = true
        rs = data.executeQuery("SELECT * FROM ledger;")
        while(rs.next()){
            empty = false
        }
        if(!empty){
            assertTrue !empty
        }
    }

    void testGetBalance(){
        def balance = 0.0
        balance = data.getBalance("Bank")
        println balance
        assert balance == '1616.22'
    }

    void testTransactions(){

        ResultSet rs = null
        rs = data.getTransactions("Bank")
        while(rs.next()){
            for(int i=1;i<=rs.getMetaData().getColumnCount();i++){
                print(rs.getString(i))
            }
        }
        assert rs != null
    }

    void testGetBudget(){

        Double budget = data.getBudget('Bank')
        assert budget == 4000.0
    }

    void testGetSpent(){
        DBUtil data = new DBUtil()
        Double spent = data.getSpent('Groceries/Food')
        assert spent == 218.14
    }
}
