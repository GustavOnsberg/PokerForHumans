package server;

import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;

import java.util.ArrayList;
import java.util.Random;


public class PokerServer {
    SpaceRepository serverRep;

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
                    new Thread(new Waiter(serverRep,server,"t_"+server.lastServerId)).start();
                    serverRep.get("lobby").put("resp","t_"+server.lastServerId,tuple[1]);
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
            while(turnNotDetermined && gameIsInProgress){
                if(players.get(turn).state != 4){
                    turn = getNextPlayer();
                }
                if(turn != -1){
                    players.get(turn).state = 4;
                    turnNotDetermined = false;
                }
                else{
                    try {
                        goToNextBettingRound();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                Object[] tuple = serverRep.get(table).get(new FormalField(String.class),new FormalField(String.class),new FormalField(Integer.class));
                System.out.println("Waiter ("+table+")> Reading: "+tuple[0].toString()+" : "+tuple[1].toString() + " : "+(int)tuple[2]);
                if(tuple[0].equals("req") && !gameIsInProgress){
                    players.add(new ServerPlayer(tuple[1].toString()));
                    serverRep.get(table).put("resp",tuple[1].toString());
                    System.out.println("Waiter ("+table+")> New player has joined: "+tuple[1].toString());
                }
                else{
                    if (!gameIsInProgress && tuple[1].equals(players.get(0).uid)){
                        if(tuple[0].equals("set_start_money")){
                            startMoney = (int)tuple[2];
                            System.out.println("Waiter ("+table+")> Start money set to "+startMoney);
                        }
                        else if(tuple[0].equals("set_small_blind")){
                            smallBlind = (int)tuple[2];
                            System.out.println("Waiter ("+table+")> Small blind set to "+smallBlind);
                        }
                        else if(tuple[0].equals("set_big_blind")){
                            bigBlind = (int)tuple[2];
                            System.out.println("Waiter ("+table+")> Big blind set to "+bigBlind);
                        }
                        else if(tuple[0].equals("start_game")){
                            System.out.println("Waiter ("+table+")> Starting game");
                            gameIsInProgress = true;
                            for(ServerPlayer p : players){
                                p.bank = startMoney;
                                p.state = 1;
                                broadcast("player_count","player_count",players.size(),players.size(),players.size()+ " players around the table");
                                broadcast("start_money","start_money",startMoney,startMoney,"Start money is "+startMoney);
                            }

                            players.get(players.size()-1).isDealer = true;
                            startNewRound();

                            for(int i = 0; i < players.size(); i++){
                                serverRep.get(table).put(players.get(i).uid, "your_index", "your_index", i,i, "My index is "+i);
                            }

                            broadcast("turn","turn",turn,turn,"It's player "+turn+"'s turn");
                        }
                    }
                    else if(gameIsInProgress){
                        if(tuple[0].equals("fold") && tuple[1].equals(players.get(turn).uid)){
                            players.get(turn).state = 0;
                            broadcast("fold","fold",turn,turn,"Player "+turn+" has folded");
                            System.out.println("Waiter ("+table+")> "+players.get(turn).name+" has folded");
                            broadcast("turn","turn",turn,turn,"It's player "+turn+"'s turn");
                            turn = getNextPlayer();
                        }
                        else if(tuple[0].equals("call") && tuple[1].equals(players.get(turn).uid)){
                            if(players.get(turn).bank + players.get(turn).bet >= requiredBet){
                                players.get(turn).bank -= requiredBet - players.get(turn).bet;
                                players.get(turn).bet = requiredBet;
                                players.get(turn).state = 2;
                                broadcast("call","call",requiredBet,turn,"Player "+turn+" has called "+requiredBet);
                                System.out.println("Waiter ("+table+")> "+players.get(turn).name+" has called "+requiredBet);
                                turn = getNextPlayer();
                            }
                            else{
                                players.get(turn).bet += players.get(turn).bank;
                                players.get(turn).bank = 0;
                                players.get(turn).state = 3;
                                broadcast("allin","allin",players.get(turn).bet,turn,"Player "+turn+" is all in with "+players.get(turn).bet);
                                System.out.println("Waiter ("+table+")> "+players.get(turn).name+" is all in with "+players.get(turn).bet);
                                turn = getNextPlayer();
                            }
                            broadcast("turn","turn",turn,turn,"It's player "+turn+"'s turn");
                        }
                        else if(tuple[0].equals("raise") && tuple[1].equals(players.get(turn).uid)){
                            if((int)tuple[2] + requiredBet <= players.get(turn).bet + players.get(turn).bank){
                                players.get(turn).bank -= (int)tuple[2] + requiredBet - players.get(turn).bet;
                                players.get(turn).bet = (int)tuple[2] + requiredBet;
                                requiredBet += (int)tuple[2];
                                players.get(turn).state = 2;
                                broadcast("raise","raise",players.get(turn).bet,turn,"Player "+turn+" has raised to "+players.get(turn).bet);
                                System.out.println("Waiter ("+table+")> "+players.get(turn).name+" has raised by "+(int)tuple[2]);
                                turn = getNextPlayer();
                            }
                            else{
                                players.get(turn).bet += players.get(turn).bank;
                                players.get(turn).bank = 0;
                                players.get(turn).state = 3;
                                broadcast("allin","allin",players.get(turn).bet,turn,"Player "+turn+" is all in with "+players.get(turn).bet);
                                System.out.println("Waiter ("+table+")> "+players.get(turn).name+" is all in with "+players.get(turn).bet);
                                turn = getNextPlayer();
                            }
                            broadcast("turn","turn",turn,turn,"It's player "+turn+"'s turn");
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



    void goToNextBettingRound() throws InterruptedException {
        boolean dealerFound = false;
        for(ServerPlayer p : players){
            if(p.state == 2 || p.state == 4){
                p.state = 1;
            }
        }
        int beforeDealerId = 0;
        for(int i = 0; i < players.size(); i++){
            if (players.get(i).isDealer){
                beforeDealerId = i;
            }
        }
        turn = beforeDealerId;
        while(!dealerFound){
            if (players.get(turn).isDealer){
                dealerFound = true;
            }
            turn = getNextPlayer();
        }
        players.get(turn).state = 4;
        broadcast("turn","turn",turn,turn,"It's player "+turn+"'s turn");

        if(roundProgression == 0){
            communityCards.add(deck.get(0));
            broadcast("community_card","community_card",deck.get(0).id,0,"First community card is "+deck.get(0).id);
            deck.remove(0);
            communityCards.add(deck.get(0));
            broadcast("community_card","community_card",deck.get(0).id,1,"Second community card is "+deck.get(0).id);
            deck.remove(0);
            communityCards.add(deck.get(0));
            broadcast("community_card","community_card",deck.get(0).id,2,"Third community card is "+deck.get(0).id);
            deck.remove(0);
            roundProgression++;
            broadcast("betting_round","betting_round",roundProgression,roundProgression,"Now in betting round "+roundProgression);
        }
        else if(roundProgression <= 2){
            communityCards.add(deck.get(0));
            if(roundProgression == 2){
                broadcast("community_card","community_card",deck.get(0).id,3,"Forth community card is "+deck.get(0).id);
            }
            else{
                broadcast("community_card","community_card",deck.get(0).id,4,"Fifth community card is "+deck.get(0).id);
            }

            deck.remove(0);
            roundProgression++;
            broadcast("betting_round","betting_round",roundProgression,roundProgression,"Now in betting round "+roundProgression);
        }
        else {
            roundProgression++;
            goToShowdown();
        }
    }
    void goToShowdown() throws InterruptedException {
        int pot = 0;
        for(ServerPlayer p : players){
            pot += p.bet;
            p.bet = 0;
        }

        int highScore = 0;
        int highScorPlayer = 0;

        players.get(0).bank += pot;
        for(ServerPlayer p : players){
            ArrayList<ServerCard> sevenHand = new ArrayList<>();
            for(ServerCard cc : communityCards){
                sevenHand.add(cc);
            }
            sevenHand.add(p.cards.get(0));
            sevenHand.add(p.cards.get(1));

            if (HandScore.handScore(sevenHand) > highScore){
                highScore = HandScore.handScore(sevenHand);
                highScorPlayer = players.indexOf(p);
            }
            players.get(highScorPlayer).bank+=pot;
            pot = 0;
            if(p.bank <= 0){
                p.state = -1;
                broadcast("player_out","player_out",players.indexOf(p),players.indexOf(p),"NPlayer "+players.indexOf(p)+" is out");
            }
        }
        startNewRound();
    }
    void startNewRound() throws InterruptedException {
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
        if(players.get(turn).bank >= smallBlind){
            players.get(turn).bet = smallBlind;
            players.get(turn).bank -= smallBlind;
        }
        else{
            players.get(turn).bet = players.get(turn).bank;
            players.get(turn).bank = 0;
        }
        turn = getNextPlayer();
        players.get(turn).isBigBlind = true;
        if(players.get(turn).bank >= bigBlind){
            players.get(turn).bet = bigBlind;
            players.get(turn).bank -= bigBlind;
        }
        else{
            players.get(turn).bet = players.get(turn).bank;
            players.get(turn).bank = 0;
        }
        turn = getNextPlayer();
        players.get(turn).state = 4;
        requiredBet = bigBlind;

        ArrayList<ServerCard> allCards = new ArrayList<>();
        deck.clear();
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
            if(players.get(i).state != -1){
                players.get(i).cards.add(deck.get(0));
                broadcast("player_card","player_card",deck.get(0).id,i,"");
                deck.remove(0);
                players.get(i).cards.add(deck.get(0));
                broadcast("player_card","player_card",deck.get(0).id,i,"");
                deck.remove(0);
            }
        }
    }
    int getNextPlayer(){
        return getNextPlayerHelper(turn + 1, turn);
    }
    int getNextPlayerHelper(int i1, int i2){
        if(i1 >= players.size()){
            return getNextPlayerHelper(0,i2);
        }
        else if (i1 == i2 || (players.get(i1).bet == requiredBet && players.get(i1).state != 1)){
            return -1;
        }
        else if(players.get(i1).state == 1 || players.get(i1).state == 2) {
            return i1;
        }
        else{
            return getNextPlayerHelper(i1 + 1, i2);
        }
    }

    void broadcast(String str1, String str2,int i,int j, String print) throws InterruptedException {
        System.out.println("Waiter ("+table+")> Broadcasting: "+str1+" : "+str2+" : "+i+" : "+j);
        for(ServerPlayer p : players){
            serverRep.get(table).put(p.uid, str1, str2, i,j, print);
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
    */
    int state;



    boolean isDealer;
    boolean isSmallBlind;
    boolean isBigBlind;

    ServerPlayer(String uid){
        this.uid = uid;
    }
}












