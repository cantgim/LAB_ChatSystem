
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
            String line = null;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split(" ");
                accountsMap.put(split[0], split[1]);
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
            FileOutputStream fos = new FileOutputStream("offline_msg.txt", true);
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
            String line = null;
            while ((line =reader.readLine()) != null) {
                System.out.println(line);
                String[] split = line.split(" ");
                accounts.add(split[0]);
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

    static HashMap<String, ArrayList<String>> checkForOfflineMsg(String login) {
        HashMap<String, ArrayList<String>> msgOffline = new HashMap<>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("offline_msg.txt"));
            String line = null;
            while ((line =reader.readLine()) != null) {
                String[] split = line.split(" ", 2);
                String infor = split[0];
                String[] split1 = infor.split("\\-");
                if (split1[1].equalsIgnoreCase(login)) {
                    if (msgOffline.containsKey(split[0])) {
                        ArrayList<String> get = msgOffline.get(split[0]);
                        get.add(split[1]);
                    } else {
                        ArrayList<String> msg = new ArrayList<>();
                        msg.add(split[1]);
                        msgOffline.put(split[0], msg);
                    }
                }
                System.out.println(Arrays.toString(split) + " " + split.length);
                // read next line
            }
            reader.close();

        } catch (IOException ex) {
            Logger.getLogger(MyUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return msgOffline;
    }

    static void deleteMsgHadRead(String msgToBeDeleted) {
        BufferedReader reader;
        BufferedWriter writer;
        try {
            File originalFile = new File("offline_msg.txt");
            File replaceFile = new File("temp.txt");
            reader = new BufferedReader(new FileReader(originalFile));
            writer = new BufferedWriter(new FileWriter(replaceFile));

            String line;
            while ((line = reader.readLine()) != null) {
                String trimLine = line.trim();
                String[] split = trimLine.split(" ", 2);
                if (split[0].equalsIgnoreCase(msgToBeDeleted)) {
                    System.out.println("OKeeee delete");
                    continue;
                }
                writer.write(line + "\n");
            }

            writer.close();
            reader.close();
            boolean delete = originalFile.delete();
            if (delete) {
                replaceFile.renameTo(originalFile);
            }else{
                System.out.println("Can't delete file");
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
