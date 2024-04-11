package packages;

import java.io.IOException;
import java.net.Socket;

// Custom subclass for the second type of socket
class DataSocket extends Socket {
    private boolean isDataSocket;

    public DataSocket(String ip, int port) throws IOException {
        super(ip, port);
        this.isDataSocket = true;
    }

    public boolean isDataSocket() {
        return isDataSocket;
    }

    // Add more methods or properties specific to DataSocket if needed
}