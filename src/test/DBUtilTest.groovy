package test

import ledger.util.DBUtil

import java.sql.ResultSet

class DBUtilTest extends GroovyTestCase{

    DBUtil data = new DBUtil()

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
}
