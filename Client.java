import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;

class Send implements Runnable {
    Scanner sc;
    Socket s;
    String senderName;
    String receiverName;

    public Send(Scanner sc, Socket s, String senderName, String receiverName) {
        this.sc = sc;
        this.s = s;
        this.senderName = senderName;
        this.receiverName = receiverName;
    }

    public void run() {

        while (true) {
            // System.out.print(senderName + " : ");
            // System.out.println("\n-->");
            String str = sc.nextLine(); // Data to be sent to the server
            if (str.equals("")) {
                continue;
            }
            str = "sendTo:" + receiverName + ":" + str;

            try {
                PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                // System.out.println("\nSent to server : " + str);
                out.println(str); // Writing data to the server
            } catch (Exception e) {
                System.out.println("Error generated while sending msg from client to server");
            }
        }
    }
}

class Receive implements Runnable {
    Socket s;
    String senderName;
    String receiverName;

    public Receive(Socket s, String senderName, String receiverName) {
        this.s = s;
        this.senderName = senderName;
        this.receiverName = receiverName;
    }

    public void run() {

        while (true) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                String str = null;
                while ((str = br.readLine()) != null) {
                    System.out.print(str + "\n"); // Print received message
                }
            } catch (Exception e) {
                System.err.println("Error while receiving message from server: " + e.getMessage());
                e.printStackTrace(); // Print stack trace for debugging
            } finally {
                try {
                    s.close(); // Close socket
                } catch (Exception e) {
                    System.err.println("Error while closing socket: " + e.getMessage());
                }
            }
        }
    }
}

public class Client {
    public static void dottedLine() {
        for (int i = 1; i <= 30; i++) {
            System.out.print("--");
        }
    }

    public static void main(String[] args) throws Exception {

        // String ip = "localhost"; // IP address of the server (in this case, localhost)
        String ip = "192.168.108.46"; // IP address of the server (in this case, localhost)
        int port = 8000; // Port number the server is listening on

        // creating socket for client
        Socket s = new Socket(ip, port);
        PrintWriter out;
        BufferedReader br;

        out = new PrintWriter(s.getOutputStream(), true); //
        br = new BufferedReader(new InputStreamReader(s.getInputStream()));

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.print("\033[H\033[2J");
            System.out.flush();
            dottedLine();
            System.out.println("\nWelcome to Chat Application");
            System.out.println("\nChoose (1/2)");
            System.out.println("1. Register (Sign-Up)");
            System.out.println("2. Login");

            System.out.print("\nChoice : ");
            int choice = sc.nextInt();
            dottedLine();

            switch (choice) {
                case 1:
                    // sign-up section
                    // Registeration u = new Registeration(url, username, password, con, sc);

                    String u_name;
                    String pass;
                    while (true) {
                        System.out.print("\033[H\033[2J");
                        System.out.flush();
                        dottedLine();
                        System.out.print("\nSign-up Form\n");
                        dottedLine();
                        System.out.print("\n");
                        sc.nextLine();
                        System.out.print("Enter Username : ");
                        u_name = sc.next().trim();

                        out = new PrintWriter(s.getOutputStream(), true); // autoFlush : true
                        out.println("isUserNameTaken:" + u_name); // sending to server

                        br = new BufferedReader(new InputStreamReader(s.getInputStream()));

                        int isUserNameTaken = Integer.parseInt(br.readLine());

                        if (isUserNameTaken == 1) {
                            System.out.println("\nUsername already Taken.");
                            try {
                                Thread.sleep(3000);
                            } catch (Exception e) {
                            }
                            continue;
                        }

                        System.out.print("Enter Password : ");
                        pass = sc.next();
                        System.out.print("Confirm Password : ");
                        String C_pass = sc.next();

                        if (!pass.equals(C_pass)) {
                            System.out.println("Passwords do not match. Please try again.");
                            try {
                                Thread.sleep(3000);
                            } catch (Exception e) {
                            }
                            continue;
                        }
                        break;
                    }

                    out = new PrintWriter(s.getOutputStream(), true); // autoFlush : true
                    out.println("signup:" + u_name + ":" + pass); // sending to server
                    System.out.println("Sign-up Completed ...");
                    System.out.println("Account Created Successfully.\nPlease Login");

                    String removeFromClientHandler = u_name;
                    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                        System.out.print("\033[H\033[2J");
                        System.out.flush();
                        try {
                            final PrintWriter out2 = new PrintWriter(s.getOutputStream(), true); // autoFlush : true
                            out2.println("removeClient:" + removeFromClientHandler); // sending to server

                            System.out.println("\nRemoved from clientHandler Successfully.");
                        } catch (Exception e) {
                            System.err.println("Error setting login status: " + e.getMessage());
                        }

                    }));

                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                    }
                    break;

                case 2:

                    String login_u_name;
                    String login_pass;
                    int loginStatus = 0;

                    while (true) {
                        System.out.print("\033[H\033[2J");
                        System.out.flush();
                        dottedLine();
                        System.out.println("\nLogin to Chat-Application");
                        dottedLine();
                        System.out.print("\n");
                        sc.nextLine();
                        System.out.print("Enter Username : ");
                        login_u_name = sc.next().trim();

                        System.out.print("Enter Password : ");
                        login_pass = sc.next();

                        out = new PrintWriter(s.getOutputStream(), true); // autoFlush : true
                        out.println("login:" + login_u_name + ":" + login_pass); // sending to server

                        br = new BufferedReader(new InputStreamReader(s.getInputStream()));

                        int isLoginComplete = Integer.parseInt(br.readLine());

                        if (isLoginComplete == 1) {

                            // login successful
                            System.out.println("Login Successfully");
                            loginStatus = 1;
                            try {
                                Thread.sleep(3000);
                            } catch (Exception e) {
                            }
                            break;
                        } else {

                            // login failed
                            System.out.println("Login Failed\nTry Again....");
                            try {
                                Thread.sleep(3000);
                            } catch (Exception e) {
                            }
                            // try again
                            continue;
                        }
                    }

                    if (loginStatus == 1) {
                        // will logout user on abnormal termination of program (ctrl + c)
                        final String userLogout = login_u_name;
                        System.out.println("I am logged in ..");
                        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                            System.out.print("\033[H\033[2J");
                            System.out.flush();
                            try {
                                final PrintWriter out2 = new PrintWriter(s.getOutputStream(), true); // autoFlush : true
                                out2.println("logout:" + userLogout); // sending to server

                                System.out.println("\nLog-Out Successfully.");
                            } catch (Exception e) {
                                System.err.println("Error setting login status: " + e.getMessage());
                            }

                        }));

                        String chatWithUsername = null;
                        while (true) {
                            System.out.print("\033[H\033[2J");
                            System.out.print("\nChat with (Username) : ");
                            chatWithUsername = sc.next().trim();

                            if (chatWithUsername.equals(login_u_name)) {
                                System.out.println("Can't talk to yourself ....");

                                try {
                                    Thread.sleep(2000);
                                } catch (Exception e) {
                                }
                                continue;
                            }

                            out = new PrintWriter(s.getOutputStream(), true); // autoFlush : true
                            out.println("isUserNameTaken:" + chatWithUsername); // sending to server

                            br = new BufferedReader(new InputStreamReader(s.getInputStream()));

                            int doesUserExist = Integer.parseInt(br.readLine());

                            if (doesUserExist == 0) {
                                System.out.println("User Does not Exist !!");
                                Thread.sleep(1000);
                                System.out.print("\033[H\033[2J");
                                System.out.flush();
                                continue;

                            } else {

                                out = new PrintWriter(s.getOutputStream(), true); // autoFlush : true
                                out.println("isUserOnline:" + chatWithUsername); // sending to server

                                br = new BufferedReader(new InputStreamReader(s.getInputStream()));

                                int isUserOnline = Integer.parseInt(br.readLine());

                                if (isUserOnline == 1) {
                                    break;
                                } else {
                                    System.out.println(chatWithUsername + " is offline .");
                                    Thread.sleep(1000);
                                    System.out.println("User Does not Exist !!");
                                    System.out.print("\033[H\033[2J");
                                    continue;
                                }
                            }
                        }

                        System.out.print("\033[H\033[2J");
                        System.out.flush();
                        dottedLine();
                        System.out.println("\n" + chatWithUsername);

                        out = new PrintWriter(s.getOutputStream(), true); // autoFlush : true
                        out.println("createUniqueName:" + login_u_name + ":" + chatWithUsername); // sending to server

                        dottedLine();
                        System.out.println();

                        Send sendThread = new Send(sc, s, login_u_name, chatWithUsername);
                        Thread sendThread1 = new Thread(sendThread);
                        Receive receiveThread = new Receive(s, chatWithUsername, login_u_name);
                        Thread receiveThread1 = new Thread(receiveThread);

                        sendThread1.start(); // to handle sending
                        receiveThread1.start(); // to handle receiving

                        try {
                            sendThread1.join();
                            receiveThread1.join();
                        } catch (Exception e) {
                            System.out.println("\nError occured in Client.java : " + e);
                        }

                        try {
                            Thread.sleep(6000);
                        } catch (Exception e) {
                        }
                    }

                    break;

                default:
                    System.out.println("Invalid Choice");
                    break;
            }

        }
        // sc.close();
    }
}
