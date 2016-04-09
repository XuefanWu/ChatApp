package edu.stevens.cs522.chat.oneway.server.entities;

import android.net.Uri;
import android.os.Parcelable;
import android.util.JsonReader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.UUID;

import edu.stevens.cs522.chat.oneway.server.managers.Response;

/**
 * Created by Xuefan on 3/18/2016.
 */
public abstract class Request implements Parcelable{

    public long clientID;
    public UUID registrationID;
    public abstract Map<String,String> getRequestHeaders();
    public abstract Uri getRequestUri();

    public abstract String getRequestEntity() throws IOException;
    public abstract Response getResponse(HttpURLConnection connection, JsonReader jsonReader);

}
