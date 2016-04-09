package edu.stevens.cs522.chat.oneway.server.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLException;

import edu.stevens.cs522.chat.oneway.server.contract.MessageContract;
import edu.stevens.cs522.chat.oneway.server.contract.PeerContract;
import edu.stevens.cs522.chat.oneway.server.entities.Message;
import edu.stevens.cs522.chat.oneway.server.entities.Peer;

/**
 * Created by Xuefan on 2/14/2016.
 */
public class PeerDbAdapter {

    public static final String KEY_MESSAGESPEER_INDEX = "MessagesPeerIndex";


    public static final String DATABASE_NAME = "ChatAppDB.db";
    private static final int DATABASE_VERSION = 1;


    DatabaseHelper dbHelper;
    private final Context context;
    private SQLiteDatabase db;

    public PeerDbAdapter(Context _context){
        context = _context;
        dbHelper = new DatabaseHelper(context,DATABASE_NAME,null,DATABASE_VERSION);

    }

    public PeerDbAdapter open() throws SQLException {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() { db.close(); }

    public void persist(Peer peer,Message message) throws SQLException{
        db = dbHelper.getWritableDatabase();
        ContentValues peerContent, messageContent;
        long peer_id = 0;
        Cursor cursor = db.query(DatabaseHelper.PEER_TABLE,
                new String[] {PeerContract.ID, PeerContract.NAME,
                        PeerContract.ADDRESS, PeerContract.PORT},
                PeerContract.NAME + " = '" + peer.name + "'",
                null, null, null, null);
        if(cursor.getCount() != 0){
            cursor.moveToFirst();
            Peer exist_peer = new Peer(cursor);
            peer_id = exist_peer.id;
        }
        else{
            // insert data to peer_table
            peerContent = new ContentValues();
            peer.writeToProvider(peerContent,peer);
            peer_id = db.insertWithOnConflict(DatabaseHelper.PEER_TABLE, null,
                    peerContent, SQLiteDatabase.CONFLICT_IGNORE);
        }

        // insert data to message_table
        messageContent = new ContentValues();
        MessageContract.putMessage(messageContent, message.messageText);
        MessageContract.putPeer_fk(messageContent, (int) peer_id);
        db.insert(DatabaseHelper.MESSAGE_TABLE, null, messageContent);

        return;
    }

    public long getPeer_fk(Peer peer){
        db = dbHelper.getWritableDatabase();
        ContentValues peerContent;
        long peer_id = 0;
        Cursor cursor = db.query(DatabaseHelper.PEER_TABLE,
                new String[] {PeerContract.ID, PeerContract.NAME,
                        PeerContract.ADDRESS, PeerContract.PORT},
                PeerContract.NAME + " = '" + peer.name + "'",
                null, null, null, null);
        if(cursor.getCount() != 0){
            cursor.moveToFirst();
            Peer exist_peer = new Peer(cursor);
            peer_id = exist_peer.id;
        }
        else{
            // insert data to peer_table
            peerContent = new ContentValues();
            peer.writeToProvider(peerContent,peer);
            peer_id = db.insertWithOnConflict(DatabaseHelper.PEER_TABLE, null,
                    peerContent, SQLiteDatabase.CONFLICT_IGNORE);
        }
        return peer_id;
    }

    public Cursor fetchAllPeers(){
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT "
                        + DatabaseHelper.PEER_TABLE + "." + PeerContract.ID + ", "
                        + PeerContract.NAME + ", "
                        + PeerContract.ADDRESS + ", "
                        + PeerContract.PORT
                        + " FROM "
                        + DatabaseHelper.PEER_TABLE ,
                null);
        return cursor;

    }

    public Cursor sync(){
        String[] _selectionArg = {"0"};
        // query the database
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT "
                        + dbHelper.MESSAGE_TABLE + "." + MessageContract.ID + ", "
                        + MessageContract.TIME + ", "
                        + MessageContract.SEQUENCE + ", "
                        + MessageContract.MESSAGE + ", "
                        + PeerContract.NAME
                        + " FROM " + dbHelper.PEER_TABLE + " LEFT OUTER JOIN "
                        + dbHelper.MESSAGE_TABLE + " ON "
                        + dbHelper.PEER_TABLE + "." + PeerContract.ID + " = "
                        + dbHelper.MESSAGE_TABLE + "." + MessageContract.PEER_FK
                        + " WHERE " + MessageContract.SEQUENCE + " = ? "
                        + " GROUP BY "
                        + dbHelper.MESSAGE_TABLE + "." + MessageContract.ID,
                _selectionArg);
        if(cursor != null){
            cursor.moveToFirst();
        }
        //cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    public Cursor fetchPeers(long id){
        db = dbHelper.getWritableDatabase();
        String query = "SELECT "
                + DatabaseHelper.PEER_TABLE + "." + PeerContract.ID + ", "
                + PeerContract.NAME + ", "
                + PeerContract.ADDRESS + ", "
                + PeerContract.PORT
                + " FROM "
                + DatabaseHelper.PEER_TABLE
                +" WHERE "+PeerContract.ID+" =?";
        Cursor cursor = db.rawQuery(query,new String[]{String.valueOf(id)});
        return cursor;
    }




    public Cursor fetchAllMessages(){
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT "
                        + DatabaseHelper.MESSAGE_TABLE + "."+ MessageContract.ID + ", "
                        + "["
                        + PeerContract.NAME + "] || ' : ' || ["
                        + MessageContract.MESSAGE + "] AS "
                        + MessageContract.TXT +", "+ MessageContract.TIME+", "+MessageContract.SEQUENCE
                        + " FROM "+ DatabaseHelper.PEER_TABLE +" LEFT OUTER JOIN "
                        + DatabaseHelper.MESSAGE_TABLE + " ON "
                        + DatabaseHelper.PEER_TABLE + "." + PeerContract.ID + " = "
                        + DatabaseHelper.MESSAGE_TABLE + "." + MessageContract.PEER_FK
                        + " GROUP BY "
                        + DatabaseHelper.MESSAGE_TABLE + "."+ MessageContract.ID,
                null);
        return cursor;
    }

    public Cursor fetchMessages(long id){
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(DatabaseHelper.MESSAGE_TABLE,
                new String[]{MessageContract.ID, MessageContract.MESSAGE,MessageContract.TIME,MessageContract.SEQUENCE},
                MessageContract.PEER_FK + " = " + id,
                null, null, null, null);
        return cursor;
    }






    private class DatabaseHelper extends SQLiteOpenHelper{
        public static final String PEER_TABLE = "PeerTable";
        public static final String MESSAGE_TABLE = "MessageTable";

        private static final String DATABASE_CREATE = "create table "+
                PEER_TABLE + " (" + PeerContract.ID + " integer primary key autoincrement, "
                + PeerContract.NAME +" text not null, "
                +PeerContract.ADDRESS+" text not null,"
                +PeerContract.PORT +"  integer not null)";

        private static final String MESSAGE_DATABASE_CREATE = "create table "+
                MESSAGE_TABLE + " (" + MessageContract.ID + " integer primary key autoincrement, "
                +MessageContract.MESSAGE+" text not null, "
                +MessageContract.PEER_FK + " integer not null, "
                + "foreign key ("+MessageContract.PEER_FK
                +") references PeerTable(_id) on delete cascade)";

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
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
