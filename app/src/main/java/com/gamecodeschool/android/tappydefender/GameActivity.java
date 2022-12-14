package com.gamecodeschool.android.tappydefender;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;

public class GameActivity extends Activity {

    private TDView gameView;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        gameView = new TDView(this, size.x, size.y);
        setContentView(gameView);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.throwbackgalaxy);
        mediaPlayer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return false;
    }
}