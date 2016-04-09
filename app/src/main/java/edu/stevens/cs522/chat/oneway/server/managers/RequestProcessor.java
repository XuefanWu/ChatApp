package edu.stevens.cs522.chat.oneway.server.managers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import java.io.IOException;

import edu.stevens.cs522.chat.oneway.server.activities.ChatApp;
import edu.stevens.cs522.chat.oneway.server.activities.SettingActivity;
import edu.stevens.cs522.chat.oneway.server.contract.Properties;
import edu.stevens.cs522.chat.oneway.server.entities.Message;
import edu.stevens.cs522.chat.oneway.server.entities.Peer;
import edu.stevens.cs522.chat.oneway.server.entities.Register;
import edu.stevens.cs522.chat.oneway.server.entities.Synchronize;
import edu.stevens.cs522.chat.oneway.server.interfaces.IEntityCreator;

/**
 * Created by Xuefan on 3/18/2016.
 */
public class RequestProcessor {
    private RestMethod restMethod;
    private Context context;
    private PeerManager peerManager;
    private MessageManager messageManager;

    public RequestProcessor(Context context){
        restMethod = new RestMethod();
        this.context = context;
        peerManager = new PeerManager(this.context, new IEntityCreator() {
            @Override
            public Peer create(Cursor cursor) {
                return new Peer(cursor);
            }
        },ChatApp.SYNC_LOADER_ID);
        messageManager = new MessageManager(this.context, new IEntityCreator() {
            @Override
            public Message create(Cursor cursor) {
                return new Message(cursor);
            }
        },ChatApp.SYNC_LOADER_ID);
    }
    public void perform(Register register) throws IOException {
        Log.v("RequestProcess","YES");
        Response response = restMethod.perform(register);
        if(response.isValid()){
            JsonReader jsonReader = response.getJsonReader();
            jsonReader.beginObject();
            if("id".equals(jsonReader.nextName())){
                long id = jsonReader.nextLong();
                SharedPreferences settings = context.getSharedPreferences(Properties.SETTING_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putLong(Properties.CLIENT_ID_KEY, id);
                editor.commit();

            }
            jsonReader.endObject();
            jsonReader.close();
            Intent intent = new Intent(SettingActivity.context, ChatApp.class);
            intent.putExtra(Properties.CLIENT_NAME_KEY, register.getClientName());
            intent.putExtra(Properties.SERVER_URL_KEY, register.getUrl());
            SettingActivity.context.startActivity(intent);
        }
        Intent broadcast = new Intent("android.action.intent.PROVIDER_CHANGED");
        broadcast.putExtra(Properties.RESPONSE_CODE, response.getResponseCode());
        //broadcast.putExtra(Properties.RESPONSE_CODE, "hahahahahahahah");
        context.sendBroadcast(broadcast);

    }


    public void perform(Synchronize synchronize) throws IOException{
        StreamingResponse streamingResponse = restMethod.perform(synchronize);
        Response response = streamingResponse.getResponse();
        if(response.isValid()){
            JsonReader reader = response.getJsonReader();
            SharedPreferences sharedPreferences
                    = context.getSharedPreferences(Properties.SETTING_PREFERENCES, Context.MODE_PRIVATE);
            long max_sequenceNum = sharedPreferences.getLong(Properties.SEQUENCE_NUMBER, Properties.DEFAULT_SEQNUM);
            // read object
            reader.beginObject();
            while (reader.hasNext()){
                String name = reader.nextName();
                if (name.equals("messages")&& reader.peek() != JsonToken.NULL) {
                    // read array
                    reader.beginArray();
                    while( reader.hasNext() )
                    {
                        // read message object
                        long time = 0;
                        long sequenceNum = 0;
                        String sender = "";
                        String messageTxt = "";
                        reader.beginObject();
                        while (reader.hasNext()) {
                            String k_name = reader.nextName();
                            if (k_name.equals("timestamp")&& reader.peek() != JsonToken.NULL){
                                time = reader.nextLong();
                            }
                            else if (k_name.equals("seqnum")&& reader.peek() != JsonToken.NULL){
                                sequenceNum = reader.nextLong();
                            }
                            else if (k_name.equals("sender")&& reader.peek() != JsonToken.NULL){
                                sender = reader.nextString();
                            }
                            else if (k_name.equals("text")&& reader.peek() != JsonToken.NULL){
                                messageTxt = reader.nextString();
                            }
                            else
                                reader.skipValue();
                        }
                        reader.endObject();
                        Peer peer = new Peer(sender, Properties.DEFAULT_URL, Properties.DEFAULT_PORT);
                        Message message = new Message(time, sequenceNum, messageTxt, sender);
                        messageManager.persistAsync(peer, message);
                        peerManager.persistAsync(peer);
                        max_sequenceNum = max_sequenceNum > sequenceNum ? max_sequenceNum : sequenceNum;
                    }
                    reader.endArray();
                }
                else
                    reader.skipValue();
            }
            reader.endObject();
            reader.close();

            // todo updata the seqNum
            SharedPreferences settingSharedPreferences
                    = context.getSharedPreferences(Properties.SETTING_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor settingEditor = settingSharedPreferences.edit();
            settingEditor.putLong(Properties.SEQUENCE_NUMBER, max_sequenceNum);
            settingEditor.commit();
            messageManager.delete(0);

            //messageManager.removeAsync(0);

        }

    }
}
