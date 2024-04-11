import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Scanner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.awt.*;
import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

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

            System.out.print("\033[H\033[2J");
            System.out.flush();

            int port = 8000; // Port number the server is listening on

            dottedLine();
            System.out.print("\nEnter IP : ");
            String ip = sc.next();

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
        }
    }
}

// GUI SECTION
class IP extends JFrame {
    JLabel ipAddress;
    JTextField inputIP;
    JButton enterBtn;

    String ip = null;

    public IP() throws Exception {

        ipAddress = new JLabel("Enter IP : ");
        inputIP = new JTextField(20);
        enterBtn = new JButton("Enter");

        add(ipAddress);
        add(inputIP);
        add(enterBtn);

        enterBtn.addActionListener(ae -> {
            try {
                if (inputIP.getText().length() >= 7) {
                    this.ip = inputIP.getText();
                    new INDEX(this.ip);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid Input", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
            }
        });
        // Bind the Enter key to the enterBtn using lambda expression
        InputMap inputMap = inputIP.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = inputIP.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "clickEnter");
        actionMap.put("clickEnter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enterBtn.doClick();
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
                PrintWriter out = new PrintWriter(s.getOutputStream(), true); // autoFlush: true
                out.println("");
                new SignUpPage(this.s, ip);
                this.dispose();
            } catch (Exception e) {
            }
        });

        // for Login button
        loginBtn.addActionListener(ae -> {
            try {
                s = new Socket(ip, 8000);

                PrintWriter out = new PrintWriter(s.getOutputStream(), true); // autoFlush: true
                out.println("");

                new LoginPage(this.s, this.ip);
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
                            final PrintWriter out2 = new PrintWriter(s.getOutputStream(), true);
                            // autoFlush : true
                            out2.println("removeClient:" + removeFromClientHandler); // sending to server

                            System.out.println("\nRemoved from clientHandler Successfully.");
                        } catch (Exception e) {
                            System.err.println("Error setting login status: " + e.getMessage());
                        }
                    }));

                    try {
                        new INDEX(ip);
                        this.dispose();
                    } catch (Exception e) {
                    }

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

    String login_pass, ip;
    int loginStatus = 0;

    public LoginPage(Socket s, String ip) throws Exception {

        this.s = s;
        this.ip = ip;

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

        // Bind the Enter key to the login button using lambda expression
        InputMap inputMap = username.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = username.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "clickEnter");
        actionMap.put("clickEnter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pass.requestFocusInWindow();
            }
        });

        // Bind the Enter key to the login button using lambda expression
        inputMap = pass.getInputMap(JComponent.WHEN_FOCUSED);
        actionMap = pass.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "login");
        actionMap.put("login", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginButton.doClick();
            }
        });

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
                    // System.out.println("Login Successfully");
                    JOptionPane.showMessageDialog(null, "Login Successfully", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    loginStatus = 1;
                } else {
                    // login failed
                    // successfullLogin.setText("Invalid Credentials...");
                    JOptionPane.showMessageDialog(null, "Invalid Credentials...", "Error", JOptionPane.ERROR_MESSAGE);
                }

                if (loginStatus == 1) {
                    // successfullLogin.setText("Login Successfully");
                    try {
                        new FirstPage(this.s, login_u_name, ip);
                        this.dispose();
                    } catch (Exception e) {
                    }
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
    String loggedInPerson, ip; // person who is logged in

    JButton chatsBtn, logOutBtn;
    JPanel dataPanel;
    GridBagConstraints gbc;

    PrintWriter out;
    BufferedReader br;

    public FirstPage(Socket s, String loggedInPerson, String ip) throws Exception {
        this.s = s;
        this.ip = ip;
        this.loggedInPerson = loggedInPerson;

        // Set layout for the frame
        setLayout(new BorderLayout());

        JPanel p1 = new JPanel(new GridLayout(1, 3));
        chatsBtn = new JButton("Chats");
        logOutBtn = new JButton("Logout");

        p1.add(chatsBtn);
        p1.add(logOutBtn);

        // Add the panel with buttons at the top
        add(p1, BorderLayout.NORTH);

        // Create the data panel
        dataPanel = new JPanel();
        dataPanel.setLayout(new GridBagLayout()); // Vertical layout
        dataPanel.setBackground(Color.LIGHT_GRAY); // Set background color

        gbc = new GridBagConstraints();

        // Add the data panel below the buttons panel
        add(dataPanel, BorderLayout.CENTER);

        // Register button listeners
        logOutBtn.addActionListener(ae -> {
            try {
                final PrintWriter out2 = new PrintWriter(s.getOutputStream(), true); // autoFlush : true
                out2.println("logout:" + loggedInPerson); // sending to server
                JOptionPane.showMessageDialog(dataPanel, "Logout Successfully", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                new INDEX(ip);
                this.dispose();

            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        chatsBtn.addActionListener(ae -> {
            try {
                handleChatsButtonClick();
            } catch (Exception e) {
                // TODO: handle exception
            }
        });

        setTitle("Chat Application");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void handleChatsButtonClick() throws Exception {

        dataPanel.removeAll();
        String[] receivedUsers = null;

        System.out.println("\nTrying to refresh Users available....");

        Socket dataSocket = new Socket(this.ip, 8000);

        out = new PrintWriter(dataSocket.getOutputStream(), true); // autoFlush: true
        out.println("fetchUsers:" + this.loggedInPerson); // sending to server

        System.out.println("\nReceived Available users successfully....");
        String allUsers = null;
        dataSocket.setSoTimeout(2000); // Set a timeout of 5 seconds (adjust as needed)

        try {
            try {
                br = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
                System.out.println("\nReaching here...");
                allUsers = br.readLine();
                dataSocket.close();
                System.out.println("Received all online users by chats btn (string) : " + allUsers);

                receivedUsers = allUsers.split(" ");
                System.out.println("\nLength of list received : " + receivedUsers.length);
                for (String users : receivedUsers) {
                    System.out.println("\n" + users);
                }

                JLabel userNames[] = new JLabel[receivedUsers.length];
                JButton connectBtn[] = new JButton[receivedUsers.length];
                if (receivedUsers.length != 0) {
                    int i = 0;
                    // Process the received data
                    for (String user : receivedUsers) {

                        userNames[i] = new JLabel(user);
                        connectBtn[i] = new JButton("Connect");

                        gbc.insets = new Insets(10, 0, 0, 0);
                        gbc.gridx = 0;
                        gbc.gridy = i;
                        dataPanel.add(userNames[i], gbc);

                        gbc.insets = new Insets(10, 10, 0, 0);
                        gbc.gridx = 1;
                        gbc.gridy = i;
                        dataPanel.add(connectBtn[i], gbc);

                        i++;
                    }

                    for (int j = 0; j < receivedUsers.length; j++) {
                        final int effectiveIndex = j;
                        connectBtn[j].addActionListener(ae -> {
                            try {
                                // creating new socket
                                Socket clienSocket = new Socket(ip, 8000);

                                out = new PrintWriter(clienSocket.getOutputStream(), true);

                                out.println(
                                        "addClient:" + this.loggedInPerson + ":" + userNames[effectiveIndex].getText());

                                System.out.println("\nAdded in clientHandler successfully...." + "(" +
                                        loggedInPerson + ")");

                                Thread chatThread = new Thread(() -> {
                                    new ChattingPage(clienSocket, loggedInPerson, userNames[effectiveIndex].getText());
                                });
                                chatThread.start();
                            } catch (Exception e) {
                                // TODO: handle exception
                            }
                        });
                    }
                    revalidate();
                    repaint();

                    System.out.println(receivedUsers.length + " members are online right now.");
                } else {
                    System.out.println("Received data is not in the expected format.");
                }

            } catch (SocketTimeoutException ste) {
                System.err.println("Timeout while reading input stream: " + ste.getMessage());
                // Handle the timeout scenario
            } catch (IOException e) {
                System.err.println("Error reading input stream: " + e.getMessage());
                // Handle other IO exceptions
            }
        } catch (Exception e) {
            // e.printStackTrace();
        } finally {
            dataSocket.close();
        }
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

          // Bind the Enter key to the enterBtn using lambda expression
          InputMap inputMap = messageField.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
          ActionMap actionMap = messageField.getActionMap();
  
          inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "clickEnter");
          actionMap.put("clickEnter", new AbstractAction() {
              @Override
              public void actionPerformed(ActionEvent e) {
                sendButton.doClick();
              }
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
        // this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
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

                try (FileWriter fw = new FileWriter("Log.txt", true);
                        BufferedWriter bw = new BufferedWriter(fw)) {
                    bw.write("\nFrom Client.java : \nSender : " + selfUsername + " to Socket : " + s);
                }

            } catch (Exception e) {
                System.out.println("(GUI) Error generated while sending msg from client to server : " + e);
            }
        }
    }

    public void receiveMessages(Socket s, JTextArea chatArea) {

        try (FileWriter fw = new FileWriter("Log.txt", true);
                BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("\nFrom Client.java : \nReceiver : " + selfUsername + " from Socket : " + s);
        } catch (Exception e) {

        }
        ReceiveOnGui receiveThread = new ReceiveOnGui(s, chatArea);
        Thread receiveThread1 = new Thread(receiveThread);
        receiveThread1.start();

    }
}
