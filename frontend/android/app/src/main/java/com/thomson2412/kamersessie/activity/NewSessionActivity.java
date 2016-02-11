package com.thomson2412.kamersessie.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import com.thomson2412.kamersessie.R;
import com.thomson2412.kamersessie.dataObject.PartyData;
import com.thomson2412.kamersessie.database.DBconnector;
import com.thomson2412.kamersessie.dataObject.Constans;

public class NewSessionActivity extends AppCompatActivity implements View.OnClickListener{

    private Toolbar mToolbar;
    private SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_session);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_activity_new_session);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Button cS = (Button) findViewById(R.id.create_session);
        cS.setOnClickListener(this);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create_session:
                EditText sN = (EditText) findViewById(R.id.sessionname);
                EditText sL = (EditText) findViewById(R.id.session_location);
                String sessioname = sN.getText().toString();
                String location = sL.getText().toString();
                int userId = prefs.getInt("userId", -1);
                if(sessioname.length() > 0 && sessioname.length() < 50
                        && location.length() > 0 &&location.length() < 50){
                    if(userId > 0){
                        new CreateSession(sessioname, location).execute();
                    }
                }
                else{
                    showToast("Name and location must be above 0 and under 50 charaters");
                }
                break;
        }
    }

    private void showToast(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private class CreateSession extends AsyncTask<Activity, Void, Boolean> {

        private final String sessionname;
        private final String location;

        CreateSession(String sN, String nL) {
            sessionname = sN;
            location = nL;
        }

        @Override
        protected void onPreExecute(){
        }

        @Override
        protected Boolean doInBackground(Activity... params) {
            JSONObject response = null;
            String result = "";
            try {
                String userId = Integer.toString(prefs.getInt("userId" , -1));
                Intent intent = getIntent();
                PartyData partyData = intent.getParcelableExtra("partyData");
                int partyId = Integer.parseInt(partyData.getPartyId());
                Log.e("PartyId", "partyid: " + partyId);
                long epoch = System.currentTimeMillis() / 1000;
                if(partyId > 0) {
                    response = new DBconnector().postUrlResponse(
                            Constans.SERVERURL + "createsession.php",
                            "sessionname=" + sessionname + "&startTime=" + epoch + "&location=" + location +
                                    "&partyId=" + partyId + "&userId=" + userId
                    );
                }
            } catch (IOException e){
                e.printStackTrace();
                return false;
            }

            try {
                result = response.getString("result");
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e){
              return false;
            }
            return result.equals("true");
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                finish();
            } else {
                // TODO: Error melding
            }
        }
    }
}
