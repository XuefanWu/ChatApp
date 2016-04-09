package edu.stevens.cs522.chat.oneway.server.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import edu.stevens.cs522.chat.oneway.server.contract.PeerContract;

/**
 * Created by Xuefan on 2/14/2016.
 */
public class Peer implements Parcelable {
    public long id;
    public String name;
    //public InetAddress address;
    public String address;
    public int port;

//    public Peer(String name,InetAddress address,int port){
//        this.name = name;
//        this.address = address;
//        this.port = port;
//    }

    public Peer(String name,String address,int port){
        this.name = name;
        this.address = address;
        this.port = port;
    }

    protected Peer(Parcel in) {
        id = in.readLong();
        name = in.readString();
        port = in.readInt();
//        byte[] ipAddress = new byte[in.readInt()];
//        in.readByteArray(ipAddress);
//        try {
//            address = InetAddress.getByAddress(ipAddress);
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
        address = in.readString();
    }

    public Peer(Cursor cursor){
        this.id = PeerContract.getId(cursor);
        this.name = PeerContract.getName(cursor);
        this.address = PeerContract.getAddress(cursor);
        this.port = PeerContract.getPort(cursor);


    }

    public static final Creator<Peer> CREATOR = new Creator<Peer>() {
        public Peer createFromParcel(Parcel in) {
            return new Peer(in);
        }

        public Peer[] newArray(int size) {
            return new Peer[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(name);
        parcel.writeInt(port);
        //byte[] ipAddress = address.getAddress();
        //parcel.writeInt(ipAddress.length);
        //parcel.writeByteArray(ipAddress);
        parcel.writeString(address);
    }

    public void writeToProvider(ContentValues values,Peer peer) {
        PeerContract.putName(values, peer.name);
        PeerContract.putAddress(values,peer.address);
        PeerContract.putPort(values, peer.port);


    }

    @Override
    public String toString(){
        return "Sender : " + this.name
                + " Address : " + this.address.toString()
                + " Port :  " +  this.port ;
    }
}
