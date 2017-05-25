package ca.dorsaydevelopment.convoy;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class WebSocketService extends Service {

    private WebSocketClient socket;

    public WebSocketService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String address = intent.getStringExtra("address");
        URI uri = null;
        try {
            uri = new URI(address);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        socket = createWebSocket(uri);
        socket.connect();

        // TODO: Do real things here instead of made up things
//        socket.close();

        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
    }

    private WebSocketClient createWebSocket(URI uri) {
        WebSocketClient client = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.d("debug", "Websocket connection opened");
                this.send("{\"data\": \"hello world\"}");
            }

            @Override
            public void onMessage(String s) {
                Log.d("debug", "Websocket message: " + s);
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.d("debug", "Websocket connection closed");

            }

            @Override
            public void onError(Exception e) {
                Log.e("debug", "Error: " + e);
            }
        };

        return client;
    }
}
