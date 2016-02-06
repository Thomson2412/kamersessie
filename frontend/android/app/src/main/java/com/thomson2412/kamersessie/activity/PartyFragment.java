package com.thomson2412.kamersessie.activity;

/**
 * Created by tfink on 15-9-2015.
 */
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import com.thomson2412.kamersessie.R;
import com.thomson2412.kamersessie.dataObject.Constans;
import com.thomson2412.kamersessie.dataObject.PartyData;
import com.thomson2412.kamersessie.adapter.PartyAdapter;
import com.thomson2412.kamersessie.database.DBconnector;

public class PartyFragment extends Fragment implements View.OnClickListener {
    private SharedPreferences prefs = null;
    private RecyclerView mRecyclerView;
    private PartyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public PartyFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.party_recycler_view);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        FloatingActionButton addPartyButton = (FloatingActionButton) rootView.findViewById(R.id.fab_normal);
        addPartyButton.setOnClickListener(this);

        new GetPartyTask().execute();

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_normal:
                Intent intent = new Intent(getActivity(), NewPartyActivity.class);
                startActivity(intent);
                break;
        }
    }


    private class GetPartyTask extends AsyncTask<Activity, Void, ArrayList<PartyData>> {


        GetPartyTask(){}

        @Override
        protected void onPreExecute(){
        }

        @Override
        protected ArrayList<PartyData> doInBackground(Activity... params) {
            // TODO: attempt authentication against a network service.
            ArrayList<PartyData> partyData = new ArrayList<>();
            JSONObject response = null;
            String userId = Integer.toString(prefs.getInt("userId", -1));
            try {
                response = new DBconnector().postUrlResponse(Constans.SERVERURL + "getyourpartys.php", "userId="+userId);
            } catch (IOException e){
                Log.e("Exception: ", e.getLocalizedMessage());
            }
            try {
                JSONArray arrayResponse = response.getJSONArray("partys");
                for(int i = 0; i < arrayResponse.length(); i++){
                    String partyId = arrayResponse.getJSONObject(i).getString("partyId");
                    String partyname = arrayResponse.getJSONObject(i).getString("partyname");
                    String partyAdmin = arrayResponse.getJSONObject(i).getString("partyAdmin");
                    String created = arrayResponse.getJSONObject(i).getString("created");
                    PartyData pData = new PartyData(partyId,partyname,partyAdmin,created);
                    partyData.add(pData);
                }

            } catch (JSONException e) {
                Log.e("Exception: ", e.getLocalizedMessage());
            } catch (NullPointerException e){
                e.printStackTrace();
            }

            // TODO: register the new account here.
            return partyData;
        }

        @Override
        protected void onPostExecute(final ArrayList<PartyData> partydata) {
            LinearLayout linlaHeaderProgress = (LinearLayout) getView().findViewById(R.id.linlaHeaderProgress);
            linlaHeaderProgress.setVisibility(View.GONE);
            if (!partydata.isEmpty()) {
                RecyclerView partys = (RecyclerView) getView().findViewById(R.id.party_recycler_view);
                partys.setVisibility(View.VISIBLE);
                mAdapter = new PartyAdapter(partydata, getActivity());
                mRecyclerView.setAdapter(mAdapter);
            } else {
                TextView noParty = (TextView) getView().findViewById(R.id.noParty);
                noParty.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onCancelled() {

        }

    }
}