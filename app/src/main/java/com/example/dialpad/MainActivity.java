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
    /**
     * @TODO Use BaseContainer = new UIContainer instead of UIContainer
     */
    UIContainer<View> uiContainer;
    UINavigator navigator;

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
        uiContainer.set(new Position(0,0), null);
        uiContainer.set(new Position(1, 0), null);
        uiContainer.set(new Position(2, 0), findViewById(R.id.action_call_list));
        uiContainer.set(new Position(0, 1), null);
        uiContainer.set(new Position(1, 1), findViewById(R.id.deleteButton));
        uiContainer.set(new Position(2, 1), findViewById(R.id.phoneButton));
        uiContainer.set(new Position(0, 2), findViewById(R.id.imageButton1));
        uiContainer.set(new Position(1, 2), findViewById(R.id.imageButton2));
        uiContainer.set(new Position(2, 2), findViewById(R.id.imageButton3));
        uiContainer.set(new Position(0, 3), findViewById(R.id.imageButton4));
        uiContainer.set(new Position(1, 3), findViewById(R.id.imageButton5));
        uiContainer.set(new Position(2, 3), findViewById(R.id.imageButton6));
        uiContainer.set(new Position(0, 4), findViewById(R.id.imageButton7));
        uiContainer.set(new Position(1, 4), findViewById(R.id.imageButton8));
        uiContainer.set(new Position(2, 4), findViewById(R.id.imageButton9));
        uiContainer.set(new Position(0, 5), findViewById(R.id.imageButtonStar));
        uiContainer.set(new Position(1, 5), findViewById(R.id.imageButton0));
        uiContainer.set(new Position(2, 5), findViewById(R.id.imageButtonPound));

        // If this activity was started with an intent, get number from intent
        Intent intent = getIntent();
        String number = intent.getStringExtra("NUMBER");
        TextView textView = findViewById(R.id.input);
        textView.setText(number);

        navigator = new UINavigator(uiContainer, getApplicationContext());
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
                Intent callListIntent = new Intent(this, SelectableCallListActivity.class);
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

        //System.out.println("Motion detected X: " + event.values[1] + ", Y: " + event.values[2]);

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        //CLICK
        if(event.values[2] > 15){

            System.out.println("CLICK detected");

            if (directionToast != null) {
                directionToast.cancel();
            }

            sm.unregisterListener(this);

            try{
                Thread.sleep(300);
            } catch(InterruptedException e){
                e.printStackTrace();
            }

            sm.registerListener(MainActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        //UP
        else if(event.values[1] < 6 && event.values[2] > 10){

            System.out.println("UP detected");

            if (directionToast != null) {
                directionToast.cancel();
            }

            sm.unregisterListener(this);

            navigator.moveUp();

            try{
                Thread.sleep(300);
            } catch(InterruptedException e){
                e.printStackTrace();
            }

            sm.registerListener(MainActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        }
        //DOWN
        else if(event.values[1] > 8 && event.values[2] < 2){

            System.out.println("DOWN detected");

            if (directionToast != null) {
                directionToast.cancel();
            }

            sm.unregisterListener(this);

            navigator.moveDown();

            try{
                Thread.sleep(300);
            } catch(InterruptedException e){
                e.printStackTrace();
            }

            sm.registerListener(MainActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        }
        //LEFT
        else if(event.values[0] > 3){

            System.out.println("LEFT detected");

            if (directionToast != null) {
                directionToast.cancel();
            }

            sm.unregisterListener(this);

            navigator.moveLeft();

            try{
                Thread.sleep(300);
            } catch(InterruptedException e){
                e.printStackTrace();
            }

            sm.registerListener(MainActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        }
        //RIGHT
        else if(event.values[0] < -3){

            System.out.println("RIGHT detected");

            if (directionToast != null) {
                directionToast.cancel();
            }

            sm.unregisterListener(this);

            navigator.moveRight();

            try{
                Thread.sleep(300);
            } catch(InterruptedException e){
                e.printStackTrace();
            }

            sm.registerListener(MainActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        }


    }
}
