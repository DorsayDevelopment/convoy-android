package ca.dorsaydevelopment.convoy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;


public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private Button connect;
    private Button send;
    private Button openMap;
    private Button getLocation;
    private UdpClient client;
    private LinearLayout linearLayout;
    private GoogleApiClient googleApiClient;
    private Location location;

    private static final String SERVERNAME = "192.168.0.120";
    private static final int SERVERPORT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        connect = (Button) findViewById(R.id.tcpConnect);
        send = (Button) findViewById(R.id.tcpSend);
        openMap = (Button) findViewById(R.id.openMap);
        getLocation = (Button) findViewById(R.id.getLocation);


        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("debug", "Connect");
                new ConnectTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "";
                if(location != null) {
                    message += "69" + "|";
                    message += location.getLatitude() + "|";
                    message += location.getLongitude() + "|";
                    message += location.getAltitude() + "|";
                    message += location.getSpeed() + "|";
                } else {
                    message = "54|-56.6432|8.8764";
                }
                new SendTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, message);
            }
        });

        openMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("debug", "Open Map");
                Intent intent = new Intent(v.getContext(), MapActivity.class);
                startActivity(intent);

            }
        });

        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("debug", "Get Location");
                getLocation();
                Snackbar.make(linearLayout, location.getLatitude() + "|" + location.getLongitude(), Snackbar.LENGTH_LONG).show();
            }
        });

        if(googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();
        }



    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, 1);
            return;
        }

        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Check location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class ConnectTask extends AsyncTask<String, Void, TcpClient> {

        @Override
        protected TcpClient doInBackground(String... params) {
            Log.d("debug", "Connect");
            Snackbar.make(linearLayout, "Connected", Snackbar.LENGTH_SHORT).show();


            client = new UdpClient();
            client.connect(SERVERNAME, SERVERPORT);

            Snackbar.make(linearLayout, "Disconnected", Snackbar.LENGTH_SHORT).show();

            return null;
        }
    }

    private class SendTask extends AsyncTask<String, Void, TcpClient> {

        @Override
        protected TcpClient doInBackground(String... message) {
            Log.d("debug", "Send: " + message[0]);


            if(client != null) {
                client.send(message[0]);
            }

            return null;
        }
    }

}
