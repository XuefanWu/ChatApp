package edu.stevens.cs522.chat.oneway.server.managers;

import java.net.HttpURLConnection;

/**
 * Created by Xuefan on 3/18/16.
 */
public class StreamingResponse {

    private HttpURLConnection httpURLConnection;
    private Response response;

    public StreamingResponse(HttpURLConnection httpURLConnection, Response response) {
        this.httpURLConnection = httpURLConnection;
        this.response = response;
    }

    public HttpURLConnection getHttpURLConnection() {
        return httpURLConnection;
    }

    public Response getResponse() {
        return response;
    }
}
