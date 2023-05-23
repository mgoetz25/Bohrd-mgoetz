package com.example.bohrd;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Button;
import androidx.appcompat.widget.Toolbar;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    Button[] titleBtns; // grid for accessing each button in the menu
    MediaPlayer menuMusic; // plays menu music loop

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // toolbar examples taken from geeksforgeeks toolbar in Android page
        // set header on top to use FSU seal
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setLogo(R.mipmap.ic_fsu_seal);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // assign each button to the array
        titleBtns = new Button[] {
                findViewById(R.id.title_play_button),
                findViewById(R.id.title_options_button),
                findViewById(R.id.title_records_button),
                findViewById(R.id.title_quit_button)
        };

        setButtonSizes();
        setMenuMusic();

        // play button listener
        titleBtns[0].setOnClickListener(view -> {
            clearMenuMusic();
            // from Emmanuel on stackoverflow "How to start new activity on button click"
            Intent playIntent = new Intent(MainActivity.this, PlayActivity.class);
            MainActivity.this.startActivity(playIntent);
            finish();
        });

        // option button listener
        titleBtns[1].setOnClickListener(view -> {
            clearMenuMusic();
            Intent optionsIntent = new Intent(MainActivity.this, OptionsActivity.class);
            MainActivity.this.startActivity(optionsIntent);
            finish();
        });

        // records button listener
        titleBtns[2].setOnClickListener(view -> {
            clearMenuMusic();
            Intent recordIntent = new Intent(MainActivity.this, RecordsActivity.class);
            MainActivity.this.startActivity(recordIntent);
            finish();
        });

        // quit button listener
        titleBtns[3].setOnClickListener(view -> {
            clearMenuMusic();
            finish();
            System.exit(0);
        });

    }

    // function to set button sizes relative to screen width
    protected void setButtonSizes() {

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int margin = (int) (metrics.widthPixels / 25.0);
        int newSide = (int) (metrics.widthPixels / 3.0) - margin;

        for (Button titleBtn : titleBtns) {
            titleBtn.setHeight(newSide);
            titleBtn.setWidth(newSide);
        }

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