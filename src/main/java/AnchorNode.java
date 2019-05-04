import network.nodeServer.NodeServer;
import node.StabilizerNode;
import utils.Util;

import java.io.IOException;

public class AnchorNode {
    public static void main(String[] args){
        String ip = "localhost";
        int port = 8888;

        StabilizerNode anchor = Util.createDefaultStabilizerNode(0, new long[]{500, 800}, new long[]{250, 250}, false);
        anchor.start();
        try {
            NodeServer server = new NodeServer(anchor, ip, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
