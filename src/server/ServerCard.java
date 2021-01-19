package server;

class ServerCard {
    int id;
    int suit;
    int rank;

    ServerCard(int id) {
        this.id = id;
        suit = id / 13;
        rank = id % 13;
    }
}
