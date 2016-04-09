package edu.stevens.cs522.chat.oneway.server.interfaces;

import java.util.List;

/**
 * Created by Xuefan on 2/26/2016.
 */
public interface ISimpleQueryListener<T> {
    public void handleResults(List<T> results);
}
