package server;

import java.util.ArrayList;


public class HandScore {
    public static void main(String[] args){
        ArrayList<ServerCard> cards = new ArrayList<> ();
        cards.add(new ServerCard(1));
        cards.add(new ServerCard(1));
        cards.add(new ServerCard(1));
        cards.add(new ServerCard(1));
        cards.add(new ServerCard(1));
        cards.add(new ServerCard(1));
        cards.add(new ServerCard(1));
        System.out.println(handScore(cards));
    }
    public static int handScore(ArrayList<ServerCard> cards){
        int[] cardAmount= new int[13];
        int[] suitAmount= new int[4];
        for (int i = 0; i < 7; i++) {
            cardAmount[cards.get(i).rank]++;
            suitAmount[cards.get(i).suit]++;
        }
        System.out.println(cardAmount);
        System.out.println(suitAmount);
        return 0;
    }
}
