package utils;

import network.exeptions.NetworkFailureException;
import node.LocalNode;
import node.Node;
import node.StabilizerNode;
import node.exceptions.NodeNotFoundException;

import java.util.function.Consumer;

public abstract class Util {

    public static final int M = 8;
    private static final Consumer<LocalNode> STABILIZER_ROUTINE = LocalNode::stabilize;
    private static final Consumer<LocalNode> FIX_FINGER_ROUTINE = LocalNode::fixFingers;
    private static final Consumer<LocalNode>[] DEFAULT_ROUTINES = new Consumer[]{STABILIZER_ROUTINE, FIX_FINGER_ROUTINE};
    private static final String[] defaultLabels = new String[]{"stabilizer", "fix_fingers"};

    public static boolean isInsideInterval(int id, int start, int end){
        if(start > end){
            return id > start || id < end;
        }
        else if(start < end) return id > start && id < end;
        else return id!=start; //
    }

    public static StabilizerNode createDefaultStabilizerNode(int id, long[] delays, long[] periods) {
        return new StabilizerNode(id, DEFAULT_ROUTINES, defaultLabels, delays, periods);
    }

    public static StabilizerNode createDefaultStabilizerNode(int id, Node toJoin, long[] delays, long[] periods) throws NodeNotFoundException, NetworkFailureException {
        return new StabilizerNode(id, toJoin, DEFAULT_ROUTINES, defaultLabels, delays, periods);
    }

    public static StabilizerNode createDefaultStabilizerNode(int id, Node toJoin, String ip, int port, long[] delays, long[] periods) throws NodeNotFoundException, NetworkFailureException {
        return new StabilizerNode(id, toJoin, ip, port, DEFAULT_ROUTINES, defaultLabels, delays, periods);
    }
}
