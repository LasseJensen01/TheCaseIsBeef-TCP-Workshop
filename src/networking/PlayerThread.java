package networking;

import java.net.Socket;

public class PlayerThread extends Thread{
    private Socket connectionSocket;
    public PlayerThread(Socket sock) {
        connectionSocket = sock;
    }

    @Override
    public void run() {
        System.out.println("FUCK");
    }
}
