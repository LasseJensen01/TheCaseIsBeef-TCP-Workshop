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
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

non-sealed class MotherServer extends ServerFieldCapsule {
    public static void main(String[] args) {
        MotherServer beefServer1 = new MotherServer();
    }

    public MotherServer() {
        try {
            System.out.println("**MotherServer**");
            System.out.println("Starting MotherServer");
            port = 1234;
            isBeefing = true;
            System.out.println("Creating board");
            board = Generel.constructBoard(20,20);
            Generel.setBoard(board);
            serverSocket = new ServerSocket(port);
            playerThreads = new HashMap<>();
            inFromServer = new BufferedReader(new InputStreamReader(System.in));
            IP = InetAddress.getLocalHost().getHostAddress();

        } catch (IOException e) {
            System.err.println("fucky wucky");
        }
        boot(port);
        tick2(50.00);

    }

    public void boot(int port) {
        System.out.println("Initiating Boot Thread");
        Thread hostingThread = new Thread(() -> {
            System.out.println("Boot thread made \n");
            while (isBeefing) {
                initializePlayer();
            }
        });
        hostingThread.start();
    }

    public void initializePlayer() {
        try {
            System.out.println("**Initialize Player**");
            System.out.println("Awaiting client connection...");
            Socket playerConnSocket = serverSocket.accept();
            System.out.println("Connection made with " + playerConnSocket.getInetAddress().getHostAddress());
            PlayerInstance newPlayer = new PlayerInstance(playerConnSocket);
            playerThreads.put(newPlayer.getPlayer().getName(), newPlayer);
            DataOutputStream outToPlayer = new DataOutputStream(playerConnSocket.getOutputStream());
            BufferedReader inFromPlayer = new BufferedReader(new InputStreamReader(playerConnSocket.getInputStream()));
            // Send map and later game state to the new pleb
            System.out.println("Sending board to client");
            for (int i = 0; i < board.length; i++) {
                outToPlayer.writeBytes(board[i] + "\n");
            }
            outToPlayer.writeBytes("quit" + "\n");
            System.out.println("Map succesfullt sent \n");
            fixPlayerPosition(newPlayer.getPlayer(), inFromPlayer.readLine()); // Blocks until client sends posXY
            System.out.println("Setting up listener for Clients inputs");
            ServerListenerThread slt = new ServerListenerThread(playerConnSocket, newPlayer.getPlayer().getName(), inputs);
            slt.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public synchronized void tick2(Double firmTime){
            while(isBeefing){
                // System.out.println("tick");
                LocalTime gamestateTime = LocalTime.now();
                resolveOutcome2();
                shipGamestate();
                LocalTime timeSpent = LocalTime.now();
                Long deltaTime = timeSpent.toNanoOfDay() - gamestateTime.toNanoOfDay();
                try {
                    if(firmTime >= deltaTime){
                        wait((long) (firmTime - deltaTime));
                    }
                }catch (Exception e){
                    System.err.println("Exception in tick2: " + e);
                }

            }
    }

    /**
     * Initially when player is created both client and server side the getRandomPosition method in gamelogic is called on botb sides leading
     * to one player having diffrent positions server and client side. This method aims to fix Server side postion to the same as Client side
     */
    public void fixPlayerPosition(Player p, String pos){
        String[] newPos = pos.split(",");
        String posX = newPos[0];
        String posY = newPos[1];
        p.setPos(new PosXY(Integer.parseInt(posX), Integer.parseInt(posY)));
    }


    public boolean resolveOutcome2() {
        for (int i = 0; i < inputs.size(); i++) {
           String[] words = inputs.get(i).split(",");

            String name = words[1];
            String action = words[3];
            Player p = playerThreads.get(name).getPlayer();
            int posX = p.getXpos();
            int posY = p.getYpos();

            if (action.equals("moveUp")) {
                posY--; // Fordi JavaFX er på crack så er
                GameLogic.updatePlayerServer(p, 0, -1, "up");
            } else if (action.equals("moveDown")) {
                posY++; // Fordi JavaFX
                GameLogic.updatePlayerServer(p, 0, 1, "down");
            } else if (action.equals("moveLeft")) {
                posX--; //Fordi
                GameLogic.updatePlayerServer(p, -1, 0, "left");
            } else if (action.equals("moveRight")) {
                posX++; //Hvorfor...
                GameLogic.updatePlayerServer(p, 1, 0, "right");
            } else if (action.equals("quit")){
                for(int k = 0; k < GameLogic.players.size(); k++){
                    p.setFacingDir("quit");
                    GameLogic.players.remove(p);
                    playerThreads.remove(p.getName());
                }
            }
        }
        inputs.clear();
        return false;
    }

    /**
     * Calculates and assembels the games state; Players positions, actions, points etc, and assembels them into
     * a list and ships it off to every player to update their GUI
     * @return true if succesfully shipped, false if error chaught
     */
    public boolean shipGamestate() {
        //New gamestae
        List<PlayerInstance> players = playerThreads.values().stream().toList();
        ArrayList<String> states = new ArrayList<>();
        for (PlayerInstance p : players){
            // Compiles players state into a string
            // Sent String is formed as: Name, xPos, yPos, facingDir, Point
            // Use Strings split function to split info on ","
            String state = p.getPlayer().getName() + ",";
            state += p.getPlayer().getXpos() + ",";
            state += p.getPlayer().getYpos() + ",";
            state += p.getPlayer().getFacingDir() + ",";
            state += p.getPlayer().getPoints();
            states.add(state);
        }
        // Arraylist 'states' contains every players new state, ship off to every player
        // Could make threads here to quicken the workload but maybe in a second iteration
        // Nested for loops could prove to slow down game significantly with either more players or if we up tickrate
        try{
            for (PlayerInstance p : players){
                if (!p.getSocket().isClosed()){
                    DataOutputStream sendIt = new DataOutputStream(p.getSocket().getOutputStream());
                    for (String s : states){
                        sendIt.writeBytes(s + "\n");
                    }
                }
            }
        }catch (IOException e){
            System.err.println("Error in shipgamestate: " + e);
            return false;
        }
        return true;


//        //Assemble Gamestate
//        String gameState = "";
//        List<PlayerInstance> instances = playerThreads.values().stream().toList();
//
//
//        for (PlayerInstance client : instances) {
//            Player player = client.getPlayer();
//
//            gameState += player.getName();
//            gameState += ",";
//            gameState += player.getXpos();
//            gameState += ",";
//            gameState += player.getYpos();
//            gameState += ",";
//            gameState += player.getFacingDir();
//            gameState += ",";
//            gameState += player.getPoints();
//
//            //Send gamestate to client
//            client.returnGamestate(gameState);
//        }
//        return true;
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
    ArrayList<String> inputs = new ArrayList<>();

    ServerFieldCapsule(){

    }
}

