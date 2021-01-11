package server;

import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;

public class Server {

    static SpaceRepository serverRep;

    public static void main(String[] argv) {
        serverRep = new SpaceRepository();

        serverRep.add("chat", new SequentialSpace());
        serverRep.add("lobby", new SequentialSpace());
        serverRep.add("game", new SequentialSpace());

        serverRep.addGate("tcp://localhost:33333/?keep");

        new Thread(new Butler(serverRep)).start();
    }
}

class Butler implements Runnable{
    SpaceRepository serverRep;

    Butler(SpaceRepository rep){
        serverRep = rep;
        System.out.println("Butler> Hi, I'm the butler");
    }

    public void run() {
        while(true){
            try {
                Object[] tuple = serverRep.get("lobby").get(new FormalField(String.class),new FormalField(String.class));
                if (tuple[0].equals("privateSpaceRequest")){
                    serverRep.add("pr_"+tuple[1],new SequentialSpace());
                    System.out.println("Butler> Created private room: pr_"+tuple[1]);
                    new Thread(new Waiter(serverRep,"pr_" + tuple[1])).start();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}


class Waiter implements Runnable{

    Waiter(SpaceRepository rep, String prid){
        System.out.println("Waiter ("+prid+")> Hi, I'm the waiter for private room "+prid);
    }


    public void run() {

    }
}