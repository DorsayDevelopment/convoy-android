package ca.dorsaydevelopment.convoy;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by brycen on 2017-04-25.
 */

public class UdpClient {
    private AsyncTask<Void, Void, Void> async_client;
    private DatagramSocket socket;
    private InetAddress serverAddress;

    private int serverPort;

    public UdpClient() {

    }

    public void connect(String serverName, int serverPort) {

        try {
            this.serverAddress = InetAddress.getByName(serverName);
            this.serverPort = serverPort;

            socket = new DatagramSocket();

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


    }

    public boolean send(String message) {
        try {
            DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), serverAddress, serverPort);
            socket.send(packet);
            return true;
        } catch(IOException ex) {
            return false;
        }

    }

    public void disconnect() {
        if(socket.isConnected()) {
            socket.close();
        }
    }
}
