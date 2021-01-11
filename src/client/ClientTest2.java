package client;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import java.io.IOException;
import java.util.Scanner;

public class ClientTest2 {
    public static void main(String[] argv) throws IOException, InterruptedException {
        RemoteSpace serverSpaceChat = new RemoteSpace("tcp://localhost:33333/chat?keep");
        System.out.println("1");

        String message = "";
        System.out.println("2");
        //Object[] tuple = serverSpaceChat.get(new FormalField(String.class()));
        Object[] tuple = serverSpaceChat.get(new FormalField(String.class));
        System.out.println("3");


        System.out.println("received: "+tuple[0]);
        System.out.println("4");
    }
}
