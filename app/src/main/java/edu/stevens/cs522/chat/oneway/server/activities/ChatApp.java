package edu.stevens.cs522.chat.oneway.server.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.net.UnknownHostException;
import java.util.Date;

import edu.stevens.cs522.chat.oneway.server.R;
import edu.stevens.cs522.chat.oneway.server.adapters.PeerMessageAdapter;
import edu.stevens.cs522.chat.oneway.server.contract.MessageContract;
import edu.stevens.cs522.chat.oneway.server.contract.Properties;
import edu.stevens.cs522.chat.oneway.server.entities.Message;
import edu.stevens.cs522.chat.oneway.server.entities.Peer;
import edu.stevens.cs522.chat.oneway.server.interfaces.IEntityCreator;
import edu.stevens.cs522.chat.oneway.server.interfaces.IQueryListener;
import edu.stevens.cs522.chat.oneway.server.managers.MessageManager;
import edu.stevens.cs522.chat.oneway.server.managers.PeerManager;
import edu.stevens.cs522.chat.oneway.server.managers.TypedCursor;
import edu.stevens.cs522.chat.oneway.server.receivers.AlarmReceiver;
import edu.stevens.cs522.chat.oneway.server.receivers.ResultReceiverWrapper;
import edu.stevens.cs522.chat.oneway.server.services.ChatReceiverService;
import edu.stevens.cs522.chat.oneway.server.services.ChatSenderService;
import edu.stevens.cs522.chat.oneway.server.services.RequestService;

public class ChatApp extends Activity implements ResultReceiverWrapper.IReceiver {

    final static public String TAG = ChatApp.class.getCanonicalName();

    public final static String EXTRA_MESSAGE
            = "edu.stevens.cs522.chat.oneway.server.activities.ChatApp";


    public static final String CLIENT_NAME_KEY = "client_name";


    private String clientName;
    private EditText messageText;


    public static final String DEST_ADDR = "Dest Address";
    public static final String TXT_MESSAGE = "Txt Message";
    public static final String OUT_RECEIVER = "Result Receiver";
    public static final String FEEDBACK = "result";
    public static final String IN_RECEIVER = "Result Receiver";

    public static final int PEER_LOADER_ID = 1;
    public static final int MESSAGE_LOADER_ID = 2;
    public static final int SYNC_LOADER_ID = 3;

    public static final int RECEIVE_MESSAGE = 99;
    public static final int SEND_MESSAGE = 98;

    static PeerMessageAdapter mAdapter;
    PeerManager peerManager;
    static MessageManager messageManager;
    private Messenger messenger;
    private ResultReceiverWrapper mReceiver;
    public static Context context;
    private PendingIntent pendingIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_app);



        ListView listview = (ListView)findViewById(R.id.msgList);
        mAdapter = new PeerMessageAdapter(this, android.R.layout.simple_list_item_2,null);
        listview.setAdapter(mAdapter);
        peerManager = new PeerManager(this, new IEntityCreator() {
            @Override
            public Peer create(Cursor cursor) {
                return new Peer(cursor);
            }
        },PEER_LOADER_ID);
        messageManager = new MessageManager(this, new IEntityCreator() {
            @Override
            public Message create(Cursor cursor) {
                return new Message(cursor);
            }
        },MESSAGE_LOADER_ID);
        messageManager.executeQuery(
                MessageContract.CONTENT_URI, null, null, null,
                new IQueryListener<Message>() {
                    @Override
                    public void handleResult(TypedCursor<Message> results) {
                        mAdapter.swapCursor(results.getCursor());
                    }

                    @Override
                    public void closeResult() {
                        mAdapter.swapCursor(null);
                    }

                });



//        Intent callingIntent = getIntent();
//        clientName = callingIntent.getExtras().getString(CLIENT_NAME_KEY);


        mReceiver = new ResultReceiverWrapper(new Handler());
        mReceiver.setReceiver(this);


        Intent bindIntent = new Intent(this, ChatSenderService.class);
        bindService(bindIntent, connection, Context.BIND_AUTO_CREATE);


        Intent startIntent = new Intent(this, ChatReceiverService.class);
        startIntent.putExtra(IN_RECEIVER,mReceiver);
        startService(startIntent);
        context = this;
        auto_sync();
    }

    public void onClick(View v) throws UnknownHostException {

        SharedPreferences sharedPreferences = getSharedPreferences(Properties.SETTING_PREFERENCES, MODE_PRIVATE);
        clientName = sharedPreferences.getString(Properties.CLIENT_NAME, Properties.DEFAULT_NAME);


        String sendData ;  // Combine sender and message text; default encoding is UTF-8

        messageText = (EditText)findViewById(R.id.message_text);
        String message = messageText.getText().toString();
        sendData = clientName+":"+message;
        Date date = new Date(System.currentTimeMillis());


        android.os.Message messagee = android.os.Message.obtain();
        Bundle args = new Bundle();
        args.putString(TXT_MESSAGE, sendData);
        args.putLong(MessageContract.TIME, date.getTime());
        args.putParcelable(OUT_RECEIVER, mReceiver);
        messagee.setData(args);
        try {
            messenger.send(messagee);
        } catch (RemoteException e) {
        }
        messageText.setText("");
    }

    private ServiceConnection connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder binder) {
            // Called when the connection is made.
            messenger = new Messenger(binder);
        }

        public void onServiceDisconnected(ComponentName className) {
            // Received when the service unexpectedly disconnects.
            messenger = null;
        }
    };

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        if(resultCode == SEND_MESSAGE){
            String result = resultData.getString(FEEDBACK);
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
        }
        if(resultCode == RECEIVE_MESSAGE){
            ChatApp.refresh();
            String result = resultData.getString(FEEDBACK);
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_app, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.sender:
                Intent addIntent = new Intent(this, PeerListActivity.class);
                startActivity(addIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onDestroy(){
        unbindService(connection);
        Intent stopIntent = new Intent(this, ChatReceiverService.class);
        stopService(stopIntent);
        super.onDestroy();
    }

    public void auto_sync(){
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 30*1000;
        long timeToRefresh = SystemClock.elapsedRealtime()+ 10000;
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, timeToRefresh, interval, pendingIntent);
        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
        Log.v("AUTOSYNC","YES");
    }

    public static void refresh(){
        if(messageManager!= null) {
            messageManager.reexecuteQuery(
                    MessageContract.CONTENT_URI, null, null, null,
                    new IQueryListener<Message>() {
                        @Override
                        public void handleResult(TypedCursor<Message> results) {
                            mAdapter.swapCursor(results.getCursor());
                        }
                        @Override
                        public void closeResult() {
                            mAdapter.swapCursor(null);
                        }

                    });

        }
    }

    public static class Receiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            ChatApp.refresh();
            Toast.makeText(context,"Synchronize with server",Toast.LENGTH_SHORT).show();
        }
    }

    Receiver receiver = new Receiver();
    @Override
    public void onStart() {
        IntentFilter intentFilter = new IntentFilter(RequestService.NEW_MESSAGE_BROADCAST);
        registerReceiver(receiver, intentFilter);
        super.onStart();
    }

    @Override
    public void onStop() {
        unregisterReceiver(receiver);
        super.onStop();
    }

}
