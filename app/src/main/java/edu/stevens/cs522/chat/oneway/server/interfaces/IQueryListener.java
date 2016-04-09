package edu.stevens.cs522.chat.oneway.server.interfaces;

import edu.stevens.cs522.chat.oneway.server.managers.TypedCursor;

/**
 * Created by Xuefan on 2/26/2016.
 */
public interface IQueryListener<T> {

    public void handleResult(TypedCursor<T> results);

    public void closeResult();
}
