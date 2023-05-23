package com.example.bohrd;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

public class EndActivity extends AppCompatActivity {

    String [] keyNameNames = {"firstNameKey","secondNameKey","thirdNameKey","fourthNameKey","fifthNameKey"};
    String [] keyScoreNames = {"firstScoreKey","secondScoreKey","thirdScoreKey","fourthScoreKey","fifthScoreKey"};
    String [] places = {"1st","2nd","3rd","4th","5th"};
    EditText playername; // textbox for user name entry
    MediaPlayer menuMusic; // plays menu music loop

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        // toolbar examples taken from geeksforgeeks toolbar in Android page
        // set header on top to use FSU seal
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setLogo(R.mipmap.ic_fsu_seal);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // assign playername to textbox
        playername = findViewById(R.id.end_name);
        playername.setVisibility(View.INVISIBLE);
        // check if a record was set
        int index = checkRecords();
        // print end game message
        printResult(index);

        // set replay button listener
        // store user score/name if new hi score
        // require user to enter a non-empty string
        Button replayButton = findViewById(R.id.end_replay_button);
        replayButton.setOnClickListener(view -> {
            if (index == -1) {
                clearMenuMusic();
                Intent playIntent = new Intent(EndActivity.this, PlayActivity.class);
                EndActivity.this.startActivity(playIntent);
                finish();
            } else if (String.valueOf(playername.getText()).isEmpty()) {
                TextView endMsg = findViewById(R.id.end_message);
                String endStr = "GAME OVER\nPlease enter a valid, non-empty name." + "\n";
                endMsg.setText(endStr);
            } else {
                clearMenuMusic();
                saveScore(index);
                Intent playIntent = new Intent(EndActivity.this, PlayActivity.class);
                EndActivity.this.startActivity(playIntent);
                finish();
            }
        });

        // set title button listener
        // store user score/name if new hi score
        // require user to enter a non-empty string
        Button titleButton = findViewById(R.id.end_title_button);
        titleButton.setOnClickListener(view -> {
            // Log.d("player",String.valueOf(playername.getText()));
            if (index == -1) {
                clearMenuMusic();
                Intent titleIntent = new Intent(EndActivity.this, MainActivity.class);
                EndActivity.this.startActivity(titleIntent);
                finish();
            } else if (String.valueOf(playername.getText()).isEmpty()) {
                TextView endMsg = findViewById(R.id.end_message);
                String endStr = "GAME OVER\nPlease enter a valid, non-empty name." + "\n";
                endMsg.setText(endStr);
            } else {
                clearMenuMusic();
                saveScore(index);
                Intent titleIntent = new Intent(EndActivity.this, MainActivity.class);
                EndActivity.this.startActivity(titleIntent);
                finish();
            }
        });

        setMenuMusic();

    }

    // get records and check if user score replaces any of them
    protected int checkRecords() {

        SharedPreferences prefs = this.getSharedPreferences("recsKey", Context.MODE_PRIVATE);
        int score = prefs.getInt("scoreKey",0);

        int [] records = new int[] {
                prefs.getInt("firstScoreKey",24),
                prefs.getInt("secondScoreKey",20),
                prefs.getInt("thirdScoreKey",15),
                prefs.getInt("fourthScoreKey",9),
                prefs.getInt("fifthScoreKey",4)
        };
        String [] recordNames = new String[] {
                prefs.getString("firstNameKey","Andy"),
                prefs.getString("secondNameKey","Myrtle"),
                prefs.getString("thirdNameKey","Robert"),
                prefs.getString("fourthNameKey","Rosie"),
                prefs.getString("fifthNameKey","Felix")
        };
        for (int i = 0; i < records.length; ++i) {
            if (score > records[i]) {
                // Log.d("record beaten",records[i] + " - " + recordNames[i]);
                playername.setVisibility(View.VISIBLE);
                return i;
            }
        }
        return -1;

    }

    // print end screen message
    protected void printResult(int index) {

        SharedPreferences prefs = this.getSharedPreferences("recsKey", Context.MODE_PRIVATE);
        int score = prefs.getInt("scoreKey",0);
        TextView endMsg = findViewById(R.id.end_message);
        String endStr = "GAME OVER\nYour Score - " + score + "\n";
        if (index > -1) {
            endStr += "New HI SCORE for " + places[index] + " place!\n";
        }
        endMsg.setText(endStr);

    }

    // save user score upon leaving screen
    protected void saveScore(int index) {

        SharedPreferences prefs = this.getSharedPreferences("recsKey", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        int score = prefs.getInt("scoreKey",0);
        editor.putInt(keyScoreNames[index], score);
        editor.putString(keyNameNames[index], String.valueOf(playername.getText()));
        editor.apply();

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
