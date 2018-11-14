package com.objectsolutions.carrom;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

public class CarromActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set to your layout file
        setContentView(R.layout.activity_carrom);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.titlebar, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.restart1:case R.id.restart2:
                System.out.println("restart");
                recreate();
                break;

            case R.id.radioButton2:
                System.out.println("2 player game");
                break;
            case R.id.radioButton4:
                System.out.println("4 player game");
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
