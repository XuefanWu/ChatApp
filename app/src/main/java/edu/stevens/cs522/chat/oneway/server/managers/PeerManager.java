package edu.stevens.cs522.chat.oneway.server.managers;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import edu.stevens.cs522.chat.oneway.server.contract.PeerContract;
import edu.stevens.cs522.chat.oneway.server.entities.Peer;
import edu.stevens.cs522.chat.oneway.server.interfaces.IEntityCreator;
import edu.stevens.cs522.chat.oneway.server.providers.PeerProvider;

/**
 * Created by Xuefan on 2/26/2016.
 */
public class PeerManager extends Manager<Peer>{


    public PeerManager(Context context, IEntityCreator creator, int loadID) {
        super(context, creator, loadID);
    }

    public boolean checkPeer(final Peer peer){
        return PeerProvider.checkPeerExist(peer);
    }

    public void persistAsync(final Peer peer){
        if(checkPeer(peer)) {
            ContentValues contentValues = new ContentValues();
            peer.writeToProvider(contentValues, peer);
            Log.v("PersistAsync", peer.name);
            getAsyncContentResolver().insertAsync(PeerContract.CONTENT_URI,
                contentValues, null);

        }

    }

    public void persist(final Peer peer){
        if(checkPeer(peer)) {
            ContentValues contentValues = new ContentValues();
            peer.writeToProvider(contentValues, peer);
            Log.v("PersistAsync", peer.name);
            getSyncResolver().insert(PeerContract.CONTENT_URI,
                    contentValues);

        }

    }



//    public void removeAsync(long ID) {
//        getAsyncContentResolver().deleteAsync(BookContract.CONTENT_URI(String.valueOf(ID)));
//    }
//    public void removeAsync() {
//
//        getAsyncContentResolver().deleteAsync(BookContract.CONTENT_URI);
//    }



}
