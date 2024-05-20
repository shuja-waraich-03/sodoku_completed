package sudoku;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

//timer imports
import java.util.Timer;
import java.util.TimerTask;

public class Sudoku extends Application
{
    private Board board = new Board();
    public static final int SIZE = 9;
    private VBox root;
    private TextField[][] textFields = new TextField[SIZE][SIZE];
    private int width = 600;
    private int height = 600;
    private boolean updatingBoard = false;


    //timer variables
    private Timer timer;
    private long startTime;
    private long elapsedTime = 0; // to keep track of cumulative elapsed time
    private Label timerLabel;


    @Override
    public void start(Stage primaryStage) throws Exception
    {
        root = new VBox();

        // Initialize the timer label
        timerLabel = new Label("Time: 0 seconds");
        HBox timerBox = new HBox(timerLabel);
        timerBox.setStyle("-fx-alignment: top-right; -fx-padding: 10;");
        root.getChildren().add(timerBox);

        //System.out.println(new File(".").getAbsolutePath());
        root.getChildren().add(createMenuBar(primaryStage));

        GridPane gridPane = new GridPane();
        root.getChildren().add(gridPane);
        gridPane.getStyleClass().add("grid-pane");

        // create a 9x9 grid of text fields
        for (int row = 0; row < SIZE; row++)
        {
            for (int col = 0; col < SIZE; col++)
            {
                textFields[row][col] = new TextField();
                TextField textField = textFields[row][col];
                
                // setting ID so that we can look up the text field by row and col
                // IDs are #3-4 for the 4th row and 5th column (start index at 0)
                textField.setId(row + "-" + col);
                gridPane.add(textField, col, row);
                // using CSS to get the darker borders correct
                if (row % 3 == 2 && col % 3 == 2)
                {
                    // we need a special border to highlight the borrom right
                    textField.getStyleClass().add("bottom-right-border");
                }
                else if (col % 3 == 2) { 
                    // Thick right border
                    textField.getStyleClass().add("right-border");
                }
                else if (row % 3 == 2) { 
                    // Thick bottom border
                    textField.getStyleClass().add("bottom-border");
                }

                // add a handler for when we select a textfield
                textField.setOnMouseClicked(event -> {
                    // toggle highlighting
                    if (textField.getStyleClass().contains("text-field-selected"))
                    {
                        // remove the highlight if we click on a selected cell
                        textField.getStyleClass().remove("text-field-selected");
                    }
                    else
                    {
                        // otherwise 
                        textField.getStyleClass().add("text-field-selected");
                    }
                });

                // add a handler for when we lose focus on a textfield
                textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
                    if (!newValue)
                    {
                        // remove the highlight when we lose focus
                        textField.getStyleClass().remove("text-field-selected");
                    }
                });

                // RIGHT-CLICK handler
                // add handler for when we RIGHT-CLICK a textfield
                // to bring up a selection of possible values
                textField.setOnContextMenuRequested(event -> {
                    // change the textfield background to red while keeping the rest of the css the same
                    // textField.getStyleClass().add("text-field-highlight");
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Possible values");
                    String id = textField.getId();
                    String[] parts = id.split("-");
                    int r = Integer.parseInt(parts[0]);
                    int c = Integer.parseInt(parts[1]);

                    showPossibleValues(r, c);
                });

                // using a listener instead of a KEY_TYPED event handler
                // KEY_TYPED requires the user to hit ENTER to trigger the event
                textField.textProperty().addListener((observable, oldValue, newValue) -> {

                    if (updatingBoard)
                    {
                        return;
                    }

                    if (!newValue.matches("[1-9]?")) {
                        // restrict textField to only accept single digit numbers from 1 to 9
                        textField.setText(oldValue);
                    }
                    String id = textField.getId();
                    String[] parts = id.split("-");
                    int r = Integer.parseInt(parts[0]);
                    int c = Integer.parseInt(parts[1]);
                    
                    if (newValue.length() > 0)
                    {
                        try
                        {
                            System.out.printf("Setting cell %d, %d to %s\n", r, c, newValue);
                            int value = Integer.parseInt(newValue);
                            board.setCell(r, c, value);
                            // remove the highlight when we set a value
                            textField.getStyleClass().remove("text-field-selected");
                            
                        }
                        catch (NumberFormatException e)
                        {
                            // ignore; should never happen
                        }
                        catch (Exception e)
                        {
                            // TODO: if the value is not a possible value, catch the exception and show an alert
                            System.out.println("Invalid Value: " + newValue);
                            Alert alert = new Alert(AlertType.ERROR);
                            alert.setTitle("Invalid Value");
                            alert.setHeaderText("Invalid Value");
                            alert.setContentText("This value is not possible in this cell. Please try again.");
                            alert.showAndWait();
                            updateBoard();
                            updateBoard();
                            
                        }
                    }
                    else
                    {
                        board.setCell(r, c, 0);
                    }
                });
            }
        }


        // add key listener to the root node to grab ESC keys
        root.setOnKeyPressed(event -> {
            System.out.println("Key pressed: " + event.getCode());
            switch (event.getCode())
            {
                // check for the ESC key
                case ESCAPE:
                    // clear all the selected text fields
                    for (int row = 0; row < SIZE; row++)
                    {
                        for (int col = 0; col < SIZE; col++)
                        {
                            TextField textField = textFields[row][col];
                            textField.getStyleClass().remove("text-field-selected");
                        }
                    }
                    break;
                default:
                    System.out.println("you typed key: " + event.getCode());
                    break;
                
            }
        });

        Scene scene = new Scene(root, width, height);

        URL styleURL = getClass().getResource("/style.css");
		String stylesheet = styleURL.toExternalForm();
		scene.getStylesheets().add(stylesheet);
        primaryStage.setTitle("Sudoku");
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
        	System.out.println("oncloserequest");
            stopTimer();
        });

        startTimer();
    }

    private void updateBoard()
    {
        updatingBoard = true;
        for (int row = 0; row < SIZE; row++)
        {
            for (int col = 0; col < SIZE; col++)
            {
                TextField textField = textFields[row][col];
                int value = board.getCell(row, col); 
                if (value > 0)
                {
                    textField.setText(Integer.toString(value));
                }
                else
                {
                    textField.setText("");
                }
            }
        }
        updatingBoard = false;
    }

    private void showPossibleValues(int row, int col) {
        Set<Integer> possibleValues = board.getPossibleValues(row, col);
        StringBuilder content = new StringBuilder("Possible values: ");
        for (Integer value : possibleValues) {
            content.append(value).append(" ");
        }

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Possible values");
        alert.setContentText(content.toString());
        alert.showAndWait();
    }

    private MenuBar createMenuBar(Stage primaryStage)
    {
        MenuBar menuBar = new MenuBar();
    	menuBar.getStyleClass().add("menubar");

        //
        // File Menu
        //
    	Menu fileMenu = new Menu("File");

        addMenuItem(fileMenu, "Load from file", () -> {
            System.out.println("Load from file");
            FileChooser fileChooser = new FileChooser();
            // XXX: this is a hack to get the file chooser to open in the right directory
            // we should probably have a better way to find this folder than a hard coded path
			fileChooser.setInitialDirectory(new File("../puzzles"));
			File sudokuFile = fileChooser.showOpenDialog(primaryStage);
            if (sudokuFile != null)
            {
                System.out.println("Selected file: " + sudokuFile.getName());
                
                try {
                    //TODO: loadBoard() method should throw an exception if the file is not a valid sudoku board
                    board = Board.loadBoard(new FileInputStream(sudokuFile));
                    updateBoard();
                } catch (Exception e) {
                    // pop up and error window
                    Alert alert = new Alert(AlertType.ERROR);
    	            alert.setTitle("Unable to load sudoku board from file "+ sudokuFile.getName());
    	            alert.setHeaderText(e.getMessage());
                    alert.setContentText(e.getMessage());
                    e.printStackTrace();
                    if (e.getCause() != null) e.getCause().printStackTrace();
                    
                    alert.showAndWait();
                }
            }
        });

        // save to text
        addMenuItem(fileMenu, "Save to text", () -> {
            System.out.println("Save puzzle to text");
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File("../puzzles"));
            File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null)
            {
                System.out.println("Selected file: " + file.getName());
                try {
                // Check if the file already exists and prompt for confirmation to overwrite
                if (file.exists()) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirm Overwrite");
                    alert.setHeaderText("File already exists");
                    alert.setContentText("Do you want to overwrite the existing file?");
                    java.util.Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() != ButtonType.OK) {
                        return; // If user does not confirm, return without writing
                    }
                }
                // Proceed with writing to the file
                writeToFile(file, board.toString());
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Unable to save to file");
                    alert.setHeaderText("Unsaved changes detected!");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }
            }
        });
        
        addMenuItem(fileMenu, "Print Board", () -> {
            // Debugging method that just prints the board
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Board");
            alert.setHeaderText(null);
            alert.setContentText(board.toString());
            alert.showAndWait();
        });
        // add a separator to the fileMenu
        fileMenu.getItems().add(new SeparatorMenuItem());

        addMenuItem(fileMenu, "Exit", () -> {
            System.out.println("Exit");
            primaryStage.close();
        });

        menuBar.getMenus().add(fileMenu);

        //
        // Edit
        //
        Menu editMenu = new Menu("Edit");

        addMenuItem(editMenu, "Undo", () -> {
            System.out.println("Undo");
            board.undoMove();
            updateBoard();
        });

        addMenuItem(editMenu, "Show values entered", () -> {
            System.out.println("Show all the values we've entered since we loaded the board");
            showEnteredValues();
        });


        menuBar.getMenus().add(editMenu);

        //
        // Hint Menu
        //
        Menu hintMenu = new Menu("Hints");

        addMenuItem(hintMenu, "Show hint", () -> {
            System.out.println("Show hint");
            highlightSingleValueCells();
        });

        menuBar.getMenus().add(hintMenu);


        //
        // Timer Menu
        //
        Menu timerMenu = new Menu("Timer");

        addMenuItem(timerMenu, "Start Timer", this::startTimer);
        addMenuItem(timerMenu, "Stop Timer", this::stopTimer);

        menuBar.getMenus().add(timerMenu);

         // Add the Solve Puzzle menu item
         Menu solveMenu = new Menu("Solve");
         addMenuItem(solveMenu, "Solve Puzzle", this::solvePuzzle);
         menuBar.getMenus().add(solveMenu);


        return menuBar;
    }

    // Implement solvePuzzle method in Sudoku class
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void solvePuzzle() {
        SudokuSolver solver = new SudokuSolver();
        if (solver.solveSudoku(board)) {
            updateBoard();
            showAlert("Puzzle Solved", "The puzzle was successfully solved!");
        } else {
            showAlert("Unsolvable Puzzle", "The puzzle cannot be solved.");
        }
    }


//methods for hints
    private void highlightSingleValueCells() {
        boolean foundHint = false;
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board.getCell(row, col) == 0) { // Only check empty cells
                    Set<Integer> possibleValues = board.getPossibleValues(row, col);
                    if (possibleValues.size() == 1) {
                        TextField textField = textFields[row][col];
                        textField.getStyleClass().add("hint-highlight");
                        foundHint = true;
                    }
                }
            }
        }
        if (!foundHint) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Hints");
            alert.setHeaderText(null);
            alert.setContentText("No hints available.");
            alert.showAndWait();
        }
    }


//method for values entered since the start of the puzzle
    private void showEnteredValues() {
        List<Move> enteredValues = board.getEnteredValues();
        StringBuilder message = new StringBuilder("Entered Values:\n");
        for (Move move : enteredValues) {
            message.append("Row: ").append(move.getRow())
                   .append(", Column: ").append(move.getCol())
                   .append(", Entered Values: ").append(board.getCell(move.getRow(), move.getCol()))
                   .append("\n");
        }
        // Show entered values in a popup window
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Entered Values");
        alert.setHeaderText(null);
        alert.setContentText(message.toString());
        alert.showAndWait();
    }

    private static void writeToFile(File file, String content) throws IOException
    {
        Files.write(file.toPath(), content.getBytes());
    }

    private void addMenuItem(Menu menu, String name, Runnable action)
    {
        MenuItem menuItem = new MenuItem(name);
        menuItem.setOnAction(event -> action.run());
        menu.getItems().add(menuItem);
    }


    private void startTimer() {
        if (timer != null) {
            timer.cancel();
        }
        startTime = System.currentTimeMillis();
        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                long totalElapsedTime = elapsedTime + (currentTime - startTime) / 1000;
                Platform.runLater(() -> timerLabel.setText("Time: " + totalElapsedTime + " seconds"));
            }
        }, 0, 1000);
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            long currentTime = System.currentTimeMillis();
            elapsedTime += (currentTime - startTime) / 1000;
        }
    }
       
    public static void main(String[] args) 
    {
        launch(args);
    }
}
