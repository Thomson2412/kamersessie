package com.thomson2412.kamersessie.activity;

/**
 * Created by tfink on 15-9-2015.
 */

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.thomson2412.kamersessie.R;
import com.thomson2412.kamersessie.dataObject.Constans;
import com.thomson2412.kamersessie.dataObject.SessionData;
import com.thomson2412.kamersessie.adapter.SessionAdapter;

import java.io.IOException;
import java.util.ArrayList;

import com.thomson2412.kamersessie.database.DBconnector;


public class ActiveSessionsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private SessionAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SharedPreferences prefs = null;

    public ActiveSessionsFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_active_sessions, container, false);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.session_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        new GetSessionsTask().execute();

        return rootView;
    }

    private void showToast(String msg){
        Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }


    private class GetSessionsTask extends AsyncTask<Activity, Void, ArrayList<SessionData>> {


        GetSessionsTask(){}

        @Override
        protected void onPreExecute(){
        }

        @Override
        protected ArrayList<SessionData> doInBackground(Activity... params) {
            // TODO: attempt authentication against a network service.
            ArrayList<SessionData> sessionData = new ArrayList<>();
            JSONObject response = null;
            String userId = Integer.toString(prefs.getInt("userId", -1));
            try {
                response = new DBconnector().postUrlResponse(Constans.SERVERURL + "getyoursessions.php", "userId="+userId);
            } catch (IOException e){
                Log.e("Exception: ", e.getLocalizedMessage());
            }
            try {
                JSONArray arrayResponse = response.getJSONArray("sessions");
                for(int i = 0; i < arrayResponse.length(); i++){
                    String sessionname = arrayResponse.getJSONObject(i).getString("sessionname");
                    String sessionCreator = arrayResponse.getJSONObject(i).getString("username");
                    String partyname = arrayResponse.getJSONObject(i).getString("partyname");
                    String location = arrayResponse.getJSONObject(i).getString("location");
                    String epoch = arrayResponse.getJSONObject(i).getString("startTime");
                    SessionData sData = new SessionData(sessionname,sessionCreator,location,partyname,epoch);
                    sessionData.add(sData);
                }

            } catch (JSONException e) {
                Log.e("Exception: ", e.getLocalizedMessage());
            }

            // TODO: register the new account here.
            return sessionData;
        }

        @Override
        protected void onPostExecute(final ArrayList<SessionData> sessionData) {
            if(getView() != null) {
                LinearLayout linlaHeaderProgress = (LinearLayout) getView().findViewById(R.id.linlaHeaderSessionProgress);
                linlaHeaderProgress.setVisibility(View.GONE);
                if (!sessionData.isEmpty()) {
                    RecyclerView sessions = (RecyclerView) getView().findViewById(R.id.session_recycler_view);
                    sessions.setVisibility(View.VISIBLE);
                    mAdapter = new SessionAdapter(sessionData, getActivity());
                    mRecyclerView.setAdapter(mAdapter);
                } else {
                    TextView noSession = (TextView) getView().findViewById(R.id.noSessions);
                    noSession.setVisibility(View.VISIBLE);
                }
            }
        }
        @Override
        protected void onCancelled() {

        }

    }


    /*private class GetDataFromDatabase extends AsyncTask<String, Void, ArrayList<HighscoreData>> {
        @Override
        protected ArrayList<HighscoreData> doInBackground(String... args) {
            ArrayList<HighscoreData> response = new ArrayList<HighscoreData>();
            SQLiteDatabase db = mDbHelper.getReadableDatabase();

            String[] projection = {
                    HighscoreEntry.COLUMN_NAME_USERNAME,
                    HighscoreEntry.COLUMN_NAME_HIGHSCORE,
                    HighscoreEntry.COLUMN_NAME_DATE
            };

            Cursor c = db.query(
                    HighscoreEntry.TABLE_NAME,  // The table to query
                    projection,                               // The columns to return
                    null,                                // The columns for the WHERE clause
                    null,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null                                 // The sort order
            );

            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                String username = c.getString(c.getColumnIndex(HighscoreEntry.COLUMN_NAME_USERNAME));
                int highscore = c.getInt(c.getColumnIndex(HighscoreEntry.COLUMN_NAME_HIGHSCORE));
                String date = c.getString(c.getColumnIndex(HighscoreEntry.COLUMN_NAME_DATE));
                response.add(new HighscoreData(username,highscore,date));
                c.moveToNext();
            }
            return response;
        }

        @Override
        protected void onPostExecute(ArrayList<HighscoreData> result) {
            ArrayList<String> data = new ArrayList<>();
            for(HighscoreData d : result){
                data.add(d.toString());
            }
            mAdapter = new RecycleAdapter(data);
            mRecyclerView.setAdapter(mAdapter);
        }
    }*/

}