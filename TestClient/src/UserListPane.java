
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author DVG
 */
public class UserListPane extends JPanel implements UserStatusListener {

    private ChatClient client;
    private JList<String> userListUI;
    private DefaultListModel<String> userListModel;
    private JButton offlineMsgButton;
    private OfflineMessagePane offlineMessagePane;
    int countMsgOffline;

    public UserListPane(ChatClient client) {
        this.client = client;
        this.client.addUserStatusListener(this);

        userListModel = new DefaultListModel<>();
        userListUI = new JList<>(userListModel);
        System.out.println("Before create offline pane");
        if (this.client.hasMsgOffline) {
            offlineMessagePane = new OfflineMessagePane(client);
            System.out.println("has Offline messsage!!!!!");
            System.out.println(System.currentTimeMillis() + " init offline message");
            offlineMessagePane.setVisible(false);
        }

        setLayout(new BorderLayout());
        add(new JScrollPane(userListUI), BorderLayout.CENTER);
        countMsgOffline = this.client.getCountMessageOffline();
        System.out.println("Set count offline msg!");
        offlineMsgButton = new JButton("Offline Message (" + countMsgOffline + ")");
        add(offlineMsgButton, BorderLayout.SOUTH);

        userListUI.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    String[] split = userListUI.getSelectedValue().split(" ");
                    String login = split[0];
                    if (!client.isExistMsgPane(login)) {
                        MessagePane messagePane = new MessagePane(client, login);
                        client.addMsgPaneToMap(login, messagePane);
                        messagePane.setVisible(true);
                    }
                }
            }
        });

        offlineMsgButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (offlineMessagePane != null) {
                    offlineMessagePane.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Empty offline message!");
                }
            }
        });
    }

    @Override
    public void online(String login) {
        System.out.println(login);
        if (userListModel.contains(login)) {
            userListModel.removeElement(login);
        }
        userListModel.addElement(login + " (online)");
    }

    @Override
    public void offline(String login) {
        userListModel.removeElement(login + " (online)");
        userListModel.addElement(login);
    }

    @Override
    public void addNewUser(String login) {
        if (userListModel == null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(UserListPane.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("NULLLLLLLLLL");
        }
        userListModel.addElement(login);
    }

    public void setLabelOfflineMsg(int count) {
        offlineMsgButton.setText("Offline Message (" + count + ")");
    }

    @Override
    public void updateCountMsgOffline() {
        int count = countMsgOffline - 1;
        if (count < 0) {
            count = 0;
        }
        offlineMsgButton.setText("Offline Message (" + count + ")");
        countMsgOffline = count;
    }
}
