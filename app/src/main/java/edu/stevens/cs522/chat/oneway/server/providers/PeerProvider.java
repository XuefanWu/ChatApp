package edu.stevens.cs522.chat.oneway.server.providers;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import java.sql.SQLException;

import edu.stevens.cs522.chat.oneway.server.contract.MessageContract;
import edu.stevens.cs522.chat.oneway.server.contract.PeerContract;
import edu.stevens.cs522.chat.oneway.server.database.PeerDbAdapter;
import edu.stevens.cs522.chat.oneway.server.entities.Peer;

/**
 * Created by Xuefan on 2/27/2016.
 */
public class PeerProvider extends ContentProvider{


    private static SQLiteDatabase db;
    public static DatabaseHelper dbHelper;
    public static final String KEY_MESSAGESPEER_INDEX = "MessagesPeerIndex";
    public static final String DATABASE_NAME = "ChatAppDB.db";
    private static final int DATABASE_VERSION = 1;
    public static final String PEER_TABLE = "PeerTable";
    public static final String MESSAGE_TABLE = "MessageTable";
    public static final Uri CONTENT_URI = PeerContract.CONTENT_URI;
    public static final Uri MESSAGE_CONTENT_URI = MessageContract.CONTENT_URI;


    static final int PEER = 1;
    static final int PEER_ID = 2;
    static final int MESSAGE= 3;
    static final int MESSAGE_ID = 4;
    static final int SYNC = 5;
    PeerDbAdapter pDb;

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PeerContract.AUTHORITY, PeerContract.CONTENT_PATH, PEER);
        uriMatcher.addURI(PeerContract.AUTHORITY, PeerContract.CONTENT_PATH_ITEM, PEER_ID);
        uriMatcher.addURI(MessageContract.AUTHORITY, MessageContract.CONTENT_PATH, MESSAGE);
        uriMatcher.addURI(MessageContract.AUTHORITY, MessageContract.CONTENT_PATH_SEQUENCE, SYNC);
        uriMatcher.addURI(MessageContract.AUTHORITY, MessageContract.CONTENT_PATH_ITEM, MESSAGE_ID);
    }
    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        pDb = new PeerDbAdapter(context);
        return (db == null)? false:true;
    }

    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        Cursor cursor;
        long id;
        switch (uriMatcher.match(uri)){
            case PEER:
                cursor = pDb.fetchAllPeers();
                break;
            case PEER_ID:
                id = ContentUris.parseId(uri);
                cursor = pDb.fetchPeers(id);
                break;
            case MESSAGE:
                cursor = pDb.fetchAllMessages();
                break;
            case MESSAGE_ID:
                id = ContentUris.parseId(uri);
                cursor = pDb.fetchMessages(id);
                break;
            case SYNC:
                cursor = pDb.sync();
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        Uri _uri = null;
        switch (uriMatcher.match(uri)){
            case PEER:
                Log.v("yellow","blue");
                long rowId = db.insertWithOnConflict(PEER_TABLE, null
                        ,contentValues, SQLiteDatabase.CONFLICT_IGNORE);
                if(rowId>0){
                    _uri = ContentUris.withAppendedId(CONTENT_URI,rowId);
                    ContentResolver cr = getContext().getContentResolver();
                    cr.notifyChange(_uri,null);
                }
                break;
            case MESSAGE:
                long rowId1 = db.insert(MESSAGE_TABLE,null,contentValues);
                if(rowId1>0){
                    _uri = ContentUris.withAppendedId(MESSAGE_CONTENT_URI,rowId1);
                    ContentResolver cr = getContext().getContentResolver();
                    cr.notifyChange(_uri,null);
                }
                break;
            default:
                try {
                    throw new SQLException("Failed to insert row into " + uri);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
        return _uri;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        int count = 0;
        db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri))
        {
            case MESSAGE_ID:
                count = (int)db.delete(MESSAGE_TABLE, MessageContract.SEQUENCE+"=?", new String[]{"0"});
                // since the sender will be there after insert new data
                // not modify the peer table
                return count;
            default:
                throw new IllegalArgumentException ("Unsupported URI:" + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    public static long getPeer_fk(Peer peer){
        db = dbHelper.getWritableDatabase();
        long peer_id = 0;
        Cursor cursor = db.query(PEER_TABLE,
                new String[] {PeerContract.ID, PeerContract.NAME,
                        PeerContract.ADDRESS, PeerContract.PORT},
                PeerContract.NAME + " =? ",new String[]{peer.name},
                null, null, null, null);
        if(cursor.getCount() != 0){
            cursor.moveToFirst();
            Peer exist_peer = new Peer(cursor);
            peer_id = exist_peer.id;
        }
        while (cursor!=null &&cursor.moveToNext()) {
            peer_id = cursor.getInt(cursor.getColumnIndexOrThrow(PeerContract.ID));}

        return peer_id;
    }

    public static boolean checkPeerExist(Peer peer){
        boolean flag = false;
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(PEER_TABLE,
                new String[] {PeerContract.ID, PeerContract.NAME,
                        PeerContract.ADDRESS, PeerContract.PORT},
                PeerContract.NAME + " =? ",new String[]{peer.name},
                null, null, null, null);
        if(cursor.getCount() == 0){
            flag = true;
        }
        return flag;
    }

    private class DatabaseHelper extends SQLiteOpenHelper {


        private static final String DATABASE_CREATE = "create table "+
                PEER_TABLE + " (" + PeerContract.ID + " integer primary key autoincrement, "
                + PeerContract.NAME +" text not null, "
                +PeerContract.ADDRESS+" text not null,"
                +PeerContract.PORT +"  integer not null)";

        private static final String MESSAGE_DATABASE_CREATE = "create table "+
                MESSAGE_TABLE + " (" + MessageContract.ID + " integer primary key autoincrement, "
                +MessageContract.MESSAGE+" text not null, "
                +MessageContract.TIME+" text not null, "
                +MessageContract.SEQUENCE+" text not null, "
                +MessageContract.PEER_FK + " integer not null, "
                + "foreign key ("+MessageContract.PEER_FK
                +") references PeerTable(_id) on delete cascade)";

        public DatabaseHelper(Context context) {
            super(context,DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("PRAGMA foreign_keys=ON;");
            db.execSQL(DATABASE_CREATE);
            db.execSQL(MESSAGE_DATABASE_CREATE);
            db.execSQL("create index "+KEY_MESSAGESPEER_INDEX+" on MessageTable("+MessageContract.PEER_FK+")");

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            Log.w(DatabaseHelper.class.getName(),
                    "Upgrading database from version " + oldVersion + " to "
                            + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + PEER_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + MESSAGE_TABLE);
            onCreate(db);
        }
    }
}
