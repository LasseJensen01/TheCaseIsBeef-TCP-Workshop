package networking;

import logic.Player;

import java.net.Socket;
import java.time.LocalTime;

public class PlayerInstance implements Runnable {
    private Player player;
    private LocalTime inputTime;
    private Socket socket;
    private Thread thread;

    public PlayerInstance(Socket socket) {
        this.socket = socket;
        this.thread = new Thread(this::run);
        thread.start();

    }

    @Override
    public void run() {
        //Læs input fra brugeren

        //Send det løste gamestate tilbage.

        //Rinse and repeat!
    }
}
