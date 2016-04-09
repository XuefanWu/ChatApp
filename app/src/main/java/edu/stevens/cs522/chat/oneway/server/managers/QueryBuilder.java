package edu.stevens.cs522.chat.oneway.server.managers;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import edu.stevens.cs522.chat.oneway.server.interfaces.IEntityCreator;
import edu.stevens.cs522.chat.oneway.server.interfaces.IQueryListener;

/**
 * Created by Xuefan on 2/27/2016.
 */
public class QueryBuilder<T> implements LoaderManager.LoaderCallbacks<Cursor> {
    String tag;
    Context context;
    Uri uri;
    int loaderID;
    IEntityCreator<T> creator;
    IQueryListener<T> listener;
    String[] projection;
    String selection;
    String[] selectionArgs;

    private QueryBuilder(String tag,Context context,
                         Uri uri, int loaderID,
                         IEntityCreator<T> creator,
                         IQueryListener<T> listener){
        this.tag = tag;
        this.context = context;
        this.uri = uri;
        this.loaderID = loaderID;
        this.creator = creator;
        this.listener = listener;
    }

    private QueryBuilder(String tag,Context context,
                         Uri uri, int loaderID,
                         String[] projection,
                         String selection,
                         String[] selectionArgs,
                         IEntityCreator<T> creator,
                         IQueryListener<T> listener){
        this.tag = tag;
        this.context = context;
        this.uri = uri;
        this.loaderID = loaderID;
        this.creator = creator;
        this.listener = listener;
        this.selection = selection;
        this.projection = projection;
        this.selectionArgs = selectionArgs;
    }

    public static <T> void executeQuery(String tag,Activity context,
                                        Uri uri, int loaderID,
                                        IEntityCreator<T> creator,
                                        IQueryListener<T> listener){
        QueryBuilder<T> qb = new QueryBuilder<T>(tag,context,uri,
                                           loaderID,creator,listener);
        LoaderManager lm = context.getLoaderManager();
        lm.initLoader(loaderID,null,qb);
    }

    public static <T> void executeQuery(String tag,Activity context,
                                        Uri uri, int loaderID,
                                        String[] projection,
                                        String selection,
                                        String[] selectionArgs,
                                        IEntityCreator<T> creator,
                                        IQueryListener<T> listener){
        QueryBuilder<T> qb = new QueryBuilder<T>(tag,context,uri,
                loaderID,projection,selection,selectionArgs,creator,listener);
        LoaderManager lm = context.getLoaderManager();
        lm.initLoader(loaderID,null,qb);
    }

    public static <T> void reexecuteQuery(String tag,Activity context,
                                        Uri uri, int loaderID,
                                        String[] projection,
                                        String selection,
                                        String[] selectionArgs,
                                        IEntityCreator<T> creator,
                                        IQueryListener<T> listener){
        QueryBuilder<T> qb = new QueryBuilder<T>(tag,context,uri,
                loaderID,projection,selection,selectionArgs,creator,listener);
        LoaderManager lm = context.getLoaderManager();
        lm.restartLoader(loaderID,null,qb);
    }
    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        if(id == loaderID){
            return new CursorLoader(context,uri,null,null,null,null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(loader.getId() == loaderID){
            listener.handleResult(new TypedCursor<T>(cursor,creator));
        }
        else{
            throw  new IllegalStateException("Unexpected loader callback");
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        if(loader.getId() == loaderID){
            listener.closeResult();
        }
        else{
            throw  new IllegalStateException("Unexpected loader callback");
        }

    }
}
