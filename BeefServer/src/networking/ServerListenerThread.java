package networking;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class ServerListenerThread extends Thread{
    private Socket socket;
    private String name;
    private ArrayList<String> inputs;
    public ServerListenerThread(Socket socket, String name, ArrayList<String> input) {
        this.socket = socket;
        this.inputs = input;
        this.name = name;
    }

    @Override
    public void run() {
        try {
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String input;
            while (!socket.isClosed()){
                input = inFromClient.readLine();
                if (input != null){
                    String[] temp = input.split(",");
                    String name = temp[1];
                    for (int i = inputs.size() - 1; i == 0 ; i--) {
                        if (inputs.get(i).contains("PLAYER,"+ name)){
                            inputs.remove(i);
                            inputs.add(input);
                        }else inputs.add(input);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private String[] parseIncomming(String input) { // Legacy code, AKA, what not to do.
        char[] toParse = input.toCharArray();
        String[] toReturn = new String[4];


        int i = 0;
        int value = 0;
        for (char c : toParse) {
            int j = i;
            String toAdd = "";
            if (c == '{') {
                while (toParse[j] != '}') {
                    toAdd += toParse[j];
                    j++;
                }
                toReturn[value] = toAdd;
                value++;
                i = j;
            }
            i++;
        }
        return toReturn;
    }
}
