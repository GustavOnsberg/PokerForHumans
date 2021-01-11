package client;

import org.jspace.*;

import java.io.IOException;
import java.util.Scanner;

public class ClientTest {
    public static void main(String[] argv) throws IOException, InterruptedException {
        RemoteSpace serverSpaceChat = new RemoteSpace("tcp://localhost:33333/chat?keep");
        System.out.println("1");
        Scanner sc = new Scanner(System.in);
        System.out.println("2");
        //String input = sc.nextLine();
        System.out.println("3");

        serverSpaceChat.put("John");
        System.out.println("4");

        //System.out.println("input: " + input);
        System.out.println("5");

    }
}
