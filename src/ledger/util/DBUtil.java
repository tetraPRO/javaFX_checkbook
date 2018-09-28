package ledger.util;

import ledger.Password;

import java.sql.*;

public class DBUtil {
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String connStr = "jdbc:mysql://localhost:3306/finances?autoReconnect=true&useSSL=false";
    private static Connection conn = null;

    /**
     * Returns a boolean value true if
     * the database is connected or false
     * otherwise
     * @return
     */
    public boolean isConnected(){
        if(conn == null){
            return false;
        }else{
            return true;
        }
    }

    /**
     * Connects the database with username & password
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void connectDB() throws SQLException, ClassNotFoundException {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException ex) {
            System.out.println("Where is your mysql Driver?");
            ex.printStackTrace();
            throw ex;
        }

        try {
            Password password = new Password();
            char[] passwd = password.getPass();

            conn = DriverManager.getConnection(connStr, "tetrapro", new String(passwd));

        } catch (SQLException ex) {
            System.out.println("Connection Failed! Check output console " + ex);
            System.exit(0);//close bc incorrect password
            ex.printStackTrace();
            throw ex;
        }
    }

    /**
     * Disconnect the database
     * @throws SQLException
     */
    public static void dbDisconnect() throws SQLException {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * INSERT INTO ledger (date, from_account, to_account, amount, notes) VALUES (?,?,?,?,?)
     * @param query
     * @param values
     */
    public boolean executeUpdate(String query, String[] values){
        try {
            if(conn == null){
                connectDB();
            }
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, values[0]);
            statement.setString(2, values[1]);
            statement.setString(3, values[2]);
            statement.setString(4, values[3]);
            statement.setString(5, values[4]);
            int rowsInserted = statement.executeUpdate();
            if(rowsInserted > 0){
                System.out.println("Data updated successfully!");
                return true;
            }else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }return false;
    }

    /**
     * Execute a SQL query to the database and
     * return a resultset with queried data
     * @param query
     * @return
     */
    public ResultSet executeQuery(String query){
        ResultSet result = null;
        try{
            String sql = query;
            if(conn == null){
                connectDB();
            }

            Statement statement = conn.createStatement();
            result = statement.executeQuery(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Returns the current balance of a given account
     * @param account
     * @return
     */
    public String getBalance(String account){

        double balance = 0.0;
        String initial = null;
        String initialDate = null;
        String allDeposits = null;
        String allDebits = null;
        String queryInitial = "SELECT date, amount FROM ledger WHERE from_account = ? AND notes = 'Initial Balance';";//get initial balance and add all deposits and subtract all debits
        String queryDeposits = "SELECT SUM(amount) FROM ledger WHERE date >= ? AND from_account = ? AND notes = 'Deposit';";
        String queryDebits = "SELECT SUM(amount) FROM ledger WHERE date >= ? AND from_account = ? AND notes != 'Initial Balance' AND notes != 'Deposit';";// AND (date BETWEEN DATE_FORMAT(NOW(), '%Y-%m-01') AND NOW())
        ResultSet rs;

        if(conn == null){
            try {
                connectDB();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        try{
            PreparedStatement statement = conn.prepareStatement(queryInitial);
            statement.setString(1, account);
            rs = statement.executeQuery();
            while(rs.next()){
                initialDate = rs.getString(1);
                initial = rs.getString(2);
            }

            PreparedStatement depositStmt = conn.prepareStatement(queryDeposits);
            depositStmt.setString(1, initialDate);
            depositStmt.setString(2, account);
            rs = depositStmt.executeQuery();
            while(rs.next()){
                allDeposits = rs.getString(1);
            }

            PreparedStatement debitsStmt = conn.prepareStatement(queryDebits);
            debitsStmt.setString(1, initialDate);
            debitsStmt.setString(2, account);
            rs = debitsStmt.executeQuery();
            while(rs.next()){
                allDebits = rs.getString(1);
            }
            rs.close();

            if(initial == null){
                initial = "0.0";
            }
            if(allDeposits == null){
                allDeposits = "0.0";
            }
            if(allDebits == null){
                allDebits = "0.0";
            }

            balance = Double.parseDouble(initial) + Double.parseDouble(allDeposits) - Double.parseDouble(allDebits);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return String.valueOf(balance);
    }
}
