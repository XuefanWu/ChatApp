package edu.stevens.cs522.chat.oneway.server.managers;

import android.util.JsonReader;

/**
 * Created by Xuefan on 3/18/2016.
 */
public class Response {

    private int responseCode;
    private JsonReader jsonReader;

    public Response(int responseCode, JsonReader jsonReader) {
        this.responseCode = responseCode;
        this.jsonReader = jsonReader;
    }

    public JsonReader getJsonReader() {
        return jsonReader;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public boolean isValid() {
        return responseCode >= 200 && responseCode < 300;
    }
}
