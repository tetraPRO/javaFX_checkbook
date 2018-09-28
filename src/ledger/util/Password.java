package ledger.util;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JOptionPane;

/**
 * Generates a pop-up dialog to get
 * password input.  Returns 0 if
 * entered 'OK', otherwise returns 1
 */
public class Password{

    public char[] getPass(){
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Enter password:");
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
        return passwd;
    }
}