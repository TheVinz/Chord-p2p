import network.exeptions.NetworkFailureException;
import network.remoteNode.RemoteNode;
import resource.ChordResource;
import node.LocalNode;
import node.Node;
import node.exceptions.NodeNotFoundException;
import resource.RemoteResource;
import utils.Util;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ChordNetwork {
    private static final String ANCHOR_IP = "localhost";
    private static final int ANCHOR_PORT = 8888;
    private static final int ANCHOR_ID = 0;

    private LocalNode node;
    private boolean closed = false;

    public void join(String ip, int port){
        RemoteNode anchor = new RemoteNode(ANCHOR_ID, ANCHOR_IP, ANCHOR_PORT);
        int id;
        try {
            id = calculateDigest(ip +":"+port);
            System.out.println("id: " + id);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return;
        }

        node = new LocalNode(id, ip, port);

        try {
            node.join(anchor);
            anchor.close();
        } catch (NetworkFailureException | NodeNotFoundException e) {
            e.printStackTrace();
        }
    }

    public synchronized void publish(String title, String content){
        try {
            int id = calculateDigest(title);
            Node n = node.findSuccessor(id);
            n.publish(new ChordResource(title, content));
            if (n instanceof RemoteNode)
                ((RemoteNode) n).close();
        } catch (NoSuchAlgorithmException | NetworkFailureException | NodeNotFoundException e) {
            e.printStackTrace();
        }
    }

    public synchronized RemoteResource find(String title){
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
