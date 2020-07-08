
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 *
 * @author DVG
 */
public class MessagePane extends JPanel implements MessageListener {
    
    private final ChatClient client;
    private final String login;
    
    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> messageList = new JList<>(listModel);
    private JTextField inputField = new JTextField();
    
    MessagePane(ChatClient client, String login) {
        this.client = client;
        this.login = login;
        
        client.addMessageListener(this);
        
        setLayout(new BorderLayout());
        add(new JScrollPane(messageList), BorderLayout.CENTER);
        add(inputField, BorderLayout.SOUTH);
        
        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String text = inputField.getText();
                    client.msg(login, text);
                    listModel.addElement(text);
                    inputField.setText("");
                } catch (IOException ex) {
                    Logger.getLogger(MessagePane.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    
    @Override
    public void onlineMessage(String fromLogin, String msgBody) {
        String line = fromLogin + ": " + msgBody;
        listModel.addElement(line);
    }

}
