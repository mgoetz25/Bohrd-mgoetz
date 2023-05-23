package com.example.bohrd;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.Objects;

public class RecordsActivity extends AppCompatActivity {

    MediaPlayer menuMusic; // plays menu music loop

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        // toolbar examples taken from geeksforgeeks toolbar in Android page
        // set header on top to use FSU seal
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setLogo(R.mipmap.ic_fsu_seal);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // back button listener, swap to title screen
        Button backButton = findViewById(R.id.records_back_button);
        backButton.setOnClickListener(view -> {
            clearMenuMusic();
            Intent recordIntent = new Intent(RecordsActivity.this, MainActivity.class);
            RecordsActivity.this.startActivity(recordIntent);
            finish();
        });

        // update records from key stores
        updateRecords();
        setMenuMusic();

    }

    // find records in key stores and display them
    protected void updateRecords() {

        SharedPreferences prefs = this.getSharedPreferences("recsKey", Context.MODE_PRIVATE);

        TextView firstRecord = findViewById(R.id.records_1st);
        TextView secondRecord = findViewById(R.id.records_2nd);
        TextView thirdRecord = findViewById(R.id.records_3rd);
        TextView fourthRecord = findViewById(R.id.records_4th);
        TextView fifthRecord = findViewById(R.id.records_5th);

        String firstStr = "1st - " + prefs.getString("firstNameKey", "Andy") + " - " + prefs.getInt("firstScoreKey", 24);
        String secondStr = "2nd - " + prefs.getString("secondNameKey", "Myrtle") + " - " + prefs.getInt("secondScoreKey", 20);
        String thirdStr = "3rd - " + prefs.getString("thirdNameKey", "Robert") + " - " + prefs.getInt("thirdScoreKey", 15);
        String fourthStr = "4th - " + prefs.getString("fourthNameKey", "Rosie") + " - " + prefs.getInt("fourthScoreKey", 9);
        String fifthStr = "5th - " + prefs.getString("fifthNameKey", "Felix") + " - " + prefs.getInt("fifthScoreKey", 4);

        firstRecord.setText(firstStr);
        secondRecord.setText(secondStr);
        thirdRecord.setText(thirdStr);
        fourthRecord.setText(fourthStr);
        fifthRecord.setText(fifthStr);

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
