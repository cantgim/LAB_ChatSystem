
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author DVG
 */
public class OfflineMessagePane extends JFrame implements OfflineMessageListener {

    private JList<String> msgOfflineList;
    private DefaultListModel<String> msgListModel;
    private final ChatClient client;
    HashMap<String, ArrayList<String>> msgOfflines;

    public OfflineMessagePane(ChatClient client) {
        setSize(300, 400);
        this.client = client;
        System.out.println("Before add listener");
        this.client.addOfflineMessageListener(this);
        System.out.println("After add");
        msgOfflines = this.client.getMsgOfflineContainer();

        msgListModel = new DefaultListModel<>();
        msgOfflineList = new JList<>(msgListModel);
        setLayout(new BorderLayout());
        add(new JScrollPane(msgOfflineList), BorderLayout.CENTER);

        msgOfflineList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    String[] split = msgOfflineList.getSelectedValue().split(" ");
                    String login = split[0];
                    if (!client.isExistMsgPane(login)) {
                        MessagePane messagePane = new MessagePane(client, login);
                        client.addMsgPaneToMap(login, messagePane);
                        ArrayList<String> get = msgOfflines.get(login);
                        for (String msg : get) {
                            messagePane.addElementToList(login + ": " + msg);
                        }
                        removeMsgOffline(login);
                        try {
                            client.hasReadOfflineMsg(login);
                        } catch (IOException ex) {
                            Logger.getLogger(OfflineMessagePane.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        client.updateCountMsgOffline();
                        client.addMsgPaneToMap(login, messagePane);
                        messagePane.setVisible(true);
                    }
                }
            }
        });
    }

    @Override
    public void addMsgOffline(String login, int count) {
        int curOfMsg = count - 1;
        System.out.println("I came here");
        if (msgListModel.contains(login + " (" + curOfMsg + ")")) {
            System.out.println(login + " (" + curOfMsg + ")");
            msgListModel.removeElement(login + " (" + curOfMsg + ")");
        }
        msgListModel.addElement(login + " (" + count + ")");
    }

    private void removeMsgOffline(String login) {
        String[] array = new String[msgListModel.size()];
        msgListModel.copyInto(array);
        for (int i = 0; i < array.length; i++) {
            String string = array[i];
            if (string.contains(login)) {
                msgListModel.removeElementAt(i);
            }
        }
    }
}
