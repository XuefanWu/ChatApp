package edu.stevens.cs522.chat.oneway.server.contract;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by Xuefan on 2/14/2016.
 */
public class MessageContract {

    public MessageContract() {
    }

    public static final String ID = "_id";
    public static final String MESSAGE = "message";
    public static final String TXT = "txt";
    public static final String PEER_FK = "peer_fk";
    public static final String TIME = "time";
    public static final String SEQUENCE = "sequence";
    public static final String AUTHORITY = "edu.stevens.cs522.chat.oneway.server";
    public static final String PATH ="MessageTable";
    public static final Uri CONTENT_URI = CONTENT_URI(AUTHORITY,PATH);
    public static final String CONTENT_PATH = CONTENT_PATH(CONTENT_URI);
    public static final String CONTENT_PATH_SEQUENCE = CONTENT_PATH(CONTENT_URI("sync"));
    public static final String CONTENT_PATH_ITEM = CONTENT_PATH(CONTENT_URI("#"));


    public static Uri CONTENT_URI(String authority,String path){
        return new Uri.Builder().scheme("content")
                .authority(authority)
                .path(path)
                .build();
    }


    public static Uri withExtendedPath(Uri uri, String... path){
        Uri.Builder builder = uri.buildUpon();
        for(String p:path)
            builder.appendPath(p);
        return builder.build();
    }
    //override
    public static Uri CONTENT_URI(String id){
        return withExtendedPath(CONTENT_URI, id);
    }
    public static String CONTENT_PATH(Uri uri){
        return uri.getPath().substring(1);
    }


    // accessor for Id
    public static long getId(Cursor cursor) {
        long a = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
        return a;
    }
    public static void putId(ContentValues values, long id) {
        values.put(ID, id);
    }

    // accessor for Message
    public static String getMessage(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndexOrThrow(MESSAGE));
    }

    public static void putMessage(ContentValues values, String message) {
        values.put(MESSAGE, message);
    }

    // accessor for Txt
    public static String getTxt(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndexOrThrow(TXT));
    }
    public static void putTxt(ContentValues values, String txt) {
        values.put(TXT, txt);
    }

//    // accessor for Sender
//    public static String getSender(Cursor cursor) {
//        return cursor.getString(cursor.getColumnIndexOrThrow(SENDER));
//    }
//    public static void putSender(ContentValues values, String sender) {
//        values.put(SENDER, sender);
//    }

    public static void putTime(ContentValues values, long time) {
        values.put(TIME, time);
    }

    public static long getTime(Cursor cursor) {
        return cursor.getLong(cursor.getColumnIndexOrThrow(TIME));
    }

    public static void putSequence(ContentValues values, long time) {
        values.put(SEQUENCE, time);
    }

    public static long getSequence(Cursor cursor) {
        return cursor.getLong(cursor.getColumnIndexOrThrow(SEQUENCE));
    }



    // accessor for PEER_FK
    public static long getPeer_fk(Cursor cursor) {
        long a = cursor.getInt(cursor.getColumnIndexOrThrow(PEER_FK));
        return a;
    }
    public static void putPeer_fk(ContentValues values, long fk) {
        values.put(PEER_FK, fk);
    }

}
