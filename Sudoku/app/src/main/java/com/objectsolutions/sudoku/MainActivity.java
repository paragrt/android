package com.objectsolutions.sudoku;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.preference.EditTextPreference;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.AttributedCharacterIterator;

public class MainActivity extends Activity {
    String[] columnNames = {"A", "B", "C", "D", "E", "F", "G", "H", "I"};
    EditText[][] sudokuArray;
    TextView[] columnHeaders, rowHeaders;
    Sudoku s;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String msg = "To load the previously saved game, select LOAD SAVED.";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout  ll = (LinearLayout)findViewById(R.id.activity_main);
        GridLayout gridLayout = (GridLayout)ll.findViewById(R.id.gl);
        gridLayout.setPadding(20,20,20,20);
        gridLayout.setBackgroundColor(Color.BLACK);
        // add edit text
        sudokuArray = new EditText[9][9];
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(1);

        columnHeaders = new TextView[9];
        rowHeaders = new TextView[9];
        gridLayout.addView(new TextView(this));
        for(int i = 0; i < columnHeaders.length; i++){
            TextView tvc = new TextView(this);
            tvc.setWidth(90);
            tvc.setTextSize(16);
            tvc.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tvc.setTextColor(Color.GREEN);
            tvc.setHeight(55);
            columnHeaders[i] = tvc;
            columnHeaders[i].setText(columnNames[i]);
            gridLayout.addView(columnHeaders[i]);
            TextView tvr = new TextView(this);
            tvr.setTextColor(Color.RED);
            tvr.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tvr.setWidth(90);
            tvr.setHeight(50);
            tvr.setTextSize(16);
            rowHeaders[i] = tvr;
            rowHeaders[i].setText(""+(i+1));
        }
        for (int i = 0; i < sudokuArray.length; i++) {
            gridLayout.addView(rowHeaders[i]);
            for(int j = 0; j < sudokuArray[i].length;j++){
                EditText et = new EditText(this);
                sudokuArray[i][j] = et;
                et.setHint("0");
                et.setHeight(25);
                et.setMinLines(1);
                et.setMaxLines(1);
                et.setFilters(filterArray);

                if ( (i % 3)==2 ) {
                    et.setPadding(et.getPaddingLeft(),et.getPaddingTop(),et.getPaddingRight(), 0);

                }
                if ( (j % 3)==2 ) {
                    et.setPadding(et.getPaddingLeft(),et.getPaddingTop(),0, et.getPaddingBottom());
                    et.setWidth(84);
                } else {
                    et.setWidth(90);
                }

                et.setBackgroundColor(Color.WHITE);
                et.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                gridLayout.addView(et);
            }
        }
        try {
            FileInputStream f = new FileInputStream(getFileStreamPath("currentSudoku.txt"));
        } catch (FileNotFoundException fne) {
            msg = "Did not find any saved games. Use NEW PUZZLE to setup and then Start to begin solving it.";
        }
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
    public void loadDemo(View v) {
        s = new Sudoku();
        try {
            AssetManager am = getAssets();
            s.board = Sudoku.readBoard(am.open("sudoku.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < s.board.length; i++) {
            for(int j = 0; j < s.board[i].length;j++){
                EditText et = sudokuArray[i][j];
                et.setText(s.board[i][j].tryValue);

                et.setEnabled(s.board[i][j].isEditable() );
                if ( et.isEnabled() ) {
                    et.setBackgroundColor(Color.WHITE);
                } else {
                    et.setTextColor(Color.RED);
                    et.setBackgroundColor(Color.LTGRAY);
                }
            }
        }
    }
    public void loadIt(View v) {
        String msg = "Loaded the saved GAME.";
        try {
            FileInputStream f = new FileInputStream(getFileStreamPath("currentSudoku.txt"));
            ObjectInputStream oos = new ObjectInputStream(f);
            s = (Sudoku)oos.readObject(); // read string
            oos.close();
            for (int i = 0; i < s.board.length; i++) {
                for(int j = 0; j < s.board[i].length;j++){
                    EditText et = sudokuArray[i][j];
                    et.setText(s.board[i][j].tryValue);

                    et.setEnabled(s.board[i][j].isEditable() );
                    if ( et.isEnabled() ) {
                        et.setBackgroundColor(Color.WHITE);
                    } else {
                        et.setTextColor(Color.RED);
                        et.setBackgroundColor(Color.LTGRAY);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            msg = "Unable to load or find the saved GAME." + e.getMessage();
        }
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
    public void saveIt(View v) {
        String msg = "Your game will be saved. Use LOAD SAVED button to start where you left off.";
        for (int i = 0; i < s.board.length; i++) {
            for(int j = 0; j < s.board[i].length;j++){
                EditText et = sudokuArray[i][j];
                if ( et.isEnabled() ) {
                    s.board[i][j].tryValue = et.getText().toString();
                }
            }
        }
        try {
            FileOutputStream f = new FileOutputStream(getFileStreamPath("currentSudoku.txt"));
            ObjectOutputStream oos = new ObjectOutputStream(f);
            oos.writeObject(s); // write string
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
            msg = "Saving the game failed. You will need to restart the game." + e.getMessage();
        }
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
    public void clearIt(View v) {

        for (int i = 0; i < sudokuArray.length; i++) {
            for(int j = 0; j < sudokuArray[i].length;j++){
                EditText et = sudokuArray[i][j];
                et.setEnabled(true);
                et.setText("");
                et.setHint("0");
                et.setBackgroundColor(Color.WHITE);
                et.setTextColor(Color.DKGRAY);
            }
        }
        s = null;
    }
    public void helpMe(View v) {
        if ( s == null || s.board == null ) {
            System.out.println("=====================> NOT SET ==========================>");
            Toast.makeText(this, "Please ensure that a Sudoku is loaded(load or setup).", Toast.LENGTH_LONG).show();
            return;
        }
        Sudoku.markSureCells(s.board);
        for (int i = 0; i < s.board.length; i++) {
            for(int j = 0; j < s.board[i].length;j++){
                EditText et = sudokuArray[i][j];
                if ( et.isEnabled() ) {
                    et.setText(s.board[i][j].tryValue);
                }
            }
        }
    }
    public void setIt(View v) {
        String[][] brd = new String[9][9];
        for (int i = 0; i < sudokuArray.length; i++) {
            for(int j = 0; j < sudokuArray[i].length;j++){
                EditText et = sudokuArray[i][j];
                String val = et.getText().toString();
                if ( !val.isEmpty() && !"0".equals(val) ) {
                    et.setEnabled(false);
                    brd[i][j] = val;
                } else {
                    brd[i][j] = "0";
                }
            }
        }
        s = new Sudoku(brd);
    }
    public void checkIt(View v) {
        int[][] mycells = new int[9][9];
        for (int i = 0; i < mycells.length; i++) {
            for (int j = 0; j < mycells[i].length; j++) {
                try {
                    mycells[i][j] = Integer.parseInt(sudokuArray[i][j].getText().toString());
                } catch ( Exception e) {
                    mycells[i][j] = 0;
                }
            }
        }
        boolean allGood = true;
        for (int i = 0; i < mycells.length; i++) {
            for (int j = 0; j < mycells[i].length; j++) {
                boolean legalStatus = Sudoku.checkCell(i, j, mycells[i][j], mycells);
                if ( !legalStatus ) {
                    sudokuArray[i][j].setBackgroundColor(Color.YELLOW);
                    allGood = false;
                }
            }
        }
        Toast.makeText(this,
                allGood?"All Good! Congrats.":"Check for errors in Yellow boxes.",
                Toast.LENGTH_LONG).show();

    }
    public void solveIt(View v) {
        try {
            int[][] result = Sudoku.solveIt(s.board);
            for (int i = 0; i < result.length; i++) {
                for(int j = 0; j < result[i].length;j++){
                    EditText et = sudokuArray[i][j];
System.out.print(result[i][j]+", ");
                    if ( et.isEnabled() ) {
                        et.setText("" + result[i][j]);
                        et.setEnabled(false);
                        et.setTextColor(Color.BLACK);
                    }
                }
                System.out.println();
            }
        } catch ( Exception e) {
            Toast.makeText(this, "No Soultion Exists", Toast.LENGTH_LONG).show();
        }

    }
}
