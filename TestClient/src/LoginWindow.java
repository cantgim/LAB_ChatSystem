
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author DVG
 */
public class LoginWindow extends JFrame {

    private final ChatClient client;
    private JTextField loginField = new JTextField(10);
    private JPasswordField passwordField = new JPasswordField(10);
    private JButton loginButton = new JButton("Login");
    private JButton registerButton = new JButton("Register");
    private String[] nameLabel = {"Login", "Password"};

    public LoginWindow() {
        super("Login");
        this.client = new ChatClient("localhost", 8188);
        client.connect();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doLogin();
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RegistrationPane registrationPane = new RegistrationPane(client);
                registrationPane.setVisible(true);
            }
        });
        layoutComponents();

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(LoginWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        setVisible(true);
        setLocationRelativeTo(null);
    }

    private void layoutComponents() {
        ArrayList<JTextField> fields = new ArrayList<>();
        fields.add(loginField);
        fields.add(passwordField);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        GridBagConstraints gbc;
        JLabel[] labels = new JLabel[nameLabel.length];

        for (int i = 0; i < nameLabel.length; i++) {
            labels[i] = new JLabel(nameLabel[i]);
        }

        JPanel[] panels = new JPanel[nameLabel.length];
        Insets WEST_INSETS = new Insets(5, 0, 5, 5);
        Insets EAST_INSETS = new Insets(5, 5, 5, 0);

        for (int i = 0; i < nameLabel.length; i++) {
            panels[i] = new JPanel(new GridBagLayout());
            gbc = new GridBagConstraints();
            gbc.weightx = 0.1;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.insets = WEST_INSETS;
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
        p.add(loginButton);
        p.add(registerButton);
        panel.add(p);

        getContentPane().add(panel, BorderLayout.CENTER);

        pack();

    }

    private void doLogin() {
        String login = loginField.getText();
        String password = passwordField.getText();

        try {
            if (client.login(login, password)) {
                // bring up the user list window
                UserListPane userListPane = new UserListPane(client);
                JFrame frame = new JFrame("List of users");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(400, 600);
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        try {
                            client.logoff();
                            System.out.println(login + " has left.");
                            frame.dispose();
                        } catch (IOException ex) {
                            Logger.getLogger(LoginWindow.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });

                frame.getContentPane().add(userListPane, BorderLayout.CENTER);
                frame.setVisible(true);

                setVisible(false);
            } else {
                // show error message
                JOptionPane.showMessageDialog(this, "Invalid login/password.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        LoginWindow loginWin = new LoginWindow();
        loginWin.setVisible(true);
    }
}
