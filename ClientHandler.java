import packages.*;

import java.util.Iterator;
import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
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
    public static String url;
    public static String username;
    public static String password;
    public static Connection con;

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;
    private String uniqueName;
    private String mode; // will give info about how client is added in handler {by signup or login}

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
            this.uniqueName = "null";
            clientHandlers.add(this);
            try (FileWriter fw = new FileWriter("Log.txt", true);
                    BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write("\nNew Client joined ClientHandler.." + this.socket
                        + "\nAfter adding client from constructor for client handler : ");
            }

            for (ClientHandler ch : ClientHandler.clientHandlers) {
                try (FileWriter fw = new FileWriter("Log.txt", true);
                        BufferedWriter bw = new BufferedWriter(fw)) {
                    bw.write("\n " + ch.toString());
                }
            }

        } catch (IOException e) {
            try (FileWriter fw = new FileWriter("Log.txt", true);
                    BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write("\n Constructor didn't worked as expected");
            }
        }
    }

    public String toString() {
        return "ClientHandler{" +
                "socket=" + socket +
                ", clientUsername='" + clientUsername + '\'' +
                ", uniqueName='" + uniqueName + '\'' +
                ", mode= '" + mode + '\'' +
                '}';
    }

    public void run() {
        String messageFromClient;
        Registeration u = new Registeration(url, username, password, con);

        while (socket.isConnected()) {
            try {
                messageFromClient = this.bufferedReader.readLine();

                try (FileWriter fw = new FileWriter("Log.txt", true);
                        BufferedWriter bw = new BufferedWriter(fw)) {
                    bw.write("\nString received : " + messageFromClient);
                }

                int colonFound = 0;

                for (int i = 0; i < messageFromClient.length(); i++) {
                    if (messageFromClient.charAt(i) == ':') {
                        colonFound++;
                    }
                }
                try (FileWriter fw = new FileWriter("Log.txt", true);
                        BufferedWriter bw = new BufferedWriter(fw)) {
                    bw.write("\nColon Found : " + colonFound);
                }

                if (colonFound == 1) {

                    String[] parts = messageFromClient.split(":");
                    String methodToExecute = parts[0];
                    String str = parts[1];
                    try (FileWriter fw = new FileWriter("Log.txt", true);
                            BufferedWriter bw = new BufferedWriter(fw)) {
                        bw.write("\nPart 1 : " + parts[0]);
                        bw.write("\nPart 2 : " + parts[1]);

                    }

                    if (methodToExecute.equals("isUserNameTaken")) {

                        try (FileWriter fw = new FileWriter("Log.txt", true);
                                BufferedWriter bw = new BufferedWriter(fw)) {

                            bw.write("\nUsername from Server : " + str);
                        }
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
                    } else if (methodToExecute.equals("getOnlineUsers")) {

                        try (FileWriter fw = new FileWriter("Log.txt", true);
                                BufferedWriter bw = new BufferedWriter(fw)) {
                            bw.write("Inside get online users");
                        }
                        getOnlineUsers(parts[1]);
                    }
                } else if (colonFound == 2) {
                    String[] parts = messageFromClient.split(":");
                    String methodToExecute = parts[0];
                    String u_name = parts[1];
                    String pass = parts[2];

                    try (FileWriter fw = new FileWriter("Log.txt", true);
                            BufferedWriter bw = new BufferedWriter(fw)) {

                        bw.write("\nData Received at server : " + "\nMethod to execute : " + methodToExecute
                                + "\nFirst Arguement : " + parts[1] + "\nSecond Arguement : " + parts[2]);

                    }

                    if (methodToExecute.equals("signup")) {
                        try (FileWriter fw = new FileWriter("Log.txt", true);
                                BufferedWriter bw = new BufferedWriter(fw)) {
                            bw.write("\nBefore signup : ");

                        }
                        int i = 1;
                        for (ClientHandler ch : ClientHandler.clientHandlers) {

                            try (FileWriter fw = new FileWriter("Log.txt", true);
                                    BufferedWriter bw = new BufferedWriter(fw)) {

                                bw.write("\n" + i + " : " + ch.toString());

                            }
                            i++;
                        }
                        u.signUp(u_name, pass); // creating accout
                        this.clientUsername = u_name;
                        this.mode = "signup";
                        removeExtraClients();
                        try (FileWriter fw = new FileWriter("Log.txt", true);
                                BufferedWriter bw = new BufferedWriter(fw)) {
                            bw.write("\nAfter signup : ");

                        }
                        i = 1;
                        for (ClientHandler ch : ClientHandler.clientHandlers) {
                            try (FileWriter fw = new FileWriter("Log.txt", true);
                                    BufferedWriter bw = new BufferedWriter(fw)) {
                                bw.write("\n" + i + " : " + ch.toString());

                            }
                            i++;
                        }
                    } else if (methodToExecute.equals("login")) {
                        // login

                        try (FileWriter fw = new FileWriter("Log.txt", true);
                                BufferedWriter bw = new BufferedWriter(fw)) {
                            bw.write("\nTrying to login inside CH 1");
                        }
                        int status = u.login(u_name, pass);

                        if (status == 1) {
                            // login done
                            this.mode = "login";
                            try (FileWriter fw = new FileWriter("Log.txt", true);
                                    BufferedWriter bw = new BufferedWriter(fw)) {
                                bw.write("\nLogin done");

                            }
                            this.clientUsername = u_name;
                            u.setLoginStatus(1, u_name);
                            PrintWriter out = new PrintWriter(socket.getOutputStream(), true); // autoFlush : true

                            try (FileWriter fw = new FileWriter("Log.txt", true);
                                    BufferedWriter bw = new BufferedWriter(fw)) {

                                bw.write("\n\nAfter login : ");
                            }
                            int i = 1;
                            for (ClientHandler ch : ClientHandler.clientHandlers) {
                                try (FileWriter fw = new FileWriter("Log.txt", true);
                                        BufferedWriter bw = new BufferedWriter(fw)) {
                                    bw.write("\n" + i + " : " + ch.toString());
                                }
                                i++;
                            }
                            out.println("1");
                        } else {
                            // login failed
                            PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true); // autoFlush : true
                            out.println("-1");
                        }
                    } else if (methodToExecute.equals("sendTo")) {
                        try (FileWriter fw = new FileWriter("Log.txt", true);
                                BufferedWriter bw = new BufferedWriter(fw)) {
                            bw.write("\nMsg reached to server...");
                        }
                        String receiverName = parts[1];
                        String msg = parts[2];
                        try (FileWriter fw = new FileWriter("Log.txt", true);
                                BufferedWriter bw = new BufferedWriter(fw)) {
                            bw.write("\nMsg from clientHandler : send to -> " + receiverName + "\nmsg : " + msg);
                        }
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

    public void showClientHandlerStatus() {
        int i = 1;
        for (ClientHandler ch : ClientHandler.clientHandlers) {
            try (FileWriter fw = new FileWriter("Log.txt", true);
                    BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write("\n" + i + " : " + ch.toString());
            } catch (Exception e) {
            }
            i++;
        }
    }

    public void setDetailsOfClient(String firstName, String secondName) {
        String uniqueName;

        // Compare the names to determine the order
        if (firstName.compareTo(secondName) < 0) {
            uniqueName = firstName + "_" + secondName;
        } else {
            uniqueName = secondName + "_" + firstName;
        }

        this.clientUsername = firstName;
        this.uniqueName = uniqueName;
        this.mode = "Paired";
    }

    public boolean checkIfPairExist(String firstName, String secondName) throws Exception {

        try (FileWriter fw = new FileWriter("Log.txt", true);
                BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("\n Inside checkIfPairExist");
        }
        boolean returnValue = false;

        String uniqueName;

        // Compare the names to determine the order
        if (firstName.compareTo(secondName) < 0) {
            uniqueName = firstName + "_" + secondName;
        } else {
            uniqueName = secondName + "_" + firstName;
        }

        for (ClientHandler ch : ClientHandler.clientHandlers) {
            if (ch.clientUsername.compareTo(firstName) == 0 && ch.uniqueName.compareTo(uniqueName) == 0
                    && ch.mode.compareTo("Paired") == 0) {

                try (FileWriter fw = new FileWriter("Log.txt", true);
                        BufferedWriter bw = new BufferedWriter(fw)) {
                    bw.write("\nPair exists...");
                }
                return true;
            }
        }
        try (FileWriter fw = new FileWriter("Log.txt", true);
                BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("\nchecking pair working fine....  returning : " + returnValue);
        }

        return returnValue;
    }

    public void getOnlineUsers(String rejectName) throws Exception {

        try (FileWriter fw = new FileWriter("Log.txt", true);
                BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("\n Preparing online users list");
        }
        List<String> users = new ArrayList<>();
        for (ClientHandler ch : ClientHandler.clientHandlers) {
            int flag = 0;
            if (ch.clientUsername.equals(rejectName)) {
                continue;
            } else {
                for (String user : users) {
                    if (ch.clientUsername.equals(user)) {
                        flag = 1;
                    }
                }
                if (flag == 0) {
                    users.add(ch.clientUsername);
                }
            }
        }
        try (FileWriter fw = new FileWriter("Log.txt", true);
                BufferedWriter bw = new BufferedWriter(fw)) {

            bw.write("List prepared : ");

        }

        StringBuilder allUsersBuilder = new StringBuilder();
        for (String user : users) {
            allUsersBuilder.append(user).append(" ");
        }
        String allUsers = allUsersBuilder.toString().trim();
        allUsersBuilder.append("\n");
        try (FileWriter fw = new FileWriter("Log.txt", true);
                BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("\nSending : " + allUsers);
        }
        try {

            if (allUsers.length() > 0) {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true); // autoFlush : true
                out.println(allUsers); // sending to client
                try (FileWriter fw = new FileWriter("Log.txt", true);
                        BufferedWriter bw = new BufferedWriter(fw)) {
                    bw.write("Data sent successfully...");
                }
            }
        } catch (Exception e) {
        }

    }

    public void createUniqueName(String firstName, String secondName) throws Exception {
        String uniqueName;

        if (firstName.compareTo(secondName) < 0) {
            uniqueName = firstName + "_" + secondName;
        } else {
            uniqueName = secondName + "_" + firstName;
        }

        for (ClientHandler ch : ClientHandler.clientHandlers) {

            if (ch.clientUsername.equals(secondName) && ch.uniqueName.equals("null")) {
                ch.uniqueName = uniqueName;
                break;
            }
        }
        try (FileWriter fw = new FileWriter("Log.txt", true);
                BufferedWriter bw = new BufferedWriter(fw)) {

            bw.write("\nFrom unique name setter : ");
        }

        int i = 1;
        for (ClientHandler ch : ClientHandler.clientHandlers) {

            try (FileWriter fw = new FileWriter("Log.txt", true);
                    BufferedWriter bw = new BufferedWriter(fw)) {

                bw.write("\n" + i + " : " + ch.toString());
            }
            i++;
        }

    }

    public void isUserNameTaken(String clientName) throws Exception {
        Registeration u = new Registeration(url, username, password, con);
        // Checking for username
        if (!u.isUserNameTaken(clientName)) {

            try (FileWriter fw = new FileWriter("Log.txt", true);
                    BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write("\nFrom Server : This Username can be used..." + "\nFrom ClientHandler");
            }

            int i = 1;
            for (ClientHandler ch : ClientHandler.clientHandlers) {
                try (FileWriter fw = new FileWriter("Log.txt", true);
                        BufferedWriter bw = new BufferedWriter(fw)) {
                    bw.write("\n" + i + " : " + ch.toString());
                }
                i++;
            }

            PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true); // autoFlush : true
            out.println("0");
        } else {
            try (FileWriter fw = new FileWriter("Log.txt", true);
                    BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write("Username already taken.");
            }
            PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true); // autoFlush : true
            out.println("1");
        }
    }

    public void sendMessasgeTo(String messageToSend, String targetClient) throws Exception {
        String uniqueName = null;

        // Compare the names to determine the order
        if ((this.clientUsername).compareTo(targetClient) < 0) {
            uniqueName = this.clientUsername + "_" + targetClient;
        } else {
            uniqueName = targetClient + "_" + this.clientUsername;
        }

        try (FileWriter fw = new FileWriter("Log.txt", true);
                BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("\n\nInside send to message...: " + uniqueName);
        }

        if (targetClient.compareTo("") == 0) {
            return;
        }

        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (clientHandler.clientUsername.equals(targetClient) && clientHandler.uniqueName.equals(uniqueName)) {

                    try (FileWriter fw = new FileWriter("Log.txt", true);
                            BufferedWriter bw = new BufferedWriter(fw)) {
                        bw.write("\nSending to : " + targetClient + "\nMsg : " + messageToSend);
                    }

                    clientHandler.bufferedWriter.write(this.clientUsername + " : " + messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (Exception e) {

            }
        }
    }

    public void removeExtraClients2() throws Exception {
        ArrayList<ClientHandler> toRemove = new ArrayList<>();
        for (int i = 0; i < clientHandlers.size(); i++) {
            ClientHandler ch1 = clientHandlers.get(i);
            for (int j = i + 1; j < clientHandlers.size(); j++) {
                ClientHandler ch2 = clientHandlers.get(j);
                if (ch1.clientUsername.equals(ch2.clientUsername)
                        && ch1.mode.equals(ch2.mode)
                        && ch1.uniqueName.equals(ch2.uniqueName)) {
                    toRemove.add(ch2);
                    break;
                }
            }
        }
        clientHandlers.removeAll(toRemove);
    }

    public void removeExtraClients() throws Exception {
        try (FileWriter fw = new FileWriter("Log.txt", true);
                BufferedWriter bw = new BufferedWriter(fw)) {

            bw.write("Inside removeExtraClients");
        }
        Iterator<ClientHandler> iterator = ClientHandler.clientHandlers.iterator();
        while (iterator.hasNext()) {
            ClientHandler ch = iterator.next();

            if (ch.mode.equals("signup") || ch.clientUsername == null) {
                iterator.remove(); // Remove the client using iterator
                try (FileWriter fw = new FileWriter("Log.txt", true);
                        BufferedWriter bw = new BufferedWriter(fw)) {
                    bw.write("SERVER : " + ch.clientUsername + " has left the clientHandler !");
                }

            } else {
            }
        }
    }

    public void removeClient(String clientName) throws Exception {
        try (FileWriter fw = new FileWriter("Log.txt", true);
                BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("Inside removeClient");
        }
        Iterator<ClientHandler> iterator = ClientHandler.clientHandlers.iterator();
        while (iterator.hasNext()) {
            ClientHandler ch = iterator.next();
            if (ch.clientUsername.equals(clientName)) {
                iterator.remove(); // Remove the client using iterator

                try (FileWriter fw = new FileWriter("Log.txt", true);
                        BufferedWriter bw = new BufferedWriter(fw)) {
                    bw.write("SERVER : " + ch.clientUsername + " has left the clientHandler !");
                }
            } else {
            }
        }
    }
}