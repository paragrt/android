package com.example.pterede.mywidget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Pterede on 9/19/2016.
 */
public class MyTextView extends TextView {


    public MyTextView(Context context) {
        super(context);
        setBackgroundColor(Color.parseColor("#A00000"));

    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(Color.parseColor("#A00000"));

    }

    public MyTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setBackgroundColor(Color.parseColor("#A00000"));


    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);


    }


}
