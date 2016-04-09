package edu.stevens.cs522.chat.oneway.server.entities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.JsonReader;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import edu.stevens.cs522.chat.oneway.server.activities.SettingActivity;
import edu.stevens.cs522.chat.oneway.server.contract.Properties;
import edu.stevens.cs522.chat.oneway.server.managers.Response;
import edu.stevens.cs522.chat.oneway.server.managers.StreamingResponse;

/**
 * Created by Xuefan on 3/18/2016.
 */
public class PostMessage extends Request{

    private String url;
    private String message;
    private long time;


    public PostMessage(Parcel in){
        url = in.readString();
        message = in.readString();
        time = in.readLong();
        clientID = in.readLong();
        registrationID = UUID.fromString(in.readString());
    }

    public PostMessage(String url,String message,long time){
        this.url = url;
        this.message = message;
        this.time = time;
        SharedPreferences settings = SettingActivity.context.getSharedPreferences(Properties.SETTING_PREFERENCES, Context.MODE_PRIVATE);
        registrationID = UUID.fromString(settings.getString(Properties.UUID_KEY, ""));
        clientID = settings.getLong(Properties.CLIENT_ID_KEY, 0);


    }

    public StreamingResponse getStreamingResponse(HttpURLConnection httpURLConnection, JsonReader jsonReader) {
        return new StreamingResponse(httpURLConnection, getResponse(httpURLConnection, jsonReader));
    }

    @Override
    public String toString() {
        return "PostMessage{" +
                "message='" + message + '\'' +
                ", url='" + url + '\'' +
                ", time=" + time +
                ", clientId=" + clientID +
                ", regid=" + registrationID.toString() +
                '}';
    }

    public static final Parcelable.Creator<PostMessage> CREATOR
            = new Parcelable.Creator<PostMessage>() {
        public PostMessage createFromParcel(Parcel in) {
            return new PostMessage(in);
        }

        public PostMessage[] newArray(int size) {
            return new PostMessage[size];
        }
    };


    @Override
    public Map<String, String> getRequestHeaders() {

        Map<String, String> requestHeaders = new HashMap<String,String>();
        requestHeaders.put("Content-Type", "application/json");
        requestHeaders.put("X-latitude", Double.toString(10.0));
        requestHeaders.put("X-longitude", Double.toString(10.0));
        return requestHeaders;
    }

    @Override
    public Uri getRequestUri() {
        StringBuilder uriBuilder = new StringBuilder();
        try {
            uriBuilder.append("regid=");
            uriBuilder.append(URLEncoder.encode(registrationID.toString(), Properties.ENCODING));
            uriBuilder.append("&seqnum=0");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        return Uri.parse(url + "/" + clientID + "?" + uriBuilder.toString());
    }

    @Override
    public String getRequestEntity() throws IOException {
        return message + "@" + time;
    }

    @Override
    public Response getResponse(HttpURLConnection connection, JsonReader jsonReader) {
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(message);
        dest.writeLong(time);
        dest.writeLong(clientID);
        dest.writeString(registrationID.toString());
    }
}
