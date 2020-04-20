public class Battleship {
    private Board[] boards;
    private int currentPlayer = 0;

    public Battleship(String[] command) {
        String playerName = command[1].split("!~")[0];
        String otherPlayerName = command[2].split(" ")[3];
        boards = new Board[]{new Board(playerName), new Board(otherPlayerName)};

        // Send welcome messages!
        IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :Welcome to Battleship!");
        IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :Player 1 -> " + playerName);
        IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :Player 2 -> " + otherPlayerName);

        startGame();
    }

    private void startGame() {
        // Send message to the other player.
        IrcMain.write("PRIVMSG ", boards[currentPlayer == 0 ? 1 : 0].getName() + " :");
        IrcMain.write("PRIVMSG ", boards[currentPlayer == 0 ? 1 : 0].getName() + " :");
        IrcMain.write("PRIVMSG ", boards[currentPlayer == 0 ? 1 : 0].getName() + " :Player " + (currentPlayer+1) + "'s turn!");
        IrcMain.write("PRIVMSG ", boards[currentPlayer == 0 ? 1 : 0].getName() + " :Wait for the other player to finish their turn...");

        // Send message to current player.
        IrcMain.write("PRIVMSG ", boards[currentPlayer].getName() + " :");
        IrcMain.write("PRIVMSG ", boards[currentPlayer].getName() + " :");
        IrcMain.write("PRIVMSG ", boards[currentPlayer].getName() + " :Your turn!");
        printBoard();
        IrcMain.write("PRIVMSG ", boards[currentPlayer].getName() + " :Enter the command 'attack <Row> <Col>' to attack a specific coordinate!");
        IrcMain.write("PRIVMSG ", boards[currentPlayer].getName() + " :Or, enter the command 'stop' to stop the game.");

        while(IrcMain.in.hasNext()) {
            String message = IrcMain.in.nextLine().toLowerCase();
            String[] commandArr = message.split(":");
            String[] messageArr = commandArr[2].split(" ");

            if (messageArr.length > 1) {
                // When the current player has entered their attack command...
                if (messageArr[0].equals("attack") && commandArr[1].split("!~")[0].equals(boards[currentPlayer].getName())) {
                    // Check if they hit one of the other player's ships.
                    boolean didHit = boards[currentPlayer == 0 ? 1 : 0].hit(Integer.parseInt(messageArr[1]), Integer.parseInt(messageArr[2]));
                    // Record that hit attempt on the current player's view of the other player's board.
                    boards[currentPlayer].recordHitAttempt(Integer.parseInt(messageArr[1]), Integer.parseInt(messageArr[2]), didHit);

                    if (didHit) {
                        IrcMain.write("PRIVMSG ", boards[currentPlayer].getName() + " :You hit the player's ship!");
                        IrcMain.write("PRIVMSG ", boards[currentPlayer == 0 ? 1 : 0].getName() + " :One of your ships has been hit!");

                        // If the current player has hit all of the other player's ships, the current player wins!
                        if (boards[currentPlayer].getHits() == 17) {
                            IrcMain.write("PRIVMSG ", boards[currentPlayer == 0 ? 1 : 0].getName() + " :Player " + (currentPlayer+1) + " has destroyed all your ships!");
                            IrcMain.write("PRIVMSG ", boards[currentPlayer == 0 ? 1 : 0].getName() + " :You Lose!");

                            IrcMain.write("PRIVMSG ", boards[currentPlayer].getName() + " :You have destroyed all of Player " + (currentPlayer == 0 ? 2 : 1) + "'s ships!");
                            IrcMain.write("PRIVMSG ", boards[currentPlayer].getName() + " :You Win!");

                            IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :Player " + (currentPlayer+1) + " has destroyed all of player " + (currentPlayer == 0 ? 2 : 1) + "'s ships!");
                            IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :Player " + (currentPlayer+1) + " wins!");

                            return;
                        }
                    } else {
                        IrcMain.write("PRIVMSG ", boards[currentPlayer].getName() + " :You missed!");
                        IrcMain.write("PRIVMSG ", boards[currentPlayer == 0 ? 1 : 0].getName() + " :The other player missed!");
                    }

                    // Swap current players and start new round.
                    if (boards[currentPlayer].getHits() != 17) {
                        currentPlayer = currentPlayer == 0 ? 1 : 0;
                        startGame();
                        return;
                    }
                }
            } else if (messageArr.length == 1 && messageArr[0].equals("stop") && commandArr[1].split("!~")[0].equals(boards[currentPlayer].getName())) {
                IrcMain.write("PRIVMSG ", boards[currentPlayer].getName() + " :Game has been stopped!");
                IrcMain.write("PRIVMSG ", boards[currentPlayer == 0 ? 1 : 0].getName() + " :The other player has stopped the match!");
                IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :Player " + (currentPlayer+1) + " has stopped the match! Game Over.");
                return;
            }
        }
    }

    // Display the current players board along with the current player's view of the other player's board.
    private void printBoard() {
        IrcMain.write("PRIVMSG ", boards[currentPlayer].getName() + " :-------Your Board-------");
        IrcMain.write("PRIVMSG ", boards[currentPlayer].getName() + " :  1 2 3 4 5 6 7 8");
        boards[currentPlayer].printBoard();
        IrcMain.write("PRIVMSG ", boards[currentPlayer].getName() + " :-----------------");

        IrcMain.write("PRIVMSG ", boards[currentPlayer].getName() + " :-------Other Player's Board-------");
        IrcMain.write("PRIVMSG ", boards[currentPlayer].getName() + " :  1 2 3 4 5 6 7 8");
        boards[currentPlayer].printOtherBoard();
        IrcMain.write("PRIVMSG ", boards[currentPlayer].getName() + " :-----------------");
    }
}
