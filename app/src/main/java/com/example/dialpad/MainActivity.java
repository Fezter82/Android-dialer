package com.example.dialpad;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    private SensorManager sm;
    private Sensor accelerometer;
    private TextView x, y, z;
    private Toast directionToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        // Check permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request permissions
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }

        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(MainActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        // Set default values if no values are set
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission denied
                    // tell the user with a toast
                    Context context = getApplicationContext();
                    CharSequence text = getString(R.string.toast_permissionNotGranted);
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbaritems, menu);
        return true;
    }

    // Handle selections on the menu of the app bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;

            case R.id.action_call_list:
                Intent callListIntent = new Intent(this, CallListActivity.class);
                startActivity(callListIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //Feeds new X, Y, Z-values when accelerometer/gyro has detected a motion
    @Override
    public void onSensorChanged(SensorEvent event) {

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        //if phone is pushed downwards/backwards
        if(event.values[2] > 15){
            if (directionToast != null) {
                directionToast.cancel();
            }
            directionToast = Toast.makeText(context, "Click!", duration);
            directionToast.show();
        }
        //if phone is tilted forward(away from user) with enough directional speed(>10)
        else if(event.values[1] < 6 && event.values[2] > 10){
            //if a toast is already open, cancel it
            if (directionToast != null) {
                directionToast.cancel();
            }
            directionToast = Toast.makeText(context, "Uppåt!", duration);
            directionToast.show();
        }
        //if phone is tilted backwards(against user) with enough directional speed(<2)
        else if(event.values[1] > 8 && event.values[2] < 2){
            if (directionToast != null) {
                directionToast.cancel();
            }
            directionToast = Toast.makeText(context, "Neråt!", duration);
            directionToast.show();
        }
        //if phone is tilted left
        else if(event.values[0] > 3){
            if (directionToast != null) {
                directionToast.cancel();
            }
            directionToast = Toast.makeText(context, "Vänster!", duration);
            directionToast.show();
        }
        //if phone is tilted right
        else if(event.values[0] < -3){
            if (directionToast != null) {
                directionToast.cancel();
            }
            directionToast = Toast.makeText(context, "Höger!", duration);
            directionToast.show();
        }


    }
}
