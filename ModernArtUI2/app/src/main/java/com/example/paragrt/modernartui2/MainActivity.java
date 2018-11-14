package com.example.paragrt.modernartui2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final DrawView drawView = (DrawView)findViewById(R.id.drawview1);
        SeekBar sb = (SeekBar)findViewById(R.id.seekBar);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //System.out.println("onProgressChanged===> progress=" + progress + " boolean fromUser:" + fromUser);
                //as user drags the thumbnail...redraw picture on canvas
                // Gives a "colorful" rendering of random number generation sequence
                drawView.updateDrawing();

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //System.out.println("onStartTrackingTouch===> STARTED");
                // could do something here but not now
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //System.out.println("onStopTrackingTouch===> STOPPED");
                // could do something here but not now
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        //http://developer.android.com/guide/topics/ui/dialogs.html
        //NOTE: Negative and Positve are switched around to position the buttons as required.
        new AlertDialog.Builder(this)
                .setTitle("Visit MOMA?")
                .setMessage("Would you like to visit the Museum of Modern Art in NY?")
                .setNegativeButton("YES!, Visit MOMA", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("User clicked YES:" + which);
                        Uri uri = Uri.parse("http://www.moma.com");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                })
                .setPositiveButton("Never", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("User clicked Never:" + which);
                    }
                })
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show();

        return super.onOptionsItemSelected(item);
    }
}
