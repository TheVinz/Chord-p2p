package utils;

import distributedDB.ResourceManager;
import network.exceptions.NetworkFailureException;
import node.LocalNode;
import node.Node;
import node.StabilizerNode;
import node.FailingNode;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.function.Consumer;

public abstract class Util {

    public static final int M = 6;
    public static final int R = 2;
    private static final Consumer<LocalNode> STABILIZER_ROUTINE = LocalNode::stabilize;
    private static final Consumer<LocalNode> FIX_FINGER_ROUTINE = LocalNode::fixFingers;
    private static final Consumer<LocalNode> CHECK_PREDECESSOR_ROUTINE = LocalNode::checkPredecessor;
    private static final Consumer<LocalNode> CHECK_SUCCESSOR_ROUTINE = LocalNode::checkSuccessor;
    private static final Consumer<LocalNode>[] DEFAULT_ROUTINES = new Consumer[]{STABILIZER_ROUTINE, FIX_FINGER_ROUTINE, CHECK_PREDECESSOR_ROUTINE, CHECK_SUCCESSOR_ROUTINE};
    private static final String[] defaultLabels = new String[]{"stabilizer", "fix_fingers", "check_predecessor", "check_successor"};

    public static boolean isInsideInterval(int id, int start, int end){
        if(start > end){
            return id > start || id < end;
        }
        else if(start < end) return id > start && id < end;
        else return id!=start; //
    }

    public static StabilizerNode createDefaultStabilizerNode(int id, long[] delays, long[] periods, boolean withFailure, ResourceManager resourceManager) throws NetworkFailureException {
        if(!withFailure)
            return new StabilizerNode(id, DEFAULT_ROUTINES, defaultLabels, delays, periods, resourceManager);
        else
            return new FailingNode(id, DEFAULT_ROUTINES, defaultLabels, delays, periods);
    }

    public static StabilizerNode createDefaultStabilizerNode(int id, String host, int port, long[] delays, long[] periods, ResourceManager resourceManager) throws NetworkFailureException {
        return new StabilizerNode(id, host, port, DEFAULT_ROUTINES, defaultLabels, delays, periods, resourceManager);
    }

    public static StabilizerNode createDefaultStabilizerNode(int id, Node node, long[] delays, long[] periods, boolean withFailure, ResourceManager resourceManager) throws NetworkFailureException {
        if(!withFailure)
            return new StabilizerNode(id, node, DEFAULT_ROUTINES, defaultLabels, delays, periods, resourceManager);
        else
            return new FailingNode(id, node, DEFAULT_ROUTINES, defaultLabels, delays, periods);
    }

    public static StabilizerNode createDefaultStabilizerNode(int id, Node toJoin, long[] delays, long[] periods, ResourceManager resourceManager) throws NetworkFailureException {
        return new StabilizerNode(id, toJoin, DEFAULT_ROUTINES, defaultLabels, delays, periods, resourceManager);
    }

    public static StabilizerNode createDefaultStabilizerNode(int id, Node toJoin, String ip, int port, long[] delays, long[] periods, ResourceManager resourceManager) throws NetworkFailureException {
        return new StabilizerNode(id, toJoin, ip, port, DEFAULT_ROUTINES, defaultLabels, delays, periods, resourceManager);
    }

    public static int calculateDigest(String obj) {
        int res, mod= (int) Math.pow(2, M -1);
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
        byte[] messageDigest = md.digest(obj.getBytes());
        BigInteger no = new BigInteger(1, messageDigest);
        res=no.intValue() % (mod-1) + mod-1;
        return res;
    }
}
