import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.awt.*;
import javax.swing.*;

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

    public Receive(Socket s) {
        this.s = s;
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

class ReceiveOnGui implements Runnable {
    Socket s;
    JTextArea chatArea;

    public ReceiveOnGui(Socket s, JTextArea chatArea) {
        this.s = s;
        this.chatArea = chatArea;
    }

    public void run() {
        while (true) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                String str = null;
                while ((str = br.readLine()) != null) {
                    chatArea.append(str + "\n");
                }
            } catch (Exception e) {
                System.err.println("Error while receiving message from server: " + e.getMessage());
                e.printStackTrace(); // Print stack trace for debugging
            } finally {
                try {
                    // s.close(); // Close socket
                } catch (Exception e) {
                    // System.err.println("Error while closing socket: " + e.getMessage());
                }
            }
        }
    }
}

public class Client {
    public static void dottedLine() {
        for (int i = 1; i <= 23; i++) {
            System.out.print("--");
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        Scanner sc = new Scanner(System.in);
        dottedLine();
        System.out.println("\nWelcome to Chat Application");
        dottedLine();

        System.out.println("\nWhich feature do you want : ");
        System.out.println("\n1. GUI\n2. Terminal");
        System.out.print("\nChoice : ");
        int guiChoice = sc.nextInt();

        if (guiChoice == 1) {
            IP obj = new IP();
        } else {
            // String ip = "localhost"; // IP address of the server (in this case,
            // localhost)
            // String ip = "192.168.108.46";

            System.out.print("\033[H\033[2J");
            System.out.flush();

            int port = 8000; // Port number the server is listening on

            dottedLine();
            System.out.print("\nEnter IP : ");
            String ip = sc.next();

            // creating socket for client
            // Socket s = new Socket(ip, port);
            // PrintWriter out;
            // BufferedReader br;

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
                        Socket s = new Socket(ip, port);
                        PrintWriter out;
                        BufferedReader br;

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

                        Socket S = new Socket(ip, port);
                        PrintWriter OUT;
                        BufferedReader BR;

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

                            OUT = new PrintWriter(S.getOutputStream(), true); // autoFlush : true
                            OUT.println("login:" + login_u_name + ":" + login_pass); // sending to server

                            BR = new BufferedReader(new InputStreamReader(S.getInputStream()));

                            int isLoginComplete = Integer.parseInt(BR.readLine());

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
                                    final PrintWriter out2 = new PrintWriter(S.getOutputStream(), true); // autoFlush :
                                                                                                         // true
                                    out2.println("logout:" + userLogout); // sending to server
                                    // logout in abnormal termination
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

                                OUT = new PrintWriter(S.getOutputStream(), true); // autoFlush : true
                                OUT.println("isUserNameTaken:" + chatWithUsername); // sending to server

                                BR = new BufferedReader(new InputStreamReader(S.getInputStream()));

                                int doesUserExist = Integer.parseInt(BR.readLine());

                                if (doesUserExist == 0) {
                                    System.out.println("User Does not Exist !!");
                                    Thread.sleep(1000);
                                    System.out.print("\033[H\033[2J");
                                    System.out.flush();
                                    continue;

                                } else {

                                    OUT = new PrintWriter(S.getOutputStream(), true); // autoFlush : true
                                    OUT.println("isUserOnline:" + chatWithUsername); // sending to server

                                    BR = new BufferedReader(new InputStreamReader(S.getInputStream()));

                                    int isUserOnline = Integer.parseInt(BR.readLine());

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

                            OUT = new PrintWriter(S.getOutputStream(), true); // autoFlush : true
                            OUT.println("createUniqueName:" + login_u_name + ":" + chatWithUsername); // sending to
                                                                                                      // server

                            dottedLine();
                            System.out.println();

                            Send sendThread = new Send(sc, S, login_u_name, chatWithUsername);
                            Thread sendThread1 = new Thread(sendThread);
                            Receive receiveThread = new Receive(S);
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

}

// GUI SECTION

class IP extends JFrame {
    JLabel ipAddress;
    JTextField inputIP;
    JButton enterBtn;

    String ip = null;
    Socket s;

    public IP() throws Exception {

        ipAddress = new JLabel("Enter IP : ");
        inputIP = new JTextField(20);
        enterBtn = new JButton("Enter");

        add(ipAddress);
        add(inputIP);
        add(enterBtn);

        enterBtn.addActionListener(ae -> {
            this.ip = inputIP.getText();
            try {
                new INDEX(this.ip);
                dispose();

            } catch (Exception e) {
            }
        });

        this.setLayout(new GridLayout(10, 1));
        this.setTitle("Chat Application");
        this.setVisible(true);
        this.setSize(400, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

class INDEX extends JFrame {

    JButton signUpBtn, loginBtn;
    Socket s;
    String ip = null;

    public INDEX(String ip) throws Exception {

        this.ip = ip;
        signUpBtn = new JButton("Create Account");
        loginBtn = new JButton("Login");

        signUpBtn.setFocusPainted(false);
        loginBtn.setFocusPainted(false);

        add(signUpBtn);
        add(loginBtn);

        // for Sign-Up button
        signUpBtn.addActionListener(ae -> {
            try {
                s = new Socket(ip, 8000);
                new SignUpPage(this.s, ip);
                this.dispose();
            } catch (Exception e) {
            }
        });

        // for Login button
        loginBtn.addActionListener(ae -> {
            try {
                s = new Socket(ip, 8000);
                new LoginPage(this.s);
                this.dispose();
            } catch (Exception e) {
            }
        });

        this.setLayout(new GridLayout(10, 1));
        this.setTitle("Chat Application");
        this.setVisible(true);
        this.setSize(400, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

class SignUpPage extends JFrame {

    JTextField username, pass, cPass;
    JLabel header, usernameLabel, passLabel, cPassLabel, invalidUsername, invalidPassword, successfulSignUp;
    JButton signUpButton;
    int flag = 1;
    PrintWriter out;
    BufferedReader br;
    Socket s;
    String ip = null;

    public SignUpPage(Socket s, String ip) throws Exception {

        this.s = s;
        this.ip = ip;

        header = new JLabel("Sign Up");
        username = new JTextField(40);
        pass = new JTextField(20);
        cPass = new JTextField(20);

        usernameLabel = new JLabel("Username : ");
        passLabel = new JLabel("Password : ");
        cPassLabel = new JLabel("Confirm Password : ");
        invalidUsername = new JLabel("");
        invalidPassword = new JLabel("");
        successfulSignUp = new JLabel("");

        signUpButton = new JButton("Create Account");

        this.add(header);
        this.setLayout(new GridLayout(18, 4)); // Adjust layout as needed
        this.add(usernameLabel);
        this.add(username);
        this.add(passLabel);
        this.add(pass);
        this.add(cPassLabel);
        this.add(cPass);
        this.add(signUpButton);
        this.add(invalidUsername);
        this.add(invalidPassword);
        this.add(successfulSignUp);

        // on clicking button sign-up
        signUpButton.addActionListener(ae -> {
            invalidUsername.setText("");
            invalidPassword.setText("");
            successfulSignUp.setText("");
            flag = 1;

            String u_name = username.getText();
            String password = pass.getText();
            String confirmPassword = cPass.getText();

            try {
                out = new PrintWriter(s.getOutputStream(), true); // autoFlush : true
                out.println("isUserNameTaken:" + u_name); // sending to server

                br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                int isUserNameTaken = Integer.parseInt(br.readLine());

                if (isUserNameTaken == 1) {
                    flag = 0;
                    invalidUsername.setText("Username already Taken.");
                }

                if (!password.equals(confirmPassword)) {
                    flag = 0;
                    invalidPassword.setText("Passwords do not match. Please try again.");
                }

                if (flag == 1) {
                    // signup successfully
                    System.out.println("Entered for signup");

                    out = new PrintWriter(s.getOutputStream(), true); // autoFlush : true
                    out.println("signup:" + u_name + ":" + password); // sending to server

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
                        new INDEX(ip);
                    } catch (Exception e) {
                    }
                    this.dispose();

                } else {

                    successfulSignUp.setText("Try Again....");

                }

            } catch (Exception e) {
                System.out.println("\nError occured in Signup\n");
            }

        });

        this.setVisible(true);
        this.setSize(400, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

class LoginPage extends JFrame {

    JTextField username, pass;
    JLabel header, usernameLabel, passLabel, invalidUsername, invalidPassword, successfullLogin;
    JButton loginButton;
    int flag = 1;

    PrintWriter out;
    BufferedReader br;
    Socket s;
    String login_u_name;

    String login_pass;
    int loginStatus = 0;

    public LoginPage(Socket s) throws Exception {

        this.s = s;

        header = new JLabel("Login to Chat-Application");
        username = new JTextField(40);
        pass = new JTextField(40);

        usernameLabel = new JLabel("Username : ");
        passLabel = new JLabel("Password : ");
        invalidUsername = new JLabel("");
        invalidPassword = new JLabel("");
        successfullLogin = new JLabel("");

        loginButton = new JButton("Login");

        this.add(header);
        this.setLayout(new GridLayout(18, 4)); // Adjust layout as needed
        this.add(usernameLabel);
        this.add(username);
        this.add(passLabel);
        this.add(pass);
        this.add(loginButton);
        this.add(invalidUsername);
        this.add(invalidPassword);
        this.add(successfullLogin);

        // on clicking button sign-up
        loginButton.addActionListener(ae -> {
            invalidUsername.setText("");
            invalidPassword.setText("");
            successfullLogin.setText("");
            loginStatus = 0;

            String login_u_name = username.getText();
            String login_pass = pass.getText();

            try {
                out = new PrintWriter(s.getOutputStream(), true); // autoFlush : true
                out.println("login:" + login_u_name + ":" + login_pass); // sending to server

                br = new BufferedReader(new InputStreamReader(s.getInputStream()));

                int isLoginComplete = Integer.parseInt(br.readLine());

                if (isLoginComplete == 1) {

                    // login successful
                    System.out.println("Login Successfully");
                    loginStatus = 1;
                } else {
                    // login failed
                    successfullLogin.setText("Invalid Credentials...");
                }

                if (loginStatus == 1) {
                    // will logout user on abnormal termination of program (ctrl + c)
                    successfullLogin.setText("Login Successfully");

                    final String userLogout = login_u_name;
                    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                        try {
                            final PrintWriter out2 = new PrintWriter(s.getOutputStream(), true); // autoFlush : true
                            out2.println("logout:" + userLogout); // sending to server
                        } catch (Exception e) {
                            System.err.println("Error setting login status: " + e.getMessage());
                        }
                    }));

                    try {
                        new FirstPage(this.s, login_u_name);
                    } catch (Exception e) {
                    }
                    this.dispose();

                }

            } catch (Exception e) {
            }

        });

        this.setVisible(true);
        this.setSize(400, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

class FirstPage extends JFrame {

    Socket s;
    String selfUsername; // person who is logged in

    JTextField targetUsername; // whom we want to chat with
    JLabel header, usernameLabel, notification;
    JButton chatBtn;

    int doesUserExist;
    int isUserOnline;

    PrintWriter out;
    BufferedReader br;

    FirstPage(Socket s, String senderName) throws Exception {
        this.s = s;
        this.selfUsername = senderName;

        usernameLabel = new JLabel("Chat with (Username) : ");
        notification = new JLabel("");
        targetUsername = new JTextField(40);
        chatBtn = new JButton("Start Chat");

        this.add(usernameLabel);
        this.add(targetUsername);
        this.add(chatBtn);
        this.add(notification);

        // on clicking chatBtn button
        chatBtn.addActionListener(ae -> {
            String targetusername = targetUsername.getText();
            notification.setText("");

            if (targetusername.equals(selfUsername)) {
                notification.setText("Can't talk to yourself ....");
                return;
            }

            try {

                out = new PrintWriter(s.getOutputStream(), true); // autoFlush : true
                out.println("isUserNameTaken:" + targetusername); // sending to server

                br = new BufferedReader(new InputStreamReader(s.getInputStream()));

                doesUserExist = Integer.parseInt(br.readLine());

            } catch (Exception e) {
            }

            if (doesUserExist == 0) {
                notification.setText("User Does not Exist !!");
                return;

            } else {

                try {
                    out = new PrintWriter(s.getOutputStream(), true); // autoFlush : true
                    out.println("isUserOnline:" + targetusername); // sending to server

                    br = new BufferedReader(new InputStreamReader(s.getInputStream()));

                    isUserOnline = Integer.parseInt(br.readLine());

                } catch (Exception e) {
                }

                if (isUserOnline == 1) {
                    // redirect to user for chatting
                    // System.out.println("Starting chat with : " + targetusername);
                    notification.setText("Starting Chat with " + targetusername);

                    try {
                        out = new PrintWriter(s.getOutputStream(), true); // autoFlush : true
                        out.println("createUniqueName:" + selfUsername + ":" + targetusername);
                    } catch (Exception e) {
                    }

                    try {
                        new ChattingPage(this.s, selfUsername, targetusername);
                    } catch (Exception e) {
                    }
                    this.dispose();

                } else {
                    notification.setText(targetusername + " is offline .");
                    return;
                }

            }

        });

        this.setTitle("Chat Application");
        this.setLayout(new GridLayout(12, 1));
        this.setVisible(true);
        this.setSize(400, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

class ChattingPage extends JFrame {
    Socket s;
    String selfUsername; // person who is logged in

    String targetUsername; // whom we want to chat with
    JLabel header, chattingWith;
    JButton send;

    JTextArea chatArea;
    JTextField messageField;
    JButton sendButton;

    PrintWriter out;
    BufferedReader br;

    public ChattingPage(Socket s, String selfUsername, String targetusername) {

        this.s = s;
        this.selfUsername = selfUsername;
        this.targetUsername = targetusername;

        // header = new JLabel("Chat Application");
        chattingWith = new JLabel(targetusername);

        chatArea = new JTextArea(10, 30);
        chatArea.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);

        messageField = new JTextField(30);
        sendButton = new JButton("Send");

        receiveMessages(s, chatArea);

        sendButton.addActionListener(ae -> {
            sendMessage();
        });

        JPanel inputPanel = new JPanel();
        inputPanel.add(messageField);
        inputPanel.add(sendButton);

        this.setLayout(new BorderLayout());
        this.add(chattingWith, BorderLayout.NORTH);
        this.add(chatScrollPane, BorderLayout.CENTER);
        this.add(inputPanel, BorderLayout.SOUTH);

        this.setTitle("Chat Application");
        this.setSize(400, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {

            chatArea.append(selfUsername + " : " + message + "\n");
            messageField.setText(""); // Clear the message field after sending

            // Send the message to the server
            try {
                PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                out.println("sendTo:" + targetUsername + ":" + message); // Writing data to the server
            } catch (Exception e) {
                System.out.println("(GUI) Error generated while sending msg from client to server : " + e);
            }

        }
    }

    public void receiveMessages(Socket s, JTextArea chatArea) {
        ReceiveOnGui receiveThread = new ReceiveOnGui(s, chatArea);
        Thread receiveThread1 = new Thread(receiveThread);
        receiveThread1.start();

    }
}
