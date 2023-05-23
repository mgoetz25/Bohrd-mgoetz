package com.example.bohrd;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

public class ConfirmActivity extends AppCompatActivity {

    MediaPlayer menuMusic; // plays menu music loop

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        // toolbar examples taken from geeksforgeeks toolbar in Android page
        // set header on top to use FSU seal
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setLogo(R.mipmap.ic_fsu_seal);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // yes button listener, clear records and reset to defaults
        Button confirmYesButton = findViewById(R.id.confirm_yes_button);
        confirmYesButton.setOnClickListener(view -> {
            clearMenuMusic();
            SharedPreferences prefs = this.getSharedPreferences("recsKey", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
            Intent optionsIntent = new Intent(ConfirmActivity.this, OptionsActivity.class);
            ConfirmActivity.this.startActivity(optionsIntent);
            finish();
        });

        // no button listener, go back to options screen with no changes
        Button confirmNoButton = findViewById(R.id.confirm_no_button);
        confirmNoButton.setOnClickListener(view -> {
            clearMenuMusic();
            Intent optionsIntent = new Intent(ConfirmActivity.this, OptionsActivity.class);
            ConfirmActivity.this.startActivity(optionsIntent);
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
