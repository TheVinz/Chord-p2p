import network.nodeServer.NodeServer;
import node.StabilizerNode;
import utils.LogFormatter;
import utils.Util;

import java.io.IOException;
import java.util.logging.Level;

public class AnchorNode {
    public static void main(String[] args){
        int port = 8888;

        LogFormatter.logSetup(Level.FINER);

        StabilizerNode anchor = Util.createDefaultStabilizerNode(0, new long[]{500, 800, 0, 100}, new long[]{250, 250, 20, 20}, false);
        anchor.start();
        try(NodeServer server = new NodeServer(anchor, port)) {
            server.loop();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            anchor.close();
        }
    }

}
