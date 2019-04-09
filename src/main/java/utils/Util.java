package utils;

import node.FailureHandlerNode;
import node.LocalNode;
import node.StabilizerNode;
import test.FailingNode;

import java.util.function.Consumer;

public abstract class Util {

    public static final int M = 4;
    public static final int R = 3;
    private static final Consumer<LocalNode> STABILIZER_ROUTINE = LocalNode::stabilize;
    private static final Consumer<LocalNode> FIX_FINGER_ROUTINE = LocalNode::fixFingers;
    private static final Consumer<FailureHandlerNode> CHECK_PREDECESSOR_ROUTINE = FailureHandlerNode::checkPredecessor;
    private static final Consumer<FailureHandlerNode> CHECK_SUCCESSOR_ROUTINE = FailureHandlerNode::checkSuccessor;
    private static final Consumer<FailureHandlerNode> UPDATE_SUCCESSOR_LIST_ROUTINE = FailureHandlerNode::checkSuccessor;
    private static final Consumer<LocalNode>[] DEFAULT_ROUTINES = new Consumer[]{STABILIZER_ROUTINE, FIX_FINGER_ROUTINE, CHECK_PREDECESSOR_ROUTINE, CHECK_SUCCESSOR_ROUTINE, UPDATE_SUCCESSOR_LIST_ROUTINE};
    private static final String[] defaultLabels = new String[]{"stabilizer", "fix_fingers", "check_predecessor", "check_successor", "update_succerssor_list"};

    public static boolean isInsideInterval(int id, int start, int end){
        if(start > end){
            return id > start || id < end;
        }
        else if(start < end) return id > start && id < end;
        else return id != start; //
    }

    public static StabilizerNode createDefaultStabilizerNode(int id, long[] delays, long[] periods, boolean withFailure) {
        if(!withFailure)
            return new StabilizerNode(id, DEFAULT_ROUTINES, defaultLabels, delays, periods);
        else
            return new FailingNode(id, DEFAULT_ROUTINES, defaultLabels, delays, periods);
    }

}
