package com.thomson2412.kamersessie.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

import com.thomson2412.kamersessie.R;
import com.thomson2412.kamersessie.dataObject.Constans;
import com.thomson2412.kamersessie.database.DBconnector;
import com.thomson2412.kamersessie.service.CheckNewSessionService;

/**
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends AppCompatActivity implements OnClickListener{

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    /*private static final String[] DUMMY_CREDENTIALS = new String[]{
            "world:hello", "hello.com:world"
    };*/
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    private SharedPreferences prefs = null;

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private String mKeepUsername = "";
    private String mKeepPassword = "";
    //private View mLoginFormView;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        Intent startService = new Intent(this, CheckNewSessionService.class);
        startService(startService);

        setContentView(R.layout.activity_login);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.title_activity_login);


        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        boolean keepSigned = prefs.getBoolean("keepSignedIn", false);
        if(keepSigned){
            mKeepUsername = prefs.getString("username", "");
            mKeepPassword = prefs.getString("password", "");
            if(!mKeepUsername.equals("") && !mKeepPassword.equals("")) {
                new UserLoginTask(mKeepUsername, mKeepPassword, true).execute();
            }
        }

// Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.username);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(this);

        TextView registerTV = (TextView) findViewById(R.id.registerTextView);
        registerTV.setOnClickListener(this);

        //mLoginFormView = findViewById(R.id.login_form);
    }

    @Override
    public void onClick(View v){
        int viewId = v.getId();

        switch (viewId){
            case R.id.sign_in_button:
                attemptLogin();
                break;
            case R.id.registerTextView:
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid username, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean keepSigned = prefs.getBoolean("keepSignedIn", false);
        if(keepSigned){
            username = mKeepUsername;
            password = mKeepPassword;
        }

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid username address.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                // fetch data
                CheckBox check = (CheckBox) findViewById(R.id.checkBox);
                mAuthTask = new UserLoginTask(username, password, check.isChecked());
                mAuthTask.execute();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("No connection")
                        .setMessage("This app needs an internet connection")
                        .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
            }

        }
    }

    private boolean isUsernameValid(String username) {
        //TODO: Replace this with your own logic
        return username.length() < 40;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() < 40;
    }


    private void startMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    private class UserLoginTask extends AsyncTask<Activity, Void, Boolean> {

        private final String mUsername;
        private final String mPassword;
        private final boolean keepSigned;

        UserLoginTask(String username, String password, boolean keep) {
            mUsername = username;
            mPassword = password;
            keepSigned  = keep;
        }

        @Override
        protected void onPreExecute(){
            progress = ProgressDialog.show(LoginActivity.this, "Singing in",
                    "Please wait", true);
        }

        @Override
        protected Boolean doInBackground(Activity... params) {
            // TODO: attempt authentication against a network service.
            JSONObject response = null;
            String rUsername = "";
            String rPassword = "";
            int rId = -1;
            try {
                response = new DBconnector().postUrlResponse(Constans.SERVERURL + "login.php", "username="+mUsername);
            } catch (IOException e){
                e.printStackTrace();
                return false;
            }

            try {
                rId = Integer.parseInt(response.getString("userId"));
                rUsername = response.getString("username");
                rUsername = rUsername.toLowerCase();
                rPassword = response.getString("password");
            } catch (JSONException e){
                Log.e("Exception: ", e.getLocalizedMessage());
            } catch (NumberFormatException e){
                return false;
            }
            Log.d("Info: ", mUsername + " | " + rUsername);
            Log.d("Info: ", mPassword + " | " + rPassword);
            Log.d("Info: ", "id: " + rId);
            if (mUsername.toLowerCase().equals(rUsername)) {
                // Account exists, return true if the password matches.
                prefs.edit().putString("username", mUsername).apply();
                prefs.edit().putString("password", mPassword).apply();
                prefs.edit().putInt("userId", rId).apply();
                return mPassword.equals(rPassword);
            }

            // TODO: register the new account here.
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            progress.dismiss();
            int id = prefs.getInt("userId", -1);
            Log.d("Info: ", "id: " + id);
            if (success && id != -1) {
                CheckBox check = (CheckBox) findViewById(R.id.checkBox);
                if(keepSigned){
                    prefs.edit().putBoolean("keepSignedIn", true).apply();
                }
                finish();
                startMain();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            progress.dismiss();
        }

    }
}

