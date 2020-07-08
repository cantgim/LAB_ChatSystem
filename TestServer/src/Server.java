
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author DVG
 */
public class Server extends Thread {

    private final int serverPort;
    private ArrayList<ServerWorker> workerList = new ArrayList<>();

    Server(int port) {
        serverPort = port;
    }

    public ArrayList<ServerWorker> getWorkerList() {
        return workerList;
    }
    
    
    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);
            while (true) {
                System.out.println("Waiting connection...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted a connection from " + clientSocket);
                ServerWorker worker = new ServerWorker(this,clientSocket);
                workerList.add(worker);
                worker.start();
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void removeWorker(ServerWorker aThis) {
        workerList.remove(aThis);
    }
}
