package com.example.sudoku2;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.InputFilter;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    List<String> myList = new ArrayList<>();
    ArrayAdapter<String> adapter;
    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    String[] columnNames = {"A", "B", "C", "D", "E", "F", "G", "H", "I"};
    EditText[][] sudokuArray;
    TextView[] columnHeaders, rowHeaders;
    Sudoku2 s;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String msg = "To load the previously saved game, select LOAD SAVED.";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout ll = (LinearLayout)findViewById(R.id.activity_main);
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
            tvc.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);//tvc.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tvc.setTextColor(Color.GREEN);
            tvc.setHeight(55);
            columnHeaders[i] = tvc;
            columnHeaders[i].setText(columnNames[i]);
            gridLayout.addView(columnHeaders[i]);
            TextView tvr = new TextView(this);
            tvr.setTextColor(Color.RED);
            tvr.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);//
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
                et.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);//
                gridLayout.addView(et);
            }

            // Get ListView object from xml
            listView = (ListView) findViewById(R.id.list);

            adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, myList);
            listView.setAdapter(adapter);
        }
        try {
            FileInputStream f = new FileInputStream(getFileStreamPath("currentSudoku.txt"));
        } catch (FileNotFoundException fne) {
            msg = "Did not find any saved games. Use NEW PUZZLE to setup and then Start to begin solving it.";
        }
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
    public void loadDemo(View v) {
        s = new Sudoku2();
        try {
            AssetManager am = getAssets();
            s.board = Sudoku2.readBoard();
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
            s = (Sudoku2)oos.readObject(); // read string
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
        Sudoku2.markSureCells(s.board);
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
        s = new Sudoku2(brd);
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
                boolean legalStatus = Sudoku2.checkCell(i, j, mycells[i][j], mycells);
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
            //convert board to int[][]
            int[][] myboard = new int[9][9];
            for(int i = 0; i < 9; i++) {
                for(int j = 0; j < 9; j++) {
                    if ( s.board[i][j] == null ) {
                        myboard[i][j] = 0;
                    }else{
                        myboard[i][j] = Integer.parseInt(s.board[i][j].origValue);
                    }
                }
            }
            boolean ans = Sudoku2Solver.solveSudoku(myboard);
            for (int i = 0; i < myboard.length; i++) {
                for(int j = 0; j < myboard[i].length;j++){
                    EditText et = sudokuArray[i][j];
                    System.out.print(myboard[i][j]+", ");
                    if ( et.isEnabled() ) {
                        et.setText("" + myboard[i][j]);
                        et.setEnabled(false);
                        et.setTextColor(Color.BLACK);
                    }
                }
                System.out.println();
            }
            displaySolution(Sudoku2Solver.steps2solve);
        } catch ( Exception e) {
            Toast.makeText(this, "No Soultion Exists", Toast.LENGTH_LONG).show();
        }

    }

    private void displaySolution(List<String> steps2solve) {
        myList.clear();
        myList.add("Total steps:" + steps2solve.size());
        myList.addAll(steps2solve);
        adapter.notifyDataSetChanged();
    }
}
