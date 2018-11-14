package com.lfg.mobileang;

import android.graphics.Canvas;

/**
 * Created by pterede on 10/6/2016.
 */

public class MyThread extends Thread {

    CarromView myView;
    private boolean running = true;

    public MyThread(CarromView view) {
        myView = view;
    }

    public void end() {
        running = false;
    }
    public boolean isRunning() { return running;}
    @Override
    public void run() {
        while(running){

            Canvas canvas = myView.getHolder().lockCanvas();

            if(canvas != null){
                synchronized (myView.getHolder()) {
                    myView.drawSomething(canvas);
                }
                myView.getHolder().unlockCanvasAndPost(canvas);
            }

            try {
                sleep(10); //make it 20 or 50 for real runtime....200 is for debugging
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

}