/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.support.v4.view.GestureDetectorCompat;
import android.view.MotionEvent;

/**
 * A view container where OpenGL ES graphics can be drawn on screen.
 * This view can also be used to capture touch events, such as a user
 * interacting with drawn objects.
 */
public class MyGLSurfaceView extends GLSurfaceView {

    private final MyGLRenderer mRenderer;
    private boolean isSpinning = false;
    private boolean isSliding = false;


    public MyGLSurfaceView(Context context) {
        super(context);

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new MyGLRenderer();
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()& MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_MOVE:
                if ( isSpinning ) {
                    System.out.println("ACTION_MOVE");
                    float dx = x - mPreviousX;
                    float dy = y - mPreviousY;

                    // reverse direction of rotation above the mid-line
                    if (y > getHeight() / 2) {
                        dx = dx * -1;
                    }

                    // reverse direction of rotation to left of the mid-line
                    if (x < getWidth() / 2) {
                        dy = dy * -1;
                    }

                    mRenderer.setAngle(
                            mRenderer.getAngle() +
                                    0.4f * ( ((dx + dy) * TOUCH_SCALE_FACTOR) ) );  // = 180.0f / 320
                } else
                //pure translation
                {
                    if ( isSliding ) {
                        System.out.println("ACTION_MOVE Horz=" + (mPreviousX - x) + " VERT = " + (mPreviousY - y));
                        //move to new location
                        mRenderer.shiftOrigin((x - mPreviousX) * 0.001f, (y - mPreviousY) * 0.001f);
                    } else {
                        isSliding = true;
                    }
                }
                requestRender();
                break;
            case MotionEvent.ACTION_DOWN: System.out.println( "ACTION_DOWN");isSliding = true; break;

            case MotionEvent.ACTION_POINTER_DOWN: System.out.println( "ACTION_POINTER_DOWN");isSpinning = true;break;
            case MotionEvent.ACTION_UP: System.out.println( "ACTION_UP");isSliding=false; break;
            case MotionEvent.ACTION_POINTER_UP: System.out.println( "ACTION_POINTER_UP");isSpinning = false;break;
            case MotionEvent.ACTION_OUTSIDE: System.out.println( "ACTION_OUTSIDE");isSliding=false; break;
            case MotionEvent.ACTION_CANCEL: System.out.println( "ACTION_CANCEL");isSpinning=false;isSliding=false;break;

        }

        mPreviousX = x;
        mPreviousY = y;
        return true;

    }
    public void setShape(int position) {
        //doing nothing yet
    }
}
