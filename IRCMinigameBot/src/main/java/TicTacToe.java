public class TicTacToe {
    private String[][] board = new String[3][3];
    private String[] playerNames = new String[2];
    private int currentPlayer = 0;

    public TicTacToe(String[] command) {
        playerNames[0] = command[1].split("!~")[0];
        playerNames[1] = command[2].split(" ")[3];

        // Initialize both player's boards with the default board value.
        for (int i = 0; i < 3; i++) {
            for (int x = 0; x < 3; x++) {
                board[i][x] = "-";
            }
        }

        // Send welcome messages!
        IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :Welcome to Naughts and Crosses/TicTacToe!");
        IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :Player 1 (O) -> " + playerNames[0]);
        IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :Player 2 (X) -> " + playerNames[1]);

        startGame();
    }

    private void startGame() {
        // Send message to the other player.
        IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :");
        IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :");
        IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :Player " + (currentPlayer+1) + "'s turn!");

        printBoard();
        IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :Enter the command 'place <Row> <Col>' to place a coin at a specific column.");
        IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :Or, enter the command 'stop' to stop the game.");

        while(IrcMain.in.hasNext()) {
            String message = IrcMain.in.nextLine().toLowerCase();
            String[] commandArr = message.split(":");
            String[] messageArr = commandArr[2].split(" ");
            boolean winner = false;

            if (messageArr.length > 2) {
                // When the current player has entered their attack command...
                if (messageArr[0].equals("place") && commandArr[1].split("!~")[0].equals(playerNames[currentPlayer])) {
                    // Check if they hit one of the other player's ships.
                    boolean canPut = checkPlace(Integer.parseInt(messageArr[1]), Integer.parseInt(messageArr[2]));

                    if (canPut) {
                        winner = checkWinner();
                        if (winner) {
                            IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :");
                            IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :");
                            printBoard();
                            IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :Player " + (currentPlayer+1) + " wins!");
                        } else {
                            currentPlayer = currentPlayer == 0 ? 1 : 0;
                            startGame();
                        }
                    } else {
                        IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :Invalid position! Try Again!");
                        startGame();
                    }
                    return;
                }
            } else if (messageArr.length == 1 && messageArr[0].equals("stop") && commandArr[1].split("!~")[0].equals(playerNames[currentPlayer])) {
                IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :Game has been stopped!");
                IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :Player " + (currentPlayer+1) + " has stopped the match! Game Over.");
                return;
            }
        }
    }

    private boolean checkPlace(int row, int col) {
        row--;
        col--;
        if (board[row][col].equals("-")) {
            board[row][col] = currentPlayer == 0 ? "O" : "X";
            return true;
        }
        return false;
    }

    private boolean checkWinner() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (!board[row][col].equals("-")) {
                    String symbol = board[row][col];

                    // Check right.
                    if (col + 2 < 3 && symbol.equals(board[row][col+1]) && symbol.equals(board[row][col+2])) {
                        return true;
                    }

                    if (row + 2 < 3) {
                        // Check up.
                        if (symbol.equals(board[row+1][col]) && symbol.equals(board[row+2][col])) {
                            return true;
                        }

                        // Check up and right.
                        else if (col == 0 && symbol.equals(board[row+1][col+1]) && symbol.equals(board[row+2][col+2])) {
                            return true;
                        }

                        // Check up and left.
                        else if (col == 2 && symbol.equals(board[row+1][col-1]) && symbol.equals(board[row+2][col-2])) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    // Display the current players board along with the current player's view of the other player's board.
    private void printBoard() {
        IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :-------Board-------");
        IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :   1   2   3");

        for (int i = 0; i < 3; i++) {
            StringBuilder row = new StringBuilder();
            row.append(i+1).append("  ");
            for (int x = 0; x < 2; x++) {
                row.append(board[i][x]).append(" | ");
            }
            row.append(board[i][2]);
            IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :" + row);
            IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :  -----------");
        }
    }
}
