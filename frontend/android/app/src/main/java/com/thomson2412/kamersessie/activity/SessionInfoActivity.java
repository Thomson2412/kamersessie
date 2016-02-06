package com.thomson2412.kamersessie.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import com.thomson2412.kamersessie.R;
import com.thomson2412.kamersessie.dataObject.SessionData;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SessionInfoActivity extends AppCompatActivity {

    private TextView tvLocation;
    private TextView tvParty;
    private TextView tvCreator;
    private TextView tvStartTime;
    private SessionData sessionData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvLocation = (TextView) findViewById(R.id.session_info_location);
        tvParty = (TextView) findViewById(R.id.session_info_party);
        tvCreator = (TextView) findViewById(R.id.session_info_creator);
        tvStartTime = (TextView) findViewById(R.id.session_info_date);

        sessionData = getIntent().getParcelableExtra("sessiondata");
        fillData(sessionData);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(sessionData.getSessionname());
    }

    private void fillData(SessionData sD){
        tvLocation.setText(sD.getLocation());
        tvParty.setText(sD.getPartyname());
        tvCreator.setText(sD.getSessionCreator());

        long epoch = Long.parseLong(sD.getStartTime()) * 1000;
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy HH:mm");
        tvStartTime.setText(sdf.format(new Date(epoch)));
    }
}
