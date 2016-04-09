package edu.stevens.cs522.chat.oneway.server.interfaces;

import java.net.InetAddress;

/**
 * Created by Xuefan on 3/9/2016.
 */
public interface IChatSendService {

    public void send(InetAddress destAddr, byte[] sendData, int destPort);

}
