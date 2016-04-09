package edu.stevens.cs522.chat.oneway.server.contract;

/**
 * Created by Xuefan on 3/18/16.
 */
public class Properties {

    public static final String ENCODING = "UTF-8";
    public static final String SETTING_PREFERENCES = "SETTINGS";


    public static final String CLIENT_NAME = "client_name";
    public static final String RESPONSE_CODE = "response_code";
    public static final String CHAT_ROOM = "chatroom";
    public static final String TIMESTAMP = "timestamp";
    public static final String TEXT = "text";

    public static final String POST_METHOD = "POST";

    public static final String UUID_KEY = "uuid";
    public static final String CLIENT_ID_KEY = "client_id_key";
    public static final String CLIENT_NAME_KEY = "client_name_key";
    public static final String SERVER_URL_KEY = "server_url_key";
    public static final String SEQUENCE_NUMBER = "sequence_number";

    public static final int OK = 200;
    public static final int CREATED = 201;

    public static final String DEFAULT_URL = "http://localhost:8080/chat";
    public static final String DEFAULT_NAME = "client";
    public static final long DEFAULT_SEQNUM = 0;
    public static final int DEFAULT_PORT = 8080;
}
