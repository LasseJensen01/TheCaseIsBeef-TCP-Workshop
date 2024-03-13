package networking;

import beef_commons.utility.*;
import beef_commons.logic.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

non-sealed class MotherServer extends ServerFieldCapsule {
    public static void main(String[] args) {
        MotherServer beefServer1 = new MotherServer();
    }

    public MotherServer() {
        try {
            port = 1234;
            isBeefing = true;
            board = Generel.constructBoard(20,20);
            serverSocket = new ServerSocket(port);
            playerThreads = new HashMap<>();
            inFromServer = new BufferedReader(new InputStreamReader(System.in));
            IP = InetAddress.getLocalHost().getHostAddress();

        } catch (IOException e) {
            System.err.println("fucky wucky");
        }
        boot(port);
        while (isBeefing) {
            tick();
            //Sleep?
        }

    }

    public void boot(int port) {
        System.out.println("Debog boot");
        Thread hostingThread = new Thread(() -> {
            System.out.println("Boot thread made");
            while (isBeefing) {
                initializePlayer();
            }
        });
        hostingThread.start();
    }

    public void initializePlayer() {
        try {
            Socket playerConnSocket = serverSocket.accept();
            System.out.println("Starting playerInstance");
            PlayerInstance newPlayer = new PlayerInstance(playerConnSocket);
            playerThreads.put(newPlayer.getPlayer().getName(), newPlayer);
            DataOutputStream outToPlayer = new DataOutputStream(playerConnSocket.getOutputStream());
            // Send map and later game state to the new pleb
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

    public void tick() {

    }

    public boolean resolveOutcome(ArrayList<String> inputs) {
        String[] words = new String[inputs.size()];

        for (int j = 0; j < inputs.size(); j++) {
            int i = 0;
            inputs.get(i).split(","/*check it spits on the right thing */, -1);

            String name = words[0];
            int posX = Integer.parseInt(words[1]);
            int posY = Integer.parseInt(words[2]);
            String action = words[3];
            Player getPlayer = playerThreads.get(name).getPlayer();

            Player currPlayer = playerThreads.get(name).getPlayer();

            switch (action) {
                case "w" -> {
                    GameLogic.updatePlayer(currPlayer,posX,posY,"up");
                }
                case "s" -> {
                    GameLogic.updatePlayer(currPlayer,posX,posY,"down");
                }
                case "a" -> {
                    GameLogic.updatePlayer(currPlayer,posX,posY,"left");
                }
                case "d" -> {
                    GameLogic.updatePlayer(currPlayer,posX,posY,"right");
                }
                default -> {}
            }
            i++;
        }
        return false;
    }
    public boolean resolveOutcome2(ArrayList<String> inputs){
        String[] words = new String[inputs.size()];

        String name = words[0];
        int posX = Integer.parseInt(words[1]);
        int posY = Integer.parseInt(words[2]);
        String action = words[3];

        Player getPlayer = playerThreads.get(name).getPlayer();

        if(getPlayer.getXpos() == posX && getPlayer.getYpos() == posY){
            return false;
        }else if(action.equals("w")){
            GameLogic.updatePlayer(playerThreads.get(name).getPlayer(), posX, posY, "up");
        }else if(action.equals("s")){
            GameLogic.updatePlayer(playerThreads.get(name).getPlayer(), posX, posY, "down");
        }else if(action.equals("a")){
            GameLogic.updatePlayer(playerThreads.get(name).getPlayer(), posX, posY, "left");
        }else if(action.equals("d")){
            GameLogic.updatePlayer(playerThreads.get(name).getPlayer(), posX, posY, "right");
        }
        return false;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }
}
sealed abstract class ServerFieldCapsule permits MotherServer {
    int port;
    boolean isBeefing;
    BufferedReader inFromServer;
    String IP;
    ServerSocket serverSocket;
    HashMap<String, PlayerInstance> playerThreads;
    String[] board;

    ServerFieldCapsule(){

    }
}

