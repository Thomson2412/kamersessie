package com.thomson2412.kamersessie.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import com.thomson2412.kamersessie.Functions;
import com.thomson2412.kamersessie.R;
import com.thomson2412.kamersessie.adapter.RecycleAdapter;
import com.thomson2412.kamersessie.dataObject.Constans;
import com.thomson2412.kamersessie.dataObject.PartyData;
import com.thomson2412.kamersessie.database.DBconnector;

public class PartyInfoActivity extends AppCompatActivity implements View.OnClickListener{

    private RecyclerView mRecyclerView;
    private RecycleAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SharedPreferences prefs = null;
    private PartyData partyData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        partyData = getIntent().getParcelableExtra("partyData");

        mRecyclerView = (RecyclerView) findViewById(R.id.users_in_party_recycler_view);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(partyData.getPartyname());

        Button aP = (Button) findViewById(R.id.partyinfo_adduser);
        aP.setOnClickListener(this);

        new GetPartyUsers().execute();
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.partyinfo_adduser:
                EditText tvusername = (EditText)findViewById(R.id.partyinfo_username);
                String username = tvusername.getText().toString();
                new UserExists(username).execute();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.add(0, 21, Menu.NONE, "Delete");
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case 21:
                String partyId = partyData.getPartyId();
                int id = prefs.getInt("userId", -1);
                String userId = Integer.toString(id);
                if(id >= 0) {
                    new DeleteParty(partyId, userId).execute();
                }
                return true;
            case R.id.action_logout:
                Functions.logout(this);
                finish();
                return true;
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class GetPartyUsers extends AsyncTask<Activity, Void, ArrayList<String>> {


        GetPartyUsers(){}

        @Override
        protected void onPreExecute(){
        }

        @Override
        protected ArrayList<String> doInBackground(Activity... params) {
            // TODO: attempt authentication against a network service.
            ArrayList<String> userData = new ArrayList<>();
            JSONObject response = null;
            String partyId = partyData.getPartyId();
            try {
                response = new DBconnector().postUrlResponse(Constans.SERVERURL + "getpartyusers.php", "partyId="+partyId);
            } catch (IOException e){
                Log.e("Exception: ", e.getLocalizedMessage());
            }
            try {
                JSONArray arrayResponse = response.getJSONArray("users");
                for(int i = 0; i < arrayResponse.length(); i++){
                    String username = arrayResponse.getJSONObject(i).getString("username");
                    userData.add(username);
                }

            } catch (JSONException e) {
                Log.e("Exception: ", e.getLocalizedMessage());
            } catch (NullPointerException e){
                e.printStackTrace();
            }

            // TODO: register the new account here.
            return userData;
        }

        @Override
        protected void onPostExecute(final ArrayList<String> userdata) {
            if (!userdata.isEmpty()) {
                mAdapter = new RecycleAdapter(userdata);
                mRecyclerView.setAdapter(mAdapter);
            }
        }

        @Override
        protected void onCancelled() {
        }
    }

    private class UserExists extends AsyncTask<Activity, Void, Boolean> {

        private final String mUsername;

        UserExists(String username) {
            mUsername = username;
        }

        @Override
        protected void onPreExecute(){
            EditText pU = (EditText) findViewById(R.id.partyinfo_username);
            pU.setEnabled(false);
            Button aP = (Button) findViewById(R.id.partyinfo_adduser);
            aP.setEnabled(false);
        }

        @Override
        protected Boolean doInBackground(Activity... params) {
            JSONObject response = null;
            String lCRUsername = "";
            String rUsername = "";
            String partyId = partyData.getPartyId();
            int rId = -1;
            try {
                response = new DBconnector().postUrlResponse(Constans.SERVERURL + "login.php", "username=" + mUsername);
            } catch (IOException e){
                e.printStackTrace();
                return false;
            }
            try {
                rId = Integer.parseInt(response.getString("userId"));
                rUsername = response.getString("username");
                lCRUsername = rUsername.toLowerCase();
            } catch (JSONException e){
                e.printStackTrace();
            } catch (NumberFormatException e){
                return false;
            } catch (NullPointerException e){
                return false;
            }
            Log.d("Info: ", mUsername + " | " + lCRUsername);
            Log.d("Info: ", "id: " + rId);

            if(mUsername.toLowerCase().equals(lCRUsername)){
                try {
                    response = new DBconnector().postUrlResponse(Constans.SERVERURL + "addusertoparty.php", "userId=" + rId + "&partyId=" + partyId);
                    return response.getString("added").equals("true");
                } catch (IOException | JSONException | NullPointerException e){
                    e.printStackTrace();
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean response) {
            EditText pU = (EditText) findViewById(R.id.partyinfo_username);
            pU.setEnabled(true);
            Button aP = (Button) findViewById(R.id.partyinfo_adduser);
            aP.setEnabled(true);
            new GetPartyUsers().execute();
        }
    }

    private class DeleteParty extends AsyncTask<Activity, Void, Boolean> {

        private final String partyId;
        private final String userId;

        DeleteParty(String partyId, String userId) {
            this.partyId = partyId;
            this.userId = userId;
        }

        @Override
        protected void onPreExecute(){
        }

        @Override
        protected Boolean doInBackground(Activity... params) {
            JSONObject response = null;
            String result = "";

            try {
                response = new DBconnector().postUrlResponse(Constans.SERVERURL + "deleteparty.php", "partyId=" + partyId + "&userId=" + userId);
            } catch (IOException e){
                e.printStackTrace();
                return false;
            }
            try {
                result = response.getString("result");
            } catch (JSONException e){
                e.printStackTrace();
            } catch (NumberFormatException e){
                return false;
            } catch (NullPointerException e){
                return false;
            }

            return result.toLowerCase().equals("true");
        }

        @Override
        protected void onPostExecute(final Boolean response) {
            finish();
        }
    }
}
