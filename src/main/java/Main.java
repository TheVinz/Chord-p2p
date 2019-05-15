import resource.RemoteResource;
import utils.LogFormatter;

import java.util.Scanner;
import java.util.logging.Level;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String ip = "localhost";
        int port = sc.nextInt();

        LogFormatter.logSetup(Level.FINE);

        ChordNetwork network = new ChordNetwork();
        network.join(ip, port);

        while(!network.isClosed()){
            String input = sc.next(), name, content;
            switch (input){
                case "publish":
                    name = sc.next();
                    content=sc.next();
                    network.publish(name, content);
                    break;
                case "find":
                    name = sc.next();
                    RemoteResource remoteResource = network.find(name);
                    System.out.println(remoteResource.fetch().getContent());
                    break;
            }
        }

        sc.close();
    }
}
