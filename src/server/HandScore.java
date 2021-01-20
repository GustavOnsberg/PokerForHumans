package server;

import com.google.gson.JsonArray;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;


public class HandScore {
    public static void main(String[] args) {
        ArrayList<ServerCard> cards = new ArrayList<>();
        cards.add(new ServerCard(1));
        cards.add(new ServerCard(17));
        cards.add(new ServerCard(2));
        cards.add(new ServerCard(16));
        cards.add(new ServerCard(35));
        cards.add(new ServerCard(6));
        cards.add(new ServerCard(7));
        System.out.println(handScore(cards));
    }

    public static int handScore(ArrayList<ServerCard> unsortedCards) {
        ArrayList<ServerCard> cards = new ArrayList<>();

        while (unsortedCards.size() != 0) {
            int lowestCard = 0;
            for (int i = 0; i < unsortedCards.size(); i++) {
                if (unsortedCards.get(i).id <= unsortedCards.get(lowestCard).id) {
                    lowestCard = i;
                }
            }
            cards.add(unsortedCards.get(lowestCard));
            unsortedCards.remove(lowestCard);
        }
        //System.out.println(cards);
        int[] cardAmount = new int[13];
        int[] suitAmount = new int[4];
        for (int i = 0; i < 7; i++) {
            cardAmount[cards.get(i).rank]++;
            suitAmount[cards.get(i).suit]++;
        }
        System.out.println(Arrays.toString(cardAmount));
        System.out.println(Arrays.toString(suitAmount));
        int suit = -1;
        int straight = -1;
        int counter = 0;
        if (suitAmount[0] > 4 || suitAmount[1] > 4 || suitAmount[2] > 4 || suitAmount[3] > 4) {
            System.out.println("royal");
            for (int i = 0; i < suitAmount.length; i++) {
                if (suitAmount[i] > 4) suit = i;
            }
            System.out.println(suit);
            //royal flush
            if (cardAmount[0] != 0 && cardAmount[12] != 0 && cardAmount[11] != 0 && cardAmount[10] != 0 && cardAmount[9] != 0) {

                boolean ace = false;
                boolean king = false;
                boolean queen = false;
                boolean knight = false;
                boolean ten = false;
                for (int i = 0; i < cards.size(); i++) {
                    if (cards.get(i).suit == suit) {
                        if (cards.get(i).rank == 0) {
                            ace = true;
                        } else if (cards.get(i).rank == 12) {
                            king = true;
                        } else if (cards.get(i).rank == 11) {
                            queen = true;
                        } else if (cards.get(i).rank == 10) {
                            knight = true;
                        } else if (cards.get(i).rank == 9) {
                            ten = true;
                        }
                    }
                }
                //System.out.println(ace);
                //System.out.println(king);
                //System.out.println(queen);
                //System.out.println(knight);
                //System.out.println(ten);
                if (ace && king && queen && knight && ten) return 10000000;
            }
            for (int i = 1; i < cardAmount.length; i++) {
                if (cardAmount[i] != 0 && cardAmount[i - 1] != 0) {
                    counter++;
                } else {
                    counter = 0;
                }
                if (counter > 4) {
                    System.out.println("straight");
                    straight = i;
                }
            }


            //straight flush
            ServerCard lastCard = cards.get(0);
            counter = 0;
            if (straight != -1) {
                for (int i = cards.size() - 1; i > 0; i--) {

                    if (lastCard.id == cards.get(i).id + 1) {
                        counter++;
                        lastCard = cards.get(i);
                    } else {
                        counter = 0;
                        lastCard = cards.get(i);
                    }
                    System.out.println(counter);
                    if (counter == 4) {
                        return 9000000 + lastCard.id;
                    }
                }
            }


        }
        //four of a kind
        int fourOfAKind = -1;
        int oneOfAKind = -1;
        for (int i = 0; i < cardAmount.length; i++) {
            if (cardAmount[i] > 3) {
                fourOfAKind = i;
            } else if (cardAmount[i] > 0) {
                oneOfAKind = i;
            }
        }
        if (fourOfAKind != -1) {
            return 8000000 + fourOfAKind * 100 + oneOfAKind;
        }

        //full house
        int threeOfAKind = -1;
        int twoOfAKind = -1;
        if (cardAmount[0] > 2) {
            threeOfAKind = 0;
        } else if (cardAmount[0] > 1) {
            twoOfAKind = 0;
        }
        for (int i = cardAmount.length - 1; i > 0; i--) {
            if (cardAmount[i] > 2) {
                if (threeOfAKind != -1) {
                    threeOfAKind = i;
                }
            } else if (cardAmount[i] > 1) {
                if (threeOfAKind != -1) {
                    twoOfAKind = i;
                }
            }
        }
        if (threeOfAKind != -1 && twoOfAKind != -1) {
            return 7000000 + threeOfAKind * 100 + twoOfAKind;
        }

        //flush
        if (suit != -1) {
            int[] highCardsFlush = new int[5];
            counter = 0;
            for (int i = cards.size() - 1; i > 0; i--) {
                if (cards.get(i).suit == suit) {
                    if (counter < 5) {
                        highCardsFlush[counter] = cards.get(i).id;
                        counter++;
                    }
                }
            }
            System.out.println(highCardsFlush.toString());
            return (int) (6000000 + highCardsFlush[4] + highCardsFlush[3] * 13 + highCardsFlush[2] * Math.pow(13, 2) + highCardsFlush[1] * Math.pow(13, 3) + highCardsFlush[0] * Math.pow(13, 4));
        }

        //straight
        if (straight != -1) {
            return 5000000 + straight;
        }

        //three of a kind
        if (threeOfAKind != -1) {
            return 4000000 + threeOfAKind;
        }

        //two pairs
        int pair1 = -1;
        int pair2 = -1;
        int kicker = -1;
        for (int i = cardAmount.length - 1; i > 0; i--) {
            if (pair1 == -1) {
                if (cardAmount[i] > 1) {
                    pair1 = i;
                }
            } else if (pair2 == -1) {
                if (cardAmount[i] > 1) {
                    pair2 = i;
                }
            }
            if (kicker == -1 && pair1 != i && pair2 != i) {
                kicker = i;
            }

        }
        if (pair1 != -1 && pair2 != -1) {
            return 3000000 + pair1 * 10000 + pair2 * 100 + kicker;
        } else if (pair1 != -1) {                   //one pair
            int[] kickers = new int[3];
            for (int i = cardAmount.length - 1; i > 0; i--) {
                if (pair1 != i) {
                    for (int j = 0; j < cardAmount[i]; j++) {
                        kickers[j] = i;
                        System.out.println(kickers.toString());
                    }
                }
            }
            return (int) (2000000 + kickers[2] + kickers[1] * 13 + kickers[0] * Math.pow(13, 2) + pair1 * Math.pow(13, 3));
        }

        //high card
        int highcard = 0;
        counter = 4;
        for (int i = cardAmount.length - 1; i > 0; i--) {
            if (counter >= 0) {
                if (cardAmount[i] != 0) {
                    for (int j = 0; j < cardAmount[i]; j++) {
                        highcard += i * Math.pow(13, counter);
                        System.out.println(i * Math.pow(13, counter));
                        counter--;
                    }
                }

            }
        }
        if (highcard > 0) {
            return 1000000 + highcard;
        }
        return -1;
    }
}
