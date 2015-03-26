package com.dorsaydevelopment.convoy;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;

/**
 * Created by Brycen on 2015-03-25.
 */
public class ParseDataReceiver extends ParsePushBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("DataReceiver", "Push received");
    }
}
