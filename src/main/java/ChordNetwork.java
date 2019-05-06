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

public class ChordNetwork {
    private static final String ANCHOR_IP = "localhost"; // TODO set externally
    private static final int ANCHOR_PORT = 8888;
    private static final int ANCHOR_ID = 0;

    private StabilizerNode node;
    private NodeServer server;
    private boolean closed = false;

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
            node = Util.createDefaultStabilizerNode(id, anchor, ip, port, new long[]{500, 800, 0, 100}, new long[]{250, 250, 20, 20});
            node.start();
            try {
                server = new NodeServer(node, ip, port);
            } catch (IOException e) {
                e.printStackTrace();
            }
            anchor.close();
        } catch (NetworkFailureException e) {
            e.printStackTrace();
        }
    }

    public void publish(String title, String content){
        try {
            int id = calculateDigest(title);
            Node n = node.findSuccessor(id);
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


}
