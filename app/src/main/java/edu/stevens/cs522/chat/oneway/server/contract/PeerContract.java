package edu.stevens.cs522.chat.oneway.server.contract;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by Xuefan on 2/14/2016.
 */
public class PeerContract {

    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String ADDRESS = "address";
    public static final String PORT = "port";
    public static final String AUTHORITY = "edu.stevens.cs522.chat.oneway.server";
    public static final String PATH ="PeerTable";
    public static final Uri CONTENT_URI = CONTENT_URI(AUTHORITY,PATH);
    public static final String CONTENT_PATH = CONTENT_PATH(CONTENT_URI);
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


    public static long getId(Cursor cursor) {
        long a = cursor.getLong(cursor.getColumnIndexOrThrow(ID));
        return a;
    }
    public static void putId(ContentValues values, long id) {
        values.put(ID, id);
    }
    public static String getName(Cursor cursor){
        String s = null;
        if(cursor != null ){
            s= cursor.getString(cursor.getColumnIndexOrThrow(NAME));
        }
        return s;
    }

//    public static InetAddress getAddress(Cursor cursor){
//        String address = cursor.getString(cursor.getColumnIndexOrThrow(ADDRESS));
//        address = address.replace("/", "");
//        InetAddress ipAddresses[] = new InetAddress[1];
//        try {
//            ipAddresses = InetAddress.getAllByName(address);
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
//        return ipAddresses[0];
//    }

    public static String getAddress(Cursor cursor){
        String s = "";
        if(cursor != null && cursor.moveToFirst()){
            s= cursor.getString(cursor.getColumnIndexOrThrow(ADDRESS));
        }
        return s;
    }

    public static int getPort(Cursor cursor){
        int s = 0;
        if(cursor != null && cursor.moveToFirst()){
            s= cursor.getInt(cursor.getColumnIndexOrThrow(PORT));
        }
        return s;
    }

    public static void putName(ContentValues values, String name) {

        values.put(NAME,name);
    }
//    public static void putAddress(ContentValues values, InetAddress address) {
//        values.put(ADDRESS,address.toString());
//    }

        public static void putAddress(ContentValues values, String address) {
        values.put(ADDRESS,address);
    }

    public static void putPort(ContentValues values, int port) {
        values.put(PORT,port);
    }


}
