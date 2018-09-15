package sample.util;

import javafx.scene.control.TextInputDialog;
import javafx.stage.StageStyle;
import sample.FxDialog;

import javax.swing.*;
import java.sql.*;
import java.util.Optional;

public class DBUtil {
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String connStr = "jdbc:mysql://localhost:3306/finances?autoReconnect=true&useSSL=false";
    private static Connection conn = null;

    public boolean isConnected(){
        if(conn == null){
            return false;
        }else{
            return true;
        }
    }

    public void connectDB() throws SQLException, ClassNotFoundException {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException ex) {
            System.out.println("Where is your mysql Driver?");
            ex.printStackTrace();
            throw ex;
        }

        try {


            JPanel panel = new JPanel();
              JLabel label = new JLabel("Enter database password:");
              JPasswordField pass = new JPasswordField(10);
              panel.add(label);
              panel.add(pass);
              String[] options = new String[]{"OK", "Cancel"};
              int option = JOptionPane.showOptionDialog(null, panel, "Database password",
                      JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                      null, options, options[0]);
              char[] passwd = null;
              if(option == 0){//pressed OK
                  passwd = pass.getPassword();
              }else{
                  //close program bc no password
                  System.exit(0);
              }


            conn = DriverManager.getConnection(connStr, "tetrapro", new String(passwd));

        } catch (SQLException ex) {
            System.out.println("Connection Failed! Check output console " + ex);
            ex.printStackTrace();
            throw ex;
        }
    }

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
    public void executeUpdate(String query, String[] values){
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ResultSet executeQuery(String query){
        ResultSet result = null;
        try{
            String sql = query;
            connectDB();

            Statement statement = conn.createStatement();
            result = statement.executeQuery(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getBalance(String account){

        double balance = 0.0;
        String initial = null;
        String allDeposits = null;
        String allDebits = null;
        String queryInitial = "SELECT amount FROM ledger WHERE from_account = ? AND notes = 'Initial Balance';";//get initial balance and add all deposits and subtract all debits
        String queryDeposits = "SELECT SUM(amount) FROM ledger WHERE from_account = ? AND notes = 'Deposit';";
        String queryDebits = "SELECT SUM(amount) FROM ledger WHERE from_account = ?;";// AND (date BETWEEN DATE_FORMAT(NOW(), '%Y-%m-01') AND NOW())
        ResultSet rs = null;

        if(conn == null){
            try {
                connectDB();
                PreparedStatement statement = conn.prepareStatement(queryInitial);
                statement.setString(1, account);
                rs = statement.executeQuery();
                while(rs.next()){
                    initial = rs.getString(1);
                }
                conn.close();

                PreparedStatement depositStmt = conn.prepareStatement(queryDeposits);
                depositStmt.setString(1, account);
                rs = statement.executeQuery();
                while(rs.next()){
                    allDeposits = rs.getString(1);
                }
                conn.close();

                PreparedStatement debitsStmt = conn.prepareStatement(queryDebits);
                debitsStmt.setString(1, account);
                rs = statement.executeQuery();
                while(rs.next()){
                    allDebits = rs.getString(1);
                }
                conn.close();

                balance = Double.parseDouble(initial) + Double.parseDouble(allDeposits) - Double.parseDouble(allDebits);
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return String.valueOf(balance);
    }
}
