
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 *
 * @author DVG
 */
public class ServerWorker extends Thread {

    private final Socket clientSocket;
    private final Server server;
    private String login = null;
    private OutputStream outputStream;

    public ServerWorker(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            handleClientSocket();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleClientSocket() throws IOException, InterruptedException {
        InputStream inputStream = clientSocket.getInputStream();
        this.outputStream = clientSocket.getOutputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] tokens = line.split(" ");
            if (tokens != null && tokens.length > 0) {
                String cmd = tokens[0];
                if ("logoff".equals(cmd)) {
                    handleLogoff();
                    break;
                } else if ("login".equalsIgnoreCase(cmd)) {
                    handleLogin(outputStream, tokens);
                } else if ("msg".equalsIgnoreCase(cmd)) {
                    String[] tokensMsg = line.split(" ", 3);
                    handleMessage(tokensMsg);
                } else if ("register".equalsIgnoreCase(cmd)) {
                    handleRegister(outputStream, tokens);
                } else if ("hadread".equalsIgnoreCase(cmd)) {
                    hadReadMsgOffline(tokens);
                } else {
                    String msg = "unknown " + cmd + "\n";
                    outputStream.write(msg.getBytes());
                }
            }
        }
        clientSocket.close();
    }

    private void handleMessage(String[] tokens) throws IOException {
        String sendTo = tokens[1];
        String body = tokens[2];
        boolean hasOnline = false;
        List<ServerWorker> workerList = server.getWorkerList();
        for (ServerWorker worker : workerList) {
            if (sendTo.equalsIgnoreCase(worker.getLogin())) {
                hasOnline = true;
                String outMsg = "msg " + login + " " + body + "\n";
                worker.send(outMsg);
            }
        }
        if (!hasOnline) {
            MyUtils.storeMessageOffline(this.login, sendTo, body);
        }
    }

    private void handleLogoff() throws IOException {
        server.removeWorker(this);
        List<ServerWorker> workerList = server.getWorkerList();

        // send other online users current user's status
        String onlineMsg = "offline " + login + "\n";
        for (ServerWorker worker : workerList) {
            if (!login.equals(worker.getLogin())) {
                worker.send(onlineMsg);
            }
        }
        clientSocket.close();
    }

    public String getLogin() {
        return login;
    }

    private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException {
        if (tokens.length == 3) {
            String login = tokens[1];
            String password = tokens[2];

            if (MyUtils.checkLogin(login, password)) {
                String msg = "ok login\n";
                outputStream.write(msg.getBytes());
                this.login = login;
                List<ServerWorker> workerList = server.getWorkerList();

                //check for offline message
                HashMap<String, ArrayList<String>> checkForOfflineMsg = MyUtils.checkForOfflineMsg(this.login);
                System.out.println(checkForOfflineMsg.size() + " mMMMMMMMM");
                Set<String> keySet = checkForOfflineMsg.keySet();
                for (String infor : keySet) {
                    String[] split = infor.split("\\-");
                    String sender = split[0];

                    ArrayList<String> msgs = checkForOfflineMsg.get(infor);
                    System.out.println("Msgs: " + msgs.size());
                    for (String get : msgs) {
                        System.out.println("Offline msg is: " + get);
                        String msgOffline = "msgoffline " + sender + " " + get + "\n";
                        send(msgOffline);
                    }
                }
                
                //load user list
                ArrayList<String> users = MyUtils.loadAccountRegistration();
                String listUser = "users";

                for (int i = 0; i < users.size(); i++) {
                    String user = users.get(i);
                    if (!user.equals(login)) {
                        listUser = listUser.concat(" " + user);
                    }
                    if (i == users.size() - 1) {
                        listUser = listUser.concat("\n");
                    }
                }
                System.out.println(listUser);
                outputStream.write(listUser.getBytes());

                // send current user all other online logins
                for (ServerWorker worker : workerList) {
                    if (worker.getLogin() != null) {
                        if (!login.equals(worker.getLogin())) {
                            String msg2 = "online " + worker.getLogin() + "\n";
                            send(msg2);
                        }
                    }
                }

                // send other online users current user's status
                String onlineMsg = "online " + login + "\n";
                for (ServerWorker worker : workerList) {
                    if (!login.equals(worker.getLogin())) {
                        worker.send(onlineMsg);
                    }
                }

                
            } else {
                String msg = "error login\n";
                outputStream.write(msg.getBytes());
                System.err.println("Login failed for " + login);
            }
        }
    }

    private void send(String msg) throws IOException {
        if (login != null) {
            try {
                outputStream.write(msg.getBytes());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void handleRegister(OutputStream outputStream, String[] tokens) throws IOException {
        if (tokens.length == 3) {
            String login = tokens[1];
            String pass = tokens[2];

            if (MyUtils.checkRegistration(login, pass)) {
                String msg = "ok register\n";
                outputStream.write(msg.getBytes());

                List<ServerWorker> workerList = server.getWorkerList();

                // send other online users current user's status
                String newUserMsg = "newuser " + login + "\n";
                for (ServerWorker worker : workerList) {
                    worker.send(newUserMsg);
                }
            } else {
                String msg = "error register\n";
                outputStream.write(msg.getBytes());
            }
        }
    }

    private void hadReadMsgOffline(String[] tokens) {
        String sender = tokens[1];
        String msgToBeDeleted = sender + "-" + login;
        MyUtils.deleteMsgHadRead(msgToBeDeleted);
    }
}
