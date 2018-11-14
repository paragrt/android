package com.lfg.mobileang;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

/**
 * Created by pterede on 10/11/2016.
 */

public class MovingDisk {

    private Context ctx;
    private Bitmap bmp;
    private Bitmap org;
    public int diskId;
    private Bitmap pocketedIcon;
    public String name;
    public int radius;
    private int diameter ;
    public int mass = 5;
    public int  x;
    public int  y;
    //public float velocityX;
    //public float velocityY;
    public float deltaX = 0f;
    public float deltaY = 0f;

    private boolean play = true;

    boolean isMoving = false;
    public float threshold = 2f;
    public MovingDisk previousCollisionWith;
    private Bitmap original;

    public boolean inPlay() { return play;}
    public void setPlay(boolean flag) { this.play = flag;this.bmp = createScaledCircularBMP( R.drawable.pocketed, diameter);}
    public void showOriginal() { this.bmp = this.original;}

    public MovingDisk(Context aCtx, String name, int diskID, int aRadius, int ax, int ay) {
        this.ctx = aCtx;
        this.name = name;
        this.radius = aRadius;
        this.diameter = 2*this.radius;
        this.bmp = createScaledCircularBMP(diskID, diameter);
        this.original = this.bmp;
        this.x = ax;
        this.y = ay;
        this.diskId = diskID;
    }

    private Bitmap createScaledCircularBMP(int diskID, int diam) {
        Bitmap tmp = BitmapFactory.decodeResource(ctx.getResources(), diskID);
        tmp = getCroppedBitmap(tmp);
        tmp = Bitmap.createScaledBitmap(tmp, diam, diam, true);
        return tmp;
    }
    private Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }
    public void drawMeOn(Canvas c) {
        c.drawBitmap(bmp, x, y, null);
    }
}
