package com.thomson2412.kamersessie.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.thomson2412.kamersessie.activity.MainActivity;
import com.thomson2412.kamersessie.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import com.thomson2412.kamersessie.dataObject.Constans;
import com.thomson2412.kamersessie.dataObject.SessionData;
import com.thomson2412.kamersessie.database.DBconnector;

public class CheckNewSessionService extends Service {
    private static final int VIEW_FRAGMENT = 1;
    private static final int NOTIFICATION_ID = 1;
    private String lastSessionId = "";
    private Handler handler;
    private final static int CHECKTIME = 5 * 60 * 1000;

    public CheckNewSessionService() {
    }


    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        Log.d("Serviceintnet", "OnCreate");
        HandlerThread thread = new HandlerThread("MyHandlerThread");
        thread.start();
        handler = new Handler(thread.getLooper());
        handler.postDelayed(runnable, CHECKTIME);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            handleNewSession(prefs.getInt("userId", -1));
            handler.postDelayed(this, CHECKTIME);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        Log.d("Serviceintnet", "Destroyed");
    }

    private void handleNewSession(int id) {
        Log.d("Serviceintnet", "check new session");
        //ArrayList<SessionData> sessionData = new ArrayList<>();
        JSONObject response = null;
        if(id > 0 && isOnline()) {
            Log.d("Serviceintnet", "id" + id + "ok");
            try {
                response = new DBconnector().postUrlResponse(Constans.SERVERURL + "getyoursessions.php", "userId=" + id);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                JSONArray arrayResponse = response.getJSONArray("sessions");
                /*for (int i = 0; i < arrayResponse.length(); i++) {
                    String sessionname = arrayResponse.getJSONObject(i).getString("sessionname");
                    String sessionCreator = arrayResponse.getJSONObject(i).getString("username");
                    String partyname = arrayResponse.getJSONObject(i).getString("partyname");
                    String location = arrayResponse.getJSONObject(i).getString("location");
                    String epoch = arrayResponse.getJSONObject(i).getString("startTime");
                    SessionData sData = new SessionData(sessionname,sessionCreator,location,partyname,epoch);
                    sessionData.add(sData);
                }*/

                if(arrayResponse.length() > 0
                        && !lastSessionId.equals(arrayResponse.getJSONObject(arrayResponse.length()-1).getString("sessionId"))){
                    Log.e("Serviceintnet", "lastsession !lastsession");
                    int i = arrayResponse.length() - 1;
                    String sessionId = arrayResponse.getJSONObject(i).getString("sessionId");
                    String sessionname = arrayResponse.getJSONObject(i).getString("sessionname");
                    String sessionCreator = arrayResponse.getJSONObject(i).getString("username");
                    String partyname = arrayResponse.getJSONObject(i).getString("partyname");
                    String location = arrayResponse.getJSONObject(i).getString("location");
                    String epoch = arrayResponse.getJSONObject(i).getString("startTime");
                    SessionData sData = new SessionData(sessionname,sessionCreator,location,partyname,epoch);
                    showNotification(sData.getSessionCreator(),sData.getLocation());
                    lastSessionId = sessionId;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            catch (NullPointerException e) {
                Log.d("Serviceintnet", "No data recieved");
            }
        }
    }

    private void showNotification(String creator, String location){
        Log.e("Serviceintnet", "notyfi");
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_profile)
                        .setContentTitle("Kamer sessie geactiveerd")
                        .setContentText("Nieuwe Sessie van " + creator + " op " + location);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = mBuilder.build();
        notification.defaults |= Notification.DEFAULT_ALL;
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
