package client;

import org.jspace.RemoteSpace;

import java.util.ArrayList;
import java.util.UUID;

import java.io.IOException;

public class Client {

    RemoteSpace ssLobby;
    RemoteSpace ssPrivate;

    public Player playerMe;
    public ArrayList<Player> players;



    public static void main(String[] argv) throws IOException, InterruptedException {
        Client client = new Client("localhost:33333");
    }

    public Client(String gate) throws IOException, InterruptedException {
        connectToServer(gate);
        playerMe = new Player("Name");
        players = new ArrayList<Player>();
    }


    public void connectToServer(String gate) throws IOException, InterruptedException {
        System.out.println("Client> Trying to connect to "+gate);
        ssLobby = new RemoteSpace("tcp://"+gate+"/lobby?keep");
        System.out.println("Client> Connected to "+gate);
        System.out.println("Client> Requesting private room");
        String prid = UUID.randomUUID().toString();
        ssLobby.put("privateSpaceRequest", prid);
        System.out.println("Client> Trying to access private room");
        ssPrivate = new RemoteSpace("tcp://"+gate+"/pr_"+prid+"?keep");
        System.out.println("Client> Now in private room pr_" + prid);
    }

    public void sendAction(String action, int value) throws InterruptedException {
        ssPrivate.put("action", action, value);
        System.out.println("Client> Sending action: " + action + " " + value);
    }





    public Player getPlayerMe() {
        return playerMe;
    }
}


class Player {
    public int getBank() {
        return bank;
    }


    int bank;

    public String getName() {
        return name;
    }


    String name;

    int[] cards;

    public int getBet() {
        return bet;
    }

    int bet;

    public void setState(int state) {
        this.state = state;
    }

    /*
        -1 = out
        0 = folded
        1 = yet to bet
        2 = in the game
        3 = all in
        4 = players turn

        * */
    int state;



    public boolean isDealer;
    public boolean isSmallBlind;
    public boolean isBigBlind;

    Player(String name){
        bank = 0;
        this.name = name;
        cards = new int[]{-1, -1};
        bet = 0;

        state = 4;
    }



    public int[] getCards() {
        return cards;
    }

}