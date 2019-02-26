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
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    private SensorManager sm;
    private Sensor accelerometer;
    private TextView x, y, z;
    private Toast directionToast;


    /**
     * UI-container that contains all buttons etc that can be interacted with using
     * accelerometer.
     *
     * Layout:

     (x:0,y:0)  (x:1,y:0)   (x:2,y:0)
     null       null        Call-list

     (x:0,y:1)  (x:1,y:1)   (x:2,y:1)
     null       delete      call

     (x:0,y:2)  (x:1,y:2)   (x:2,y:2)
     1          2           3

     (x:0,y:3)  (x:1,y:3)   (x:2,y:3)
     4          5           6

     (x:0,y:4)  (x:1,y:4)   (x:2,y:4)
     7          8           9

     (x:0,y:5)  (x:1,y:5)   (x:2,y:5)
     *          0           #


     */
    UIContainer<View> uiContainer;

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


        // put ui-elements in uicontainer
        uiContainer = new UIContainer<>(3, 6);
        uiContainer.set(0, 0, null);
        uiContainer.set(1, 0, null);
        uiContainer.set(2, 0, findViewById(R.id.action_call_list));
        uiContainer.set(0, 1, null);
        uiContainer.set(1, 1, findViewById(R.id.deleteButton));
        uiContainer.set(2, 1, findViewById(R.id.phoneButton));
        uiContainer.set(0, 2, findViewById(R.id.imageButton1));
        uiContainer.set(1, 2, findViewById(R.id.imageButton2));
        uiContainer.set(2, 2, findViewById(R.id.imageButton3));
        uiContainer.set(0, 3, findViewById(R.id.imageButton4));
        uiContainer.set(1, 3, findViewById(R.id.imageButton5));
        uiContainer.set(2, 3, findViewById(R.id.imageButton6));
        uiContainer.set(0, 4, findViewById(R.id.imageButton7));
        uiContainer.set(1, 4, findViewById(R.id.imageButton8));
        uiContainer.set(2, 4, findViewById(R.id.imageButton9));
        uiContainer.set(0, 5, findViewById(R.id.imageButtonStar));
        uiContainer.set(1, 5, findViewById(R.id.imageButton0));
        uiContainer.set(2, 5, findViewById(R.id.imageButtonPound));
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
