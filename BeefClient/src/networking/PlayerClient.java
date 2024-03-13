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

non-sealed class PlayerClient extends ClientFieldCapsule {
    static Player me;

    public static void main(String[] args) {
        //test
        try {
            PlayerClient pc1 = new PlayerClient("192.168.1.202");

            me = GameLogic.makePlayer("Jønke");
            GameLogic.makeVirtualPlayer("Kaj"); // to be removed
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
        while(!isDone){
            String temp = inFromServer.readLine();
            if(temp.equals("quit")){
                isDone = true;
            }else {
                tempBoard.add(temp);
            }
        }
        System.out.println("Board read");
        board = new String[tempBoard.size()];
        tempBoard.toArray(board);

        Generel.setBoard(board);

        if (Generel.board[0] == null) {
            Generel.board = Generel.constructBoard(20,20); //TODO for no-network testing
        }

        System.out.println();
        for (int i = 0; i < board.length; i++) {
            System.out.println(board[i]);
        }
        System.out.println();
    }

    public void declareAction() {
        Player thisPlayer = App.me;

        try {
            String name = "PLAYER,"+thisPlayer.getName()+",";
            String action = parseAction((KeyEvent) Gui.currentAction);

            String updateMsg = name+action;
            outToServer.writeBytes(updateMsg); //SENDING UPDATE
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

    public void recieveGamestate(String gameState) {
        //nav, x pos, y pos, retning, point
        //recieve gamestate skal tage højde for CRUD
        String[] gameStateSplit = gameState.split(",");

        String name = gameStateSplit[0];
        int posX = Integer.parseInt(gameStateSplit[1]);
        int posY = Integer.parseInt(gameStateSplit[2]);
        String direction = gameStateSplit[3];
        int points = Integer.parseInt(gameStateSplit[4]);

        for(int i = 0; i < GameLogic.players.size(); i++){
            if(GameLogic.players.get(i).getName().equals(name)) {
                GameLogic.updatePlayer(GameLogic.players.get(i), posX, posY, direction);
                GameLogic.setPoints(GameLogic.players.get(i), points);
            }
        }
    }
    private void checkForNewPlayer(String name){
        //hvis et navn ikke er der, skal vi tilføje en spiller
        for(int i = 0; i < GameLogic.players.size(); i++){
            if(GameLogic.players.get(i).getName() != name){
                GameLogic.makeVirtualPlayer(name);
            }//Should also handle if someone has disconnected
        }
    }
}



sealed abstract class ClientFieldCapsule permits PlayerClient {
    int port;
    String[] board;
    Socket connectionSocket;
    BufferedReader inFromServer;
    BufferedReader inFromClient;
    DataOutputStream outToServer;

    public ClientFieldCapsule() {

    }
}
