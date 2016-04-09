package edu.stevens.cs522.chat.oneway.server.entities;

import android.net.Uri;
import android.os.Parcel;
import android.util.JsonReader;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import edu.stevens.cs522.chat.oneway.server.managers.Response;

/**
 * Created by Xuefan on 3/18/2016.
 */
public class Register extends Request{

    private String clientName;
    private String url;

    public Register(Parcel in) {
        clientID = in.readLong();
        registrationID = UUID.fromString(in.readString());
        url = in.readString();
        clientName = in.readString();
    }

    public Register(long clientId, UUID registrationId, String clientName, String url) {
        this.clientID = clientId;
        this.registrationID = registrationId;
        this.clientName = clientName;
        this.url = url;
    }


    public String getClientName(){
        return clientName;
    }

    public String getUrl(){
        return url;
    }

    public static final Creator<Register> CREATOR = new Creator<Register>() {
        @Override
        public Register createFromParcel(Parcel in) {
            return new Register(in);
        }

        @Override
        public Register[] newArray(int size) {
            return new Register[size];
        }
    };


    @Override
    public Map<String, String> getRequestHeaders() {
        Map<String,String> headers = new HashMap<String, String>();
        headers.put("X-latitude",Double.toString(100.0));
        headers.put("X-longitude",Double.toString(100.0));
        return headers;
    }

    @Override
    public Uri getRequestUri() {
        StringBuilder sb = new StringBuilder();

        try {
            sb.append("username=");
            sb.append(URLEncoder.encode(clientName,"UTF-8"));
            sb.append("&regid=");
            sb.append(URLEncoder.encode(registrationID.toString(),"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return Uri.parse(url+"?"+sb.toString());
    }

    @Override
    public String getRequestEntity() throws IOException {
        return null;
    }

    @Override
    public Response getResponse(HttpURLConnection connection, JsonReader jsonReader) {
        try {
            return new Response(connection.getResponseCode(),jsonReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(clientID);
        dest.writeString(registrationID.toString());
        dest.writeString(url);
        dest.writeString(clientName);
    }
}
