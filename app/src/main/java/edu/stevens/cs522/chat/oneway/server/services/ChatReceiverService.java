package edu.stevens.cs522.chat.oneway.server.services;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.os.StrictMode;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import edu.stevens.cs522.chat.oneway.server.R;
import edu.stevens.cs522.chat.oneway.server.activities.ChatApp;
import edu.stevens.cs522.chat.oneway.server.entities.Message;
import edu.stevens.cs522.chat.oneway.server.entities.Peer;
import edu.stevens.cs522.chat.oneway.server.interfaces.IEntityCreator;
import edu.stevens.cs522.chat.oneway.server.managers.MessageManager;
import edu.stevens.cs522.chat.oneway.server.managers.PeerManager;

public class ChatReceiverService extends Service {


    final static public String TAG = ChatApp.class.getCanonicalName();

    /*
     * Socket used both for sending and receiving
     */
    public final static String EXTRA_MESSAGE
            = "edu.stevens.cs522.chat.oneway.server.activities.ChatServer";
    private DatagramSocket serverSocket;

    public static final String NEW_MESSAGE_BROADCAST =
            "edu.stevens.cs522.chat.NewMessageBroadcast";

    /*
     * True as long as we don't get socket errors
     */
    private boolean socketOK = true;
    static final private int SENDER_REQUEST = 1;

    private static final int PEER_LOADER_ID = 1;
    private static final int MESSAGE_LOADER_ID = 2;

    PeerManager peerManager;
    MessageManager messageManager;
    private ResultReceiver mReceiver;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mReceiver = intent.getParcelableExtra(ChatApp.IN_RECEIVER);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            int port = Integer.parseInt(this.getString(R.string.target_port_default));
            serverSocket = new DatagramSocket(port);
        } catch (Exception e) {
            Log.e(TAG, "Cannot open socket" + e.getMessage());
        }

        messageManager = new MessageManager(this, new IEntityCreator() {
            @Override
            public Message create(Cursor cursor) {
                return new Message(cursor);
            }
        },MESSAGE_LOADER_ID);
        peerManager = new PeerManager(this, new IEntityCreator() {
            @Override
            public Peer create(Cursor cursor) {
                return new Peer(cursor);
            }
        },PEER_LOADER_ID);
        AsyncTask<Void,Void,Void> task = new ReceiveMessageTask();
        task.execute();
        return START_STICKY;
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class ReceiveMessageTask extends AsyncTask<Void,Void,Void>{


        @Override
        protected Void doInBackground(Void... voids) {

//            new Thread(new Runnable() {
//                public void run() {
//                    Looper.prepare();
//                    Log.d(TAG, "Looper.prepare() ok");
//                    while(true){
//                        receiveMessage();
//
//                        }
//                }
//            }).start();
           while (true) {
               receiveMessage();
            Bundle data = new Bundle();
            data.putString(ChatApp.FEEDBACK, "Get new message");
            mReceiver.send(ChatApp.RECEIVE_MESSAGE, data);

           }

        }

//        @Override
//        protected void onPostExecute(Void unused) {
//            mActivity = ThisActivity.this;
//
//            mActivity.runOnUiThread(new Runnable() {
//                public void run() {
//                    new ReceiveMessageTask().execute();
//        }
    }

    public void receiveMessage(){
        byte[] receiveData = new byte[1024];

        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

        try {

            serverSocket.receive(receivePacket);
            Log.i(TAG, "Received a packet");

            InetAddress sourceIPAddress = receivePacket.getAddress();
            Log.i(TAG, "Source IP Address: " + sourceIPAddress);

            byte data[] = receivePacket.getData();
            String str = new String(data, "UTF-8");

            String[] result = str.split(":");
            String sender = result[0], messageTxt = result[1];
            Log.v("SENDDDDDDDDER",sender);
            Log.v("MESSAGEEEEE",messageTxt);
            int port_num = Integer.parseInt(String.valueOf(receivePacket.getPort()));
            Log.v("PORTTTTTT",String.valueOf(port_num));
            Peer peer = null;
            //Peer peer = new Peer(sender,sourceIPAddress,port_num);
            Message message_txt = new Message(messageTxt, sender);
            peerManager.persistAsync(peer);
            messageManager.persistAsync(peer,message_txt);



        } catch (Exception e) {
            Log.e(TAG, "Problems receiving packet: " + e.getMessage());
            socketOK = false;
        }
    }
}
