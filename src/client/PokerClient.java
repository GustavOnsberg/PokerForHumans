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
    ArrayList<ClientCard> communityCards;
    int playerMeId = 0;
    int start_money = 0;
    int requiredBet = 0;
    int roundProgression = 0;
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
                    if(tuple[1].equals("your_index")){
                        playerMeId = (int)tuple[3];
                    }
                    else if(tuple[1].equals("turn")){
                        if ((int)tuple[3] == playerMeId){
                            isTurn = true;
                        }
                    }
                    else if(tuple[1].equals("player_count")){
                        total_players = (int)tuple[3];
                    }
                    else if(tuple[1].equals("start_money")){
                        start_money = (int)tuple[3];
                    }
                    else if(tuple[1].equals("raise")){
                        players.get((int)tuple[4]).bank -= (int)tuple[3] - players.get((int)tuple[4]).bet;
                        players.get((int)tuple[4]).bet = (int)tuple[3];
                        requiredBet = (int)tuple[3];
                    }
                    else if(tuple[1].equals("allin")){
                        players.get((int)tuple[4]).bank = 0;
                        players.get((int)tuple[4]).bet += (int)tuple[3];
                        if(requiredBet < (int)tuple[3]){
                            requiredBet = (int)tuple[3];
                        }
                    }
                    else if(tuple[1].equals("fold")){
                        players.get((int)tuple[4]).state = 0;
                    }
                    else if(tuple[1].equals("community_card")){
                        communityCards.add(new ClientCard((int)tuple[3]));
                    }
                    else if(tuple[1].equals("betting_round")){
                        roundProgression = (int)tuple[3];
                    }
                    else if(tuple[1].equals("player_out")){
                        players.get((int)tuple[3]).state = -1;
                    }
                    else if(tuple[1].equals("player_card")){
                        players.get((int)tuple[4]).cards.add(new ClientCard((int)tuple[3]));
                    }
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




