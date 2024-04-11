import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;

import packages.Registeration;

public class Server {
    private ServerSocket serverSocket;
    public static String url;
    public static String username;
    public static String password;
    public static Connection con;

    public Server(ServerSocket serverSocket) throws Exception {
        this.serverSocket = serverSocket;
    }

    public void startServer() throws Exception {
        try (FileWriter fw = new FileWriter("Log.txt", true);
                BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("\nServer Started ...." + "\nWaiting for Clients...");

        }
        try {
            while (!serverSocket.isClosed()) {
                Socket s = serverSocket.accept();

                BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                String strFromClient = br.readLine();

                int colonFound = 0;

                for (int i = 0; i < strFromClient.length(); i++) {
                    if (strFromClient.charAt(i) == ':') {
                        colonFound++;
                    }
                }

                try (FileWriter fw = new FileWriter("Log.txt", true);
                        BufferedWriter bw = new BufferedWriter(fw)) {
                    bw.write("\nString received at server with new socket connection : " + strFromClient);
                }

                if (colonFound == 1) {
                    String[] parts = strFromClient.split(":");
                    String methodToExecute = parts[0];
                    String data = parts[1];

                    if (methodToExecute.equals("fetchUsers")) {

                        try (FileWriter fw = new FileWriter("Log.txt", true);
                                BufferedWriter bw = new BufferedWriter(fw)) {
                            bw.write("\nData fetching using socket.....");
                        }

                        String rejectName = data;

                        url = "jdbc:mysql://localhost:3306/chatapplication";
                        username = "root";
                        password = "root";
                        con = DriverManager.getConnection(url, username, password);

                        Registeration R = new Registeration(url, username, password, con);

                        String allUsers = R.getOnlineUsers(rejectName);
                        try (FileWriter fw = new FileWriter("Log.txt", true);
                                BufferedWriter bw = new BufferedWriter(fw)) {
                            bw.write("\nFetched all users ... " + allUsers);
                        }

                        PrintWriter out = new PrintWriter(s.getOutputStream(), true); // autoFlush: true
                        out.println(allUsers);
                        try (FileWriter fw = new FileWriter("Log.txt", true);
                                BufferedWriter bw = new BufferedWriter(fw)) {
                            bw.write("\nSent online users to client's Chats page successfully...");
                        }

                    }

                } else if (colonFound == 0) {
                    try (FileWriter fw = new FileWriter("Log.txt", true);
                            BufferedWriter bw = new BufferedWriter(fw)) {
                        bw.write("\nA new client is connected.");

                    }

                    ClientHandler clientHandler = new ClientHandler(s);
                    
                    Thread thread = new Thread(clientHandler);
                    thread.start();
                } else if (colonFound == 2) {
                    
                    String[] parts = strFromClient.split(":");
                    String methodToExecute = parts[0];
                    String firstName = parts[1];
                    String secondName = parts[2];
                    
                    if(methodToExecute.equals("addClient")){
                        ClientHandler clientHandler = new ClientHandler(s);
                        Thread thread = new Thread(clientHandler);
                        thread.start();
                        Thread.sleep(500);
                        clientHandler.setDetailsOfClient(firstName, secondName);
                        clientHandler.showClientHandlerStatus();
                    }                            
                }

            }
        } catch (IOException e) {
            System.out.println("Error : " + e);
        }
    }

    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {

        System.out.println("Server Started ....");
        System.out.println("Waiting for Clients...");
        ServerSocket serverSocket = new ServerSocket(8000);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}