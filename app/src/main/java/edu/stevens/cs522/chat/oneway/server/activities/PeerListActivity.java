package edu.stevens.cs522.chat.oneway.server.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import edu.stevens.cs522.chat.oneway.server.R;
import edu.stevens.cs522.chat.oneway.server.adapters.PeerAdapter;
import edu.stevens.cs522.chat.oneway.server.contract.PeerContract;
import edu.stevens.cs522.chat.oneway.server.entities.Peer;
import edu.stevens.cs522.chat.oneway.server.interfaces.IEntityCreator;
import edu.stevens.cs522.chat.oneway.server.interfaces.IQueryListener;
import edu.stevens.cs522.chat.oneway.server.managers.PeerManager;
import edu.stevens.cs522.chat.oneway.server.managers.TypedCursor;

public class PeerListActivity extends ListActivity {

    static final private int DETAIL_REQUEST = 1;
    private ListView listViewItems;
    private PeerAdapter mAdapter;
    public static final String PEER_DETAIL_ID_KEY = "peer_detail_id";
    private static final int PEER_LOADER_ID = 1;
    PeerManager peerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peer_list);

        peerManager = new PeerManager(this, new IEntityCreator<Peer>() {
            @Override
            public Peer create(Cursor cursor) {
                return new Peer(cursor);
            }
        },PEER_LOADER_ID);
        peerManager.executeQuery(
                PeerContract.CONTENT_URI, null, null, null,
                new IQueryListener<Peer>() {
                    @Override
                    public void handleResult(TypedCursor<Peer> results) {
                        mAdapter.swapCursor(results.getCursor());
                    }
                    @Override
                    public void closeResult() {
                        mAdapter.swapCursor(null);
                    }
                });

        listViewItems = getListView();
        mAdapter = new PeerAdapter(this, android.R.layout.simple_list_item_2,null);
        listViewItems.setAdapter(mAdapter);

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Intent intent = new Intent(getApplicationContext(), PeerDetailActivity.class);
        intent.putExtra(PEER_DETAIL_ID_KEY, (int) id);
        Log.v("detailID",String.valueOf(id));
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_peer_list, menu);
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
