package com.gamecodeschool.android.tappydefender;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.content.Context;

import androidx.constraintlayout.widget.ConstraintSet;

import java.io.IOException;
import java.util.ArrayList;

public class TDView extends SurfaceView implements Runnable{

    volatile boolean playing;
    Thread gameThread = null;
    private PlayerShip player;
    public EnemyShip enemy1;
    public EnemyShip enemy2;
    public EnemyShip enemy3;
    public EnemyShip enemy4;
    public EnemyShip enemy5;
    public EnemyShip enemy6;
    public EnemyShip enemy7;
    public ArrayList<SpaceDust> dustList = new ArrayList<SpaceDust>();

    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder ourHolder;

    private float distanceTotal;
    private long timeTaken;
    private long timeStarted;
    private long highScore;
    private int screenX;
    private int screenY;
    private Context context;

    private boolean gameEnded;
    private SoundPool soundPool;
    int start = -1;
    int bump = -1;
    int destroyed = -1;
    int music = -1;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;



    public TDView(Context context, int x, int y) {
        super(context);
        this.context = context;
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            descriptor = assetManager.openFd("herewego.wav");
            start = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("mammamia.wav");
            bump = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("falling.wav");
            destroyed = soundPool.load(descriptor, 0);

        } catch (IOException e) {
            Log.e("error", "failed to load sound files");
        }

        prefs = context.getSharedPreferences("HighScores", context.MODE_PRIVATE);
        editor = prefs.edit();
        highScore = prefs.getLong("highScore", 0);


        screenX = x;
        screenY = y;
//
        ourHolder = getHolder();
        paint = new Paint();
        startGame();
//        player = new PlayerShip(context, x, y);
//        enemy1 = new EnemyShip(context, x, y);
//        enemy2 = new EnemyShip(context, x, y);
//        enemy3 = new EnemyShip(context, x, y);
//
//
//        int numSpecs = 40;
//        for (int i = 0; i < numSpecs; i++) {
//            SpaceDust spec = new SpaceDust(x, y);
//            dustList.add(spec);
//        }
    }

    private void startGame(){
        //Initialize game objects
        player = new PlayerShip(context, screenX, screenY);
        enemy1 = new EnemyShip(context, screenX, screenY);
        enemy2 = new EnemyShip(context, screenX, screenY);
        enemy3 = new EnemyShip(context, screenX, screenY);
        enemy4 = new EnemyShip(context, screenX, screenY);
        enemy5 = new EnemyShip(context, screenX, screenY);
        enemy6 = new EnemyShip(context, screenX, screenY);
        enemy7 = new EnemyShip(context, screenX, screenY);

        int numSpecs = 40;
        for (int i = 0; i < numSpecs; i++) {
            SpaceDust spec = new SpaceDust(screenX, screenY);
            dustList.add(spec);
        }
        distanceTotal = 0;// 10 km
        timeTaken = 0;
        timeStarted = System.currentTimeMillis();
        gameEnded = false;
        soundPool.play(start, 6, 6, 1, 0, 1);
    }

    @Override
    public void run() {
        while (playing) {
            update();
            draw();
            control();
        }
    }

    private void update() {

        boolean hitDetected = false;

        if(Rect.intersects(player.getHitbox(), enemy1.getHitbox())){
            hitDetected = true;
            enemy1.setX(-100);
        }
        if(Rect.intersects(player.getHitbox(), enemy2.getHitbox())){
            hitDetected = true;
            enemy2.setX(-100);
        }
        if(Rect.intersects(player.getHitbox(), enemy3.getHitbox())){
            hitDetected = true;
            enemy3.setX(-100);
        }
        if(Rect.intersects(player.getHitbox(), enemy4.getHitbox())){
            hitDetected = true;
            enemy4.setX(-100);
        }
        if(Rect.intersects(player.getHitbox(), enemy5.getHitbox())){
            hitDetected = true;
            enemy5.setX(-100);
        }
        if(Rect.intersects(player.getHitbox(), enemy6.getHitbox())){
            hitDetected = true;
            enemy6.setX(-100);
        }
        if(Rect.intersects(player.getHitbox(), enemy7.getHitbox())){
            hitDetected = true;
            enemy7.setX(-100);
        }

        if (hitDetected) {
            soundPool.play(bump, 6, 6, 0, 0, 1);
            player.reduceShieldStrength();
            if (player.getShieldStrength() == 0) {
                soundPool.play(destroyed, 6, 6, 0, 0, 1);

                if (distanceTotal > highScore) {
                    editor.putLong("highScore", (long) distanceTotal);
                    editor.commit();
                    highScore = (long) distanceTotal;
                }
                gameEnded = true;
            }
        }

        player.update();

        enemy1.update();
        enemy2.update();
        enemy3.update();
        enemy4.update();
        enemy5.update();
        enemy6.update();
        enemy7.update();

        for (SpaceDust sd : dustList) {
            sd.update();
        }

        if(!gameEnded) {
            //subtract distance to home planet based on current speed
            distanceTotal += player.getSpeed();

            //How long has the player been flying
            timeTaken = System.currentTimeMillis() - timeStarted;
        }

    }

    private void draw() {
        if (ourHolder.getSurface().isValid()) {
            canvas = ourHolder.lockCanvas();
            canvas.drawColor(Color.argb(255,0,0,0));
            paint.setColor(Color.argb(255,255,255,255));
            for (SpaceDust sd : dustList) {
                canvas.drawPoint(sd.getX(), sd.getY(), paint);
            }

            canvas.drawBitmap(
                    player.getBitmap(),
                    player.getX(),
                    player.getY(),
                    paint);

            canvas.drawBitmap(enemy1.getBitmap(), enemy1.getX(), enemy1.getY(), paint);
            canvas.drawBitmap(enemy2.getBitmap(), enemy2.getX(), enemy2.getY(), paint);
            canvas.drawBitmap(enemy3.getBitmap(), enemy3.getX(), enemy3.getY(), paint);
            canvas.drawBitmap(enemy4.getBitmap(), enemy4.getX(), enemy4.getY(), paint);
            canvas.drawBitmap(enemy5.getBitmap(), enemy5.getX(), enemy5.getY(), paint);
            canvas.drawBitmap(enemy6.getBitmap(), enemy6.getX(), enemy6.getY(), paint);
            canvas.drawBitmap(enemy7.getBitmap(), enemy7.getX(), enemy7.getY(), paint);

            if (!gameEnded) {
                paint.setTextAlign(Paint.Align.LEFT);
                paint.setColor(Color.argb(255, 255, 255, 255));
                paint.setTextSize(25);
                canvas.drawText("Time: " + formatTime(timeTaken) + "s", screenX / 2, 20, paint);
                canvas.drawText("Distance: " + distanceTotal / 1000 +
                        " KM", screenX / 3, 20, paint);
                canvas.drawText("Lives: " +
                        player.getShieldStrength(), 10, screenY - 20, paint);
            } else {
                paint.setTextSize(80);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("Game Over", screenX/2, 250, paint);
                paint.setTextSize(25);
                canvas.drawText("Time:" + formatTime(timeTaken) + "s", screenX / 2, 200, paint);

                canvas.drawText("Distance traveled: " +
                        distanceTotal/1000 + " KM",screenX/2, 400, paint);

                paint.setTextSize(80);
                canvas.drawText("Tap to replay!", screenX/2, 550, paint);
            }

            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                player.stopBoosting();
                break;

            case MotionEvent.ACTION_DOWN:
                player.setBoosting();
                if(gameEnded){
                    startGame();
                }
                break;
        }
        return true;
    }

    private void control() {
        try {
            gameThread.sleep(17);
        } catch (InterruptedException e) {

        }
    }

    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {

        }
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    private String formatTime(long time){
        long seconds = (time) / 1000;
        long thousandths = (time) - (seconds * 1000);
        String strThousandths = "" + thousandths;
        if (thousandths < 100){strThousandths = "0" + thousandths;}
        if (thousandths < 10){strThousandths = "0" + strThousandths;}
        String stringTime = "" + seconds + "." + strThousandths;
        return stringTime;
    }
}
