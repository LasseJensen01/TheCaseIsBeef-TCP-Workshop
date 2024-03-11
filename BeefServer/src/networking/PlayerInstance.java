package networking;

import beef_commons.logic.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.time.LocalTime;
import java.util.function.Function;

public class PlayerInstance implements Runnable {
    private Player player;
    private LocalTime inputTime;
    private String latestInput;
    private Socket socket;
    private Thread thread;

    public PlayerInstance(Socket socket) {
        nr++;
        this.socket = socket;
        this.player = new Player("", GameLogic.getRandomFreePosition(), "up");
        System.out.println("Type name");
        while (!pickingName());
        System.out.println("Name debug " + player.getName());
        this.thread = new Thread(this::run);
        thread.start();
    }

    @Override
    public void run() {

    }

    private boolean pickingName() {
        try {
            System.out.println("Picking name debug");
            //Læs input fra brugeren
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String name = inFromClient.readLine();
            System.out.println("Fuck");

            //Opdatér this.player.name
            this.player.setName(name);

            //Send navn tilbage - et !godt baby step!
            /*DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
            Function<String, String> ack = String::toUpperCase;
            outToClient.writeBytes(ack.apply(this.toString()));*/

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    /** Polls the input buffer for this players latest action */
    public void readInput(){

    }
    private static int nr = 0;
    @Override
    public String toString() {
        return "PlayerInstance " + nr + " {"+
                "\n   player=   " + player +
                "\n   inputTime=" + inputTime +
                "\n   socket=   " + socket +
                "\n   thread=   " + thread +
                "\n}";
    }

    public Player getPlayer() {
        return player;
    }

    public Socket getSocket() {
        return socket;
    }
}
