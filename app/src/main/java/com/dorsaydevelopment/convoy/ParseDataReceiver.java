package com.dorsaydevelopment.convoy;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Brycen on 2015-03-25.
 */
public class ParseDataReceiver extends ParsePushBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("DataReceiver", "Push received");

        try {
            JSONObject data = new JSONObject(intent.getExtras().getString("com.parse.Data"));
            Log.i("DataReceiver", "Data: " + data.get("location").toString());


        } catch (JSONException je) {
            Log.e("ParseDataReceiver", je.toString());
        }

    }
}
