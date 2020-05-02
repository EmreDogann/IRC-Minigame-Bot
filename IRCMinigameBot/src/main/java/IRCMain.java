import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

class IrcMain {
    private static String ip;
    private static int port;
    private static String serverName = "selsey.nsqdc.city.ac.uk";
    public static String channel;
    private static PrintWriter out;
    private static Socket socket;
    static Scanner in;

    public static void main(String [] args) throws IOException {
        // Ask user for IP, Port, and Channel Name.
        Scanner console = new Scanner(System.in);
        System.out.println("Enter IP Address: ");
        ip = console.nextLine();
        System.out.println("Enter Port: ");
        port = Integer.parseInt(console.nextLine());
        System.out.println("Enter Channel Name: ");
        channel = console.nextLine();

        Socket socket = new Socket(ip, port);

        out = new PrintWriter(socket.getOutputStream(), true);
        in = new Scanner(socket.getInputStream());

        write("NICK", "EmreBot");
        write("USER", "emreBot 8 * :emre's bot v0.1" );
        write("JOIN", "#" + channel);

        // Parse through initial server messages.
        while(in.hasNext()) {
            String serverMessage = in.nextLine().toLowerCase();
            String[] messageArr = serverMessage.split(" ");
            System.out.println("<<< " + serverMessage);

            // When final server message has been sent, continue to next while loop.
            if (messageArr[1].equals("366")) {
                serverName = messageArr[0];
                break;
            }
        }

        write("NOTICE ", "Enter 'emrebot help' to see the list of commands.");

        // While loop which listens for defined commands.
        while (in.hasNext()) {
            String serverMessage = in.nextLine();
            String[] serverMessageArr = serverMessage.toLowerCase().split(":");
            System.out.println("<<< " + serverMessage);

            // Respond to server ping.
            if (serverMessageArr[0].equals("ping ")) {
                write("PONG", serverName);
            } else if (serverMessageArr.length > 2 && serverMessageArr[2].length() > 6 && serverMessageArr[2].substring(0, 7).equals("emrebot")) {
                String[] command = serverMessageArr[2].split(" ");

                if (command.length > 1) {
                    switch (command[1]) {
                        // If command entered is battleship, create instance of battleship class and start the game.
                        case "battleship":
                            if (command.length == 4) {
                                if (command[2].equals("start")) {
                                     new Battleship(serverMessageArr);
                                }
                            } else if (command.length == 3) {
                                // Print out the rules of the game.
                                if (command[2].equals("howto")) {
                                    write("PRIVMSG ", "#" + channel + " :In Battleship there are 2 players, each with 5 ships on a 8x8 size grid.");
                                    write("PRIVMSG ", "#" + channel + " :Each ship is a different size ranging from 5 units long down to 2 units.");
                                    write("PRIVMSG ", "#" + channel + " :A ship can be placed horizontally or vertically.");
                                    write("PRIVMSG ", "#" + channel + " :Each player takes turns in guessing where the other player's ships are byt entering coordinates.");
                                    write("PRIVMSG ", "#" + channel + " :The first player to destroy all parts of the pother player's grid which has a ship on it wins!");
                                }
                            } else {
                                write("PRIVMSG ", "#" + channel + " :Invalid Command.");
                            }
                            break;
                        // If command entered is connect4, create instance of connect4 class and start the game.
                        case "connect4":
                            if (command.length == 4) {
                                if (command[2].equals("start")) {
                                    new ConnectFour(serverMessageArr);
                                }
                            } else {
                                write("PRIVMSG ", "#" + channel + " :Invalid Command.");
                            }
                            break;
                        // If command entered is tictactoe, create instance of tictactoe class and start the game.
                        case "tictactoe":
                            if (command.length == 4) {
                                if (command[2].equals("start")) {
                                    new TicTacToe(serverMessageArr);
                                }
                            } else {
                                write("PRIVMSG ", "#" + channel + " :Invalid Command.");
                            }
                            break;
                        // If command entered is blackjack21, create instance of blackjack21 class and start the game.
                        case "blackjack21":
                            if (command.length == 4) {
                                if (command[2].equals("start")) {
                                    new BlackjackTwentyOne(serverMessageArr);
                                }
                            } else {
                                write("PRIVMSG ", "#" + channel + " :Invalid Command.");
                            }
                            break;
                        // Kick user with the specified username.
                        case "exile":
                            if (command.length == 3) {
                                write("KICK", "#" + channel + " " + command[2] + " :" + command[2] + " has been kicked!");
                            }
                            break;
                        // Display the server's MOTD.
                        case "motd":
                            if (command.length == 2) {
                                write("MOTD", "");
                                while (in.hasNext()) {
                                    String mes = in.nextLine();
                                    String[] mesArr = mes.split(":");

                                    if (mesArr[1].split(" ")[1].equals("376")) {
                                        break;
                                    } else {
                                        write("PRIVMSG ", "#" + channel + " :" + mesArr[2]);
                                    }
                                }
                            }
                            break;
                        // Bot leaves server.
                        case "quit":
                            write("QUIT", "");
                            break;
                        // Display the current time as given by the server.
                        case "time":
                            write("TIME", "");
                            while (in.hasNext()) {
                                String mes = in.nextLine();
                                System.out.println("<<< " + mes);
                                String[] mesArr = mes.split(":");

                                if (mesArr[1].split(" ")[1].equals("391")) {
                                    write("PRIVMSG ", "#" + channel + " :" + mesArr[2] + ":" + mesArr[3]);
                                    break;
                                }
                            }
                            break;
                        // Ping the current server.
                        case "ping":
                            write("PING ", serverName);
                            break;
                        // Display list of commands.
                        case "help":
                            write("NOTICE ", "#" + channel + " :battleship start <Opponent Name> - Play a game of battleship against a player of your choosing.");
                            write("NOTICE ", "#" + channel + " :connect4 start <Opponent Name> - Play connect4 with another player.");
                            write("NOTICE ", "#" + channel + " :tictactoe start <Opponent Name> - Play Naughts and Crosses/Tic Tac Toe with another player.");
                            write("NOTICE ", "#" + channel + " :blackjack21 start <Opponent Name> - Play 21, a variant of blackjack, with another player.");
                            write("NOTICE ", "#" + channel + " :exile <Username> - Kick a user from the channel.");
                            write("NOTICE ", "#" + channel + " :motd - Display the message of the day.");
                            write("NOTICE ", "#" + channel + " :quit - Shuts down EmreBot.");
                            write("NOTICE ", "#" + channel + " :time - Display the current time.");
                            write("NOTICE ", "#" + channel + " :ping - Ping the server.");
                            break;
                    }
                }
            }
        }
        in.close();
        out.close();
        socket.close();
    }

    // General write method which takes in an IRC command along with a message and sends it to the server.
    public static void write(String command, String message) {
        String fullMessage =  command + " " + message;
        System.out.println(">>> " + fullMessage);
        out.print(fullMessage + "\r\n");
        out.flush();
    }
}