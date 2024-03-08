package networking;

import logic.Player;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.PriorityQueue;

public class Server implements Beefable {
    private ServerSocket serverSocket;
    private PriorityQueue<PlayerInstance> instances = new PriorityQueue<>();
    public static void main(String[] args) {

    }

    public void boot(int port) {
        try {
            boolean isBeefing = true;
            serverSocket = new ServerSocket(port);

            Thread hostingThread = new Thread(() -> {
                while (isBeefing) {
                    initializePlayer();
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void initializePlayer() {
        Socket playerConnSocket = null;
        try {
            playerConnSocket = serverSocket.accept();

            PlayerInstance newPlayer = new PlayerInstance(playerConnSocket /*og mere?*/);
            instances.add(newPlayer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void tick() {

    }

    public boolean resolveOutcome() {
        return false;
    }
}
