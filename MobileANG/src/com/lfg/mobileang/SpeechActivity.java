package com.lfg.mobileang;

/**
 * Created by Pterede on 8/17/2016.
 */

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.api.GoogleAPI;
import com.google.api.translate.Language;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class SpeechActivity extends Activity {
    TextToSpeech t1,t2;

    EditText ed1, ed2;
    Button b1, b2;
    Spinner sp1, sp2;

    Map<String, String> map = new HashMap<String, String>();
    Map<String, Language> mapL = new HashMap<String, Language>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);



        ed1 = (EditText) findViewById(R.id.speak_editText);
        ed2 = (EditText) findViewById(R.id.text_editText);

        b1 = (Button) findViewById(R.id.speak_button);
        b2 = (Button) findViewById(R.id.text_button);

        sp1 = (Spinner) findViewById(R.id.spinner1);
        sp2 = (Spinner) findViewById(R.id.spinner2);

        map.put("Hindi", "hi-IN");
        map.put("Marathi", "hi-IN");
        map.put("Bengali", "bn-BN");
        map.put("Gujarati", "gu-IN");
        map.put("Indian English", "en-IN");
        map.put("British English", "en-GB");
        map.put("American English", "en-US");
        map.put("French", "fr-FR");
        map.put("French Canadian", "fr-CA");
        map.put("German", "de-DE");
        map.put("Urdu", "ur");

        mapL.put("Hindi", Language.HINDI);
        mapL.put("Marathi", Language.MARATHI);
        mapL.put("Bengali",Language.BENGALI);
        mapL.put("Gujarati", Language.GUJARATI);
        mapL.put("Indian English", Language.ENGLISH);
        mapL.put("British English", Language.ENGLISH);
        mapL.put("American English",  Language.ENGLISH);
        mapL.put("French", Language.FRENCH);
        mapL.put("French Canadian", Language.FRENCH);
        mapL.put("German",Language.GERMAN);
        mapL.put("Urdu",Language.URDU);

        //set Hindi & English as default
        sp1.setSelection(0);
        sp2.setSelection(5);
        showLangs(null);

    }

    public void showLangs(View v) {
        //Added to lookup languages
        Intent detailsIntent =  new Intent(RecognizerIntent.ACTION_GET_LANGUAGE_DETAILS);
        LanguageDetailsChecker ldc = new LanguageDetailsChecker((Spinner) findViewById(R.id.spinner3), getApplicationContext());
        sendOrderedBroadcast(
                detailsIntent, null, ldc, null, Activity.RESULT_OK, null, null);

    }

    public void onPause() {
        if (t1 != null) {
            t1.stop();
            t1.shutdown();
        }
        if (t2 != null) {
            t2.stop();
            t2.shutdown();
        }
        super.onPause();
    }
    public void startTalkingS2T() {

        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                System.out.println("=====STatus=" + status);
                if (status != TextToSpeech.ERROR) {


                    String country_selection = sp1.getSelectedItem().toString();
                    System.out.println("=====Locale=" + map.get(country_selection));
                    System.out.println("-------available locales----->"+t1.getAvailableLanguages());
                    t1.setLanguage(new Locale(map.get(country_selection)));
                    String cmd = ed1.getText().toString();

                    Toast.makeText(getApplicationContext(), cmd, Toast.LENGTH_SHORT).show();
                    t1.speak(cmd, TextToSpeech.QUEUE_FLUSH, null);


                }
            }
        });
    }
    public void startTalkingT2S() {

        t2 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                System.out.println("=====STatus=" + status);
                if (status != TextToSpeech.ERROR) {


                    String country_selection = sp2.getSelectedItem().toString();
                    System.out.println("=====Locale=" + map.get(country_selection));
                    System.out.println("-------available locales----->"+t2.getAvailableLanguages());
                    t2.setLanguage(new Locale(map.get(country_selection)));
                    String cmd = ed2.getText().toString();

                    Toast.makeText(getApplicationContext(), cmd, Toast.LENGTH_SHORT).show();
                    t2.speak(cmd, TextToSpeech.QUEUE_FLUSH, null);


                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( requestCode == 100 && resultCode == RESULT_OK) {
            onSpeechActivity(requestCode, resultCode, data);
        }
    }
    /**
     * Receiving speech input
     * */

    private void onSpeechActivity(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        switch (requestCode) {
            case 100: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    System.out.println("Possible interpretations:"+result);

                    ed1.setTextColor(Color.RED);
                    ed1.setText(result.get(0));
                    startTalkingS2T();
                }
                break;
            }
        }
    }
    public void startListeningFor(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        String country_selection = sp1.getSelectedItem().toString();
        System.out.println("Listening for=====Locale=" + map.get(country_selection));
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, map.get(country_selection));
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say something in "+ country_selection );

        try {
            startActivityForResult(intent, 100);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "You cant talk to me like that",
                    Toast.LENGTH_SHORT).show();
        }

    }
    public void translate(View view) {


        Toast.makeText(getApplicationContext(),
                "Translating " + ed1.getText(),
                Toast.LENGTH_SHORT).show();

        ed2.setText(ed1.getText());

        String country_selection1 = sp1.getSelectedItem().toString();

        String country_selection2 = sp2.getSelectedItem().toString();

        new TranslateTask(ed1.getText().toString(), mapL.get(country_selection1), mapL.get(country_selection2)).execute();
        //translate(ed1.getText().toString(), mapL.get(country_selection1), mapL.get(country_selection2));


        startTalkingT2S();
    }
    public static String translate(String input, Language from, Language to) {

        try {
            //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            //StrictMode.setThreadPolicy(policy);
            GoogleAPI.setHttpReferrer("https://www.yahoo.com");

// Set the Google Translate API key
// See: http://code.google.com/apis/language/translate/v2/getting_started.html
            GoogleAPI.setKey("AIzaSyCXeYd2VxLbGdd1aTbsS2E2l60gOBCAo28");

            String translatedText = com.google.api.translate.Translate.DEFAULT.execute(input, from, to);

            System.out.println(translatedText);
            return translatedText;
        } catch (Exception e) {
            e.printStackTrace();
            return "UNABLE to Translate:" + e.getMessage();
        }

    }
    private class TranslateTask extends AsyncTask<Void, Void, String> {

        private String input;
        private Language from, to;
        private TranslateTask(String inputStr, Language fromLanguage, Language toLanaguage) {
            this.input = inputStr;
            this.from = fromLanguage;
            this.to = toLanaguage;
        }
        protected String doInBackground(Void... nothing) {
            try{
                GoogleAPI.setHttpReferrer("https://www.yahoo.com");

// Set the Google Translate API key
// See: http://code.google.com/apis/language/translate/v2/getting_started.html
                GoogleAPI.setKey("AIzaSyCXeYd2VxLbGdd1aTbsS2E2l60gOBCAo28");

                String translatedText = com.google.api.translate.Translate.DEFAULT.execute(input, from, to);

                System.out.println(translatedText);
                return translatedText;
            } catch (Exception e) {
                e.printStackTrace();
                return "UNABLE to Translate:" + e.getMessage();
            }
        }
        protected void onPostExecute(String result) {
            ed2.setText(result);
        }
    }

    public void go2sfdc(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public void go2Sensor(View view) {
        Intent intent = new Intent(this, SensorActivity.class);
        startActivity(intent);
    }
    public void go2Fractal(View view) {
        Intent intent = new Intent(this, OPENGLESActivity.class);
        startActivity(intent);
    }
    public void playCarrom(View view) {
        Intent intent = new Intent(this, CarromActivity.class);
        startActivity(intent);
    }
}


