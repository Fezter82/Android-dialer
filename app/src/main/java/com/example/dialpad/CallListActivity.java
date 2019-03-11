package com.example.dialpad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
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

public class CallListActivity extends AppCompatActivity {

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

            navigator = new UINavigator(uiContainer, getApplicationContext());
        }
    }
}
