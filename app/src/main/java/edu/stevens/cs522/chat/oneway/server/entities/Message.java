package edu.stevens.cs522.chat.oneway.server.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import edu.stevens.cs522.chat.oneway.server.contract.MessageContract;

/**
 * Created by Xuefan on 2/14/2016.
 */
public class Message implements Parcelable {
    public long id;
    public String messageText;
    public long time;
    public String sender;
    public long sequenceNum;

    public Message(String messageText,String sender){
        this.messageText = messageText;
        this.sender = sender;
    }

    protected Message(Parcel in) {
        id = in.readLong();
        messageText = in.readString();
        sender = in.readString();
        time = in.readLong();
        sequenceNum = in.readLong();
    }

    public Message(long time, long sequenceNum,
                   String messageText, String sender)
    {
        this.time = time;
        this.sequenceNum = sequenceNum;
        this.messageText = messageText;
        this.sender = sender;
    }

    public Message(Cursor cursor){
        this.id = MessageContract.getId(cursor);
        this.messageText = MessageContract.getMessage(cursor);
        this.time = MessageContract.getTime(cursor);
        this.sequenceNum = MessageContract.getSequence(cursor);
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {

        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }


        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(messageText);
        parcel.writeString(sender);
        parcel.writeLong(sequenceNum);
        parcel.writeLong(time);
    }

    public void writeToProvider(ContentValues values,Message message) {
        MessageContract.putMessage(values,message.messageText);
        MessageContract.putId(values, message.id);
    }
    @Override
    public String toString(){
        return this.sender + " : " + this.messageText;}

}