package edu.stevens.cs522.chat.oneway.server.managers;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import edu.stevens.cs522.chat.oneway.server.interfaces.IContinue;
import edu.stevens.cs522.chat.oneway.server.interfaces.IEntityCreator;
import edu.stevens.cs522.chat.oneway.server.interfaces.ISimpleQueryListener;

/**
 * Created by Xuefan on 2/26/2016.
 */
public class SimpleQueryBuilder<T> implements IContinue<Cursor> {

    private IEntityCreator<T> helper;
    private ISimpleQueryListener listener;

    private SimpleQueryBuilder(
            IEntityCreator<T> helper,
            ISimpleQueryListener<T> listener){
        this.helper = helper;
        this.listener = listener;
    }

    public static <T> void executeQuery(Activity context,
                                   Uri uri,
                                   IEntityCreator<T> helper,
                                   ISimpleQueryListener<T> listener){
        SimpleQueryBuilder<T> qb =
                new SimpleQueryBuilder<T>(helper,listener);

        AsyncContentResolver resolver =
                new AsyncContentResolver(context.getContentResolver());

        resolver.queryAsync(uri,null,null,null,null,qb);
    }

    public static <T> void executeQuery(Activity context,
                                        Uri uri,
                                        String[] projection,
                                        String selection,
                                        String[] selectionArgs,
                                        IEntityCreator<T> helper,
                                        ISimpleQueryListener<T> listener){
        SimpleQueryBuilder<T> qb =
                new SimpleQueryBuilder<T>(helper,listener);

        AsyncContentResolver resolver =
                new AsyncContentResolver(context.getContentResolver());

        resolver.queryAsync(uri,projection,selection,selectionArgs,null,qb);
    }

    @Override
    public void kontinue(Cursor value) {
        List<T> instances = new ArrayList<T>();
        if(value.moveToFirst()){
            do{
                T instance = helper.create(value);
                instances.add(instance);
            }while(value.moveToNext());
        }
        value.close();
        listener.handleResults(instances);
    }
}
