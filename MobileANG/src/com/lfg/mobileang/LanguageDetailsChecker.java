package com.lfg.mobileang;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.List;

/**
 * Created by Pterede on 9/30/2016.
 */

public class LanguageDetailsChecker extends BroadcastReceiver
{
    private List<String> supportedLanguages;

    private String languagePreference;

    Spinner sp;
    Context appCtx;

    public LanguageDetailsChecker(Spinner mysp, Context ctx) {
        this.sp = mysp;
        this.appCtx = ctx;
    }
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Bundle results = getResultExtras(true);
        System.out.println("=====================>"+results);
        if (results.containsKey(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE))
        {
            languagePreference =
                    results.getString(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE);
        }
        if (results.containsKey(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES))
        {
            supportedLanguages =
                    results.getStringArrayList(
                            RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.appCtx,
                android.R.layout.simple_spinner_item, supportedLanguages);
        sp.setAdapter(adapter);
    }


}