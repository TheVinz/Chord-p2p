import network.exeptions.NetworkFailureException;
import network.remoteNode.RemoteNode;
import utils.ChordResource;
import node.LocalNode;
import node.Node;
import node.exceptions.NodeNotFoundException;
import utils.Util;

import java.io.Closeable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ChordNetwork implements Closeable {
    private static final String ANCHOR_IP = "localhost";
    private static final int ANCHOR_PORT = 8888;
    private static final int ANCHOR_ID = 0;

    private LocalNode node;
    private boolean closed = false;

    public void join(String ip, int port){
        RemoteNode anchor = new RemoteNode(ANCHOR_ID, ANCHOR_IP, ANCHOR_PORT);
        int id= 0;
        try {
            id = calculateDigest(ip +":"+port);
            System.out.println("id: " + id);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return;
        }

        node = new LocalNode(id, ip, port);

        try {
            anchor.setUpConnection();
            node.join(anchor);
        } catch (NetworkFailureException | NodeNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void publish(String title, String content){
        try {
            int id = calculateDigest(title);
            Node n = node.findSuccessor(id);
            n.publish(new ChordResource(title, content));
        } catch (NoSuchAlgorithmException | NetworkFailureException | NodeNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String fetch(String title){
        try {
            int id = calculateDigest(title);
            Node n = node.findSuccessor(id);
            return n.fetch(title).getContent();
        } catch (NoSuchAlgorithmException | NodeNotFoundException | NetworkFailureException e) {
            e.printStackTrace();
        }
        return null;
    }

    private int calculateDigest(String obj) throws NoSuchAlgorithmException {
        int res, mod= (int) Math.pow(2, Util.M-1);
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] messageDigest = md.digest(obj.getBytes());
        BigInteger no = new BigInteger(1, messageDigest);
        res=no.intValue() % (mod-1) + mod-1;
        return res;
    }

    public void printFingertable(){
        node.getFingerTable().print();
    }

    @Override
    public void close() {
        closed=true;
    }

    public int findSuccessor(int x) {
        try {
            return node.findSuccessor(x).getId();
        } catch (NodeNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
