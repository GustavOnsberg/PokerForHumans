package server;

import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;

import java.util.ArrayList;

public class Server {

    SpaceRepository serverRep;

    ArrayList <Player> players = new ArrayList<Player>();
    int turn;

    int requiredBet = 0;


    public static void main(String[] argv) {
        Server server = new Server();
    }

    Server(){

        serverRep = new SpaceRepository();

        serverRep.add("chat", new SequentialSpace());
        serverRep.add("lobby", new SequentialSpace());
        serverRep.add("game", new SequentialSpace());

        serverRep.addGate("tcp://localhost:33333/?keep");

        new Thread(new Butler(serverRep, this)).start();
    }
}

class Player {
    int bank;
    String name;
    String room;
    int[] cards;
    int bet;

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

    Player(String name, String room){
        bank = 0;
        this.name = name;
        this.room = room;
        cards = new int[]{-1, -1};
        bet = 0;

        state = 0;
    }
}



class GameHandler implements Runnable{

    boolean lobbyIsRunning = true;
    boolean gameIsInProgress = false;

    /*
    0 = pre-flop
    1 = flop
    2 = turn
    3 = river
    4 = showdown
     */
    int roundProgression = 0;

    @Override
    public void run() {
        while(lobbyIsRunning){
            if (gameIsInProgress){

            }
        }
    }
}

class Waiter implements Runnable{
    SpaceRepository serverRep;
    Player player;
    Server server;

    Waiter(SpaceRepository rep, String prid, Player player, Server server){
        System.out.println("Waiter ("+prid+")> Hi, I'm the waiter for room "+prid);
        serverRep = rep;
        this.player = player;
        this.server = server;
    }


    public void run() {
        while(true){
            try {
                Object[] tuple = serverRep.get(player.room).get(new FormalField(String.class),new FormalField(String.class));
                if (tuple[0].equals("action") && player.state == 4){
                    if (tuple[1].equals("fold")){
                        System.out.println("Waiter ("+player.room+")> Player has folded");
                        player.state = 0;
                    }
                    else if(tuple[1].equals("call")){
                        if(player.bank + player.bet >= server.requiredBet){
                            System.out.println("Waiter ("+player.room+")> Player has called/checked");
                            player.bank -= server.requiredBet - player.bet;
                            player.bet = server.requiredBet;
                            player.state = 2;
                        }
                        else{
                            System.out.println("Waiter ("+player.room+")> Player is all in");
                            player.bet += player.bank;
                            player.bank = 0;
                            player.state = 3;
                        }
                    }
                    else if(tuple[1].equals("raise")){
                        if((int)tuple[2] + server.requiredBet <= player.bet + player.bank){
                            System.out.println("Waiter ("+player.room+")> Player has raisen by "+(int)tuple[2]);
                            player.bank -= (int)tuple[2] + server.requiredBet - player.bet;
                            player.bet = (int)tuple[2] + server.requiredBet;
                            server.requiredBet += (int)tuple[2];
                            player.state = 2;
                        }
                        else{
                            System.out.println("Waiter ("+player.room+")> Player is all in");
                            player.bet += player.bank;
                            player.bank = 0;
                            player.state = 3;
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Butler implements Runnable{
    SpaceRepository serverRep;
    Server server;

    Butler(SpaceRepository rep, Server server){
        serverRep = rep;
        this.server = server;
        System.out.println("Butler> Hi, I'm the butler");
    }

    public void run() {
        while(true){
            try {
                Object[] tuple = serverRep.get("lobby").get(new FormalField(String.class),new FormalField(String.class));
                if (tuple[0].equals("privateSpaceRequest")){
                    serverRep.add("pr_"+tuple[1],new SequentialSpace());
                    System.out.println("Butler> Created private room: pr_"+tuple[1]);
                    server.players.add(new Player("name","pr_"+tuple[1]));
                    new Thread(new Waiter(serverRep,"pr_" + tuple[1],server.players.get(server.players.size()-1),server)).start();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Hi");
        }
    }
}


