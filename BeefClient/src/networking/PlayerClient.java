package networking;

import gui.App;
import gui.Gui;

import javafx.application.Application;
import javafx.event.Event;
import javafx.scene.input.KeyEvent;
import beef_commons.logic.*;
import beef_commons.utility.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

non-sealed public class PlayerClient extends ClientFieldCapsule {
    public static Player me;

    public static void main(String[] args) {
        //test
        try {
            PlayerClient pc1 = new PlayerClient("192.168.1.14");
            Gui.pc = pc1;

            // GameLogic.makeVirtualPlayer("Kaj"); // to be removed
            Application.launch(Gui.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public PlayerClient(String IP) throws IOException {
        try {
            inFromClient = new BufferedReader(new InputStreamReader(System.in)); // Define the client reader
            System.out.println("Sei yuw naem, baka! UwU"); //Type
            String msg = inFromClient.readLine();
            port = 1234;
            connectionSocket = new Socket(IP, port);
            outToServer = new DataOutputStream(connectionSocket.getOutputStream());
            inFromServer = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            outToServer.writeBytes(msg + "\n");

            recieveBoard();
            me = GameLogic.makePlayer(msg);
            outToServer.writeBytes(me.getXpos() + "," + me.getYpos() + "\n");
            GameStateReceiveThread t1 = new GameStateReceiveThread();
            t1.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            outToServer.flush();
        }
    }


    public void recieveBoard() throws IOException {
        System.out.println("Starting receive board");
        ArrayList<String> tempBoard = new ArrayList<>();
        boolean isDone = false;
        while (!isDone) {
            String temp = inFromServer.readLine();
            if (temp.equals("quit")) {
                isDone = true;
            } else {
                tempBoard.add(temp);
            }
        }
        System.out.println("Board read");
        board = new String[tempBoard.size()];
        tempBoard.toArray(board);

        Generel.setBoard(board);

        if (Generel.board[0] == null) {
            Generel.board = Generel.constructBoard(20, 20); //TODO for no-network testing
        }

        System.out.println();
        for (int i = 0; i < board.length; i++) {
            System.out.println(board[i]);
        }
        System.out.println();
    }

    public void declareAction(String action) {
        try {
            String msg = "PLAYER," + me.getName() + ",ACTION," + action;
            outToServer.writeBytes(msg + "\n"); //SENDING UPDATE
            if (action.equalsIgnoreCase("Quit")) {
                connectionSocket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String parseAction(KeyEvent event) {
        String action = null;
        switch (event.getCode()) {
            case UP -> action = "ACTION,moveUp,";
            case DOWN -> action = "ACTION,moveDown,";
            case LEFT -> action = "ACTION,moveLeft,";
            case RIGHT -> action = "ACTION,moveRight,";
            case SPACE -> action = "ACTION,shoot,";
            case ESCAPE -> action = "ACTION,quit,";
            default -> action = "ACTION,none,";
        }
        return action;
    }


    private class GameStateReceiveThread extends Thread {
        @Override
        public void run() {
            try {
                System.out.println("GameStateReceiveThread Running");
                String input = "";
                TimeUnit.SECONDS.sleep(1);
                while (!connectionSocket.isClosed()) {
                    input = inFromServer.readLine();
                    System.out.println(input);
                    String[] playerState = input.split(",");

                    //Updating
                    // Received String is formed as: Name, xPos, yPos, facingDir, Point
                    // Seperated by ',' so using split function of a string
                    Player p = null;
                    for (Player pl : GameLogic.players) {
                        if (pl.getName().equals(playerState[0])) {
                            p = pl;
                            break;
                        }
                    }
                    int xPos = Integer.parseInt(playerState[1]);
                    int yPos = Integer.parseInt(playerState[2]);
                    PosXY newPos = new PosXY(xPos, yPos);
                    int points = Integer.parseInt(playerState[4]);
                    if (p != null) {
                        PosXY oldPos = new PosXY(p.getXpos(), p.getYpos());
                        Gui.movePlayerOnScreen(oldPos, newPos, playerState[3]);
                        p.setPos(newPos);
                        p.setPoints(points);
                        Gui.updateScoreTable();
                    } else
                        addNewPlayerToGUI(playerState[0], newPos); // Should add a new player onto into the gui alongside you
                }
                System.out.println(connectionSocket.isClosed());
                System.out.println("GameStateReceiveThread Ending");

            } catch (Exception e) {
                System.err.println("Error in GSRT Thread: " + e.getMessage());
            }

        }

        // Should probably rename this to something more accurate
        private void addNewPlayerToGUI(String name, PosXY posXY) {
            Player p = new Player(name, posXY, "up");
            GameLogic.players.add(p);
            Gui.placePlayerOnScreen(p.getPos(), "up");
        }
    }

}


sealed abstract class ClientFieldCapsule permits PlayerClient {
    int port;
    String[] board;
    public Socket connectionSocket;
    BufferedReader inFromServer;
    BufferedReader inFromClient;
    DataOutputStream outToServer;

    public ClientFieldCapsule() {

    }
}
