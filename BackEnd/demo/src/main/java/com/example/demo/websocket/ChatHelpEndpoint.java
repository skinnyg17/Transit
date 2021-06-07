package com.example.demo.websocket;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ServerEndpoint("/chat/help/{username}/{type}")
@Component
public class ChatHelpEndpoint {
    private static Map<Session, String[]> usernameMap = new HashMap<>();
    private static Map<String, Session> sessionMap = new HashMap<>();

    private final Logger log = LoggerFactory.getLogger(ChatHelpEndpoint.class);

    /**
     * On open.
     *
     * @param session  the session
     * @param username the username
     * @throws IOException the io exception
     */
    @OnOpen
    public void onOpen(
            Session session,
            @PathParam("username") String username,
            @PathParam("type") String type) throws IOException, JSONException {
        log.info("Open");

        usernameMap.put(session, new String[]{username,type});
        sessionMap.put(username, session);

        // broadcast to all customer service
        String message="User:" + username + " has Joined the Chat";
        broadcastCustomerService(message, username, type);
    }

    /**
     * On message.
     *
     * @param session the session
     * @param message the message
     * @throws IOException the io exception
     */
    @OnMessage
    public void onMessage(Session session, String message) throws IOException, JSONException {
        String username = usernameMap.get(session)[0];
        String type = usernameMap.get(session)[1];
        log.info("Entered into Message: Got Message:"+message + " Send by: " + username);

        // self
        JSONObject from = new JSONObject();
        from.put("type","from");
        from.put("user", username);

        if (message.startsWith("@")) // Direct message to a user using the format "@username <message>"
        {
            String destUsername = message.split(" ")[0].substring(1);
            String messageText = message.replaceFirst("@"+destUsername, "");
            from.put("message",messageText);

            if(destUsername.equals("god")){
                // sends to everyone except the from user
                broadcast(messageText,username);
                // sends to the from user
                sendMessagetoSingleUser(username, from.toString());
            }
            else if((type.equals("customerservice") || type.equals("manager") || type.equals("driver")) && destUsername.equals("employee")){
                broadcastEmployee(messageText,username, type);
                sendMessagetoSingleUser(username, from.toString());
            }
            else if((type.equals("customerservice") || type.equals("manager") || type.equals("driver")) && destUsername.equals("customer")){
                broadcastCustomer(messageText,username);
                sendMessagetoSingleUser(username, from.toString());
            }
            else {
                // if customerservice
                //      if username exists
                //          send to user and self
                //      else
                //          return user does not exist to username
                // else
                //      send it to customerservice

//                if(type.equals("customerservice"))
                {

                    if(userExists(destUsername)){

                        // to
                        JSONObject to = new JSONObject();
                        to.put("type","to");
                        to.put("user", username);
                        to.put("message",messageText);


                        sendMessagetoSingleUser(destUsername, to.toString());
                        sendMessagetoSingleUser(username, from.toString());
                    }
                    else{
                        JSONObject errorToSelf = new JSONObject();
                        errorToSelf.put("type","from");
                        errorToSelf.put("user", "system");
                        errorToSelf.put("message","System: User does not exist");
                        sendMessagetoSingleUser(username, errorToSelf.toString());
                    }
                }
//                else{
//                    JSONObject errorToSelf = new JSONObject();
//                    errorToSelf.put("type","from");
//                    errorToSelf.put("user", "system");
//                    errorToSelf.put("message","System: User does not exist");
//                    sendMessagetoSingleUser(username, errorToSelf.toString());
//                }
            }
        }
        else {
            broadcastCustomerService(message, username, type);
            from.put("message",message);
            sendMessagetoSingleUser(username, from.toString());
        }
    }

    private void broadcastEmployee(String messageText, String userName, String type) throws JSONException {
        JSONObject toSend = new JSONObject();
        toSend.put("type","to");
        toSend.put("message", messageText);
        usernameMap.forEach((session, userinfo) -> {
            if((userinfo[1].equals("customerservice") || userinfo[1].equals("manager") || userinfo[1].equals("driver"))&&userinfo[0].equals(userName)==false) {
                synchronized (session) {
                    try {
                        toSend.put("user",userName);
                        toSend.put("userType",type);
                        session.getBasicRemote().sendText(toSend.toString());
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void broadcastCustomer(String messageText, String userName) throws JSONException {
        JSONObject toSend = new JSONObject();
        toSend.put("type","to");
        toSend.put("message", messageText);
        usernameMap.forEach((session, userinfo) -> {
                if(userinfo[1].equals("customer") && userinfo[0].equals(userName)==false) {
                synchronized (session) {
                    try {
                        toSend.put("user",userName);
                        session.getBasicRemote().sendText(toSend.toString());
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * On close.
     *
     * @param session the session
     * @throws IOException the io exception
     */
    @OnClose
    public void onClose(Session session) throws IOException, JSONException {
        log.info("close");

        String username = usernameMap.get(session)[0];
        usernameMap.remove(session);
        sessionMap.remove(username);
    }

    /**
     * On error.
     *
     * @param session   the session
     * @param throwable the throwable
     */
    @OnError
    public void onError(Session session, Throwable throwable)
    {
        log.info("Error has Occured");
    }

    private void sendMessagetoSingleUser(String username, String message)
    {
        try {
            sessionMap.get(username).getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.info("Caught exception while sending message to Session: " + e.getMessage().toString());
            e.printStackTrace();
        }
    }

    private static void broadcast(String message, String userName) throws IOException, JSONException {
        JSONObject toSend = new JSONObject();
        toSend.put("type","to");
        toSend.put("message", message);
        usernameMap.forEach((session, userinfo) -> {
            if(userinfo[0].equals(userName)==false) {
                synchronized (session) {
                    try {
                        toSend.put("user",userName);
                        session.getBasicRemote().sendText(toSend.toString());
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private static void broadcastCustomerService(String message, String userName, String type) throws IOException, JSONException {


        JSONObject toSend = new JSONObject();
        toSend.put("type","to");
        toSend.put("message", message);
        usernameMap.forEach((session, userinfo) -> {
            if(userinfo[1].equals("customerservice")) {
                synchronized (session) {
                    try {
                        toSend.put("user",userName);
                        toSend.put("userType",type);
                        session.getBasicRemote().sendText(toSend.toString());
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private boolean userExists(String userName){
        for (Map.Entry<Session,String[]> entry : usernameMap.entrySet()) {
            if(entry.getValue()[0].equals(userName)){
                return true;
            }
        }
        return false;
    }
}


