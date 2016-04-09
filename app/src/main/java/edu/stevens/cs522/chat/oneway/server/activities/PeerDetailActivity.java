package edu.stevens.cs522.chat.oneway.server.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import edu.stevens.cs522.chat.oneway.server.R;
import edu.stevens.cs522.chat.oneway.server.adapters.MessageAdapter;
import edu.stevens.cs522.chat.oneway.server.contract.MessageContract;
import edu.stevens.cs522.chat.oneway.server.contract.PeerContract;
import edu.stevens.cs522.chat.oneway.server.entities.Message;
import edu.stevens.cs522.chat.oneway.server.entities.Peer;
import edu.stevens.cs522.chat.oneway.server.interfaces.IEntityCreator;
import edu.stevens.cs522.chat.oneway.server.interfaces.IQueryListener;
import edu.stevens.cs522.chat.oneway.server.interfaces.ISimpleQueryListener;
import edu.stevens.cs522.chat.oneway.server.managers.MessageManager;
import edu.stevens.cs522.chat.oneway.server.managers.PeerManager;
import edu.stevens.cs522.chat.oneway.server.managers.TypedCursor;

public class PeerDetailActivity extends ListActivity {

    private ListView listViewItems;
    private MessageAdapter mAdapter;
    private static final int PEER_LOADER_ID = 1;
    private static final int MESSAGE_LOADER_ID = 1;
    int column_id = 0;
    PeerManager peerManager;
    MessageManager messageManager;
    String[] projections = {PeerContract.ID,PeerContract.NAME,PeerContract.ADDRESS,
    PeerContract.PORT};
    Peer peer_detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peer_detail);
//        Bundle b =getIntent().getExtras();
//        peer_detail = b.getParcelable(ChatApp.EXTRA_MESSAGE);
        Intent intent = getIntent();
        column_id = intent.getIntExtra(PeerListActivity.PEER_DETAIL_ID_KEY,4);
        Log.v("DetailActivityID",String.valueOf(column_id));
        peerManager = new PeerManager(this, new IEntityCreator<Peer>() {
            @Override
            public Peer create(Cursor cursor) {
                return new Peer(cursor);
            }
        },PEER_LOADER_ID);
        peerManager.executeSimpleQuery(
                PeerContract.CONTENT_URI(String.valueOf(column_id)), projections, null, null,
                new ISimpleQueryListener<Peer>() {
                    @Override
                    public void handleResults(List<Peer> results) {
                        if(results.size()>0){
                        renderView(results.get(0));}
                        else{
                            Log.v("PeerDetailActivity","size0");
                        }
                       // renderView(peer_detail);
                    }
                });


        messageManager = new MessageManager(this, new IEntityCreator<Message>() {
            @Override
            public Message create(Cursor cursor) {
                return new Message(cursor);
            }
        },MESSAGE_LOADER_ID);
        messageManager.executeQuery(
                MessageContract.CONTENT_URI(String.valueOf(column_id)), null, null, null,
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
        listViewItems = getListView();
        mAdapter = new MessageAdapter(this, android.R.layout.simple_list_item_2,null);
        listViewItems.setAdapter(mAdapter);
    }

    private void renderView(Peer peer){
        String Peer = "Sender : " + peer.name;
        //String Address = "\nAddress : " + peer.address.getHostAddress();
        String Address = "\nAddress : " + peer.address;
        String Port = "\nPort : " + peer.port + "\n\n\n";
        TextView tt;
        tt =(TextView)findViewById(R.id.name);
        tt.setText(Peer);
        tt =(TextView)findViewById(R.id.address);
        tt.setText(Address);
        tt =(TextView)findViewById(R.id.port);
        tt.setText(Port);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_peer_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.back) {
            setResult(RESULT_CANCELED, null);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
