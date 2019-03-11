package com.example.dialpad;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class CallListActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sm;
    private Sensor accelerometer;
    private TextView x, y, z;
    private Toast directionToast;
    BaseContainer<View> uiContainer;
    UINavigator navigator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_list);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
//        getActionBar().setDisplayHomeAsUpEnabled(true);


        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(CallListActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        // Get all stored numbers
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        ArrayList<String> storedNumbers = new ArrayList<>(sharedPreferences.getStringSet(SettingsActivity.STORED_NUMBERS, null));

        // Display all numbers
        LinearLayout list = findViewById(R.id.the_list);
        list.removeAllViews();
        if (storedNumbers != null && storedNumbers.size() > 0) {
            uiContainer = new UIContainer<>(1, storedNumbers.size());

            for (int i = 0; i < storedNumbers.size(); i++) {
                final TextView textView = new TextView(this);
                textView.setText(storedNumbers.get(i));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
                textView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // send intent with number to main activity
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("NUMBER", textView.getText().toString());
                        startActivity(intent);
                    }
                });
                textView.setClickable(true);
                list.addView(textView);
                uiContainer.set(new Position(i), textView);
            }


        } else {
            uiContainer = new UIContainer<>(0, 1);
        }
        navigator = new UINavigator(uiContainer, getApplicationContext());
    }

    private void init() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sm.registerListener(CallListActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
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

            View clickedView = navigator.click();

            if(clickedView != null)
                if(clickedView.isClickable()){
                    clickedView.performClick();
                }

            try{
                Thread.sleep(300);
            } catch(InterruptedException e){
                e.printStackTrace();
            }

            sm.registerListener(CallListActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
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

            sm.registerListener(CallListActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

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

            sm.registerListener(CallListActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

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

            sm.registerListener(CallListActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

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

            sm.registerListener(CallListActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
