package com.example.dialpad;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import java.util.Set;

public class CallListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_list);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
//        getActionBar().setDisplayHomeAsUpEnabled(true);


        // Activate scrolling in the textView
        TextView view = findViewById(R.id.numbers);
        view.setMovementMethod(new ScrollingMovementMethod());

        // Get all stored numbers
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> storedNumbers = sharedPreferences.getStringSet(SettingsActivity.STORED_NUMBERS, null);

        StringBuilder numbersStr = new StringBuilder();

        // Display all numbers
        if (storedNumbers != null && storedNumbers.size() > 0) {

            for (String s : storedNumbers) {
                numbersStr.append(s).append("\n");
            }
        }

        view.setText(numbersStr.toString());



    }
}
