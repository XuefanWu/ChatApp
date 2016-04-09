package edu.stevens.cs522.chat.oneway.server.receivers;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by Xuefan on 3/10/2016.
 */
public class ResultReceiverWrapper extends ResultReceiver {

    private IReceiver receiver;

    public ResultReceiverWrapper(Handler handler) {
        super(handler);
    }

    public interface IReceiver {
        public void onReceiveResult(int resultCode, Bundle resultData);
    }

    public void setReceiver(IReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (this.receiver != null) {
            this.receiver.onReceiveResult(resultCode, resultData);
        }

    }
}
