package com.thomson2412.kamersessie;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import com.thomson2412.kamersessie.activity.LoginActivity;

/**
 * Created by Thomas on 8-11-2015.
 */
public class Functions {
    public static void logout(Context context){
        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().commit();
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }
}
