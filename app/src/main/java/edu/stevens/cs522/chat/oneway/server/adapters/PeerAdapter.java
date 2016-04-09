package edu.stevens.cs522.chat.oneway.server.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import edu.stevens.cs522.chat.oneway.server.contract.PeerContract;

/**
 * Created by Xuefan on 2/28/2016.
 */
public class PeerAdapter extends ResourceCursorAdapter{

    protected final static int ROW_LAYOUT = android.R.layout.simple_list_item_2;

    public PeerAdapter(Context context, int layout, Cursor c) {

        super(context, layout, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(ROW_LAYOUT,parent,false);

        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView senderLine = (TextView)view.findViewById(android.R.id.text1);
        senderLine.setText(PeerContract.getName(cursor));
    }
}
