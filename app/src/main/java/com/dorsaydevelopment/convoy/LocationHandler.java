package com.dorsaydevelopment.convoy;


import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by brycen on 15-03-11.
 */
public class LocationHandler implements
        ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    GoogleApiClient client;
    Location lastLocation;
    LocationRequest locationRequest;
    String groupId;
    SharedPreferences preferences;

    private String PACKAGE_NAME = "com.dorsaydevelopment.convoy";
    private int LOCATION_REQUEST_INTERVAL = 10 * 1000;
    private int FASTEST_LOCATION_REQUEST_INTERVAL = 2 * 1000;

    public LocationHandler(Context context) {
        client = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(LOCATION_REQUEST_INTERVAL)
                .setFastestInterval(FASTEST_LOCATION_REQUEST_INTERVAL);

        preferences = context.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
    }

    public void connectClient() {
        Log.i("GoogleApiClient", "Connected");
        groupId = preferences.getString(PACKAGE_NAME + ".activeGroup", "");
        client.connect();
    }

    public void disconnectClient() {
        Log.i("GoogleApiClient", "Disconnected");
        client.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i("GoogleApiClient", "Connected. GroupID: " + groupId);
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(client);
        Log.i("GoogleApiClient", "Last location > " + lastLocation);
        LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onLocationChanged(Location location) {
        Log.i("GoogleApiClient", "Location update > " + location + " for group: " + groupId);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("GoogleClientApi", "Connection failed > " + connectionResult.toString());
    }
}
