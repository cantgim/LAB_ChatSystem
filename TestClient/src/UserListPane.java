
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 *
 * @author DVG
 */
public class UserListPane extends JPanel implements UserStatusListener {

    private ChatClient client;
    private JList<String> userListUI;
    private DefaultListModel<String> userListModel;
    private JButton offlineMsgButton;

    public UserListPane(ChatClient client) {
        this.client = client;
        this.client.addUserStatusListener(this);

        offlineMsgButton = new JButton("Offline Message");
        userListModel = new DefaultListModel<>();
        userListUI = new JList<>(userListModel);
        setLayout(new BorderLayout());
        add(new JScrollPane(userListUI), BorderLayout.CENTER);
        add(offlineMsgButton, BorderLayout.SOUTH);

        userListUI.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    String[] split = userListUI.getSelectedValue().split(" ");
                    String login = split[0];
                    //if (!client.isVisibleMsgPane()) {
                    if (!client.isExistMsgPane(login)) {
                        MessagePane messagePane = new MessagePane(client, login);
                        client.addMsgPaneToMap(login, messagePane);
                        //client.setIsVisibleMsgPane(true);
                        messagePane.setVisible(true);
                    }

                    //}
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
        userListModel.addElement(login);
    }
}
