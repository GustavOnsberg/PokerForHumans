package client;

public class ClientCard {
    public int id;
    public int suit;
    public int rank;

    public ClientCard(int id) {
        this.id = id;
        suit = id % 4;
        rank = id % 13;
    }
}
