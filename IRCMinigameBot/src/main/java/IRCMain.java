import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

class IrcMain {
    private static String nick;
    private static String ip;
    private static int port;
    public static String channel = "TheBois";
    private static String isJoin;
    private static String userName;
    private static String realName;
    private static Battleship battleshipGame = null;
    static ConnectFour connectFourGame = null;
    private static PrintWriter out;
    private static Socket socket;
    static Scanner in;

    public static void main(String [] args) throws IOException {
        Scanner console = new Scanner(System.in);
//        System.out.println("Enter IP Address: ");
//        ip = console.nextLine();
//        System.out.println("Enter Port: ");
//        port = Integer.parseInt(console.nextLine());
//        System.out.println("Enter nickname: ");
//        nick = console.nextLine();
//        System.out.println("Enter username: ");
//        userName = console.nextLine();
//        System.out.println("Enter realname: ");
//        realName = console.nextLine();
//        System.out.println("Would you like to Join a channel or create a channel? Enter 'join' or 'create': ");
//        isJoin = console.nextLine();
//        System.out.println("Enter Channel Name: ");
//        channel = console.nextLine();

//        Socket socket = new Socket(ip, port);
        socket = new Socket("127.0.0.1", 6667);

        out = new PrintWriter(socket.getOutputStream(), true);
        in = new Scanner(socket.getInputStream());

//        write("Nick", nick);
//        write("USER", userName + " 0 * :" + realName);
//        if (isJoin.equals("join")) {
//            write("JOIN", "#" + channel);
//        } else if (isJoin.equals("create")) {
//            write("JOIN", "#" + channel);
//        }

        write("NICK", "EmreBot");
        write("USER", "emreBot 8 * :emre's bot v0.1" );
        write("JOIN", "#" + channel);

        while(in.hasNext()) {
            String serverMessage = in.nextLine();
            System.out.println("<<< " + serverMessage);
            if (serverMessage.equals(":selsey.nsqdc.city.ac.uk 366 EmreBot #" + channel.toLowerCase() + " :End of NAMES list")) {
                break;
            }
        }

        while (in.hasNext()) {
            String serverMessage = in.nextLine();
            System.out.println("<<< " + serverMessage);
            String[] serverMessageArr = serverMessage.toLowerCase().split(":");

            if (serverMessageArr[2].length() > 6 && serverMessageArr[2].substring(0, 7).equals("emrebot")) {
                String[] command = serverMessageArr[2].split(" ");

                if (command.length > 1) {
                    if (command[1].equals("battleship")) {
                        if (command.length == 4) {
                            if (command[2].equals("start")) {
                                battleshipGame = new Battleship(serverMessageArr);
                                battleshipGame = null;
                            }
                        } else if (command.length == 3) {
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

                    } else if (command[1].equals("connect4")) {
                        if (command.length == 4) {
                            if (command[2].equals("start")) {
                                connectFourGame = new ConnectFour(serverMessageArr);
                                connectFourGame = null;
                            }
                        } else {
                            write("PRIVMSG ", "#" + channel + " :Invalid Command.");
                        }
                    }
                }
            }
        }
        in.close();
        out.close();
        socket.close();
    }

    public static void write(String command, String message) {
        String fullMessage =  command + " " + message;
        System.out.println(">>> " + fullMessage);
        out.print(fullMessage + "\r\n");
        out.flush();
    }
}