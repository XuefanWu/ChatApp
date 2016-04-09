package edu.stevens.cs522.chat.oneway.server.managers;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import edu.stevens.cs522.chat.oneway.server.contract.MessageContract;
import edu.stevens.cs522.chat.oneway.server.entities.Message;
import edu.stevens.cs522.chat.oneway.server.entities.Peer;
import edu.stevens.cs522.chat.oneway.server.interfaces.IEntityCreator;
import edu.stevens.cs522.chat.oneway.server.interfaces.IQueryListener;
import edu.stevens.cs522.chat.oneway.server.interfaces.ISimpleQueryListener;
import edu.stevens.cs522.chat.oneway.server.providers.PeerProvider;

/**
 * Created by Xuefan on 2/26/2016.
 */
public class MessageManager extends Manager<Message>{

    public final static  String[] PROJECTION
            = new String[] {MessageContract.ID, MessageContract.MESSAGE, MessageContract.PEER_FK};



    public final static Uri CONTENT_URI = MessageContract.CONTENT_URI;

    private IQueryListener<Message> def_listener;
    public MessageManager(Context context, IEntityCreator creator, int loadID) {
        super(context, creator, loadID);
    }

    public void persistAsync(final Peer peer,final Message message){
        int flag=0;
        while (flag ==0){
             flag = (int) PeerProvider.getPeer_fk(peer);
        }
        ContentValues contentValues = new ContentValues();
        MessageContract.putMessage(contentValues, message.messageText);
        MessageContract.putPeer_fk(contentValues, flag);
        MessageContract.putTime(contentValues, message.time);
        MessageContract.putSequence(contentValues,message.sequenceNum);
        this.getAsyncContentResolver().insertAsync(MessageContract.CONTENT_URI,
                contentValues, null);

    }
    public void persist(final Peer peer,final Message message){
        int flag=0;
        while (flag ==0){
            flag = (int) PeerProvider.getPeer_fk(peer);
        }
        ContentValues contentValues = new ContentValues();
        MessageContract.putMessage(contentValues, message.messageText);
        MessageContract.putPeer_fk(contentValues, flag);
        MessageContract.putTime(contentValues, message.time);
        MessageContract.putSequence(contentValues, message.sequenceNum);
        this.getSyncResolver().insert(MessageContract.CONTENT_URI,
                contentValues);

    }


    public void delete(int message_id){
        String  whereClause= MessageContract.SEQUENCE + "=?";
        String[] whereArgs = new String[] { Integer.toString(message_id) };
        Log.v("reach delete","YEs");
         this.getSyncResolver().delete(
                 MessageContract.CONTENT_URI(Integer.toString(message_id)), whereClause,whereArgs);
    }

    public void removeAsync(long ID) {
        getAsyncContentResolver().deleteAsync(MessageContract.CONTENT_URI(String.valueOf(ID)));
    }
//    public void removeAsync() {
//
//        getAsyncContentResolver().deleteAsync(BookContract.CONTENT_URI);
//    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public List<Message> search(String id){
        Cursor cursor = getSyncResolver().query(MessageContract.CONTENT_URI(id),
                new String[]{MessageContract.ID, MessageContract.TIME, MessageContract.SEQUENCE,
                        MessageContract.MESSAGE}, null, null, null, null);
        List<Message> messageList = new ArrayList();
        if (cursor.moveToFirst()) {
            do {
                Message instance = getCreator().create(cursor);
                messageList.add(instance);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return messageList;

    }


    public void searchAsync(String id, ISimpleQueryListener<Message> listener){
        this.executeSimpleQuery(MessageContract.CONTENT_URI(id), listener);
    }

    public void getAllAsync(IQueryListener<Message> listener){
        def_listener = listener;
        this.executeQuery(CONTENT_URI, listener);
    }

    // get all
    public TypedCursor<Message> getAll(){
        Cursor cursor = this.getSyncResolver().query(CONTENT_URI,PROJECTION,null,null,null);
        TypedCursor<Message> book_cursor = new TypedCursor(cursor, getCreator());
        return book_cursor;
    }






}
