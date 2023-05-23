package com.example.bohrd;

import static com.example.bohrd.R.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class PlayActivity extends AppCompatActivity {

    Button[] gridBtns; // contains the button references for the grid
    int[] gridColors; // contains the color int vals for each button in the grid
    MediaPlayer[] gridSfx; // contains players for each button
    MediaPlayer playMusic;  // plays in-game music loop
    List<Integer> gridMoves; // contains the index sequence of the button presses for the level
    CountDownTimer prepare; // countdown timer for grid sequence
    int level; // keep track of user's level
    int counter; // counter used for grid sequence
    int moves; // keep track of number of moves user has taken so far
    long cntDwn; // grid sequence tick duration in ms
    TextView levelLabel; // label for level number
    TextView moveLabel; // label for current move number
    TextView playRecord; // label for hi score

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(layout.activity_play);

        // toolbar examples taken from geeksforgeeks toolbar in Android page
        // set header on top to use FSU seal
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setLogo(R.mipmap.ic_fsu_seal);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // create array of buttons to run game
        gridBtns = new Button[] {
                findViewById(id.play_topleft),
                findViewById(id.play_top),
                findViewById(id.play_topright),
                findViewById(id.play_centerleft),
                findViewById(id.play_center),
                findViewById(id.play_centerright),
                findViewById(id.play_bottomleft),
                findViewById(id.play_bottom),
                findViewById(id.play_bottomright)
        };

        // set grid colors to user choice from options
        SharedPreferences prefs = this.getSharedPreferences("prefsKey", Context.MODE_PRIVATE);
        setGridColors(prefs.getString("themeKey", "Rainbow"));

        // populate sfx array with sound files
        gridSfx = new MediaPlayer[] {
                MediaPlayer.create(this, raw.topleft),
                MediaPlayer.create(this, raw.top),
                MediaPlayer.create(this, raw.topright),
                MediaPlayer.create(this, raw.centerleft),
                MediaPlayer.create(this, raw.center),
                MediaPlayer.create(this, raw.centerright),
                MediaPlayer.create(this, raw.bottomleft),
                MediaPlayer.create(this, raw.bottom),
                MediaPlayer.create(this, raw.bottomright)
        };

        // default values for running the game
        level = 1;
        counter = -1;
        moves = 0;
        cntDwn = 500;

        // return button listener, sends back to title screen
        Button returnButton = findViewById(id.play_return_button);
        returnButton.setOnClickListener(view -> {
            if (prepare != null) {
                prepare.cancel();
            }
            clearPlayMusic();
            Intent titleIntent = new Intent(PlayActivity.this, MainActivity.class);
            PlayActivity.this.startActivity(titleIntent);
            finish();
        });

        // set textviews to labels in layout and show hi score
        SharedPreferences recs = this.getSharedPreferences("recsKey", Context.MODE_PRIVATE);
        levelLabel = findViewById(id.play_level_count);
        moveLabel = findViewById(id.play_move_count);
        playRecord = findViewById(id.play_record);
        String recordStr = "HI SCORE: " + recs.getInt("firstScoreKey", 24);
        playRecord.setText(recordStr);

        // initialize grid move array
        gridMoves = new ArrayList<>();

        // set button sizes and listeners
        // start music loop
        setAllButtons();
        resetButtons();
        setPlayMusic();

        // start game
        playGame();

    }

    // set all buttons to size relative to screen width
    protected void setAllButtons() {

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int margin = (int) (metrics.widthPixels / 25.0);
        int newSide = (int) (metrics.widthPixels / 3.0) - margin;

        for (int i = 0; i < gridBtns.length; ++i) {
            setButton(i, newSide);
        }

    }

    // set individual button size and listeners
    // time decreases between button flashes each level by 10 ms
    // time gets faster from 500 ms to 200 ms (cap) at LVL 30
    // each button has a unique sound
    protected void setButton(int index, int side) {

        gridBtns[index].setHeight(side);
        gridBtns[index].setWidth(side);
        SharedPreferences prefs = this.getSharedPreferences("prefsKey", Context.MODE_PRIVATE);
        int val = prefs.getInt("sfxKey",50);
        float log1=(float) (1-(Math.log(100-val)/Math.log(100)));
        gridSfx[index].setVolume(log1, log1);
        gridBtns[index].setOnClickListener(view -> {
            counter = -1;
            gridSfx[index].start();
            if (gridBtns[index].getId() == gridBtns[gridMoves.get(moves)].getId()) {
                // Log.d("result","correct");
                moves += 1;
                if (cntDwn > 200) {
                    cntDwn -= 10;
                }
                String moveStr = "Move " + (moves + 1) + "/" + level;
                moveLabel.setText(moveStr);
                if (moves == level) {
                    // Log.d("Level beat", String.valueOf(level));
                    level += 1;
                    moves = 0;
                    playGame();
                }
            } else {
                // Log.d("result","incorrect");
                SharedPreferences recs = this.getSharedPreferences("recsKey", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = recs.edit();
                editor.putInt("scoreKey", level - 1);
                editor.apply();
                if (prepare != null) {
                    prepare.cancel();
                }
                clearPlayMusic();
                Intent endIntent = new Intent(PlayActivity.this, EndActivity.class);
                PlayActivity.this.startActivity(endIntent);
                finish();
            }
        });

    }

    // reset all buttons back to not enabled and gray between flashes
    protected void resetButtons() {

        for (Button gridBtn : gridBtns) {
            gridBtn.setEnabled(false);
            gridBtn.setBackgroundColor(Color.GRAY);
        }

    }

    // set all buttons to enabled and change colors back
    protected void activateButtons() {

        for (int i = 0; i < gridBtns.length; ++i) {
            gridBtns[i].setEnabled(true);
            gridBtns[i].setBackgroundColor(gridColors[i]);
        }

    }

    // set grid colors based on user's theme
    protected void setGridColors(String theme) {

        if (Objects.equals(theme, "Rainbow")) {
            gridColors = new int[] {
                    Color.RED,
                    Color.rgb(255,165,0),
                    Color.YELLOW,
                    Color.GREEN,
                    Color.CYAN,
                    Color.BLUE,
                    Color.rgb(75,0,130),
                    Color.rgb(155,38,182),
                    Color.MAGENTA
            };
        } else if (Objects.equals(theme, "Sunset")) {
            gridColors = new int[] {
                    Color.rgb(0,32,46),
                    Color.rgb(0,63,92),
                    Color.rgb(44,72,117),
                    Color.rgb(138,80,143),
                    Color.rgb(188,80,144),
                    Color.rgb(255,99,97),
                    Color.rgb(255,133,49),
                    Color.rgb(255,166,0),
                    Color.rgb(255,211,128)
            };
        } else if (Objects.equals(theme, "Pastel")) {
            gridColors = new int[] {
                    Color.rgb(184,216,253),
                    Color.rgb(192,192,253),
                    Color.rgb(237,208,253),
                    Color.rgb(255,198,248),
                    Color.rgb(255,179,214),
                    Color.rgb(255,207,188),
                    Color.rgb(255,226,198),
                    Color.rgb(255,245,198),
                    Color.rgb(241,254,188)
            };
        } else if (Objects.equals(theme, "Neon")) {
            gridColors = new int[] {
                    Color.rgb(39,178,67),
                    Color.rgb(77,238,234),
                    Color.rgb(116,238,21),
                    Color.rgb(255,231,0),
                    Color.rgb(248,116,128),
                    Color.rgb(240,0,255),
                    Color.rgb(120,15,255),
                    Color.rgb(0,30,255),
                    Color.rgb(128,143,255)
            };
        } else if (Objects.equals(theme, "Blackout")) {
            gridColors = new int[] {
                    Color.BLACK,
                    Color.BLACK,
                    Color.BLACK,
                    Color.BLACK,
                    Color.BLACK,
                    Color.BLACK,
                    Color.BLACK,
                    Color.BLACK,
                    Color.BLACK
            };
        }

    }

    // function to start/continue game music loop
    protected void setPlayMusic() {

        playMusic = MediaPlayer.create(this, raw.play_music);
        SharedPreferences prefs = this.getSharedPreferences("prefsKey", Context.MODE_PRIVATE);
        int val = prefs.getInt("musicKey",50);
        float log1=(float) (1-(Math.log(100-val)/Math.log(100)));
        playMusic.setVolume(log1,log1);
        playMusic.setLooping(true);
        playMusic.start();

    }

    // function to clear current game music mediaplayer
    protected void clearPlayMusic() {

        if (playMusic != null) {
            playMusic.stop();
            playMusic.reset();
            playMusic.release();
        }

        for (MediaPlayer grids : gridSfx) {
            grids.stop();
            grids.reset();
            grids.release();
        }

    }

    // play a level of the game and update labels and counters
    protected void playGame() {

        String levelStr = "LVL " + level;
        levelLabel.setText(levelStr);
        String moveStr = "Move 1/" + level;
        moveLabel.setText(moveStr);
        addLevelMove();
        // Log.d("moves", String.valueOf(gridMoves));
        gridSequence(gridMoves);

    }

    // play the grid sequence for the current level, flashing buttons in order
    boolean show;
    protected void gridSequence(List<Integer> gMoves) {

        show = false;
        counter = -1;
        long cntDur = level * cntDwn * 2;
        long cntDelay = cntDwn * 3;
        if (cntDwn == 500) {
            cntDur += 50;
        }
        prepare = new CountDownTimer(cntDur + cntDelay, cntDwn) {
            @Override
            public void onTick(long l) {
                // Log.d("tick:", String.valueOf(l));
                if (show) {
                    if (counter >= 0) {
                        int i = gMoves.get(counter);
                        gridSfx[i].start();
                        gridBtns[i].setBackgroundColor(gridColors[i]);
                    }
                    if (counter < gMoves.size()) {
                        counter += 1;

                    }
                    show = false;
                } else {
                    resetButtons();
                    show = true;
                }
            }

            @Override
            public void onFinish() {
                // Log.d("tick:", "finished");
                activateButtons();
            }
        };
        prepare.start();
    }

    // add level after user beats current level
    protected void addLevelMove() {

        Random random = new Random();
        gridMoves.add(random.nextInt(9));

    }

}
