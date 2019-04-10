import node.LocalNode;

public class AnchorNode {
    public static void main(String[] args){
        String ip = "localhost";
        int port = 8888;

        LocalNode anchor = new LocalNode(0, ip, port);
    }
}
