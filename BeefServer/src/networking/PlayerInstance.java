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
    private int nr = 0;

    public PlayerInstance(Socket socket) {
        nr++;
        this.socket = socket;
        this.player = new Player("", GameLogic.getRandomFreePosition(), "up");
        while (!pickingName());
        this.thread = new Thread(this::run);
        thread.start();
    }

    @Override
    public void run() {

    }

    private boolean pickingName() {
        try {
            //Læs input fra brugeren
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String name = inFromClient.readLine();

            //Opdatér this.player.name
            this.player.setName(name);

            //Send evt. navn tilbage


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }


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
