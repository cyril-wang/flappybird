package com.gamecodeschool.android.tappydefender;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.util.Random;

public class EnemyShip {
    private Bitmap bitmap;
    private int x, y;
    private int speed = 1;

    private int maxX;
    private int minX;

    private int maxY;
    private int minY;

    private Rect hitBox;

    public EnemyShip(Context context, int screenX, int screenY) {
        Random generator = new Random();
        int whichBitmap = generator.nextInt(3);
        switch (whichBitmap) {
            case 0:
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.redkoopa), 100, 100, true);
                break;
            case 1:
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.bulletbill), 120, 70, true);
                break;
            case 2:
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.boo), 100, 90, true);                break;
        }

        scaleBitmap(screenX);
        maxX = screenX;
        maxY = screenY;
        minX = 0;
        minY = 0;

        speed = generator.nextInt(10)+20;

        x = screenX;
        y = generator.nextInt(maxY) - bitmap.getHeight();

        hitBox = new Rect(x,y, bitmap.getWidth(), bitmap.getHeight());
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void update() {
        // if you want to speed up
        // x -= playerSpeed
        x -= speed;

        if(x < minX-bitmap.getWidth()){
            Random generator = new Random();
            speed = generator.nextInt(10)+20;
            x = maxX;
            y = generator.nextInt(maxY) - bitmap.getHeight();
        }

        hitBox.left = x;
        hitBox.top = y;
        hitBox.right = x + bitmap.getWidth();
        hitBox.bottom = y + bitmap.getHeight();
    }

    public Rect getHitbox() {
        return hitBox;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void scaleBitmap(int x){

        if(x < 1000) {
            bitmap = Bitmap.createScaledBitmap(bitmap,
                    bitmap.getWidth() / 3,
                    bitmap.getHeight() / 3,
                    false);
        }else if(x < 1200){
            bitmap = Bitmap.createScaledBitmap(bitmap,
                    bitmap.getWidth() / 2,
                    bitmap.getHeight() / 2,
                    false);
        }
    }
}
