package edu.stevens.cs522.chat.oneway.server.services;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;

import edu.stevens.cs522.chat.oneway.server.activities.ChatApp;
import edu.stevens.cs522.chat.oneway.server.contract.MessageContract;
import edu.stevens.cs522.chat.oneway.server.contract.Properties;
import edu.stevens.cs522.chat.oneway.server.entities.Peer;
import edu.stevens.cs522.chat.oneway.server.interfaces.IEntityCreator;
import edu.stevens.cs522.chat.oneway.server.managers.MessageManager;
import edu.stevens.cs522.chat.oneway.server.managers.PeerManager;
import edu.stevens.cs522.chat.oneway.server.managers.ServiceHelper;

public class ChatSenderService extends Service{


    private Looper looper;
    private MessageHandler handler;
    private Messenger messenger;
    final static public String ServiceStartArg = "ServiceStartArguments";
    PeerManager peerManager;
    MessageManager messageManager;
    private static final int PEER_LOADER_ID = 1;
    private static final int MESSAGE_LOADER_ID = 2;



    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread(ServiceStartArg, Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        looper = thread.getLooper();
        handler = new MessageHandler(looper);
        messenger = new Messenger(handler);
        messageManager = new MessageManager(this, new IEntityCreator() {
            @Override
            public edu.stevens.cs522.chat.oneway.server.entities.Message create(Cursor cursor) {
                return new edu.stevens.cs522.chat.oneway.server.entities.Message(cursor);
            }
        },MESSAGE_LOADER_ID);
        peerManager = new PeerManager(this, new IEntityCreator() {
            @Override
            public Peer create(Cursor cursor) {
                return new Peer(cursor);
            }
        },PEER_LOADER_ID);
        super.onCreate();
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = handler.obtainMessage();
        msg.arg1 = startId;
        handler.sendMessage(msg);
        // If we get killed, after returning from here, restart
        return START_STICKY;
    }


    class MessageHandler extends Handler {

        public MessageHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message message) {
            Bundle data = message.getData();
            String address = data.getString(ChatApp.DEST_ADDR);
            SharedPreferences sharedPreferences
                    = getApplicationContext().getSharedPreferences(Properties.SETTING_PREFERENCES, Context.MODE_PRIVATE);
            long seqnum = sharedPreferences.getLong(Properties.SEQUENCE_NUMBER, Properties.DEFAULT_SEQNUM);

            String txt_message = data.getString(ChatApp.TXT_MESSAGE);
            String[] result = txt_message.split(":");
            String sender = result[0], messageTxt = result[1];
            SharedPreferences settings = ChatApp.context.getSharedPreferences(Properties.SETTING_PREFERENCES, Context.MODE_PRIVATE);
            String url = settings.getString(Properties.SERVER_URL_KEY, Properties.DEFAULT_URL);
            long time = data.getLong(MessageContract.TIME);

            //serviceHelper.synchronize(url,seqnum,messageManager);
            String name = settings.getString(Properties.CLIENT_NAME, "");
            Peer peer = new Peer(name, url,8000);
            edu.stevens.cs522.chat.oneway.server.entities.Message message_txt = new edu.stevens.cs522.chat.oneway.server.entities.Message(messageTxt,name);
            peerManager.persist(peer);
            messageManager.persist(peer,message_txt);
            ServiceHelper serviceHelper = new ServiceHelper(ChatApp.context);
            serviceHelper.synchronize(url, seqnum, messageManager);
        }

    }
}
