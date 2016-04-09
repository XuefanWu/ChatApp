package edu.stevens.cs522.chat.oneway.server.managers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.List;
import java.util.UUID;

import edu.stevens.cs522.chat.oneway.server.contract.Properties;
import edu.stevens.cs522.chat.oneway.server.entities.Message;
import edu.stevens.cs522.chat.oneway.server.entities.PostMessage;
import edu.stevens.cs522.chat.oneway.server.entities.Register;
import edu.stevens.cs522.chat.oneway.server.entities.Synchronize;
import edu.stevens.cs522.chat.oneway.server.services.RequestService;

/**
 * Created by Xuefan on 3/18/2016.
 */
public class ServiceHelper {

    public static final String REQUEST_TYPE = "request_type";
    public static final int REGISTER_REQUEST = 1;
    public static final int POST_REQUEST = 2;
    public static final String REGISTER = "register";
    public static final int SYNC_REQUEST = 3;
    public static final String POSTMESSAGE = "post_message";
    public static final String SYNCHRONIZE = "synchronize";

    private Context context;

    public ServiceHelper(Context context) {
        this.context = context;
    }

    public void register(String clientName, String url){
        Log.v("ServiceHelper","YES");
        long clientId = 0;
        UUID registrationId = UUID.randomUUID();
        Register register = new Register(clientId, registrationId, clientName, url);
        SharedPreferences settings = context.getSharedPreferences(Properties.SETTING_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(Properties.CLIENT_ID_KEY, clientId);
        editor.putString(Properties.UUID_KEY, registrationId.toString());
        editor.putString(Properties.CLIENT_NAME, clientName);
        editor.putString(Properties.SERVER_URL_KEY, url);
        editor.commit();

        Intent registerRequest = new Intent(context, RequestService.class);
        registerRequest.putExtra(REQUEST_TYPE, REGISTER_REQUEST);
        registerRequest.putExtra(REGISTER, register);
        context.startService(registerRequest);
    }
    public void sendMessage(String message, String url, long time){
        Log.v("ServiceHelpersendMessage", "YES");
        PostMessage postMessage = new PostMessage(url, message, time);
        Log.v("REGISTERID", String.valueOf(postMessage.registrationID));
        Intent postRequest = new Intent(context, RequestService.class);
        postRequest.putExtra(REQUEST_TYPE, POST_REQUEST);
        postRequest.putExtra(POSTMESSAGE, postMessage);
        context.startService(postRequest);
    }

    public void synchronize(String url,long seqnum, MessageManager myManager){
        List<Message> client_data = myManager.search("sync");
        String update_data = "";
        for(Message m : client_data){
            update_data += "&"+m.messageText + "@" + m.time;
        }
        Synchronize synchronize = new Synchronize(url,update_data,seqnum);
        Intent syncRequest = new Intent(context, RequestService.class);
        syncRequest.putExtra(REQUEST_TYPE, SYNC_REQUEST);
        syncRequest.putExtra(SYNCHRONIZE, synchronize);
        context.startService(syncRequest);
    }
}
