import java.util.ArrayList;
import java.util.Random;

// Class for the game called 21, a variant of blackjack.
public class BlackjackTwentyOne {
    private ArrayList<Integer> deck = new ArrayList<>();
    private BlackjackPlayer[] players = new BlackjackPlayer[2];
    private int currentPlayer = 0;

    public BlackjackTwentyOne(String[] command) {
        players[0] = new BlackjackPlayer(command[1].split("!~")[0]);
        players[1] = new BlackjackPlayer(command[2].split(" ")[3]);

        // Initialize the game deck.
        for (int i = 1; i < 12; i++) {
            deck.add(i);
        }

        // Assign 2 random cards from the newly initialised game deck to each player. Remove these 4 assigned cards from the game deck.
        Random rand = new Random();
        for (int i = 0; i < 4; i++) {
            int index = rand.nextInt(deck.size());
            players[i%2].addToHand(deck.get(index));
            deck.remove(index);
        }

        // Send introductory howto messages!
        IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :Welcome to 21! A variation of Blackjack!");
        IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :Player 1 -> " + players[0].getName());
        IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :Player 2 -> " + players[1].getName());
        IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :Each player starts off with 2 cards in their hand.");
        IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :The first card in the player's hand (i.e. the mystery card) is hidden from the opponent, however the opponent can see every other card in your hand.");
        IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :Both players will now be privately messaged their mystery card, do not reveal this card to your opponent!");
        IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :");
        IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :The cards are numbered 1-11.");
        IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :There are no doubles in the deck.");
        IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :Both players share the same deck.");
        IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :");
        IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :Let the game begin!");

        // Privately message each player their mystery card.
        IrcMain.write("PRIVMSG ", players[0].getName() + " :Your mystery card is " + players[0].getHand().get(0));
        IrcMain.write("PRIVMSG ", players[1].getName() + " :Your mystery card is " + players[1].getHand().get(0));

        startGame();
    }

    private void startGame() {
        // If both players don't stay for their turns, reset their state.
        players[currentPlayer].setStay(false);

        IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :");
        IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :");

        printHands();

        IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :Player " + (currentPlayer+1) + "'s turn!");
        IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :Enter the command 'hit' or 'stay'.");
        IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :Or, enter the command 'stop' to stop the game.");

        while(IrcMain.in.hasNext()) {
            String message = IrcMain.in.nextLine().toLowerCase();
            String[] commandArr = message.split(":");
            String[] messageArr = commandArr[2].split(" ");

            if (messageArr.length == 1) {
                // If the current player chooses to stay...
                if (messageArr[0].equals("stay") && commandArr[1].split("!~")[0].equals(players[currentPlayer].getName()) && !players[currentPlayer].isStay()) {
                    players[currentPlayer].setStay(true);
                    // If the other player has already chosen to stay on their turn, check who the winner is and end the game.
                    if (players[currentPlayer == 0 ? 1 : 0].isStay()) {
                        getWinner();
                    } else {
                        // Swap turns.
                        currentPlayer = currentPlayer == 0 ? 1 : 0;
                        startGame();
                    }
                    return;
                } else if (messageArr[0].equals("hit") && commandArr[1].split("!~")[0].equals(players[currentPlayer].getName()) && deck.size() != 0) {
                    // If player chooses to hit, draw a random card from the game deck, assign it to the player's hand, then remove that card from the game deck.
                    Random rand = new Random();
                    int index = rand.nextInt(deck.size());
                    players[currentPlayer].addToHand(deck.get(index));
                    deck.remove(index);

                    // If the game deck is empty, check for a winner.
                    if (deck.size() == 0) {
                        getWinner();
                    } else {
                        // Swap turns.
                        currentPlayer = currentPlayer == 0 ? 1 : 0;
                        startGame();
                    }
                    return;
                } else if (messageArr[0].equals("stop") && commandArr[1].split("!~")[0].equals(players[currentPlayer].getName())) {
                    // If the player chooses to stop the game...
                    IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :Game has been stopped!");
                    IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :Player " + (currentPlayer+1) + " has stopped the match! Game Over.");
                    return;
                }
            }
        }
    }

    // Print who the winner is.
    private void getWinner() {
        int winner = checkWinner();

        if (winner == -1) {
            IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :It's a draw!");
        } else if (winner == 1) {
            IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :Player 1 wins!");
        } else {
            IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :Player 2 wins!");
        }
    }

    // Check who the winner is. The player with the hand closest to 21 wins.
    private int checkWinner() {
        int[] sums = new int[2];
        IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :Checking Winner...");

        // Display final state of the game.
        for (int i = 0; i < 2; i++) {
            IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :-------Player " + (i+1) + "'s Hand-------");
            StringBuilder row = new StringBuilder();
            ArrayList<Integer> hand = players[i].getHand();
            int sum = 0;

            for (int x = 0; x < hand.size()-1; x++) {
                sum += hand.get(x);
                row.append(hand.get(x)).append(" | ");
            }
            row.append(hand.get(hand.size()-1));
            sum += hand.get(hand.size()-1);
            sums[i] = sum;

            IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :" + row);
            IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :Total Value: " + sum);
            IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :");
        }

        // If both players went bust, the player closest to 21 wins.
        if (sums[0] > 21 && sums[1] > 21) {
            int player1Diff = sums[0] - 21;
            int player2Diff = sums[1] - 21;

            if (player1Diff < player2Diff) {
                return 1;
            } else {
                return 2;
            }
        } else if (sums[0] > 21) { // If player 1 went bust, player 2 wins.
            return 2;
        } else if (sums[1] > 21) { // If player 2 went bust, player 1 wins.
            return 1;
        } else if (sums[0] > sums[1]) { // If player 1 is closer to 21 than player 2, player 1 wins.
            return 1;
        } else if (sums[1] > sums[0]) { // If player 2 is closer to 21 than player 1, player 2 wins.
            return 2;
        }

        return -1;
    }

    // Display both player's hands.
    private void printHands() {
        for (int i = 0; i < 2; i++) {
            IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :-------Player " + (i+1) + "'s Hand-------");
            StringBuilder row = new StringBuilder();
            ArrayList<Integer> hand = players[i].getHand();
            row.append("? | ");
            int sum = 0;

            for (int x = 1; x < hand.size()-1; x++) {
                sum += hand.get(x);
                row.append(hand.get(x)).append(" | ");
            }

            row.append(hand.get(hand.size()-1));
            sum += hand.get(hand.size()-1);
            IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :" + row);
            IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :Total Value: ? + " + sum + " / 21");
            IrcMain.write("PRIVMSG ", "#" + IrcMain.channel + " :");
        }
    }
}
