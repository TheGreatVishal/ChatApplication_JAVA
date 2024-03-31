import packages.*;

import java.util.Iterator;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<ClientHandler>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;
    private String uniqueName;
    private String mode; // will give info about how client is added in handler {by signup or login}

    public static String url;
    public static String username;
    public static String password;
    public static Connection con;

    public ClientHandler(Socket socket) throws Exception {
        url = "jdbc:mysql://localhost:3306/chatapplication";
        username = "root";
        password = "root";
        con = DriverManager.getConnection(url, username, password);
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = null;
            this.uniqueName = null;
            clientHandlers.add(this);
        } catch (IOException e) {
            // closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public String toString() {
        return "ClientHandler{" +
                "socket=" + socket +
                ", clientUsername='" + clientUsername + '\'' +
                ", uniqueName='" + uniqueName + '\'' +
                '}';
    }

    public void run() {
        String messageFromClient;
        Registeration u = new Registeration(url, username, password, con);

        while (socket.isConnected()) {
            try {
                messageFromClient = this.bufferedReader.readLine();
                System.out.println("String received : " + messageFromClient);
                int colonFound = 0;

                for (int i = 0; i < messageFromClient.length(); i++) {
                    if (messageFromClient.charAt(i) == ':') {
                        colonFound++;
                    }
                }
                System.out.println("\nColon Found : " + colonFound);

                if (colonFound == 1) {
                    // checking for username availability

                    String[] parts = messageFromClient.split(":");
                    String methodToExecute = parts[0];
                    String str = parts[1];
                    System.out.println("\nPart 1 : " + parts[0]);
                    System.out.println("\nPart 2 : " + parts[1]);

                    if (methodToExecute.equals("isUserNameTaken")) {
                        System.out.println("Username from Server : " + str);
                        isUserNameTaken(str);
                    } else if (methodToExecute.equals("logout")) {
                        u.setLoginStatus(0, str);
                        removeClient(str);
                    } else if (methodToExecute.equals("removeClient")) {
                        removeClient(str);
                    } else if (methodToExecute.equals("isUserNameTaken")) {
                        if (u.isUserNameTaken(str)) {
                            PrintWriter out = new PrintWriter(socket.getOutputStream(), true); // autoFlush : true
                            out.println("1");
                        } else {
                            PrintWriter out = new PrintWriter(socket.getOutputStream(), true); // autoFlush : true
                            out.println("0");
                        }
                    } else if (methodToExecute.equals("isUserOnline")) {
                        if (u.isUserOnline(str) == 1) {
                            PrintWriter out = new PrintWriter(socket.getOutputStream(), true); // autoFlush : true
                            out.println("1");
                        } else {
                            PrintWriter out = new PrintWriter(socket.getOutputStream(), true); // autoFlush : true
                            out.println("0");
                        }
                    }

                } else if (colonFound == 2) {
                    String[] parts = messageFromClient.split(":");
                    String methodToExecute = parts[0];
                    String u_name = parts[1];
                    String pass = parts[2];

                    System.out.print("\nData Received at server : ");
                    System.out.print("\nMethod to execute : " + methodToExecute);
                    System.out.print("\nFirst Arguement : " + parts[1]);
                    System.out.println("\nSecond Arguement : " + parts[2]);

                    if (methodToExecute.equals("signup")) {
                        System.out.println("\nBefore signup : ");
                        int i = 1;
                        for (ClientHandler ch : ClientHandler.clientHandlers) {
                            System.out.println("\n" + i + " : " + ch.toString());
                            i++;
                        }
                        u.signUp(u_name, pass); // creating accout
                        this.clientUsername = u_name;
                        this.mode = "signup";
                        removeExtraClients();
                        System.out.println("\nAfter signup : ");
                        i = 1;
                        for (ClientHandler ch : ClientHandler.clientHandlers) {
                            System.out.println("\n" + i + " : " + ch.toString());
                            i++;
                        }
                    } else if (methodToExecute.equals("login")) {
                        // login
                        System.out.println("\nTrying to login inside CH 1");
                        int status = u.login(u_name, pass);

                        if (status == 1) {
                            // login done
                            this.mode = "login";
                            System.out.println("\nLogin done");
                            this.clientUsername = u_name;
                            u.setLoginStatus(1, u_name);
                            PrintWriter out = new PrintWriter(socket.getOutputStream(), true); // autoFlush : true

                            System.out.println("After login : ");
                            int i = 1;
                            for (ClientHandler ch : ClientHandler.clientHandlers) {
                                System.out.println("\n" + i + " : " + ch.toString());
                                i++;
                            }
                            out.println("1");
                        } else {
                            // login failed

                            PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true); // autoFlush : true
                            out.println("-1");
                        }
                    } else if (methodToExecute.equals("sendTo")) {
                        System.out.println("\nMsg reached to server...");
                        String receiverName = parts[1];
                        String msg = parts[2];
                        System.out.println("\nMsg from clientHandler : send to -> " + receiverName + "\nmsg : " + msg);

                        sendMessasgeTo(msg, receiverName);
                    } else if (methodToExecute.equals("createUniqueName")) {
                        String firstName = parts[1];
                        String secondName = parts[2];

                        createUniqueName(firstName, secondName);
                    }

                }
            } catch (Exception e) {
                break;
            }
        }
    }

    public void createUniqueName(String firstName, String secondName) {
        String uniqueName;


        // Compare the names to determine the order
        if (firstName.compareTo(secondName) < 0) {
            uniqueName = firstName + "_" + secondName;
        } else {
            uniqueName = secondName + "_" + firstName;
        }
        
        for (ClientHandler ch : ClientHandler.clientHandlers) {
            
            if (ch.clientUsername.equals(secondName) && ch.uniqueName == null) {
                ch.uniqueName = uniqueName;
                break;
            }
        }
        
        System.out.println("\nFrom unique name setter : ");
        int i = 1;
        for (ClientHandler ch : ClientHandler.clientHandlers) {
            System.out.println("\n" + i + " : " + ch.toString());
            i++;
        }

    }

    public void isUserNameTaken(String clientName) throws Exception {
        Registeration u = new Registeration(url, username, password, con);
        // Checking for username
        if (!u.isUserNameTaken(clientName)) {
            System.out.println("\nFrom Server : This Username can be used...");
            // this.clientUsername = clientName;

            System.out.println("From ClientHandler");
            int i = 1;
            for (ClientHandler ch : ClientHandler.clientHandlers) {
                System.out.println("\n" + i + " : " + ch.toString());
                i++;
            }

            PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true); // autoFlush : true
            out.println("0");
        } else {
            System.out.println("Username already taken.");
            PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true); // autoFlush : true
            out.println("1");
        }
    }

    public void sendMessasgeTo(String messageToSend, String targetClient) {
        String uniqueName = null;
        
        // Compare the names to determine the order
        if ((this.clientUsername).compareTo(targetClient) < 0) {
            uniqueName = this.clientUsername + "_" + targetClient;
        } else {
            uniqueName = targetClient + "_" + this.clientUsername;
        }
        System.out.println("\n\nInside send to message...: " + uniqueName);

        if (targetClient.compareTo("") == 0) {
            return;
        }
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (clientHandler.clientUsername.equals(targetClient) && clientHandler.uniqueName.equals(uniqueName)) {
                    System.out.println("\nMessage send to : " + targetClient);
                    System.out.println("\nMsg is : " + messageToSend);
                    clientHandler.bufferedWriter.write(this.clientUsername + " : " + messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (Exception e) {

                // closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }


    public static void removeExtraClients() throws Exception {
        Iterator<ClientHandler> iterator = ClientHandler.clientHandlers.iterator();
        while (iterator.hasNext()) {
            ClientHandler ch = iterator.next();

            if (ch.mode.equals("signup") || ch.clientUsername == null) {
                iterator.remove(); // Remove the client using iterator
                System.out.println("Removed ");
                System.out.println("SERVER : " + ch.clientUsername + " has left the clientHandler !");
            } else {
            }
        }
    }

    public static void removeClient(String clientName) throws Exception {
        Iterator<ClientHandler> iterator = ClientHandler.clientHandlers.iterator();
        while (iterator.hasNext()) {
            ClientHandler ch = iterator.next();
            System.out.println("Inside for loop");

            if (ch.clientUsername.equals(clientName)) {
                iterator.remove(); // Remove the client using iterator
                System.out.println("Removed ");
                System.out.println("SERVER : " + ch.clientUsername + " has left the clientHandler !");
            } else {
                // System.out.println("Client not found");
            }
        }

    }
}
