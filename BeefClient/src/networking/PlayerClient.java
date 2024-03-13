package networking;

import gui.App;
import gui.Gui;

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

import static beef_commons.utility.Generel.constructBoard;

non-sealed class PlayerClient extends ClientFieldCapsule {

    public static void main(String[] args) {
        //test
        try {
            PlayerClient pc1 = new PlayerClient("192.168.1.186");
            while (true) {
                // gør din ting, bby!
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public PlayerClient(String IP) throws IOException {
        try {
            port = 1234;
            connectionSocket = new Socket(IP, port);
            outToServer = new DataOutputStream(connectionSocket.getOutputStream());
            inFromServer = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            inFromClient = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Sei yuw naem, baka! UwU");
            String msg = inFromClient.readLine();
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

        Generel.board = board;

        if (Generel.board == null) {
            Generel.board = constructBoard(20,20); //TODO for no-network testing
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

    public void recieveGamestate() {



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
