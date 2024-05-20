package sudoku;

public class SudokuSolver {
    public static final int SIZE = 9;

    // Validation function to check if a number can be placed in a given cell
    public boolean isValid(Board board, int row, int col, int num) {
        // Check if the number is already in the row
        for (int x = 0; x < SIZE; x++) {
            if (board.getCell(row, x) == num) {
                return false;
            }
        }
        
        // Check if the number is already in the column
        for (int x = 0; x < SIZE; x++) {
            if (board.getCell(x, col) == num) {
                return false;
            }
        }

        // Check if the number is in the 3x3 subgrid
        int startRow = row - row % 3;
        int startCol = col - col % 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board.getCell(i + startRow, j + startCol) == num) {
                    return false;
                }
            }
        }

        return true;
    }

    // Solve function using backtracking algorithm
    public boolean solveSudoku(Board board) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board.getCell(row, col) == 0) { // Find an empty cell
                    for (int num = 1; num <= SIZE; num++) { // Try all possible numbers
                        if (isValid(board, row, col, num)) {
                            board.setCell(row, col, num); // Place the number
                            if (solveSudoku(board)) { // Recursively solve the rest of the board
                                return true;
                            }
                            board.setCell(row, col, 0); // Backtrack if no solution found
                        }
                    }
                    return false; // No valid number found, trigger backtracking
                }
            }
        }
        return true; // Solved
    }
}
