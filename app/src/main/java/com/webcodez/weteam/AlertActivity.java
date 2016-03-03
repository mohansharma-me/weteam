package com.webcodez.weteam;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;


public class AlertActivity extends ActionBarActivity {

    MediaPlayer mediaPlayer=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaPlayer=MediaPlayer.create(this,R.raw.alarm1);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_alert);

        try {
            Intent intent=getIntent();
            if(intent!=null) {
                String json=intent.getStringExtra("alertJson");
                if(json!=null && json.length()>0) {
                    startAlarm(new JSONObject(json));
                }
            }
        } catch (Exception ex) { Toast.makeText(this,"Unable to start alert. ["+ex+"]",Toast.LENGTH_LONG).show(); ex.printStackTrace(); }

    }

    private void startAlarm(JSONObject json) {
        try {
            ((TextView)findViewById(R.id.lblName)).setText(json.getString("userName"));
            ((TextView)findViewById(R.id.lblMessage)).setText(json.getString("alertMessage"));
            ((TextView)findViewById(R.id.lblAddress)).setText(json.getString("userAddress"));
            mediaPlayer.start();
        } catch (Exception ex) {
            Toast.makeText(this,"Unable to initiate alert. ["+ex+"]",Toast.LENGTH_LONG).show();
        }
    }

    public void stopAlert(View view) {
        mediaPlayer.stop();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
