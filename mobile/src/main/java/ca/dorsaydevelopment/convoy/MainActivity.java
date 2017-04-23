package ca.dorsaydevelopment.convoy;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;


import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Button tcpConnect;
    private Button tcpSend;
    private Button tcpDisconnect;
    private TcpClient client;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        tcpConnect = (Button) findViewById(R.id.tcpConnect);
        tcpSend = (Button) findViewById(R.id.tcpSend);
        tcpDisconnect = (Button) findViewById(R.id.tcpDisconnect);

        tcpConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("debug", "Connect");
                new ConnectTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }
        });

        tcpSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SendTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "54|-56.6432|8.8764");
            }
        });

        tcpDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("debug", "Disconnect");
                new DisconnectTask().execute("");
            }
        });

    }

    private class ConnectTask extends AsyncTask<String, Void, TcpClient> {



        @Override
        protected TcpClient doInBackground(String... params) {
            Log.d("debug", "Connect");

            client = new TcpClient(new TcpClient.OnMessageReceived() {
                @Override
                public void messageReceived(String message) {
                    Log.d("debug", "Message received: " + message);
                    Snackbar.make(linearLayout, message, Snackbar.LENGTH_LONG).show();
                }
            });

            client.run();

            return null;
        }
    }

    private class SendTask extends AsyncTask<String, Void, TcpClient> {

        @Override
        protected TcpClient doInBackground(String... message) {
            Log.d("debug", "Send: " + message[0]);


            if(client != null) {
                client.sendMessage(message[0]);
            }

            return null;
        }
    }

    private class DisconnectTask extends AsyncTask<String, Void, TcpClient> {

        @Override
        protected TcpClient doInBackground(String... message) {
            Log.d("debug", "doInBackground: " + message[0]);

            if(client != null) {
                client.stopClient();
            }

            return null;
        }
    }

}
