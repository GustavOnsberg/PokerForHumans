package server;

import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;

import java.util.ArrayList;
import java.util.Random;


public class PokerServer {
    SpaceRepository serverRep;

    ArrayList<Player> players = new ArrayList<Player>();
    int turn;

    int requiredBet = 0;
    int lastServerId = 0;


    public static void main(String[] argv) {
        PokerServer server = new PokerServer();
    }

    PokerServer(){
        System.out.println("Server> Server is starting");
        serverRep = new SpaceRepository();

        serverRep.add("lobby", new SequentialSpace());

        serverRep.addGate("tcp://localhost:33333/?keep");

        new Thread(new Butler(serverRep, this)).start();
    }
}

class Butler implements Runnable{
    SpaceRepository serverRep;
    PokerServer server;

    Butler(SpaceRepository rep, PokerServer server){
        serverRep = rep;
        this.server = server;
        System.out.println("Butler> Hi, I'm the butler");
    }

    public void run() {
        while(true){
            try {
                Object[] tuple = serverRep.get("lobby").get(new FormalField(String.class),new FormalField(String.class));
                System.out.println("hey");
                if (tuple[0].equals("req")){
                    server.lastServerId++;
                    serverRep.add("t_"+server.lastServerId,new SequentialSpace());
                    System.out.println("Butler> Created new tabel: t_"+server.lastServerId);
                    serverRep.get("lobby").put("resp","t_"+server.lastServerId,tuple[1]);
                    new Thread(new Waiter(serverRep,server,"t_"+server.lastServerId)).start();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}


class Waiter implements Runnable{
    SpaceRepository serverRep;
    PokerServer server;
    String table;
    ArrayList<ServerPlayer> players = new ArrayList<>();

    int turn;
    int requiredBet = 0;
    int startMoney = 10000;
    int smallBlind = 25;
    int bigBlind = 50;
    int pot = 0;
    ArrayList<ServerCard> communityCards = new ArrayList<>();
    ArrayList<ServerCard> deck = new ArrayList<>();

    boolean gameIsInProgress = false;

    /*
    0 = pre-flop
    1 = flop
    2 = turn
    3 = river
    4 = showdown
     */
    int roundProgression = 0;


    Waiter(SpaceRepository rep, PokerServer server, String table){
        System.out.println("Waiter ("+table+")> Hi, I'm the waiter for table "+table);
        serverRep = rep;
        this.server = server;
        this.table = table;
    }


    public void run() {
        while(true){

            boolean turnNotDetermined = true;
            while(turnNotDetermined){
                if(players.get(turn).state != 4){
                    turn = getNextPlayer();
                }
                if(turn != -1){
                    turnNotDetermined = false;
                }
                else{
                    goToNextBettingRound();
                }
            }

            try {
                Object[] tuple = serverRep.get(table).get(new FormalField(String.class),new FormalField(String.class),new FormalField(Integer.class));
                if(tuple[0].equals("req") && !gameIsInProgress){
                    players.add(new ServerPlayer(tuple[1].toString()));
                    System.out.println("Waiter ("+table+")> New player has joined: "+tuple[1].toString());
                }
                else{
                    if (!gameIsInProgress && tuple[1].equals(players.get(0).uid)){
                        if(tuple[0].equals("set_start_money")){
                            startMoney = (int)tuple[3];
                            System.out.println("Waiter ("+table+")> Start money set to "+startMoney);
                        }
                        else if(tuple[0].equals("set_small_blind")){
                            smallBlind = (int)tuple[3];
                            System.out.println("Waiter ("+table+")> Small blind set to "+smallBlind);
                        }
                        else if(tuple[0].equals("set_big_blind")){
                            bigBlind = (int)tuple[3];
                            System.out.println("Waiter ("+table+")> Big blind set to "+bigBlind);
                        }
                        else if(tuple[0].equals("start_game")){
                            System.out.println("Waiter ("+table+")> Starting game");
                            gameIsInProgress = true;
                            for(int i = 0; i < players.size(); i++){
                                players.get(i).bank = startMoney;
                                players.get(i).state = 1;
                            }
                            startNewRound();

                            communityCards.clear();
                            for (int i = 0; i < 52; i++){
                                deck.add(new ServerCard(i));
                            }
                        }
                    }
                    else if(gameIsInProgress){
                        if(tuple[0].equals("fold") && tuple[1].equals(players.get(turn).uid)){
                            players.get(turn).state = 0;
                            turn = getNextPlayer();
                            System.out.println("Waiter ("+table+")> "+players.get(turn).name+" has folded");
                        }
                        else if(tuple[0].equals("call") && tuple[1].equals(players.get(turn).uid)){
                            if(players.get(turn).bank + players.get(turn).bet >= server.requiredBet){
                                players.get(turn).bank -= server.requiredBet - players.get(turn).bet;
                                players.get(turn).bet = server.requiredBet;
                                players.get(turn).state = 2;
                                System.out.println("Waiter ("+table+")> "+players.get(turn).name+" has called "+requiredBet);
                            }
                            else{
                                players.get(turn).bet += players.get(turn).bank;
                                players.get(turn).bank = 0;
                                players.get(turn).state = 3;
                                System.out.println("Waiter ("+table+")> "+players.get(turn).name+" is all in with "+players.get(turn).bet);
                            }
                        }
                        else if(tuple[0].equals("raise") && tuple[1].equals(players.get(turn).uid)){
                            if((int)tuple[2] + server.requiredBet <= players.get(turn).bet + players.get(turn).bank){
                                players.get(turn).bank -= (int)tuple[2] + server.requiredBet - players.get(turn).bet;
                                players.get(turn).bet = (int)tuple[2] + server.requiredBet;
                                server.requiredBet += (int)tuple[2];
                                players.get(turn).state = 2;
                                System.out.println("Waiter ("+table+")> "+players.get(turn).name+" has raised by "+(int)tuple[2]);
                            }
                            else{
                                players.get(turn).bet += players.get(turn).bank;
                                players.get(turn).bank = 0;
                                players.get(turn).state = 3;
                                System.out.println("Waiter ("+table+")> "+players.get(turn).name+" is all in with "+players.get(turn).bet);
                            }
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



    void goToNextBettingRound(){
        boolean dealerFound = false;
        while(!dealerFound){
            if (players.get(turn).isDealer){
                dealerFound = true;
                players.get(turn).isDealer = false;
            }
            turn = getNextPlayer();
        }

        if(roundProgression == 0){
            communityCards.add(deck.get(0));
            deck.remove(0);
            communityCards.add(deck.get(0));
            deck.remove(0);
            communityCards.add(deck.get(0));
            deck.remove(0);
            roundProgression++;
        }
        else if(roundProgression <= 2){
            communityCards.add(deck.get(0));
            deck.remove(0);
            roundProgression++;
        }
        else {
            roundProgression++;
            goToShowdown();
        }
    }
    void goToShowdown(){
        while(pot > 0){
            ArrayList<ServerPlayer> winners = new ArrayList<>();
            winners.add(players.get(1));
            winners.get(0).bank += pot;
            pot = 0;
            for (int i = 0; i < players.size(); i++){
                players.get(i).bet = 0;
            }
        }

    }
    void startNewRound(){
        boolean dealerFound = false;
        while(!dealerFound){
            if (players.get(turn).isDealer){
                dealerFound = true;
                players.get(turn).isDealer = false;
            }
            turn = getNextPlayer();
        }
        players.get(turn).isDealer = true;
        turn = getNextPlayer();
        players.get(turn).isSmallBlind = true;
        turn = getNextPlayer();
        players.get(turn).isBigBlind = true;
        turn = getNextPlayer();
        players.get(turn).state = 4;

        ArrayList<ServerCard> allCards = new ArrayList<>();

        communityCards.clear();
        for (int i = 0; i < 52; i++){
            allCards.add(new ServerCard(i));
        }
        Random rand = new Random();
        for (int i = 0; i < 52; i++){
            int randomInt = rand.nextInt(allCards.size());
            deck.add(allCards.get(randomInt));
            allCards.remove(randomInt);
        }
        for (int i = 0; i < players.size(); i++){
            players.get(i).cards.clear();
            if(players.get(i).state == 2){
                players.get(i).cards.add(deck.get(0));
                deck.remove(0);
                players.get(i).cards.add(deck.get(0));
                deck.remove(0);
            }
        }
    }
    int getNextPlayer(){
        return getNextPlayerHelper(turn + 1, turn);
    }
    int getNextPlayerHelper(int i1, int i2){
        if (i1 == i2 || players.get(i1).bet == requiredBet){
            return -1;
        }
        else if(i1 >= players.size()){
            return getNextPlayerHelper(0,i2);
        }
        else if(players.get(i1).state == 1 || players.get(i1).state == 2) {
            return i1;
        }
        else{
            return getNextPlayerHelper(i1 + 1, i2);
        }
    }
}


class ServerPlayer{
    String uid;
    String name;

    int bank;
    ArrayList<ServerCard> cards = new ArrayList<>();
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

    ServerPlayer(String uid){
        this.uid = uid;
    }
}

class ServerCard{
    int id;
    int suit;
    int rank;
    ServerCard(int id){
        this.id = id;
        suit = id % 4;
        rank = id % 13;
    }
}












