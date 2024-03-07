package networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private static final String server_IP = "192.168.0.159";
    private static final int serverPort = 1234;

    public static void main(String[] args) throws IOException {
        Socket clientSocket = new Socket(server_IP, serverPort);

        ServerConnection serverCon = new ServerConnection(clientSocket);

        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        new Thread(serverCon).start();

        boolean isTrue = true;
        while(isTrue){
            System.out.println(">.< ");

            String command = keyboard.readLine();


            if(command.equals("quit")){
                isTrue = false;
            }

            out.println(command);

        }

        clientSocket.close();
    }


}
