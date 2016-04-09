package edu.stevens.cs522.chat.oneway.server.managers;

import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import edu.stevens.cs522.chat.oneway.server.contract.Properties;
import edu.stevens.cs522.chat.oneway.server.entities.Register;
import edu.stevens.cs522.chat.oneway.server.entities.Synchronize;

/**
 * Created by Xuefan on 3/18/2016.
 */
public class RestMethod {

    public Response perform(Register request) throws IOException {
        Log.v("RestMethod","YES");
        URL url = new URL(request.getRequestUri().toString());
        Log.v("url:", url.toString());
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod(Properties.POST_METHOD);
        httpURLConnection.setDoInput(true);
        Map<String, String> headers = request.getRequestHeaders();
        for (Map.Entry<String, String> header : headers.entrySet()){
            httpURLConnection.addRequestProperty(header.getKey(), header.getValue());
        }
        StringBuilder entityBuilder = new StringBuilder();
        if (httpURLConnection.getResponseCode() == Properties.CREATED){
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null){
                entityBuilder.append(line);
            }
        }
        JsonReader jsonReader = new JsonReader(new StringReader(entityBuilder.toString()));
        Response response = request.getResponse(httpURLConnection, jsonReader);
        httpURLConnection.disconnect();
        return response;
    }

//    public StreamingResponse perform(PostMessage request) throws IOException{
//        URL url = new URL(request.getRequestUri().toString());
//        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//        httpURLConnection.setRequestMethod(Properties.POST_METHOD);
//        httpURLConnection.setDoInput(true);
//        httpURLConnection.setDoOutput(true);
//        Map<String, String> headers = request.getRequestHeaders();
//        for (Map.Entry<String, String> header : headers.entrySet()){
//            httpURLConnection.addRequestProperty(header.getKey(), header.getValue());
//        }
//        return request.getStreamingResponse(httpURLConnection, null);
//    }


    public StreamingResponse perform(Synchronize request) throws IOException{
        Log.v("RestMethodSynchronize","YES");
        URL url = new URL(request.getRequestUri().toString());
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod(Properties.POST_METHOD);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);
        Map<String, String> headers = request.getRequestHeaders();
        for (Map.Entry<String, String> header : headers.entrySet()){
            httpURLConnection.addRequestProperty(header.getKey(), header.getValue());
        }
        JsonWriter jsonWriter = new JsonWriter(new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream(), Properties.ENCODING)));
        String entity = request.getRequestEntity();
        String[] entityArray = entity.split("&");
        jsonWriter.beginArray();
        for (String msg : entityArray){
            if(msg.length() > 0){
                String[] token = msg.split("@");
                jsonWriter.beginObject();
                jsonWriter.name(Properties.CHAT_ROOM).value("_default");
                jsonWriter.name(Properties.TIMESTAMP).value(Long.parseLong(token[1]));
                jsonWriter.name(Properties.TEXT).value(token[0]);
                jsonWriter.endObject();
            }
        }
        jsonWriter.endArray();
        jsonWriter.close();

        StringBuilder entityBuilder = new StringBuilder();
        if (httpURLConnection.getResponseCode() == Properties.OK){
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null){
                entityBuilder.append(line);
            }
        }
        JsonReader jsonReader = new JsonReader(new StringReader(entityBuilder.toString()));
        Response r = request.getResponse(httpURLConnection, jsonReader);
        return new StreamingResponse(httpURLConnection, r);
    }
}
