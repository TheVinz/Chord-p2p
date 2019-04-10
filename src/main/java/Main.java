
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
                case "fetch":
                    name=sc.next();
                    content=network.fetch(name);
                    System.out.println(content);
                    break;
                case "fingertable":
                    network.printFingertable();
                    break;
                case "find":
                    int x = sc.nextInt();
                    System.out.println(network.findSuccessor(x));
                    break;
            }
        }
    }
}
