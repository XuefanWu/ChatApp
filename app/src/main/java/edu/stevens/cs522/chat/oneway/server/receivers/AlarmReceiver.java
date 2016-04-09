package edu.stevens.cs522.chat.oneway.server.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

import edu.stevens.cs522.chat.oneway.server.activities.ChatApp;
import edu.stevens.cs522.chat.oneway.server.contract.Properties;
import edu.stevens.cs522.chat.oneway.server.entities.Message;
import edu.stevens.cs522.chat.oneway.server.entities.Synchronize;
import edu.stevens.cs522.chat.oneway.server.interfaces.IEntityCreator;
import edu.stevens.cs522.chat.oneway.server.managers.MessageManager;
import edu.stevens.cs522.chat.oneway.server.managers.ServiceHelper;
import edu.stevens.cs522.chat.oneway.server.services.RequestService;

/**
 * Created by Xuefan on 4/2/2016.
 */
public class AlarmReceiver extends BroadcastReceiver {
    private MessageManager messageManager = null;

    @Override
    public void onReceive(Context context, Intent intent) {
//        myManager = new MessageManager(context, new IEntityCreator<Message>() {
//            @Override
//            public Message create(Cursor cursor) {
//                Message message_t = new Message(cursor);
//                return message_t;
//            }
//        }, ChatApp.MY_LOADER_ID);

        messageManager = new MessageManager(context, new IEntityCreator() {
            @Override
            public Message create(Cursor cursor) {
                return new Message(cursor);
            }
        },ChatApp.MESSAGE_LOADER_ID);

        SharedPreferences sharedPreferences
                = context.getSharedPreferences(Properties.SETTING_PREFERENCES, Context.MODE_PRIVATE);
        long seqnum = sharedPreferences.getLong(Properties.SEQUENCE_NUMBER, Properties.DEFAULT_SEQNUM);
        String url = sharedPreferences.getString(Properties.SERVER_URL_KEY, Properties.DEFAULT_URL);

        // Todo: synchronize
        List<Message> client_data = messageManager.search("sync");
        String update_data = "";
        for(Message instance : client_data){
            update_data += "&"+instance.messageText + "@" + instance.time;
        }
        Synchronize synchronize = new Synchronize(url,update_data,seqnum);
        Intent syncRequest = new Intent(context, RequestService.class);
        syncRequest.putExtra(ServiceHelper.REQUEST_TYPE, ServiceHelper.SYNC_REQUEST);
        syncRequest.putExtra(ServiceHelper.SYNCHRONIZE, synchronize);
        context.startService(syncRequest);

        // For our recurring task, we'll just display a message
        Toast.makeText(context, "Auto Sync : " + new Date().toString(), Toast.LENGTH_SHORT).show();
    }
}