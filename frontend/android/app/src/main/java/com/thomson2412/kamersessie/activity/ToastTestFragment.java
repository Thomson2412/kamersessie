package com.thomson2412.kamersessie.activity;

/**
 * Created by tfink on 15-9-2015.
 */
import android.app.Fragment;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.thomson2412.kamersessie.R;
import com.thomson2412.kamersessie.database.DbHelper;
import com.thomson2412.kamersessie.database.DBContract.HighscoreEntry;

public class ToastTestFragment extends Fragment implements OnClickListener {

    private EditText _usernameEditText;
    private EditText _highscoreEditText;
    DbHelper mDbHelper;

    public ToastTestFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_toast_test, container, false);
        _usernameEditText = (EditText)rootView.findViewById(R.id.editText);
        _highscoreEditText = (EditText)rootView.findViewById(R.id.scoreText);
        Button b = (Button) rootView.findViewById(R.id.toast_button);
        b.setOnClickListener(this);

        mDbHelper = new DbHelper(getActivity().getApplicationContext());

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toast_button:
                showToast(_usernameEditText.getText().toString() + ": " + _highscoreEditText.getText().toString());
                break;
            default:
                break;
        }
    }

    private void showToast(String msg){
        Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        new DatabaseInsert().execute(_usernameEditText.getText().toString(), _highscoreEditText.getText().toString());
    }


    private class DatabaseInsert extends AsyncTask<String, Void, Long> {
        @Override
        protected Long doInBackground(String... args) {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            DateFormat df = new SimpleDateFormat("yyyy-MM-d HH:mm:ss");
            String date = df.format(Calendar.getInstance().getTime());

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(HighscoreEntry.COLUMN_NAME_USERNAME, args[0]);
            values.put(HighscoreEntry.COLUMN_NAME_HIGHSCORE, args[1]);
            values.put(HighscoreEntry.COLUMN_NAME_DATE, date);

            // Insert the new row, returning the primary key value of the new row
            long newRowId;
            newRowId = db.insert(
                    HighscoreEntry.TABLE_NAME,
                    "NULL",
                    values);

            return newRowId;
        }

        @Override
        protected void onPostExecute(Long result) {

        }
    }
}