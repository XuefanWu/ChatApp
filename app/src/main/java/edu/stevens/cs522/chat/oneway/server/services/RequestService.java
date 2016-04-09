package edu.stevens.cs522.chat.oneway.server.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;

import edu.stevens.cs522.chat.oneway.server.entities.Register;
import edu.stevens.cs522.chat.oneway.server.entities.Synchronize;
import edu.stevens.cs522.chat.oneway.server.managers.RequestProcessor;
import edu.stevens.cs522.chat.oneway.server.managers.ServiceHelper;

/**
 * Created by Xuefan on 3/18/2016.
 */
public class RequestService extends IntentService {
    private RequestProcessor requestProcessor;
    public static final String NEW_MESSAGE_BROADCAST =
            "edu.stevens.cs522.chat.oneway.server.services.NewMessageBroadcast";

    public RequestService() {
        super("RequestService");

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        requestProcessor = new RequestProcessor(this);
        int requestType = intent.getIntExtra(ServiceHelper.REQUEST_TYPE,0);
        switch (requestType){
            case ServiceHelper.REGISTER_REQUEST:
                Register register = intent.getParcelableExtra(ServiceHelper.REGISTER);
                try {
                    Log.v("RequestService","YES");
                    requestProcessor.perform(register);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
//            case ServiceHelper.POST_REQUEST:
//                PostMessage postMessage = intent.getParcelableExtra(ServiceHelper.POSTMESSAGE);
//                try {
//                    requestProcessor.perform(postMessage);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                break;
            case ServiceHelper.SYNC_REQUEST:
                Synchronize synchronize = intent.getParcelableExtra(ServiceHelper.SYNCHRONIZE);
                try {
                    requestProcessor.perform(synchronize);
                    Intent msgUpdateBroadcast = new Intent(NEW_MESSAGE_BROADCAST);
                    sendBroadcast(msgUpdateBroadcast);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            default:
                break;
        }
    }
}
