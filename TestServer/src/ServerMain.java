

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author DVG
 */
public class ServerMain {

    public static void main(String[] args) {
        int port = 8188;
        Server server = new Server(port);
        server.start();
    }
}
