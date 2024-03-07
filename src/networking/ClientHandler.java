package networking;

import com.sun.source.tree.WhileLoopTree;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private ArrayList<ClientHandler> clients;

    public ClientHandler(Socket clientSocket, ArrayList<ClientHandler> clients) throws IOException {
        this.clientSocket = clientSocket;
        this.clients = clients;
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
    }

    public void close() throws IOException {
        clientSocket.close();
        in.close();
    }

    @Override
    public void run() {
        try{
            while(true){
                String msg = in.readLine();
                System.out.println(msg);
                outToAll(msg);
            }
        } catch (IOException e) {
            System.out.println("Client handler error");
            throw new RuntimeException(e);
        }
    }


    private void outToAll(String msg){
        for(ClientHandler client : clients){
            client.out.println(msg);
        }
    }
}
