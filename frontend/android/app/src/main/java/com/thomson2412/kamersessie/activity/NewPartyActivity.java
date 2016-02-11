package com.thomson2412.kamersessie.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.thomson2412.kamersessie.R;
import com.thomson2412.kamersessie.adapter.RecycleAdapter;
import com.thomson2412.kamersessie.dataObject.Constans;
import com.thomson2412.kamersessie.database.DBconnector;

public class NewPartyActivity extends AppCompatActivity implements View.OnClickListener{

    private Toolbar mToolbar;
    Set<String> addedIDS = new HashSet<>();
    private SharedPreferences prefs = null;
    private RecyclerView mRecyclerView;
    private RecycleAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<String> data = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_party);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        mRecyclerView = (RecyclerView) findViewById(R.id.party_recycler_view);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new RecycleAdapter(data);
        mRecyclerView.setAdapter(mAdapter);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.title_activity_new_party);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        EditText personUn = (EditText) findViewById(R.id.personUn);
        personUn.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.new_party_add_new_person || id == EditorInfo.IME_NULL) {
                    addPerson();
                    return true;
                }
                return false;
            }
        });

        Button aP = (Button) findViewById(R.id.add_person);
        aP.setOnClickListener(this);

        Button cP = (Button) findViewById(R.id.create_party);
        cP.setOnClickListener(this);
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
            case R.id.add_person:
                addPerson();
                break;
            case R.id.create_party:
                EditText pN = (EditText) findViewById(R.id.partyname);
                int adminID = prefs.getInt("userId", -1);
                String partyname = pN.getText().toString();
                if(partyname.length() > 0 && partyname.length() < 50) {
                    if (adminID > 0) {
                        new CreateParty(partyname, adminID).execute();
                    }
                }
                else{
                    showToast("Partyname must be above 0 and under 50 charaters");
                }
                break;
        }
    }

    private void showToast(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void addPerson(){
        EditText personUn = (EditText) findViewById(R.id.personUn);
        String pUn = personUn.getText().toString();
        new UserExists(pUn).execute();
        
    }

    private class UserExists extends AsyncTask<Activity, Void, String> {

        private final String mUsername;

        UserExists(String username) {
            mUsername = username;
        }

        @Override
        protected void onPreExecute(){
            EditText pU = (EditText) findViewById(R.id.personUn);
            pU.setEnabled(false);
            Button aP = (Button) findViewById(R.id.add_person);
            aP.setEnabled(false);
        }

        @Override
        protected String doInBackground(Activity... params) {
            JSONObject response;
            String lCRUsername = "";
            String rUsername = "";
            int rId = -1;
            try {
                response = new DBconnector().postUrlResponse(Constans.SERVERURL + "login.php", "username=" + mUsername);
            } catch (IOException e){
                e.printStackTrace();
                return "";
            }
            try {
                rId = Integer.parseInt(response.getString("userId"));
                rUsername = response.getString("username");
                lCRUsername = rUsername.toLowerCase();
            } catch (JSONException e){
                e.printStackTrace();
            } catch (NumberFormatException e){
                return "";
            } catch (NullPointerException e){
                return "";
            }
            Log.d("Info: ", mUsername + " | " + lCRUsername);
            Log.d("Info: ", "id: " + rId);
            if (mUsername.toLowerCase().equals(lCRUsername)){
                if(addedIDS.add(Integer.toString(rId))) {
                    return rUsername;
                }
            }
            return "";
        }

        @Override
        protected void onPostExecute(final String response) {
            EditText pU = (EditText) findViewById(R.id.personUn);
            pU.setEnabled(true);
            Button aP = (Button) findViewById(R.id.add_person);
            aP.setEnabled(true);
            if(!response.isEmpty() || !response.equals("")) {
                mAdapter.add(mAdapter.getItemCount(), response);
            }

        }
    }

    private class CreateParty extends AsyncTask<Activity, Void, Boolean> {

        private final String mPartyname;
        private final int mAdminID;

        CreateParty(String partyname, int adminID) {
            mPartyname = partyname;
            mAdminID = adminID;
        }

        @Override
        protected void onPreExecute(){
        }

        @Override
        protected Boolean doInBackground(Activity... params) {
            JSONObject response;
            String result = "";
            String id = Integer.toString(prefs.getInt("userId" , -1));
            addedIDS.add(id);
            ArrayList<String> arrayListIDS = new ArrayList<>();
            arrayListIDS.addAll(addedIDS);
            String allUserIDS = "";
            for(int i = 0; i < arrayListIDS.size() - 1; i++){
                allUserIDS += arrayListIDS.get(i)+ ":";
            }
            allUserIDS += arrayListIDS.get(addedIDS.size() - 1);
            try {
                response = new DBconnector().postUrlResponse(
                        Constans.SERVERURL + "createparty.php",
                        "partyname="+mPartyname+"&partyAdmin="+mAdminID+"&userIDS="+allUserIDS
                );
            } catch (IOException e){
                Log.e("Exception: ", e.getLocalizedMessage());
                return false;
            }

            try {
                result = response.getString("result");
            } catch (JSONException e) {
                Log.e("Exception: ", e.getLocalizedMessage());
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
