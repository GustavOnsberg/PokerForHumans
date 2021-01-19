package server;

class ServerCard {
    int id;
    int suit;
    int rank;

    ServerCard(int id) {
        this.id = id;
        suit = id % 4;
        rank = id % 13;
    }
}
