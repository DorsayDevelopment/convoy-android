package com.dorsaydevelopment.convoy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.facebook.AppEventsLogger;
import com.parse.ParseUser;

public class DispatchActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatch);

        if (ParseUser.getCurrentUser() != null) {
            // User is already logged in -> Go to main activity
            startActivity(new Intent(this, MainActivity.class));
        } else {
            // No user logged in
            startActivity(new Intent(this, AuthenticationActivity.class));
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }
}
