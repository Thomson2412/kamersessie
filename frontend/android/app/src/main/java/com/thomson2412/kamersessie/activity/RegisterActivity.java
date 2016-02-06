package com.thomson2412.kamersessie.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.thomson2412.kamersessie.R;
import com.thomson2412.kamersessie.dataObject.Constans;
import com.thomson2412.kamersessie.database.DBconnector;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText etUsername;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etUsername = (EditText) findViewById(R.id.reg_username);
        etPassword = (EditText) findViewById(R.id.reg_password);
        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    onClick(findViewById(R.id.register_button));
                    return true;
                }
                return false;
            }
        });

        Button register = (Button) findViewById(R.id.register_button);
        register.setOnClickListener(this);

    }


    public void onClick(View v){
        int viewId = v.getId();

        switch (viewId){
            case R.id.register_button:
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                new UserRegisterTask(username,password).execute();
                break;

            default:
                break;
        }
    }

    private class UserRegisterTask extends AsyncTask<Activity, Void, Boolean> {

        private final String mUsername;
        private final String mPassword;
        ProgressDialog progress;

        UserRegisterTask(String username, String password) {
            mUsername = username;
            mPassword = password;
        }

        @Override
        protected void onPreExecute(){
            if(mUsername == null || mUsername.equals("")){
                this.cancel(true);
            }
            if(mPassword == null || mPassword.equals("")){
                this.cancel(true);
            }
            progress = ProgressDialog.show(RegisterActivity.this, "Registering",
                    "Please wait", true);
        }

        @Override
        protected Boolean doInBackground(Activity... params) {
            // TODO: attempt authentication against a network service.
            JSONObject response = null;
            String result = null;
            try {
                response = new DBconnector().postUrlResponse(Constans.SERVERURL + "register.php", "username="+mUsername + "&password=" + mPassword);
            } catch (IOException e){
                e.printStackTrace();
                return false;
            }

            try {
                result = response.getString("result");
            } catch (JSONException e){
                Log.e("Exception: ", e.getLocalizedMessage());
            } catch (NumberFormatException e){
                return false;
            }
            // TODO: register the new account here.
            return "true".equals(result);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            progress.dismiss();
            if (success) {
                finish();
            } else {
                showToast("Errooooor");
                //mPasswordView.setError(getString(R.string.error_incorrect_password));
                //mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            if(progress != null) {
                progress.dismiss();
            }
            showToast("Shit moet ingevult");
        }

    }

    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
