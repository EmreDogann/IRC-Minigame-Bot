import java.util.Random;

// An instance of the player's board state.
public class Board {
    private String[][] grid = new String[8][8];
    private String[][] gridChecked = new String[8][8];
    private String[][] guessedBoard = new String[8][8];
    private String playerName;
    private int hits = 0;

    public Board(String playerName) {
        this.playerName = playerName;

        // Initialize both player's boards with the default board value.
        for (int i = 0; i < 8; i++) {
            for (int x = 0; x < 8; x++) {
                grid[i][x] = "-";
                gridChecked[i][x] = "U";
                guessedBoard[i][x] = "?";
            }
        }

        // Randomly generate all ships for your board.
        generateRandomShips(5);
        generateRandomShips(4);
        generateRandomShips(3);
        generateRandomShips(3);
        generateRandomShips(2);
    }

    public String getName() {
        return playerName;
    }

    public int getHits() {
        return hits;
    }

    public void generateRandomShips(int size) {
        Random rand = new Random();
        boolean placed = false;

        while (!placed) {
            // Generate random start points for the ship on the x and y axis.
            int row = rand.nextInt(8);
            int col = rand.nextInt(8);

            // First identify if we have already checked this position. We don't want to re-check positions we have already checked in the past.
            if (gridChecked[row][col].equals("U")) {
                gridChecked[row][col] = "C";

                // If this spot is free...
                if (grid[row][col].equals("-")) {
                    // Generate a random integer between 0 (inclusive) and 2 (exclusive). This determines the direction to place the ship.
                    // 0 -> Vertical.
                    // 1 -> Horizontal.
                    int dir = rand.nextInt(2);
                    boolean canFit = true;
                    if (dir == 0) {
                        if (row - (size - 1) >= 0) { // Place up if it can fit.
                            // Check if all of the positions that are to be filled in by the new ship are free.
                            for (int i = row; i >= row - (size - 1) && canFit; i--) {
                                canFit = grid[i][col].equals("-");
                            }

                            // If the new positions are free, place the ship and mark it as placed.
                            if (canFit) {
                                placed = true;
                                for (int i = row; i >= row - (size - 1); i--) {
                                    grid[i][col] = "S";
                                }
                            }
                        } else if (row + (size - 1) < 8) { // Place down if it can fit.
                            for (int i = row; i <= row + (size - 1) && canFit; i++) {
                                canFit = grid[i][col].equals("-");
                            }

                            if (canFit) {
                                placed = true;
                                for (int i = row; i <= row + (size - 1); i++) {
                                    grid[i][col] = "S";
                                }
                            }
                        }
                    } else {
                        if (col - (size - 1) >= 0) { // Place left if it can fit.
                            for (int i = col; i >= col - (size - 1) && canFit; i--) {
                                canFit = grid[row][i].equals("-");
                            }

                            if (canFit) {
                                placed = true;
                                for (int i = col; i >= col - (size - 1); i--) {
                                    grid[row][i] = "S";
                                }
                            }
                        } else if (col + (size - 1) < 8) { // Place right if it can fit.
                            for (int i = col; i <= col + (size - 1) && canFit; i++) {
                                canFit = grid[row][i].equals("-");
                            }

                            if (canFit) {
                                placed = true;
                                for (int i = col; i <= col + (size - 1); i++) {
                                    grid[row][i] = "S";
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean placeShip(int size) {
        Random rand = new Random();
        boolean placed = false;

        // Generate random start points for the ship on the x and y axis.
        int row = rand.nextInt(8);
        int col = rand.nextInt(8);

        // First identify if we have already checked this position. We don't want to re-check positions we have already checked in the past.
        if (gridChecked[row][col].equals("U")) {
            gridChecked[row][col] = "C";

            // If this spot is free...
            if (grid[row][col].equals("-")) {
                // Generate a random integer between 0 (inclusive) and 2 (exclusive). This determines the direction to place the ship.
                // 0 -> Vertical.
                // 1 -> Horizontal.
                int dir = rand.nextInt(2);
                boolean canFit = true;
                if (dir == 0) {
                    if (row - (size - 1) >= 0) { // Place up if it can fit.
                        // Check if all of the positions that are to be filled in by the new ship are free.
                        for (int i = row; i >= row - (size - 1) && canFit; i--) {
                            canFit = grid[i][col].equals("-");
                        }

                        // If the new positions are free, place the ship and mark it as placed.
                        if (canFit) {
                            placed = true;
                            for (int i = row; i >= row - (size - 1); i--) {
                                grid[i][col] = "S";
                            }
                        }
                    } else if (row + (size - 1) < 8) { // Place down if it can fit.
                        for (int i = row; i <= row + (size - 1) && canFit; i++) {
                            canFit = grid[i][col].equals("-");
                        }

                        if (canFit) {
                            placed = true;
                            for (int i = row; i <= row + (size - 1); i++) {
                                grid[i][col] = "S";
                            }
                        }
                    }
                } else {
                    if (col - (size - 1) >= 0) { // Place left if it can fit.
                        for (int i = col; i >= col - (size - 1) && canFit; i--) {
                            canFit = grid[row][i].equals("-");
                        }

                        if (canFit) {
                            placed = true;
                            for (int i = col; i >= col - (size - 1); i--) {
                                grid[row][i] = "S";
                            }
                        }
                    } else if (col + (size - 1) < 8) { // Place right if it can fit.
                        for (int i = col; i <= col + (size - 1) && canFit; i++) {
                            canFit = grid[row][i].equals("-");
                        }

                        if (canFit) {
                            placed = true;
                            for (int i = col; i <= col + (size - 1); i++) {
                                grid[row][i] = "S";
                            }
                        }
                    }
                }
            }
        }

        return placed;
    }

    // Check if the other player hit a shit on your board.
    public boolean hit(int row, int col) {
        row--;
        col--;
        if (grid[row][col].equals("S")) {
            grid[row][col] = "X";
            return true;
        } else {
            grid[row][col] = "/";
            return false;
        }
    }

    // Record your guess on your view of the other player's board.
    public void recordHitAttempt(int row, int col, boolean didHit) {
        row--;
        col--;
        if (didHit) {
            guessedBoard[row][col] = "X";
            hits++;
        } else {
            guessedBoard[row][col] = "/";
        }
    }

    // Display your board.
    public void printBoard() {
        for (int i = 0; i < 8; i++) {
            StringBuilder row = new StringBuilder();
            row.append(i+1).append(" ");
            for (int x = 0; x < 7; x++) {
                row.append(grid[i][x]).append(" ");
            }
            row.append(grid[i][7]);
            IrcMain.write("PRIVMSG ", playerName + " :" + row);
        }
    }

    // Display your view of the other player's board.
    public void printOtherBoard() {
        for (int i = 0; i < 8; i++) {
            StringBuilder row = new StringBuilder();
            row.append(i+1).append(" ");
            for (int x = 0; x < 7; x++) {
                row.append(guessedBoard[i][x]).append(" ");
            }
            row.append(guessedBoard[i][7]);
            IrcMain.write("PRIVMSG ", playerName + " :" + row);
        }
    }
}
