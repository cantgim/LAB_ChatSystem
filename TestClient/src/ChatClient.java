
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
    private boolean isVisibleMsgPane = false;

    private ArrayList<UserStatusListener> userStatusListeners = new ArrayList<>();
    private ArrayList<MessageListener> messageListeners = new ArrayList<>();
    private HashMap<String, MessagePane> msgPaneMap = new HashMap<>();

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
        String msgBody = tokensMsg[2];
        System.out.println(msgBody);
        if (messageListeners.isEmpty()) {
            System.out.println("No msg panel!");
            if (isExistMsgPane(login)) {
                MessagePane messagePane = new MessagePane(this, login);
                msgPaneMap.put(login, messagePane);
                //isVisibleMsgPane = true;
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

    private void handleMsgOffline(String[] tokens) {
        String sender = tokens[1];
        String msgBody = tokens[2];
        System.out.println(msgBody);

        
    }

    private void handleListUsers(String[] tokens) {
        for (int i = 1; i < tokens.length; i++) {
            String login = tokens[i];
            for (UserStatusListener listener : userStatusListeners) {
                listener.addNewUser(login);
            }
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

    public boolean isExistMsgPane(String login) {
        return msgPaneMap.containsKey(login);
    }

    public void addMsgPaneToMap(String login, MessagePane messagePane) {
        this.msgPaneMap.put(login, messagePane);
    }

    public void removeMsgPaneFromMap(String login) {
        this.msgPaneMap.remove(login);
    }

    public boolean isVisibleMsgPane() {
        return isVisibleMsgPane;
    }

    public void setIsVisibleMsgPane(boolean isVisibleMsgPane) {
        this.isVisibleMsgPane = isVisibleMsgPane;
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
}
