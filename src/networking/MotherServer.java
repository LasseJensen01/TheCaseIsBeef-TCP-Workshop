package networking;

import utility.Generel;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class MotherServer {
    private static BufferedReader inFromServer = new BufferedReader(new InputStreamReader(System.in));
    private static int port = 1234;
    private static ServerSocket serverSocket;
    private static String IP;
    private static boolean isBeefing = true;
    private static HashMap<String, PlayerInstance> playerThreads = new HashMap<>();
    public static String[] board = Generel.constructBoard(20,20);
    public static ArrayList<String> inputs = new ArrayList<>();
    public static void main(String[] args) {
        try {
            IP = InetAddress.getLocalHost().getHostAddress();
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("fucky wucky");
        }
        boot(port);
        tick();
    }


    public static void boot(int port) {
        System.out.println("Debog boot");
        Thread hostingThread = new Thread(() -> {
            System.out.println("Boot thread made");
            while (isBeefing) {
                initializePlayer();
            }
        });
        hostingThread.start();
    }

    public static void initializePlayer() {
        Socket playerConnSocket = null;
        try {
            playerConnSocket = serverSocket.accept();
            System.out.println("Starting playerInstance");
            PlayerInstance newPlayer = new PlayerInstance(playerConnSocket /*og mere?*/);
            playerThreads.put(newPlayer.getPlayer().getName(), newPlayer);
            DataOutputStream outToPlayer = new DataOutputStream(playerConnSocket.getOutputStream());
            // Send map and later gamestate to the new pleb
            System.out.println("Reading Board");
            for (int i = 0; i < board.length; i++) {
                outToPlayer.writeBytes(board[i] + "\n");
            }
            outToPlayer.writeBytes("quit" + "\n");
            System.out.println("Map sent to client");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void tick() {
    }

    public static boolean resolveOutcome(ArrayList<String> inputs) {
        return false;
    }

    public static ServerSocket getServerSocket() {
        return serverSocket;
    }
}
