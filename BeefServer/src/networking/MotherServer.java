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
import java.util.List;
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
            //Sleep? Sleep!
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
    public boolean resolveOutcome2(ArrayList<String> inputs) {
        String[] words = new String[inputs.size()];

        for (int j = 0; j < inputs.size(); j++) {
            int i = 0;
            inputs.get(i).split(",");

            String name = words[1];
            String action = words[3];
            Player p = playerThreads.get(name).getPlayer();
            int posX = p.getXpos();
            int posY = p.getYpos();

            if (action.equals("moveUp")) {
                posY--; // Fordi JavaFX er på crack så er
                GameLogic.updatePlayer(p, posX, posY, "up");
            } else if (action.equals("moveDown")) {
                posY++; // Fordi JavaFX
                GameLogic.updatePlayer(p, posX, posY, "down");
            } else if (action.equals("moveLeft")) {
                posX--; //Fordi
                GameLogic.updatePlayer(p, posX, posY, "left");
            } else if (action.equals("moveRight")) {
                posX++; //Hvorfor...
                GameLogic.updatePlayer(p, posX, posY, "right");
            } else if (action.equals("quit")){

            }
            i++;
        }
        return false;
    }


    public boolean shipGamestate() {

        //Assemble Gamestate
            //Pos(+ dir), point for hver spiller
            //Skal kunne CRUD spillerbrikker hos hver modtagende spiller

        String gameState = "";
        ArrayList<PlayerInstance> instances = (ArrayList<PlayerInstance>) playerThreads.values().stream().toList();

        for (PlayerInstance client : instances) {
            Player player = client.getPlayer();

            gameState += player.getName();
            gameState += ",";
            gameState += player.getXpos();
            gameState += ",";
            gameState += player.getYpos();
            gameState += ",";
            gameState += player.getFacingDir();
            gameState += ",";
            gameState += player.getPoints();
        }

        //Send Gamestate
        for (PlayerInstance client : instances) {

        }
        //playerThreads.forEach();
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

