package networking;

import logic.GameLogic;
import logic.Player;
import utility.Generel;
import utility.PosXY;

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
    public static String[] board = Generel.constructBoard(20, 20);

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
        String[] words = new String[inputs.size()];

        for (int j = 0; j < inputs.size(); j++) {
            int i = 0;
            inputs.get(i).split(","/*check it spits on the right thing */, -1);

            String name = words[0];
            int posX = Integer.parseInt(words[1]);
            int posY = Integer.parseInt(words[2]);
            String action = words[3];
            Player getPlayer = playerThreads.get(name).getPlayer();

            if(getPlayer.getXpos() == posX && getPlayer.getYpos() == posY){
                return false;
            }else if(action.equals("w")) {
                GameLogic.updatePlayer(playerThreads.get(name).getPlayer(), posX, posY, "up");
            }else if(action.equals("s")){
                GameLogic.updatePlayer(playerThreads.get(name).getPlayer(), posX, posY, "down");
            }else if(action.equals("a")){
                GameLogic.updatePlayer(playerThreads.get(name).getPlayer(), posX, posY, "left");
            }else if(action.equals("d")){
                GameLogic.updatePlayer(playerThreads.get(name).getPlayer(), posX, posY, "right");
            }

            i++;
        }
        return false;
    }

}
