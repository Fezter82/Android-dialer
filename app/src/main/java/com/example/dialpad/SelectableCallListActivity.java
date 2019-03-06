package com.example.dialpad;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class SelectableCallListActivity extends ListActivity {

    BaseContainer<View> uiContainer;
    UINavigator navigator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectable_call_list);

        // Get all stored numbers
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String[] storedNumbers = sharedPreferences.getStringSet(SettingsActivity.STORED_NUMBERS, null).toArray(new String[0]);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.activity_selectable_call_list_row, R.id.listRowTextView, storedNumbers);

        this.setListAdapter(adapter);

        uiContainer = new ListViewContainer<>(this.getListView());
        navigator = new UINavigator(uiContainer, getApplicationContext());
    }

    @Override
    protected void onListItemClick(ListView listView, View v, int position, long id) {
        ListAdapter listAdapter = listView.getAdapter();
        Object object = listAdapter.getItem(position);
        String number = (String)object;

        // send intent with number to main activity
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("NUMBER", number);
        startActivity(intent);
    }
}
