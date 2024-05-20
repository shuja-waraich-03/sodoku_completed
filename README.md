# Sudoku Application

Welcome to the Sudoku Application! This project is a Sudoku game implemented using JavaFX. It includes a variety of features to enhance your gameplay experience, such as loading and saving boards, undoing moves, providing hints, and more.

## Features and Implementation Details

### Load Board

The `loadBoard()` method is responsible for loading a Sudoku board from a file. It ensures that the file is valid and throws exceptions if it is not.

**Explanation:**
- The method reads the file line by line.
- It checks that there are exactly 9 lines, and each line contains exactly 9 integers.
- If these conditions are violated, an `IllegalArgumentException` is thrown with an appropriate message.

### Save Board

When saving the board, the application checks if the file already exists and asks the user if they want to overwrite it.

**Explanation:**
- The `FileChooser` allows you to select the file.
- If the file exists, a confirmation dialog pops up, asking for permission to overwrite.
- If confirmed, the file is overwritten; otherwise, the save operation is cancelled.

### Undo the Last Move

The application lets you undo the last move you made.

**Explanation:**
- Moves are stored in a stack (`moveStack`).
- The `undoMove()` method pops the last move from the stack and reverts the board to its previous state.
- This functionality works for both newly created Sudoku games and ones loaded from a file.

### Show Values Entered

You can view all the values that have been entered since the game started.

**Explanation:**
- The `showEnteredValues()` method retrieves all moves from the `moveList`.
- It displays these moves in an alert dialog, showing the row, column, and value of each move.

### Hint: Highlight Cells with Only One Possible Value

The application highlights all cells where only one legal value is possible.

**Explanation:**
- The method scans all cells to find empty ones.
- It calculates possible values for each empty cell.
- If only one possible value is found, the cell is highlighted.
- If no hints are available, an informational alert is shown.

### Right-Click Handler: Show Possible Values

Right-clicking on a cell shows a list of possible values that can be placed in that cell.

**Explanation:**
- A right-click event handler detects right-clicks on a cell.
- The `showPossibleValues()` method is called, displaying possible values for the selected cell in an alert dialog.

## Additional Features

### Timer

A timer tracks how long you've been playing, with the ability to start and stop it.

**Explanation:**
- The `startTimer()` method initializes and starts a timer that updates every second.
- The `stopTimer()` method stops the timer and records the elapsed time.

### Sudoku Solver

The application includes a feature to solve a valid Sudoku puzzle.

**Explanation:**
- The `solvePuzzle()` method uses the `SudokuSolver` class to solve the puzzle.
- If the puzzle is solvable, the board is updated, and a success message is displayed.
- If the puzzle cannot be solved, an error message is shown.
