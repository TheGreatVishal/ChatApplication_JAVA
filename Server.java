import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) throws Exception {
        this.serverSocket = serverSocket;
    }

    public void startServer() throws Exception {
        try {
            while (!serverSocket.isClosed()) {
                Socket s = serverSocket.accept();
                System.out.println("A new client is connected.");

                ClientHandler clientHandler = new ClientHandler(s);

                Thread thread = new Thread(clientHandler);
                thread.start();
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