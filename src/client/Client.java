package client;

import org.jspace.RemoteSpace;
import java.util.UUID;

import java.io.IOException;

public class Client {

    RemoteSpace ssLobby;
    RemoteSpace ssPrivate;



    public static void main(String[] argv) throws IOException, InterruptedException {
        Client client = new Client("localhost:33333");
    }

    Client(String gate) throws IOException, InterruptedException {
        connectToServer(gate);
    }


    public void connectToServer(String gate) throws IOException, InterruptedException {
        System.out.println("Trying to connect to "+gate);
        ssLobby = new RemoteSpace("tcp://"+gate+"/lobby?keep");
        System.out.println("Connected to "+gate);
        System.out.println("Requesting private room");
        String prid = UUID.randomUUID().toString();
        ssLobby.put("privateSpaceRequest", prid);
        System.out.println("Trying to access private room");
        ssPrivate = new RemoteSpace("tcp://"+gate+"/pr_"+prid+"?keep");
        System.out.println("Now in private room pr_" + prid);
    }

    public void sendAction(String action, int value) throws InterruptedException {
        System.out.println("Sending action: " + action + " " + value);
        ssPrivate.put("action", action, value);
    }
}
