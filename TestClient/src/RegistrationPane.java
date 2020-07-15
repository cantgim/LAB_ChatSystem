
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
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
    private JButton registerButton, cancelButton;
    private String[] nameLabels = {"Full Name", "Login", "Password", "Re-Password"};
    
    public RegistrationPane(ChatClient client) {
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.client = client;
        
        fName = new JTextField(10);
        loginField = new JTextField(10);
        passwordField = new JPasswordField(10);
        repassField = new JPasswordField(10);
        registerButton = new JButton("Register");
        cancelButton = new JButton("Cancel");
        
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doRegister();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });
        
        layoutComponents();
        
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void layoutComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        GridBagConstraints gbc;
        JLabel[] labels = new JLabel[nameLabels.length];
        
        for (int i = 0; i < nameLabels.length; i++) {
            labels[i] = new JLabel(nameLabels[i]);
        }
        
        JPanel[] panels = new JPanel[nameLabels.length];
        Insets WEST_INSETS = new Insets(5, 0, 5, 5);
        Insets EAST_INSETS = new Insets(5, 5, 5, 0);
        
        for (int i = 0; i < nameLabels.length; i++) {
            ArrayList<JTextField> fields = new ArrayList<>();
            fields.add(fName);
            fields.add(loginField);
            fields.add(passwordField);
            fields.add(repassField);
            panels[i] = new JPanel(new GridBagLayout());
            gbc = new GridBagConstraints();
            gbc.gridheight = 1;
            gbc.gridwidth = 1;
            gbc.weighty = 1.0;
            gbc.weightx = 0.1;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.insets = WEST_INSETS;
            gbc.gridx = 0;
            gbc.gridy = 0;
            panels[i].add(labels[i], gbc);
            
            gbc.weightx = 1.0;
            gbc.anchor = GridBagConstraints.EAST;
            gbc.fill = GridBagConstraints.NONE;
            gbc.insets = EAST_INSETS;
            gbc.gridx = 1;
            panels[i].add(fields.get(i), gbc);
            
            panel.add(panels[i]);
        }
        
        JPanel p = new JPanel();
        p.add(registerButton);
        p.add(cancelButton);
        panel.add(p);
        
        getContentPane().add(panel, BorderLayout.CENTER);
        
        pack();
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
