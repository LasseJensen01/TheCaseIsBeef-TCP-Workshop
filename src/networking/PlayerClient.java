package networking;

import gui.App;
import gui.Gui;
import javafx.event.Event;
import javafx.scene.input.KeyEvent;
import logic.Player;
import utility.PosXY;

import utility.Generel;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class PlayerClient {
    private static int port = 1234;
    private static String[] board;
    private static Socket connectionSocket;
    private static BufferedReader inFromServer;
    private static BufferedReader inFromClient = new BufferedReader(new InputStreamReader(System.in));
    private static DataOutputStream outToServer;


    public static void main(String[] args) throws IOException {
        connectionSocket = new Socket("10.10.138.237", port );
        inFromServer = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        outToServer = new DataOutputStream(connectionSocket.getOutputStream());
        System.out.println("Type ur name idiot");
        String msg = inFromClient.readLine();
        outToServer.writeBytes(msg + "\n");
        recieveBoard();
    }

    public static void recieveBoard() throws IOException {
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
        System.out.println();
        for (int i = 0; i < board.length; i++) {
            System.out.println(board[i]);
        }
        System.out.println();
    }

    public void declareAction() {
        Player thisPlayer = App.me;

        try {
            String name = "PLAYER{"+thisPlayer.getName()+"}";
            String currPos = "POS{"+thisPlayer.getPos().toString()+"}";
            String action = parseAction((KeyEvent) Gui.currentAction);
            String points = "POINTS{"+thisPlayer.getPoints()+"}";

            String updateMsg = name+currPos+action+points;
            outToServer.writeBytes(updateMsg); //SENDING UPDATE
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private String parseAction(KeyEvent event) {
        String action = null;
        switch (event.getCode()) {
            case UP -> {action = "ACTION{moveUp}";}
            case DOWN -> {action = "ACTION{moveDown}";}
            case LEFT -> {action = "ACTION{moveLeft}";}
            case RIGHT -> {action = "ACTION{moveRight}";}
            case SPACE -> {action = "ACTION{shoot}";}
            case ESCAPE -> {action = "ACTION{quit}";}
            default -> {action = "ACTION{none}";}
        }
        return action;
    }


}
