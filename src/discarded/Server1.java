package discarded;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server1 {
    private static int port = 1234;

    private static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        while(true) {
            System.out.println("waiting for client");
            Socket client = serverSocket.accept();
            System.out.println("Connected");

            ClientHandler playerThread = new ClientHandler(client, clientHandlers);
            clientHandlers.add(playerThread);
            pool.execute(playerThread);

        }
    }
}
