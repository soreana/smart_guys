package com.smartguys.parkooshlocal;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends ActionBarActivity
{

    protected static final String TargetBTDeviceName="HC-06";
    public static SharedPreferences ReporterSharedDB;

    //Button turnOffButton=(Button)findViewById(R.id.turnOffButton);
    //Button turnOnButton=(Button)findViewById(R.id.turnOnButton);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button turnOffButton=(Button)findViewById(R.id.turnOffButton);
        turnOffButton.setEnabled(false);
        ReporterSharedDB=getSharedPreferences(Reporter.SharedPreferencesDBName, MODE_PRIVATE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void turnOnButtonClick(View v)
    {
        final Button turnOffButton=(Button) findViewById(R.id.turnOffButton);
        final Button turnOnButton=(Button) findViewById(R.id.turnOnButton);
        Intent startReportIntent=new Intent(this, Reporter.class);
        startReportIntent.putExtra(Reporter.DevNameParameter, TargetBTDeviceName);
        startService(startReportIntent);
        turnOnButton.setEnabled(false);
        turnOffButton.setEnabled(true);
        ui_delayMS(10000, new Runnable(){@Override
                                         public void run() {handleEnablingButtons();}});
    }

    public void turnOffButtonClick(View v)
    {
        Reporter.stopReporterService();
        handleEnablingButtons();
    }

    protected void handleEnablingButtons()
    {
        final Button turnOffButton=(Button)findViewById(R.id.turnOffButton);
        final Button turnOnButton=(Button)findViewById(R.id.turnOnButton);
        if(ReporterSharedDB.getBoolean(Reporter.RunningStatusKey, false))
        {
            turnOnButton.setEnabled(false);
            turnOffButton.setEnabled(true);
        }
        else
        {
            turnOnButton.setEnabled(true);
            turnOffButton.setEnabled(false);
        }
    }

    protected void ui_delayMS(final int time_ms, final Runnable r)
    {
        runOnUiThread( new Runnable()
                       {

                           @Override
                           public void run() {
                               final Handler handler=new Handler();
                               handler.postDelayed(r, time_ms);
                           }
                       }
        );
    }
}