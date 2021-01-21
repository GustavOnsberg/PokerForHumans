package client;

import java.util.ArrayList;

public class ClientPlayer {
    String uid;
    String name;

    public int bank;
    public ArrayList<ClientCard> cards = new ArrayList<>();
    public int bet;

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

    ClientPlayer() {

    }

    public int getBank() {
        return bank;
    }

    public int getState() {
        return state;
    }

}
