package com.dorsaydevelopment.convoy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;


public class MapActivity extends ActionBarActivity implements OnMapReadyCallback {

    private Location lastLocation;
    private Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Intent intent = getIntent();
        final String groupId = intent.getExtras().getString("groupId");
        final String groupName = intent.getExtras().getString("groupName");
        setTitle(groupName);

        ParseQuery<Group> query = Group.getQuery();
        query.whereEqualTo("objectId", groupId);
        query.findInBackground(new FindCallback<Group>() {
            @Override
            public void done(List<Group> groups, ParseException e) {
                if (e == null && groups.size() > 0) {
                    group = groups.get(0);
                    populateMap();
                } else if (e != null) {
                    Log.e("Group", "Error getting group info > " + e.toString());
                    finish();
                } else {
                    Log.e("Group", "No group found that matches id " + groupId);
                    finish();
                }
                // TODO: Hide progress spinner
            }
        });
    }

    private void populateMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        List<ParseGeoPoint> pitStops = group.getPitStops();

//        for(ParseGeoPoint pitStop : pitStops) {
//
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        lastLocation = ApplicationController.locationHandler.getLastLocation();
        final double lat = lastLocation.getLatitude();
        double lon = lastLocation.getLongitude();
        LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());

        // TODO: Set onLongClickLister for dropping new pins for pit stops and save to database
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                createPitStop(googleMap, latLng);
            }
        });

        googleMap.setMyLocationEnabled(true);

        CameraUpdate center = CameraUpdateFactory.newLatLngZoom(latLng, 15);

        googleMap.moveCamera(center);

    }

    private void createPitStop(final GoogleMap map, final LatLng location) {
        //TODO: The final location may be a problem
        final ParseObject pitStop = new ParseObject("PitStops");

        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        builder.setTitle("Create Pit Stop");
        final EditText titleText = new EditText(this);
        titleText.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(titleText);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String titleResult = titleText.getText().toString();
                pitStop.put("location", new ParseGeoPoint(location.latitude, location.longitude));
                pitStop.put("title", titleResult);
                pitStop.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(getApplicationContext(), "Pit Stop created", Toast.LENGTH_SHORT).show();
                            map.addMarker(new MarkerOptions()
                                    .position(location)
                                    .title("Hello world"));
                        }
                    }
                });

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }
}
