package networking;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

public class UDPBroadcastThread extends Thread{
    String IP;
    String serverName;
    public UDPBroadcastThread(String serverName, String IP) {
        this.serverName = serverName;
        this.IP = IP;
    }

    @Override
    public void run() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            socket.setBroadcast(true);
            String msg = serverName + "," + IP;
            byte[] data = msg.getBytes();
            InetAddress broadcastAdress = InetAddress.getByName("255.255.255.255");
            DatagramPacket packet = new DatagramPacket(data, data.length, broadcastAdress, 1212);
            while (true){
                socket.send(packet);
                System.out.println(IP);
                TimeUnit.SECONDS.sleep(2);
            }
        } catch (Exception e) {
            System.err.println("Error in UDPBroadcast Thread: " + this.getId() + ": " + e);
        }

    }
}
