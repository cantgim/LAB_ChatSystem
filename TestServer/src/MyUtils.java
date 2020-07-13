
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
        HashMap<String, String> accountsMap = new HashMap<>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("accounts.txt"));
            String line = reader.readLine();
            while (line != null) {
                String[] split = line.split(" ");
                accountsMap.put(split[0], split[1]);
                // read next line
                line = reader.readLine();
            }
            if (accountsMap.containsKey(login)) {
                String value = accountsMap.get(login);
                if (value.equals(password)) {
                    return true;
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    static void storeMessageOffline(String sender, String receiver, String body) {
        try {
            String msgToBeSaved = sender + "-" + receiver + " " + body + "\n";
            FileOutputStream fos = new FileOutputStream("offline_msg.txt");
            fos.write(msgToBeSaved.getBytes());

            fos.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MyUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MyUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static ArrayList<String> loadAccountRegistration() {
        ArrayList<String> accounts = new ArrayList<>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("accounts.txt"));
            String line = reader.readLine();
            while (line != null) {
                System.out.println(line);
                String[] split = line.split(" ");
                accounts.add(split[0]);
                // read next line
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return accounts;
    }

    static boolean checkRegistration(String login, String pass) {
        ArrayList<String> accounts = loadAccountRegistration();
        if (!accounts.contains(login)) {
            saveAccountToFile(login, pass);
            return true;
        }
        return false;
    }

    static void saveAccountToFile(String login, String pass) {
        try {
            String str = login + " " + pass + "\n";
            FileOutputStream fos = new FileOutputStream("accounts.txt", true);
            fos.write(str.getBytes());

            fos.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MyUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MyUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static HashMap<String, ArrayList<String>> checkForOfflineMsg() {
        HashMap<String, ArrayList<String>> msgOffline = new HashMap<>();
        ArrayList<String> msg = new ArrayList<>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("offline_msg.txt"));
            String line = reader.readLine();
            while (line != null) {
                System.out.println(line);
                String[] split = line.split(" ", 2);
                if (msgOffline.containsKey(split[0])) {
                    ArrayList<String> get = msgOffline.get(split[0]);
                    get.add(split[1]);
                } else {
                    msg.add(split[1]);
                    msgOffline.put(split[0], msg);
                }
                // read next line
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(MyUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
