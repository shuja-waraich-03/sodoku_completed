package sudoku;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

public class Board
{
    private int[][] board;
    private Stack<Move> moveStack; // Stack to store moves for Undo
    private List<Move> moveList;

    public Board()
    {
        board = new int[9][9];
        this.moveStack = new Stack<>();
        this.moveList = new ArrayList<>();

    }

    public static Board loadBoard(InputStream in) {
        Board board = new Board();
        Scanner scanner = new Scanner(in);
        try {
            for (int row = 0; row < 9; row++) {
                if (!scanner.hasNextLine()) {
                    // Throw exception if there are fewer than 9 lines
                    throw new IllegalArgumentException("Input file is missing rows. Expected 9 rows, but fewer were found.");
                }
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    throw new IllegalArgumentException("Empty row found. Each row must contain exactly 9 integers.");
                }
                Scanner lineScanner = new Scanner(line);
                int colCount = 0;
                while (lineScanner.hasNextInt()) {
                    if (colCount >= 9) {
                        // If more than 9 integers are found in a row before all integers are processed
                        throw new IllegalArgumentException("More than 9 integers found in a row. Each row must contain exactly 9 integers.");
                    }
                    int value = lineScanner.nextInt(); // Read next integer from line
                    board.setCellInitial(row, colCount, value); // Load initial board without pushing to stack                    colCount++;
                    colCount++;
                }
                if (colCount < 9) {
                    // If fewer than 9 integers are found in a row after processing all integers
                    throw new IllegalArgumentException("Not enough integers in row. Each row must contain exactly 9 integers.");
                }
                lineScanner.close();
            }
            if (scanner.hasNextLine()) {
                // Throw exception if there are more than 9 lines
                throw new IllegalArgumentException("Input file contains more data than expected. Expected only 9x9 grid.");
            }
        } finally {
            scanner.close(); // Ensure scanner resources are freed
        }
        return board;
    }

    public boolean isLegal(int row, int col, int value)
    {
        return value >= 1 && value <= 9 && getPossibleValues(row, col).contains(value);
    }

    // public void setCell(int row, int col, int value)
    // {
    //     if (value < 0 || value > 9)
    //     {
    //         throw new IllegalArgumentException("Value must be between 1 and 9 (or 0 to reset a value)");
    //     }
    //     if (value != 0 && !getPossibleValues(row, col).contains(value))
    //     {
    //         throw new IllegalArgumentException("Value " + value + " is not possible for this cell");
    //     }
    //     // based on other values in the sudoku grid
    //     board[row][col] = value;
    // }


    //this fixes the issues where undo does not work with a board laoded from a text file
    private void setCellInitial(int row, int col, int value) {
        if (value < 0 || value > 9) {
            throw new IllegalArgumentException("Value must be between 1 and 9 (or 0 to reset a value)");
        }
        board[row][col] = value;
    }

    //this is used to set the value of a cell and push it to the stack

    public void setCell(int row, int col, int value) {
        if (value < 0 || value > 9) {
            throw new IllegalArgumentException("Value must be between 1 and 9 (or 0 to reset a value)");
        }
        if (value != 0 && !getPossibleValues(row, col).contains(value)) {
            throw new IllegalArgumentException("Value " + value + " is not possible for this cell");
        }
        int previousValue = board[row][col];
        board[row][col] = value;
        

        Move move = new Move(row, col, previousValue);
        moveStack.push(move);
        moveList.add(move);
    }

    public void makeMove(int row, int col, int value) {
        if (isLegal(row, col, value)) {
            setCell(row, col, value);
        } else {
            throw new IllegalArgumentException("Illegal move");
        }
    }

    public boolean undoMove() {
        if (!moveStack.isEmpty()) {
            Move lastMove = moveStack.pop();
            board[lastMove.getRow()][lastMove.getCol()] = lastMove.getPreviousValue();
            moveList.add(new Move(lastMove.getRow(), lastMove.getCol(), board[lastMove.getRow()][lastMove.getCol()]));
            return true;
        }
        return false;
    }

    public int getCell(int row, int col)
    {
        return board[row][col];
    }

    public boolean hasValue(int row, int col)
    {
        return getCell(row, col) > 0;
    }

    public Set<Integer> getPossibleValues(int row, int col)
    {
        Set<Integer> possibleValues = new HashSet<>();
        for (int i = 1; i <= 9; i++)
        {
            possibleValues.add(i);
        }
        // check the row
        for (int c = 0; c < 9; c++)
        {
            possibleValues.remove(getCell(row, c));
        }
        // check the column
        for (int r = 0; r < 9; r++)
        {
            possibleValues.remove(getCell(r, col));
        }
        // check the 3x3 square
        int startRow = row / 3 * 3;
        int startCol = col / 3 * 3;
        for (int r = startRow; r < startRow + 3; r++)
        {
            for (int c = startCol; c < startCol + 3; c++)
            {
                possibleValues.remove(getCell(r, c));
            }
        }
        return possibleValues;
    }

    public List<Move> getEnteredValues() {
        return moveList;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < 9; row++)
        {
            for (int col = 0; col < 9; col++)
            {
                sb.append(getCell(row, col));
                if (col < 8)
                {
                    sb.append(" ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

}
