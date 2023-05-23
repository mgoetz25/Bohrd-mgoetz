package com.example.bohrd;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.Objects;

public class OptionsActivity extends AppCompatActivity {

    MediaPlayer menuMusic; // plays menu music loop

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        // toolbar examples taken from geeksforgeeks toolbar in Android page
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setLogo(R.mipmap.ic_fsu_seal);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        SharedPreferences prefs = this.getSharedPreferences("prefsKey", Context.MODE_PRIVATE);

        // music bar listener, update the music volume key
        SeekBar musicBar = findViewById(R.id.options_music_bar);
        musicBar.setProgress(prefs.getInt("musicKey", 50));
        musicBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                // Log.d("music val:",String.valueOf(i));
                int val = musicBar.getProgress();
                float log1 = (float) (1-(Math.log(100-val)/Math.log(100)));
                menuMusic.setVolume(log1,log1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SharedPreferences.Editor editor = prefs.edit();
                int val = musicBar.getProgress();
                editor.putInt("musicKey", val);
                editor.apply();
            }
        });

        // sfx bar listener, update the sfx volume key
        // play a sound on touch stopped to play sample volume to user
        SeekBar sfxBar = findViewById(R.id.options_sfx_bar);
        sfxBar.setProgress(prefs.getInt("sfxKey", 50));
        MediaPlayer sfxSample = MediaPlayer.create(this,R.raw.center);
        sfxBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                // Log.d("sfx val:",String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int val = sfxBar.getProgress();
                float log1 = (float) (1-(Math.log(100-val)/Math.log(100)));
                sfxSample.setVolume(log1,log1);
                sfxSample.start();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("sfxKey", val);
                editor.apply();
            }
        });

        // spinner for changing the game button theme
        Spinner themeSpinner = findViewById(R.id.options_themes);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.options_themes_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        themeSpinner.setAdapter(adapter);
        // spinner listener, change game theme
        themeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SharedPreferences.Editor editor = prefs.edit();
                String themeChosen = (String) themeSpinner.getItemAtPosition(i);
                editor.putString("themeKey", themeChosen);
                editor.putInt("themePosKey", themeSpinner.getSelectedItemPosition());
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        // default spinner position
        int pos = prefs.getInt("themePosKey", 0);
        themeSpinner.setSelection(pos);

        // reset options listener, set to defaults
        Button resetOptionsButton = findViewById(R.id.options_reset_button);
        resetOptionsButton.setOnClickListener(view -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
            musicBar.setProgress(prefs.getInt("musicKey", 50));
            sfxBar.setProgress(prefs.getInt("sfxKey", 50));
            themeSpinner.setSelection(0);
        });

        // reset records listener, prompt user if they are sure they want to reset
        Button resetRecordsButton = findViewById(R.id.options_records_button);
        resetRecordsButton.setOnClickListener(view -> {
            clearMenuMusic();
            Intent confirmIntent = new Intent(OptionsActivity.this, ConfirmActivity.class);
            OptionsActivity.this.startActivity(confirmIntent);
            finish();
        });

        // back button listener, go to title screen
        Button backButton = findViewById(R.id.options_back_button);
        backButton.setOnClickListener(view -> {
            clearMenuMusic();
            Intent optionsIntent = new Intent(OptionsActivity.this, MainActivity.class);
            OptionsActivity.this.startActivity(optionsIntent);
            finish();
        });

        setMenuMusic();

    }

    // function to start/continue menu music loop
    protected void setMenuMusic() {

        menuMusic = MediaPlayer.create(this, R.raw.menu_music);
        SharedPreferences prefs = this.getSharedPreferences("prefsKey", Context.MODE_PRIVATE);
        int val = prefs.getInt("musicKey",50);
        float log1=(float) (1-(Math.log(100-val)/Math.log(100)));
        int dur = prefs.getInt("durKey",0);
        menuMusic.setVolume(log1,log1);
        menuMusic.seekTo(dur);
        menuMusic.setLooping(true);
        menuMusic.start();

    }

    // function to clear current menu music mediaplayer
    protected void clearMenuMusic() {

        if (menuMusic != null) {
            SharedPreferences prefs = this.getSharedPreferences("prefsKey", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("durKey", menuMusic.getCurrentPosition());
            editor.apply();
            menuMusic.stop();
            menuMusic.reset();
            menuMusic.release();
        }

    }

}
