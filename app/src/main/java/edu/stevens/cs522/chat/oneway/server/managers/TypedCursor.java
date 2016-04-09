package edu.stevens.cs522.chat.oneway.server.managers;

import android.database.Cursor;

import edu.stevens.cs522.chat.oneway.server.interfaces.IEntityCreator;

/**
 * Created by Xuefan on 2/27/2016.
 */
public class TypedCursor<T> {
    private Cursor cursor;
    private IEntityCreator<T> creator;

    public TypedCursor(Cursor cursor,IEntityCreator<T> creator){
        this.cursor = cursor;
        this.creator = creator;
    }

    public Cursor getCursor() {
        return cursor;
    }
}
