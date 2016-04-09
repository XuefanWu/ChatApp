package edu.stevens.cs522.chat.oneway.server.managers;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import edu.stevens.cs522.chat.oneway.server.interfaces.IEntityCreator;
import edu.stevens.cs522.chat.oneway.server.interfaces.IQueryListener;
import edu.stevens.cs522.chat.oneway.server.interfaces.ISimpleQueryListener;

/**
 * Created by Xuefan on 2/26/2016.
 */
public abstract class Manager<T> {

    public final Context context;
    private final IEntityCreator<T> creator;
    private  final int loadID;
    private final String tag;

    protected Manager(Context context,
                      IEntityCreator<T> creator,
                      int loadID
    ) {
        this.context = context;
        this.creator = creator;
        this.loadID = loadID;
        this.tag = this.getClass().getCanonicalName();
        this.syncResolver = context.getContentResolver();
        this.asyncResolver = new AsyncContentResolver(context.getContentResolver());
    }


    private ContentResolver syncResolver;
    private AsyncContentResolver asyncResolver;
    protected ContentResolver getSyncResolver(){
        if(syncResolver == null)
            syncResolver = context.getContentResolver();
        return syncResolver;
    }
    protected AsyncContentResolver getAsyncContentResolver(){
        if(asyncResolver == null){
            asyncResolver =new AsyncContentResolver(context.getContentResolver());}
        return asyncResolver;
    }


    public void executeSimpleQuery(Uri uri,
                                      ISimpleQueryListener<T> listener){
        SimpleQueryBuilder.executeQuery((Activity)context,
                uri,creator,listener);

    }
    protected IEntityCreator<T> getCreator(){
        return creator;
    }

    public void executeSimpleQuery(Uri uri,
                                      String[] projection,
                                      String selection,String[] selectionArgs,
                                      ISimpleQueryListener<T> listener){
        SimpleQueryBuilder.executeQuery((Activity)context,
                uri,projection,selection,selectionArgs,creator,listener);
    }

    protected void executeQuery(Uri uri,IQueryListener<T> listener){
        QueryBuilder.executeQuery(tag,(Activity) context,uri,
                loadID,creator,listener);
    }

    public void executeQuery(Uri uri,String[] projection,
                                String selection,String[] selectionArgs,
                                IQueryListener<T> listener){
        QueryBuilder.executeQuery(tag,(Activity) context,uri,
                loadID,projection,selection,selectionArgs,creator,listener);
    }

    public void reexecuteQuery(Uri uri,String[] projection,
                                String selection,String[] selectionArgs,
                                IQueryListener<T> listener){
        QueryBuilder.reexecuteQuery(tag, (Activity) context, uri,
                loadID, projection, selection, selectionArgs, creator, listener);
    }

}
