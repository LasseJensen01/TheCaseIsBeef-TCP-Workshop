package networking;

import java.io.*;
import java.net.Socket;

public class ServerConnection implements Runnable{
    private Socket server;
    private BufferedReader in;


    public ServerConnection(Socket socket) throws IOException {
        server = socket;
        in = new BufferedReader(new InputStreamReader(server.getInputStream()));
    }

    @Override
    public void run() {
        while(true){
            try {
                String serverResponse = in.readLine();
                System.out.println("ServerConnections says: " + serverResponse);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
