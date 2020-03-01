package com.example.sudoku2;

/**
 * Created by paragrt on 12/19/2016.
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Sudoku2 implements Serializable {
    SudokuCell[][] board;
    public static void main(String[] args) throws Exception {
        Sudoku2 s = new Sudoku2();
        s.board = readBoard(new FileInputStream("c:\\temp\\sudoku.txt"));

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                System.out.print(s.board[i][j].origValue + ",");
            }
            System.out.println();
        }

        // 1st pass....mark all the sure cells...this also builds up a new guess list
        markSureCells(s.board);

        //Backtracking recursive algorithm starts
        int[][] board2 = copyBoard(s.board);

        if ( solve(0,0,board2) ) {
            writeMatrix(board2);
        } else {
            System.out.println("No solution exists");
        }

    }
    public static int[][] solveIt(SudokuCell[][] board)throws Exception {
        int[][] board2 = copyBoard(board);

        if ( solve(0,0,board2) ) {
            writeMatrix(board2);
            return board2;
        } else {
            System.out.println("No solution exists");
            throw new Exception("No Solution Exists");
        }
    }
    public static boolean solve(int i, int j, int[][] cells) {
        if (i == 9) {
            i = 0;
            if (++j == 9)
                return true;
        }
        if (cells[i][j] != 0)  // skip filled cells
            return solve(i+1,j,cells);

        for (int val = 1; val <= 9; ++val) {
            if (legal(i,j,val,cells)) {
                cells[i][j] = val;
                if (solve(i+1,j,cells))
                    return true;
            }
        }
        cells[i][j] = 0; // reset on backtrack
        return false;
    }
    public static boolean checkCell(int i, int j, int val, int[][] cells) {
        for (int k = 0; k < 9; ++k)  // row
            if (i != k && val == cells[k][j])
                return false;

        for (int k = 0; k < 9; ++k) // col
            if (j != k && val == cells[i][k])
                return false;

        int boxRowOffset = (i / 3)*3;
        int boxColOffset = (j / 3)*3;
        for (int k = 0; k < 3; ++k) // box
            for (int m = 0; m < 3; ++m)
                if ( ((boxRowOffset+k) != i && (boxRowOffset+m) != j) && val == cells[boxRowOffset+k][boxColOffset+m])
                    return false;

        return true; // no violations, so it's legal
    }
    public static boolean legal(int i, int j, int val, int[][] cells) {
        for (int k = 0; k < 9; ++k)  // row
            if (val == cells[k][j])
                return false;

        for (int k = 0; k < 9; ++k) // col
            if (val == cells[i][k])
                return false;

        int boxRowOffset = (i / 3)*3;
        int boxColOffset = (j / 3)*3;
        for (int k = 0; k < 3; ++k) // box
            for (int m = 0; m < 3; ++m)
                if (val == cells[boxRowOffset+k][boxColOffset+m])
                    return false;

        return true; // no violations, so it's legal
    }

    static void writeMatrix(int[][] solution) {
        for (int i = 0; i < 9; ++i) {
            if (i % 3 == 0)
                System.out.println(" -----------------------");
            for (int j = 0; j < 9; ++j) {
                if (j % 3 == 0) System.out.print("| ");
                System.out.print(solution[i][j] == 0
                        ? " "
                        : Integer.toString(solution[i][j]));

                System.out.print(' ');
            }
            System.out.println("|");
        }
        System.out.println(" -----------------------");
    }
    private static int[][] copyBoard(SudokuCell[][] board) {
        int[][] retBrd = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                retBrd[i][j] = Integer.parseInt(board[i][j].tryValue);
            }
        }
        return retBrd;
    }

    //returns
    //1 if we made progress
    //-1 if we hit a unsolveable cell.
    //0 if we need to move to the next guess.
    public static int markSureCells(SudokuCell[][] board) {

        // the SURE mark phase
        for (int passNbr = 1;; passNbr++) {
            int     gotOneSure = 0;

            // do entire board
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if (!board[i][j].isSolved()) {
                        //if we reached a dead end, we need to backtrack our guess
                        int result = markupOneCell(i, j, board);
                        if( result == -1 ) {
                            return -1;
                        } else if ( result == 1 ) {
                            //either sureCell(result = 1) or multiple options(result = zero)
                            gotOneSure = 1;
                        } else {
                            //if result == 0, then leave gotOneSure untouched(it may be 1 or zero)
                        }
                    }
                }
            }
            // if no improvement then quit
            System.out.println("------------------PASS #" + passNbr + "--------------------");
            return gotOneSure;
        }
    }

    // Start top left corner(0,0)
    // Find empty cell.
    // try a nbr not in the row, not in the column and not in the 3x3 cell.
    // returns
    //1 if we made progress
    //-1 if we hit a unsolveable cell.
    //0 if we need to move to the next guess.
    private static int markupOneCell(int row, int col, SudokuCell[][] board) {

        List<String> pots = new ArrayList<String>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9"));
        List<String> alreadyInRow = new ArrayList<String>();
        List<String> alreadyInCol = new ArrayList<String>();
        List<String> alreadyIn3x3 = new ArrayList<String>();
        for (int i = 0; i < 9; i++) {
            if (!board[row][i].isEmpty()) {
                alreadyInRow.add(board[row][i].tryValue);
            }
        }
        for (int i = 0; i < 9; i++) {
            if (!board[i][col].isEmpty()) {
                alreadyInCol.add(board[i][col].tryValue);
            }
        }
        int cell_start_row = (row / 3) * 3;
        int cell_start_col = (col / 3) * 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (!board[cell_start_row + i][cell_start_col + j].isEmpty()) {
                    alreadyIn3x3.add(board[cell_start_row + i][cell_start_col + j].tryValue);
                }
            }
        }
        pots.removeAll(alreadyInRow);
        pots.removeAll(alreadyInCol);
        pots.removeAll(alreadyIn3x3);

        // no guessing found exact match
        if (pots.size() == 1) {
            System.out.println("SURE: [" + row + "," + col + "]==>" + pots.get(0));
            board[row][col].tryValue = pots.get(0);
            board[row][col].possibleValues = null;
            //found a sure cell
            return 1;
        }

        // no solution...need to backtrack
        if (pots.isEmpty()) {
            System.out.println("Unable to solve this puzzle.");
            System.exit(-1);
            //hit a wall...unsolveable cell.
            return -1;
        }

        // Only record one guess....because the rest of the guesses are meaningless unless we know the right value for this
        if (pots.size() > 1 ) {
            System.out.println("POSSible: [" + row + "," + col + "]==>" + pots);
            board[row][col].possibleValues = pots;
            //no progress
            return 0;
        }
        //if we found multiple options and did not update the guessList
        //then also return 0
        return 0;
    }

    public static SudokuCell[][] readBoard(InputStream is) throws IOException {
        String s = readStream(is);
        s = s.replaceAll("\\n", "");
        String[] arr = s.split(",|\\n");
        SudokuCell[][] board = new SudokuCell[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                board[i][j] = new SudokuCell();
                board[i][j].origValue = arr[(i * 9) + j].trim();
                board[i][j].tryValue = arr[(i * 9) + j].trim();
                if (!board[i][j].isEmpty()) {
                    board[i][j].possibleValues = null;// solved
                }
            }

        }
        return board;
    }

    public static SudokuCell[][] readBoard() throws IOException {
        int idx = (int)(Math.random() * Sudoku2Solver.array_of_boards.length);
        int[][] arr = Sudoku2Solver.array_of_boards[idx];
        SudokuCell[][] board = new SudokuCell[arr.length][arr.length];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                board[i][j] = new SudokuCell();
                board[i][j].origValue = String.valueOf(arr[i][j]);
                board[i][j].tryValue = String.valueOf(arr[i][j]);
                if (!board[i][j].isEmpty()) {
                    board[i][j].possibleValues = null;// solved
                }
            }

        }
        return board;
    }
    public Sudoku2(String[][] brd) {

        this.board = new SudokuCell[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                board[i][j] = new SudokuCell();
                board[i][j].origValue = brd[i][j];
                board[i][j].tryValue = brd[i][j];
                if (!board[i][j].isEmpty()) {
                    board[i][j].possibleValues = null;// solved
                }
            }

        }
    }
    public Sudoku2() {

    }

    private static String readStream(InputStream in) throws IOException {
        byte[] arr = new byte[4096];
        StringBuffer sb = new StringBuffer();
        int count = 0;
        while ((count = in.read(arr)) > 0) {
            String s = new String(arr, 0, count);
            sb.append(s);
            // System.out.println(s);
        }
        return sb.toString();
    }

    static class SudokuCell implements Serializable {
        public boolean isSolved() {
            return possibleValues == null && !isEmpty();
        }

        public String origValue;
        public String tryValue;
        public int tryIdx = -1;

        public boolean isEditable() {
            return (origValue == null || origValue == "" || "0".equals(origValue));
        }

        public boolean isEmpty() {
            return (tryValue == null || tryValue == "" || "0".equals(tryValue));
        }

        public List<String> possibleValues = new ArrayList<String>();


        public SudokuCell() {

        }
        public SudokuCell(SudokuCell a) {
            super();
            this.origValue = a.origValue;
            this.tryValue = a.tryValue;
            this.possibleValues = a.possibleValues;
            this.tryIdx = a.tryIdx;
        }

    }
}