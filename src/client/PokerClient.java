package client;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class PokerClient implements Runnable{


    RemoteSpace ssLobby;
    RemoteSpace ssGame;

    ClientPlayer playerMe;
    ArrayList<ClientPlayer> players;
    public int total_players = 8;
    static String uid = UUID.randomUUID().toString();
    boolean isTurn = false;
    public ArrayList<String> toPrint = new ArrayList<>();



    public static void main(String[] argv) throws IOException, InterruptedException {
        PokerClient client = new PokerClient("localhost:33333","");
    }

    public PokerClient(String gate,String table) throws IOException, InterruptedException {
        connectToServer(gate,table);
    }


    public void connectToServer(String gate,String table) throws IOException, InterruptedException {
        System.out.println("Client> Trying to connect to "+gate);
        ssLobby = new RemoteSpace("tcp://"+gate+"/lobby?keep");
        System.out.println("Client> Connected to "+gate);
        System.out.println("Client> Requesting table");
        if(table.equals("")){
            System.out.println("Client> Requesting new table");
            ssLobby.put("req", uid);
            System.out.println("Client> Trying to find table");
            Object[] tuple = ssLobby.get(new ActualField("resp"),new FormalField(String.class),new ActualField(uid));
            System.out.println("Client> Table found");
            table = tuple[1].toString();
        }
        System.out.println("Client> Trying to access table");
        ssGame = new RemoteSpace("tcp://"+gate+"/"+table+"?keep");
        System.out.println("Client> Now at table "+table);
        ssGame.put("req",uid,0);
    }


    public void sendAction(String action, int value) throws InterruptedException {
        ssGame.put(action, uid, value);
        System.out.println("Client> Sending action: " + action + " " + value);
    }

    @Override
    public void run() {
        while (true){
            if (!isTurn){
                try {
                    Object[] tuple = ssGame.get(new ActualField(uid), new FormalField(String.class), new FormalField(String.class), new FormalField(Integer.class), new FormalField(Integer.class));
                    toPrint.add(tuple[5].toString());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


class ClientPlayer{
    String uid;
    String name;

    int bank;
    ArrayList<ClientCard> cards = new ArrayList<>();
    int bet;

    int score;

    /*
    -1 = out
    0 = folded
    1 = yet to bet
    2 = in the game
    3 = all in
    4 = players turn

    * */
    int state;



    boolean isDealer;
    boolean isSmallBlind;
    boolean isBigBlind;

    ClientPlayer(String uid){
        this.uid = uid;
    }
}

class ClientCard{
    int id;
    int suit;
    int rank;
    ClientCard(int id){
        this.id = id;
        suit = id % 4;
        rank = id % 13;
    }
}




