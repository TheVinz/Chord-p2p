import network.exceptions.NetworkFailureException;
import network.nodeServer.NodeServer;
import network.remoteNode.RemoteNode;
import node.Node;
import node.StabilizerNode;
import resource.ChordResource;
import resource.RemoteResource;
import utils.Util;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChordNetwork {
    private static final String ANCHOR_IP = "localhost"; // TODO set externally
    private static final int ANCHOR_PORT = 8888;
    private static final int ANCHOR_ID = 0;
    private static final Logger LOGGER = Logger.getLogger(ChordNetwork.class.getSimpleName());

    private StabilizerNode node;
    private NodeServer server;
    private Thread serverThread;
    private boolean closed = true;

    public void join(String ip, int port) {
        Node anchor = new RemoteNode(ANCHOR_ID, ANCHOR_IP, ANCHOR_PORT);
        int id;
        try {
            id = calculateDigest(ip +":"+port);
            System.out.println("id: " + id);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return;
        }

        try {
            closed = false;
            node = Util.createDefaultStabilizerNode(id, anchor, ip, port, new long[]{500, 800, 0, 100}, new long[]{250, 250, 200, 200});
            node.start();
            server = new NodeServer(node, port);
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
                            new Object[] {port, e.getMessage()});
            prepareToExit();
        } finally {
            anchor.close();
            LOGGER.log(Level.INFO, "Initial anchor node closed");
        }
    }

    public void publish(String title, String content){
        try {
            int id = calculateDigest(title);
            Node n = node.findSuccessor(id).wrap();
            n.publish(new ChordResource(title, content));
            n.close();
        } catch (NoSuchAlgorithmException | NetworkFailureException e) {
            e.printStackTrace();
        }
    }

    public RemoteResource find(String title){
        try {
            synchronized (this) {
                int id = calculateDigest(title);
                return new RemoteResource(node, title, id);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }


    private int calculateDigest(String obj) throws NoSuchAlgorithmException {
        int res, mod= (int) Math.pow(2, Util.M-1);
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] messageDigest = md.digest(obj.getBytes());
        BigInteger no = new BigInteger(1, messageDigest);
        res=no.intValue() % (mod-1) + mod-1;
        return res;
    }

    private void prepareToExit() {
        if(!closed) {
            serverThread = null;

            if (node != null)
                node.close();

            if (server != null) // TODO: message level lock to block message deliverie to application
                server.close();

            closed = true;
        }
    }

    public boolean isClosed() {
        return closed;
    }
}
