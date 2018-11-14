package com.example.paragrt.modernartui2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

/**
 * Created by paragrt on 2/6/2015.
 * Draws a random number of tiles between (2x2=4) and (5x7=35)
 * plus 1 white tile at a random postion and size
 * Client can call updateDrawing to redraw
 */


public class DrawView extends View {
    final Paint borderPaint = new Paint();

    Random rnd = new Random();
    int MAX_COLS = 2;
    int MAX_ROWS = 3;

    int[] carr = new int[]{Color.WHITE,Color.GRAY};

    public DrawView(Context context) {
        super(context);
        setupBorder();

    }

    private void setupBorder() {
        borderPaint.setColor(Color.BLACK);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(15f);
    }

    //http://developer.android.com/guide/topics/graphics/2d-graphics.html
    //need this to be able to include it in resource xml
    public DrawView(Context context, AttributeSet set) {
        super(context, set);
        setupBorder();
    }

    @Override
    public void onDraw(Canvas canvas) {



        Paint paint = new Paint();
        int colWidth = canvas.getWidth()/MAX_COLS;
        int rowHeight = canvas.getHeight()/MAX_ROWS;
        for(int row = 0; row < MAX_ROWS; row++)
        {
            for (int col = 0; col < MAX_COLS; col++)
            {
                int left = col * colWidth;
                int top = row * rowHeight;
                int right = left + colWidth;
                int bottom = top + rowHeight;
                //http://stackoverflow.com/questions/5280367/android-generate-random-color-on-click
                int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                paint.setColor(color);
                paint.setStyle(Paint.Style.FILL);//trokeWidth(3);
                //System.out.println("left=" + left + " top=" + top + " width=" + colWidth + " height=" + rowHeight);
                canvas.drawRect(left, top, right, bottom, paint);//filled rectangle in one color
                canvas.drawRect(left, top, right, bottom, borderPaint);//border in black a la Pete Mondrian
            }
        }
        drawGrayOrWhiteSquareAtRandomSpot(paint, canvas);
    }

    private void drawGrayOrWhiteSquareAtRandomSpot(Paint paint, Canvas canvas) {

        paint.setColor(carr[0]);
        paint.setStrokeWidth(3);
        //System.out.println("left=" + left + " top=" + top + " width=" + colWidth + " height=" + rowHeight);
        int left = rnd.nextInt(200);
        int top = rnd.nextInt(200);
        int right = 250+rnd.nextInt(600);
        int bottom = 250+rnd.nextInt(800);

        canvas.drawRect(left, top, right, bottom, paint);
        canvas.drawRect(left, top, right, bottom, borderPaint);
    }

    //called by client to redraw picture...just random number of tiles...
    // making sure never less that 2 rows and 2 cols and never more than 6 cols and 8 rows
    public void updateDrawing() {
        MAX_COLS = rnd.nextInt(4)+2;
        MAX_ROWS = rnd.nextInt(6)+2;
        invalidate();
    }
}
