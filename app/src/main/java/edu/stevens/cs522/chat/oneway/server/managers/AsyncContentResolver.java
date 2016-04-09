package edu.stevens.cs522.chat.oneway.server.managers;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import edu.stevens.cs522.chat.oneway.server.interfaces.IContinue;

/**
 * Created by Xuefan on 2/26/2016.
 */
public class AsyncContentResolver extends AsyncQueryHandler {
    public AsyncContentResolver(ContentResolver cr) {
        super(cr);
    }

    //INSERT
    public void insertAsync(Uri uri,
                            ContentValues values,
                            IContinue<Uri> callback){
        Log.v("InsetAsync","kfdkldslkdfklsdfldlkdlk;");
        this.startInsert(0, callback, uri, values);
    }
    @Override
    protected void onInsertComplete(int token, Object cookie, Uri uri) {
        if(cookie!=null){
            IContinue<Uri> callback = (IContinue<Uri>) cookie;
            callback.kontinue(uri);
        }
        //super.onInsertComplete(token, cookie, uri);
    }


    //QUERY
    public void queryAsync(Uri uri, String[] projection,
                           String selection, String[] selectionArgs,
                           String sortOrder,
                           IContinue<Cursor> callback){
        this.startQuery(0,callback,uri,projection,
                selection,selectionArgs,sortOrder);
    }
    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        if(cookie!=null){
            IContinue<Cursor> callback = (IContinue<Cursor>) cookie;
            callback.kontinue(cursor);
        }
       // super.onQueryComplete(token, cookie, cursor);
    }



    //DELETE
    public void deleteAsync(Uri uri){

        this.startDelete(0,null,uri,null,null);
    }



//    //UPDATE
//    public void updateAsync(Uri uri, ContentValues contentValues,
//                            String selection, String[] selectionArgs,
//                            IContinue<Integer> callback){
//        this.startUpdate(0,null,uri,contentValues,
//                selection,selectionArgs);
//    }
//    @Override
//    protected void onUpdateComplete(int token, Object cookie, int result) {
////        if(cookie!=null){
////            IContinue<Integer> callback = (IContinue<Integer>) cookie;
////            callback.kontinue(result);
////        }
////        //super.onUpdateComplete(token, cookie, result);
//    }


}
