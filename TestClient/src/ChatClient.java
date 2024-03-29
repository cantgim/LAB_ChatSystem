
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author DVG
 */
public class ChatClient {

    private final String serverName;
    private final int serverPort;
    private Socket socket;
    private InputStream serverIn;
    private OutputStream serverOut;
    private BufferedReader bufferedIn;
     boolean hasMsgOffline = false;
    private String login = null;

    private ArrayList<UserStatusListener> userStatusListeners = new ArrayList<>();
    private ArrayList<MessageListener> messageListeners = new ArrayList<>();
    private ArrayList<OfflineMessageListener> offlineMessageListeners = new ArrayList<>();
    private HashMap<String, MessagePane> msgPaneMap = new HashMap<>();
    //private ArrayList<String> countOfflineMsg = new ArrayList<>();
    private HashMap<String, ArrayList<String>> msgOfflineContainer = new HashMap<>();

    public ChatClient(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    public void msg(String sendTo, String msgBody) throws IOException {
        String cmd = "msg " + sendTo + " " + msgBody + "\n";
        serverOut.write(cmd.getBytes());
    }

    public boolean login(String login, String password) throws IOException {
        String cmd = "login " + login + " " + password + "\n";
        serverOut.write(cmd.getBytes());
        this.login = login;
        String response = bufferedIn.readLine();
        System.out.println("Response Line:" + response);

        if ("ok login".equalsIgnoreCase(response)) {
            startMessageReader();
            return true;
        } else {
            return false;
        }
    }

    public void logoff() throws IOException {
        String cmd = "logoff\n";
        serverOut.write(cmd.getBytes());
    }

    public boolean register(String login, String password) throws IOException {
        String cmd = "register " + login + " " + password + "\n";
        serverOut.write(cmd.getBytes());
        String response = bufferedIn.readLine();
        System.out.println("Response Line: " + response);
        if ("ok register".equalsIgnoreCase(response)) {
            return true;
        } else {
            return false;
        }
    }

    private void startMessageReader() {
        Thread t = new Thread() {
            @Override
            public void run() {
                readMessageLoop();
            }
        };
        t.start();
    }

    private synchronized void readMessageLoop() {
        while (userStatusListeners.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            String line;
            while ((line = bufferedIn.readLine()) != null) {
                String[] tokens = line.split(" ");
                if (tokens != null && tokens.length > 0) {
                    String cmd = tokens[0];
                    if ("online".equalsIgnoreCase(cmd)) {
                        handleOnline(tokens);
                    } else if ("offline".equalsIgnoreCase(cmd)) {
                        handleOffline(tokens);
                    } else if ("msg".equalsIgnoreCase(cmd)) {
                        String[] tokensMsg = line.split(" ", 3);
                        handleMessage(tokensMsg);
                    } else if ("newuser".equalsIgnoreCase(cmd)) {
                        handleNewUser(tokens);
                    } else if ("users".equalsIgnoreCase(cmd)) {
                        handleListUsers(tokens);
                    } else if ("msgoffline".equalsIgnoreCase(cmd)) {
                        hasMsgOffline = true;
                        System.out.println("You got a offline msg..");
                        String[] tokensMsg = line.split(" ", 3);
                        
                        handleMsgOffline(tokensMsg);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleMessage(String[] tokensMsg) {
        String login = tokensMsg[1];
        System.out.println("Message from " + login);
        String msgBody = tokensMsg[2];
        System.out.println(msgBody);
        if (messageListeners.isEmpty()) {
            System.out.println("No msg panel for " + this.login);
            if (!isExistMsgPane(login)) {
                MessagePane messagePane = new MessagePane(this, login);
                msgPaneMap.put(login, messagePane);
                messagePane.setVisible(true);
            }
        }
        for (MessageListener listener : messageListeners) {
            listener.onlineMessage(login, msgBody);
        }
    }

    private void handleOffline(String[] tokens) {
        String login = tokens[1];
        for (UserStatusListener listener : userStatusListeners) {
            listener.offline(login);
        }
    }

    private void handleOnline(String[] tokens) {
        String login = tokens[1];
        System.out.println("Tokens are: " + Arrays.toString(tokens));
        System.out.println(userStatusListeners.size() + " is in online.");
        for (UserStatusListener listener : userStatusListeners) {
            listener.online(login);
        }
    }

    private void handleNewUser(String[] tokens) {
        String login = tokens[1];
        for (UserStatusListener listener : userStatusListeners) {
            listener.addNewUser(login);
        }
    }

    public boolean isHasMsgOffline() {
        return hasMsgOffline;
    }

    private synchronized void handleMsgOffline(String[] tokens) {
        if (offlineMessageListeners.isEmpty()) {
                System.out.println("Empty!!!!!");
            try {
                System.out.println("Before wait");
                wait();
                System.out.println("After wait");
            } catch (InterruptedException ex) {
                Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        String sender = tokens[1];
        String msgBody = tokens[2];

        if (msgOfflineContainer.containsKey(sender)) {
            msgOfflineContainer.get(sender).add(msgBody);
            for (OfflineMessageListener listener : offlineMessageListeners) {
                ArrayList<String> get = msgOfflineContainer.get(sender);
                listener.addMsgOffline(sender, get.size());
            }
        } else {
            msgOfflineContainer.put(sender, new ArrayList<>());
            msgOfflineContainer.get(sender).add(msgBody);
            for (OfflineMessageListener listener : offlineMessageListeners) {
                ArrayList<String> get = msgOfflineContainer.get(sender);
                listener.addMsgOffline(sender, get.size());
            }
        }
        //System.out.println(System.currentTimeMillis() + " offline msg come");
    }

    private void handleListUsers(String[] tokens) {
        for (int i = 1; i < tokens.length; i++) {
            String login = tokens[i];
            for (UserStatusListener listener : userStatusListeners) {
                listener.addNewUser(login);
            }
        }
    }

    public void updateCountMsgOffline() {
        for (UserStatusListener userStatusListener : userStatusListeners) {
            userStatusListener.updateCountMsgOffline();
        }
    }

    public boolean connect() {
        try {
            this.socket = new Socket(serverName, serverPort);
            System.out.println("Client port is " + socket.getLocalPort());
            this.serverOut = socket.getOutputStream();
            this.serverIn = socket.getInputStream();
            this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getCountMessageOffline() {
        return msgOfflineContainer.keySet().size();
    }

    public HashMap<String, ArrayList<String>> getMsgOfflineContainer() {
        return msgOfflineContainer;
    }

    public boolean isExistMsgPane(String login) {
        return msgPaneMap.containsKey(login);
    }

    public void addMsgPaneToMap(String login, MessagePane messagePane) {
        this.msgPaneMap.put(login, messagePane);
    }

    public void removeMsgPaneFromMap(String login) {
        this.msgPaneMap.remove(login);
    }

    public synchronized void addUserStatusListener(UserStatusListener listener) {
        userStatusListeners.add(listener);
        notify();
        System.out.println(userStatusListeners.size());
    }

    public void removeUserStatusListener(UserStatusListener listener) {
        userStatusListeners.remove(listener);
    }

    public void addMessageListener(MessageListener listener) {
        messageListeners.add(listener);

    }

    public void removeMessageListener(MessageListener listener) {
        messageListeners.remove(listener);
    }

    public synchronized void addOfflineMessageListener(OfflineMessageListener listener) {
        offlineMessageListeners.add(listener);
        System.out.println("Before notify");
        notify();
        System.out.println("After notify");
    }

    public void removeOfflineMessageListener(OfflineMessageListener listener) {
        offlineMessageListeners.remove(listener);
    }

    public void hasReadOfflineMsg(String login) throws IOException {
        String msg = "hadread " + login + "\n";
        serverOut.write(msg.getBytes());
    }
}
