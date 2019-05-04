import resource.RemoteResource;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String ip = "localhost";
        int port = sc.nextInt();

        ChordNetwork network = new ChordNetwork();
        network.join(ip, port);

        while(true){
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
    }
}
