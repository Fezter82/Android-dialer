package com.example.dialpad;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import static android.content.Context.AUDIO_SERVICE;

public class DialPadView extends TableLayout{

    private final ArrayList<View> buttons; // all dialer buttons 0-9, #, *
    private final SoundPool soundPool;    // plays sounds
    private int sound0, sound1, sound2, sound3, sound4, sound5, sound6, sound7, sound8, sound9, soundStar, soundPound; // id:s of all sounds
    private boolean soundPoolLoaded = false; // true when soundPool is loaded
    private boolean soundsLoaded = false; // true when sounds are loaded

    // constructors
    public DialPadView(Context context) {
        this(context, null);
    }

    public DialPadView(final Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            inflater.inflate(R.layout.dialpadview, this, true);
        }

        // initialize buttons container
        buttons = new ArrayList<>();
        buttons.add(findViewById(R.id.imageButton1));
        buttons.add(findViewById(R.id.imageButton2));
        buttons.add(findViewById(R.id.imageButton3));
        buttons.add(findViewById(R.id.imageButton4));
        buttons.add(findViewById(R.id.imageButton5));
        buttons.add(findViewById(R.id.imageButton6));
        buttons.add(findViewById(R.id.imageButton7));
        buttons.add(findViewById(R.id.imageButton8));
        buttons.add(findViewById(R.id.imageButton9));
        buttons.add(findViewById(R.id.imageButton0));
        buttons.add(findViewById(R.id.imageButtonStar));
        buttons.add(findViewById(R.id.imageButtonPound));

        // add onclick listener for dialer buttons.
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetBackgroundColor();      // restore background to default
                    playSound((ImageButton) v); // play sound associated with the button
                    addInput((ImageButton) v);  // add the value of the button to the input field
                }
            });
        }

        // Add onlongclick listener for delete button
        View deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // delete all user inputted numbers/characters in the dialer
                TextView input = findViewById(R.id.input);
                input.setText("");

                return false;
            }
        });

        // Add single click listener for delete button
        deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // get input field
                TextView inputField = findViewById(R.id.input);

                // Get current value of input field
                String input = (String) inputField.getText();

                // Delete the last character of input
                if (input.length() > 0) {
                    input = input.substring(0, input.length() - 1);

                    // Set text of input field to the new value
                    inputField.setText(input);
                }
            }
        });

        // Add onclick listener for phone button
        View phoneButton = findViewById(R.id.phoneButton);
        phoneButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialHandler();
            }
        });

        // Makes view focusable, allows physical keyboard to be used
        setFocusable(true);

        // initialize soundPool
        soundPool = new SoundPool(12, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPoolLoaded = true;
            }
        });

        // add default numbers to call history
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> storedNumbers = sharedPreferences.getStringSet(SettingsActivity.STORED_NUMBERS, new HashSet<String>());

        for (int i = 0; i < 100; i++) {
            String number = String.valueOf(new Random().nextInt(888888) + 111111);
            if (storedNumbers != null) {
                storedNumbers.add(number);
            }
        }

        editor.putStringSet(SettingsActivity.STORED_NUMBERS, storedNumbers);
        editor.apply();

    }

    // Check if external storage is available to read
    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {

            // Check permissions
            return ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        }
        return false;
    }

    private void dialHandler() {
        // get context
        Context context = getContext();

        // get input field
        TextView inputField = findViewById(R.id.input);

        // Get current value of input field
        String input = (String) inputField.getText();

        // Check settings if numbers should be saved
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean saveNumber = sharedPreferences.getBoolean(SettingsActivity.SAVE_NUMBERS_SWITCH, true);

        // if numbers should be saved, save number
        if (saveNumber) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Set<String> storedNumbers = sharedPreferences.getStringSet(SettingsActivity.STORED_NUMBERS, new HashSet<String>());

            if (storedNumbers != null) {
                storedNumbers.add(input);
            }
            editor.putStringSet(SettingsActivity.STORED_NUMBERS, storedNumbers);
            editor.apply();
        }

        // encode input
        String inputEncoded = Uri.encode(input);

        // add prefix
        inputEncoded = "tel:" + inputEncoded;

        // Send number to the phones dialer
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(inputEncoded));

        // Verify that the intent will resolve to an activity
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            // Send intent
            context.startActivity(intent);
        } else {
            // if not activity could resolve the intent, make a toast!
            Toast toast = Toast.makeText(context, R.string.toast_noDialer, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    // listen for physical keyboard keys being pressed. Call keyPressed function for the correct key.
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch(keyCode) {
            case KeyEvent.KEYCODE_1: keyPressed((ImageButton) findViewById(R.id.imageButton1));
                return true;
            case KeyEvent.KEYCODE_2: keyPressed((ImageButton) findViewById(R.id.imageButton2));
                return true;
            case KeyEvent.KEYCODE_3: keyPressed((ImageButton) findViewById(R.id.imageButton3));
                return true;
            case KeyEvent.KEYCODE_4: keyPressed((ImageButton) findViewById(R.id.imageButton4));
                return true;
            case KeyEvent.KEYCODE_5: keyPressed((ImageButton) findViewById(R.id.imageButton5));
                return true;
            case KeyEvent.KEYCODE_6: keyPressed((ImageButton) findViewById(R.id.imageButton6));
                return true;
            case KeyEvent.KEYCODE_7: keyPressed((ImageButton) findViewById(R.id.imageButton7));
                return true;
            case KeyEvent.KEYCODE_8: keyPressed((ImageButton) findViewById(R.id.imageButton8));
                return true;
            case KeyEvent.KEYCODE_9: keyPressed((ImageButton) findViewById(R.id.imageButton9));
                return true;
            case KeyEvent.KEYCODE_0: keyPressed((ImageButton) findViewById(R.id.imageButton0));
                return true;
            case KeyEvent.KEYCODE_STAR: keyPressed((ImageButton) findViewById(R.id.imageButtonStar));
                return true;
            case KeyEvent.KEYCODE_POUND: keyPressed((ImageButton) findViewById(R.id.imageButtonPound));
                return true;
                default: return false;
        }
    }

    // Used when physical keyboard keys are being pressed. Resets key background. Sets background for the latest pressed key. Plays sound.
    private void keyPressed(ImageButton button) {
        resetBackgroundColor();
        button.setBackgroundColor(Color.parseColor("#ffb7f9"));
        playSound(button);
        addInput(button);
    }


    private void addInput(ImageButton button) {
        // get input field
        TextView inputField = findViewById(R.id.input);

        // Get current value of input field
        String input = (String) inputField.getText();

        // Add the value of the pressed button to the string
        if (button == findViewById(R.id.imageButton1))
            input = input + "1";
        if (button == findViewById(R.id.imageButton2))
            input = input + "2";
        if (button == findViewById(R.id.imageButton3))
            input = input + "3";
        if (button == findViewById(R.id.imageButton4))
            input = input + "4";
        if (button == findViewById(R.id.imageButton5))
            input = input + "5";
        if (button == findViewById(R.id.imageButton6))
            input = input + "6";
        if (button == findViewById(R.id.imageButton7))
            input = input + "7";
        if (button == findViewById(R.id.imageButton8))
            input = input + "8";
        if (button == findViewById(R.id.imageButton9))
            input = input + "9";
        if (button == findViewById(R.id.imageButton0))
            input = input + "0";
        if (button == findViewById(R.id.imageButtonStar))
            input = input + "*";
        if (button == findViewById(R.id.imageButtonPound))
            input = input + "#";

        // Set the value of the inputField to input
        inputField.setText(input);
    }

    // Resets key background color
    private void resetBackgroundColor() {
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setBackgroundColor(Color.TRANSPARENT);
        }
    }

    // Plays sound for the given key
    private void playSound(ImageButton button) {

        // load sound ids
        if (!soundsLoaded && isExternalStorageReadable()) {
            String soundsPath = Environment.getExternalStorageDirectory().getPath() + "/dialpad/sounds/mamacita_us";
            sound0 = soundPool.load(soundsPath + "/zero.mp3", 1);
            sound1 = soundPool.load(soundsPath + "/one.mp3", 1);
            sound2 = soundPool.load(soundsPath + "/two.mp3", 1);
            sound3 = soundPool.load(soundsPath + "/three.mp3", 1);
            sound4 = soundPool.load(soundsPath + "/four.mp3", 1);
            sound5 = soundPool.load(soundsPath + "/five.mp3", 1);
            sound6 = soundPool.load(soundsPath + "/six.mp3", 1);
            sound7 = soundPool.load(soundsPath + "/seven.mp3", 1);
            sound8 = soundPool.load(soundsPath + "/eight.mp3", 1);
            sound9 = soundPool.load(soundsPath + "/nine.mp3", 1);
            soundStar = soundPool.load(soundsPath + "/star.mp3", 1);
            soundPound = soundPool.load(soundsPath + "/pound.mp3", 1);

            soundsLoaded = true;
        }

        if (soundsLoaded && soundPoolLoaded) {
            AudioManager audioManager = (AudioManager) this.getContext().getSystemService(AUDIO_SERVICE);
            float actualVolume = 0;
            if (audioManager != null) {
                actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
            }
            float maxVolume = 0;
            if (audioManager != null) {
                maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
            }
            float volume = actualVolume / maxVolume;

            if (button == findViewById(R.id.imageButton1))
                soundPool.play(sound1, volume, volume, 1, 0, 1f);
            if (button == findViewById(R.id.imageButton2))
                soundPool.play(sound2, volume, volume, 1, 0, 1f);
            if (button == findViewById(R.id.imageButton3))
                soundPool.play(sound3, volume, volume, 1, 0, 1f);
            if (button == findViewById(R.id.imageButton4))
                soundPool.play(sound4, volume, volume, 1, 0, 1f);
            if (button == findViewById(R.id.imageButton5))
                soundPool.play(sound5, volume, volume, 1, 0, 1f);
            if (button == findViewById(R.id.imageButton6))
                soundPool.play(sound6, volume, volume, 1, 0, 1f);
            if (button == findViewById(R.id.imageButton7))
                soundPool.play(sound7, volume, volume, 1, 0, 1f);
            if (button == findViewById(R.id.imageButton8))
                soundPool.play(sound8, volume, volume, 1, 0, 1f);
            if (button == findViewById(R.id.imageButton9))
                soundPool.play(sound9, volume, volume, 1, 0, 1f);
            if (button == findViewById(R.id.imageButton0))
                soundPool.play(sound0, volume, volume, 1, 0, 1f);
            if (button == findViewById(R.id.imageButtonStar))
                soundPool.play(soundStar, volume, volume, 1, 0, 1f);
            if (button == findViewById(R.id.imageButtonPound))
                soundPool.play(soundPound, volume, volume, 1, 0, 1f);
        }
    }
}


