package com.thomson2412.kamersessie.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootedReciver extends BroadcastReceiver {
    private static final String ACTION_FETCH_NEW_SESSION = "Service.action.FETCH_NEW_SESSION";
    public BootedReciver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.e("Boot", "Bootrecieved starting intentservice");
        Intent startService = new Intent(context, CheckNewSessionService.class);
        context.startService(startService);
    }
}
