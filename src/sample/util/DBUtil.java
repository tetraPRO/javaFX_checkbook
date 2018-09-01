package sample.util;

import javax.swing.*;
import java.sql.*;

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
<<<<<<< HEAD
            JPanel panel = new JPanel();
            JLabel label = new JLabel("Enter database password:");
            JPasswordField pass = new JPasswordField(10);
            panel.add(label);
            panel.add(pass);
            String[] options = new String[]{"OK", "Cancel"};
            int option = JOptionPane.showOptionDialog(null, panel, "Database password",
                    JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, options, options[1]);
            char[] passwd = null;
            if(option == 0){//pressed OK
                passwd = pass.getPassword();
            }else{
                //close pane
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
}
