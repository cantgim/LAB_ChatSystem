
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author DVG
 */
public class OfflineMessagePane extends JPanel{

    private JList<String> msgOfflineList;
    private DefaultListModel<String> msgListModel;
    private final ChatClient client;

    public OfflineMessagePane(ChatClient client) {
        this.client = client;
        
        msgListModel = new DefaultListModel<>();
        msgOfflineList = new JList<>(msgListModel);
    }
}
