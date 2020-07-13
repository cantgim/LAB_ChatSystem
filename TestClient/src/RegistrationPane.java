
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author DVG
 */
public class RegistrationPane extends JFrame {

    private final ChatClient client;
    private JTextField fName, loginField;
    private JPasswordField passwordField, repassField;
    private JLabel loginLabel, fNameLabel, passLabel, repassLabel;
    private JButton registerButton;

    public RegistrationPane(ChatClient client) {
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.client = client;

        fName = new JTextField();
        loginField = new JTextField();
        passwordField = new JPasswordField();
        repassField = new JPasswordField();
        registerButton = new JButton("Register");

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doRegister();
            }
        });
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(fName);
        p.add(loginField);
        p.add(passwordField);
        p.add(repassField);
        p.add(registerButton);

        add(p, BorderLayout.CENTER);
        setVisible(true);
    }

    private void doRegister() {
        String login = loginField.getText();
        String pass = passwordField.getText();
        String repass = repassField.getText();

        if (pass != null && repass != null) {
            if (!pass.equals(repass)) {
                JOptionPane.showMessageDialog(this, "Password not match!");
                return;
            }
        }
        try {

            boolean register = client.register(login, pass);
            if (register) {
                JOptionPane.showMessageDialog(this, "Register Success!");
                setVisible(false);
                dispose();
            }
        } catch (IOException ex) {
            Logger.getLogger(RegistrationPane.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
