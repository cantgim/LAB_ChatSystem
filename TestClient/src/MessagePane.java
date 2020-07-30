
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 *
 * @author DVG
 */
public class MessagePane extends JFrame implements MessageListener {

    private final ChatClient client;
    private final String login;

    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> messageList = new JList<>(listModel);
    private JTextField inputField = new JTextField();

    MessagePane(ChatClient client, String login) {
        setSize(500, 500);
        this.client = client;
        this.login = login;

        this.client.addMessageListener(this);
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.add(new JScrollPane(messageList), BorderLayout.CENTER);
        p.add(inputField, BorderLayout.SOUTH);

        add(p, BorderLayout.CENTER);

        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String text = inputField.getText();
                    if (text != null && !"".equals(text)) {
                        client.msg(login, text);
                        listModel.addElement("You: " + text);
                        inputField.setText("");
                    }
                } catch (IOException ex) {
                    Logger.getLogger(MessagePane.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                doClosing();
            }
        });
    }

    @Override
    public void onlineMessage(String fromLogin, String msgBody) {
        String line = fromLogin + ": " + msgBody;
        listModel.addElement(line);
    }

    public void addElementToList(String msg) {
        listModel.addElement(msg);
    }

    private void doClosing() {
        System.out.println("Remove msg listener");
        this.client.removeMsgPaneFromMap(login);
        this.client.removeMessageListener(this);
    }
}
