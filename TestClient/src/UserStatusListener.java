 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author DVG
 */
public interface UserStatusListener {
    public void online(String login);
    public void offline(String login);
    public void addNewUser(String login);
    public void updateCountMsgOffline();
}
