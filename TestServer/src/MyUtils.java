
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author DVG
 */
public class MyUtils {
    
    static boolean checkLogin(String login, String password) {
        if ((login.equals("guest") && password.equals("guest"))
                || (login.equals("jim") && password.equals("jim"))
                || (login.equals("oola") && password.equals("oola"))) {
            return true;
        }
        return false;
    }
    
    static void storeMessageOffline(String sender, String receiver, String body) {
        try {
            String msgToBeSaved = "sender=" + sender + "&receiver=" + receiver + "&msg=" + body+"\n";
            FileOutputStream fos = new FileOutputStream("offline_msg.txt");
            fos.write(msgToBeSaved.getBytes());
            
            fos.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MyUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MyUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
