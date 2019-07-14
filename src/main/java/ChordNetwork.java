import network.exceptions.NetworkFailureException;
import network.nodeServer.NodeServer;
import network.remoteNode.RemoteNode;
import node.Node;
import node.StabilizerNode;
import resource.ChordResource;
import resource.RemoteResource;
import utils.NetworkSettings;
import utils.SettingsManager;
import utils.Util;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChordNetwork {
    private static final Logger LOGGER = Logger.getLogger(ChordNetwork.class.getSimpleName());

    private StabilizerNode node;
    private NodeServer server;
    private Thread serverThread;
    private boolean closed = true;

    public void join(String anchorHost, int anchorPort, String nodeHost, int nodePort) {
        int anchorId, nodeId;
        anchorId = Util.calculateDigest(anchorHost +":"+ anchorPort);
        nodeId = Util.calculateDigest(nodeHost +":"+ nodePort);
        System.out.println("id: " + nodeId);

        Node anchor = new RemoteNode(anchorId, anchorHost, anchorPort);
        NetworkSettings config = SettingsManager.getNetworkSettings();

        try {
            closed = false;
            node = Util.createDefaultStabilizerNode(nodeId, anchor, nodeHost, nodePort,
                    config.getRoutineDelays(), config.getRoutinePeriods());
            node.start();
            server = new NodeServer(node, nodePort);
            serverThread = new Thread(server::loop, "server loop");
            serverThread.start();


        } catch (NetworkFailureException e) {
            LOGGER.log(Level.SEVERE,
                    "Impossible to join node {0}:{1}: {2}\nDue to: {3}",
                            new Object[]{anchor.getIp(), anchor.getPort(), e.getMessage(), e.getCause().getMessage()});
            prepareToExit();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,
                    "Impossible to bind server in port {0}: {1}.",
                            new Object[] {nodePort, e.getMessage()});
            prepareToExit();
        } finally {
            anchor.close();
            LOGGER.log(Level.INFO, "Initial anchor node closed");
        }
    }

    public void publish(String title, String content){
        try {
            int id = Util.calculateDigest(title);
            Node n = node.findSuccessor(id).wrap();
            n.publish(new ChordResource(title, content));
            n.close();
        } catch (NetworkFailureException e) {
            e.printStackTrace();
        }
    }

    public RemoteResource find(String title){
        synchronized (this) {
            int id = Util.calculateDigest(title);
            return new RemoteResource(node, title, id);
        }
    }


    private void prepareToExit() {
        if(!closed) {
            serverThread = null;

            if (node != null)
                node.stop();

            if (server != null) // TODO: message level lock to block message deliverie to application
                server.close();

            closed = true;
        }
    }

    public boolean isClosed() {
        return closed;
    }

    public void dumpNode() {
        System.out.println("======= DUMP NODE =======");
        System.out.println(node);
        System.out.println("=========================");
    }
}
