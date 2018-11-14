package com.lfg.mobileang;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.salesforce.androidsdk.push.PushNotificationInterface;

import java.util.Locale;

/**
 * Created by Pterede on 8/19/2016.
 */
public class MyPushHandler implements PushNotificationInterface {

    Activity activity;
    TooltipWindow tipWindow;
    TextToSpeech t3;
    public MyPushHandler(Activity a, TooltipWindow t) {
        this.activity = a;
        this.tipWindow = t;
    }

    @Override
    public void onPushMessageReceived(final Bundle m) {

        System.out.println("=============>got pushhhhhhh message"+m);


        this.activity.runOnUiThread(new Runnable() {
        @Override
        public void run() {
            String message = m.getString("msg");
            ((TextView)activity.findViewById(R.id.pushMsg)).setText(message.toString());
            activity.findViewById(R.id.pushMsg).setVisibility(View.VISIBLE);
            t3 = new TextToSpeech(activity, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    System.out.println("=====STatus=" + status);
                    if (status != TextToSpeech.ERROR) {
                        t3.setLanguage(Locale.US);

                        t3.speak("ANG PUSH Notification:"+m.getString("msg"), TextToSpeech.QUEUE_FLUSH, null);

                    }
                }
            });


            Toast.makeText(activity,
            		message.toString(),
            		Toast.LENGTH_LONG).show();
            /*
            ((TextView)activity.findViewById(R.id.pushMsg)).postDelayed(new Runnable() {
                public void run() {
                    ((TextView)activity.findViewById(R.id.pushMsg)).setVisibility(View.INVISIBLE);
                }
            }, 6000);*/
        }
    });

    }
}
